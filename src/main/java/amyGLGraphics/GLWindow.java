package amyGLGraphics;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
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
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import amyGLGraphics.IO.GraphicsNotifier;
import movement.Room;

public abstract class GLWindow implements Runnable {
	
	private static int windowWidth;
	private static int windowHeight;
	
	private long window;
	protected GLGraphicsHandler graphicsContext;
	protected GLCamera camera;
	
	protected GraphicsNotifier handler;
	
	private boolean errorCallbackCreated;
	
	public GLWindow(int width, int height, GraphicsNotifier handler) {
		setWindowWidth(width);
		setWindowHeight(height);
		
		camera = new GLCamera();
		
		this.handler = handler;
	}

	@Override
	public void run() {
		init();
		loop();
		end();
	}
	
	private void init() {
		//Uncomment this if you want error reporting
		//GLFWErrorCallback.createPrint(System.err).set();
		//errorCallbackCreated = true;
		
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		glfwDefaultWindowHints();
		setupWindowHints();
		window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL);
		if ( window == NULL ) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		setupInputCallback();
		
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
		}
		
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
		
		GL.createCapabilities();
		//Uncomment this for error callback
		//GLUtil.setupDebugMessageCallback();
		
		setupGLSettings();
		
		graphicsContext = new GLGraphicsHandler();
		graphicsContext.setCamera(camera);
		
		if (handler != null) {
			handler.graphicsCreated();
		}
	}
	
	private void loop() {
		glClearColor(0.3f, 0.3f, 0.3f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			 // swap the buffers
			graphicsContext.render();
			glfwSwapBuffers(window);
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			processInput();
		}
	}
	
	private void end() {
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		graphicsContext.unbindGL();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		if (errorCallbackCreated) {
			glfwSetErrorCallback(null).free();
		}
		System.exit(0);
	}
	
	protected abstract void setupWindowHints();
	
	protected abstract void setupInputCallback();
	
	protected abstract void setupGLSettings();
	
	protected abstract void processInput();
	
	public long getWindow() {
		return window;
	}
	
	public void setRoom(Room room) {
		graphicsContext.setRoom(room);
	}
	
	public void setWindowWidth(int width) {
		GLWindow.windowWidth = width;
	}
	
	public void setWindowHeight(int height) {
		GLWindow.windowHeight = height;
	}
	
	public static int getWindowWidth() {
		return windowWidth;
	}
	
	public static int getWindowHeight() {
		return windowHeight;
	}
}
