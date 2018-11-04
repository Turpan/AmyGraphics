package amyInterface;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import amyGLGraphics.IO.MouseEvent;
import amyGraphics.Animation;
import amyGraphics.Texture;

public class Component {
	
	static final Color BASE = Color.GRAY;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	private int preferredWidth;
	private int preferredHeight;
	
	private Rectangle bounds;
	
	private List<Texture> textures = new ArrayList<Texture>();
	
	private Texture activeTexture;
	private Color colour = BASE;
	
	private boolean visible;
	private boolean resizable;
	private boolean interactable;
	
	private Container parent;
	
	public Component() {
		bounds = new Rectangle();
		parent = null;
	}
	
	public Component(int x, int y, int width, int height) {
		this();
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
		
		callUpdate();
	}
	
	public void setBounds(int x, int y, int width, int height) {
		setBounds(new Rectangle(x, y, width, height));
		
		if (parent == null) {
			Layout.layoutSelf(this);
		}
	}
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
		
		callUpdate();
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
	
	public void addTexture(Texture background) {
		textures.add(background);
		
		callUpdate();
	}
	
	public void removeTexture(Texture background) {
		textures.remove(background);
		
		callUpdate();
	}
	
	public void setActiveTexture(Texture texture) {
		if (!textures.contains(texture)) {
			activeTexture = null;
		}
		
		activeTexture = texture;
	}
	
	public Texture getActiveTexture() {
		return activeTexture;
	}
	
	public Set<Component> getRenderOrder() {
		Set<Component> renderOrder = new LinkedHashSet<Component>();
		renderOrder.add(this);
		
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
	
	public int getRight() {
		return getX() + getWidth();
	}
	
	public int getBottom() {
		return getY() + getHeight();
	}

	public List<Texture> getTextures() {
		return textures;
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
		
		callUpdate();
	}
	
	public boolean isInteractable() {
		return interactable;
	}
	
	public void setInteractable(boolean interactable) {
		this.interactable = interactable;
	}

	public Color getColour() {
		return colour;
	}

	public void setColour(Color colour) {
		if (colour == null) {
			colour = BASE;
		}
		
		this.colour = colour;
	}

	public Component findMouseClick(MouseEvent event) {
		Component clickSource = null;
		
		if (event == null) {
			return clickSource;
		}
		
		if (isVisible() && clickInBounds(event.getX(), event.getY())) {
			clickSource = this;
		} else {
			clickSource = null;
		}
		return clickSource;
	}
	
	protected void callUpdate() {
		if (parent != null) {
			parent.refreshLayout();
		}
	}

	public Component getParent() {
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
	
	protected boolean clickInBounds(int clickX, int clickY) {
		boolean inBounds = (clickX > getX() && clickX < (getX() + getWidth()) &&
				clickY > getY() && clickY < (getY() + getHeight()));
		return inBounds;
	}
	
	public void updateAnimation() {
		for (Texture texture : textures) {
			if (texture instanceof Animation) {
				Animation anim = (Animation) texture;
				
				anim.nextFrame();
			}
		}
	}
}
