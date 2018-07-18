package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import movement.Room;

public class SpookyRoom extends Room {
	public SpookyRoom() {
		super();
		try {
			BufferedImage spooky = ImageIO.read(new File("graphics/spooky.png"));
			setBackground(spooky);
		} catch (IOException e) {
			System.out.println("where is ambrose?");
			System.exit(1);
		}
	}
}
