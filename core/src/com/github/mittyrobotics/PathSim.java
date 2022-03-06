package com.github.mittyrobotics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;
import com.github.mittyrobotics.tools.*;

import javax.swing.*;
import java.awt.*;

public class PathSim extends ApplicationAdapter {

	public static Renderer3D renderer3d;
	public static Renderer2D renderer2d;
	public static AssetManager assets;
	public static boolean in3d;
	public static int RIGHT_WIDTH = 300;
	public static int LEFT_WIDTH;
	public static Skin skin;
	public static BitmapFont font, f20;
	public static InputMultiplexer input;
	public static PathManager pathManager;

	public static boolean debug = true;
	public static JTextArea debugText;

	@Override
	public void create () {

		LEFT_WIDTH = Gdx.graphics.getWidth() - RIGHT_WIDTH;

		assets = new AssetManager();
		assets.load("field.g3db", Model.class);
		assets.load("img/FIELD_RENDER.png", Texture.class);
		assets.load("img/title1.png", Texture.class);
		assets.load("img/title2.png", Texture.class);
		assets.load("img/points.png", Texture.class);
		assets.load("img/pointl.png", Texture.class);
		assets.load("img/pointw.png", Texture.class);
		assets.load("img/pointp.png", Texture.class);
		assets.load("img/pointh.png", Texture.class);
		assets.load("img/pointt.png", Texture.class);

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("skin/Holo-dark-xhdpi.atlas"));
		skin = new Skin(atlas);

		Texture texture = new Texture(Gdx.files.internal("skin/Roboto-xhdpi.png"), true);
		texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

		font = new BitmapFont(Gdx.files.internal("skin/Roboto-xhdpi.fnt"), new TextureRegion(texture), false);

		Texture t20 = new Texture(Gdx.files.internal("font/roboto.png"), true);
		t20.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		f20 = new BitmapFont(Gdx.files.internal("font/roboto.fnt"), new TextureRegion(t20), false);

		renderer3d = new Renderer3D();

		renderer2d = new Renderer2D();

		pathManager = new PathManager();

//		Gdx.input.setInputProcessor(Renderer2D.camController);

		input = new InputMultiplexer();
		input.addProcessor(renderer2d.ui.stage);
		input.addProcessor(Renderer2D.camController);
		Gdx.input.setInputProcessor(input);

		in3d = false;

		if(debug) {
			JFrame debugFrame = new JFrame("Debug");
			debugFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			debugText = new JTextArea("", 1, 1);
			debugText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
			debugText.setForeground(Color.WHITE);
			debugFrame.setBackground(new Color(0.1f, 0.1f, 0.1f));
			debugText.setBackground(new Color(0.1f, 0.1f, 0.1f));
			debugText.setLineWrap(true);
			debugText.setWrapStyleWord(true);
			debugText.setEditable(false);
			debugFrame.setPreferredSize(new Dimension(200, 500));
			debugFrame.getContentPane().add(debugText, BorderLayout.CENTER);

			debugFrame.setLocation(0, 0);
			debugFrame.pack();
			debugFrame.setVisible(true);
		}

	}

	public static void switchModes(boolean in3dd) {
		if(renderer3d.loading) renderer3d.load();
		if(in3dd) {
			renderer3d.resetCam();
			in3d = true;
			input.addProcessor(Renderer3D.camController);
			input.removeProcessor(Renderer2D.camController);
		} else {
			in3d = false;
			input.removeProcessor(Renderer3D.camController);
			input.addProcessor(Renderer2D.camController);
		}
	}

	@Override
	public void render () {
		if(in3d) {
			renderer3d.render();
			renderer2d.renderUI();
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
