/*package movementTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import movement.Wall;
import movement.mathDS.Vector;

public class TestWall extends Wall{
	
	public TestWall(Vector angle) {
		setNormal (angle);
		setBounciness(1);
	}
	
	public void loadLongImage() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/wall-long.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setSprite(img);
		setDimensions(new double[] {20,600,70000});
	}
	public void loadWideImage() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/wall-wide.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		setSprite(img);
		setDimensions(new double[] {600,20,70000});
	}
}
*/