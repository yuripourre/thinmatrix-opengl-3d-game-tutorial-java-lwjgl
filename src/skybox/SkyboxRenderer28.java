package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;

public class SkyboxRenderer28 {

    private static final float SIZE = 50000f;

    private static final float[] VERTICES = {
        -SIZE, SIZE, -SIZE,
        -SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,

        -SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, -SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, -SIZE, SIZE,

        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,

        -SIZE, -SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        SIZE, -SIZE, SIZE,
        -SIZE, -SIZE, SIZE,

        -SIZE, SIZE, -SIZE,
        SIZE, SIZE, -SIZE,
        SIZE, SIZE, SIZE,
        SIZE, SIZE, SIZE,
        -SIZE, SIZE, SIZE,
        -SIZE, SIZE, -SIZE,

        -SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE, SIZE,
        SIZE, -SIZE, SIZE
    };

    private static String[] TEXTURE_FILES = {
        "skyRight", "skyLeft", "skyTop", "skyBottom", "skyBack", "skyFront"
    };

//    private static String[] NIGHT_TEXTURE_FILES = {
//        "nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront"
//    };

    private RawModel cube;
    private int texture;
    //private int nightTexture;
    private SkyboxShader28 shader;
    private float time = 0;

    public SkyboxRenderer28(Loader loader, Matrix4f projectionMatrix) {
        cube = loader.loadToVAO(VERTICES, 3);
        texture = loader.loadCubeMap(TEXTURE_FILES);
        //nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES);
        shader = new SkyboxShader28();
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.loadLimits(0f, SIZE);
        shader.stop();
    }

    public void render(Camera camera, Vector3f skyColor) {
    	render(camera, skyColor.x, skyColor.y, skyColor.z);
    }
    
    public void render(Camera camera, float r, float g, float b) {
        shader.start();
        shader.loadViewMatrix(camera);
        shader.loadSkyColor(r, g, b);
        GL30.glBindVertexArray(cube.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        bindTextures();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void bindTextures() {
        time += DisplayManager.getFrameTimeSeconds() * 1000; // 100 instead of 1000 to slow down a bit
        time %= 24000;
        time = 12000;
        int texture1;
        int texture2;
        float blendFactor;
        if (time >= 0 && time < 5000) {
            texture1 = texture; // was nightTexture
            texture2 = texture; // was nightTexture
            blendFactor = (time - 0) / (5000 - 0);
        } else if (time >= 5000 && time < 8000) {
            texture1 = texture; // was nightTexture
            texture2 = texture;
            blendFactor = (time - 5000) / (8000 - 5000);
        } else if (time >= 8000 && time < 21000) {
            texture1 = texture;
            texture2 = texture;
            blendFactor = (time - 8000) / (21000 - 8000);
        } else {
            texture1 = texture;
            texture2 = texture; // was nightTexture
            blendFactor = (time - 21000) / (24000 - 21000);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2);
        shader.loadBlendFactor(blendFactor);
    }
}