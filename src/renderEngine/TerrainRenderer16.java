package renderEngine;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import shaders.TerrainShader16;
import terrains.Terrain14;
import textures.ModelTexture;
import toolbox.Maths;

public class TerrainRenderer16 {

    private TerrainShader16 shader;

    public TerrainRenderer16(TerrainShader16 shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(List<Terrain14> terrains) {
        for (Terrain14 terrain:terrains) {
        	prepareTerrain(terrain);
        	loadModelMatrix(terrain);
        	int vertexCount = terrain.getModel().getVertexCount();
			//GL11.glDrawElements(GL11.GL_LINE_LOOP, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        	GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
        	unbindTerrain();
        }
    }

    public void prepareTerrain(Terrain14 terrain) {
    	RawModel rawModel = terrain.getModel();
    	GL30.glBindVertexArray(rawModel.getVaoID());
    	GL20.glEnableVertexAttribArray(0); // position
    	GL20.glEnableVertexAttribArray(1); // textureCoordinates
    	GL20.glEnableVertexAttribArray(2); // normal
    	ModelTexture texture = terrain.getTexture();
    	shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
    }
	
    // unbindTexturedModel
    public void unbindTerrain() {
    	GL20.glDisableVertexAttribArray(0);
    	GL20.glDisableVertexAttribArray(1);
    	GL20.glDisableVertexAttribArray(2);
    	GL30.glBindVertexArray(0);
    }
    
    private void loadModelMatrix(Terrain14 terrain) {
    	Vector3f translation = terrain.getPosition();
    	float rx = 0; //terrain.getRotX();
    	float ry = 0; //terrain.getRotY();
    	float rz = 0; //terrain.getRotZ();
    	float scale = 1;
    	
    	Matrix4f transformationMatrix = Maths.createTransformationMatrix(
    			translation, rx, ry, rz, scale);
    	shader.loadTransformationMatrix(transformationMatrix);
    }
}
