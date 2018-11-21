package amyGraphics;

import java.awt.image.BufferedImage;

public class Texture {
	private BufferedImage sprite;

	//These denote what part of the sprite should be rendered
	private int x;
	private int y;
	private int width;
	private int height;

	public Texture(java.awt.image.BufferedImage sprite) {
		//setSprite(sprite);
		this.sprite = sprite;
		width = sprite.getWidth();
		height = sprite.getHeight();
	}

	public BufferedImage getSprite() {
		return sprite;
	}

	/*public void setSprite(BufferedImage sprite) {
		this.sprite = sprite;
		setX(getX());
		setY(getY());
		setWidth(getWidth());
		setHeight(getHeight());
	}*/

	public int getX() {
		return x;
	}

	public void setX(int x) {
		if (x < 0) {
			x = 0;
		}
		if (x > sprite.getWidth()) {
			x = sprite.getWidth();
		}
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		if (y < 0) {
			y = 0;
		}
		if (y > sprite.getHeight()) {
			y = sprite.getHeight();
		}
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		if (width < 1) {
			width = 1;
		}
		if ((width + x) > sprite.getWidth()) {
			width = x - sprite.getWidth();
		}
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		if (height < 1) {
			height = 1;
		}
		if ((height + y) > sprite.getHeight()) {
			height = y - sprite.getHeight();
		}
		this.height = height;
	}

	public Texture getRenderTarget() {
		return this;
	}
	
	@Override
	public Texture clone() {
		Texture texture = new Texture(getSprite());
		texture.setX(getX());
		texture.setY(getY());
		texture.setWidth(getWidth());
		texture.setHeight(getHeight());
		return texture;
	}
}
