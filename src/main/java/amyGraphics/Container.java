package amyGraphics;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Container extends Component {
	private Layout layout;
	private Set<Component> children;
	
	public Container() {
		super();
		layout = null;
		children = new HashSet<Component>();
	}
	
	public void addChild(Component component) {
		children.add(component);
		if (children.contains(component)) {
			component.removeFromParent();
			component.setParent(this);
			refreshLayout();
		}
	}
	
	public void addChild(List<Component> components) {
		for (Component component : components) {
			addChild(component);
		}
	}
	
	public void removeChild(Component component) {
		if (children.contains(component)) {
			component.setParent(null);
			children.remove(component);
			refreshLayout();
		}
	}
	
	public void removeChild(List<Component> components) {
		for (Component component : components) {
			removeChild(component);
		}
	}
	
	@Override
	public Set<Component> getRenderOrder() {
		Set<Component> renderOrder = super.getRenderOrder();
		if (renderOrder.size() > 0) {
			for (Component component : children) {
				if (component.isVisible()) {
					renderOrder.addAll(component.getRenderOrder());
				}
			}
		}
		return renderOrder;
	}
	
	@Override
	public Component findMouseClick(int x, int y) {
		var clickSource = super.findMouseClick(x, y);
		//click is inside this object if the result is not null
		if (clickSource != null) {
			for (Component component : children) {
				clickSource = component.findMouseClick(x, y);
			}
		}
		//check if click was on a child component
		if (clickSource == null) {
			return super.findMouseClick(x, y);
		} else {
			return clickSource;
		}
	}
	
	public Set<Component> getChildren() {
		return Collections.unmodifiableSet(children);
	}
	
	public void refreshLayout() {
		if (layout != null) {
			layout.layoutComponents(this, children);
		} else {
			nullAlign();
		}
	}
	
	private void nullAlign() {
		for (Component component : children) {
			Rectangle bounds = component.getBounds();
			component.setX(bounds.x);
			component.setY(bounds.y);
			component.setWidth(bounds.width);
			component.setHeight(bounds.height);
		}
	}
	
	protected Layout getLayout() {
		return layout;
	}

	protected void setLayout(Layout layout) {
		this.layout = layout;
	}
}
