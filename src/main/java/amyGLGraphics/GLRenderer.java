package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL20;

public abstract class GLRenderer {
	
	protected GLCamera camera;
	
	public GLRenderer() {
		createProgram();
		resetState();
	}
	
	public void render(List<GLObject> objects) {
		globalSetup();
		updateUniversalUniforms();
		for (GLObject object : objects) {
			renderObject(object);
		}
		resetGlobal();
	}
	
	public void render(GLObject object) {
		globalSetup();
		updateUniversalUniforms();
		renderObject(object);
		resetGlobal();
	}
	
	protected void renderObject(GLObject object) {
		GLProgram program = getProgram();
		Map<GLTexture, Integer> textures = object.getTextures();
		int objectID = object.getObjectID();
		int objectIndicesID = object.getObjectIndicesBufferID();
		int programID = program.getProgramID();
		updateUniforms(object);
		GL20.glUseProgram(programID);
		for (GLTexture texture : textures.keySet()) {
			int target = textures.get(texture);
			glActiveTexture(target);
			glBindTexture(texture.getTextureType(), texture.getTextureID());
		}
		glBindVertexArray(objectID);
		for (int attrib : object.getAttributePointers()) {
			glEnableVertexAttribArray(attrib);
		}
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, object.getDrawLength(), GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		for (int attrib : object.getAttributePointers()) {
			glDisableVertexAttribArray(attrib);
		}
		for (GLTexture texture : textures.keySet()) {
			int target = textures.get(texture);
			glActiveTexture(target);
			glBindTexture(texture.getTextureType(), 0);
		}
		glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	public void resetState() {
		unbindGL();
		getProgram().createProgram();
	}
	
	public void unbindGL() {
		if (getProgram() != null && getProgram().isGLBound()) {
			getProgram().unbindProgram();
		}
	}
	
	public void setCamera(GLCamera camera) {
		this.camera = camera;
	}
	
	protected abstract void createProgram();
	
	protected abstract void updateUniversalUniforms();
	
	protected abstract void updateUniforms(GLObject object);
	
	protected abstract GLProgram getProgram();
	
	protected abstract void globalSetup();
	
	protected abstract void resetGlobal();
	
}
