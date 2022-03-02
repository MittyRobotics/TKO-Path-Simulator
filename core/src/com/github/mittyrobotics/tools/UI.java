package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.github.mittyrobotics.PathSim;

public class UI implements Disposable {

    public Stage stage;
    public Table container, table;
    public ScrollPane pane;
    public int right, prevState;
    public static int addingSpline;
    public Label addingLabel;

    public UI() {
        stage = new Stage();

        container = new Table();
        table = new Table();

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = PathSim.skin.getDrawable("scroll_vertical_knob");

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
        pane.setWidth(300);
        pane.layout();
        container.add(pane).width(300).height(200);
        container.row();
        container.setBounds(right, 250, 300, 200);
        stage.addActor(container);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_pressed");

        TextButton addPath = new TextButton("Add Path", textButtonStyle);
        addPath.getLabel().setFontScale(0.7f);
        addPath.setBounds(right+50, Gdx.graphics.getHeight() - 300, 200, 50);

        TextButton addNode1 = new TextButton("Add Start Node", textButtonStyle);
        addNode1.getLabel().setFontScale(0.7f);
        addNode1.setBounds(right+50, Gdx.graphics.getHeight() - 370, 200, 50);

        TextButton addNode2 = new TextButton("Add End Node", textButtonStyle);
        addNode2.getLabel().setFontScale(0.7f);
        addNode2.setBounds(right+50, Gdx.graphics.getHeight() - 440, 200, 50);

        addPath.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0) {
                    addingSpline = 2;
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

        addNode1.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                PathSim.renderer2d.onButton = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                PathSim.renderer2d.onButton = false;
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

        addNode2.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                PathSim.renderer2d.onButton = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                PathSim.renderer2d.onButton = false;
            }
        });


        stage.addActor(addPath);
        stage.addActor(addNode1);
        stage.addActor(addNode2);

        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = PathSim.f20;
        lStyle.fontColor = new Color(104/255f,204/255f,220/255f, 1f);

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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {

    }
}
