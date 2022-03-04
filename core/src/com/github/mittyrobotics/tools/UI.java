package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

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
    public TextButton pathId, addNode1, addNode2, spline, path, addPath, export, delete, deleteNode;
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
//        scrollPaneStyle.background = PathSim.skin.getDrawable("btn_default_normal");

        System.out.println(Gdx.graphics.getHeight());

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
        pane.setFlickScroll(false);
        pane.layout();
        container.add(pane).width(250).height(Gdx.graphics.getHeight() - 518);
        container.row();
        container.setBounds(right+25, 82, 250, Gdx.graphics.getHeight() - 518);
        splineEdit.add(container);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = PathSim.font;
        textButtonStyle.up = PathSim.skin.getDrawable("btn_default_normal");
        textButtonStyle.down = PathSim.skin.getDrawable("btn_default_focused");

        TextButton.TextButtonStyle textButtonStyleNoBg = new TextButton.TextButtonStyle();
        textButtonStyleNoBg.font = PathSim.font;

        addPath = new TextButton("Add Path", textButtonStyle);
        addPath.getLabel().setFontScale(0.7f);
        addPath.setBounds(right+50, Gdx.graphics.getHeight() - 265, 200, 50);

        addNode1 = new TextButton("Add Start", textButtonStyle);
        addNode1.getLabel().setFontScale(0.5f);
        addNode1.setBounds(right+25, Gdx.graphics.getHeight() - 398, 130, 40);

        addNode2 = new TextButton("Add End", textButtonStyle);
        addNode2.getLabel().setFontScale(0.5f);
        addNode2.setBounds(right+145, Gdx.graphics.getHeight() - 398, 130, 40);

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
        delete.setBounds(right+25, Gdx.graphics.getHeight() - 428, 130, 40);

        deleteNode = new TextButton("Delete Node", textButtonStyle);
        deleteNode.getLabel().setFontScale(0.5f);
        deleteNode.setBounds(right+145, Gdx.graphics.getHeight() - 428, 130, 40);

        addListeners();

        toggle.add(path);
        toggle.add(spline);
        toggle.add(export);

        splineEdit.add(addNode1);
        splineEdit.add(addNode2);
        splineEdit.add(delete);
        splineEdit.add(deleteNode);

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

        TextField temp = new TextField(df.format(sp.get(0).getPose0().getPosition().getX()), textFieldStyle);
        temp.addListener(new InputListener() {
            final QuinticHermiteSpline s = sp.get(0); final TextField tf = temp; @Override
            public boolean keyTyped (InputEvent event, char character) {
                if(event.getKeyCode() == Input.Keys.ENTER) {
                    if(checkPosition(Double.parseDouble(tf.getText()), false)) s.setPose0(new Pose2D(new Point2D(Double.parseDouble(tf.getText()), s.getPose0().getPosition().getY()), s.getPose0().getAngle()));
                    tf.setText(df.format(s.getPose0().getPosition().getX()));
                    stage.unfocus(tf);
                } return super.keyTyped(event, character);
            }
        });
        splines.add(temp);

        TextField temp2 = new TextField(df.format(sp.get(0).getPose0().getPosition().getY()), textFieldStyle);
        temp2.addListener(new InputListener() {
            final QuinticHermiteSpline s = sp.get(0); final TextField tf = temp2; @Override
            public boolean keyTyped (InputEvent event, char character) {
                if(event.getKeyCode() == Input.Keys.ENTER) {
                    if(checkPosition(Double.parseDouble(tf.getText()), true)) s.setPose0(new Pose2D(new Point2D(s.getPose0().getPosition().getX(), Double.parseDouble(tf.getText())), s.getPose0().getAngle()));
                    tf.setText(df.format(s.getPose0().getPosition().getY()));
                } return super.keyTyped(event, character);
            }
        });
        splines.add(temp2);

        for(QuinticHermiteSpline s_ : sp) {
            TextField temp3 = new TextField(df.format(s_.getPose1().getPosition().getX()), textFieldStyle);
            temp3.addListener(new InputListener() {
                final QuinticHermiteSpline s = s_; final TextField tf = temp3; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosition(Double.parseDouble(tf.getText()), false)) s.setPose1(new Pose2D(new Point2D(Double.parseDouble(tf.getText()), s.getPose1().getPosition().getY()), s.getPose1().getAngle()));
                        tf.setText(df.format(s.getPose1().getPosition().getX()));
                    } return super.keyTyped(event, character);
                }
            });
            splines.add(temp3);

            TextField temp4 = new TextField(df.format(s_.getPose1().getPosition().getY()), textFieldStyle);
            temp4.addListener(new InputListener() {
                final QuinticHermiteSpline s = s_; final TextField tf = temp4; @Override
                public boolean keyTyped (InputEvent event, char character) {
                    if(event.getKeyCode() == Input.Keys.ENTER) {
                        if(checkPosition(Double.parseDouble(tf.getText()), true)) s.setPose1(new Pose2D(new Point2D(s.getPose1().getPosition().getX(), Double.parseDouble(tf.getText())), s.getPose1().getAngle()));
                        tf.setText(df.format(s.getPose1().getPosition().getY()));
                    } return super.keyTyped(event, character);
                }
            });
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
                    if(pointer == -1) PathSim.pathManager.curUIHoveringNode = -1;
                }

                @Override
                public void clicked (InputEvent event, float x, float y) {
                    PathSim.pathManager.curEditingNode = finalI;
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

    public boolean checkPosition(double p, boolean y) {
        return (y && Math.abs(p) <= 162) || (!y && Math.abs(p) <= 324);
    }

    public void updateSplineEdit() {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) PathSim.pathManager.paths.get(PathSim.pathManager.curEditingPath).getParametric();
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
