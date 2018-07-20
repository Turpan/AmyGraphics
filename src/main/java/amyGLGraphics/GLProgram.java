package amyGLGraphics;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public abstract class GLProgram {
	private GLShader vertexShader = new GLShader();
	private GLShader geometryShader = new GLShader();
	private GLShader fragmentShader = new GLShader();
	private int programID;
	private boolean GLBound;
	
	public GLProgram() {
		
	}
	
	protected void createProgram() {
		GLBound = true;
		programID = glCreateProgram();
		//glBindAttribLocation(programID, GLRoomRenderer.VERTEXPOSITION, "in_Position");
		//glBindAttribLocation(programID, GLRoomRenderer.COLOURPOSITION, "in_Color");
		//glBindAttribLocation(programID, GLRoomRenderer.TEXTUREPOSITION, "in_TextureCoord");
		createShaders();
	}
	
	protected abstract void createShaders();
	
	protected void linkProgram() {
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	
	private void checkLink() {
		if (vertexShader.isGLBound() && fragmentShader.isGLBound()) {
			linkProgram();
		}
	}
	
	protected void createVertexShader(String fileLocation) {
		vertexShader = new GLShader(fileLocation, GL_VERTEX_SHADER);
		glAttachShader(programID, getVertexShaderID());
		checkLink();
	}
	
	protected void createGeometryShader(String fileLocation) {
		geometryShader = new GLShader(fileLocation, GL_GEOMETRY_SHADER);
		glAttachShader(programID, getGeometryShaderID());
		checkLink();
	}
	
	protected void createFragmentShader(String fileLocation) {
		fragmentShader = new GLShader(fileLocation, GL_FRAGMENT_SHADER);
		glAttachShader(programID, getFragmentShaderID());
		checkLink();
	}
	
	public void unbindProgram() {
		if (vertexShader.isGLBound()) {
			int vertexShaderID = getVertexShaderID();
			glDetachShader(programID, vertexShaderID);
			vertexShader.unbindShader();
		}
		if (geometryShader.isGLBound()) {
			int geometryShaderID = getGeometryShaderID();
			glDetachShader(programID, geometryShaderID);
			geometryShader.unbindShader();
		}
		if (fragmentShader.isGLBound()) {
			int fragmentShaderID = getFragmentShaderID();
			glDetachShader(programID, fragmentShaderID);
			fragmentShader.unbindShader();
		}
		glDeleteProgram(programID);
		GLBound = false;
	}
	
	public int getProgramID() {
		return programID;
	}
	
	public int getVertexShaderID() {
		return vertexShader.getShaderID();
	}
	
	public int getGeometryShaderID() {
		return geometryShader.getShaderID();
	}
	
	public int getFragmentShaderID() {
		return fragmentShader.getShaderID();
	}
	
	protected void updateMat4(Matrix4f matrix, int location) {
		GL20.glUseProgram(programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.get(buffer);
		GL20.glUniformMatrix4fv(location, false, buffer);
		GL20.glUseProgram(0);
	}
	
	protected void updateVec3(Vector3f vector, int location) {
		GL20.glUseProgram(programID);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
		vector.get(buffer);
		GL20.glUniform3fv(location, buffer);
		GL20.glUseProgram(0);
	}
	
	protected int queryVariable(String varName) {
		return GL20.glGetUniformLocation(programID, varName);
	}
	
	public boolean isGLBound() {
		return GLBound;
	}
}
