package com.gamedev.character;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gamedev.manager.ResourcesManager;

public abstract class Player extends AnimatedSprite {

	private Body body;
	private boolean canRun = false;
	private int footContacts = 0;

	private float tiltSpeedX;
	private float tiltSpeedY;
	private PhysicsWorld physicsWorld;

	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}

	public abstract void onDie();

	private void createPhysics(final Camera camera, PhysicsWorld physWorld) {
		this.physicsWorld = physWorld;

		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(10, 0, 0.5f));

		body.setUserData("player");
		body.setFixedRotation(true);

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);

				if (getY() <= 0) {
					onDie();
				}

				//				if (canRun) {
				//					body.setLinearVelocity(new Vector2(5, body.getLinearVelocity().y));
				//				}
			}
		});
	}

	public void setRunning() {
		canRun = true;

		final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100 };

		animate(PLAYER_ANIMATE, 0, 2, true);
	}

	public void jump() {
		if (footContacts < 1) {
			return;
		}
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x, 12));
	}

	public void increaseFootContacts() {
		footContacts++;
	}

	public void decreaseFootContacts() {
		footContacts--;
	}

}
