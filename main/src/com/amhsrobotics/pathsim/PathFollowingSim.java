package com.amhsrobotics.pathsim;


import com.amhsrobotics.pathsim.templates.ImagePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PathFollowingSim {
    private static JFrame frame;
    private static ImagePanel panel;

    public static void main(String[] args) {
        frame = new JFrame();
        try {
            panel = new ImagePanel(ImageIO.read(new File("./assets/img/FIELD_RENDER.png")));
            panel.setBackground(Color.DARK_GRAY);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JViewport viewport = new JViewport();

        viewport.add(panel);


        frame.setTitle("TKO Path Following");
        frame.setSize(new Dimension(1200, 800));
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(viewport);

        frame.setVisible(true);
    }

    public static JFrame getMainFrame() {
        return frame;
    }

    public static ImagePanel getImgPanel() {
        return panel;
    }
}
