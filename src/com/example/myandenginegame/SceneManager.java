package com.example.myandenginegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.util.GLState;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.debug.Debug;
import android.graphics.Color;
import android.graphics.Typeface;


public class SceneManager {
	
	private SceneType currentScene;
	private BaseGameActivity activity;
	private Engine engine;
	private Camera camera;
	private Scene splashScene, titleScene, mainGameScene, menuScene;
	private BitmapTextureAtlas splashTextureAtlas, scoreTextureAtlas, totalScoreTextureAtlas, menuBackgroundTexture;
	private TextureRegion splashTextureRegion, menuBgTexture;
	private ITextureRegion mBackgroundTextureRegion, mStagesTextureRegion, mTowerTextureRegion, mRing1, mRing2, mRing3, mstageClearedTextureRegion, mstageOneTextureRegion, mstageTwoTextureRegion;
	private Sprite mTower1, mTower2, mTower3, stageClearedSprite, menuOneStage_1, menuOneStage_2, menuOneStage_3;//click to enter game stages one, two, three
	private Stack mStack1, mStack2, mStack3;
	private Ring ring1, ring2, ring3, ring4;
	boolean stageClearFlag = false;
	private Font stageScoreFont, totalScoreFont;
	private int stageScore, totalScore=0;
	private Text stageScoreText, totalScoreText, timeRemainText;
	
	public enum SceneType
	{
		SPLASH,
		TITLE,
		MAINGAME,
		MENU
	}
	
	public SceneManager(BaseGameActivity activity, Engine engine, Camera camera) {
		this.activity = activity;
		this.engine = engine;
		this.camera = camera;		
	}

