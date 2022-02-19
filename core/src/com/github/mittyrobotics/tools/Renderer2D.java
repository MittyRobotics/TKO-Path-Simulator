package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.mittyrobotics.PathSim;

public class Renderer2D {

    public static CamController2D camController;
    public boolean loading;
    public Texture field;
    public SpriteBatch batch;

    public double fieldWidth, fieldHeight;
    public int width, height;
    public double zoom, centerx, centery, xc, yc;

    public ShapeRenderer shapeRenderer;

    public Renderer2D() {

        width = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;
        height = Gdx.graphics.getHeight();

        loading = true;
        batch = new SpriteBatch();
        xc = 0;
        yc = 0;
        zoom = 0.11;

        camController = new CamController2D(this, width, height);

        shapeRenderer = new ShapeRenderer();

    }


    public void doneLoading() {
        field = PathSim.assets.get("FIELD_RENDER.png", Texture.class);
        fieldWidth = field.getWidth();
        fieldHeight = field.getHeight();

        loading = false;
    }

    public void render () {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        if(loading && PathSim.assets.update()) {
            doneLoading();
        }

        if(!loading) {
            double x = xc + (width - zoom * fieldWidth)/2;
            double y = yc + (height - zoom * fieldHeight)/2;

            batch.begin();
            batch.draw(field, (int) x, (int) y, (int) (fieldWidth * zoom), (int) (fieldHeight * zoom));
            batch.end();

            centerx = x + (fieldWidth * zoom) / 2;
            centery = y + (fieldHeight * zoom) / 2;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
            shapeRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);

            shapeRenderer.end();

        }
    }

    public void dispose () {
        batch.dispose();
    }

}
