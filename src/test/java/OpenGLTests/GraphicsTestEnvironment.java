package OpenGLTests;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import amyInterface.Component;
import movement.Room;

public class GraphicsTestEnvironment {

	protected GraphicsTestWindow window;

	protected List<Room> rooms;
	protected List<Component> scenes;

	protected Timer timer;

	public GraphicsTestEnvironment() {
		rooms = getRooms();
		scenes = getScenes();

		window = new GraphicsTestWindow(() -> {
			if (rooms != null) {
				for (Room room : rooms) {
					window.addRoom(room);
				}

				if (rooms.size() > 0) {
					window.setActiveRoom(rooms.get(0));
				}
			}

			if (scenes != null) {
				for (Component scene : scenes) {
					window.addScene(scene);
				}

				if (scenes.size() > 0) {
					window.setActiveScene(scenes.get(0));
				}
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

	public void tick() {
		for (Room room : rooms) {
			room.tick();
		}
	}

	protected List<Room> getRooms() {
		List<Room> rooms = new ArrayList<Room>();
		Room room = new TyroneRoom();

		rooms.add(room);
		return rooms;
	}

	protected List<Component> getScenes() {
		List<Component> components = new ArrayList<Component>();
		return components;
	}

	public static void main(String[] args) {
		new GraphicsTestEnvironment();
	}

}
