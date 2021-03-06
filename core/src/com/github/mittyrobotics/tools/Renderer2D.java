package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

import javax.swing.*;
import java.util.ArrayList;

public class Renderer2D {

    public static CamController2D camController;
    public boolean loading, addFront, addBack, wasJustPlaced, addNew, scrubbing, movingWidget, widgetExpanded, importShown;
    public Texture field, title1, title2, pointl, points, pointp, pointw, pointh, pointt, trash, edit, visible, invisible;
    public SpriteBatch batch, fontBatch, onBatch;
    public Stage imports;

    public double fieldWidth, fieldHeight, inch, zoom, centerx, centery, xc, yc, x, y, wx, wy, prevx, prevy;
    public int width, height, right, widgetX, widgetY, ww, wh, rwx, rwy;
    public Point2D temp;

    public ShapeRenderer uiRenderer, fieldRenderer;
    public BitmapFont font, font2;
    public ShaderProgram fontShader;

    public Color blue = new Color(67/255f,227/255f,1f, 1f);
    public Color transblue = new Color(67/255f,227/255f,1f, 0.3f);
    public Color green = new Color(67/255f, 1f, 170/255f, 1f);
    public Color transgreen = new Color(67/255f, 1f, 170/255f, 0.3f);
    public Color transgreen2 = new Color(67/255f, 1f, 170/255f, 0.6f);
    public Color transred = new Color(1f, 100f/255f, 80/255f, 0.6f);

    public Label.LabelStyle lStyle;
    public TextField.TextFieldStyle tfStyle;
    public TextArea a;

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
        widgetX = 20;
        widgetY = height - 290;
        ww = 250;
        wh = 280;
        widgetExpanded = true;


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

        imports = new Stage();

        lStyle = new Label.LabelStyle();
        lStyle.font = PathSim.font;
        lStyle.fontColor = Color.WHITE;

        Label title = new Label("Import Paths", lStyle);
        title.setBounds(0, Gdx.graphics.getHeight() - 150, Gdx.graphics.getWidth(), 50);
        title.setAlignment(Align.center);
        title.setFontScale(1.2f);
        imports.addActor(title);

        tfStyle = new TextField.TextFieldStyle(PathSim.font, Color.WHITE,
                PathSim.skin.getDrawable("textfield_cursor"), PathSim.skin.getDrawable("textfield_selection"), null);
        tfStyle.font.getData().setScale(0.55f);
        a = new TextArea("", tfStyle);
        a.setBounds(250, 140, Gdx.graphics.getWidth() - 500, Gdx.graphics.getHeight() - 320);
        imports.addActor(a);

