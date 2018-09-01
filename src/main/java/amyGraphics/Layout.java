package amyGraphics;

import java.awt.Rectangle;
import java.util.Set;

public abstract class Layout {

	protected abstract void layoutComponents(Component parent, Set<Component> children);
	
	public static void nullAlign(Container container) {
		for (Component component : container.getChildren()) {
			Rectangle bounds = component.getBounds();
			component.setX(bounds.x + container.getX());
			component.setY(bounds.y + container.getY());
			component.setWidth(bounds.width);
			component.setHeight(bounds.height);
		}
	}
	
	public static void layoutSelf(Component root) {
		Rectangle bounds = root.getBounds();
		root.setX(bounds.x);
		root.setY(bounds.y);
		root.setWidth(bounds.width);
		root.setHeight(bounds.height);
	}
}
