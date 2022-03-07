package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.Path;
import com.github.mittyrobotics.pathfollowing.Point2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSplineGroup;

public class Renderer3D {

    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model, sphere;
    public ModelInstance instance, sphereInstance;
    public Environment environment;
    public static CamController3D camController;
    public Array<ModelInstance> instances = new Array<>();
    public Array<ModelInstance> sphereInstances = new Array<>();
    public boolean loading;

    public double fieldWidth, fieldHeight;
    public int width, height;

    public QuinticHermiteSplineGroup group;
    public Path path;

    public ModelBuilder modelBuilder;

    public double inch;

    public Color green = new Color(67/255f, 1f, 170/255f, 1f);

    public Renderer3D() {
        modelBatch = new ModelBatch();
        modelBuilder = new ModelBuilder();
        sphere = modelBuilder.createSphere(5f, 5f, 5f, 10, 10, new Material(ColorAttribute.createDiffuse(green)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));


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

        path = PathSim.pathManager.paths.get(PathSim.pathManager.curEditingPath);
        group = (QuinticHermiteSplineGroup) path.getParametric();

        renderSpline();
    }

    public void doneLoading() {
        model = PathSim.assets.get("field.g3db", Model.class);
        BoundingBox temp = new BoundingBox();
        model.calculateBoundingBox(temp);

        fieldWidth = temp.getWidth();
        fieldHeight = temp.getDepth();

        inch = fieldWidth / 864;

        instance = new ModelInstance(model);
        instances.add(instance);
        loading = false;
    }

    public void render () {

        Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.12f, 1f);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);

        modelBatch.render(sphereInstances);

        modelBatch.end();
    }

    public void renderSpline() {
        sphereInstances.clear();
        double step = 1. / (10 * (int) (group.getLength()));

        for(double i = step; i <= 1; i += step) {
            sphereInstance = new ModelInstance(sphere);
            Point2D cur = group.getPoint(i);
            sphereInstance.transform.translate((float) (cur.x * inch), 10f, (float) (-cur.y * inch));
            sphereInstances.add(sphereInstance);
        }
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
