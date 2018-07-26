package amyGraphics;

import movement.Room;
import movement.RoomListener;

public abstract class RoomHandler implements RoomListener{
	protected Room room;
	
	public RoomHandler() {
		
	}
	
	public RoomHandler(Room room) {
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
