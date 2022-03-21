package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UI implements Disposable {

    public Stage stage;
    public Table container, table, pcontainer, ptable;
    public ScrollPane pane, ppane;
    public int right, prevState, prevEditing;
    public static int addingSpline;
    public Label addingLabel, widget;
    public boolean splineMode, prevMode, purePursuitMode, showing;
    public TextButton pathId, addNode1, addNode2, spline, path, addPath, play, export, delete, deleteNode, purePursuit, ramsete;
    public ArrayList<TextField> splines = new ArrayList<>();
    ArrayList<TextField> paths = new ArrayList<>();
    public TextField.TextFieldStyle textFieldStyle;
    public Label.LabelStyle lStyle2;
    public ArrayList<Actor> toggle = new ArrayList<>();
    public ArrayList<Actor> splineEdit = new ArrayList<>();
    public ArrayList<Actor> pathEdit = new ArrayList<>();
    public ArrayList<Actor> notPathEdit = new ArrayList<>();
    public DecimalFormat df, df2;

    public String[] purePursuitLabels = {"Lookahead", "End Threshold", "Max Acceleration", "Max Deceleration", "Max Velocity", "Max Angular Vel.", "Start Velocity", "End Velocity", "Adjust Threshold", "Newton's Steps"};
    public String[] ramseteLabels = {"b (Convergence)", "Z (Dampening)", "End Threshold", "Max Acceleration", "Max Deceleration", "Max Velocity", "Max Angular Vel.", "Start Velocity", "End Velocity", "Adjust Threshold", "Newton's Steps"};

    public Simulator simulator = new Simulator();

    public UI() {
        stage = new Stage();
        purePursuitMode = true;

        right = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;

        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        df2 = new DecimalFormat();
        df2.setMaximumFractionDigits(3);
        df2.setMinimumFractionDigits(3);

        container = new Table();
        table = new Table();
        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        Label.LabelStyle lStyle = new Label.LabelStyle();
        lStyle.font = PathSim.font;
        lStyle.font.getData().setScale(0.5f);
        lStyle.fontColor = new Color(104/255f,204/255f,220/255f, 1f);

        lStyle2 = new Label.LabelStyle();
        lStyle2.font = PathSim.font;
        lStyle2.font.getData().setScale(0.6f);
        lStyle2.fontColor = Color.WHITE;

        Label.LabelStyle lStyle3 = new Label.LabelStyle();
        lStyle3.font = PathSim.font;
        lStyle3.font.getData().setScale(0.5f);
        lStyle3.fontColor = Color.WHITE;
//        scrollPaneStyle.vScrollKnob = PathSim.skin.getDrawable("scroll_vertical_knob");
//        scrollPaneStyle.background = PathSim.skin.getDrawable("btn_default_normal");

        pane = new ScrollPane(table, scrollPaneStyle);
        table.align(Align.top);
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
        pane.setFlickScroll(false);
        pane.layout();
        container.add(pane).fill().expand();
        container.row();
        container.setBounds(right+25, 82, 250, Gdx.graphics.getHeight() - 518);
        splineEdit.add(container);

        pcontainer = new Table();
        ptable = new Table();

        ppane = new ScrollPane(ptable, scrollPaneStyle);
        ptable.align(Align.top);
        ppane.setScrollingDisabled(true, false);
        ppane.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                stage.setScrollFocus(ppane);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                stage.setScrollFocus(null);
            }
        });
        ppane.setFlickScroll(false);
        ppane.layout();
        pcontainer.add(ppane).fill().expand();
        pcontainer.row();
        pcontainer.setBounds(right+25, 82, 250, Gdx.graphics.getHeight() - 478);
        pathEdit.add(pcontainer);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_focused");

        TextButton.TextButtonStyle textButtonStyleNoBg = new TextButton.TextButtonStyle();
        textButtonStyleNoBg.font = PathSim.font;

        addPath = new TextButton("Add Path", textButtonStyle);
        addPath.getLabel().setFontScale(0.7f);
        addPath.setBounds(right+50, Gdx.graphics.getHeight() - 265, 200, 50);

        play = new TextButton("Pause Sim", textButtonStyle);
        play.getLabel().setFontScale(0.7f);
        play.setBounds(right+50, Gdx.graphics.getHeight() - 265, 200, 50);

        addNode1 = new TextButton("Add Start", textButtonStyle);
        addNode1.getLabel().setFontScale(0.5f);
        addNode1.setBounds(right+25, Gdx.graphics.getHeight() - 395, 130, 40);

        addNode2 = new TextButton("Add End", textButtonStyle);
        addNode2.getLabel().setFontScale(0.5f);
        addNode2.setBounds(right+145, Gdx.graphics.getHeight() - 395, 130, 40);

        pathId = new TextButton("", textButtonStyleNoBg);
        pathId.getLabel().setFontScale(0.7f);
        pathId.setBounds(right+50, Gdx.graphics.getHeight() - 325, 200, 50);

        spline = new TextButton("Edit Spline", textButtonStyleNoBg);
        spline.getLabel().setFontScale(0.6f);
        spline.setBounds(right+25, Gdx.graphics.getHeight() - 357, 125, 45);

        path = new TextButton("Edit Path", textButtonStyleNoBg);
        path.getLabel().setFontScale(0.6f);
        path.setBounds(right+150, Gdx.graphics.getHeight() - 357, 125, 45);

        export = new TextButton("Export Path", textButtonStyle);
        export.getLabel().setFontScale(0.6f);
        export.setBounds(right+50, 30, 200, 45);

        delete = new TextButton("Delete Spline", textButtonStyle);
        delete.getLabel().setFontScale(0.5f);
        delete.setBounds(right + 85, Gdx.graphics.getHeight() - 428, 130, 40);

        deleteNode = new TextButton("Delete Node", textButtonStyle);
        deleteNode.getLabel().setFontScale(0.5f);
        deleteNode.setBounds(right + 145, Gdx.graphics.getHeight() - 428, 130, 40);

        purePursuit = new TextButton("Pure Pursuit", textButtonStyle);
        purePursuit.getLabel().setFontScale(0.5f);
        purePursuit.setBounds(right+25, Gdx.graphics.getHeight() - 395, 130, 40);

        ramsete = new TextButton("Ramsete", textButtonStyle);
        ramsete.getLabel().setFontScale(0.5f);
        ramsete.setBounds(right+145, Gdx.graphics.getHeight() - 395, 130, 40);

        widget = new Label("Manage Paths", lStyle3);

        addListeners();

        toggle.add(path);
        toggle.add(spline);
        toggle.add(export);

        splineEdit.add(addNode1);
        splineEdit.add(addNode2);
        splineEdit.add(delete);

        pathEdit.add(purePursuit);
        pathEdit.add(ramsete);
        pathEdit.add(play);

        stage.addActor(pathId);
        notPathEdit.add(addPath);
        notPathEdit.add(widget);


        for(Actor a : notPathEdit) stage.addActor(a);

        textFieldStyle = new TextField.TextFieldStyle(PathSim.font, Color.WHITE,
                PathSim.skin.getDrawable("textfield_cursor"), PathSim.skin.getDrawable("textfield_selection"), PathSim.skin.getDrawable("textfield_default"));
        textFieldStyle.font.getData().setScale(0.55f);

        stage.getRoot().addCaptureListener(new InputListener() {
           public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
               if (!(event.getTarget() instanceof TextField)) stage.setKeyboardFocus(null);
                   return false;
               }
        });

        addingLabel = new Label("", lStyle);

        prevEditing = -1;
    }

    public void update(float delta) {
        if(addingSpline == 2 && prevState != 2) {
            addingLabel.setText("Place first point.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 50, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 1 && prevState != 1) {
            addingLabel.setText("Place second point.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 50, addingLabel.getPrefWidth(), 30);
        } else if (addingSpline == 3 && prevState != 3) {
            addingLabel.setText("Place new starting node.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 50, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 4 && prevState != 4) {
            addingLabel.setText("Place new ending node.");
            addingLabel.setBounds((Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH) / 2f - addingLabel.getPrefWidth() / 2f, 50, addingLabel.getPrefWidth(), 30);
            stage.addActor(addingLabel);
            PathSim.input.removeProcessor(stage);
        } else if (addingSpline == 0 && prevState != 0) {
            addingLabel.remove();
            PathSim.input.addProcessor(stage);
        }

        prevState = addingSpline;

        pathId.setText(PathSim.pathManager.notEditing() ? "No Path Selected" : "Path " + (PathSim.pathManager.curEditingPath+1));
        if(PathSim.pathManager.notEditing() && prevEditing != -1) {
            for(Actor a : toggle) a.remove();
            if(splineMode) for(Actor a : splineEdit) a.remove();
            else for(Actor a : pathEdit) a.remove();
            splineMode = false;
            showing = false;
        } else if (PathSim.pathManager.editingPath() && prevEditing == -1) {
            for(Actor a : toggle) stage.addActor(a);
            splineMode = true;
            populateSplineEdit();
        } else if (PathSim.pathManager.curEditingPath != prevEditing) {
            splineMode = true;
            populateSplineEdit();
        }

        if(PathSim.pathManager.editingPath() && splineMode) {
            updateSplineEdit();
            if(PathSim.pathManager.curSelectedNode < 0 || ((QuinticHermiteSplineGroup) (PathSim.pathManager.getCurPath().getParametric())).getSplines().size() <= 1) {
                delete.setBounds(right + 85, Gdx.graphics.getHeight() - 428, 130, 40);
                deleteNode.remove();
            } else if (((QuinticHermiteSplineGroup) (PathSim.pathManager.getCurPath().getParametric())).getSplines().size() > 1) {
                delete.setBounds(right + 25, Gdx.graphics.getHeight() - 428, 130, 40);
                stage.addActor(deleteNode);
            }
        } else {
            deleteNode.remove();
        }

        if(splineMode && !prevMode && PathSim.pathManager.editingPath()) {
            for(Actor a : splineEdit) stage.addActor(a);
            for(Actor a : pathEdit) a.remove();
            showing = true;

            for(Actor a : notPathEdit) stage.addActor(a);
        } else if (!splineMode && prevMode && PathSim.pathManager.editingPath()) {
            for(Actor a : pathEdit) stage.addActor(a);
            for(Actor a : notPathEdit) a.remove();
            path.setText("Pause Sim");
            for(Actor a : splineEdit) a.remove();
            showing = true;
            populatePathEdit();
        }

        widget.setBounds(PathSim.renderer2d.widgetX + 20, PathSim.renderer2d.widgetY + PathSim.renderer2d.wh - 15 - widget.getPrefHeight()/2, widget.getPrefWidth(), widget.getPrefHeight());

        prevEditing = PathSim.pathManager.curEditingPath;
        prevMode = splineMode;

        stage.act(delta);
        stage.draw();

        updateExportFrame(false);
    }

    public void populateSplineEdit() {
        export.setText("Export Spline");

        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.getCurPath().getParametric();
        ArrayList<QuinticHermiteSpline> sp = group.getSplines();

        splines.clear();

        ClickListener sel = new ClickListener() {
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(button == 0) { PathSim.pathManager.curSelectedNode = 0; }
                return super.touchDown(event, x, y, pointer, button);
            }
        };

        TextField temp = new TextField(df.format(sp.get(0).getPose0().getPosition().getX()), textFieldStyle);
        temp.addListener(new InputListener() {
            final QuinticHermiteSpline s = sp.get(0); @Override
            public boolean keyTyped (InputEvent event, char character) {
                if(event.getKeyCode() == Input.Keys.ENTER) {
                    if(checkPosition(temp.getText(), false)) s.setPose0(new Pose2D(new Point2D(Double.parseDouble(temp.getText()), s.getPose0().getPosition().getY()), s.getPose0().getAngle()));
                    temp.setText(df.format(s.getPose0().getPosition().getX()));
                    updateExportFrame(true);
                    stage.unfocus(temp);
                } return super.keyTyped(event, character);
            }
        });
        temp.addListener(sel);
        splines.add(temp);

        TextField temp2 = new TextField(df.format(sp.get(0).getPose0().getPosition().getY()), textFieldStyle);
        temp2.addListener(new InputListener() {
            final QuinticHermiteSpline s = sp.get(0); @Override
            public boolean keyTyped (InputEvent event, char character) {
                if(event.getKeyCode() == Input.Keys.ENTER) {
                    if(checkPosition(temp2.getText(), true)) s.setPose0(new Pose2D(new Point2D(s.getPose0().getPosition().getX(), Double.parseDouble(temp2.getText())), s.getPose0().getAngle()));
                    temp2.setText(df.format(s.getPose0().getPosition().getY()));
                    updateExportFrame(true);
                    stage.unfocus(temp2);
                } return super.keyTyped(event, character);
            }
        });
        temp2.addListener(sel);
        splines.add(temp2);

        int tt = 0;
        for(QuinticHermiteSpline s_ : sp) {
            tt++;
            final int ttf = tt;
            ClickListener sel2 = new ClickListener() {
                @Override
                public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                    if(button == 0) { PathSim.pathManager.curSelectedNode = ttf; }
                    return super.touchDown(event, x, y, pointer, button);
                }
            };
            TextField temp3 = new TextField(df.format(s_.getPose1().getPosition().getX()), textFieldStyle);
            temp3.addListener(new InputListener() {
                final QuinticHermiteSpline s = s_; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosition(temp3.getText(), false)) s.setPose1(new Pose2D(new Point2D(Double.parseDouble(temp3.getText()), s.getPose1().getPosition().getY()), s.getPose1().getAngle()));
                        temp3.setText(df.format(s.getPose1().getPosition().getX()));
                        updateExportFrame(true);
                        stage.unfocus(temp3);
                    } return super.keyTyped(event, character);
                }
            });
            temp3.addListener(sel2);
            splines.add(temp3);

            TextField temp4 = new TextField(df.format(s_.getPose1().getPosition().getY()), textFieldStyle);
            temp4.addListener(new InputListener() {
                final QuinticHermiteSpline s = s_; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosition(temp4.getText(), true)) s.setPose1(new Pose2D(new Point2D(s.getPose1().getPosition().getX(), Double.parseDouble(temp4.getText())), s.getPose1().getAngle()));
                        temp4.setText(df.format(s.getPose1().getPosition().getY()));
                        updateExportFrame(true);
                        stage.unfocus(temp4);
                    } return super.keyTyped(event, character);
                }
            });
            temp4.addListener(sel2);
            splines.add(temp4);
        }

        table.clear();
        for(int i = 0; i < splines.size()/2; i++) {
            int finalI = i;
            ClickListener cl = new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    PathSim.pathManager.curUIHoveringNode = finalI;
                }
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    PathSim.pathManager.curUIHoveringNode = -1;
                }
            };
            Label tl = new Label("  P" + i, lStyle2);
            tl.addListener(cl);
            splines.get(2*i).addListener(cl);
            splines.get(2*i+1).addListener(cl);
            table.add(tl).width(40).height(35);
            table.add(splines.get(2*i)).width(105).height(35);
            table.add(splines.get(2*i+1)).width(105).height(35);
            table.row();
        }
    }

    public void populatePathEdit() {
        export.setText("Export Path");

        simulator.updatePath(PathSim.pathManager.getCurEPath());

        ExtendedPath path = PathSim.pathManager.getCurEPath();
        path.update();
        ptable.clear();
        if(purePursuitMode) {
            paths.clear();

            TextField temp1 = new TextField(df.format(path.lookahead), textFieldStyle);
            temp1.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp1.getText())) epath.lookahead = Double.parseDouble(temp1.getText());
                        temp1.setText(df.format(epath.lookahead));
                        stage.unfocus(temp1);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp1);

            TextField temp2 = new TextField(df.format(path.end_threshold), textFieldStyle);
            temp2.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp2.getText())) epath.end_threshold = Double.parseDouble(temp2.getText());
                        temp2.setText(df.format(epath.end_threshold));
                        stage.unfocus(temp2);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp2);

            TextField temp3 = new TextField(df.format(path.purePursuitPath.getMaxAcceleration()), textFieldStyle);
            temp3.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp3.getText())) epath.setMaxAcceleration(Double.parseDouble(temp3.getText()), true);
                        temp3.setText(df.format(epath.purePursuitPath.getMaxAcceleration()));
                        stage.unfocus(temp3);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp3);

            TextField temp4 = new TextField(df.format(path.purePursuitPath.getMaxDeceleration()), textFieldStyle);
            temp4.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp4.getText())) epath.setMaxDeceleration(Double.parseDouble(temp4.getText()), true);
                        temp4.setText(df.format(epath.purePursuitPath.getMaxDeceleration()));
                        stage.unfocus(temp4);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp4);

            TextField temp5 = new TextField(df.format(path.purePursuitPath.getMaxVelocity()), textFieldStyle);
            temp5.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp5.getText())) epath.setMaxVelocity(Double.parseDouble(temp5.getText()), true);
                        temp5.setText(df.format(epath.purePursuitPath.getMaxVelocity()));
                        stage.unfocus(temp5);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp5);

            TextField temp6 = new TextField(df.format(path.purePursuitPath.getMaxAngularVelocity()), textFieldStyle);
            temp6.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp6.getText())) epath.setMaxAngularVelocity(Double.parseDouble(temp6.getText()), true);
                        temp6.setText(df.format(epath.purePursuitPath.getMaxAngularVelocity()));
                        stage.unfocus(temp6);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp6);

            TextField temp7 = new TextField(df.format(path.purePursuitPath.getStartVelocity()), textFieldStyle);
            temp7.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp7.getText())) epath.setStartVelocity(Double.parseDouble(temp7.getText()), true);
                        temp7.setText(df.format(epath.purePursuitPath.getStartVelocity()));
                        stage.unfocus(temp7);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp7);

            TextField temp8 = new TextField(df.format(path.purePursuitPath.getEndVelocity()), textFieldStyle);
            temp8.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp8.getText())) epath.setEndVelocity(Double.parseDouble(temp8.getText()), true);
                        temp8.setText(df.format(epath.purePursuitPath.getEndVelocity()));
                        stage.unfocus(temp8);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp8);

            TextField temp9 = new TextField(df.format(path.adjust_threshold), textFieldStyle);
            temp9.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp3.getText())) epath.adjust_threshold = Double.parseDouble(temp9.getText());
                        temp9.setText(df.format(epath.adjust_threshold));
                        stage.unfocus(temp9);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp9);

            TextField temp10 = new TextField(df.format(path.newtonsSteps), textFieldStyle);
            temp10.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosInt(temp10.getText())) epath.newtonsSteps = Integer.parseInt(temp10.getText());
                        temp10.setText(df.format(epath.newtonsSteps));
                        stage.unfocus(temp10);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp10);

            for(int i = 0; i < paths.size(); ++i) {
                Label l = new Label("  " + purePursuitLabels[i], lStyle2);
                l.setFontScale(0.45f);
                ptable.add(l).width(200).height(15).align(Align.center);
                ptable.row();
                ptable.add(paths.get(i)).width(200).height(35).align(Align.center);
                ptable.row();
            }
        } else {
            paths.clear();

            TextField temp0 = new TextField(df.format(path.b), textFieldStyle);
            temp0.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp0.getText())) epath.b = Double.parseDouble(temp0.getText());
                        temp0.setText(df.format(epath.b));
                        stage.unfocus(temp0);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp0);

            TextField temp1 = new TextField(df.format(path.Z), textFieldStyle);
            temp1.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkZ(temp1.getText())) epath.Z = Double.parseDouble(temp1.getText());
                        temp1.setText(df.format(epath.Z));
                        stage.unfocus(temp1);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp1);

            TextField temp2 = new TextField(df.format(path.r_end_threshold), textFieldStyle);
            temp2.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp2.getText())) epath.r_end_threshold = Double.parseDouble(temp2.getText());
                        temp2.setText(df.format(epath.r_end_threshold));
                        stage.unfocus(temp2);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp2);

            TextField temp3 = new TextField(df.format(path.ramsetePath.getMaxAcceleration()), textFieldStyle);
            temp3.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp3.getText())) epath.setMaxAcceleration(Double.parseDouble(temp3.getText()), false);
                        temp3.setText(df.format(epath.ramsetePath.getMaxAcceleration()));
                        stage.unfocus(temp3);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp3);

            TextField temp4 = new TextField(df.format(path.ramsetePath.getMaxDeceleration()), textFieldStyle);
            temp4.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp4.getText())) epath.setMaxDeceleration(Double.parseDouble(temp4.getText()), false);
                        temp4.setText(df.format(epath.ramsetePath.getMaxDeceleration()));
                        stage.unfocus(temp4);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp4);

            TextField temp5 = new TextField(df.format(path.ramsetePath.getMaxVelocity()), textFieldStyle);
            temp5.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp5.getText())) epath.setMaxVelocity(Double.parseDouble(temp5.getText()), false);
                        temp5.setText(df.format(epath.ramsetePath.getMaxVelocity()));
                        stage.unfocus(temp5);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp5);

            TextField temp6 = new TextField(df.format(path.ramsetePath.getMaxAngularVelocity()), textFieldStyle);
            temp6.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp6.getText())) epath.setMaxAngularVelocity(Double.parseDouble(temp6.getText()), false);
                        temp6.setText(df.format(epath.ramsetePath.getMaxAngularVelocity()));
                        stage.unfocus(temp6);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp6);

            TextField temp7 = new TextField(df.format(path.ramsetePath.getStartVelocity()), textFieldStyle);
            temp7.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp7.getText())) epath.setStartVelocity(Double.parseDouble(temp7.getText()), false);
                        temp7.setText(df.format(epath.ramsetePath.getStartVelocity()));
                        stage.unfocus(temp7);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp7);

            TextField temp8 = new TextField(df.format(path.ramsetePath.getEndVelocity()), textFieldStyle);
            temp8.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkNonNeg(temp8.getText())) epath.setEndVelocity(Double.parseDouble(temp8.getText()), false);
                        temp8.setText(df.format(epath.ramsetePath.getEndVelocity()));
                        stage.unfocus(temp8);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp8);

            TextField temp9 = new TextField(df.format(path.r_adjust_threshold), textFieldStyle);
            temp9.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPositive(temp3.getText())) epath.r_adjust_threshold = Double.parseDouble(temp9.getText());
                        temp9.setText(df.format(epath.r_adjust_threshold));
                        stage.unfocus(temp9);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp9);

            TextField temp10 = new TextField(df.format(path.r_newtonsSteps), textFieldStyle);
            temp10.addListener(new InputListener() {
                final ExtendedPath epath = path; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosInt(temp10.getText())) epath.r_newtonsSteps = Integer.parseInt(temp10.getText());
                        temp10.setText(df.format(epath.r_newtonsSteps));
                        stage.unfocus(temp10);
                        simulator.updatePath(PathSim.pathManager.getCurEPath());
                    } return super.keyTyped(event, character);
                }
            });
            paths.add(temp10);

            for(int i = 0; i < paths.size(); ++i) {
                Label l = new Label("  " + ramseteLabels[i], lStyle2);
                l.setFontScale(0.45f);
                ptable.add(l).width(200).height(15).align(Align.center);
                ptable.row();
                ptable.add(paths.get(i)).width(200).height(35).align(Align.center);
                ptable.row();
            }

        }
    }

    public boolean checkPosition(String s, boolean y) {
        try {
            double p = Double.parseDouble(s);
            return (y && Math.abs(p) <= 162) || (!y && Math.abs(p) <= 324);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkPositive(String s) {
        try {
            double p = Double.parseDouble(s);
            return p > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkNonNeg(String s) {
        try {
            double p = Double.parseDouble(s);
            return p >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkPosInt(String s) {
        try {
            int p = Integer.parseInt(s);
            return p > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkZ(String s) {
        try {
            double p = Double.parseDouble(s);
            return p > 0 && p < 1;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateSplineEdit() {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.getCurPath().getParametric();
        ArrayList<QuinticHermiteSpline> sp = group.getSplines();

        if(!splines.get(0).hasKeyboardFocus()) splines.get(0).setText(df.format(sp.get(0).getPose0().getPosition().getX()));
        if(!splines.get(1).hasKeyboardFocus()) splines.get(1).setText(df.format(sp.get(0).getPose0().getPosition().getY()));

        int i = 2;
        for(QuinticHermiteSpline s : sp) {
            if(!splines.get(i).hasKeyboardFocus()) splines.get(i).setText(df.format(s.getPose1().getPosition().getX()));
            if(!splines.get(i+1).hasKeyboardFocus()) splines.get(i+1).setText(df.format(s.getPose1().getPosition().getY()));
            i += 2;
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

        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(PathSim.renderer3d.running) {
                    PathSim.renderer3d.running = false;
                    play.setText("Resume Sim");
                } else {
                    if (PathSim.renderer3d.curInd == simulator.getEnd(purePursuitMode) - 1) {
                        PathSim.renderer3d.curInd = 0;
                    }
                    PathSim.renderer3d.running = true;
                    play.setText("Pause Sim");
                }
            }
        });

        addNode1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0 && PathSim.pathManager.editingPath()) {
                    addingSpline = 3;
                    PathSim.pathManager.curSelectedNode = -1;
                }
            }
        });

        addNode2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(addingSpline == 0 && PathSim.pathManager.editingPath()) {
                    addingSpline = 4;
                    PathSim.pathManager.curSelectedNode = -1;
                }
            }
        });

        path.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!PathSim.in3d) {
                    splineMode = false;
                    PathSim.switchModes(true);
                    updateExportFrame(true);
                }
            }
        });

        spline.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(PathSim.in3d) {
                    splineMode = true;
                    PathSim.switchModes(false);
                    updateExportFrame(true);
                }
            }
        });

        delete.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PathSim.pathManager.paths.remove(PathSim.pathManager.curEditingPath);
                PathSim.pathManager.curEditingPath = -1;
            }
        });

        deleteNode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PathSim.pathManager.deleteNode(PathSim.pathManager.curEditingPath, PathSim.pathManager.curSelectedNode);
            }
        });

        purePursuit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!purePursuitMode) {
                    purePursuitMode = true;
                    populatePathEdit();
                    PathSim.renderer3d.resetSim();
                    updateExportFrame(true);
                }
            }
        });

        //TODO: fix ramsete
