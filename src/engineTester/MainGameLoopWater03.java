package engineTester;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Camera18;
import entities.Camera19;
import entities.Entity;
import entities.Light;
import entities.Player19;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRendererWater03;
import skybox.Sky;
import terrains.Terrain;
import terrains.Terrain22;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer01;
import water.WaterShader01;
import water.WaterTile;
import water.WaterTile01;

// OpenGL Water Tutorial 3: Clipping Planes
// https://www.youtube.com/watch?v=0NH9k4zTAqk&list=PLRIWtICgwaX23jiqVByUs0bqhnalNTNZh&index=3

public class MainGameLoopWater03
{
	public static String title = "OpenGL Water Tutorial 3: Clipping Planes";
	public static String subTitle = "Use keys w, a, s, d to move player, use mouse to control camera";
	public static String subSubTitle = "Use key c to swap to second camera, move it with arrow keys";
	
    public static void main(String[] args) {
    	DisplayManager.createDisplay(title + ": " + subTitle);
        Loader loader = new Loader();

        TextMaster.init(loader);
        FontType font = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
        GUIText text = new GUIText(title, 2.5f, font, new Vector2f(0.0f, 0.1f), 1.0f, true);
        text.setColor(0.2f, 0.2f, 0.8f);
        FontType font2 = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
        GUIText text2 = new GUIText(subTitle, 2, font2, new Vector2f(0.0f, 0.2f), 1.0f, true);
        text2.setColor(0.8f, 0.2f, 0.2f);
        FontType font3 = new FontType(loader.loadFontTextureAtlas("candara"), new File("res/fonts/candara.fnt"));
        GUIText text3 = new GUIText(subSubTitle, 1.5f, font3, new Vector2f(0.0f, 0.3f), 1.0f, true);
        text3.setColor(0.4f, 0.8f, 0.8f);
        
        // *********TERRAIN TEXTURE STUFF**********

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mossPath256"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,
                rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        // *****************************************

        TexturedModel treeModel = loader.createTexturedModel("tree", "tree", 1, 0);
        TexturedModel lowPolyTreeModel = loader.createTexturedModel("lowPolyTree", "lowPolyTree", 1, 0);
        TexturedModel grassModel = loader.createTexturedModel("grassModel", "grassTexture", 1, 0, true, true);
        TexturedModel flowerModel = loader.createTexturedModel("grassModel", "flower", 1, 0, true, true);
        TexturedModel fernModel = loader.createTexturedModel("fern", "fern1", 1, 0, true, false);
        TexturedModel rocksModel = loader.createTexturedModel("rocks", "rocks", 1, 0);
        
        List<Entity> entities = new ArrayList<>();
        Random random = new Random(676452);
        
        for (int i = 0; i < 400; i++) {
        	
        	float x = 0, y = 0, z = 0, rx = 0, ry = 0, rz = 0, scale = 1;
        	
        	if (i % 7 == 0) {
                // grass
                x = random.nextFloat() * 400 - 200;
            	z = random.nextFloat() * -400;
                rx = 0;
                ry = random.nextFloat() * 360;
                rz = 0;
                scale = 1.8f;
                entities.add(new Entity(grassModel, new Vector3f(x, y, z), rx, ry, rz, scale));

                // flower
                x = random.nextFloat() * 400 - 200;
            	z = random.nextFloat() * -400;
                rx = 0;
                ry = random.nextFloat() * 360;
                rz = 0;
                scale = 2.3f;
                entities.add(new Entity(flowerModel, new Vector3f(x, y, z), rx, ry, rz, scale));
        	}

        	if (i % 3 == 0) {
	            // fern
	            x = random.nextFloat() * 400 - 400;
	        	z = random.nextFloat() * -400;
	            rx = 10 * random.nextFloat() - 5;
	            ry = random.nextFloat() * 360;
	            rz = 10 * random.nextFloat() - 5;
	            scale = 0.9f;
	            entities.add(new Entity(fernModel, new Vector3f(x, y, z), rx, ry, rz, scale));
	
	            // low poly tree "bobble"
	        	x = random.nextFloat() * 800 - 400;
	        	y = 0; 
	        	z = random.nextFloat() * -600; 
	            rx = 4 * random.nextFloat() - 2;
	            ry = random.nextFloat() * 360;
	            rz = 4 * random.nextFloat() - 2;
	            scale = random.nextFloat() * 0.1f + 0.6f;
	            entities.add(new Entity(lowPolyTreeModel, new Vector3f(x, y, z), rx, ry, rz, scale));
	
	        	// tree
	        	x = random.nextFloat() * 800 - 400;
	        	y = 0; 
	        	z = random.nextFloat() * -600; 
	            rx = 4 * random.nextFloat() - 2;
	            ry = random.nextFloat() * 360;
	            rz = 4 * random.nextFloat() - 2;
	            scale = random.nextFloat() * 1f + 4f;
	            entities.add(new Entity(treeModel, new Vector3f(x, y, z), rx, ry, rz, scale));
        	}
        }
        entities.add(new Entity(rocksModel, new Vector3f(0, 0, 0), 0, 0, 0, 75));
        
        TexturedModel playerModel = loader.createTexturedModel("person", "playerTexture", 1, 0);
        Player19 player = new Player19(playerModel, new Vector3f(0, 0, -50), 0, 180, 0, 0.6f);
        entities.add(player);
        
        Camera19 camera1 = new Camera19(player);
        camera1.getPosition().translate(0, 20, 0);

        Camera18 camera2 = new Camera18();
        camera2.getPosition().translate(0, 30, 0);
        
        Camera camera = camera1;

        Light light = new Light(
        		new Vector3f(20000, 40000, 20000),
        		new Vector3f(1f, 1f, 1f)); // white light
        
        List<Light> lights = new ArrayList<Light>();
        lights.add(light);
        
        //ModelTexture terrainModelTexture = new ModelTexture(loader.loadTexture("grass"));
        
        List<Terrain> terrains = new ArrayList<Terrain>();
		Terrain terrain1 = new Terrain22(0, 0, loader, texturePack, blendMap, "heightmap");
		Terrain terrain2 = new Terrain22(-1, 0, loader, texturePack, blendMap, "heightmap");
		Terrain terrain3 = new Terrain22(-1, -1, loader, texturePack, blendMap, "heightmap");
		Terrain terrain4 = new Terrain22(0, -1, loader, texturePack, blendMap, "heightmap");
		terrains.add(terrain1);
		terrains.add(terrain2);
		terrains.add(terrain3);
		terrains.add(terrain4);

        MasterRendererWater03 renderer = new MasterRendererWater03();
        
        int i = 0;
        int cameraFrames = 0;
        
        // Water
        
        WaterShader01 waterShader = new WaterShader01();
        WaterRenderer01 waterRenderer = new WaterRenderer01(loader, waterShader, renderer.getProjectionMatrix());
        List<WaterTile> waters = new ArrayList<>();
        WaterTile water = new WaterTile01(0, -150, 1);
        waters.add(water);
        
        WaterFrameBuffers buffers = new WaterFrameBuffers();
        
        List<GuiTexture> guiTextures = new ArrayList<>();
        GuiTexture refrGui = new GuiTexture(buffers.getRefractionTexture(), new Vector2f( 0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
        GuiTexture reflGui = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.8f, -0.8f), new Vector2f(0.2f, 0.2f));
        //GuiTexture reflGui = new GuiTexture(buffers.getReflectionTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.4f, 0.4f));
        guiTextures.add(refrGui);
        guiTextures.add(reflGui);
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        Sky sky = new Sky(0.6f, 0.7f, 0.8f, 0.0035f, 5f);

        //****************Game Loop Below*********************
        
        while (!Display.isCloseRequested()) {
        	player.move();
        	
        	cameraFrames++;
        	// key C used to swap camera
        	if (cameraFrames > 10 && Keyboard.isKeyDown(Keyboard.KEY_C)) {
        		if (camera == camera1) {
            		camera = camera2;
            	}
            	else if (camera == camera2) {
        			camera = camera1;
            	}
        		cameraFrames = 0;
        	}
        	
        	camera.move();
        	
        	//camera2.getPosition().translate(0, 0, -0.02f);
        	
        	GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        	// render to reflection texture: set the clip plane to clip stuff above water
        	buffers.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            // change position and pitch of camera to render the reflection 
            camera.getPosition().y -= distance;
            camera.invertPitch();
        	renderer.renderScene(entities, terrains, lights, sky, camera, new Vector4f(0, 1, 0, -water.getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

        	// render to refraction texture: set the clip plane to clip stuff below water
        	buffers.bindRefractionFrameBuffer();
        	renderer.renderScene(entities, terrains, lights, sky, camera, new Vector4f(0, -1, 0, water.getHeight()));
        	
        	// render to screen: set the clip plane at a great height, so it won't clip anything
        	buffers.unbindCurrentFrameBuffer();
        	renderer.renderScene(entities, terrains, lights, sky, camera, new Vector4f(0, -1, 0, 1000000));

        	waterRenderer.render(waters, sky, camera);
        	guiRenderer.render(guiTextures);

        	TextMaster.render();
            
        	DisplayManager.updateDisplay();
            
            if ((i % 60) == 0) {
            	camera.printPosition();
            }
            i++;
        }

        buffers.cleanUp();
        waterShader.cleanUp();
        TextMaster.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
