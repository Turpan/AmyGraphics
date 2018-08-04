package amyGLGraphics.IO;

public class ButtonState {
	
	private static boolean playerMoveUpPressed;
	private static boolean playerMoveDownPressed;
	private static boolean playerMoveLeftPressed;
	private static boolean playerMoveRightPressed;
	
	private static boolean stopPressed;
	
	private static boolean playerDashPressed;
	
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
	
	public static synchronized void setStopPressed(boolean pressed) {
		stopPressed = pressed;
	}
	
	public static synchronized void setPlayerDashPressed(boolean pressed) {
		playerDashPressed = pressed;
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
	
	public static synchronized boolean getStopPressed() {
		return stopPressed;
	}
	
	public static synchronized boolean getPlayerDashPressed() {
		return playerDashPressed;
	}
}
