package amyGraphics;

import movement.Room;
import movement.RoomListener;

public abstract class RoomRenderer implements RoomListener{
	protected Room room;
	
	public RoomRenderer() {
		
	}
	
	public RoomRenderer(Room room) {
		this();
		setRoom(room);
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public boolean hasRoom() {
		return room != null;
	}
	
	public abstract void renderRoom();
}
