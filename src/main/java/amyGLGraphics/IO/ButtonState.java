package amyGLGraphics.IO;

public class ButtonState {
	
	private static boolean playerMoveUpPressed;
	private static boolean playerMoveDownPressed;
	private static boolean playerMoveLeftPressed;
	private static boolean playerMoveRightPressed;
	
	public static synchronized void setPlayerMoveUpPressed(boolean pressed) {
		playerMoveUpPressed = pressed;
	}
	
	public static synchronized void setPlayerMoveDownPressed(boolean pressed) {
		playerMoveDownPressed = pressed;
	}
	
	public static synchronized void setPlayerMoveLeftPressed(boolean pressed) {
		playerMoveLeftPressed = pressed;
	}
	
	public static synchronized void setPlayerMoveRightPressed(boolean pressed) {
		playerMoveRightPressed = pressed;
	}
	
	public static synchronized boolean getPlayerMoveUpPressed() {
		return playerMoveUpPressed;
	}
	
	public static synchronized boolean getPlayerMoveDownPressed() {
		return playerMoveDownPressed;
	}
	
	public static synchronized boolean getPlayerMoveLeftPressed() {
		return playerMoveLeftPressed;
	}
	
	public static synchronized boolean getPlayerMoveRightPressed() {
		return playerMoveRightPressed;
	}
}
