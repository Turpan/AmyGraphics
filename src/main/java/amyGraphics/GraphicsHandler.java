package amyGraphics;

import movement.Room;

public abstract class GraphicsHandler {
	protected RoomRenderer roomRenderer;
	protected InterfaceRenderer interfaceRenderer;
	
	public void render() {
		if (roomRendererCreated()) {
			roomRenderer.renderRoom();
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
			roomRenderer.setRoom(room);
		}
	}
	
	protected boolean roomRendererCreated() {
		return roomRenderer != null;
	}
	
	protected boolean interfaceRendererCreated() {
		return interfaceRenderer != null;
	}
}
