package OpenGLTests;

import amyGraphics.Animation;
import lucyAnimation.LucyIO;
import movement.Entity;

public class TestLucy extends Entity {
	public TestLucy() {
		loadImage();
	}
	protected TestLucy(TestLucy tl) {
		super(tl);
	}
	
	@Override
	public TestLucy clone() {
		return new TestLucy(this);
	}
	
	public void loadImage() {
		//Animation lucy = LucyIO.readLucyFile("lcy test/bepneutral.lcy");
		Animation lucy = LucyIO.readLucyFile("lcy test/test.lcy");
		addTexture(lucy);
		setActiveTexture(lucy);
	}
}
