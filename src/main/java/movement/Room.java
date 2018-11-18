package movement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import movement.mathDS.Vector.MalformedVectorException;

public abstract class Room {
	protected List<Entity> contents;
	protected BufferedImage[] background;
	private List<RoomListener> listeners;
	
	private CollisionEngine collisionEngine;
	
	public Room() {
		contents = new ArrayList<Entity>();
		listeners = new ArrayList<RoomListener>();
		collisionEngine = new CollisionEngine();
	}
	
	public Room(BufferedImage[] background) {
		this();
		setBackground(background);
	}
	
	public List<Entity> getContents() {
		return Collections.unmodifiableList(contents);
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
		collisionEngine.add(entity);
	}
	
	public void addEntity(List<Entity> entitys) {
		for (Entity entity : entitys) {
			addEntity(entity);
		}
	}
	
	public void removeEntity(Entity entity) {
		contents.remove(entity);
		collisionEngine.remove(entity);
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
	
	protected void checkCollision() {
		try {
			collisionEngine.checkCollisions();
		} catch (MalformedVectorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
