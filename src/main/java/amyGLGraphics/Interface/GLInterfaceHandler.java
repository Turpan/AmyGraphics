package amyGLGraphics.Interface;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import amyGLGraphics.base.GLObject;
import amyInterface.Component;
import amyInterface.InterfaceHandler;
import amyInterface.Label;

public class GLInterfaceHandler extends InterfaceHandler {

	private Map<Component, GLScene> scenes = new LinkedHashMap<Component, GLScene>();

	private GLInterfaceRenderer renderer = new GLInterfaceRenderer();
	private GLTextRenderer textRenderer = new GLTextRenderer();

	@Override
	public void renderInterface() {
		if (!hasActiveScene()) {
			return;
		}

		List<GLObject> objects = new ArrayList<GLObject>();

		List<GLObject> textobjects = new ArrayList<GLObject>();

		GLScene glScene = scenes.get(getActiveScene());

		Map<Component, GLComponent> components = glScene.getComponents();
		Map<Label, GLText> text = glScene.getText();

		for (Component component : getActiveScene().getRenderOrder()) {
			GLComponent glcomponent = components.get(component);

			glcomponent.update();

			if (glcomponent.shouldRender()) {
				objects.add(glcomponent);

				if (component instanceof Label) {
					textobjects.add(text.get(component));
				}
			}
		}

		renderer.render(objects);
		textRenderer.render(textobjects);
	}

	@Override
	public void addScene(Component scene) {
		super.addScene(scene);

		GLScene glScene = new GLScene(scene);
		scenes.put(scene, glScene);
	}

	@Override
	public void removeScene(Component scene) {
		super.removeScene(scene);

		GLScene glScene = scenes.get(scene);
		glScene.unbindGL();
	}

	@Override
	public void setActiveScene(Component scene) {
		super.setActiveScene(scene);
	}

	public void resetState() {
		unBindOpenGL();
		renderer.resetState();
	}
	public void unBindOpenGL() {
		for (Component scene : scenes.keySet()) {
			GLScene glScene = scenes.get(scene);
			glScene.unbindGL();
		}

		renderer.unbindGL();
	}

	public boolean hasActiveScene() {
		return (getActiveScene() != null);
	}

}
