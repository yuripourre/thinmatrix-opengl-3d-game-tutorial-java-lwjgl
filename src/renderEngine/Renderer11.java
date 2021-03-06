package renderEngine;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader11;
import toolbox.Maths;

public class Renderer11 {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;
	
	private Matrix4f projectionMatrix;
	
	public Renderer11(StaticShader11 shader) {
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

    public void prepare() {
    	GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }
	
    public void render(Entity entity, StaticShader11 shader) {
    	TexturedModel model = entity.getModel();
    	RawModel rawModel = model.getRawModel();
    	GL30.glBindVertexArray(rawModel.getVaoID());
    	GL20.glEnableVertexAttribArray(0); // position
    	GL20.glEnableVertexAttribArray(1); // textureCoordinates
    	GL20.glEnableVertexAttribArray(2); // normal
    	
    	Vector3f translation = entity.getPosition();
    	float rx = entity.getRotX();
    	float ry = entity.getRotY();
    	float rz = entity.getRotZ();
    	float scale = entity.getScale();
    	
    	Matrix4f transformationMatrix = Maths.createTransformationMatrix(
    			translation, rx, ry, rz, scale);
    	shader.loadTransformationMatrix(transformationMatrix);
    	
    	GL13.glActiveTexture(GL13.GL_TEXTURE0);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    	GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    	GL20.glDisableVertexAttribArray(0);
    	GL20.glDisableVertexAttribArray(1);
    	GL20.glDisableVertexAttribArray(2);
    	GL30.glBindVertexArray(0);
    }
    
    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public float getNearPlane() {
        return NEAR_PLANE;
    }

    public float getFarPlane() {
        return FAR_PLANE;
    }
}
