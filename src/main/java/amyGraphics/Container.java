package amyGraphics;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import amyGLGraphics.IO.MouseEvent;

public class Container extends Component {
	private Layout layout;
	private Set<Component> children;
	
	public Container() {
		super();
		layout = null;
		children = new LinkedHashSet<Component>();
	}
	
	public Container(int x, int y, int width, int height) {
		this();
		setBounds(x, y, width, height);
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
				renderOrder.addAll(component.getRenderOrder());
			}
		}
		return renderOrder;
	}
	
	@Override
	public Component findMouseClick(MouseEvent event) {
		Component clickSource = null;
		
		if (clickInBounds(event.getX(), event.getY())) {
			for (Component component : children) {
				var current = component.findMouseClick(event);
				
				if (current != null) {
					clickSource = current;
				}
			}
		}
		//check if click was on a child component
		if (clickSource == null) {
			return super.findMouseClick(event);
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
			Layout.nullAlign(this);
		}
	}
	
	protected Layout getLayout() {
		return layout;
	}

	protected void setLayout(Layout layout) {
		this.layout = layout;
	}
	
	@Override
	public void updateAnimation() {
		super.updateAnimation();
		
		for (Component child : children) {
			child.updateAnimation();
		}
	}
}
