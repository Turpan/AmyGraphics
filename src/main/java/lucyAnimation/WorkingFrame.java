package lucyAnimation;

import javafx.scene.image.Image;

public class WorkingFrame {
	private int index;
	private Image frame;
	
	public WorkingFrame(Image frame, int index) {
		this.frame = frame;
		setIndex(index);
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Image getImage() {
		return frame;
	}
	
	public int getIndex() {
		return index;
	}
}
