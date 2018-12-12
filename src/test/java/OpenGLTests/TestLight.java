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
		setAmbient(0.0);
		setDiffuse(0.7);
		setSpecular(0.7);
	}
	protected TestLight(TestLight tl) {
		super(tl);
	}
	
	@Override
	public TestLight clone() {
		return new TestLight(this);
	}

	public void loadImage() {
		
	}
}
