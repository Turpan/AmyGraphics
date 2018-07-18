package amyGraphics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class Component {
	private int x;
	private int y;
	private int width;
	private int height;
	private int preferredWidth;
	private int preferredHeight;
	private Rectangle bounds;
	private BufferedImage background;
	private boolean interactable;
	private boolean visible;
	private boolean resizable;
	private Container parent;
	
	public Component() {
		bounds = new Rectangle();
		parent = null;
	}
	
	public Component(int x, int y, int width, int height) {
		super();
		setBounds(x, y, width, height);
	}
	
	public int getPreferredWidth() {
		return preferredWidth;
	}

	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	public int getPreferredHeight() {
		return preferredHeight;
	}

	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}

	public void setPreferredSize(int width, int height) {
		setPreferredWidth(width);
		setPreferredHeight(height);
	}
	
	public void setBounds(int x, int y, int width, int height) {
		setBounds(new Rectangle(x, y, width, height));
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}
	
	protected void setX(int x) {
		this.x = x;
	}

	protected void setY(int y) {
		this.y = y;
	}

	protected void setWidth(int width) {
		this.width = width;
	}

	protected void setHeight(int height) {
		this.height = height;
	}
	
	public void setBackground(BufferedImage background) {
		preferredWidth = background.getWidth();
		preferredHeight = background.getHeight();
		this.background = background;
	}
	
	public Set<Component> getRenderOrder() {
		Set<Component> renderOrder = new LinkedHashSet<Component>();
		if (isVisible()) {
			renderOrder.add(this);
		}
		return renderOrder;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public BufferedImage getBackground() {
		return background;
	}
	
	public boolean isInteractable() {
		return interactable;
	}

	public void setInteractable(boolean interactable) {
		this.interactable = interactable;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public Component findMouseClick(int x, int y) {
		Component clickSource = null;
		if (isInteractable() && isVisible() && clickInBounds(x, y)) {
			clickSource = this;
		} else {
			clickSource = null;
		}
		return clickSource;
	}
	
	private void callUpdate() {
		if (parent != null) {
			parent.refreshLayout();
		}
	}

	protected Component getParent() {
		return parent;
	}

	protected void setParent(Container parent) {
		this.parent = parent;
	}
	
	protected void removeFromParent() {
		if (parent != null) {
			parent.removeChild(this);
		}
	}
	
	private boolean clickInBounds(int clickX, int clickY) {
		boolean inBounds = (clickX > getX() && clickX < (getX() + getWidth()) &&
				clickY > getY() && clickY < (getY() + getHeight()));
		return inBounds;
	}
}
