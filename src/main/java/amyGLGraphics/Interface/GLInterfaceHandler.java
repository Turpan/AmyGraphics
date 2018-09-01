package amyGLGraphics.Interface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import amyGLGraphics.GLTexture2D;
import amyGLGraphics.GLTextureCache;
import amyGLGraphics.base.GLObject;
import amyGLGraphics.entitys.GLPointDepthRenderer;
import amyGLGraphics.entitys.GLSkyBox;
import amyGraphics.Component;
import amyGraphics.FontTexture;
import amyGraphics.InterfaceHandler;
import amyGraphics.Label;
import amyGraphics.Texture;
import movement.Entity;
import movement.Light;

public class GLInterfaceHandler extends InterfaceHandler {
	
	private Map<Component, GLComponent> components = new HashMap<Component, GLComponent>();
	private Map<Label, GLText> text = new HashMap<Label, GLText>();
	
	private GLInterfaceRenderer renderer = new GLInterfaceRenderer();
	private GLTextRenderer textRenderer = new GLTextRenderer();

	@Override
	public void renderInterface() {
		if (!hasScene()) {
			return;
		}
		
		List<GLObject> objects = new ArrayList<GLObject>();
		
		List<GLObject> textobjects = new ArrayList<GLObject>();
		
		for (Component component : currentScene.getRenderOrder()) {
			GLComponent glcomponent = components.get(component);
			
			glcomponent.update();
			
			if (glcomponent.shouldRender()) {
				objects.add(glcomponent);
				
				if (component instanceof Label) {
					textobjects.add(text.get((Label) component));
				}
			}
		}
		
		renderer.render(objects);
		textRenderer.render(textobjects);
	}
	
	@Override
	public void setScene(Component scene) {
		super.setScene(scene);
		
		for (Component component : scene.getRenderOrder()) {
			createComponent(component);
			
			if (component instanceof Label) {
				createLabel((Label) component);
			}
		}
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
		}
	}
	
	protected void createComponent(Component component) {
		for (var texture : component.getTextures()) {
			createInterfaceTexture(texture);
		}
		
		GLComponent glcomponent = new GLComponent(component);
		
		components.put(component, glcomponent);
	}
	
	protected void createInterfaceTexture(Texture texture) {
		if (!GLTextureCache.hasInterfaceTexture(texture.getSprite())) {
			GLTexture2D gltexture = new GLTexture2D(texture.getSprite());
	        GLTextureCache.addInterfaceTexture(texture.getSprite(), gltexture);
		}
	}
	
	public void resetState() {
		unBindOpenGL();
		renderer.resetState();
		components.clear();
	}
	public void unBindOpenGL() {
		if (hasScene()) {
			removeComponent(currentScene.getRenderOrder());
		}
		
		renderer.unbindGL();
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
			if (!textureRemains(component, texture)) {
				unbindTexture(texture);
			}
		}
		
		if (component instanceof Label) {
			Label label = (Label) component;
			
			if (!fontTextureRemains(label)) {
				unbindFontTexture(label.getFont());
			}
		}
	}
	
	private void unbindComponentBuffer(Component component) {
		var glcomponent = components.get(component);
		glcomponent.unbindObject();
	}
	private void unbindTexture(Texture texture) {
		GLTextureCache.getInterfaceTexture(texture.getSprite()).unbindTexture();
		
		GLTextureCache.removeInterfaceTexture(texture.getSprite());
	}
	
	private void unbindFontTexture(FontTexture texture) {
		GLTextureCache.getFontTexture(texture.getSprite()).unbindTexture();
		
		GLTextureCache.removeFontTexture(texture.getSprite());
	}
	
	private boolean textureRemains(Component component, Texture texture) {
		for (var testedcomponent : currentScene.getRenderOrder()) {
			if (testedcomponent != component) {
				for (var testedtexture : testedcomponent.getTextures()) {
					if (testedtexture.getSprite() == texture.getSprite()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean fontTextureRemains(Label label) {
		for (var testedcomponent : currentScene.getRenderOrder()) {
			if (testedcomponent instanceof Label) {
				Label testedlabel = (Label) testedcomponent;
				
				if (testedlabel.getFont().getSprite() == label.getFont().getSprite()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean hasScene() {
		return (currentScene != null);
	}
	
}
