package amyGraphics;

public abstract class InterfaceHandler {
	protected Component currentScene;
	
	public InterfaceHandler() {
		
	}
	
	public InterfaceHandler(Component currentScene) {
		this();
		setScene(currentScene);
	}
	
	public void setScene(Component scene) {
		currentScene = scene;
	}
	
	public abstract void renderInterface();
}
