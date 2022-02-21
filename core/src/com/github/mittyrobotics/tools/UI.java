package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.github.mittyrobotics.PathSim;

public class UI implements Disposable {

    public Stage stage;
    public Table container, table;
    public ScrollPane pane;

    public UI() {
        stage = new Stage();

        container = new Table();
        table = new Table();

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = PathSim.skin.getDrawable("scroll_vertical_knob");

        pane = new ScrollPane(table, scrollPaneStyle);
        pane.setScrollingDisabled(true, false);
        pane.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                System.out.println("hello????");
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
        container.setBounds(Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH, 250, 300, 200);
        stage.addActor(container);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_pressed");

        for(int i = 0; i < 10; i++) {
            TextButton label = new TextButton("Testing", textButtonStyle);
            label.getLabel().setFontScale(0.8f);
            table.add(label);
            table.row();
        }
    }

    public void update(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {

    }
}
