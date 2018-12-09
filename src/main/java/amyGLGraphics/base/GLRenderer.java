package amyGLGraphics.base;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
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
import org.lwjgl.opengl.GL30;

import amyGLGraphics.GLTexture;

public abstract class GLRenderer {

	protected GLCamera camera;

	public GLRenderer() {
		createProgram();
		getProgram().createProgram();
	}
	
	public void render(List<GLObject> objects, GLFrameBuffer buffer) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.getBufferID());
		render(objects);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public void render(GLObject object, GLFrameBuffer buffer) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, buffer.getBufferID());
		render(object);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
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
		int objectID = object.getObjectID();
		int objectIndicesID = object.getObjectIndicesBufferID();
		int programID = program.getProgramID();
		updateUniforms(object);
		GL20.glUseProgram(programID);
		bindTextures(object);
		glBindVertexArray(objectID);
		for (int attrib : object.getAttributePointers()) {
			glEnableVertexAttribArray(attrib);
		}
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, object.getDrawLength(), GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		for (int attrib : object.getAttributePointers()) {
			glDisableVertexAttribArray(attrib);
		}
		unbindTextures(object);
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

	protected void bindTextures(GLObject object) {
		Map<GLTexture, Integer> textures = object.getTextures();
		for (GLTexture texture : textures.keySet()) {
			int target = textures.get(texture);
			glActiveTexture(target);
			glBindTexture(texture.getTextureType(), texture.getTextureID());
		}
	}

	protected void unbindTextures(GLObject object) {
		Map<GLTexture, Integer> textures = object.getTextures();
		for (GLTexture texture : textures.keySet()) {
			int target = textures.get(texture);
			glActiveTexture(target);
			glBindTexture(texture.getTextureType(), 0);
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
