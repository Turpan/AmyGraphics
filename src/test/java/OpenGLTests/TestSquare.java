package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Entity;
import movement.Illimunates;
import movement.Light;

public class TestSquare extends Entity implements Illimunates{

	TestLight lamp;

	public TestSquare() {
		loadImage();
		createLight();
	}

	public void createLight() {
		lamp = new TestLight();
	}

	public void loadImage() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("graphics/testsquare.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		addTexture(texture);
		setActiveTexture(texture);
	}
	@Override
	public Light getLight() {
		return lamp;
	}

	@Override
	public void setPosition(double[] position) {
		super.setPosition(position);
		lamp.setPosition(position);
	}
}
