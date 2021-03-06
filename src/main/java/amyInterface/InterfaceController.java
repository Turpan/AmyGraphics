package amyInterface;

import amyGLGraphics.IO.EventManager;
import amyGLGraphics.IO.MouseEvent;
import amyGLGraphics.IO.MouseEventAction;

public class InterfaceController {

	private Component root;
	
	private Component mouseOver;

	private int tickCount;
	private int tickThreshold;

	public InterfaceController() {

	}

	public void tick() {
		if (root == null) {
			return;
		}

		tickCount++;

		MouseEvent event;
		while ((event = EventManager.getManagerInstance().getMouseEvent()) != null) {
			processInput(event);
		}
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

	protected Component processInput(MouseEvent event) {
		Component clickSource = root.findMouseClick(event);

		if (event.getMouseAction() == MouseEventAction.MOVEMENT) {
			mouseOver(clickSource, event);
		}
		
		if (clickSource == null) {
			return clickSource;
		}

		if (clickSource instanceof Button && event.getMouseAction() == MouseEventAction.PRESS
				|| event.getMouseAction() == MouseEventAction.RELEASE) {
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
	
	protected void mouseOver(Component component, MouseEvent event) {
		if (component != mouseOver) {
			if (mouseOver instanceof Hoverable) {
				Hoverable target = (Hoverable) mouseOver;
				target.mouseOff();
			}
			
			mouseOver = component;
			
			if (mouseOver instanceof Hoverable) {
				Hoverable target = (Hoverable) mouseOver;
				target.mouseOn();
			}
		}
	}

	protected void updateAnimations() {
		if (tickCount == tickThreshold) {
			tickCount = 0;

			root.updateAnimation();
		}
	}
}
