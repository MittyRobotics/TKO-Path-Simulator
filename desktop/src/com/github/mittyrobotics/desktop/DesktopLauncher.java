package com.github.mittyrobotics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.github.mittyrobotics.PathSim;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Path Simulator";
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		config.height = d.height; // for header
		config.width = d.width;
//		config.samples = 3;
		config.resizable = true;
//		config.initialBackgroundColor = new Color(0.12f, 0.12f, 0.12f, 1f);
//		config.useGL30 = false;
		new LwjglApplication(new PathSim(), config);
	}
}
