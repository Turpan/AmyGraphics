package amyGLGraphics;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;

public class GLNormalRenderer {
	
	private GLWorldProgram normalProgram = new GLNormalProgram();
	
	public GLNormalRenderer() {
		resetState();
	}
	
	public void renderNormal(GLEntity entity) {
		int objectID = entity.getObjectID();
		int objectIndicesID = entity.getObjectIndicesBufferID();
		int programID = normalProgram.getProgramID();
		normalProgram.updateModelMatrix(entity.getModelMatrix());
		GL20.glUseProgram(programID);
		glBindVertexArray(objectID);
		glEnableVertexAttribArray(GLRoomRenderer.VERTEXPOSITION);
		glEnableVertexAttribArray(GLRoomRenderer.NORMALPOSITION);
		glEnableVertexAttribArray(GLRoomRenderer.COLOURPOSITION);
		glEnableVertexAttribArray(GLRoomRenderer.TEXTUREPOSITION);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, objectIndicesID);
		glDrawElements(GL_TRIANGLES, entity.getDrawLength(), GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(GLRoomRenderer.VERTEXPOSITION);
		glDisableVertexAttribArray(GLRoomRenderer.NORMALPOSITION);
		glDisableVertexAttribArray(GLRoomRenderer.COLOURPOSITION);
		glDisableVertexAttribArray(GLRoomRenderer.TEXTUREPOSITION);
		glBindVertexArray(0);
		GL20.glUseProgram(0);
	}
	
	public void renderNormals(List<GLEntity> entitys) {
		for (var entity : entitys) {
			renderNormal(entity);
		}
	}
	
	public void updateMatrix(Matrix4f view) {
		normalProgram.updateViewMatrix(view);
	}
	
	public void resetState() {
		unbindOpenGL();
		normalProgram.createProgram();
	}
	
	public void unbindOpenGL() {
		if (normalProgram.isGLBound()) {
			normalProgram.unbindProgram();
		}
	}
}
