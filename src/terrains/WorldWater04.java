package terrains;

import java.util.ArrayList;
import java.util.List;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class WorldWater04 implements World {

	float waterHeight;
	
	List<Terrain> terrains;
	
	public WorldWater04(Loader loader, float terrainSize, float terrainMaxHeight, float waterHeight) {
		
        // *********TERRAIN TEXTURE STUFF**********

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        //TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("rockDiffuse"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mossPath256"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,
                rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
        
        terrains = new ArrayList<Terrain>();
        
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
        		Terrain terrain = new TerrainWater04(x, z, terrainSize, terrainMaxHeight, loader, texturePack, blendMap, "heightmap");
        		terrains.add(terrain);
            }
        }
        
        System.out.println("World: generated " + terrains.size() + " terrains.");
        
        this.waterHeight = waterHeight;
	}
	
	public float getHeightOfTerrain(float worldX, float worldZ) {
		float height = 0;
		Terrain terrain = null;

		// find which terrain we are standing on
		// this could be optimized with a hash table
		for (int i = 0; i < terrains.size(); i++) {
			Terrain t = terrains.get(i);
			if (t.containsPosition(worldX, worldZ)) {
				terrain = t;
				//System.out.println("getHeightOfTerrain: i = " + i);
				break;
			}
		}
		
		// if we got a terrain, get terrain height
		if (terrain != null) {
			height = terrain.getHeightOfTerrain(worldX, worldZ);
		}
		
		//System.out.println("World: getHeightOfTerrain: (" + worldX + ", " + worldZ + "), height " + height);
		
		return height;
	}
	
	public float getHeightOfWater(float worldX, float worldZ) {
		return waterHeight;
	}
	
	public List<Terrain> getTerrains() {
		return terrains;
	}
}