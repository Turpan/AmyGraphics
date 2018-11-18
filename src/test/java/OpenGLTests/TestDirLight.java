package OpenGLTests;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGraphics.Texture;
import movement.Light;
import movement.LightType;

public class TestDirLight extends Light{
	
	static final Color color = Color.WHITE;
	
	public TestDirLight() {
		super(LightType.DIRECTIONAL, color);
		loadImage();
		setAmbient(0.05);
		setDiffuse(0.7);
		setSpecular(0.7);
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
}
