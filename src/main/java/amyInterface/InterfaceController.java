package amyInterface;

import amyGLGraphics.IO.ButtonState;
import amyGLGraphics.IO.MouseEvent;
import amyGLGraphics.IO.MouseEventAction;

public class InterfaceController {
	
	private Component root;
	
	private int tickCount;
	private int tickThreshold;
	
	public InterfaceController() {
		
	}
	
	public void tick() {
		if (root == null) {
			return;
		}
		
		tickCount++;
		
		processInput();
		updateAnimations();
	}
	
	public void setRoot(Component root) {
		this.root = root;
	}
	
	public Component getRoot() {
		return root;
	}
	
	public void setTickThreshold(int tickThreshold) {
		if (tickThreshold <= 0) {
			tickThreshold = 1;
		}
		
		this.tickThreshold = tickThreshold;
	}
	
	public int getTickThreshold() {
		return tickThreshold;
	}
	
	protected Component processInput() {
		MouseEvent event = ButtonState.getMouseEvent();
		
		Component clickSource = root.findMouseClick(event);
		
		if (clickSource == null) {
			return clickSource;
		}
		
		if (clickSource instanceof Button) {
			Button button = (Button) clickSource;
			
			buttonClick(button, event);
		}
		
		return clickSource;
	}
	
	protected void buttonClick(Button button, MouseEvent event) {
		if (event.getMouseAction() == MouseEventAction.PRESS) {
			button.setPressed(true);
		} else if (event.getMouseAction() == MouseEventAction.RELEASE) {
			button.setPressed(false);
		}
	}
	
	protected void updateAnimations() {
		if (tickCount == tickThreshold) {
			tickCount = 0;
			
			root.updateAnimation();
		}
	}
}
