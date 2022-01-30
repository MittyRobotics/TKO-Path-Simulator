package com.amhsrobotics.pathsim.templates;

import com.amhsrobotics.pathsim.PathFollowingSim;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class UIPanel extends JPanel {

    private BufferedImage title1, title2;

    public UIPanel() {
//        JLabel title = new JLabel("TKO Path Simulator", JLabel.CENTER);
//        title.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
//
//        this.setLayout(null);
//        this.add(title);
//
//        System.out.println(title.getPreferredSize().width);
//
//        title.setBounds(PathFollowingSim.UI_WIDTH / 2 - title.getPreferredSize().width / 2, 70, title.getPreferredSize().width, title.getPreferredSize().height);


        try {
            title1 = ImageIO.read(new File("./assets/img/title1.png"));
        } catch (Exception e) {
            System.out.println("title1 did not load ...");
        }

        try {
            title2 = ImageIO.read(new File("./assets/img/title2.png"));
        } catch (Exception e) {
            System.out.println("title2 did not load ...");
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.drawImage(title1, 50, 70, 200, 200 * title1.getHeight()/title1.getWidth(), null);
        g2.drawImage(title2, 40, 160, 220, 220 * title2.getHeight()/title2.getWidth(), null);


        g2.dispose();
    }
}
