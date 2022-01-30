package com.amhsrobotics.pathsim.templates;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

//        g2.setColor(Color.WHITE);
//        g2.drawOval(0, 0, 100, 100);

        g2.dispose();
    }
}
