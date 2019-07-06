package amyInterface;

import java.awt.Rectangle;
import java.util.Set;

public abstract class Layout {

	protected abstract void layoutComponents(Component parent, Set<Component> children);

	public static void nullAlign(Container container) {
		for (Component component : container.getChildren()) {
			Rectangle bounds = component.getBounds();
			//component.setX(bounds.x + container.getX());
			//component.setY(bounds.y + container.getY());
			//component.setWidth(bounds.width);
			//component.setHeight(bounds.height);
			
			int x = bounds.x + container.getX();
			int y = bounds.y + container.getY();
			int right = x + bounds.width;
			int bottom = y + bounds.height;
			
			x = Math.min(Math.max(x , component.getParent().getX()), component.getParent().getRight());
			y = Math.min(Math.max(y, component.getParent().getY()), component.getParent().getBottom());
			right = Math.max(Math.min(right, component.getParent().getRight()), component.getParent().getX());
			bottom = Math.max(Math.min(bottom, component.getParent().getBottom()), component.getParent().getY());
			
			component.setX(x);
			component.setY(y);
			component.setWidth(right - x);
			component.setHeight(bottom - y);
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
