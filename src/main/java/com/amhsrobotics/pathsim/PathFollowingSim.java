package com.amhsrobotics.pathsim;


import com.amhsrobotics.pathsim.templates.*;
import com.amhsrobotics.pathsim.templates.Renderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

public class PathFollowingSim {
    private static JFrame frame;
    private static com.amhsrobotics.pathsim.templates.ImagePanel panel;
    private static UIPanel uiPanel;
    private static Renderer renderer = new Renderer();

    public static final int UI_WIDTH = 300;


    public static void main(String[] args) {
        //main frame
        frame = new JFrame();

        frame.setTitle("TKO Path Following Simulator");

        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setResizable(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Renderer renderer;


        //viewport
        try {
            panel = new com.amhsrobotics.pathsim.templates.ImagePanel(ImageIO.read(PathFollowingSim.class.getResourceAsStream("/img/FIELD_RENDER.png")));
            panel.setBackground(new Color(50, 50, 50));
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
                    uiPanel.repaint();

                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        uiPanel = new UIPanel();
        uiPanel.setBackground(new Color(45, 45, 45));
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

    public static Renderer getRenderer() {
        return renderer;
    }

}
