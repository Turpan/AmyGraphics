package movement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Room {
	protected List<Entity> contents;
	protected BufferedImage[] background;
	private List<RoomListener> listeners;
	
	public Room() {
		contents = new ArrayList<Entity>();
		listeners = new ArrayList<RoomListener>();
	}
	
	public Room(BufferedImage[] background) {
		this();
		setBackground(background);
	}
	
	public List<Entity> getContents() {
		return contents;
	}
	
	public void setBackground(BufferedImage[] background) {
		this.background = background;
		for (RoomListener listener : listeners) listener.backgroundChanged(background);
	}
	
	public BufferedImage[] getBackground() {
		if (background == null || Arrays.asList(background).contains(null)) {
			return null;
		}
		
		return background;
	}
	
	public void addEntity(Entity entity) {
		contents.add(entity);
		for (RoomListener listener : listeners) listener.entityAdded(entity);
	}
	
	public void addEntity(List<Entity> entitys) {
		for (Entity entity : entitys) {
			addEntity(entity);
		}
	}
	
	public void removeEntity(Entity entity) {
		contents.remove(entity);
		for (RoomListener listener : listeners) listener.entityRemoved(entity);
	}
	
	public void removeEntity(List<Entity> entitys) {
		for (Entity entity : entitys) {
			removeEntity(entity);
		}
	}
	
	public void removeAll() {
		removeEntity(getContents());
	}
	
	public void addListener(RoomListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(RoomListener listener) {
		listeners.remove(listener);
	}
	
	public void tick() {
		for (Entity entity : contents) {
			// TODO where the fuck my easy time advancedment rone
		}
	}
}
