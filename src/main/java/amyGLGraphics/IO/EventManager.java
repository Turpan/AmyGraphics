package amyGLGraphics.IO;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

public class EventManager {

	private static EventManager eventManager = new EventManager();

	private List<MouseEvent> mouseEvents = new ArrayList<MouseEvent>();

	private List<KeyState> keyStates = new ArrayList<KeyState>();

	private KeyState moveUp;
	private KeyState moveDown;
	private KeyState moveLeft;
	private KeyState moveRight;
	private KeyState attack1;
	private KeyState space;

	private EventManager() {
		moveUp = new KeyState(GLFW.GLFW_KEY_UP);
		moveDown = new KeyState(GLFW.GLFW_KEY_DOWN);
		moveLeft = new KeyState(GLFW.GLFW_KEY_LEFT);
		moveRight = new KeyState(GLFW.GLFW_KEY_RIGHT);
		attack1 = new KeyState(GLFW.GLFW_KEY_Z);
		space = new KeyState(GLFW.GLFW_KEY_SPACE);
				
		keyStates.add(moveUp);
		keyStates.add(moveDown);
		keyStates.add(moveLeft);
		keyStates.add(moveRight);
		keyStates.add(attack1);
		keyStates.add(space);
	}

	public static EventManager getManagerInstance() {
		return eventManager;
	}

	public synchronized void addMouseEvent(MouseEvent event) {
		mouseEvents.add(event);
	}

	public synchronized MouseEvent getMouseEvent() {
		if (mouseEvents.size() > 0) {
			MouseEvent event = mouseEvents.get(0);
			mouseEvents.remove(0);

			return event;
		} else {
			return null;
		}
	}

	public synchronized List<KeyState> getKeyStates() {
		return keyStates;
	}
	
	public synchronized KeyState getMoveUp() {
		return moveUp;
	}
	public synchronized KeyState getMoveDown() {
		return moveDown;
	}
	public synchronized KeyState getMoveLeft() {
		return moveLeft;
	}
	public synchronized KeyState getMoveRight() {
		return moveRight;
	}
	public synchronized KeyState getAttack1() {
		return attack1;
	}
	public synchronized KeyState getSpace() {
		return space;
	}

	public class KeyState {

		private int glfwKeyCode;

		private boolean pressed;

		private KeyState(int glfwKeyCode) {
			setKeyCode(glfwKeyCode);
		}

		public synchronized void setPressed(boolean pressed) {
			this.pressed = pressed;
		}

		public synchronized boolean isPressed() {
			return pressed;
		}

		public synchronized void setKeyCode(int glfwKeyCode) {
			this.glfwKeyCode = glfwKeyCode;
		}

		public synchronized int getKeyCode() {
			return glfwKeyCode;
		}

	}
}
