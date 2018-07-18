package amyGraphics;

import java.awt.image.BufferedImage;

public class Animation extends Texture {
	
	private int frameWidth;
	private int frameHeight;
	
	private int[] frameOrder = new int[]{0};
	private int frameCounter;
	
	private Texture[] frames;
	
	public Animation(BufferedImage sprite, int width, int height) throws MalformedAnimationException {
		super(sprite);
		setFrameWidth(width);
		setFrameHeight(height);
	}
	
	public Animation(BufferedImage sprite, int width, int height, int[] frameOrder) throws MalformedAnimationException {
		this(sprite, width, height);
		setFrameOrder(frameOrder);
	}
	
	public void setFrameWidth(int frameWidth) throws MalformedAnimationException {
		if (frameWidth <= 0) {
			throw new MalformedAnimationException("Frame width cannot be 0 or less.");
		} else if (frameWidth > getSprite().getWidth()) {
			throw new MalformedAnimationException("Frame width cannot be wider then the sprite sheet.");
		} else if (getSprite().getWidth() % frameWidth != 0) {
			throw new MalformedAnimationException("Frame width must be a multiple of sprite sheet width.");
		}
		this.frameWidth = frameWidth;
		if (frameHeight != 0) {
			createFrames();
		}
	}
	
	public void setFrameHeight(int frameHeight) throws MalformedAnimationException {
		if (frameHeight <= 0) {
			throw new MalformedAnimationException("Frame height cannot be 0 or less.");
		} else if (frameHeight > getSprite().getHeight()) {
			throw new MalformedAnimationException("Frame height cannot be higher then the sprite sheet.");
		} else if (getSprite().getHeight() % frameHeight != 0) {
			throw new MalformedAnimationException("Frame height must be a multiple of sprite sheet height.");
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
	
	@Override
	public Texture getRenderTarget() {
		return frames[frameOrder[frameCounter]];
	}

	public class MalformedAnimationException extends Exception {
		private static final long serialVersionUID = 1L;

		public MalformedAnimationException (String message) {
	        super (message);
	    }
	}

	public int[] getFrameOrder() {
		return frameOrder;
	}

	public void setFrameOrder(int[] frameOrder) throws MalformedAnimationException {
		if (!checkFrameOrder(frameOrder)) {
			throw new MalformedAnimationException("Frame order contains negative values or values greater than frame count.");
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
