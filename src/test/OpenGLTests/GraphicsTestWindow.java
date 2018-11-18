package OpenGLTests;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import amyGLGraphics.GLWindow;
import amyGLGraphics.IO.ButtonState;
import amyGLGraphics.IO.GraphicsNotifier;

public class GraphicsTestWindow extends GLWindow {
	
	//Change this to determine how fast the camera moves
	//Note that this is in screen space coordinates, not model space
	private static final float CAMERASPEED = 0.1f;
	
	private float lastX = width / 2;
	private float lastY = height / 2;
	
	private float pitch;
	private float yaw;
	
	//Change these to update window size
	
	//Note that window size does not at all change the mapping of object size to screen space
	//Change the values in GLGraphicsHandler to do that
	private static final int width = 1600;
	private static final int height = 900;
	
	//this is the room that will be rendered
	//Change this to the room you are working on

	public GraphicsTestWindow(GraphicsNotifier handler) {
		super(width, height, handler);
	}

	@Override
	protected void setupWindowHints() {
		//these three are done on setup
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
	}

	@Override
	protected void setupInputCallback() {
		glfwSetKeyCallback(getWindow(), (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		});
		GLFW.glfwSetCursorPosCallback(getWindow(), (window, xpos, ypos) -> {
			mouseInput(xpos, ypos);
		});
	}

	@Override
	protected void setupGLSettings() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable( GL_BLEND );
		glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void processInput() {
		//This is done in the rendering loop
		
		//Comment this out to remove wasd camera movement
		Vector3f cameraPosition = camera.getPosition();
		Vector3f cameraFront = camera.getCentre();
		Vector3f cameraUp = camera.getUp();
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.mul(CAMERASPEED, result);
			cameraPosition.add(result);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.mul(CAMERASPEED, result);
			cameraPosition.sub(result);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.cross(cameraUp, result);
			result.normalize();
			result.mul(CAMERASPEED);
			cameraPosition.sub(result);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
			Vector3f result = new Vector3f();
			cameraFront.cross(cameraUp, result);
			result.normalize();
			result.mul(CAMERASPEED);
			cameraPosition.add(result);
		}
		camera.setPosition(cameraPosition);
		
		//This is an example of using glfw input to modify the global input state
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
			ButtonState.setPlayerMoveUpPressed(true);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
			ButtonState.setPlayerMoveDownPressed(true);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
			ButtonState.setPlayerMoveLeftPressed(true);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
			ButtonState.setPlayerMoveRightPressed(true);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_P) == GLFW.GLFW_PRESS) {
			ButtonState.setStopPressed(true);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
			ButtonState.setPlayerDashPressed(true);
		}
		
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_UP) == GLFW.GLFW_RELEASE) {
			ButtonState.setPlayerMoveUpPressed(false);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_RELEASE) {
			ButtonState.setPlayerMoveDownPressed(false);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_RELEASE) {
			ButtonState.setPlayerMoveLeftPressed(false);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_RELEASE) {
			ButtonState.setPlayerMoveRightPressed(false);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_P) == GLFW.GLFW_RELEASE) {
			ButtonState.setStopPressed(false);
		}
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_RELEASE) {
			ButtonState.setPlayerDashPressed(false);
		}
		
	}
	
	private void mouseInput(double xpos, double ypos) {
		
		//Comment this out to remove mouse camera control
		float xoffset = (float) (xpos - lastX);
		float yoffset = (float) (lastY - ypos);
		lastX = (float) xpos;
		lastY = (float) ypos;

		float sensitivity = 0.05f;
		xoffset *= sensitivity;
		yoffset *= sensitivity;
		
		yaw   += xoffset;
		pitch += yoffset; 
		
		if (pitch > 89.0f) {
			pitch = 89.0f;
		} else if (pitch < -89.0f) {
			pitch = -89.0f;
		}
		
		float rpitch = (float) Math.toRadians(pitch);
		float ryaw = (float) Math.toRadians(yaw);
		
		Vector3f cameraFront = new Vector3f();
		cameraFront.x = (float) (Math.cos(rpitch) * Math.cos(ryaw));
		cameraFront.y = (float) (Math.sin(rpitch));
		cameraFront.z = (float) (Math.cos(rpitch) * Math.sin(ryaw));
		cameraFront = cameraFront.normalize();
		
		camera.setCentre(cameraFront);
	}

}