//        ramsete.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                if(purePursuitMode) {
//                    purePursuitMode = false;
//                    populatePathEdit();
//                    PathSim.renderer3d.resetSim();
//                    updateExportFrame(true);
//                }
//            }
//        });

        export.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                PathSim.exportFrame.setVisible(true);
                updateExportFrame(true);
            }
        });
    }

    public void updateExportFrame(boolean force) {
        if(force || (PathSim.pathManager.editingPath() && (PathSim.pathManager.curEditingVel != -1 || PathSim.pathManager.curEditingNode != -1))) {
            ExtendedPath path = PathSim.pathManager.getCurEPath();
            QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) path.purePursuitPath.getParametric();
            StringBuilder s = new StringBuilder();

            if(group.getSplines().size() > 1) {
                s.append("QuinticHermiteSplineGroup spline = new QuinticHermiteSplineGroup();\n\n");
            }

            int i = 1;
            for(QuinticHermiteSpline spline : group.getSplines()) {
                s.append("QuinticHermiteSpline ").append(group.getSplines().size() == 1 ? "spline" : "s" + i).append(" = new QuinticHermiteSpline(");
                s.append("\n    new Pose2D(").append(df2.format(spline.getPose0().getPosition().getX())).append(", ").append(df2.format(spline.getPose0().getPosition().getY())).append(", ");
                s.append(df2.format(spline.getPose0().getAngle().getRadians())).append("), ");
                s.append("new Pose2D(").append(df2.format(spline.getPose1().getPosition().getX())).append(", ").append(df2.format(spline.getPose1().getPosition().getY())).append(", ");
                s.append(df2.format(spline.getPose1().getAngle().getRadians())).append(")");
                if (spline.getVelocity0() != null) {
                    s.append(", \n    new Vector2D(").append(df2.format(spline.getVelocity0().getX())).append(", ").append(df2.format(spline.getVelocity0().getY())).append("), ");
                    s.append("new Vector2D(").append(df2.format(spline.getVelocity1().getX())).append(", ").append(df2.format(spline.getVelocity1().getY())).append(")");
                }
                s.append("\n);");

                if(group.getSplines().size() > 1) {
                    s.append("\nspline.addSpline(s").append(i).append(");");
                }

                s.append("\n\n");

                i++;
            }

            if(!splineMode) {
                s.append(purePursuitMode ? "PurePursuitPath path = new PurePursuitPath(" : "RamsetePath path = new RamsetePath(");
                s.append("\n    spline, ").append(path.purePursuitPath.getMaxAcceleration()).append(", ").append(path.purePursuitPath.getMaxDeceleration()).append(", ").append(path.purePursuitPath.getMaxVelocity());
                s.append(", ").append(path.purePursuitPath.getMaxAngularVelocity()).append(", ").append(path.purePursuitPath.getStartVelocity()).append(", ").append(path.purePursuitPath.getEndVelocity()).append("\n);\n");

                if(purePursuitMode) {
                    s.append("\nPurePursuitPFCommand pathCommand = new PurePursuitPFCommand(path, ");
                    s.append("\n    ").append(path.lookahead).append(", ").append(path.end_threshold).append(", ").append(path.adjust_threshold).append(", false\n);\n\n");
                } else {
                    s.append("\nRamsetePFCommand pathCommand = new RamsetePFCommand(path, ");
                    s.append("\n    ").append(path.b).append(", ").append(path.Z).append(", ").append(path.r_end_threshold).append(", ").append(path.r_adjust_threshold).append(", false\n);\n\n");
                }
            }

            PathSim.exportText.setText(s.toString());
        }
    }

    @Override
    public void dispose() {

    }
}
