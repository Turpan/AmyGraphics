package OpenGLTests;

import java.util.Timer;
import java.util.TimerTask;

import movement.Entity.MalformedEntityException;
import movement.Room;

public class GraphicsTestEnvironment {
	
	GraphicsTestWindow window;
	
	Room room;
	
	Timer timer;
	
	public GraphicsTestEnvironment() {
		try {
			room = new CommunismRoom();
		} catch (MalformedEntityException e) {
			System.exit(-1);
		}
		
		window = new GraphicsTestWindow(() -> { 
			//give it the room to render
			window.setRoom(room);
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
				room.tick();
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

	public static void main(String[] args) {
		new GraphicsTestEnvironment();
	}

}
