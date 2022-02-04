package com.amhsrobotics.pathsim.templates;

import java.awt.*;
import java.awt.geom.Point2D;

public class Renderer {
    private double zoom, cx, cy, imgwidth, imgheight, inch;

    public void paintComponent(Graphics g, double centerx, double centery, double zoom,double imgwidth, double imgheight, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();

        this.zoom = zoom;
        this.cx = centerx;
        this.cy = centery;
        this.imgheight = imgheight;
        this.imgwidth = imgwidth;

        g2.setColor(new Color(250, 250, 250));
        g2.fillRect(0, (int) (centery), width, 1);
        g2.fillRect((int) (centerx), 0, 1, height);


        inch = imgwidth/864;

        g2.setColor(new Color(200, 200, 200, 50));

        if(zoom < 0.22) {
            drawGrid(g2, centerx, centery, width, height, 24 * inch, 40);

            drawText(g2, centerx, centery, width, height, 48 * inch, 4);
        } else if (zoom < 0.4) {
            drawGrid(g2, centerx, centery, width, height, 12 * inch, 40);

            drawText(g2, centerx, centery, width, height, 24 * inch, 2);
        } else if (zoom < 0.7) {
            drawGrid(g2, centerx, centery, width, height, 12 * inch, 40);
            drawGrid(g2, centerx, centery, width, height, 6 * inch, 25);

            drawText(g2, centerx, centery, width, height, 12 * inch, 1);
        } else {
            drawGrid(g2, centerx, centery, width, height, 1 * inch, 15);
            drawGrid(g2, centerx, centery, width, height, 6 * inch, 25);
            drawGrid(g2, centerx, centery, width, height, 12 * inch, 40);

            drawText(g2, centerx, centery, width, height, 12 * inch, 1);
        }


        g2.dispose();
    }

    public void drawGrid(Graphics2D g2, double centerx, double centery, int width, int height, double increment, int transparency) {
        g2.setColor(new Color(200, 200, 200, transparency));

        for(double i = centerx + increment; i <= width; i += increment) {
            g2.fillRect((int) (i-0.5), 0, 1, height);
        }

        for(double i = centerx - increment; i >= 0; i -= increment) {
            g2.fillRect((int) (i-0.5), 0, 1, height);
        }

        for(double i = centery + increment; i <= height; i += increment) {
            g2.fillRect(0, (int) (i-0.5), width, 1);
        }

        for(double i = centery - increment; i >= 0; i -= increment) {
            g2.fillRect(0, (int) (i-0.5), width, 1);
        }
    }

    public void drawText(Graphics2D g2, double centerx, double centery, int width, int height, double increment, int t) {
        g2.setColor(new Color(200, 200, 200, 200));

        int cur = 0;

        double centerynew = Math.max(Math.min(centery, height-20), 0);
        double centerxnew = Math.max(Math.min(centerx, width), 30);

        g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        for(double i = centerx + increment; i <= width; i += increment) {
            cur += t;
            g2.drawString(cur+"", (int) (i-4-4*((cur+"").length()-1)), (int) centerynew + 13);
        }

        cur = 0;
        for(double i = centerx - increment; i >= 0; i -= increment) {
            cur -= t;
            g2.drawString(cur+"", (int) (i-4-4*((cur+"").length()-1)), (int) centerynew + 13);
        }

        cur = 0;
        for(double i = centery + increment; i <= height; i += increment) {
            cur += t;
            String temp = " ".repeat(3-(cur+"").length());
            g2.drawString(temp + cur, (int) centerxnew - 25, (int) (i+3));
        }

        cur = 0;
        for(double i = centery - increment; i >= 0; i -= increment) {
            cur -= t;
            String temp = " ".repeat(3-(cur+"").length());
            g2.drawString(temp + cur, (int) centerxnew - 25, (int) (i+3));
        }
    }

    public double x(double x) {
        return x * imgwidth / 864 + cx;
    }

    public double y(double y) {
        return -y * imgwidth / 864 + cy;
    }

}
