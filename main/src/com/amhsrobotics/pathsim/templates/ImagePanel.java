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

    public ImagePanel(BufferedImage img) {
        super();
        this.img = img;
        xc = 0;
        yc = 0;
        setZoom(0.2);

        this.addMouseWheelListener(new ZoomListener());
        this.addMouseMotionListener(new DragListener());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        double x = xc + (getWidth() - zoom * img.getWidth())/2;
        double y = yc + (getHeight() - zoom * img.getHeight())/2;

        g2.drawImage(img, (int) x, (int) y, (int) (img.getWidth() * zoom), (int) (img.getHeight() * zoom), null);

        centerx = x + (img.getWidth() * zoom) / 2;
        centery = y + (img.getHeight() * zoom) / 2;

        g2.dispose();
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;

        this.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension();
    }

    public class ZoomListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double newzoom = zoom + e.getWheelRotation() / 100.;
            newzoom = Math.min(1.5, Math.max(0.2, newzoom));

            mousex = e.getLocationOnScreen().x - PathFollowingSim.getMainFrame().getX();
            mousey = e.getLocationOnScreen().y - PathFollowingSim.getMainFrame().getY() - 28; //fix header


            double factor = 1 - newzoom / zoom;
//
            xc += (mousex - centerx) * factor;
            yc += (mousey - centery) * factor;

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