package amyGLGraphics.IO;

public class MouseEvent {
	
	private int x;
	private int y;
	
	MouseEventAction action;
	
	public MouseEvent(int x, int y, MouseEventAction action) {
		setX(x);
		setY(y);
		setMouseAction(action);
	}
	
	private void setX(int x) {
		this.x = x;
	}
	
	private void setY(int y) {
		this.y = y;
	}
	
	private void setMouseAction(MouseEventAction action) {
		this.action = action;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public MouseEventAction getMouseAction() {
		return action;
	}

}
