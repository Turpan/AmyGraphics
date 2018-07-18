package OpenGLTests;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import amyGLGraphicsIO.DecodedPNG;
import amyGLGraphicsIO.ShaderLoader;

public class OpenGLTest {
	/*static final float g_vertex_buffer_data[] = {
			   -1.0f, -1.0f, 0.0f,
			   1.0f, -1.0f, 0.0f,
			   0.0f,  1.0f, 0.0f,
			};*/
	static final float[] vertexs = {
			-0.25f, 0.25f,
            -0.25f, -0.25f,
            0.25f, -0.25f,
            0.25f, 0.25f,
	};
	static final float[] colors = {
	        1f, 0f, 0f, 0f,
	        0f, 1f, 0f, 0f,
	        0f, 0f, 1f, 0f,
	        1f, 1f, 1f, 0f
	};
	static final float[] texturecoords = {
		0.0f, 0.0f,
		0.0f, 1.0f,
		1.0f, 1.0f,
		1.0f, 0.0f
	};
	static final byte[] indices = {
            0, 1, 2,
            2, 3, 0
    };
	static final byte[] texture = new DecodedPNG(new TestSquare().getTexture().getSprite()).getData();
	private long window;
	private int vaoID;
	private int triangleBufferID;
	private int colorsBufferID;
	private int indicesBufferID;
	private int texturecoordBufferID;
	private int textureBufferID;
	private int triangleBufferLocation = 0;
	private int colorBufferLocation = 1;
	private int texturecoordsBufferLocation = 2;
	private int textureBufferLocation = GL_TEXTURE0;
	private int vertexShaderID;
	private int fragmentShaderID;
	private int programID;
	public void run() {
		init();
		loop();
		// Delete the shaders
		glUseProgram(0);
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		glDeleteProgram(programID);
		// Delete the color VBO
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(indicesBufferID);
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		System.exit(0);
	}
	private void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		window = glfwCreateWindow(600, 600, "Hello World!", NULL, NULL);
		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		GLUtil.setupDebugMessageCallback();
		//glEnable(GL_ALPHA);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable( GL_BLEND );
		createVertexArray();
		createTriangleBuffer();
		createColorBuffer();
		createOrderBuffer();
		createTextureBuffer();
		createTexture();
		createProgram();
		glBindVertexArray(0);
	}
	private void createProgram() {
		vertexShaderID = loadShader("shaders/vertex.glsl", GL_VERTEX_SHADER);
		fragmentShaderID = loadShader("shaders/fragment.glsl", GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		glBindAttribLocation(programID, triangleBufferLocation, "in_Position");
		glBindAttribLocation(programID, colorBufferLocation, "in_Color");
		glBindAttribLocation(programID, texturecoordsBufferLocation, "in_TextureCoord");
		glLinkProgram(programID);
		glValidateProgram(programID);
	}
	private void createTexture() {
		int width = 50;
		int height = 50;
		ByteBuffer buffer = BufferUtils.createByteBuffer(texture.length);
		buffer.put(texture);
		buffer.flip();
		textureBufferID = glGenTextures();
		glActiveTexture(textureBufferLocation);
		glBindTexture(GL_TEXTURE_2D, textureBufferID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
	}
	private void createVertexArray() {
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
	}
	private void createTriangleBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(vertexs.length);
		buffer.put(vertexs);
		buffer.flip();
		triangleBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, triangleBufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(triangleBufferLocation, 2, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	private void createOrderBuffer() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(indices.length);
		buffer.put(indices);
		buffer.flip();
		indicesBufferID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	private void createColorBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(colors.length);
		buffer.put(colors);
		buffer.flip();
		colorsBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorsBufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(colorBufferLocation, 4, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	private void createTextureBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(texturecoords.length);
		buffer.put(texturecoords);
		buffer.flip();
		texturecoordBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, texturecoordBufferID);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		glVertexAttribPointer(texturecoordsBufferLocation, 2, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	private int loadShader(String fileName, int type) {
		String shader = ShaderLoader.loadFile(fileName);
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shader);
		glCompileShader(shaderID);
		return shaderID;
	}
	private void loop() {

		// Set the clear color
		glClearColor(0.3f, 0.3f, 0.3f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			 // swap the color buffers
			render();
			glfwSwapBuffers(window);
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	private void render() {
		glUseProgram(programID);
		glActiveTexture(textureBufferLocation);
		glBindTexture(GL_TEXTURE_2D, textureBufferID);
		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(triangleBufferLocation);
		glEnableVertexAttribArray(colorBufferLocation);
		glEnableVertexAttribArray(texturecoordsBufferLocation);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_BYTE, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(triangleBufferLocation);
		glDisableVertexAttribArray(colorBufferLocation);
		glDisableVertexAttribArray(texturecoordsBufferLocation);
		glBindVertexArray(0);
		glUseProgram(0);
	}
	public static void main(String[] args) {
		new OpenGLTest().run();
	}
}
