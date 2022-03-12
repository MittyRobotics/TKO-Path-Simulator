package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.Path;
import com.github.mittyrobotics.pathfollowing.Point2D;
import com.github.mittyrobotics.pathfollowing.Pose2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSplineGroup;

public class Renderer3D {

    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model, sphere, robot;
    public ModelInstance instance, sphereInstance, robotInstance;
    public Environment environment;
    public static CamController3D camController;
    public Array<ModelInstance> instances = new Array<>();
    public Array<ModelInstance> sphereInstances = new Array<>();
    public boolean loading, running;

    public double fieldWidth, fieldHeight, robotL, robotW, timer;
    public int width, height, curInd;

    public QuinticHermiteSplineGroup group;
    public Path path;

    public ModelBuilder modelBuilder;

    public Pose2D prevPos;

    public double inch;
    public float scale;

    public Color green = new Color(67/255f, 1f, 170/255f, 1f);

    public UI tempui;

    public Renderer3D() {
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        sphere = modelBuilder.createSphere(3f, 3f, 3f, 10, 10, new Material(ColorAttribute.createDiffuse(green)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 1f, -1f, -0.8f, -0.2f));


        width = Gdx.graphics.getWidth() - PathSim.RIGHT_WIDTH;
        height = Gdx.graphics.getHeight();

        cam = new PerspectiveCamera(67, width, height);
        cam.position.set(0f, 1600f, 0f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 3000f;
        cam.update();

        loading = true;

        camController = new CamController3D(cam, width, height);
    }

    public void reset() {
        cam = new PerspectiveCamera(67, width, height);
        cam.position.set(0f, 1600f, 0f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 3000f;
        cam.update();

        camController = new CamController3D(cam, width, height);

        path = PathSim.pathManager.getCurPath();
        group = (QuinticHermiteSplineGroup) path.getParametric();

        renderSpline();
        running = false;
        moveRobotBack();
        moveRobotToPose(group.getPose(0));

        running = true;
    }

    public void doneLoading() {
        model = PathSim.assets.get("field.g3db", Model.class);
        robot = PathSim.assets.get("robot.g3db", Model.class);
        BoundingBox temp = new BoundingBox();
        model.calculateBoundingBox(temp);

        fieldWidth = temp.getWidth();
        fieldHeight = temp.getDepth();

        inch = fieldWidth / 864;

        instance = new ModelInstance(model);
        robotInstance = new ModelInstance(robot);

//        robotInstance.transform.scale(100f, 100f, 100f);
//        robotInstance.calculateTransforms();
        robotInstance.calculateBoundingBox(temp);
        robotW = 85;
        robotL = 100;

        double actual = inch * 38;
        scale = (float) (actual / robotL);
        robotInstance.transform.scale(scale, scale, scale);

        instances.add(instance);
        loading = false;

        tempui = PathSim.renderer2d.ui;
    }

    public void render () {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.render(robotInstance, environment);

        modelBatch.render(sphereInstances);

        modelBatch.end();

        Simulator s = tempui.simulator;

        if(s.getEnd(tempui.purePursuitMode) > 0) {
            moveRobotBack();
            moveRobotToPose(tempui.purePursuitMode ? s.getPState(curInd).robotPose : s.getRState(curInd).robotPose);

            if (running) {
                timer += Gdx.graphics.getDeltaTime();
                curInd += (int) (timer / 0.02);
                timer %= 0.02;

                if (curInd >= s.getEnd(tempui.purePursuitMode)) {
                    running = false;
                    curInd = s.getEnd(tempui.purePursuitMode) - 1;
                }
            }
        }

    }

    public void renderSpline() {
        sphereInstances.clear();
        double step = 1. / (10 * (int) (group.getLength()));

        for(double i = step; i <= 1; i += step) {
            sphereInstance = new ModelInstance(sphere);
            Point2D cur = group.getPoint(i);
            sphereInstance.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));
            sphereInstances.add(sphereInstance);
        }

    }

    public void moveRobotToPose(Pose2D cur) {
        Point2D pos = getRobotPos(cur.getPosition().getX(), cur.getPosition().getY());
        robotInstance.transform.translate((float) pos.getX(), 4f, (float) -pos.getY()).rotate(0, 1, 0,
                (float) (180 + cur.getAngle().getRadians() * 180 / Math.PI)).translate((float) (-robotL * scale / 2), 0f, (float) (robotW * scale / 2));

        prevPos = cur;
    }

    public void moveRobotBack() {
        if(prevPos != null) {
            Point2D pos = getRobotPos(prevPos.getPosition().getX(), prevPos.getPosition().getY());
            robotInstance.transform.translate((float) (robotL * scale / 2), 0f, (float) (-robotW * scale / 2)).rotate(0, 1, 0,
                    (float) (-180 - prevPos.getAngle().getRadians() * 180 / Math.PI)).translate((float) -pos.getX(), -4f, (float) pos.getY());
        }
    }

    public Point2D getRobotPos(double x, double y) {
        return new Point2D(x * inch / scale, y * inch / scale);
    }

    public void load() {
        if(loading && PathSim.assets.update()) {
            doneLoading();
        }
    }

    public void dispose () {
        modelBatch.dispose();
        instances.clear();
    }

    public void resetSim() {
        curInd = 0;
        timer = 0;
        running = true;
    }

}
