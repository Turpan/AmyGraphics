package movement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import amyGraphics.Animation;

public abstract class Room {
	private static final int ANIMTHRESHOLD = 20;

	protected List<Entity> contents;
	protected BufferedImage[] background;
	private List<RoomListener> listeners;

	private CollisionEngine collisionEngine;

	private int tickCount;

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
		tickCount ++;

		if (tickCount % ANIMTHRESHOLD == 0) {
			for (Entity entity : getContents()) {
				if (entity.getActiveTexture() instanceof Animation) {
					Animation anim = (Animation) entity.getActiveTexture();
					anim.nextFrame();
				}
			}
		}

		for (Entity entity : getContents()) {
			if (entity instanceof Movable) {
				Movable movable = (Movable) entity;
				movable.applyConstantForces();
			}
		}

		checkCollision();
		for (Entity entity : getContents()) {
			if (entity instanceof Movable) {
				Movable movable = (Movable) entity;
				movable.tick();
			}
		}
	}

	protected void checkCollision() {
		collisionEngine.checkCollisions();
	}
}
