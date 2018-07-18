package amyGraphics;

public abstract class InterfaceRenderer {
	protected Component currentScene;
	
	public InterfaceRenderer() {
		
	}
	
	public InterfaceRenderer(Component currentScene) {
		super();
		setScene(currentScene);
	}
	
	public void setScene(Component scene) {
		currentScene = scene;
	}
	
	public void renderInterface() {
		if (currentScene != null) {
			for (Component component : currentScene.getRenderOrder()) {
				renderComponent(component);
			}
		}
	}
	
	protected abstract void renderComponent(Component component);
}
