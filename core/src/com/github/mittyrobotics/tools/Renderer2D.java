package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

public class Renderer2D {

    public static CamController2D camController;
    public boolean loading;
    public Texture field, title1, title2, pointl, points;
    public SpriteBatch batch, fontBatch, onBatch;

    public double fieldWidth, fieldHeight, inch, zoom, centerx, centery, xc, yc, x, y;
    public int width, height, right;

    public ShapeRenderer uiRenderer, fieldRenderer;
    public BitmapFont font, font2;
    public ShaderProgram fontShader;

    public Color blue = new Color(67/255f,227/255f,1f, 1f);
    public Color green = new Color(67/255f, 1f, 170/255f, 1f);

    public UI ui;

    public Renderer2D() {

        width = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;
        height = Gdx.graphics.getHeight();

        loading = true;
        batch = new SpriteBatch();
        fontBatch = new SpriteBatch();
        onBatch = new SpriteBatch();
        xc = 0;
        yc = 0;
        zoom = 0.15;

        camController = new CamController2D(this, width, height);

        uiRenderer = new ShapeRenderer();
        fieldRenderer = new ShapeRenderer();

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;


        //fonts ----------
        Texture texture = new Texture(Gdx.files.internal("font/futura12.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        font = new BitmapFont(Gdx.files.internal("font/futura12.fnt"), new TextureRegion(texture), false);

        Texture texture2 = new Texture(Gdx.files.internal("font/futura16.png"), true);
        texture2.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        font2 = new BitmapFont(Gdx.files.internal("font/futura16.fnt"), new TextureRegion(texture2), false);

        fontShader = new ShaderProgram(Gdx.files.internal("font/font.vert"), Gdx.files.internal("font/font.frag"));
        if (!fontShader.isCompiled()) {
            Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
        }
        //end fonts ---------

        ui = new UI();

    }


    public void doneLoading() {
        field = PathSim.assets.get("FIELD_RENDER.png", Texture.class);
        fieldWidth = field.getWidth();
        fieldHeight = field.getHeight();

        title1 = PathSim.assets.get("title1.png", Texture.class);
        title2 = PathSim.assets.get("title2.png", Texture.class);
        pointl = PathSim.assets.get("pointl.png", Texture.class);
        points = PathSim.assets.get("points.png", Texture.class);


        loading = false;
    }

    public void render () {

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        if(loading && PathSim.assets.update()) {
            doneLoading();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            UI.addingSpline = 0;
        }

        if(!loading) {
            //calc------------------
            x = xc + (width - zoom * fieldWidth)/2;
            y = yc + (height - zoom * fieldHeight)/2;
            centerx = x + (fieldWidth * zoom) / 2;
            centery = y + (fieldHeight * zoom) / 2;
            inch = fieldWidth * zoom / 864;
            //end calc--------------

            //draw field
            drawFieldOverlay();

            //draw sprites
            drawSprites();

            //UI panel
            drawUIOverlay();

            //update stage
            ui.update(Gdx.graphics.getDeltaTime());

            //little text blurb on bottom
            drawText();
        }
    }

    public void drawSprites() {
        fieldRenderer.begin(ShapeRenderer.ShapeType.Line);
        double step = 0.001;

        for(PurePursuitPath path : PathSim.pathManager.paths) {
            fieldRenderer.setColor(green);
            Parametric spline = path.getParametric();
            for(double t = step; t <= 1; t += step) {
                Point2D p1 = toPointOnScreen(spline.getPoint(t-step));
                Point2D p2 = toPointOnScreen(spline.getPoint(t));
//                fieldRenderer.circle((float) p.x - 1, (float) p.y - 1, 2);
                fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 2);
            }

            fieldRenderer.setColor(blue);
            for(int t = 0; t <= 1; t += 1) {
                Point2D p = toPointOnScreen(spline.getPoint(t));
                Point2D v = toPointOnScreen(spline.getDerivative(t, 1).multiply(t == 1 ? 1/3. : -1/3.).add(spline.getPoint(t)));
                fieldRenderer.rectLine((float) p.x, (float) p.y, (float) v.x, (float) v.y, 1);
            }
        }
        fieldRenderer.end();

        onBatch.begin();

        //placing points! ---------------
        if(UI.addingSpline > 0 && Gdx.input.getX() < PathSim.LEFT_WIDTH) {
            onBatch.draw(pointl, Gdx.input.getX() - pointl.getWidth()/2f, Gdx.graphics.getHeight() - Gdx.input.getY() - pointl.getHeight()/2f, pointl.getWidth(), pointl.getHeight());
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if(UI.addingSpline == 2) {
                    PathSim.pathManager.storePoint(toPointInInches(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
                    UI.addingSpline = 1;
                } else {
                    PathSim.pathManager.addPathFromPoint(toPointInInches(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY()));
                    UI.addingSpline = 0;
                }
            }
        }

        if(PathSim.pathManager.storedPoint != null) {
            Point2D p = toPointOnScreen(PathSim.pathManager.storedPoint);
            onBatch.draw(pointl, (float) p.x - pointl.getWidth()/2f, (float) p.y - pointl.getHeight()/2f, pointl.getWidth(), pointl.getHeight());
        }

        //end placing points! -----------

        //draw splines

        for(PurePursuitPath path : PathSim.pathManager.paths) {
            Parametric spline = path.getParametric();
            for(int t = 0; t <= 1; t += 1) {
                Point2D p = toPointOnScreen(spline.getPoint(t));
                onBatch.draw(pointl, (float) p.x - pointl.getWidth()/2f, (float) p.y - pointl.getHeight()/2f, pointl.getWidth(), pointl.getHeight());

                Point2D v = toPointOnScreen(spline.getDerivative(t, 1).multiply(t == 1 ? 1/3. : -1/3.).add(spline.getPoint(t)));
                onBatch.draw(points, (float) (v.x - points.getWidth()/2f), (float) (v.y - points.getHeight()/2f), points.getWidth(), points.getHeight());

            }
        }

        onBatch.end();
    }

    public void drawText() {
        if(UI.addingSpline > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
            uiRenderer.setColor(0.12f, 0.12f, 0.12f, 0.8f);
            uiRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);
            uiRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void drawUIOverlay() {
        uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
        uiRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        uiRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);

        if(UI.addingSpline > 0) {
            uiRenderer.setColor(0.12f, 0.12f, 0.12f, 0.5f);
            uiRenderer.rect((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - ui.addingLabel.getPrefWidth() / 2f - 10, 70, ui.addingLabel.getPrefWidth() + 20, 30);
        }

        uiRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        batch.draw(title1, right + 50, Gdx.graphics.getHeight() - 150, 200, (int) (200. * title1.getHeight() / title1.getWidth()));
        batch.draw(title2, right + 40, Gdx.graphics.getHeight() - 210, 220, (int) (220. * title2.getHeight() / title2.getWidth()));
        batch.end();
    }

    public void drawFieldOverlay() {
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
        uiRenderer.begin(ShapeRenderer.ShapeType.Filled);

        uiRenderer.setColor(0.9f, 0.9f, 0.9f, 0.9f);
        uiRenderer.rect(0, (int) centery-1, width, 1);
        uiRenderer.rect((int) centerx-1, 0, 1, height);

        if(zoom < 0.22) {
            drawGrid(uiRenderer, centerx, centery, width, height, 24 * inch, 0.3f);
        } else if (zoom < 0.4) {
            drawGrid(uiRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
        } else if (zoom < 0.7) {
            drawGrid(uiRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
            drawGrid(uiRenderer, centerx, centery, width, height, 6 * inch, 0.2f);
        } else {
            drawGrid(uiRenderer, centerx, centery, width, height, 1 * inch, 0.1f);
            drawGrid(uiRenderer, centerx, centery, width, height, 6 * inch, 0.2f);
            drawGrid(uiRenderer, centerx, centery, width, height, 12 * inch, 0.3f);
        }
        uiRenderer.end();
    }

    //conversions
    public Point2D toPointInInches(int screenx, int screeny) {
        return new Point2D((screenx - centerx) / inch, (screeny - centery) / inch);
    }

    public Point2D toPointOnScreen(Point2D pointInInches) {
        return new Point2D(pointInInches.x * inch + centerx, pointInInches.y * inch + centery);
    }

    //big brain math dont touch ---------------------------------------
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
            String temp = "";
            for(int j = 0; j < 3-(cur+"").length(); j++) {
                temp += " ";
            }
            font.draw(batch, temp + cur, (int) centerxnew - 26, (int) (i+6));
        }

        cur = 0;
        for(double i = centery - increment; i >= 0; i -= increment) {
            cur -= t;
            String temp = "";
            for(int j = 0; j < 3-(cur+"").length(); j++) {
                temp += " ";
            }
            font.draw(batch, temp + cur, (int) centerxnew - 26, (int) (i+6));
        }
    }

    public void dispose () {
        batch.dispose();
    }

}
