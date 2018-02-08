package com.p4u1.metronome;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.p4u1.metronome.utils.PaulGraphics;

import java.sql.Time;


public class MetronomeGame extends ApplicationAdapter {
	SpriteBatch batch;
    ShapeRenderer shapes;
	Texture img;
	Sound woodblockSound;
	long bpm = 170;
	long lastTap = 0;
	static long MIL_IN_MINUTE = 60000;

	long timer = TimeUtils.millis();

	Boolean isTapping = false;
	OrthographicCamera camera;
    BitmapFont font;
    long lastFlash = TimeUtils.millis();
	
	@Override
	public void create () {

		woodblockSound = Gdx.audio.newSound(Gdx.files.internal("woodblock.wav"));
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		camera = new OrthographicCamera();
		camera.setToOrtho(false, PaulGraphics.GAMEWIDTH, PaulGraphics.GAMEHEIGHT);
        font = new BitmapFont(Gdx.files.internal("droid.fnt"),
                false);
        shapes = new ShapeRenderer();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
        batch.setProjectionMatrix(camera.combined);
        font.setColor(Color.WHITE);
        String bpmString = "- " + bpm + " +";
        //Gdx.app.log("phlusko",font.getScaleX() + "");
        //Gdx.app.log("phlusko",font.getScaleY() + "");
        float space = font.getSpaceWidth();
        //font.draw(batch, bpmString, (PaulGraphics.width / 2) - ((bpmString.length() / 2) * 87), (PaulGraphics.height / 2) + 40);
        font.draw(batch, bpmString, (PaulGraphics.GAMEWIDTH / 2) - (bpmString.length() * 20), (PaulGraphics.GAMEHEIGHT / 2) + 25);
        String tap = "Tap";
        font.draw(batch, tap, (PaulGraphics.GAMEWIDTH / 2) - (tap.length() * 20), 117 + 25);
		batch.end();

		shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setProjectionMatrix(camera.combined);
		shapes.setColor(Color.WHITE);
        shapes.line(0, 600, PaulGraphics.GAMEWIDTH, 600);
        shapes.line(0, 400, PaulGraphics.GAMEWIDTH, 400);
        if (isTapping) {
            shapes.rect(166, 68, 160, 100);
        }
		shapes.end();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        if (TimeUtils.millis() - lastFlash < 20) {
            shapes.rect(0, 600, PaulGraphics.GAMEWIDTH, 400);
        }
        shapes.end();
		logic();
	}

	public void logic() {
		if (TimeUtils.millis() - timer > (MIL_IN_MINUTE / bpm)) {
			woodblockSound.play();
			timer = TimeUtils.millis();
			lastFlash = TimeUtils.millis();
		}

        if (Gdx.input.justTouched()) {
		    Vector2 touch = PaulGraphics.pixelToCoord(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		    if (touch.y < 600 && touch.y > 400) {
		        if (touch.x > (PaulGraphics.GAMEWIDTH / 2)) {
		            bpm++;
                } else {
		            bpm--;
                }
            }
            if (touch.y < 185) {
		        if (isTapping) {

		            bpm = (int)(60000 / (TimeUtils.millis() - lastTap));
		            isTapping = false;
		            timer = TimeUtils.millis();
		            woodblockSound.play();
                } else {
                    isTapping = true;
                    lastTap = TimeUtils.millis();
                }
            }
            if (TimeUtils.millis() - lastTap > 2000) {
		        isTapping = false;
            }
            Gdx.app.log("phlusko", PaulGraphics.pixelToCoord(new Vector2(Gdx.input.getX(), Gdx.input.getY())).toString());
            Gdx.app.log("phlusko", new Vector2(Gdx.input.getX(), Gdx.input.getY()).toString());

        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
