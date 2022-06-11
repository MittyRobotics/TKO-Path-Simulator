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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

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
	public static JTextArea debugText, exportText;
	public static JFrame exportFrame;

	@Override
	public void create () {

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
		assets.load("img/trash.png", Texture.class);
		assets.load("img/edit.png", Texture.class);
		assets.load("img/visible.png", Texture.class);
		assets.load("img/invisible.png", Texture.class);


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
		input.addProcessor(Renderer2D.camController);
		Gdx.input.setInputProcessor(input);

		in3d = false;

		if(debug) {
			JFrame debugFrame = new JFrame("Debug");
			debugFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			debugText = new JTextArea("", 1, 1);
			debugText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
			debugText.setForeground(Color.BLACK);
			debugText.setBackground(new Color(0.9f, 0.9f, 0.9f));
			debugText.setMargin(new Insets(10, 10, 10, 10));
			debugText.setLineWrap(true);
			debugText.setWrapStyleWord(true);
			debugText.setEditable(false);

			debugFrame.setPreferredSize(new Dimension(200, 500));
			debugFrame.getContentPane().add(debugText, BorderLayout.CENTER);
			debugFrame.setLocation(0, 0);
			debugFrame.pack();
			debugFrame.setVisible(true);
		}

		exportFrame = new JFrame("Export");
		exportFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		exportText = new JTextArea("", 1, 1);
		exportText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		exportText.setForeground(Color.BLACK);
		exportText.setBackground(new Color(0.9f, 0.9f, 0.9f));
		exportText.setLineWrap(true);
		exportText.setWrapStyleWord(true);
		exportText.setEditable(false);
		JScrollPane sp = new JScrollPane(exportText);
		sp.setBorder(new LineBorder(new Color(0.9f, 0.9f, 0.9f), 1));
		exportText.setBorder(new LineBorder(new Color(0.9f, 0.9f, 0.9f), 20));

		exportFrame.setPreferredSize(new Dimension(800, 600));
		exportFrame.getContentPane().add(sp);
		exportFrame.setLocation(0, 0);
		exportFrame.pack();
		exportFrame.setVisible(false);




		try {
			ArrayList<ExtendedPath> a = PathImporter.parse("// spline 1\n" +
					"\n" +
					"QuinticHermiteSplineGroup spline1 = new QuinticHermiteSplineGroup();\n" +
					"\n" +
					"QuinticHermiteSpline s1 = new QuinticHermiteSpline(\n" +
					"    new Pose2D(-202.851, -21.267, 0.998), new Pose2D(-124.328, 100.608, 0.524), \n" +
					"    new Vector2D(78.523, 121.874), new Vector2D(125.505, 72.580)\n" +
					");\n" +
					"spline1.addSpline(s1);\n" +
					"\n" +
					"QuinticHermiteSpline s2 = new QuinticHermiteSpline(\n" +
					"    new Pose2D(-124.328, 100.608, 0.524), new Pose2D(-66.963, 99.790, 3.930), \n" +
					"    new Vector2D(107.507, 62.171), new Vector2D(-37.105, -37.293)\n" +
					");\n" +
					"spline1.addSpline(s2);\n" +
					"\n" +
					"// spline 2\n" +
					"\n" +
					"QuinticHermiteSplineGroup spline2 = new QuinticHermiteSplineGroup();\n" +
					"\n" +
					"QuinticHermiteSpline s1 = new QuinticHermiteSpline(\n" +
					"    new Pose2D(-144.565, -31.812, 5.649), new Pose2D(-87.563, 60.596, 5.649), \n" +
					"    new Vector2D(54.878, -40.362), new Vector2D(54.878, -40.362)\n" +
					");\n" +
					"spline2.addSpline(s1);\n" +
					"\n" +
					"QuinticHermiteSpline s2 = new QuinticHermiteSpline(\n" +
					"    new Pose2D(-87.563, 60.596, 5.649), new Pose2D(-22.417, 110.872, 1.510), \n" +
					"    new Vector2D(66.291, -48.756), new Vector2D(6.727, 110.464)\n" +
					");\n" +
					"spline2.addSpline(s2);\n" +
					"\n" +
					"// spline 3\n" +
					"\n" +
					"QuinticHermiteSpline spline3 = new QuinticHermiteSpline(\n" +
					"    new Pose2D(16.883, 99.896, 5.566), new Pose2D(113.185, 15.985, 2.684), \n" +
					"    new Vector2D(96.302, -83.910), new Vector2D(-261.291, 128.521)\n" +
					");\n" +
					"\n" +
					"// path 1 ———————————————————————————————————————————————————\n" +
					"\n" +
					"PurePursuitPath path1 = new PurePursuitPath(\n" +
					"    spline1, 50.0, 50.0, 50.0, 1000.0, 0.0, 0.0\n" +
					");\n" +
					"\n" +
					"PurePursuitPFCommand pathCommand1 = new PurePursuitPFCommand(path1, \n" +
					"    15.0, 3.0, 12.0, false\n" +
					");\n" +
					"\n" +
					"// path 2\n" +
					"\n" +
					"PurePursuitPath path2 = new PurePursuitPath(\n" +
					"    spline2, 50.0, 50.0, 50.0, 1000.0, 0.0, 0.0\n" +
					");\n" +
					"\n" +
					"PurePursuitPFCommand pathCommand2 = new PurePursuitPFCommand(path2, \n" +
					"    15.0, 3.0, 12.0, false\n" +
					");\n" +
					"\n" +
					"// path 3\n" +
					"\n" +
					"PurePursuitPath path3 = new PurePursuitPath(\n" +
					"    spline3, 50.0, 50.0, 50.0, 1000.0, 0.0, 0.0\n" +
					");\n" +
					"\n" +
					"PurePursuitPFCommand pathCommand3 = new PurePursuitPFCommand(path3, \n" +
					"    15.0, 3.0, 12.0, false\n" +
					");\n" +
					"\n");
			PathSim.pathManager.paths.addAll(a);
			//NULLPOINTER HERE BUT REMEMBER TO POPULATE WIDGET !!!!!
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void switchModes(boolean in3dd) {
		if(in3dd) {
			renderer3d.reset();
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
		if(renderer3d.loading) renderer3d.load();
		if(in3d) {
			renderer3d.render();
			renderer2d.renderUI();
		} else {
			renderer2d.render();
		}

		if(debug && !renderer2d.loading) {
			debugText.setText("PATH VARS\n-----\nediting path: " + PathSim.pathManager.curEditingPath + "\nediting node: " + PathSim.pathManager.curEditingNode
					+ "\nediting vel: " + PathSim.pathManager.curEditingVel + "\n\non path: " + PathSim.pathManager.curOnPath + "\nselected node: " + PathSim.pathManager.curSelectedNode + "\nhovering node: "
					+ PathSim.pathManager.curHoveringNode + "\n\nadd front/back/new: " + renderer2d.addFront + " " + renderer2d.addBack + " " + renderer2d.addNew + "\njust placed: " +
					renderer2d.wasJustPlaced + "\n\n\nUI VARS\n-----\nspline mode: " + renderer2d.ui.splineMode + "\npure pursuit mode: " + renderer2d.ui.purePursuitMode);
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
