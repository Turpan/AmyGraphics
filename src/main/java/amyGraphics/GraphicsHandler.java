package amyGraphics;

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
	
	public void setScene(Component scene) {
		if (interfaceRendererCreated()) {
			interfaceHandler.setScene(scene);
		}
	}
	
	public void setRoom(Room room) {
		if (roomRendererCreated()) {
			roomHandler.setRoom(room);
		}
	}
	
	protected boolean roomRendererCreated() {
		return roomHandler != null;
	}
	
	protected boolean interfaceRendererCreated() {
		return interfaceHandler != null;
	}
}
