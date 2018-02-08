package com.p4u1.metronome;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.p4u1.metronome.utils.PaulGraphics;

import java.sql.Time;


public class MetronomeGame extends ApplicationAdapter {
	SpriteBatch batch;
    ShapeRenderer shapes;
	Texture pauseTexture;
	Sprite pause;
	Texture playTexture;
	Sprite play;
	Sound woodblockSound;
	long bpm = 70;
	long lastTap = 0;
	static long MIL_IN_MINUTE = 60000;

	long timer = TimeUtils.millis();

	Boolean isTapping = false;
	Boolean pausing = false;
	OrthographicCamera camera;
    BitmapFont font;
    long lastFlash = TimeUtils.millis();

	
	@Override
	public void create () {

		woodblockSound = Gdx.audio.newSound(Gdx.files.internal("woodblock.wav"));
		batch = new SpriteBatch();
		pauseTexture = new Texture("pause.png");
		playTexture = new Texture("play.png");
		pause = new Sprite(pauseTexture);
		play = new Sprite(playTexture);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, PaulGraphics.GAMEWIDTH, PaulGraphics.GAMEHEIGHT);
        font = new BitmapFont(Gdx.files.internal("droid.fnt"),
                false);
        shapes = new ShapeRenderer();

		pause.setY(250);
		pause.setX((PaulGraphics.GAMEWIDTH / 2) - 50);

		play.setY(250);
		play.setX((PaulGraphics.GAMEWIDTH / 2) - 50);
		setOffset();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
        batch.setProjectionMatrix(camera.combined);
        font.setColor(Color.WHITE);
        String bpmString = "- " + bpm + " +";
        float space = font.getSpaceWidth();
        font.draw(batch, bpmString, (PaulGraphics.GAMEWIDTH / 2) - (bpmString.length() * 20), (PaulGraphics.GAMEHEIGHT / 2) + 25);
        String tap = "Tap";
        font.draw(batch, tap, (PaulGraphics.GAMEWIDTH / 2) - (tap.length() * 20), 117 + 25);
        if (pausing) {
			play.draw(batch);
		} else {
			pause.draw(batch);
		}
		batch.end();

		shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setProjectionMatrix(camera.combined);
		shapes.setColor(Color.WHITE);
        if (isTapping) {
            shapes.rect(166, 68, 160, 100);
        }
        long diff = TimeUtils.millis() - timer;
        float centerX = PaulGraphics.GAMEWIDTH / 2;
        float centerY = 600;
        float beatTime = 60000 / bpm;

        double angle = ((2* Math.PI) * ((int)((TimeUtils.millis() - angleOffset) % (int)(2*beatTime)) / (2*beatTime) )) + (Math.PI * .5);
        shapes.line(centerX, centerY, centerX + (float)Math.cos(angle)*250, centerY + (float)Math.abs(Math.sin(angle))*250);
		shapes.line(centerX, centerY, centerX, 900);
		shapes.end();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        if (TimeUtils.millis() - lastFlash < 30) {
            //shapes.rect(0, 600, PaulGraphics.GAMEWIDTH, 400);
        }
        shapes.end();
		logic();
	}
	double angleOffset = 0;

	public void setOffset() {
		long diff = TimeUtils.millis() - timer;
		float centerX = PaulGraphics.GAMEWIDTH / 2;
		float centerY = 600;
		float beatTime = 60000 / bpm;

		angleOffset = TimeUtils.millis() % (int)(2*beatTime);
	}

	public void logic() {
		if (TimeUtils.millis() - timer > (MIL_IN_MINUTE / bpm) && !pausing) {
			woodblockSound.play();
			timer = TimeUtils.millis();
			lastFlash = TimeUtils.millis();
			//setOffset();
		}

        if (Gdx.input.justTouched()) {
		    Vector2 touch = PaulGraphics.pixelToCoord(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		    if (touch.y < 600 && touch.y > 400) {
		        if (touch.x > (PaulGraphics.GAMEWIDTH / 2)) {
		            bpm++;
					timer = TimeUtils.millis();
					woodblockSound.play();
					setOffset();
                } else {
		            bpm--;
					timer = TimeUtils.millis();
					woodblockSound.play();
					setOffset();
                }
            }
            if (touch.y < 185) {
		        if (isTapping) {

		            bpm = (int)(60000 / (TimeUtils.millis() - lastTap));
		            isTapping = false;
		            timer = TimeUtils.millis();
		            woodblockSound.play();
					setOffset();
                } else {
                    isTapping = true;
                    lastTap = TimeUtils.millis();
                }
            }

            if (touch.y < 360 && touch.y > 240) {
		    	if (pausing) {
					pausing = false;
					timer = TimeUtils.millis();
					woodblockSound.play();
					lastFlash = TimeUtils.millis();
					setOffset();
				} else {
		    		pausing = true;
				}
			}

            Gdx.app.log("phlusko-touch_coordinate", PaulGraphics.pixelToCoord(new Vector2(Gdx.input.getX(), Gdx.input.getY())).toString());
            Gdx.app.log("phlusko-touch_pixel", new Vector2(Gdx.input.getX(), Gdx.input.getY()).toString());

        }
		if (TimeUtils.millis() - lastTap > 2000) {
			isTapping = false;
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		shapes.dispose();
		font.dispose();
		woodblockSound.dispose();
		pauseTexture.dispose();
	}
}
