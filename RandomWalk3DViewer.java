import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.awt.geom.Point2D;


public class RandomWalk3DViewer extends JPanel
        implements MouseListener, MouseMotionListener, MouseWheelListener {

	private final List<PathGenerator> generators;
    private final List<Color> colors;

    // Camera rotation
    private double camYaw = 0.0;
    private double camPitch = Math.toRadians(20);
    private double zoom = 3.0;

    // Camera pan (screen-space)
    private double panX = 0.0;
    private double panY = 0.0;

    // World-space pivot for orbiting
    private Vector3 pivot = new Vector3(0, 0, 0);

    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging = false;
    private int dragButton = MouseEvent.NOBUTTON;
    private boolean shiftDrag = false;
    
 // Orbit state
    private boolean orbitArmed = false;
    private boolean orbiting = false;
    private int pressX, pressY;
    private static final int ORBIT_DRAG_THRESHOLD_PX = 2;
 // True orbit camera
    private Vector3 camPos = new Vector3(0, 0, 200);
    private double orbitRadius = 200.0;



    public RandomWalk3DViewer(List<PathGenerator> generators, List<Color> colors) {
        this.generators = generators;
        this.colors = colors;

        setBackground(Color.BLACK);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    private Point2D.Double projectToScreenD(Vector3 v, int width, int height) {
        // Same projection as projectToScreen, but keep doubles.
        Vector3 forward = pivot.subtract(camPos).normalize();
        Vector3 upWorld = new Vector3(0, 1, 0);

        Vector3 right = forward.cross(upWorld).normalize();
        Vector3 up = right.cross(forward);

        Vector3 rel = v.subtract(camPos);

        double xCam = rel.dot(right);
        double yCam = rel.dot(up);
        double zCam = rel.dot(forward);

        double focal = 200.0;
        double denom = focal + zCam;
        if (denom <= 0.1) return null;

        double factor = focal / denom;

        double centerX = width / 2.0 + panX;
        double centerY = height / 2.0 + panY;

        double screenX = centerX + xCam * factor * zoom;
        double screenY = centerY - yCam * factor * zoom;

        return new Point2D.Double(screenX, screenY);
    }


    @Override
    protected void paintComponent(Graphics g) {
    	
        super.paintComponent(g);

        if (generators == null || generators.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        boolean heavy = generators.size() > 150; // terrain often 200+ polylines
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                heavy ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);

        Vector3 forward = pivot.subtract(camPos).normalize();
        Vector3 upWorld = new Vector3(0, 1, 0);
        Vector3 right = forward.cross(upWorld).normalize();
        Vector3 up = right.cross(forward);

        
        drawAxis(g2, w, h);

        // Draw each worm with its own color
        for (int wIndex = 0; wIndex < generators.size(); wIndex++) {
        	PathGenerator gen = generators.get(wIndex);
            List<Vector3> points = gen.getPoints();
            List<Double> sizes   = gen.getSegmentSizes();
            WormSettings settings = gen.getSettings();

            if (points == null || points.size() < 2) continue;

            Color col = (wIndex < colors.size()) ? colors.get(wIndex) : Color.CYAN;

            for (int i = 1; i < points.size(); i++) {
                Vector3 vPrev = points.get(i - 1);
                Vector3 vCur  = points.get(i);

               //	Point pPrev = projectToScreen(vPrev, w, h);
               // Point pCur  = projectToScreen(vCur,  w, h);
                Point pPrev = projectToScreenFast(vPrev, w, h, right, up, forward);
                Point pCur = projectToScreenFast(vPrev, w, h, right, up, forward);


                if (pPrev == null || pCur == null) continue;

                double segSize = (i - 1 < sizes.size())
                        ? sizes.get(i - 1)
                        : settings.fixedSegmentSize;

                float thickness = computeStrokeWidth(segSize, settings);

                g2.setStroke(new BasicStroke(
                        thickness,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                ));
                //g2.setColor(col);
                g2.drawLine(pPrev.x, pPrev.y, pCur.x, pCur.y);
                
                int segIndex = i - 1;

                Color drawCol = col;
                if (gen instanceof SegmentColorProvider scp) {
                    drawCol = scp.getSegmentColor(segIndex);
                }
                g2.setColor(drawCol);

            }
        }
    }

    private float computeStrokeWidth(double segSize, WormSettings settings) {
        double minS = settings.minSegmentSize;
        double maxS = settings.maxSegmentSize;

        if (maxS <= minS + 1e-6) {
            return 3.0f;
        }
        double t = (segSize - minS) / (maxS - minS);
        t = clamp(t, 0.0, 1.0);
        double width = 1.0 + t * 6.0; // 1 to 7
        return (float) width;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
    private Point projectToScreenFast(Vector3 v, int width, int height,
        Vector3 right, Vector3 up, Vector3 forward) {
		Vector3 rel = v.subtract(camPos);
		
		double xCam = rel.dot(right);
		double yCam = rel.dot(up);
		double zCam = rel.dot(forward);
		
		double focal = 200.0;
		double denom = focal + zCam;
		if (denom <= 0.1) return null;
		
		double factor = focal / denom;
		
		double centerX = width / 2.0 + panX;
		double centerY = height / 2.0 + panY;
		
		int screenX = (int)Math.round(centerX + xCam * factor * zoom);
		int screenY = (int)Math.round(centerY - yCam * factor * zoom);
		return new Point(screenX, screenY);
}


    private Point projectToScreen(Vector3 v, int width, int height) {
        // Build camera basis (lookAt pivot)
        Vector3 forward = pivot.subtract(camPos).normalize(); // camera looks toward pivot
        Vector3 upWorld = new Vector3(0, 1, 0);

        Vector3 right = forward.cross(upWorld).normalize();
        Vector3 up = right.cross(forward);

        Vector3 rel = v.subtract(camPos);

        double xCam = rel.dot(right);
        double yCam = rel.dot(up);
        double zCam = rel.dot(forward);

        double focal = 200.0;
        double denom = focal + zCam;
        if (denom <= 0.1) return null;

        double factor = focal / denom;

        double centerX = width / 2.0 + panX;
        double centerY = height / 2.0 + panY;

        int screenX = (int) Math.round(centerX + xCam * factor * zoom);
        int screenY = (int) Math.round(centerY - yCam * factor * zoom);
        return new Point(screenX, screenY);
    }



    

    private void drawAxis(Graphics2D g2, int w, int h) {
        Vector3 origin = new Vector3(0, 0, 0);
        Vector3 xAxis  = new Vector3(50, 0, 0);
        Vector3 yAxis  = new Vector3(0, 50, 0);
        Vector3 zAxis  = new Vector3(0, 0, 50);

        Point o = projectToScreen(origin, w, h);
        Point x = projectToScreen(xAxis,  w, h);
        Point y = projectToScreen(yAxis,  w, h);
        Point z = projectToScreen(zAxis,  w, h);

        if (o == null) return;

        g2.setStroke(new BasicStroke(1.5f));

        if (x != null) {
            g2.setColor(Color.RED);
            g2.drawLine(o.x, o.y, x.x, x.y);
            g2.drawString("X", x.x + 5, x.y);
        }
        if (y != null) {
            g2.setColor(Color.GREEN);
            g2.drawLine(o.x, o.y, y.x, y.y);
            g2.drawString("Y", y.x + 5, y.y);
        }
        if (z != null) {
            g2.setColor(Color.BLUE);
            g2.drawLine(o.x, o.y, z.x, z.y);
            g2.drawString("Z", z.x + 5, z.y);
        }
    }

    // ===== Mouse controls =====
    @Override
    public void mousePressed(MouseEvent e) {
        dragging = true;
        dragButton = e.getButton();
        lastMouseX = e.getX();
        lastMouseY = e.getY();

        // Arm orbit if shift is held, but DO NOT change pivot yet.
        if (e.isShiftDown()) {
            orbitArmed = true;
            orbiting = false;
            pressX = lastMouseX;
            pressY = lastMouseY;
        } else {
            orbitArmed = false;
            orbiting = false;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        dragButton = MouseEvent.NOBUTTON;
        shiftDrag = false;

        orbitArmed = false;
        orbiting = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!dragging) return;

        int x = e.getX();
        int y = e.getY();

        // If orbit is armed, only "start" orbit after a tiny drag.
        if (orbitArmed && !orbiting) {
            int dx0 = x - pressX;
            int dy0 = y - pressY;
            if (dx0 * dx0 + dy0 * dy0 < ORBIT_DRAG_THRESHOLD_PX * ORBIT_DRAG_THRESHOLD_PX) {
                return; // shift-click alone does nothing
            }

            // Pick pivot from the press point (Unity-like)
         // inside mouseDragged, when (orbitArmed && !orbiting) and threshold passed:

            Vector3 newPivot = findClosestWorldPointToScreen(pressX, pressY);
            if (newPivot != null) {
                // Keep the camera position where it is *right now*
                Vector3 oldCamPos = camPos;

                // Change pivot (target)
                pivot = newPivot;

                // Recompute orbit params from the *current camera position* so there’s no snap/jitter
                Vector3 v = oldCamPos.subtract(pivot);
                orbitRadius = v.length();
                camYaw = Math.atan2(v.x, v.z);
                camPitch = Math.asin(v.y / orbitRadius);

                // Optionally (usually not needed), re-derive camPos from angles:
                // updateCamPosFromAngles();
                // If you do that, it should land back on oldCamPos (within tiny floating error).
                camPos = oldCamPos;
            }

            orbiting = true;
            orbitArmed = false;
            lastMouseX = x;
            lastMouseY = y;
            repaint();
            return;

        }

        int dx = x - lastMouseX;
        int dy = y - lastMouseY;

        if (orbiting) {
            // Shift + drag: orbit around pivot
            double sensitivity = 0.01;
            camYaw += dx * sensitivity;
            camPitch += dy * sensitivity;

            double maxPitch = Math.PI / 2 - 0.1;
            if (camPitch > maxPitch) camPitch = maxPitch;
            if (camPitch < -maxPitch) camPitch = -maxPitch;

        } else {
            // Normal controls
            if (dragButton == MouseEvent.BUTTON1) {
                double sensitivity = 0.01;
                camYaw += dx * sensitivity;
                camPitch += dy * sensitivity;

                double maxPitch = Math.PI / 2 - 0.1;
                if (camPitch > maxPitch) camPitch = maxPitch;
                if (camPitch < -maxPitch) camPitch = -maxPitch;

            } else if (dragButton == MouseEvent.BUTTON3) {
                double panSensitivity = 1.0;
                panX += dx * panSensitivity;
                panY += dy * panSensitivity;
            }
        }
        
        updateCamPosFromAngles();


        lastMouseX = x;
        lastMouseY = y;
        repaint();
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        zoom *= (1 - notches * 0.1);
        if (zoom < 0.01) zoom = 0.01;
        if (zoom > 100.0) zoom = 100.0;
        repaint();
    }

    private void updateCamPosFromAngles() {
        double cx = orbitRadius * Math.sin(camYaw) * Math.cos(camPitch);
        double cy = orbitRadius * Math.sin(camPitch);
        double cz = orbitRadius * Math.cos(camYaw) * Math.cos(camPitch);
        camPos = pivot.add(new Vector3(cx, cy, cz));
    }

    
    // ===== Helper: pick world-space pivot from click =====

    private Vector3 findClosestWorldPointToScreen(int mouseX, int mouseY) {
        if (generators == null || generators.isEmpty()) return null;
        int w = getWidth();
        int h = getHeight();

        double bestDist2 = Double.MAX_VALUE;
        Vector3 best = null;

        for (PathGenerator gen : generators) {
            List<Vector3> pts = gen.getPoints();
            if (pts == null) continue;

            for (Vector3 v : pts) {
                Point2D.Double p = projectToScreenD(v, w, h);
                if (p == null) continue;

                double dx = mouseX - p.x;
                double dy = mouseY - p.y;
                double d2 = dx * dx + dy * dy;

                if (d2 < bestDist2) {
                    bestDist2 = d2;
                    best = v;
                }
            }
        }
        return best;
    }


    // Unused but required
    @Override public void mouseMoved(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
