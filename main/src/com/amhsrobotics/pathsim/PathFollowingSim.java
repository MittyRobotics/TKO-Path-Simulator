package com.amhsrobotics.pathsim;


import com.amhsrobotics.pathsim.templates.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class PathFollowingSim {
    private static JFrame frame;
    private static ImagePanel panel;
    private static UIPanel uiPanel;

    public static final int UI_WIDTH = 300;


    public static void main(String[] args) {
        //main frame
        frame = new JFrame();

        frame.setTitle("TKO Path Following");

        frame.setBackground(new Color(40, 40, 40));

        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //viewport
        try {
            panel = new ImagePanel(ImageIO.read(new File("./assets/img/FIELD_RENDER.png")));
            panel.setBackground(new Color(40, 40, 40));
            panel.setBounds(0, 0, frame.getWidth() - UI_WIDTH, frame.getHeight());

            frame.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    super.componentResized(e);
                    panel.setBounds(0, 0, frame.getWidth() - UI_WIDTH, frame.getHeight());
                    panel.resetZoomBounds();
                    panel.boundXY();
                    panel.repaint();

                    uiPanel.setBounds(frame.getWidth() - UI_WIDTH, 0, UI_WIDTH, frame.getHeight());
                }
            });
        } catch (Exception e) {
            System.out.println("field render did not load ...");
        }

        uiPanel = new UIPanel();
        uiPanel.setBackground(new Color(35, 35, 35));
        uiPanel.setBounds(frame.getWidth() - UI_WIDTH, 0, UI_WIDTH, frame.getHeight());

        //render

        frame.setLayout(null);

        frame.getContentPane().add(panel);
        frame.getContentPane().add(uiPanel);

        frame.setVisible(true);
    }

    public static JFrame getMainFrame() {
        return frame;
    }

    public static ImagePanel getImgPanel() {
        return panel;
    }

}
