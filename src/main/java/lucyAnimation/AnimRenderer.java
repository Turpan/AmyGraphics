package lucyAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class AnimRenderer extends Canvas {
	
	private List<Image> frames = new ArrayList<Image>();
	private int[] frameOrder = new int[] {0};
	private Timer timer;
	private float fps = 10.0f;
	private int tickcount;
	private int framePosition;
	
	public AnimRenderer() {
		super();
	}
	
	public void startRender() {
		tickcount = 0;
		framePosition = 0;
		
		if (fps <= 0.0f) {
			fps = 10.0f;
		}
		
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				tick();
			}
			
		}, 1, 1);
		
		setSize();
		drawFrame();
	}
	
	public void stopRender() {
		timer.cancel();
	}
	
	public void drawFrame() {
		clearImage();
		
		GraphicsContext gc = getGraphicsContext2D();
		if (frames.size() > 0) gc.drawImage(frames.get(framePosition), 0, 0);
	}
	
	private void clearImage() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
	}
	
	private void setSize() {
		double width;
		double height;

		if (frames.size() > 0) {
			width = frames.get(0).getWidth();
			height = frames.get(0).getHeight();
		} else {
			width = 0;
			height = 0;
		}

		setWidth(width);
		setHeight(height);
	}
	
	public void setFrames(List<Image> frames) {
		this.frames = frames;
	}
	
	public void setFrameOrder(int[] frameOrder) {
		if (frameOrder == null || frameOrder.length <= 0) {
			frameOrder = new int[] {0};
		}
		
		this.frameOrder = frameOrder;
	}
	
	public void setFPS(float fps) {
		this.fps = fps;
	}
	
	public void tick() {
		tickcount++;
		
		if (tickcount == (1000 / fps)) {
			framePosition++;
			if (framePosition >= frameOrder.length) framePosition = 0;
			tickcount = 0;
			drawFrame();
		}
	}
}
