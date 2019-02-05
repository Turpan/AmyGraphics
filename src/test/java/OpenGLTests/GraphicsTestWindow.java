package OpenGLTests;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
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

import amyGLGraphics.IO.EventManager;
import amyGLGraphics.IO.EventManager.KeyState;
import amyGLGraphics.IO.GraphicsNotifier;
import amyGLGraphics.IO.MouseEvent;
import amyGLGraphics.IO.MouseEventAction;
import amyGLGraphics.base.GLGraphicsHandler;
import amyGLGraphics.base.GLWindow;
import amyGLGraphics.entitys.GLRoomHandler;

public class GraphicsTestWindow extends GLWindow {

	//Change this to determine how fast the camera moves
	//Note that this is in screen space coordinates, not model space
	private static final float CAMERASPEED = 0.1f;

	private float lastX = width / 2;
	private float lastY = height / 2;

	private float pitch;
	private float yaw;

	private boolean displayCursor;

	//Change these to update window size

	//Note that window size does not at all change the mapping of object size to screen space
	//Change the values in GLGraphicsHandler to do that
	private static final int width = 1600;
	private static final int height = 900;

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
		GLFW.glfwSetCursorPosCallback(getWindow(), (window, xpos, ypos) -> {
			mouseInput(xpos, ypos);
		});
		GLFW.glfwSetMouseButtonCallback(getWindow(), (long window, int button, int action, int mods) -> {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1)
				mouseClick(action);
		});
		GLFW.glfwSetKeyCallback(getWindow(), (long window, int key, int scancode, int action, int mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			
			for (KeyState keyState : EventManager.getManagerInstance().getKeyStates()) {
				if (keyState.getKeyCode() == key) {
					if (action == GLFW.GLFW_PRESS) {
						keyState.setPressed(true);
					} else if (action == GLFW.GLFW_RELEASE) {
						keyState.setPressed(false);
					}
				}
			}
			
			if (key == GLFW.GLFW_KEY_M && action == GLFW_RELEASE) {
				GLRoomHandler.fxaa = !GLRoomHandler.fxaa;
			}
			
			if (key == GLFW.GLFW_KEY_N && action == GLFW_RELEASE) {
				GLRoomHandler.eyeAdaption = !GLRoomHandler.eyeAdaption;
			}
			
			if (key == GLFW.GLFW_KEY_K && action == GLFW_RELEASE) {
				GLRoomHandler.shadows = !GLRoomHandler.shadows;
			}
		});
	}

	@Override
	protected void setupGLSettings() {
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable( GL_BLEND );
		glEnable(GL11.GL_DEPTH_TEST);
		glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	@Override
	protected void processInput() {
		//This is done in the rendering loop
		
		if (GLFW.glfwGetKey(getWindow(), GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS) {
			if (displayCursor) {
				GLFW.glfwSetInputMode(getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
			} else {
				GLFW.glfwSetInputMode(getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
			}

			displayCursor = !displayCursor;
		}
		
		if (displayCursor) {
			return;
		}

		//Comment this out to remove wasd camera movement
		Vector3f cameraPosition = camera.getPosition();
		Vector3f cameraFront = camera.getCentre();
		Vector3f cameraUp = camera.getOrientation();
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
	}

	private void mouseClick(int action) {
		float percentx = lastX / width;
		float percenty = lastY / height;

		int x = (int) (percentx * GLGraphicsHandler.interfaceWidth);
		int y = (int) (percenty * GLGraphicsHandler.interfaceHeight);

		MouseEventAction mouseaction;

		if (action == GLFW.GLFW_RELEASE) {
			mouseaction = MouseEventAction.RELEASE;
		} else {
			mouseaction = MouseEventAction.PRESS;
		}

		MouseEvent event = new MouseEvent(x, y, mouseaction);

		EventManager.getManagerInstance().addMouseEvent(event);
	}

	private void mouseInput(double xpos, double ypos) {

		//Comment this out to remove mouse camera control
		float xoffset = (float) (xpos - lastX);
		float yoffset = (float) (lastY - ypos);
		lastX = (float) xpos;
		lastY = (float) ypos;
		
		if (displayCursor) {
			return;
		}

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
		
		Vector3f cameraUp = new Vector3f();
		cameraUp.x = (float) (Math.sin(rpitch) * Math.cos(ryaw));
		cameraUp.y = (float) (-Math.cos(rpitch));
		cameraUp.z = (float) (Math.sin(rpitch) * Math.sin(ryaw));
		cameraUp.normalize();

		camera.setCentre(cameraFront);
		camera.setUp(cameraUp);
	}

}
