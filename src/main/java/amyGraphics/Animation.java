package amyGraphics;

import java.awt.image.BufferedImage;

public class Animation extends Texture {

	private int frameWidth;
	private int frameHeight;

	private int[] frameOrder = new int[]{0};
	private int frameCounter;

	private Texture[] frames;

	public Animation(BufferedImage sprite, int width, int height) {
		super(sprite);
		setFrameWidth(width);
		setFrameHeight(height);
	}

	public Animation(BufferedImage sprite, int width, int height, int[] frameOrder) {
		this(sprite, width, height);
		setFrameOrder(frameOrder);
	}
	
	public Animation(Animation animation) {
		super(animation);
		setFrameWidth(animation.getFrameWidth());
		setFrameHeight(animation.getFrameHeight());
		setFrameOrder(animation.getFrameOrder().clone());
	}

	public void setFrameWidth(int frameWidth) {
		if (frameWidth <= 0) {
			frameWidth = getSprite().getWidth();
		} else if (frameWidth > getSprite().getWidth()) {
			frameWidth = getSprite().getWidth();
		} else if (getSprite().getWidth() % frameWidth != 0) {
			frameWidth = getSprite().getWidth();
		}
		this.frameWidth = frameWidth;
		if (frameHeight != 0) {
			createFrames();
		}
	}

	public void setFrameHeight(int frameHeight) {
		if (frameHeight <= 0) {
			frameHeight = getSprite().getHeight();
		} else if (frameHeight > getSprite().getHeight()) {
			frameHeight = getSprite().getHeight();
		} else if (getSprite().getHeight() % frameHeight != 0) {
			frameHeight = getSprite().getHeight();
		}
		this.frameHeight = frameHeight;
		if (frameWidth != 0) {
			createFrames();
		}
	}

	protected void createFrames() {
		int x = 0;
		int y = 0;

		int columns = getSprite().getWidth() / frameWidth;
		int rows = getSprite().getHeight() / frameHeight;

		int frameCount = columns * rows;
		frames = new Texture[frameCount];

		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				Texture frame = new Texture(getSprite());
				frame.setX(x);
				frame.setY(y);
				frame.setWidth(frameWidth);
				frame.setHeight(frameHeight);
				frames[(i*columns) + j] = frame;

				x += frameWidth;
			}
			x = 0;

			y += frameHeight;
		}
	}

	public void nextFrame() {
		if (frameCounter == frameOrder.length - 1) {
			frameCounter = 0;
		} else {
			frameCounter++;
		}
	}

	public void setFramePosition(int frameCounter) {
		if (frameCounter >= frameOrder.length) {
			frameCounter = frameOrder.length - 1;
		} else if (frameCounter < 0) {
			frameCounter = 0;
		}

		this.frameCounter = frameCounter;
	}

	@Override
	public Texture getRenderTarget() {
		return frames[frameOrder[frameCounter]];
	}

	public int[] getFrameOrder() {
		return frameOrder;
	}

	public void setFrameOrder(int[] frameOrder) {
		if (!checkFrameOrder(frameOrder)) {
			frameOrder = new int[] {0};
		}
		this.frameOrder = frameOrder;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	private boolean checkFrameOrder(int[] frameOrder) {
		for (int i : frameOrder) {
			if (i >= frames.length || i < 0) {
				return false;
			}
		}
		return true;
	}

	public Texture[] getFrames() {
		return frames;
	}

	public int getFrameCounter() {
		return frameCounter;
	}
}
