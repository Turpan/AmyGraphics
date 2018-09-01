package OpenGLTests;

import java.util.Timer;
import java.util.TimerTask;

import OpenGLTests.TestScene.TestAnimation;
import amyGLGraphics.IO.ButtonState;
import amyGLGraphics.IO.MouseEvent;
import amyGLGraphics.IO.MouseEventAction;
import amyGraphics.Component;
import movement.Entity.MalformedEntityException;
import movement.Room;

public class GraphicsTestEnvironment {
	
	int tick;
	
	GraphicsTestWindow window;
	
	Room room;
	
	Component scene;
	
	Timer timer;
	
	public GraphicsTestEnvironment() {
		try {
			room = new CommunismRoom();
		} catch (MalformedEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		scene = new TestScene();
		
		window = new GraphicsTestWindow(() -> { 
			//give it the room to render
			window.setRoom(room);
			
			window.setScene(scene);
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
				
				tick++;
				
				if (tick % 10 == 0) {
					scene.updateAnimation();
				}
				
				processClick();
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
	
	private void processClick() {
		MouseEvent event = ButtonState.getMouseEvent();
		
		if (event == null) {
			return;
		}
		
		Component clickSource = scene.findMouseClick(event);
		
		if (clickSource == null) {
			return;
		}
		
		if (clickSource instanceof TestAnimation) {
			if (event.getMouseAction() == MouseEventAction.PRESS) {
				System.out.println("button clicked");
			} else {
				System.out.println("button released");
			}
		}
	}

	public static void main(String[] args) {
		new GraphicsTestEnvironment();
	}

}
