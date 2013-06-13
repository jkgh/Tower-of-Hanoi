package com.example.myandenginegame;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import com.example.myandenginegame.SceneManager.SceneType;


public class Main extends BaseGameActivity {
	
	private final int CAMERA_WIDTH = 800;
	private final int CAMERA_HEIGHT = 480;
	private Camera mCamera;
	private SceneManager sceneManager;

	@Override
	public EngineOptions onCreateEngineOptions() {
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws Exception {
		
		sceneManager = new SceneManager(this, mEngine, mCamera);
		sceneManager.loadSplashSceneResources();
		
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws Exception {

		pOnCreateSceneCallback.onCreateSceneFinished(sceneManager.createSplashScene());

	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
		// TODO Auto-generated method stub
		mEngine.registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() 
		{
			public void onTimePassed(final TimerHandler pTimerHandler){
				mEngine.unregisterUpdateHandler(pTimerHandler);
				sceneManager.loadGameSceneResources();
				sceneManager.createGameScenes();
				sceneManager.setCurrentScene(SceneType.MENU);
			}
		}));

		pOnPopulateSceneCallback.onPopulateSceneFinished();

	}
	@Override
	public void onBackPressed()
	{
		System.out.println("*********************************");
		System.out.println(sceneManager.getCurrentScene());
		System.out.println(SceneType.MENU);
		System.out.println("*********************************");

		if (sceneManager.getCurrentScene() != SceneType.MENU){
			
			sceneManager.setCurrentScene(SceneType.MENU);
		}
		else{
			finish();
		}

		
	}

}
