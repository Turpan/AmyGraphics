package OpenGLTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import OpenGLTests.TestScene.TestAnimation;
import amyGLGraphics.IO.ButtonState;
import amyGLGraphics.IO.MouseEvent;
import amyGLGraphics.IO.MouseEventAction;
import amyInterface.Component;
import movement.Entity.MalformedEntityException;
import movement.Room;

public class GraphicsTestEnvironment {
	protected GraphicsTestWindow window;
	
	List<Room> rooms = new ArrayList<Room>();
	List<Component> scenes = new ArrayList<Component>();
	
	protected Component scene;
	
	Timer timer;
	
	public GraphicsTestEnvironment() {
		rooms = getRooms();
		
		scenes = getScenes();
		
		window = new GraphicsTestWindow(() -> { 
			//give it the room to render
			for (Room room : rooms) {
				window.addRoom(room);
			}
			
			if (rooms != null && rooms.size() > 0) {
				window.setActiveRoom(rooms.get(0));
			}
			
			for (Component scene : scenes) {
				window.addScene(scene);
			}
			
			if (scenes != null && scenes.size() > 0) {
				window.setActiveScene(scenes.get(0));
			}
		});
		
		//start the seperate thread
		Thread graphicsThread = new Thread(window);
		graphicsThread.start();
		
		//create the timer
		timer = new Timer();
		
		//setup timer callback & start timer
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				tick();
			}
					
		}, 10, 10);
		
		//Join the graphics thread and wait for it to terminate
		try {
			graphicsThread.join();
		} catch (InterruptedException e) {
			
		}
		
		//Remove the timer and close the program
		timer.cancel();
		System.exit(0);
	}
	
	protected List<Room> getRooms() {
		Room room = null;
		
		try {
			room = new CommunismRoom();
		} catch (MalformedEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<Room> rooms = new ArrayList<Room>();
		rooms.add(room);
		
		return rooms;
	}
	
	protected List<Component> getScenes() {
		List<Component> scenes = new ArrayList<Component>();
		scenes.add(new TestScene());
		
		return scenes;
	}
	
	protected void tick() {
		for (Room room : rooms) {
			room.tick();
		}
	}

	public static void main(String[] args) {
		new GraphicsTestEnvironment();
	}

}
