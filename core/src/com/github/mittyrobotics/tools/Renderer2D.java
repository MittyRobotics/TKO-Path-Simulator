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
    public int width, height, right, prevx, prevy;

    public ShapeRenderer uiRenderer, fieldRenderer;
    public BitmapFont font, font2;
    public ShaderProgram fontShader;

    public Color blue = new Color(67/255f,227/255f,1f, 1f);
    public Color transblue = new Color(67/255f,227/255f,1f, 0.3f);
    public Color green = new Color(67/255f, 1f, 170/255f, 1f);
    public Color transgreen = new Color(67/255f, 1f, 170/255f, 0.3f);

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

            handleInput();

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

    public void handleInput() {
        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        //general click handling --------------------
        if(Gdx.input.getX() < PathSim.LEFT_WIDTH) {
            if (UI.addingSpline > 0) {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    if (UI.addingSpline == 2) {
                        PathSim.pathManager.storePoint(toPointInInches(x, y));
                        UI.addingSpline = 1;
                    } else if (UI.addingSpline == 1) {
                        PathSim.pathManager.addPathFromPoint(toPointInInches(x, y));
                        UI.addingSpline = 0;
                    } else if (UI.addingSpline == 3) {
                        PathSim.pathManager.addPointToPath(toPointInInches(x, y), PathSim.pathManager.curEditingPath, true);
                        UI.addingSpline = 0;
                    } else if (UI.addingSpline == 4) {
                        PathSim.pathManager.addPointToPath(toPointInInches(x, y), PathSim.pathManager.curEditingPath, false);
                        UI.addingSpline = 0;
                    }
                }
            } else if (PathSim.pathManager.curEditingPath != -1) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    editPoint(PathSim.pathManager.curEditingPath, PathSim.pathManager.curEditingNode, PathSim.pathManager.curEditingVel, x - prevx, y - prevy);
                } else {
                    PathSim.pathManager.curEditingNode = -1;
                    PathSim.pathManager.curEditingVel = -1;
                }
            }
        }

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(Gdx.input.getX() < right) {
                PathSim.pathManager.curEditingPath = -1;
            }
            for (int i = 0; i < PathSim.pathManager.paths.size(); i++) {
                QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(i).getParametric();

                Point2D pos = toPointInInches(x, y);
                Point2D onscreen = toPointOnScreen(group.getPoint(group.findClosestPointOnSpline(pos, 50, 5)));
                Point2D posscreen = new Point2D(x, y);
                if(posscreen.distance(onscreen) <= 4) {
                    PathSim.pathManager.curEditingPath = i;
                    PathSim.pathManager.curEditingNode = -2;
                }

                for (int k = 0; k < group.getSplines().size(); k++) {
                    Point2D p = toPointOnScreen(group.getSpline(k).getPoint(1));
                    if(distance(x, y, p) < pointl.getWidth() / 2f) {
                        PathSim.pathManager.curEditingPath = i;
                        PathSim.pathManager.curEditingNode = k+1;
                    }
                    for (int t = 0; t <= 1; t += 1) {
                        Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                        if (distance(x, y, v) < points.getWidth() / 2f) {
                            PathSim.pathManager.curEditingPath = i;
                            PathSim.pathManager.curEditingVel = 2 * k + t;
                        }
                    }
                }

                Point2D p = toPointOnScreen(group.getSpline(0).getPoint(0));
                if(distance(x, y, p) < pointl.getWidth() / 2f) {
                    PathSim.pathManager.curEditingPath = i;
                    PathSim.pathManager.curEditingNode = 0;
                }
            }
        }

