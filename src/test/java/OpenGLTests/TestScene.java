package OpenGLTests;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;

import amyGraphics.Animation;
import amyGraphics.CentreLayout;
import amyGraphics.Component;
import amyGraphics.Container;
import amyGraphics.FontTexture;
import amyGraphics.Label;
import amyGraphics.Layout;
import lucyAnimation.LucyIO;

public class TestScene extends Container {
	public TestScene() {
		super(500, 500, 3000, 3000);
		
		setColour(new Color(0, 49, 83, 100));
		
		setVisible(true);
		
		Layout layout = new CentreLayout();
		
		setLayout(layout);
		
		Component animation = new TestAnimation();
		
		Label label = null;
		try {
			label = new TestLabel();
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		//addChild(animation);
		
		addChild(label);
	}
	
	public class TestAnimation extends Component {
		public TestAnimation() {
			super(2750, 500, 500, 500);
			
			Animation lucy = LucyIO.readLucyFile("lcy test/test.lcy");
			addTexture(lucy);
			
			setPreferredSize(500, 500);
			
			setActiveTexture(lucy);
			
			setColour(Color.RED);
			
			setVisible(true);
		}
	}
	
	private class TestLabel extends Label {
		public TestLabel() throws FontFormatException, IOException {
			setFont(FontTexture.createFontTexture("fonts/IrishPenny.ttf", true));
			
			setFontSize(100);
			
			setBounds(2750, 500, 500, 500);
			
			setText("Hello World!" + '\n' + "This is a fancy font.");
			
			setColour(new Color(0, 0, 0, 0));
			
			setFontColour(Color.BLACK);
			
			setVisible(true);
		}
	}
}

