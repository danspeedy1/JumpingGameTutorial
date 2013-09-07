package com.gamedev.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.gamedev.base.BaseScene;
import com.gamedev.manager.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
		Text loadingText = new Text(0, 0, resourcesManager.font, "Loading...", vbom);
		loadingText.setX((camera.getWidth() / 2));
		loadingText.setY((camera.getHeight() / 2));
		attachChild(loadingText);
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}

}
