package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Movable;
import movement.Obstacle;
import movement.Shapes.Rectangle;

public class TestWall extends Obstacle {

	public TestWall(boolean wide){
		setCoR(1);

		Texture texture;

		if (wide) {
			texture = wideTexture();
		} else {
			texture = longTexture();
		}

		addTexture(texture);
		setActiveTexture(texture);
	}
	protected TestWall (TestWall tw) {
		super(tw);
	}
	
	@Override
	public TestWall clone() {
		return new TestWall(this);
	}
	public Texture longTexture() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("graphics/wall-long.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setDimensions(new double[] {50,600,700});
		setOutline(new Rectangle(getDimensions()));

		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());

		return texture;
	}
	public Texture wideTexture() {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("graphics/wall-wide.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setDimensions(new double[] {600,50,700});
		setOutline(new Rectangle(getDimensions()));

		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());

		return texture;
	}

	@Override
	public void collision(Movable m) {
		// TODO Auto-generated method stub
	}
}