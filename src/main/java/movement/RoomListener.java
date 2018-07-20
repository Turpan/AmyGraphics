package movement;

import java.awt.image.BufferedImage;

public interface RoomListener {
	public void entityAdded(Entity entity);
	public void entityRemoved(Entity entity);
	public void backgroundChanged(BufferedImage[] background);
}
