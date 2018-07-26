package amyGLGraphics;

import amyGLInterface.GLInterfaceRenderer;
import amyGraphics.GraphicsHandler;

public class GLGraphicsHandler extends GraphicsHandler {
	//public static final int viewWidth = 3840;
	//public static final int viewHeight = 2160;
	//public static final int viewDepth = 2000;
	public static final int viewWidth = 4000;
	public static final int viewHeight = 4000;
	public static final int viewDepth = 4000;
	
	public GLGraphicsHandler() {
		roomHandler = new GLRoomHandler();
		interfaceRenderer = new GLInterfaceRenderer();
	}
	
	@Override
	public void render() {
		roomHandler.renderRoom();
	}
	
	public void unbindGL() {
		
	}

}
