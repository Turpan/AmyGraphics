package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Entity;

public class Communism extends Entity {
	public Communism() {
		loadImage();
	}
	protected Communism(Communism communism) {
		super(communism);
	}
	
	@Override
	public Communism clone() {
		return new Communism(this);
	}
	public void loadImage() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("graphics/tests/communism.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		addTexture(texture);
		setActiveTexture(texture);
	}
}