        a.addListener(new InputListener() {
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
                boolean shown = imports.getKeyboardFocus() != null;
                if(!shown) imports.setKeyboardFocus(a);
                if(amountY > 0 && a.getCursorLine() < a.getLines()) {
                    if(!shown) a.moveCursorLine(a.getFirstLineShowing() + a.getLinesShowing() + 1);
                    else a.moveCursorLine(a.getCursorLine() + 1);
                } else if (a.getCursorLine() > 0) {
                    if(!shown) a.moveCursorLine(a.getFirstLineShowing() - 1);
                    else a.moveCursorLine(a.getCursorLine() - 1);
                }
                if(!shown) imports.setKeyboardFocus(null);
                return true;
            }
        });

        a.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                imports.setScrollFocus(a);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                imports.setScrollFocus(null);
            }
        });

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_focused");

        TextButton importButton = new TextButton("Import", textButtonStyle);
        importButton.setBounds(Gdx.graphics.getWidth() / 2f - 200f, 90, 195, 50);
        importButton.getLabel().setFontScale(0.7f);
        imports.addActor(importButton);

        TextButton cancel = new TextButton("Cancel", textButtonStyle);
        cancel.setBounds(Gdx.graphics.getWidth() / 2f + 5f, 90, 195, 50);
        cancel.getLabel().setFontScale(0.7f);
        imports.addActor(cancel);

        importButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PathSim.pathManager.paths.addAll(PathImporter.parse(a.getText()));
                a.setText("");
                closeImportScreen();
                ui.populateWidget();
            }
        });

        cancel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                a.setText("");
                closeImportScreen();
            }
        });

    }


    public void doneLoading() {
        field = PathSim.assets.get("img/FIELD_RENDER.png", Texture.class);
        fieldWidth = field.getWidth();
        fieldHeight = field.getHeight();

        title1 = PathSim.assets.get("img/title1.png", Texture.class);
        title2 = PathSim.assets.get("img/title2.png", Texture.class);
        pointl = PathSim.assets.get("img/pointl.png", Texture.class);
        points = PathSim.assets.get("img/points.png", Texture.class);
        pointp = PathSim.assets.get("img/pointp.png", Texture.class);
        pointw = PathSim.assets.get("img/pointw.png", Texture.class);
        pointh = PathSim.assets.get("img/pointh.png", Texture.class);
        pointt = PathSim.assets.get("img/pointt.png", Texture.class);
        trash = PathSim.assets.get("img/trash.png", Texture.class);
        edit = PathSim.assets.get("img/edit.png", Texture.class);
        visible = PathSim.assets.get("img/visible.png", Texture.class);
        invisible = PathSim.assets.get("img/invisible.png", Texture.class);


        ui = new UI();
        PathSim.input.addProcessor(ui.stage);

        loading = false;
    }

    public void render () {

        Gdx.gl.glViewport(0, 0, width + PathSim.RIGHT_WIDTH, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        if(loading && PathSim.assets.update()) {
            doneLoading();
        }

        if(!loading) {
            //calc------------------
            x = xc + (width - zoom * fieldWidth)/2;
            y = yc + (height - zoom * fieldHeight)/2;
            centerx = x + (fieldWidth * zoom) / 2;
            centery = y + (fieldHeight * zoom) / 2;
            inch = fieldWidth * zoom / 864;
            //end calc--------------

            if(!importShown) handleInput();

            //draw field
            drawFieldOverlay();

            //draw sprites
            drawSprites();

            //UI panel
            drawUIOverlay();

            //widget
            drawWidget();

            //update stage
            ui.update(Gdx.graphics.getDeltaTime());

            //overlapping text
            drawText();

            if(importShown) drawImport();

            PathSim.pathManager.updateRemove();
        }
    }

    public void drawImport() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if(imports.getKeyboardFocus() != null) imports.setKeyboardFocus(null);
            else closeImportScreen();
        }

        uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
        uiRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
        roundedRect(uiRenderer, 200, 75, Gdx.graphics.getWidth() - 400, Gdx.graphics.getHeight() - 150, 50);
        uiRenderer.setColor(new Color(0.16f, 0.16f, 0.16f, 1f));
        roundedRect(uiRenderer, 205, 80, Gdx.graphics.getWidth() - 410, Gdx.graphics.getHeight() - 160, 50);
        uiRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        roundedRect(uiRenderer, 240, 150, Gdx.graphics.getWidth() - 480, Gdx.graphics.getHeight() - 320, 10);
        uiRenderer.end();

        imports.act(Gdx.graphics.getDeltaTime());
        imports.draw();

        if(imports.getKeyboardFocus() != null) imports.setScrollFocus(a);
        else imports.setKeyboardFocus(null);
    }

    public void drawWidget() {
        double x = getX();
        double y = getY();

        rwx = Math.max(0, Math.min(widgetX, right - ww));
        rwy = Math.max(0, Math.min(widgetY, height - wh));

        if(justClicked() && UI.addingSpline == 0) {
            if(x >= rwx + ww - 35 && x <= rwx + ww - 15 && y >= rwy + wh - 30 && y <= rwy + wh - 10) {
                widgetExpanded = !widgetExpanded;
                wh = widgetExpanded ? 280 : 40;
                widgetY += widgetExpanded ? -240 : 240;
            }
            if((x >= rwx && x <= rwx + ww && y >= rwy && y <= rwy + wh)) movingWidget = true;
        }
        if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) movingWidget = false;

        if(movingWidget) {
            widgetX += x - wx;
            widgetY += y - wy;
        } else {
            widgetX = rwx;
            widgetY = rwy;
        }

        rwx = Math.max(0, Math.min(widgetX, right - ww));
        rwy = Math.max(0, Math.min(widgetY, height - wh));

        uiRenderer.setColor(new Color(0f, 0f, 0f, 0.7f));
        roundedRect(uiRenderer, rwx, rwy, ww, wh, 5);
        uiRenderer.setColor(Color.WHITE);
        if(widgetExpanded) {
            uiRenderer.triangle(rwx + ww - 35, rwy + wh - 10, rwx + ww - 15, rwy + wh - 10, rwx + ww - 25f, rwy + wh - 30);
        } else {
            uiRenderer.triangle(rwx + ww - 35, rwy + wh - 30, rwx + ww - 35, rwy + wh - 10, rwx + ww - 15, rwy  + wh - 20);
        }


        wx = x;
        wy = y;
        uiRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void renderUI() {
        Gdx.gl.glViewport(0, 0, width + PathSim.RIGHT_WIDTH, height);

        //UI panel
        drawUIOverlay();

        drawText();

        //update stage
        ui.update(Gdx.graphics.getDeltaTime());
    }

    public void handleInput() {
        double x = getX();
        double y = getY();
        //general click handling --------------------
        if(x < PathSim.LEFT_WIDTH && inBounds(x, y) && UI.addingSpline > 0 && justClicked()) {
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
            wasJustPlaced = true;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            UI.addingSpline = 0;
            PathSim.pathManager.removeStoredPoint();
            PathSim.pathManager.curEditingPath = -1;
            PathSim.pathManager.curSelectedNode = -1;
            PathSim.pathManager.curEditingNode = -1;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DEL) && ui.stage.getKeyboardFocus() == null) {
            if(PathSim.pathManager.editingPath()) {
                if(UI.addingSpline > 0) {
                    UI.addingSpline = 0;
                } else if (PathSim.pathManager.curSelectedNode >= 0 &&
                        ((QuinticHermiteSplineGroup) (PathSim.pathManager.getCurPath().getParametric())).getSplines().size() > 1) {
                    PathSim.pathManager.deleteNode(PathSim.pathManager.curEditingPath, PathSim.pathManager.curSelectedNode);
                } else {
                    PathSim.pathManager.paths.remove(PathSim.pathManager.curEditingPath);
                    PathSim.pathManager.curEditingPath = -1;
                    PathSim.renderer2d.ui.populateWidget();
                }
            }
        }

        if (PathSim.pathManager.editingPath()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                editPoint(PathSim.pathManager.curEditingPath, PathSim.pathManager.curEditingNode, PathSim.pathManager.curEditingVel, x - prevx, y - prevy, true);
            } else {
                if(PathSim.pathManager.curEditingNode != -1) {
                    PathSim.pathManager.curSelectedNode = PathSim.pathManager.curEditingNode;
                    PathSim.pathManager.curEditingNode = -1;
                }
                PathSim.pathManager.curEditingVel = -1;
            }
        }

        if(!(x >= rwx && x <= rwx + ww && y >= rwy && y <= rwy + wh)) {
            PathSim.pathManager.curOnPath = -1;
            PathSim.pathManager.curHoveringNode = -1;
            for (int i = 0; i < PathSim.pathManager.paths.size(); i++) {
                if(PathSim.pathManager.paths.get(i).visible) {
                QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(i).purePursuitPath.getParametric();

                Point2D pos = toPointInInches(x, y);
                Point2D onscreen = toPointOnScreen(group.getPoint(group.findClosestPointOnSpline(pos, 50, 5)));
                Point2D posscreen = new Point2D(x, y);
                if (posscreen.distance(onscreen) <= 6) {
                    PathSim.pathManager.curOnPath = i;
                }

                for (int k = 0; k < group.getSplines().size(); k++) {
                    Point2D p = toPointOnScreen(group.getSpline(k).getPoint(1));
                    if (distance(x, y, p) < pointl.getWidth() / 2f) {
                        PathSim.pathManager.curOnPath = i;
                        PathSim.pathManager.curHoveringNode = k + 1;
                    }
                }

                Point2D p = toPointOnScreen(group.getSpline(0).getPoint(0));
                if (distance(x, y, p) < pointl.getWidth() / 2f) {
                    PathSim.pathManager.curOnPath = i;
                    PathSim.pathManager.curHoveringNode = 0;
                }
                }
            }

            if (justClicked()) {
                x = Gdx.input.getX();
                y = Gdx.graphics.getHeight() - Gdx.input.getY();
                int editingPath = -1, editingNode = -1, selectedNode = -1, editingVel = -1;
                Point2D temptemp = new Point2D();
                for (int i = 0; i < PathSim.pathManager.paths.size(); i++) {
                    if(PathSim.pathManager.paths.get(i).visible) {
                    QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(i).purePursuitPath.getParametric();

                    Point2D pos = toPointInInches(x, y);
                    Point2D onscreen = toPointOnScreen(group.getPoint(group.findClosestPointOnSpline(pos, 50, 5)));
                    Point2D posscreen = new Point2D(x, y);
                    if (posscreen.distance(onscreen) <= 6) {
                        editingPath = i;
                        editingNode = -2;
                        selectedNode = -1;
                    }

                    for (int k = 0; k < group.getSplines().size(); k++) {
                        Point2D p = toPointOnScreen(group.getSpline(k).getPoint(1));
                        if (distance(x, y, p) < pointl.getWidth() / 2f) {
                            editingPath = i;
                            editingNode = k + 1;
                            selectedNode = k + 1;
                            if (k == group.getSplines().size() - 1 && (i == PathSim.pathManager.curEditingPath) || (PathSim.pathManager.curOnPath == i && PathSim.pathManager.notEditing())) {
                                if (addBack) {
                                    UI.addingSpline = 4;
                                    PathSim.pathManager.curSelectedNode = -1;
                                    PathSim.pathManager.curEditingNode = -1;
                                    addBack = false;
                                } else if (!wasJustPlaced) {
                                    addBack = true;
                                    Timer timer = new Timer(500, arg0 -> {
                                        addBack = false;
                                    });
                                    timer.setRepeats(false);
                                    timer.start();
                                }
                            }
                            temptemp = group.getSpline(k).getPoint(1);
                        }
                        for (int t = 0; t <= 1; t += 1) {
                            Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                            if (distance(x, y, v) < points.getWidth() / 2f) {
                                editingPath = i;
                                editingVel = 2 * k + t;
                                selectedNode = -1;
                            }
                        }
                    }

                    Point2D p = toPointOnScreen(group.getSpline(0).getPoint(0));
                    if (distance(x, y, p) < pointl.getWidth() / 2f) {
                        editingPath = i;
                        editingNode = 0;
                        selectedNode = 0;
                        if (i == PathSim.pathManager.curEditingPath || (PathSim.pathManager.curOnPath == i && PathSim.pathManager.notEditing())) {
                            if (addFront) {
                                UI.addingSpline = 3;
                                PathSim.pathManager.curSelectedNode = -1;
                                PathSim.pathManager.curEditingNode = -1;
                                addFront = false;
                            } else if (!wasJustPlaced) {
                                addFront = true;
                                Timer timer = new Timer(500, arg0 -> {
                                    addFront = false;
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                        temptemp = group.getSpline(0).getPoint(0);
                    }
                    }
                }

                if (UI.addingSpline <= 0 && x < PathSim.LEFT_WIDTH) {
                    if (editingPath == PathSim.pathManager.curEditingPath || PathSim.pathManager.notEditing() || editingPath == -1) {
                        PathSim.pathManager.curEditingPath = editingPath;
                        PathSim.pathManager.curSelectedNode = selectedNode;
                        PathSim.pathManager.curEditingNode = editingNode;
                        PathSim.pathManager.curEditingVel = editingVel;
                        temp = temptemp;
                    } else {
                        PathSim.pathManager.curEditingPath = -1;
                    }
                }
                x = getX();
                y = getY();
            }
        }

        prevx = x;
        prevy = y;

        if(justClicked() && PathSim.pathManager.curOnPath == -1 && UI.addingSpline == 0 && x < PathSim.LEFT_WIDTH && inBounds(x, y)
                        && !(x >= rwx && x <= rwx + ww && y >= rwy && y <= rwy + wh)) {
            if(addNew) {
                PathSim.pathManager.storePoint(toPointInInches(x, y));
                UI.addingSpline = 1;
                PathSim.pathManager.curEditingPath = -1;
                PathSim.input.removeProcessor(ui.stage);
                ui.stage.addActor(ui.addingLabel);
                addNew = false;
            } else if (!wasJustPlaced) {
                addNew = true;
                Timer timer = new Timer(500, arg0 -> {
                    addNew = false;
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

        //end general click handling -----------------

        wasJustPlaced = false;
    }

    public Point2D getSnapPoint(double x, double y) {

        double increment;

        if(zoom < 0.22) {
            increment = 24 * inch;
        } else if (zoom < 0.4) {
            increment = 12 * inch;
        } else if (zoom < 0.7) {
            increment = 6 * inch;
        } else {
            increment = 1 * inch;
        }

        double lx = 0;
        double cx = Integer.MAX_VALUE;

        double ly = 0;
        double cy = Integer.MAX_VALUE;

        for(double i = centerx; i <= width; i += increment) {
            if(Math.abs(x - i) < cx) { lx = i; cx = Math.abs(x-i); }
        }

        for(double i = centerx - increment; i >= 0; i -= increment) {
            if(Math.abs(x - i) < cx) { lx = i; cx = Math.abs(x-i); }
        }

        for(double i = centery; i <= height; i += increment) {
            if(Math.abs(y - i) < cy) { ly = i; cy = Math.abs(y-i); }
        }

        for(double i = centery - increment; i >= 0; i -= increment) {
            if(Math.abs(y - i) < cy) { ly = i; cy = Math.abs(y-i); }
        }

        return new Point2D(lx, ly);
    }

    public void drawSprites() {
        fieldRenderer.begin(ShapeRenderer.ShapeType.Filled);
        double step = 0.001;
        double smallStep = 0.001;

        int j = 0;

        double x = getX();
        double y = getY();
        if(UI.addingSpline == 3 || UI.addingSpline == 4) {
            QuinticHermiteSpline potential = new QuinticHermiteSpline(new Pose2D(0, 0, 0), new Pose2D(0, 0, 0));
            if (UI.addingSpline == 3) {
                potential = PathSim.pathManager.getPotentialSpline(toPointInInches(x, y), PathSim.pathManager.curEditingPath, true);
            } else if (UI.addingSpline == 4) {
                potential = PathSim.pathManager.getPotentialSpline(toPointInInches(x, y), PathSim.pathManager.curEditingPath, false);
            }
            if(inBounds(x, y) && x < PathSim.LEFT_WIDTH) fieldRenderer.setColor(transgreen2);
            else fieldRenderer.setColor(transred);
            for(double t = smallStep; t <= 1; t += smallStep) {
                Point2D p1 = toPointOnScreen(potential.getPoint(t-smallStep));
                Point2D p2 = toPointOnScreen(potential.getPoint(t));
                fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 3);
            }
        }
        if(UI.addingSpline == 1) {
            QuinticHermiteSpline potential = PathSim.pathManager.getNewPathPreview(toPointInInches(x, y));
            if(inBounds(x, y) && x < PathSim.LEFT_WIDTH) fieldRenderer.setColor(transgreen2);
            else fieldRenderer.setColor(transred);
            for(double t = smallStep; t <= 1; t += smallStep) {
                Point2D p1 = toPointOnScreen(potential.getPoint(t-smallStep));
                Point2D p2 = toPointOnScreen(potential.getPoint(t));
                fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 3);
            }
        }

        for(ExtendedPath epath : PathSim.pathManager.paths) {
            if(epath.visible) {
                Path path = epath.purePursuitPath;
                QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) path.getParametric();
                for (double t = step; t <= 1; t += step) {
                    Point2D p1 = toPointOnScreen(group.getPoint(t - step));
                    Point2D p2 = toPointOnScreen(group.getPoint(t));
                    if (UI.addingSpline <= 0 && ((PathSim.pathManager.notEditing() && PathSim.pathManager.curOnPath == j) || PathSim.pathManager.curEditingPath == j || PathSim.pathManager.curUIOnPath == j)) {
                        if(PathSim.pathManager.curEditingPath == j) fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 3);
                        fieldRenderer.setColor(transgreen);
                        fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 8);
                        fieldRenderer.setColor(green);
                    } else {
                        fieldRenderer.setColor(transgreen);
                        fieldRenderer.rectLine((float) p1.x, (float) p1.y, (float) p2.x, (float) p2.y, 3);
                    }
                }

                if (UI.addingSpline <= 0 && PathSim.pathManager.curEditingPath == j) {
                    fieldRenderer.setColor(blue);
                    for (int k = 0; k < group.getSplines().size(); k++) {
                        for (int t = 0; t <= 1; t += 1) {
                            Point2D p = toPointOnScreen(group.getSpline(k).getPoint(t));
                            Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                            fieldRenderer.rectLine((float) p.x, (float) p.y, (float) v.x, (float) v.y, 2);
                        }
                    }
                }
            }
            j++;
        }
        fieldRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        onBatch.begin();

        if(UI.addingSpline > 0) {
            if (inBounds(x, y) && x < PathSim.LEFT_WIDTH) {
                onBatch.draw(pointl, (float) x - pointl.getWidth() / 2f, (float) y - pointl.getHeight() / 2f, pointl.getWidth(), pointl.getHeight());
            } else {
                onBatch.draw(pointw, (float) x - pointw.getWidth() / 2f, (float) y - pointw.getHeight() / 2f, pointw.getWidth(), pointw.getHeight());
            }
        }

        if(PathSim.pathManager.storedPoint != null) {
            Point2D p = toPointOnScreen(PathSim.pathManager.storedPoint);
            onBatch.draw(pointl, (float) p.x - pointl.getWidth()/2f, (float) p.y - pointl.getHeight()/2f, pointl.getWidth(), pointl.getHeight());
        }

        //draw splines

        int f = 0;
        for(ExtendedPath epath : PathSim.pathManager.paths) {
            if(epath.visible) {
            Path path = epath.purePursuitPath;
            QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) path.getParametric();
            for (int k = 0; k < group.getSplines().size(); k++) {
                for (int t = 0; t <= 1; t += 1) {
                    Point2D p = toPointOnScreen(group.getSpline(k).getPoint(t));
                    if (((t == 0 && k == PathSim.pathManager.curSelectedNode) || (t == 1 && k == PathSim.pathManager.curSelectedNode - 1)) && PathSim.pathManager.curEditingPath == f && UI.addingSpline == 0) {
                        onBatch.draw(pointp, (float) p.x - pointp.getWidth() / 2f, (float) p.y - pointp.getHeight() / 2f, pointp.getWidth(), pointp.getHeight());
                    } else if ((((t == 0 && k == PathSim.pathManager.curHoveringNode) || (t == 1 && k == PathSim.pathManager.curHoveringNode - 1)))
                            && ((PathSim.pathManager.curEditingPath == f && PathSim.pathManager.curOnPath == f) || (PathSim.pathManager.notEditing() && PathSim.pathManager.curOnPath == f)) && UI.addingSpline == 0) {
                        onBatch.draw(pointh, (float) p.x - pointh.getWidth() / 2f, (float) p.y - pointh.getHeight() / 2f, pointh.getWidth(), pointh.getHeight());
                    } else if (PathSim.pathManager.curEditingPath == f && ((t == 0 && k == PathSim.pathManager.curUIHoveringNode) || (t == 1 && k == PathSim.pathManager.curUIHoveringNode - 1)) && UI.addingSpline == 0) {
                        onBatch.draw(pointh, (float) p.x - pointh.getWidth() / 2f, (float) p.y - pointh.getHeight() / 2f, pointh.getWidth(), pointh.getHeight());
                    } else if (PathSim.pathManager.curEditingPath == f && UI.addingSpline == 0) {
                        onBatch.draw(pointl, (float) p.x - pointl.getWidth() / 2f, (float) p.y - pointl.getHeight() / 2f, pointl.getWidth(), pointl.getHeight());
                    } else {
                        onBatch.draw(pointt, (float) p.x - pointt.getWidth() / 2f, (float) p.y - pointt.getHeight() / 2f, pointt.getWidth(), pointt.getHeight());
                    }

                    if (UI.addingSpline <= 0 && PathSim.pathManager.curEditingPath == f) {
                        Point2D v = toPointOnScreen(group.getSpline(k).getDerivative(t, 1).multiply(t == 1 ? 1 / 5. : -1 / 5.).add(group.getSpline(k).getPoint(t)));
                        onBatch.draw(points, (float) (v.x - points.getWidth() / 2f), (float) (v.y - points.getHeight() / 2f), points.getWidth(), points.getHeight());
                    }
                }
            }
            }
            f++;
        }

        onBatch.end();
    }

    public void editPoint(int curPath, int curNode, int curVel, double dx, double dy, boolean first) {

        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) (PathSim.pathManager.paths.get(curPath).purePursuitPath.getParametric());
        double x = getX();
        double y = getY();

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
            temp = temp.add(new Point2D(dx / inch, dy / inch));
            if(curNode == 0) {
                double prev = group.getSpline(curNode).getLength();
                group.getSpline(curNode).setPose0(new Pose2D(temp, group.getSpline(curNode).getPose0().getAngle()));
                boundSplinePose(group.getSpline(curNode), true);
                group.updateSplineLength(curNode, prev);
            } else if (curNode == group.getSplines().size()) {
                double prev = group.getSpline(curNode - 1).getLength();
                group.getSpline(curNode-1).setPose1(new Pose2D(temp, group.getSpline(curNode-1).getPose1().getAngle()));
                boundSplinePose(group.getSpline(curNode-1), false);
                group.updateSplineLength(curNode-1, prev);
            } else {
                double prev1 = group.getSpline(curNode-1).getLength();
                double prev2 = group.getSpline(curNode).getLength();

                group.getSpline(curNode).setPose0(new Pose2D(temp, group.getSpline(curNode).getPose0().getAngle()));
                group.getSpline(curNode-1).setPose1(new Pose2D(temp, group.getSpline(curNode-1).getPose1().getAngle()));
                boundSplinePose(group.getSpline(curNode), true);
                boundSplinePose(group.getSpline(curNode-1), false);

                group.updateSplineLength(curNode-1, prev1);
                group.updateSplineLength(curNode, prev2);
            }
        } else if (curNode == -2) {
            moveSplineGroup(group, dx / inch, dy / inch);
        }

        if(first && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
            Point2D p = new Point2D();

            if(curVel != -1) {
                int splineId = curVel / 2;
                QuinticHermiteSpline spline = group.getSpline(splineId);
                if(curVel % 2 == 0) {
                    p = spline.getDerivative(0, 1).multiply(-1 / 5.).add(spline.getPoint(0));
                } else {
                    p = spline.getDerivative(1, 1).multiply(1 / 5.).add(spline.getPoint(1));
                }
            } else if (curNode >= 0) {
                if(curNode == 0) {
                    p = group.getSpline(0).getPose0().getPosition();
                } else {
                    p = group.getSpline(curNode - 1).getPose1().getPosition();
                }
            }
            p = toPointOnScreen(p);
            dx = (x - p.x);
            dy = (y - p.y);
            editPoint(curPath, curNode, curVel, dx, dy, false);
        }
    }

    public void editAngle(int curPath, int curNode, double angle) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) (PathSim.pathManager.paths.get(curPath).purePursuitPath.getParametric());

        Angle a = new Angle(angle);
        Angle c = new Angle(angle + Math.PI);
        if(curNode == 0) {
            double prev = group.getSpline(curNode).getLength();
            double l = (new Point2D(group.getSpline(curNode).getVelocity0())).magnitude();
            group.getSpline(curNode).setPose0(new Pose2D(group.getSpline(curNode).getPose0().getPosition(), a));
            group.getSpline(curNode).setVelocity0(new Vector2D(l * a.cos(), l * a.sin()));
            boundSplinePose(group.getSpline(curNode), true);
            group.updateSplineLength(curNode, prev);
        } else if (curNode == group.getSplines().size()) {
            double prev = group.getSpline(curNode - 1).getLength();
            double l = (new Point2D(group.getSpline(curNode - 1).getVelocity1())).magnitude();
            group.getSpline(curNode - 1).setPose1(new Pose2D(group.getSpline(curNode - 1).getPose1().getPosition(), a));
            group.getSpline(curNode - 1).setVelocity1(new Vector2D(l * a.cos(), l * a.sin()));
            boundSplinePose(group.getSpline(curNode - 1), false);
            group.updateSplineLength(curNode - 1, prev);
        } else {
            double prev1 = group.getSpline(curNode-1).getLength();
            double prev2 = group.getSpline(curNode).getLength();

            double l = (new Point2D(group.getSpline(curNode).getVelocity0())).magnitude();
            group.getSpline(curNode).setPose0(new Pose2D(group.getSpline(curNode).getPose0().getPosition(), a));
            group.getSpline(curNode).setVelocity0(new Vector2D(l * a.cos(), l * a.sin()));
            boundSplinePose(group.getSpline(curNode), true);

            l = (new Point2D(group.getSpline(curNode - 1).getVelocity1())).magnitude();
            group.getSpline(curNode - 1).setPose1(new Pose2D(group.getSpline(curNode - 1).getPose1().getPosition(), a));
            group.getSpline(curNode - 1).setVelocity1(new Vector2D(l * a.cos(), l * a.sin()));
            boundSplinePose(group.getSpline(curNode - 1), false);

            group.updateSplineLength(curNode-1, prev1);
            group.updateSplineLength(curNode, prev2);
        }
    }

    public void editPoint(int curPath, int curNode, double x, double y) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) (PathSim.pathManager.paths.get(curPath).purePursuitPath.getParametric());

        Point2D tmp = new Point2D(x, y);
        if(curNode == 0) {
            double prev = group.getSpline(curNode).getLength();
            group.getSpline(curNode).setPose0(new Pose2D(tmp, group.getSpline(curNode).getPose0().getAngle()));
            boundSplinePose(group.getSpline(curNode), true);
            group.updateSplineLength(curNode, prev);
        } else if (curNode == group.getSplines().size()) {
            double prev = group.getSpline(curNode - 1).getLength();
            group.getSpline(curNode-1).setPose1(new Pose2D(tmp, group.getSpline(curNode-1).getPose1().getAngle()));
            boundSplinePose(group.getSpline(curNode-1), false);
            group.updateSplineLength(curNode-1, prev);
        } else {
            double prev1 = group.getSpline(curNode-1).getLength();
            double prev2 = group.getSpline(curNode).getLength();

            group.getSpline(curNode).setPose0(new Pose2D(tmp, group.getSpline(curNode).getPose0().getAngle()));
            group.getSpline(curNode-1).setPose1(new Pose2D(tmp, group.getSpline(curNode-1).getPose1().getAngle()));
            boundSplinePose(group.getSpline(curNode), true);
            boundSplinePose(group.getSpline(curNode-1), false);

            group.updateSplineLength(curNode-1, prev1);
            group.updateSplineLength(curNode, prev2);
        }
    }

    public void boundSplinePose(QuinticHermiteSpline spline, boolean p0) {
        Point2D l = toPointInInches(0, 0);
        Point2D r = toPointInInches(PathSim.LEFT_WIDTH, Gdx.graphics.getHeight());
        if(p0) {
            Pose2D p = spline.getPose0();
            spline.setPose0(new Pose2D(Math.max(Math.min(p.getPosition().getX(), Math.min(r.getX(), 324)), Math.max(l.getX(), -324)),
                    Math.max(Math.min(p.getPosition().getY(), Math.min(r.getY(), 162)), Math.max(l.getY(), -162)), p.getAngle().getRadians()));
        } else {
            Pose2D p = spline.getPose1();
            spline.setPose1(new Pose2D(Math.max(Math.min(p.getPosition().getX(), Math.min(r.getX(), 324)), Math.max(l.getX(), -324)),
                    Math.max(Math.min(p.getPosition().getY(), Math.min(r.getY(), 162)), Math.max(l.getY(), -162)), p.getAngle().getRadians()));
        }
    }

    public boolean inBounds(double x, double y) {
        Point2D d = toPointInInches(x, y);
        return Math.abs(d.getX()) <= 324 && Math.abs(d.getY()) <= 162;
    }

    public void moveSplineGroup(QuinticHermiteSplineGroup group, double dx, double dy) {
        boolean good = true;

        for(QuinticHermiteSpline spline : group.getSplines()) {
            Point2D screen = toPointOnScreen(spline.getPose0().getPosition().add(new Point2D(dx, dy)));
            Point2D screen2 = toPointOnScreen(spline.getPose1().getPosition().add(new Point2D(dx, dy)));

            if(!inBounds((int) screen.x, (int) screen.y) || !inBounds((int) screen2.x, (int) screen2.y)) {
                good = false;
                PathSim.pathManager.curEditingNode = -1;
            }
        }

        if(good) {
            for (QuinticHermiteSpline spline : group.getSplines()) {
                spline.setPose0(new Pose2D(spline.getPose0().getPosition().add(new Point2D(dx, dy)), spline.getPose0().getAngle()));
                spline.setPose1(new Pose2D(spline.getPose1().getPosition().add(new Point2D(dx, dy)), spline.getPose1().getAngle()));
            }
            group.updateSplineLength();
        }
    }

    public double distance(double mx, double my, Point2D p) {
        return Math.sqrt((mx - p.x) * (mx - p.x) + (my - p.y) * (my - p.y));
    }

    public void drawText() {
        uiRenderer.end();
        batch.begin();
        batch.draw(title1, right + 50, Gdx.graphics.getHeight() - 135, 200, (int) (200. * title1.getHeight() / title1.getWidth()));
        batch.draw(title2, right + 40, Gdx.graphics.getHeight() - 195, 220, (int) (220. * title2.getHeight() / title2.getWidth()));
        batch.end();
        if(UI.addingSpline > 0) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
            uiRenderer.setColor(0.12f, 0.12f, 0.12f, 0.8f);
            uiRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);
            uiRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
            uiRenderer.end();
        }
    }

    public void drawUIOverlay() {
        double x = getX();
        double y = getY();

        uiRenderer.begin(ShapeRenderer.ShapeType.Filled);
        uiRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        uiRenderer.rect(width, 0, PathSim.RIGHT_WIDTH, height);
        uiRenderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        uiRenderer.rect(width + 25, 20, PathSim.RIGHT_WIDTH - 50, Gdx.graphics.getHeight() - 298);

        uiRenderer.setColor(0.20f, 0.20f, 0.20f, 1f);
        if(PathSim.pathManager.editingPath()) {
            if(ui.splineMode) {
                uiRenderer.rect(right+25, Gdx.graphics.getHeight() - 353, 125, 35);
            } else {
                uiRenderer.rect(right+150, Gdx.graphics.getHeight() - 353, 125, 35);
            }
            uiRenderer.rect(width+25, 20, PathSim.RIGHT_WIDTH - 50, Gdx.graphics.getHeight() - 372);
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if(UI.addingSpline > 0) {
            uiRenderer.setColor(0.12f, 0.12f, 0.12f, 0.7f);
            uiRenderer.rect((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - ui.addingLabel.getPrefWidth() / 2f - 10, 50, ui.addingLabel.getPrefWidth() + 20, 30);
        }

        if(PathSim.pathManager.editingPath() && ui.showing && !ui.splineMode) {
            uiRenderer.setColor(0.20f, 0.20f, 0.20f, 0.4f);
            roundedRect(uiRenderer, right * 0.1f, 35f, right * 0.8f, 50, 15f);
            uiRenderer.setColor(0.6f, 0.6f, 0.6f, 1f);
            float barleft = right * 0.1f + 10;
            float barwidth = right * 0.8f - 20;
            roundedRect(uiRenderer, barleft, 45f, barwidth, 5f, 2.5f);
            uiRenderer.setColor(0.9f, 0.9f, 0.9f, 1f);
            float circlePos = barleft + barwidth * ((float) PathSim.renderer3d.curInd / ui.simulator.getEnd(ui.purePursuitMode));
            boolean change = false;

            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if(distance(x, y, new Point2D(circlePos, 47.5)) <= 7.5) {
                    scrubbing = true;
                } else if(x >= barleft && x <= barleft + barwidth && y >= 40 && y <= 55) {
                        circlePos = (float) x;
                        change = true;
                }
            } else if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                scrubbing = false;
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&
                    x >= right / 2f - 10 && x <= right / 2f + 10 && y >= 58 && y <= 78)) {
                if(PathSim.renderer3d.running) {
                    PathSim.renderer3d.running = false;
                } else {
                    if (PathSim.renderer3d.curInd == ui.simulator.getEnd(ui.purePursuitMode) - 1) {
                        PathSim.renderer3d.curInd = 0;
                    }
                    PathSim.renderer3d.running = true;
                }
            }

            if(scrubbing) {
                circlePos = (float) Math.max(barleft, Math.min(x, barleft + barwidth));
                change = true;
            }

            if(change) {
                PathSim.renderer3d.curInd = (int) (((circlePos - barleft) / barwidth) * ui.simulator.getEnd(ui.purePursuitMode));
                PathSim.renderer3d.curInd = Math.min(PathSim.renderer3d.curInd, ui.simulator.getEnd(ui.purePursuitMode) - 1);
                circlePos = barleft + barwidth * ((float) PathSim.renderer3d.curInd / ui.simulator.getEnd(ui.purePursuitMode));
            }

            uiRenderer.circle(circlePos, 47.5f, 7.5f);
            if(PathSim.renderer3d.running) {
                uiRenderer.rect(right / 2f - 10, 58f, 6, 20);
                uiRenderer.rect(right / 2f + 4, 58f, 6, 20);
            } else {
                uiRenderer.triangle(right / 2f - 10, 58f, right / 2f - 10, 78f, right / 2f + 10, 68f);
            }
        }
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

    public double getX() {
        double x = Gdx.input.getX();

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            x = getSnapPoint(x, 0).getX();
        }
        return x;
    }

    public double getY() {
        double y = Gdx.graphics.getHeight() - Gdx.input.getY();

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            y = getSnapPoint(0, y).getY();
        }
        return y;
    }

    //conversions
    public Point2D toPointInInches(double screenx, double screeny) {
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

        double centerynew = Math.max(Math.min(centery, height), 24);
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

    public void roundedRect(ShapeRenderer renderer, float x, float y, float width, float height, float radius){
        // Central rectangle
        renderer.rect(x + radius, y + radius, width - 2*radius, height - 2*radius);

        // Four side rectangles, in clockwise order
        renderer.rect(x + radius, y, width - 2*radius, radius);
        renderer.rect(x + width - radius, y + radius, radius, height - 2*radius);
        renderer.rect(x + radius, y + height - radius, width - 2*radius, radius);
        renderer.rect(x, y + radius, radius, height - 2*radius);

        // Four arches, clockwise too
        renderer.arc(x + radius, y + radius, radius, 180f, 90f);
        renderer.arc(x + width - radius, y + radius, radius, 270f, 90f);
        renderer.arc(x + width - radius, y + height - radius, radius, 0f, 90f);
        renderer.arc(x + radius, y + height - radius, radius, 90f, 90f);
    }

    public boolean justClicked() {
        return !importShown && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
    }

    public void importScreen() {
        importShown = true;
        movingWidget = false;
        UI.addingSpline = 0;
        PathSim.pathManager.removeStoredPoint();
        PathSim.pathManager.curEditingPath = -1;
        PathSim.pathManager.curSelectedNode = -1;
        PathSim.pathManager.curEditingNode = -1;
        PathSim.input.removeProcessor(ui.stage);
        PathSim.input.removeProcessor(camController);
        PathSim.input.addProcessor(imports);
        imports.setKeyboardFocus(a);
    }

    public void closeImportScreen() {
        importShown = false;
        PathSim.input.addProcessor(ui.stage);
        PathSim.input.addProcessor(camController);
        PathSim.input.removeProcessor(imports);
    }

    public void dispose () {
        batch.dispose();
    }

}
