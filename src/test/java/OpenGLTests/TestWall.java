package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Wall;
import movement.mathDS.Vector;

public class TestWall extends Wall{
	
	public TestWall(Vector angle, boolean wide) throws MalformedEntityException {
		setNormal (angle);
		setBounciness(1);
		
		Texture texture;
		
		if (wide) {
			texture = wideTexture();
		} else {
			texture = longTexture();
		}
		
		setTexture(texture);
	}
	
	public Texture longTexture() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/tests/wall-long.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setDimensions(new double[] {20,600,700});
		
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		
		return texture;
	}
	public Texture wideTexture() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/tests/wall-wide.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setDimensions(new double[] {600,20,700});
		
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		
		return texture;
	}
}