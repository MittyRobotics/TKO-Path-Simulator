package com.github.mittyrobotics.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.github.mittyrobotics.PathSim;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Path Simulator";
		config.height = 800;
		config.width = 1200;
		config.samples = 3;
		config.resizable = false;
		new LwjglApplication(new PathSim(), config);
	}
}
