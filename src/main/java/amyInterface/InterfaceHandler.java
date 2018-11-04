package amyInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class InterfaceHandler {
	protected Component activeScene;
	
	List<Component> scenes = new ArrayList<Component>();
	
	public InterfaceHandler() {
		
	}
	
	public void setActiveScene(Component scene) {
		if (!scenes.contains(scene)) {
			scene = null;
		}
		
		activeScene = scene;
	}
	
	public Component getActiveScene() {
		return activeScene;
	}
	
	public void addScene(Component scene) {
		scenes.add(scene);
	}
	
	public void removeScene(Component scene) {
		scenes.remove(scene);
	}
	
	public abstract void renderInterface();
}
