package amyGraphics;

import amyInterface.Component;
import amyInterface.InterfaceHandler;
import movement.Room;

public abstract class GraphicsHandler {
	protected RoomHandler roomHandler;
	protected InterfaceHandler interfaceHandler;

	public void render() {
		if (roomRendererCreated()) {
			roomHandler.renderRoom();
		}
		if (interfaceRendererCreated()) {
			interfaceHandler.renderInterface();
		}
	}

	public void setActiveScene(Component scene) {
		if (interfaceRendererCreated()) {
			interfaceHandler.setActiveScene(scene);
		}
	}

	public void addScene(Component scene) {
		if (interfaceRendererCreated()) {
			interfaceHandler.addScene(scene);
		}
	}

	public void removeScene(Component scene) {
		if (interfaceRendererCreated()) {
			interfaceHandler.removeScene(scene);
		}
	}

	public void setActiveRoom(Room room) {
		if (roomRendererCreated()) {
			roomHandler.setActiveRoom(room);
		}
	}

	public void addRoom(Room room) {
		if (roomRendererCreated()) {
			roomHandler.addRoom(room);
		}
	}

	public void removeRoom(Room room) {
		if (roomRendererCreated()) {
			roomHandler.removeRoom(room);
		}
	}

	protected boolean roomRendererCreated() {
		return roomHandler != null;
	}

	protected boolean interfaceRendererCreated() {
		return interfaceHandler != null;
	}
}
