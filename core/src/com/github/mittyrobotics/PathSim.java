package com.github.mittyrobotics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;
import com.github.mittyrobotics.tools.*;

public class PathSim extends ApplicationAdapter {

	public static Renderer3D renderer3d;
	public static Renderer2D renderer2d;
	public static AssetManager assets;
	public static boolean in3d;
	public static final int RIGHT_WIDTH = 300;

	@Override
	public void create () {

		assets = new AssetManager();
		assets.load("field.g3db", Model.class);
		assets.load("FIELD_RENDER.png", Texture.class);
		assets.load("title1.png", Texture.class);
		assets.load("title2.png", Texture.class);

		renderer3d = new Renderer3D();

		renderer2d = new Renderer2D();

		Gdx.input.setInputProcessor(Renderer2D.camController);

		in3d = false;

	}

	@Override
	public void render () {
		if(in3d) {
			renderer3d.render();
		} else {
			renderer2d.render();
		}
	}

	@Override
	public void dispose () {
		renderer3d.dispose();
		renderer2d.dispose();
		assets.dispose();
	}

	@Override
	public void resume () {
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}

}
