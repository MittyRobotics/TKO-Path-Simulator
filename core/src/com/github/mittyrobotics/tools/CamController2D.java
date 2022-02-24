package com.github.mittyrobotics.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.github.mittyrobotics.PathSim;

public class CamController2D extends GestureDetector {
    public Renderer2D renderer;

    private float startX, startY;
    private double ZOOM_RATIO = 0.05;
    public static int width, height;
    private boolean dragging;

    private final double MAX_ZOOM_RELATIVE = 1.7;
    private final double MIN_ZOOM_RELATIVE = 0.12;


    public CamController2D(Renderer2D renderer, int width, int height) {
        super(new GestureListener() {
            @Override
            public boolean touchDown(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean tap(float x, float y, int count, int button) {
                return false;
            }

            @Override
            public boolean longPress(float x, float y) {
                return false;
            }

            @Override
            public boolean fling(float velocityX, float velocityY, int button) {
                return false;
            }

            @Override
            public boolean pan(float x, float y, float deltaX, float deltaY) {
                return false;
            }

            @Override
            public boolean panStop(float x, float y, int pointer, int button) {
                return false;
            }

            @Override
            public boolean zoom(float initialDistance, float distance) {
                return false;
            }

            @Override
            public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
                return false;
            }

            @Override
            public void pinchStop() {

            }
        });
        this.renderer = renderer;

        this.width = width;
        this.height = height;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if(UI.addingSpline == 0 || button == 1) {
            dragging = true;
            startX = screenX;
            startY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        dragging = false;
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if(Gdx.input.getX() <= PathSim.LEFT_WIDTH && dragging) {
            double deltaX = (startX - screenX);
            double deltaY = (startY - screenY);
            startX = screenX;
            startY = screenY;

            renderer.xc -= deltaX;
            renderer.yc += deltaY;

            boundXY();

            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        if(Gdx.input.getX() <= PathSim.LEFT_WIDTH) {
            return zoom(amountY);
        }
        return false;
    }

    public boolean zoom (float amount) {

        double newzoom = renderer.zoom + amount * ZOOM_RATIO * renderer.zoom;
        newzoom = Math.max(Math.min(newzoom, MAX_ZOOM_RELATIVE), MIN_ZOOM_RELATIVE);

        double mousex = Gdx.input.getX();
        double mousey = height - Gdx.input.getY();


        double factor = 1 - newzoom / renderer.zoom;

        renderer.xc += (mousex - renderer.centerx) * factor;
        renderer.yc += (mousey - renderer.centery) * factor;

        renderer.zoom = newzoom;

        boundXY();

        return true;
    }

    public void boundXY() {
        int maxx = (int) (renderer.fieldWidth * renderer.zoom / 2);
        int maxy = (int) (renderer.fieldHeight * renderer.zoom / 2);
        renderer.xc = Math.min(maxx, Math.max(renderer.xc, -maxx));
        renderer.yc = Math.min(maxy, Math.max(renderer.yc, -maxy));
    }

}
