package OpenGLTests;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Light;
import movement.LightType;

public class TestLight extends Light{
	
	static final Color color = Color.WHITE;
	
	public TestLight() {
		super(LightType.POINT, color);
		loadImage();
		setAmbient(0.1);
		setDiffuse(1);
		setSpecular(1);
	}
	
	public void loadImage() {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/testsquare.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		Texture texture = new Texture(img);
		setTexture(texture);
	}

	@Override
	public boolean inside(float[] point) {
		// TODO Auto-generated method stub
		return false;
	}
}
