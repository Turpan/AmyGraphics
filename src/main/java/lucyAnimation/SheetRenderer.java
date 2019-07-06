package lucyAnimation;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SheetRenderer extends Canvas {
	private int selectedIndex = -1;
	private int width;
	private List<Image> frames = new ArrayList<Image>();

	public SheetRenderer() {
		super();
	}

	protected void drawImage() {
		clearImage();
		setSize();
		//program wont allow images with multiple sizes to get to this point
		double imageWidth = frames.get(0).getWidth();
		double imageHeight = frames.get(0).getHeight();

		double x = 0;
		double y = 0;
		int count = 0;

		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(Color.BLACK);
		gc.setStroke(Color.AZURE);

		while (count < frames.size()) {
			gc.drawImage(frames.get(count), x, y);
			x += imageWidth;
			if ((count+1) % width == 0) {
				x = 0;
				y += imageHeight;
			}
			count++;
		}

		for (int i=0; i<width - ((count) % width); i++) {
			gc.fillRect(x, y, imageWidth, imageHeight);
			x += imageWidth;
		}

		if (selectedIndex >= 0) {
			drawHighlight();
		}
	}

	public void drawHighlight() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(Color.AZURE);
		gc.setStroke(Color.AZURE);

		double imageWidth = frames.get(0).getWidth();
		double imageHeight = frames.get(0).getHeight();

		double x = 0;
		double y = 0;
		int count = 0;

		while (count < frames.size()) {
			if (count == selectedIndex) {
				gc.strokeRect(x, y, imageWidth, imageHeight);
				break;
			}
			x += imageWidth;
			if ((count+1) % width == 0) {
				x = 0;
				y += imageHeight;
			}

			count++;

		}
	}

	public void clearImage() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, getWidth(), getHeight());
	}

	private void setSize() {
		double width;
		double height;

		if (frames.size() > 0) {
			double imageWidth = frames.get(0).getWidth();
			double imageHeight = frames.get(0).getHeight();

			height = (Math.ceil(Math.abs((double) frames.size()/this.width))) * imageHeight;
			width = imageWidth * this.width;
		} else {
			width = 0;
			height = 0;
		}

		setWidth(width);
		setHeight(height);
	}

	public void setWidth(int width) {
		this.width = width;
		if (frames.size() > 0) {
			drawImage();
		} else {
			setSize();
			clearImage();
		}
	}

	public int getFrameWidth() {
		return width;
	}

	public void setFrames(List<Image> frames) {
		this.frames = frames;
		if (frames.size() > 0) {
			drawImage();
		} else {
			clearImage();
		}
	}

	public void setSelected(int index) {
		this.selectedIndex = index;
		drawImage();
	}

}