//        System.out.println("path: " + PathSim.pathManager.curEditingPath + " | node: " + PathSim.pathManager.curEditingNode
//                + " | vel: " + PathSim.pathManager.curEditingVel + " | on: " + PathSim.pathManager.curOnPath);

        if(x < 0 || x > PathSim.LEFT_WIDTH || y < 0 || y > Gdx.graphics.getHeight()) {
            PathSim.pathManager.curEditingNode = -1;
            PathSim.pathManager.curEditingVel = -1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            PathSim.pathManager.curEditingPath = -1;
        }

        prevx = x;
        prevy = y;

        PathSim.pathManager.curOnPath = -1;
        for (int i = 0; i < PathSim.pathManager.paths.size(); i++) {
            QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(i).getParametric();

            Point2D pos = toPointInInches(x, y);
            Point2D onscreen = toPointOnScreen(group.getPoint(group.findClosestPointOnSpline(pos, 50, 5)));
            Point2D posscreen = new Point2D(x, y);
            if(posscreen.distance(onscreen) <= 4) {
                PathSim.pathManager.curOnPath = i;
            }

            for (int k = 0; k < group.getSplines().size(); k++) {
                Point2D p = toPointOnScreen(group.getSpline(k).getPoint(1));
                if(distance(x, y, p) < pointl.getWidth() / 2f) {
                    PathSim.pathManager.curOnPath = i;
                }
                for (int t = 0; t <= 1; t += 1) {
                    Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                    if (distance(x, y, v) < points.getWidth() / 2f) {
                        PathSim.pathManager.curOnPath = i;
                    }
                }
            }

            Point2D p = toPointOnScreen(group.getSpline(0).getPoint(0));
            if(distance(x, y, p) < pointl.getWidth() / 2f) {
                PathSim.pathManager.curOnPath = i;
            }
        }

        //end general click handling -----------------
    }

    public void drawSprites() {
        fieldRenderer.begin(ShapeRenderer.ShapeType.Filled);
        double step = 0.001;

        int j = 0;
        for(Path path : PathSim.pathManager.paths) {
            fieldRenderer.setColor(green);
            QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) path.getParametric();
            for(double t = step; t <= 1; t += step) {
                Point2D p1 = toPointOnScreen(group.getPoint(t-step));
                Point2D p2 = toPointOnScreen(group.getPoint(t));
                fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 3);
                if(PathSim.pathManager.curOnPath == j || PathSim.pathManager.curEditingPath == j) {
                    fieldRenderer.setColor(transgreen);
                    fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 8);
                    fieldRenderer.setColor(green);
                }
            }

            fieldRenderer.setColor(blue);
            for(int k = 0; k < group.getSplines().size(); k++) {
                for (int t = 0; t <= 1; t += 1) {
                    Point2D p = toPointOnScreen(group.getSpline(k).getPoint(t));
                    Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                    fieldRenderer.rectLine((float) p.x, (float) p.y, (float) v.x, (float) v.y, 2);
                }
            }
            j++;
        }
        fieldRenderer.end();

        onBatch.begin();

        int x = Gdx.input.getX();
        int y = Gdx.graphics.getHeight() - Gdx.input.getY();
        if(Gdx.input.getX() < PathSim.LEFT_WIDTH) {
            if (UI.addingSpline > 0) {
                onBatch.draw(pointl, x - pointl.getWidth() / 2f, y - pointl.getHeight() / 2f, pointl.getWidth(), pointl.getHeight());
            }
        }

        if(PathSim.pathManager.storedPoint != null) {
            Point2D p = toPointOnScreen(PathSim.pathManager.storedPoint);
            onBatch.draw(pointl, (float) p.x - pointl.getWidth()/2f, (float) p.y - pointl.getHeight()/2f, pointl.getWidth(), pointl.getHeight());
        }

        //draw splines

        for(Path path : PathSim.pathManager.paths) {
            QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) path.getParametric();
            for(int k = 0; k < group.getSplines().size(); k++) {
                for (int t = 0; t <= 1; t += 1) {
                    Point2D p = toPointOnScreen(group.getSpline(k).getPoint(t));
                    onBatch.draw(pointl, (float) p.x - pointl.getWidth() / 2f, (float) p.y - pointl.getHeight() / 2f, pointl.getWidth(), pointl.getHeight());

                    Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                    onBatch.draw(points, (float) (v.x - points.getWidth() / 2f), (float) (v.y - points.getHeight() / 2f), points.getWidth(), points.getHeight());
                }
            }
        }

        onBatch.end();
    }

    public void editPoint(int curPath, int curNode, int curVel, double dx, double dy) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) (PathSim.pathManager.paths.get(curPath).getParametric());
        if (curVel != -1) {
            int splineId = curVel / 2;
            double prev = group.getSpline(splineId).getLength();
            QuinticHermiteSpline spline = group.getSpline(splineId);
            if(curVel % 2 == 0) {
                spline.setVelocity0(spline.getVelocity0().add(new Vector2D(-dx * 5 / inch, -dy * 5 / inch)));
                spline.setPose0(new Pose2D(spline.getPose0().getPosition(), new Angle(new Point2D(spline.getVelocity0()))));
                if(splineId > 0) {
                    double prev1 = group.getSpline(splineId-1).getLength();
                    double angle = Math.atan2(spline.getVelocity0().getY(), spline.getVelocity0().getX());
                    double mag = new Point2D(group.getSpline(splineId-1).getVelocity1()).magnitude();
                    group.getSpline(splineId-1).setVelocity1(new Vector2D(mag * Math.cos(angle), mag * Math.sin(angle)));
                    group.getSpline(splineId-1).setPose1(new Pose2D(group.getSpline(splineId-1).getPose1().getPosition(), new Angle(new Point2D(group.getSpline(splineId-1).getVelocity1()))));
                    group.updateSplineLength(splineId-1, prev1);
                }
            } else {
                spline.setVelocity1(spline.getVelocity1().add(new Vector2D(dx * 5 / inch, dy * 5 / inch)));
                spline.setPose1(new Pose2D(spline.getPose1().getPosition(), new Angle(new Point2D(spline.getVelocity1()))));
                if(splineId < group.getSplines().size() - 1) {
                    double prev1 = group.getSpline(splineId+1).getLength();
                    double angle = Math.atan2(spline.getVelocity1().getY(), spline.getVelocity1().getX());
                    double mag = new Point2D(group.getSpline(splineId+1).getVelocity0()).magnitude();
                    group.getSpline(splineId+1).setVelocity0(new Vector2D(mag * Math.cos(angle), mag * Math.sin(angle)));
                    group.getSpline(splineId+1).setPose0(new Pose2D(group.getSpline(splineId+1).getPose0().getPosition(), new Angle(new Point2D(group.getSpline(splineId+1).getVelocity0()))));
                    group.updateSplineLength(splineId+1, prev1);
                }
            }
            group.updateSplineLength(splineId, prev);
        } else if (curNode >= 0) {
            if(curNode == 0) {
                double prev = group.getSpline(curNode).getLength();
                group.getSpline(curNode).setPose0(new Pose2D(group.getSpline(curNode).getPose0().getPosition().add(new Point2D(dx / inch, dy / inch)), group.getSpline(curNode).getPose0().getAngle()));
                group.updateSplineLength(curNode, prev);
            } else if (curNode == group.getSplines().size()) {
                double prev = group.getSpline(curNode - 1).getLength();
                group.getSpline(curNode-1).setPose1(new Pose2D(group.getSpline(curNode-1).getPose1().getPosition().add(new Point2D(dx / inch, dy / inch)), group.getSpline(curNode-1).getPose1().getAngle()));
                group.updateSplineLength(curNode-1, prev);
            } else {
                double prev1 = group.getSpline(curNode-1).getLength();
                double prev2 = group.getSpline(curNode).getLength();

                group.getSpline(curNode).setPose0(new Pose2D(group.getSpline(curNode).getPose0().getPosition().add(new Point2D(dx / inch, dy / inch)), group.getSpline(curNode).getPose0().getAngle()));
                group.getSpline(curNode-1).setPose1(new Pose2D(group.getSpline(curNode-1).getPose1().getPosition().add(new Point2D(dx / inch, dy / inch)), group.getSpline(curNode-1).getPose1().getAngle()));

                group.updateSplineLength(curNode-1, prev1);
                group.updateSplineLength(curNode, prev2);
            }
        } else if (curNode == -2) {
            moveSplineGroup(group, dx / inch, dy / inch);
        }
    }

    public void moveSplineGroup(QuinticHermiteSplineGroup group, double dx, double dy) {
        for(QuinticHermiteSpline spline : group.getSplines()) {
            spline.setPose0(new Pose2D(spline.getPose0().getPosition().add(new Point2D(dx, dy)), spline.getPose0().getAngle()));
            spline.setPose1(new Pose2D(spline.getPose1().getPosition().add(new Point2D(dx, dy)), spline.getPose1().getAngle()));
        }
        group.updateSplineLength();
    }

    public double distance(int mx, int my, Point2D p) {
        return Math.sqrt((mx - p.x) * (mx - p.x) + (my - p.y) * (my - p.y));
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
        uiRenderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        uiRenderer.rect(width + 25, 25, PathSim.RIGHT_WIDTH - 50, 500);

        uiRenderer.setColor(0.20f, 0.20f, 0.20f, 1f);
        if(PathSim.pathManager.curEditingPath != -1) {
            if(ui.splineMode) {
                uiRenderer.rect(right+25, Gdx.graphics.getHeight() - 445, 125, 35);
            } else {
                uiRenderer.rect(right+150, Gdx.graphics.getHeight() - 445, 125, 35);
            }
            uiRenderer.rect(width+25, 25, PathSim.RIGHT_WIDTH - 50, 402);
        }

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

        double centerynew = Math.max(Math.min(centery, height), 48);
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
