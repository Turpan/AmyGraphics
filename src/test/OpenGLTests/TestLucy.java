package OpenGLTests;

import amyGraphics.Animation;
import lucyAnimation.LucyIO;
import movement.Entity;

public class TestLucy extends Entity {
	public TestLucy() {
		loadImage();
	}
	public void loadImage() {
		//Animation lucy = LucyIO.readLucyFile("lcy test/bepneutral.lcy");
		Animation lucy = LucyIO.readLucyFile("lcy test/test.lcy");
		setTexture(lucy);
	}
}
