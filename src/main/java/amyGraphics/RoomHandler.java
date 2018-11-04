package amyGraphics;

import java.util.ArrayList;
import java.util.List;

import movement.Room;

public abstract class RoomHandler {
	
	List<Room> rooms = new ArrayList<Room>();
	
	Room activeRoom;
	
	public RoomHandler() {
		
	}
	
	public void addRoom(Room room) {
		rooms.add(room);
	}
	
	public void removeRoom(Room room) {
		rooms.remove(room);
	}
	
	public void setActiveRoom(Room room) {
		if (!rooms.contains(room)) {
			room = null;
		}
		
		this.activeRoom = room;
	}
	
	public Room getActiveRoom() {
		return activeRoom;
	}
	
	public boolean hasActiveRoom() {
		return activeRoom != null;
	}
	
	public abstract void renderRoom();
}
