package com.github.mittyrobotics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.mittyrobotics.PathSim;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Path Simulator";
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		config.height = d.height - 52; // for header
		config.width = d.width;
		config.samples = 3;
		config.resizable = false;
		new LwjglApplication(new PathSim(), config);
	}
}
