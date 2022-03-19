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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.mittyrobotics.tools.*;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class PathSim extends ApplicationAdapter {

	public static Renderer3D renderer3d;
	public static AssetManager assets;
	public static int RIGHT_WIDTH = 300;
	public static int LEFT_WIDTH;
	public static Skin skin;
	public static BitmapFont font, f20;
	public static InputMultiplexer input;

	public static NetworkTableInstance nt;
	public static NetworkTable table;
	public static NetworkTableEntry xEntry, yEntry, tEntry;


	@Override
	public void create () {

		nt = NetworkTableInstance.getDefault();
		nt.startClientTeam(1351);
		nt.startDSClient();

		table = nt.getTable("SmartDashboard");

		xEntry = table.getEntry("Robot X");
		yEntry = table.getEntry("Robot Y");
		tEntry = table.getEntry("Gyro");



		LEFT_WIDTH = Gdx.graphics.getWidth() - RIGHT_WIDTH;

		assets = new AssetManager();
		assets.load("field.g3db", Model.class);
		assets.load("robot.g3db", Model.class);
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

		input = new InputMultiplexer();
		input.addProcessor(Renderer3D.camController);
		Gdx.input.setInputProcessor(input);



	}

	@Override
	public void render () {
		if(renderer3d.loading) renderer3d.load();
		else renderer3d.render();
	}

	@Override
	public void dispose () {
		renderer3d.dispose();
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
