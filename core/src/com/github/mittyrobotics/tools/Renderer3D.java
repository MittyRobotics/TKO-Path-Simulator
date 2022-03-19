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
import com.github.mittyrobotics.pathfollowing.*;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Renderer3D {

    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model, sphere, robot, sphere2;
    public ModelInstance instance, sphereInstance, robotInstance;
    public Environment environment;
    public static CamController3D camController;
    public Array<ModelInstance> instances = new Array<>();
    public Array<ModelInstance> sphereInstances = new Array<>();
    public Array<ModelInstance> posInstances = new Array<>();

    public boolean loading;

    public double fieldWidth, fieldHeight, robotL, robotW;
    public int width, height;

    public ModelBuilder modelBuilder;

    public Pose2D prevPos;

    public double inch;
    public float scale;

    public Color green = new Color(67/255f, 1f, 170/255f, 1f);
    public Color blue = new Color(67/255f, 170/255f, 1f, 1f);

    public Renderer3D() {
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        sphere = modelBuilder.createSphere(3f, 3f, 3f, 10, 10, new Material(ColorAttribute.createDiffuse(green)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        sphere2 = modelBuilder.createSphere(3f, 3f, 3f, 10, 10, new Material(ColorAttribute.createDiffuse(blue)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 1f, -1f, -0.8f, -0.2f));


        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        loading = true;

        cam = new PerspectiveCamera(67, width, height);
        cam.position.set(200f, 800f, 500f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 3000f;
        cam.update();

        camController = new CamController3D(cam, width, height);
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

        robotInstance.calculateBoundingBox(temp);
        robotW = 85;
        robotL = 100;

        double actual = inch * 38;
        scale = (float) (actual / robotL);
        robotInstance.transform.scale(scale, scale, scale);

        instances.add(instance);
        instances.add(robotInstance);
        loading = false;

        QuinticHermiteSpline group = new QuinticHermiteSpline(new Pose2D(50, 50, 0), new Pose2D(250, 100, 0));

        posInstances.ordered = true;

        renderSpline(group);
    }

    public void render () {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        moveRobot(new Pose2D(PathSim.xEntry.getDouble(0.0), PathSim.yEntry.getDouble(0.0), PathSim.tEntry.getDouble(0.0)));


        moveRobot(new Pose2D(100, 100, 0));
        moveRobot(new Pose2D(200, 50, 0));
        moveRobot(new Pose2D(100, 50, 0));

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);

        modelBatch.render(sphereInstances);
        modelBatch.render(posInstances);

        modelBatch.end();
    }

    public void renderSpline(Parametric spline) {
        sphereInstances.clear();
        double step = 1. / (10 * (int) (spline.getLength()));

        for(double i = step; i <= 1; i += step) {
            sphereInstance = new ModelInstance(sphere);
            Point2D cur = spline.getPoint(i);
            sphereInstance.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));
            sphereInstances.add(sphereInstance);
        }

    }

    public void moveRobot(Pose2D pose) {
        moveRobotBack();
        moveRobotToPose(pose);
        sphereInstance = new ModelInstance(sphere2);
        Point2D cur = pose.getPosition();
        sphereInstance.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));

        if(posInstances.size > 100000) {
            posInstances.removeIndex(0);
        }
        posInstances.add(sphereInstance);

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

}
