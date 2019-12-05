package amyGLGraphics.base;

import amyGLGraphics.Interface.GLInterfaceHandler;
import amyGLGraphics.entitys.GLRoomHandler;
import amyGraphics.GraphicsHandler;

public class GLGraphicsHandler extends GraphicsHandler {

	//The way this works is its a mapping of object size coordinates to screen space
	//So with the current values of 4000 that means an object would have to be 4000 wide to fill the screen
	//You can set this to whatever makes your job easier
	public static final int viewWidth = 4000;
	public static final int viewHeight = 4000;
	public static final int viewDepth = 4000;

	public static final int interfaceWidth = 480;
	public static final int interfaceHeight = 270;
	//public static final int interfaceWidth = 4000;
	//public static final int interfaceHeight = 4000;

	public static final int shadowWidth = 1536;
	public static final int shadowHeight = 1536;
	
	public static final float gamma = 2.2f;

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

}
