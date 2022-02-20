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
    public Texture field, title1, title2;
    public SpriteBatch batch;

    public double fieldWidth, fieldHeight;
    public int width, height;
    public double zoom, centerx, centery, xc, yc;

    public int right;

    public ShapeRenderer shapeRenderer;

    public Renderer2D() {

        width = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;
        height = Gdx.graphics.getHeight();

        loading = true;
        batch = new SpriteBatch();
        xc = 0;
        yc = 0;
        zoom = 0.15;

        camController = new CamController2D(this, width, height);

        shapeRenderer = new ShapeRenderer();

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;

    }


    public void doneLoading() {
        field = PathSim.assets.get("FIELD_RENDER.png", Texture.class);
        fieldWidth = field.getWidth();
        fieldHeight = field.getHeight();

        title1 = PathSim.assets.get("title1.png", Texture.class);
        title2 = PathSim.assets.get("title2.png", Texture.class);

        loading = false;
    }

    public void render () {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        if(loading && PathSim.assets.update()) {
            doneLoading();
        }

        if(!loading) {
            double x = xc + (width - zoom * fieldWidth)/2;
            double y = yc + (height - zoom * fieldHeight)/2;
            centerx = x + (fieldWidth * zoom) / 2;
            centery = y + (fieldHeight * zoom) / 2;


            batch.begin();
            batch.draw(field, (int) x, (int) y, (int) (fieldWidth * zoom), (int) (fieldHeight * zoom));
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
            shapeRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);

            shapeRenderer.end();

            batch.begin();
            batch.draw(title1, right + 50, Gdx.graphics.getHeight() - 150, 200, (int) (200. * title1.getHeight() / title1.getWidth()));
            batch.draw(title2, right + 40, Gdx.graphics.getHeight() - 210, 220, (int) (220. * title2.getHeight() / title2.getWidth()));
            batch.end();

        }
    }

    public void dispose () {
        batch.dispose();
    }

}
