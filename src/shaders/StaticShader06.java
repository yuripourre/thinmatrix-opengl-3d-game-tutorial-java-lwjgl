package shaders;

public class StaticShader06 extends ShaderProgram {

    private static final String VERTEX_FILE = "src/shaders/vertexShader06.glsl";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader06.glsl";

    public StaticShader06() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

	@Override
	protected void getAllUniformLocations() {
		// TODO Auto-generated method stub
		
	}
}
