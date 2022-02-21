package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.mittyrobotics.PathSim;

public class Renderer2D {

    public static CamController2D camController;
    public boolean loading;
    public Texture field, title1, title2;
    public SpriteBatch batch, fontBatch;

    public double fieldWidth, fieldHeight, inch, zoom, centerx, centery, xc, yc;
    public int width, height, right;

    public ShapeRenderer shapeRenderer;
    public BitmapFont font;
    public ShaderProgram fontShader;

    public Renderer2D() {

        width = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;
        height = Gdx.graphics.getHeight();

        loading = true;
        batch = new SpriteBatch();
        fontBatch = new SpriteBatch();
        xc = 0;
        yc = 0;
        zoom = 0.15;

        camController = new CamController2D(this, width, height);

        shapeRenderer = new ShapeRenderer();

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;


        Texture texture = new Texture(Gdx.files.internal("font/futura12.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        font = new BitmapFont(Gdx.files.internal("font/futura12.fnt"), new TextureRegion(texture), false);

        fontShader = new ShaderProgram(Gdx.files.internal("font/font.vert"), Gdx.files.internal("font/font.frag"));
        if (!fontShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
        }

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
            inch = fieldWidth * zoom / 864;


            batch.begin();
            batch.draw(field, (int) x, (int) y, (int) (fieldWidth * zoom), (int) (fieldHeight * zoom));
            batch.end();

            fontBatch.begin();
            fontBatch.setShader(fontShader);
            if(zoom < 0.22) {
                drawText(font, fontBatch, centerx, centery, width, height, 48 * inch, 4);
            } else if (zoom < 0.4) {
                drawText(font, fontBatch, centerx, centery, width, height, 24 * inch, 2);
            } else if (zoom < 0.7) {
                drawText(font, fontBatch, centerx, centery, width, height, 12 * inch, 1);
            } else {
                drawText(font, fontBatch, centerx, centery, width, height, 12 * inch, 1);
            }
            fontBatch.end();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 1f);
            shapeRenderer.rect(0, (int) centery-1, width, 1);
            shapeRenderer.rect((int) centerx-1, 0, 1, height);

            if(zoom < 0.22) {
                drawGrid(shapeRenderer, centerx, centery, width, height, 24 * inch, 0.3f);
            } else if (zoom < 0.4) {
                drawGrid(shapeRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
            } else if (zoom < 0.7) {
                drawGrid(shapeRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
                drawGrid(shapeRenderer, centerx, centery, width, height, 6 * inch, 0.2f);
            } else {
                drawGrid(shapeRenderer, centerx, centery, width, height, 1 * inch, 0.1f);
                drawGrid(shapeRenderer, centerx, centery, width, height, 6 * inch, 0.2f);
                drawGrid(shapeRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
            }


            shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
            shapeRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);

            shapeRenderer.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            batch.begin();
            batch.draw(title1, right + 50, Gdx.graphics.getHeight() - 150, 200, (int) (200. * title1.getHeight() / title1.getWidth()));
            batch.draw(title2, right + 40, Gdx.graphics.getHeight() - 210, 220, (int) (220. * title2.getHeight() / title2.getWidth()));
            batch.end();

        }
    }

    public void drawGrid(ShapeRenderer renderer, double centerx, double centery, int width, int height, double increment, float transparency) {
        renderer.setColor(0.78f, 0.78f, 0.78f, transparency);

        for(double i = centerx + increment; i <= width; i += increment) {
            renderer.rect((int) (i-0.5), 0, 1, height);
        }

        for(double i = centerx - increment; i >= 0; i -= increment) {
            renderer.rect((int) (i-0.5), 0, 1, height);
        }

        for(double i = centery + increment; i <= height; i += increment) {
            renderer.rect(0, (int) (i-0.5), width, 1);
        }

        for(double i = centery - increment; i >= 0; i -= increment) {
            renderer.rect(0, (int) (i-0.5), width, 1);
        }
    }

    public void drawText(BitmapFont font, SpriteBatch batch, double centerx, double centery, int width, int height, double increment, int t) {
        int cur = 0;

        double centerynew = Math.max(Math.min(centery, height), 20);
        double centerxnew = Math.max(Math.min(centerx, width-2), 28);

        for(double i = centerx + increment; i <= width; i += increment) {
            cur += t;
            font.draw(batch, cur+"", (int) (i-4-4*((cur+"").length()-1)), (int) centerynew - 5);
        }

        cur = 0;
        for(double i = centerx - increment; i >= 0; i -= increment) {
            cur -= t;
            font.draw(batch, cur+"", (int) (i-4-4*((cur+"").length()-1)), (int) centerynew - 5);
        }

        cur = 0;
        for(double i = centery + increment; i <= height; i += increment) {
            cur += t;
            String temp = " ".repeat(3-(cur+"").length());
            font.draw(batch, temp + cur, (int) centerxnew - 26, (int) (i+6));
        }

        cur = 0;
        for(double i = centery - increment; i >= 0; i -= increment) {
            cur -= t;
            String temp = " ".repeat(3-(cur+"").length());
            font.draw(batch, temp + cur, (int) centerxnew - 26, (int) (i+6));
        }
    }

    public void dispose () {
        batch.dispose();
    }

}
