package com.gamedev;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class TiltScreenActivity extends BaseGameActivity implements IOnSceneTouchListener, SensorEventListener {

	private Camera myCamera;
	private Scene myScene;
	private SensorManager sensorManager;
	private PhysicsWorld myPhysicsWorld;

	private final static int CAMERA_WIDTH = 1024;
	private final static int CAMERA_HEIGHT = 600;

	private Sprite mySprite;
	private Body mySpriteBody;

	private float tiltSpeedX;
	private float tiltSpeedY;

	private BitmapTextureAtlas myTextureAtlas;
	private ITextureRegion myBoulderTextureRegion;

	@Override
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		myCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), myCamera);

		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// TODO Auto-generated method stub
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		myTextureAtlas = new BitmapTextureAtlas(getTextureManager(), 1024, 1024);

		myBoulderTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.myTextureAtlas, this, "boulder.png", 0, 0);
		myTextureAtlas.load();

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// TODO Auto-generated method stub
		myScene = new Scene();

		myScene.setBackground(new Background(0, 1, 2));

		myPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		// Bringing in Tilt Screen Sensor
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

		createWalls();

		myScene.registerUpdateHandler(myPhysicsWorld);

		pOnCreateSceneCallback.onCreateSceneFinished(myScene);

	}

	private void createWalls() {
		// TODO Auto-generated method stub
		FixtureDef wallFixtures = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);

		// Rectangles X, Y, Width, Height, VertextBufferManager
		Rectangle ground = new Rectangle(0, CAMERA_HEIGHT, CAMERA_WIDTH, 1, this.mEngine.getVertexBufferObjectManager());
		Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 1, this.mEngine.getVertexBufferObjectManager());
		Rectangle left = new Rectangle(0, 0, 10, CAMERA_HEIGHT, this.mEngine.getVertexBufferObjectManager());
		Rectangle right = new Rectangle(CAMERA_WIDTH, 0, 1, CAMERA_HEIGHT, this.getVertexBufferObjectManager());

		ground.setColor(0, 0, 0);
		roof.setColor(0, 0, 0);
		left.setColor(0, 0, 0);
		right.setColor(0, 0, 0);

		PhysicsFactory.createBoxBody(myPhysicsWorld, right, BodyType.StaticBody, wallFixtures);
		PhysicsFactory.createBoxBody(myPhysicsWorld, left, BodyType.StaticBody, wallFixtures);
		PhysicsFactory.createBoxBody(myPhysicsWorld, roof, BodyType.StaticBody, wallFixtures);
		PhysicsFactory.createBoxBody(myPhysicsWorld, ground, BodyType.StaticBody, wallFixtures);

		myScene.attachChild(right);
		myScene.attachChild(left);
		myScene.attachChild(ground);
		myScene.attachChild(roof);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		// TODO Auto-generated method stub

		myScene.setOnSceneTouchListener(this);

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		switch (pSceneTouchEvent.getAction()) {
		case TouchEvent.ACTION_DOWN:
			addSprites(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			return true;
		case TouchEvent.ACTION_UP:
			return true;
		}
		return false;
	}

	private void addSprites(float pX, float pY) {
		// TODO Auto-generated method stub

		FixtureDef spriteFixtureDef = PhysicsFactory.createFixtureDef(10.0f, 0.2f, 0.1f);

		mySprite = new Sprite(pX, pY, myBoulderTextureRegion, this.getVertexBufferObjectManager());
		mySpriteBody = PhysicsFactory.createCircleBody(myPhysicsWorld, mySprite, BodyType.DynamicBody, spriteFixtureDef);
		mySprite.setUserData(mySpriteBody);
		myScene.attachChild(mySprite);

		myPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(mySprite, mySpriteBody, true, true));

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		tiltSpeedX = event.values[1];
		tiltSpeedY = event.values[0];
		final Vector2 tiltGravity = Vector2Pool.obtain(tiltSpeedX, tiltSpeedY);
		myPhysicsWorld.setGravity(tiltGravity);
		Vector2Pool.recycle(tiltGravity);

	}
}
