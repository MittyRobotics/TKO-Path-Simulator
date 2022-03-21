package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
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
    public TimedModel sphereInstance2;
    public Environment environment;
    public static CamController3D camController;
    public Array<ModelInstance> instances = new Array<>();
    public Array<ModelInstance> sphereInstances = new Array<>();
    public Array<TimedModel> posInstances = new Array<>();

    public boolean loading;

    public double fieldWidth, fieldHeight, robotL, robotW;
    public int width, height;

    public ModelBuilder modelBuilder;

    public Pose2D prevPos, cur;

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
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.9f, 0.9f, 1f));
        environment.add(new DirectionalLight().set(0.6f, 0.6f, 0.6f, -1f, -0.8f, -0.2f));


        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        loading = true;

        cam = new PerspectiveCamera(67, width, height);
        cam.position.set(200f, 800f, 500f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 5000f;
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

        robotW = temp.getDepth() - 6.5;

        scale = (float) inch;
        robotInstance.transform.scale(scale, scale, scale);

        instances.add(instance);
        instances.add(robotInstance);
        loading = false;

        posInstances.ordered = true;

        for(QuinticHermiteSpline s : Splines.splines) {
            renderSpline(s);
        }

        cur = new Pose2D();
    }

    public void render () {
        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear((GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);
        Gdx.gl.glEnable(GL30.GL_TEXTURE_2D);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        moveRobot(new Pose2D(PathSim.xEntry.getDouble(0.0), PathSim.yEntry.getDouble(0.0), (PathSim.tEntry.getDouble(0.0)) * (Math.PI/180) + Math.PI/2));

        if(Gdx.input.isKeyPressed(Input.Keys.DEL)) {
            posInstances.clear();
        }


//        double angle = cur.getAngle().getRadians();
//
//        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
//            cur = new Pose2D(cur.getPosition().x - 4*Math.cos(angle), cur.getPosition().y - 4*Math.sin(angle), angle);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//            cur = new Pose2D(cur.getPosition().x + 4*Math.cos(angle), cur.getPosition().y + 4*Math.sin(angle), angle);
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            cur = new Pose2D(cur.getPosition().x, cur.getPosition().y, angle - 0.1);
//        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            cur = new Pose2D(cur.getPosition().x, cur.getPosition().y, angle + 0.1);
//        }
//
//        moveRobot(cur);


        modelBatch.begin(cam);
        modelBatch.render(instances, environment);

        modelBatch.render(sphereInstances);
        for(TimedModel m : posInstances) {
            if(m != null) modelBatch.render(m);
        }

        modelBatch.end();

        Gdx.gl.glDisable(GL30.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_TEXTURE_2D);
    }

    public void renderSpline(Parametric spline) {
        double step = 1. / (2 * (int) (spline.getLength()));

        for(double i = step; i <= 1; i += step) {
            sphereInstance = new ModelInstance(sphere);
            Point2D cur = spline.getPoint(i);
            sphereInstance.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));
            sphereInstances.add(sphereInstance);
        }

    }

    public void moveRobot(Pose2D pose) {

        if(prevPos != null) {
            QuinticHermiteSpline s = new QuinticHermiteSpline(prevPos, pose);
            for (double t = 0; t <= 1; t += 0.1) {
                sphereInstance2 = new TimedModel(sphere2, posInstances);
                Point2D cur = s.getPoint(t);
                sphereInstance2.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));
                posInstances.add(sphereInstance2);
            }
        } else {
            sphereInstance2 = new TimedModel(sphere2, posInstances);
            Point2D cur = pose.getPosition();
            sphereInstance2.transform.translate((float) (cur.getX() * inch), 10f, (float) (-cur.getY() * inch));
            posInstances.add(sphereInstance2);
        }

        moveRobotBack();
        moveRobotToPose(pose);


    }

    public void moveRobotToPose(Pose2D cur) {
        Point2D pos = getRobotPos(cur.getPosition().getX(), cur.getPosition().getY());
        robotInstance.transform.translate((float) pos.getX(), 4f, (float) -pos.getY()).rotate(0, 1, 0,
                (float) (180 + cur.getAngle().getRadians() * 180 / Math.PI)).translate(0.45f, 0, (float) robotW / 2);

        prevPos = cur;
    }

    public void moveRobotBack() {
        if(prevPos != null) {
            Point2D pos = getRobotPos(prevPos.getPosition().getX(), prevPos.getPosition().getY());
            robotInstance.transform.translate(-0.45f, 0, (float) -robotW / 2).rotate(0, 1, 0,
                    (float) (-180 - prevPos.getAngle().getRadians() * 180 / Math.PI)).translate((float) -pos.getX(), -4f, (float) pos.getY());
        }
    }

    public Point2D getRobotPos(double x, double y) {
        return new Point2D(x, y);
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
