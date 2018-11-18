package movement;

import java.awt.Dimension;

public interface GameListener {
	public Dimension getPlayerLocation();
	public void removeEntity(Entity entity);
	public void createEntity(Entity entity);
}
