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
	
	protected List<BufferedImage[]> backgrounds;
	
	protected BufferedImage[] activeBackground1;
	protected BufferedImage[] activeBackground2;
	
	protected float backgroundBlend;
	
	private List<RoomListener> listeners;

	private CollisionEngine collisionEngine;

	private int tickCount;
	
	private double cameraPosition[] = new double[3];
	private double cameraCentre[] = new double[3];

	public Room() {
		backgrounds = new ArrayList<BufferedImage[]>();
		contents = new ArrayList<Entity>();
		listeners = new ArrayList<RoomListener>();
		collisionEngine = new CollisionEngine();
	}

	public Room(BufferedImage[] background) {
		this();
		
		addBackground(background);
		setActiveBackground1(background);
	}

	public List<Entity> getContents() {
		return Collections.unmodifiableList(contents);
	}
	
	public void addBackground(BufferedImage[] background) {
		backgrounds.add(background);
		
		for (RoomListener listener : listeners) listener.backgroundAdded(background);
	}
	
	public void removeBackground(BufferedImage[] background) {
		backgrounds.remove(background);
		
		for (RoomListener listener : listeners) listener.backgroundRemoved(background);
	}

	public void setActiveBackground1(BufferedImage[] background) {
		if (!backgrounds.contains(background)) {
			background = null;
		}
		
		this.activeBackground1 = background;
		
		for (RoomListener listener : listeners) listener.backgroundChanged();
	}
	
	public void setActiveBackground2(BufferedImage[] background) {
		if (!backgrounds.contains(background)) {
			background = null;
		}
		
		this.activeBackground2 = background;
		
		for (RoomListener listener : listeners) listener.backgroundChanged();
	}
	
	public void setBackgroundBlend(float backgroundBlend) {
		if (backgroundBlend > 1) {
			backgroundBlend = 1;
		} else if (backgroundBlend < 0) {
			backgroundBlend = 0;
		}
		this.backgroundBlend = backgroundBlend;
	}

	public BufferedImage[] getActiveBackground1() {
		if (activeBackground1 == null || Arrays.asList(activeBackground1).contains(null)) {
			return null;
		}

		return activeBackground1;
	}
	
	public BufferedImage[] getActiveBackground2() {
		if (activeBackground2 == null || Arrays.asList(activeBackground2).contains(null)) {
			return null;
		}

		return activeBackground2;
	}
	
	public List<BufferedImage[]> getBackgrounds() {
		return backgrounds;
	}
	
	public float getBackgroundBlend() {
		return backgroundBlend;
	}

	public double[] getCameraPosition() {
		return cameraPosition;
	}

	public void setCameraPosition(double[] cameraPosition) {
		if (cameraPosition == null || cameraPosition.length != 3) {
			return;
		}
		
		this.cameraPosition = cameraPosition.clone();
	}

	public double[] getCameraCentre() {
		return cameraCentre;
	}

	public void setCameraCentre(double[] cameraCentre) {
		if (cameraCentre == null || cameraCentre.length != 3) {
			return;
		}
		
		this.cameraCentre = cameraCentre.clone();
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