	//Method loads all of the splash scene resources
	public void loadSplashSceneResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.DEFAULT);
		splashTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "loading86.gif", 0, 0);
		splashTextureAtlas.load();
	}
	
	public ITexture setUpBitmapTextures(final String filename){
		ITexture tempITexture = null;
		try {
			tempITexture = new BitmapTexture(activity.getTextureManager(), new IInputStreamOpener() {
			    @Override
			    public InputStream open() throws IOException {
			        return activity.getAssets().open(filename);
			    }
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return tempITexture;
	}

	
	//Method loads all of the resources for the game scenes
	public void loadGameSceneResources() {
        // 1 - Set up bitmap textures
		ITexture backgroundTexture=setUpBitmapTextures("gfx/background.png");
		ITexture stagesTexture=setUpBitmapTextures("gfx/stagesBackground.jpg");
		ITexture towerTexture=setUpBitmapTextures("gfx/tower.png");
		ITexture ring1=setUpBitmapTextures("gfx/ring1.png");
		ITexture ring2=setUpBitmapTextures("gfx/ring2.png");
		ITexture ring3=setUpBitmapTextures("gfx/ring3.png");
		ITexture ring4=setUpBitmapTextures("gfx/ring4.png");
		ITexture stageClearedTexture=setUpBitmapTextures("gfx/stageCleared.png");
		ITexture stage1Texture=setUpBitmapTextures("gfx/stage1.png");
		ITexture stage2Texture=setUpBitmapTextures("gfx/stage2.png");

		
		// set up score text and load
		scoreTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);            
		stageScoreFont = new Font(activity.getFontManager(),this.scoreTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		this.engine.getTextureManager().loadTexture(this.scoreTextureAtlas);
		activity.getFontManager().loadFont(this.stageScoreFont);
		
		// set up total score text and load
		totalScoreTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);            
		totalScoreFont = new Font(activity.getFontManager(),this.totalScoreTextureAtlas, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		this.engine.getTextureManager().loadTexture(this.totalScoreTextureAtlas);
		activity.getFontManager().loadFont(this.totalScoreFont);
		
		
		// 2 - Load bitmap textures into VRAM
		backgroundTexture.load();
		stagesTexture.load();
		towerTexture.load();
		ring1.load();
		ring2.load();
		ring3.load();
		ring4.load();
		stageClearedTexture.load();
		stage1Texture.load();
		stage2Texture.load();
		
		// 3 - Set up texture regions
		this.mBackgroundTextureRegion = TextureRegionFactory.extractFromTexture(backgroundTexture);
		this.mStagesTextureRegion = TextureRegionFactory.extractFromTexture(stagesTexture);
		this.mTowerTextureRegion = TextureRegionFactory.extractFromTexture(towerTexture);
		this.mRing1 = TextureRegionFactory.extractFromTexture(ring1);
		this.mRing2 = TextureRegionFactory.extractFromTexture(ring2);
		this.mRing3 = TextureRegionFactory.extractFromTexture(ring3);
		this.mstageClearedTextureRegion = TextureRegionFactory.extractFromTexture(stageClearedTexture);
		this.mstageOneTextureRegion = TextureRegionFactory.extractFromTexture(stage1Texture);
		this.mstageTwoTextureRegion = TextureRegionFactory.extractFromTexture(stage2Texture);
		// 4 - Create the stacks
		this.mStack1 = new Stack();
		this.mStack2 = new Stack();
		this.mStack3 = new Stack();
	}
	
	//Method creates the Splash Scene
	public Scene createSplashScene() {
		//Create the Splash Scene and set background colour to red and add the splash logo.
		splashScene = new Scene();
		splashScene.setBackground(new Background(1, 1, 1));
		Sprite splash = new Sprite(0, 0, splashTextureRegion, activity.getVertexBufferObjectManager())
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) 
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		splash.setScale(1.5f);
		splash.setPosition((camera.getWidth() - splash.getWidth()) * 0.5f, (camera.getHeight() - splash.getHeight()) * 0.5f);
		splashScene.attachChild(splash);
		
		return splashScene;
	}
	
	//Method creates all of the Game Scenes
	public void createGameScenes() {		
		
		//Create the Title Scene and set background colour to green
		titleScene = new Scene();
		titleScene.setBackground(new Background(0, 1, 0));
		
		//Create the Menu Scene and set background colour to green
		menuScene = new Scene();
		menuScene.setBackground(new Background(1, 1, 1));
		
		
		//Set up the Stages Entries background (background of the menu)
		Sprite stagesSprite = new Sprite(0, 0, this.mStagesTextureRegion, activity.getVertexBufferObjectManager());
		stagesSprite.setPosition((camera.getWidth() - stagesSprite.getWidth()) * 0.5f, (camera.getHeight() - stagesSprite.getHeight()) * 0.5f);
		menuScene.attachChild(stagesSprite);
		
		
		//Set up the Total score on menu
		totalScoreText = new Text(600, 10, this.totalScoreFont, "score:\n" + totalScore, activity.getVertexBufferObjectManager());
		totalScoreText.setHorizontalAlign(HorizontalAlign.CENTER);
        menuScene.attachChild(totalScoreText); 


		
		menuOneStage_1 = new Sprite(150, 150, this.mstageOneTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
		         	//The touch down event when Stage Entry 1 is touched
		         	if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			        	if (mainGameScene!=null)
			        		mainGameScene.reset();
			    		mainGameScene = new Scene();
			    		mainGameScene.setBackground(new Background(0, 0, 1)); 		
			    		//Create the game scene for stage 1
			    		onCreateScene(mainGameScene, 1);			        	
			        	setCurrentScene(SceneType.MAINGAME);
			        }
			        return true;
			    }
		 };
		 
		menuOneStage_2 = new Sprite(300, 150, this.mstageTwoTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {	         	
		         	 //The touch down event when Stage Entry 2 is touched
			         if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN) {
			        	if (mainGameScene!=null)
			        		mainGameScene.reset();
			    		mainGameScene = new Scene();
			    		mainGameScene.setBackground(new Background(0, 0, 1));			    		
			    		//Create the game scene for stage 2
			    		onCreateScene(mainGameScene, 2);			        	
			    		setCurrentScene(SceneType.MAINGAME);
			        }
			        return true;
			    }
		 };
		 
		 //Register the touch area for Stage Entries on menu
		 menuScene.registerTouchArea(menuOneStage_1);
		 menuScene.registerTouchArea(menuOneStage_2);
		 
		 //Attach the Sprites onto menu
		 menuScene.attachChild(menuOneStage_2);
		 menuScene.attachChild(menuOneStage_1);
		
	}
	
	
	protected Scene onCreateScene(final Scene gameScene, int gameSceneStages) {
		Sprite backgroundSprite = new Sprite(0, 0, this.mBackgroundTextureRegion, activity.getVertexBufferObjectManager());
		backgroundSprite.setPosition((camera.getWidth() - backgroundSprite.getWidth()) * 0.5f, (camera.getHeight() - backgroundSprite.getHeight()) * 0.5f);
		gameScene.attachChild(backgroundSprite);
		switch (gameSceneStages){
		
		case 1:
			// 1 - Create new scene
			stageScore=1000;
			stageScoreText = new Text(600, 10, this.stageScoreFont, "score:\n" + stageScore, activity.getVertexBufferObjectManager());//, HorizontalAlign.CENTER
            stageScoreText.setHorizontalAlign(HorizontalAlign.CENTER);
			Sprite stageOneSprite = new Sprite(50, 0, this.mstageOneTextureRegion, activity.getVertexBufferObjectManager());
			gameScene.attachChild(stageOneSprite);
			// 2 - Add the towers
			mTower1 = new Sprite(192, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			mTower2 = new Sprite(400, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			mTower3 = new Sprite(604, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			gameScene.attachChild(mTower1);
			gameScene.attachChild(mTower2);
			gameScene.attachChild(mTower3);
			gameScene.attachChild(stageScoreText); 
			// 3 - Create the rings
			ring3 = new Ring(3, mTower1.getX() + mTower1.getWidth()/2 - this.mRing3.getWidth()/2, mTower1.getY() + mTower1.getHeight() - this.mRing3.getHeight(), this.mRing3, activity.getVertexBufferObjectManager()) {	
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())	
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
					return true;
				}
			};
			ring2 = new Ring(2, mTower1.getX() + mTower1.getWidth()/2 - this.mRing2.getWidth()/2, ring3.getY() - this.mRing2.getHeight(), this.mRing2, activity.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
		        return true;
		       }
			};
			ring1 = new Ring(1, mTower1.getX() + mTower1.getWidth()/2 - this.mRing1.getWidth()/2, ring2.getY() - this.mRing1.getHeight(), this.mRing1, activity.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
					return true;
				}
			};


			stageClearedSprite = new Sprite(0, 0, this.mstageClearedTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {			            
			            clearImage(gameScene);
			        }
			        return true;
			    }
		 	};
		 	gameScene.attachChild(ring1);
		 	gameScene.attachChild(ring2);
		 	gameScene.attachChild(ring3);
		 	// 	4 - Add all rings to stack one
			this.mStack1.clear();
			this.mStack2.clear();
			this.mStack3.clear();
		 	this.mStack1.add(ring3);
			this.mStack1.add(ring2);
			this.mStack1.add(ring1);
			// 	5 - Initialize starting position for each ring
			ring1.setmStack(mStack1);
			ring2.setmStack(mStack1);
			ring3.setmStack(mStack1);
			ring1.setmTower(mTower1);
			ring2.setmTower(mTower1);
			ring3.setmTower(mTower1);
			// 6 - Add touch handlers
			gameScene.registerTouchArea(ring1);
			gameScene.registerTouchArea(ring2);
			gameScene.registerTouchArea(ring3);
			gameScene.setTouchAreaBindingOnActionDownEnabled(true);
			return gameScene;
		case 2:
			// 1 - Create new scene
			stageScore=1000;
			stageScoreText = new Text(600, 10, this.stageScoreFont, "score:\n" + stageScore, activity.getVertexBufferObjectManager());//, HorizontalAlign.CENTER
            stageScoreText.setHorizontalAlign(HorizontalAlign.CENTER);
            gameScene.attachChild(stageScoreText); 
			Sprite stageTwoSprite = new Sprite(50, 0, this.mstageTwoTextureRegion, activity.getVertexBufferObjectManager());
			gameScene.attachChild(stageTwoSprite);

			// 2 - Add the towers
			mTower1 = new Sprite(192, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			mTower2 = new Sprite(400, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			mTower3 = new Sprite(604, 63, this.mTowerTextureRegion, activity.getVertexBufferObjectManager());
			gameScene.attachChild(mTower1);
			gameScene.attachChild(mTower2);
			gameScene.attachChild(mTower3);
			// 3 - Create the rings
			ring3 = new Ring(3, mTower1.getX() + mTower1.getWidth()/2 - this.mRing3.getWidth()/2, mTower1.getY() + mTower1.getHeight() - this.mRing3.getHeight(), this.mRing3, activity.getVertexBufferObjectManager()) {	
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
					return true;
				}
			};
			ring2 = new Ring(2, mTower1.getX() + mTower1.getWidth()/2 - this.mRing2.getWidth()/2, ring3.getY() - this.mRing2.getHeight(), this.mRing2, activity.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
		        return true;
		       }
			};
			ring1 = new Ring(1, mTower1.getX() + mTower1.getWidth()/2 - this.mRing1.getWidth()/2, ring2.getY() - this.mRing1.getHeight(), this.mRing1, activity.getVertexBufferObjectManager()) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (((Ring) this.getmStack().peek()).getmWeight() != this.getmWeight())
						return false;
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
						actions_for_rings_touched(this, gameScene);
					}
					return true;
				}
			};

			stageClearedSprite = new Sprite(0, 0, this.mstageClearedTextureRegion, activity.getVertexBufferObjectManager()){
			    @Override
			    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			        if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
			            clearImage(gameScene);
			        }
			        return true;
			    }
		 	};
			gameScene.attachChild(ring1);
			gameScene.attachChild(ring2);
			gameScene.attachChild(ring3);
		 // 4 - Add all rings to stack one
			this.mStack1.clear();
			this.mStack2.clear();
			this.mStack3.clear();
			this.mStack1.add(ring3);
			this.mStack1.add(ring2);
			this.mStack1.add(ring1);
		 // 5 - Initialize starting position for each ring
		 	ring1.setmStack(mStack1);
		 	ring2.setmStack(mStack1);
		 	ring3.setmStack(mStack1);
		 	ring1.setmTower(mTower1);
		 	ring2.setmTower(mTower1);
		 	ring3.setmTower(mTower1);
			// 6 - Add touch handlers
			gameScene.registerTouchArea(ring1);
			gameScene.registerTouchArea(ring2);
			gameScene.registerTouchArea(ring3);
			gameScene.setTouchAreaBindingOnActionDownEnabled(true);
			return gameScene;
		case 3:
			return gameScene;
		}
		
		//end cases
		return gameScene;
	}
	
	private void actions_for_rings_touched(Ring ring, Scene scene){
		checkForCollisionsWithTowers(ring);
		updateScore(scene);
		boolean result = checkForWinning();
		System.out.println("the result is");
		System.out.println(result);
		System.out.println("-------");
		if (result){
			showImage(scene);
		}
	}
	
	private void checkForCollisionsWithTowers(Ring ring) {
	    Stack stack = null;
	    Sprite tower = null;
	    if (ring.collidesWith(mTower1) && (mStack1.size() == 0 || ring.getmWeight() < ((Ring) mStack1.peek()).getmWeight())) {
	        stack = mStack1;
	        tower = mTower1;
	        stageScore-=50;
	    } else if (ring.collidesWith(mTower2) && (mStack2.size() == 0 || ring.getmWeight() < ((Ring) mStack2.peek()).getmWeight())) {
	        stack = mStack2;
	        tower = mTower2;
	        stageScore-=50;
	    } else if (ring.collidesWith(mTower3) && (mStack3.size() == 0 || ring.getmWeight() < ((Ring) mStack3.peek()).getmWeight())) {
	        stack = mStack3;
	        tower = mTower3;
	        stageScore-=50;
	    } else {
	        stack = ring.getmStack();
	        tower = ring.getmTower();
	    }
	    ring.getmStack().remove(ring);
	    if (stack != null && tower !=null && stack.size() == 0) {
	        ring.setPosition(tower.getX() + tower.getWidth()/2 - ring.getWidth()/2, tower.getY() + tower.getHeight() - ring.getHeight());
	    } else if (stack != null && tower !=null && stack.size() > 0) {
	        ring.setPosition(tower.getX() + tower.getWidth()/2 - ring.getWidth()/2, ((Ring) stack.peek()).getY() - ring.getHeight());
	    }
	    stack.add(ring);
	    ring.setmStack(stack);
	    ring.setmTower(tower);
	    
	}
	
	private boolean checkForWinning() {
		if (mStack3.size() == 3){
			this.mStack1.clear();
			this.mStack2.clear();
			this.mStack3.clear();
			return true;
		}
		else return false;
	}
	
	private void showImage(Scene scene){
 
		if (!stageClearedSprite.hasParent()){
			stageClearedSprite.setPosition((camera.getWidth() - stageClearedSprite.getWidth()) * 0.5f, (camera.getHeight() - stageClearedSprite.getHeight()) * 0.5f);
			scene.attachChild(stageClearedSprite);
			mainGameScene.unregisterTouchArea(ring1);
			mainGameScene.unregisterTouchArea(ring2);
			mainGameScene.unregisterTouchArea(ring3);
			mainGameScene.registerTouchArea(stageClearedSprite);
			stageClearFlag = true;

		}
		return;
	}
	private void clearImage(Scene scene){
		
		if (stageClearedSprite.hasParent()){
			scene.detachChild(stageClearedSprite);
			stageClearFlag = false;
	        updateTotalScore(menuScene);
			engine.setScene(menuScene);
		}
		return;
	}
	private void updateScore(Scene scene){
		
		if (stageScoreText.hasParent()){
			scene.detachChild(stageScoreText);
			stageScoreText.setText("score:\n" + stageScore);
            scene.attachChild(stageScoreText);
		}
		return;
	}
	private void updateTotalScore(Scene scene){
		
		if (totalScoreText.hasParent()){
			scene.detachChild(totalScoreText);
			totalScore+=stageScore;
			totalScoreText = new Text(600, 10, this.totalScoreFont, "score:\n" + totalScore, activity.getVertexBufferObjectManager());
			totalScoreText.setHorizontalAlign(HorizontalAlign.CENTER);
			totalScoreText.setText("score:\n" + totalScore);
            scene.attachChild(totalScoreText);
		}
		return;
	}
	//Method allows you to get the currently active scene
	public SceneType getCurrentScene() {
		return currentScene;
	}
	
	//Method allows you to set the currently active scene
	public void setCurrentScene(SceneType scene) {
		currentScene = scene;
		switch (scene)
		{
		case SPLASH:
			break;
		case TITLE:
			engine.setScene(titleScene);
			break;
		case MAINGAME:
			engine.setScene(mainGameScene);
			break;
		case MENU:
			engine.setScene(menuScene);
			break;
		}		
	}
		
}
