package lucyAnimation;

public class WorkingOrder {
	private int frame;
	private int index;
	
	public WorkingOrder(int frame, int index) {
		this.frame = frame;
		setIndex(index);
	}
	
	public int getFrame() {
		return frame;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
}
