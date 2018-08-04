package amyGLGraphics;

public class GLLightRenderer extends GLRenderer {

	GLWorldProgram lightProgram;
	
	@Override
	protected void createProgram() {
		lightProgram = new GLWorldProgram();
	}

	@Override
	protected void updateUniversalUniforms() {
		if (camera != null) {
			lightProgram.updateViewMatrix(camera.getCameraMatrix());
		}
	}

	@Override
	protected void updateUniforms(GLObject object) {
		lightProgram.updateModelMatrix(object.getModelMatrix());
	}

	@Override
	protected GLProgram getProgram() {
		return lightProgram;
	}

	@Override
	protected void globalSetup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void resetGlobal() {
		// TODO Auto-generated method stub
		
	}

}
