package com.p4u1.metronome.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Paul on 12/27/2016.
 */

public class PaulGraphics {

    public static float width = Gdx.graphics.getWidth();
    public static float height = Gdx.graphics.getHeight();
    public static final float GAMEHEIGHT = 1000f;
    public static final float GAMEWIDTH = 500f;

    static public Vector2 pixelToCoord(Vector2 arg0) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        float x = (arg0.x * GAMEWIDTH) / width;
        float y = GAMEHEIGHT - (((arg0.y) * GAMEHEIGHT) / height);

        return new Vector2(x, y);
    }

    static public Vector2 coordToPixel(Vector2 arg0) {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        float x = (arg0.x * width) / GAMEWIDTH;
        float y = ((arg0.y - GAMEHEIGHT) * height) / GAMEHEIGHT;
        return new Vector2(x, y);
    }

    static public float getAngleFromVectors(Vector2 arg0, Vector2 center) {
        Vector2 ray = center.cpy().sub(arg0);
        ray = ray.nor();
        double angle = Math.acos(ray.y);
        angle = Math.toDegrees(angle);
        if (arg0.x < center.x) {
            angle = 360 - angle;
        }
        return (float)Math.floor(angle);
    }

    static public float getAngleFromArcLength(float length, float radius) {
        float circ = 2*((float)Math.PI)*radius;
        float angle = 360 * (length / circ);
        return angle;
    }

    static public Vector2 getOrtho(Vector2 arg0, Vector2 arg1){
        Vector2 ray = arg0.cpy().sub(arg1);
        ray.scl(1/ray.len());
        Vector2 ortho = new Vector2(-ray.y, ray.x);
        return ortho;
    }
}
