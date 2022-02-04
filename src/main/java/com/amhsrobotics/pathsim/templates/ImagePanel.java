package com.amhsrobotics.pathsim.templates;

import com.amhsrobotics.pathsim.PathFollowingSim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage img;

    private double zoom, mousex, mousey, centerx, centery, xc, yc;
    private double maxzoom, minzoom;

    private final double MAX_ZOOM_RELATIVE = 2.2;
    private final double MIN_ZOOM_RELATIVE = 0.1;

    public ImagePanel(BufferedImage img) {
        super();
        this.img = img;
        xc = 0;
        yc = 0;

        resetZoomBounds();
        setZoom(0.2);

        this.addMouseWheelListener(new ZoomListener());
        this.addMouseMotionListener(new DragListener());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        double x = xc + (getWidth() - zoom * img.getWidth())/2;
        double y = yc + (getHeight() - 28 - zoom * img.getHeight())/2;

        g2.drawImage(img, (int) x, (int) y, (int) (img.getWidth() * zoom), (int) (img.getHeight() * zoom), null);

        centerx = x + (img.getWidth() * zoom) / 2;
        centery = y + (img.getHeight() * zoom) / 2;

        g2.dispose();



        PathFollowingSim.getRenderer().paintComponent(g, centerx, centery, zoom, img.getWidth() * zoom, img.getHeight() * zoom, getWidth(), getHeight() - 28);
    }

    public void setZoom(double zoom) {
        this.zoom = Math.min(maxzoom, Math.max(minzoom, zoom));;

        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension();
    }

    public void resetZoomBounds() {
        maxzoom = (PathFollowingSim.getMainFrame().getWidth() / 1440.) * MAX_ZOOM_RELATIVE;
        minzoom = (PathFollowingSim.getMainFrame().getWidth() / 1440.) * MIN_ZOOM_RELATIVE;

        setZoom(zoom);
    }

    public void boundXY() {
        int maxx = (int) (img.getWidth() * zoom / 2);
        int maxy = (int) (img.getHeight() * zoom / 2);
        xc = Math.min(maxx, Math.max(xc, -maxx));
        yc = Math.min(maxy, Math.max(yc, -maxy));
    }

    public class ZoomListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double newzoom = zoom + e.getWheelRotation() / 100.;
            newzoom = Math.min(maxzoom, Math.max(minzoom, newzoom));

            mousex = e.getLocationOnScreen().x - PathFollowingSim.getMainFrame().getX();
            mousey = e.getLocationOnScreen().y - PathFollowingSim.getMainFrame().getY() - 28; //fix header


            double factor = 1 - newzoom / zoom;

            xc += (mousex - centerx) * factor;
            yc += (mousey - centery) * factor;

            boundXY();

            setZoom(newzoom);

        }
    }

    public class DragListener implements MouseMotionListener {

        public int prevX, prevY;

        @Override
        public void mouseDragged(MouseEvent e) {
//            System.out.println("hi");
            xc += e.getX() - prevX;
            yc += e.getY() - prevY;

            boundXY();
            repaint();


            prevX = e.getX();
            prevY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            prevX = e.getX();
            prevY = e.getY();
        }
    }
}