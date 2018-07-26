package amyGraphics;

import movement.Room;

public abstract class GraphicsHandler {
	protected RoomHandler roomHandler;
	protected InterfaceRenderer interfaceRenderer;
	
	public void render() {
		if (roomRendererCreated()) {
			roomHandler.renderRoom();
		}
		if (interfaceRendererCreated()) {
			interfaceRenderer.renderInterface();
		}
	}
	
	public void setScene(Component scene) {
		if (interfaceRendererCreated()) {
			interfaceRenderer.setScene(scene);
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
		return interfaceRenderer != null;
	}
}
