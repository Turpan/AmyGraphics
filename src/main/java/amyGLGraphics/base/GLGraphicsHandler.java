package amyGLGraphics.base;

import amyGLGraphics.Interface.GLInterfaceHandler;
import amyGLGraphics.Interface.GLInterfaceRenderer;
import amyGLGraphics.entitys.GLRoomHandler;
import amyGraphics.GraphicsHandler;

public class GLGraphicsHandler extends GraphicsHandler {
	
	//The way this works is its a mapping of object size coordinates to screen space
	//So with the current values of 4000 that means an object would have to be 4000 wide to fill the screen
	//You can set this to whatever makes your job easier
	public static final int viewWidth = 4000;
	public static final int viewHeight = 4000;
	public static final int viewDepth = 4000;
	
	public static final int shadowWidth = 1024;
	public static final int shadowHeight = 1024;
	
	public GLGraphicsHandler() {
		roomHandler = new GLRoomHandler();
		interfaceHandler = new GLInterfaceHandler();
	}
	
	public void unbindGL() {
		GLRoomHandler handler = (GLRoomHandler) roomHandler;
		
		GLInterfaceHandler inter = (GLInterfaceHandler) interfaceHandler;
		
		handler.unBindOpenGL();
		
		inter.unBindOpenGL();
	}
	
	public void setCamera(GLCamera camera) {
		GLRoomHandler handler = (GLRoomHandler) roomHandler;
		
		handler.setCamera(camera);
	}

}
