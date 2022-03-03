package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSplineGroup;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UI implements Disposable {

    public Stage stage;
    public Table container, table;
    public ScrollPane pane;
    public int right, prevState, prevEditing;
    public static int addingSpline;
    public Label addingLabel;
    public boolean splineMode, prevMode;
    public TextButton pathId, addNode1, addNode2, spline, path, addPath, export;
    public ArrayList<TextField> splines = new ArrayList<>();
    public ArrayList<Label> labels = new ArrayList<>();
    public TextField.TextFieldStyle textFieldStyle;
    public Label.LabelStyle lStyle2;
    public ArrayList<Actor> toggle = new ArrayList<>();
    public ArrayList<Actor> splineEdit = new ArrayList<>();
    public ArrayList<Actor> pathEdit = new ArrayList<>();
    public DecimalFormat df;

    public UI() {
        stage = new Stage();

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;

        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        container = new Table();
        table = new Table();
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
//        scrollPaneStyle.vScrollKnob = PathSim.skin.getDrawable("scroll_vertical_knob");

        pane = new ScrollPane(table, scrollPaneStyle);
        pane.setScrollingDisabled(true, false);
        pane.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(pane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setScrollFocus(null);
            }
        });
        pane.layout();
        container.add(pane).width(250).height(260);
        container.row();
        container.setBounds(right+25, 100, 250, 260);
        splineEdit.add(container);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_pressed");

        TextButton.TextButtonStyle textButtonStyleNoBg = new TextButton.TextButtonStyle();
        textButtonStyleNoBg.font = PathSim.font;

        addPath = new TextButton("Add Path", textButtonStyle);
        addPath.getLabel().setFontScale(0.7f);
        addPath.setBounds(right+50, Gdx.graphics.getHeight() - 300, 200, 50);

        addNode1 = new TextButton("Add Start", textButtonStyle);
        addNode1.getLabel().setFontScale(0.5f);
        addNode1.setBounds(right+25, Gdx.graphics.getHeight() - 500, 125, 45);

        addNode2 = new TextButton("Add End", textButtonStyle);
        addNode2.getLabel().setFontScale(0.5f);
        addNode2.setBounds(right+150, Gdx.graphics.getHeight() - 500, 125, 45);

        pathId = new TextButton("", textButtonStyleNoBg);
        pathId.getLabel().setFontScale(0.7f);
        pathId.setBounds(right+50, Gdx.graphics.getHeight() - 400, 200, 50);

        spline = new TextButton("Edit Spline", textButtonStyleNoBg);
        spline.getLabel().setFontScale(0.6f);
        spline.setBounds(right+25, Gdx.graphics.getHeight() - 450, 125, 45);

        path = new TextButton("Edit Path", textButtonStyleNoBg);
        path.getLabel().setFontScale(0.6f);
        path.setBounds(right+150, Gdx.graphics.getHeight() - 450, 125, 45);

        export = new TextButton("Export Path", textButtonStyle);
        export.getLabel().setFontScale(0.6f);
        export.setBounds(right+50, 40, 200, 45);

        addListeners();

        toggle.add(path);
        toggle.add(spline);
        toggle.add(export);

        splineEdit.add(addNode1);
        splineEdit.add(addNode2);

        for(Actor a : toggle) {
            stage.addActor(a);
        }
        stage.addActor(pathId);
        stage.addActor(addPath);


        textFieldStyle = new TextField.TextFieldStyle(PathSim.font, Color.WHITE,
                PathSim.skin.getDrawable("textfield_cursor"), PathSim.skin.getDrawable("textfield_selection"), PathSim.skin.getDrawable("textfield_default"));
        textFieldStyle.font.getData().setScale(0.55f);

        stage.getRoot().addCaptureListener(new InputListener() {
           public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
               if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                   return false;
               }
        });

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = PathSim.font;
        lStyle.font.getData().setScale(0.5f);
        lStyle.fontColor = new Color(104/255f,204/255f,220/255f, 1f);

        lStyle2 = new Label.LabelStyle();
        lStyle2.font = PathSim.font;
        lStyle2.font.getData().setScale(0.6f);
        lStyle2.fontColor = Color.WHITE;


        addingLabel = new Label("", lStyle);
    }

    public void update(float delta) {
        if(addingSpline == 2 && prevState != 2) {
            addingLabel.setText("Place first point.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 70, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 1 && prevState != 1) {
            addingLabel.setText("Place second point.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 70, addingLabel.getPrefWidth(), 30);
        } else if (addingSpline == 3 && prevState != 3) {
            addingLabel.setText("Place new starting node.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 70, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 4 && prevState != 4) {
            addingLabel.setText("Place new ending node.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 70, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 0 && prevState != 0) {
            addingLabel.remove();
            PathSim.input.addProcessor(stage);
        }

        prevState = addingSpline;

        pathId.setText(PathSim.pathManager.curEditingPath == -1 ? "No Path Selected" : "Path " + (PathSim.pathManager.curEditingPath+1));
        if(PathSim.pathManager.curEditingPath == -1 && prevEditing != -1) {
            for(Actor a : toggle) a.remove();
            if(splineMode) for(Actor a : splineEdit) a.remove();
            else for(Actor a : splineEdit) a.remove();
            splineMode = false;
        } else if (PathSim.pathManager.curEditingPath != -1 && prevEditing == -1) {
            for(Actor a : toggle) stage.addActor(a);
            splineMode = true;
            populateSplineEdit();
        } else if (PathSim.pathManager.curEditingPath != prevEditing) {
            splineMode = true;
            populateSplineEdit();
        }

        if(PathSim.pathManager.curEditingPath != -1 && splineMode) {
            updateSplineEdit();
        }

        prevEditing = PathSim.pathManager.curEditingPath;

        if(splineMode && !prevMode) {
            for(Actor a : splineEdit) stage.addActor(a);
            for(Actor a : pathEdit) a.remove();
        } else if (!splineMode && prevMode) {
            for(Actor a : pathEdit) stage.addActor(a);
            for(Actor a : splineEdit) a.remove();
        }
        prevMode = splineMode;

        stage.act(delta);
        stage.draw();
    }

    public void populateSplineEdit() {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(PathSim.pathManager.curEditingPath).getParametric();
        ArrayList<QuinticHermiteSpline> sp = group.getSplines();

        splines.clear();

        splines.add(new TextField(df.format(sp.get(0).getPose0().getPosition().getX()), textFieldStyle));
        splines.add(new TextField(df.format(sp.get(0).getPose0().getPosition().getY()), textFieldStyle));
        splines.add(new TextField(df.format(sp.get(0).getVelocity0().getX()), textFieldStyle));
        splines.add(new TextField(df.format(sp.get(0).getVelocity0().getY()), textFieldStyle));

        for(QuinticHermiteSpline s : sp) {
            splines.add(new TextField(df.format(s.getPose1().getPosition().getX()), textFieldStyle));
            splines.add(new TextField(df.format(s.getPose1().getPosition().getY()), textFieldStyle));
            splines.add(new TextField(df.format(s.getVelocity1().getX()), textFieldStyle));
            splines.add(new TextField(df.format(s.getVelocity1().getY()), textFieldStyle));
        }

        table.clear();
        for(int i = 0; i < splines.size()/4; i++) {
            table.add(new Label("  P" + i, lStyle2)).width(40).height(40);
            table.add(splines.get(4*i)).width(105).height(40);
            table.add(splines.get(4*i+1)).width(105).height(40);
            table.row();
            table.add(new Label("  V" + i, lStyle2)).width(40).height(40);
            table.add(splines.get(4*i+2)).width(105).height(40);
            table.add(splines.get(4*i+3)).width(105).height(40);
            table.row();
        }
    }

    public void updateSplineEdit() {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(PathSim.pathManager.curEditingPath).getParametric();
        ArrayList<QuinticHermiteSpline> sp = group.getSplines();

        if(!splines.get(0).hasKeyboardFocus()) splines.get(0).setText(df.format(sp.get(0).getPose0().getPosition().getX()));
        if(!splines.get(1).hasKeyboardFocus()) splines.get(1).setText(df.format(sp.get(0).getPose0().getPosition().getY()));
        if(!splines.get(2).hasKeyboardFocus()) splines.get(2).setText(df.format(sp.get(0).getVelocity0().getX()));
        if(!splines.get(3).hasKeyboardFocus()) splines.get(3).setText(df.format(sp.get(0).getVelocity0().getY()));

        int i = 4;
        for(QuinticHermiteSpline s : sp) {
            if(!splines.get(i).hasKeyboardFocus()) splines.get(i).setText(df.format(s.getPose1().getPosition().getX()));
            if(!splines.get(i+1).hasKeyboardFocus()) splines.get(i+1).setText(df.format(s.getPose1().getPosition().getY()));
            if(!splines.get(i+2).hasKeyboardFocus()) splines.get(i+2).setText(df.format(s.getVelocity1().getX()));
            if(!splines.get(i+3).hasKeyboardFocus()) splines.get(i+3).setText(df.format(s.getVelocity1().getY()));
            i += 4;
        }
    }

    public void addListeners() {
        addPath.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0) {
                    addingSpline = 2;
                    PathSim.pathManager.curEditingPath = -1;
                }
            }
        });

        addNode1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0 && PathSim.pathManager.curEditingPath != -1) {
                    addingSpline = 3;
                }
            }
        });

        addNode2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0 && PathSim.pathManager.curEditingPath != -1) {
                    addingSpline = 4;
                }
            }
        });

        path.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                splineMode = false;
            }
        });

        spline.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                splineMode = true;
            }
        });
    }

    @Override
    public void dispose() {

    }
}
