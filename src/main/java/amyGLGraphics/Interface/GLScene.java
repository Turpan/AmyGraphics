package amyGLGraphics.Interface;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGraphics.Texture;
import amyInterface.Component;
import amyInterface.FontTexture;
import amyInterface.Label;

public class GLScene {

	private Component scene;

	private Map<Component, GLComponent> components = new HashMap<Component, GLComponent>();
	private Map<Label, GLText> text = new HashMap<Label, GLText>();

	public GLScene(Component scene) {
		this.scene = scene;

		for (Component component : scene.getRenderOrder()) {
			createComponent(component);

			if (component instanceof Label) {
				createLabel((Label) component);
			}
		}
	}

	public Component getScene() {
		return scene;
	}

	public Map<Component, GLComponent> getComponents() {
		return components;
	}

	public Map<Label, GLText> getText() {
		return text;
	}

	public void unbindGL() {
		removeComponent(scene.getRenderOrder());
	}

	protected void createLabel(Label label) {
		createFontTexture(label.getFont());

		GLText gltext = new GLText(label);

		text.put(label, gltext);
	}

	protected void createFontTexture(FontTexture texture) {
		if (!GLTextureCache.hasFontTexture(texture.getSprite())) {
			GLTexture2D gltexture = new GLTexture2D(texture.getSprite());
			GLTextureCache.addFontTexture(texture.getSprite(), gltexture);
		} else {
			GLTextureCache.addFontTexture(texture.getSprite(), null);
		}
	}

	protected void createComponent(Component component) {
		for (var texture : component.getTextures()) {
			if (texture == null) {
				continue;
			}
			
			createInterfaceTexture(texture);
		}

		GLComponent glcomponent = new GLComponent(component);

		components.put(component, glcomponent);
	}

	protected void createInterfaceTexture(Texture texture) {
		if (!GLTextureCache.hasInterfaceTexture(texture.getSprite())) {
			GLTexture2D gltexture = new GLTexture2D(texture.getSprite());
			gltexture.changeFiltering(GL11.GL_NEAREST);
			GLTextureCache.addInterfaceTexture(texture.getSprite(), gltexture);
		} else {
			GLTextureCache.addInterfaceTexture(texture.getSprite(), null);
		}
	}

	private void removeComponent(Component component) {
		unbindComponent(component);
		components.remove(component);
	}

	private void removeComponent(Set<Component> componentList) {
		for (var component : componentList) {
			removeComponent(component);
		}
	}

	private void unbindComponent(Component component) {
		unbindComponentBuffer(component);
		for (var texture : component.getTextures()) {
			//if (!textureRemains(component, texture)) {
				//unbindTexture(texture);
			//}
			unbindTexture(texture);
		}

		if (component instanceof Label) {
			Label label = (Label) component;

			//if (!fontTextureRemains(label)) {
				
			//}
			unbindFontTexture(label.getFont());
		}
	}

	private void unbindComponentBuffer(Component component) {
		var glcomponent = components.get(component);
		glcomponent.unbindObject();
	}
	private void unbindTexture(Texture texture) {
		if (texture == null) return;
		
		//GLTexture2D gltexture = GLTextureCache.getInterfaceTexture(texture.getSprite());
		//if (gltexture == null) return;
		
		//gltexture.unbindTexture();

		GLTextureCache.removeInterfaceTexture(texture.getSprite());
	}

	private void unbindFontTexture(FontTexture texture) {
		if (texture == null) return;
		
		//GLTextureCache.getFontTexture(texture.getSprite()).unbindTexture();

		GLTextureCache.removeFontTexture(texture.getSprite());
	}

	/*private boolean textureRemains(Component component, Texture texture) {
		for (var testedcomponent : scene.getRenderOrder()) {
			if (testedcomponent != component) {
				for (var testedtexture : testedcomponent.getTextures()) {
					if (testedtexture == null || texture == null) {
						continue;
					}
					if (testedtexture.getSprite() == texture.getSprite()) {
						return true;
					}
				}
			}
		}
		return false;
	}*/

	/*private boolean fontTextureRemains(Label label) {
		for (var testedcomponent : scene.getRenderOrder()) {
			if (testedcomponent instanceof Label) {
				Label testedlabel = (Label) testedcomponent;

				if (testedlabel.getFont().getSprite() == label.getFont().getSprite()) {
					return true;
				}
			}
		}
		return false;
	}*/

}
