package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Entity;

public class Neutral extends Entity {
	
	public Neutral() {
		loadImage();
	}
	public void loadImage() {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/trueneutral.png"));
			//img = ImageIO.read(new File("graphics/triangle.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		setTexture(texture);
	}

	@Override
	public boolean inside(float[] point) {
		// TODO Auto-generated method stub
		return false;
	}

}
