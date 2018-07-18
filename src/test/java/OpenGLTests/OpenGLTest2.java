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
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
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
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import amyGLGraphics.GLGraphicsHandler;
import amyGLGraphics.GLRoomRenderer;
import amyGLGraphicsIO.ShaderLoader;
import movement.Room;

public class OpenGLTest2 {
	private static final int ROOMWIDTH = 1600;
	private static final int ROOMHEIGHT = 900;
	
	private static final float CAMERASPEED = 0.1f;
	
	private float lastX = ROOMWIDTH / 2;
	private float lastY = ROOMHEIGHT / 2;
	
	private float pitch;
	private float yaw;
	
	private long window;
	private GLRoomRenderer renderer;
	private CommunismRoom communismRoom;
	public void run() {
		init();
		loop();
		end();
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
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		window = glfwCreateWindow(ROOMWIDTH, ROOMHEIGHT, "Hello World!", NULL, NULL);
		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		});
		GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			mouseInput(xpos, ypos);
		});
		GLFW.glfwSetMouseButtonCallback(window, (long window, int button, int action, int mods) -> {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE)
				createSquare();
		});
		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
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
		glEnable(GL11.GL_DEPTH_TEST);
		renderer = new GLRoomRenderer();
		renderer.renderNormals = true;
		communismRoom = new CommunismRoom();
		renderer.setRoom(communismRoom);
	}

	private void loop() {

		// Set the clear color
		glClearColor(0.3f, 0.3f, 0.3f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			 // swap the color buffers
			render();
			glfwSwapBuffers(window);
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			processInput();
		}
	}
	private void render() {
		communismRoom.tick();
		renderer.renderRoom();
	}
	
	private void processInput() {
		Vector3f cameraPosition = renderer.camera.getPosition();
		Vector3f cameraFront = renderer.camera.getCentre();
		Vector3f cameraUp = renderer.camera.getUp();
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.mul(CAMERASPEED, result);
			cameraPosition.add(result);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.mul(CAMERASPEED, result);
			cameraPosition.sub(result);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.cross(cameraUp, result);
			result.normalize();
			result.mul(CAMERASPEED);
			cameraPosition.sub(result);
		}
		if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.cross(cameraUp, result);
			result.normalize();
			result.mul(CAMERASPEED);
			cameraPosition.add(result);
		}
		renderer.camera.setPosition(cameraPosition);
	}
	
	private void mouseInput(double xpos, double ypos) {
		float xoffset = (float) (xpos - lastX);
		float yoffset = (float) (lastY - ypos);
		lastX = (float) xpos;
		lastY = (float) ypos;

		float sensitivity = 0.05f;
		xoffset *= sensitivity;
		yoffset *= sensitivity;
		
		yaw   += xoffset;
		pitch += yoffset; 
		
		if (pitch > 89) {
			pitch = 89.0f;
		} else if (pitch < -89) {
			pitch = -89.0f;
		}
		
		float rpitch = (float) Math.toRadians(pitch);
		float ryaw = (float) Math.toRadians(yaw);
		
		Vector3f cameraFront = new Vector3f();
		cameraFront.x = (float) (Math.cos(rpitch) * Math.cos(ryaw));
		cameraFront.y = (float) (Math.sin(rpitch));
		cameraFront.z = (float) (Math.cos(rpitch) * Math.sin(ryaw));
		cameraFront = cameraFront.normalize();
		
		renderer.camera.setCentre(cameraFront);
	}
	
	private void createSquare() {
		var position = renderer.camera.getPosition();
		
		double x = position.x * GLGraphicsHandler.viewWidth;
		double y = position.y * GLGraphicsHandler.viewHeight;
		double z = position.z * GLGraphicsHandler.viewDepth;
		
		communismRoom.addSquare(x, y, z);
	}
	
	public static void main(String[] args) {
		new OpenGLTest2().run();
	}
	
	public void end() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		renderer.resetState();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		System.exit(0);
	}
}
