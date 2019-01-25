package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGLGraphics.base.GLGraphicsHandler;
import amyGraphics.Animation; 
import movement.Room;

public class CommunismRoom extends Room {
	Communism floor;
	Neutral neutral;
	Communism comm;
	Communism highComm;
	Communism farComm;
	TestLight light;
	TestDirLight dirLight;
	TestSquare square;
	TestLucy lucy;
	int tickCount;

	private boolean movingLeft;
	public CommunismRoom() {
		super();
		floor = new Communism();
		floor.setPosition(new double[] {-2000, -1000, -5000});
		floor.setDimensions(new double[] {15000, 100, 15000});
		neutral = new Neutral();
		neutral.setPosition(new double[] {-1000, 0, 2600});
		neutral.setDimensions(new double[] {10000, 4000, 100});
		comm = new Communism();
		comm.setPosition(new double[] {GLGraphicsHandler.viewWidth / 2, GLGraphicsHandler.viewHeight / 2, 1000});
		comm.setDimensions(new double[] {comm.getActiveTexture().getSprite().getWidth(), comm.getActiveTexture().getSprite().getHeight(), 500});
		farComm = new Communism();
		farComm.setPosition(new double[] {GLGraphicsHandler.viewWidth / 2, GLGraphicsHandler.viewHeight / 2, 10000});
		farComm.setDimensions(new double[] {comm.getActiveTexture().getSprite().getWidth(), comm.getActiveTexture().getSprite().getHeight(), 500});
		highComm = new Communism();
		highComm.setPosition(new double[] {GLGraphicsHandler.viewWidth / 2, 3000, 2000});
		highComm.setDimensions(new double[] {comm.getActiveTexture().getSprite().getWidth(), comm.getActiveTexture().getSprite().getHeight(), 500});
		square = new TestSquare();
		square.setPosition(new double[] {0, 0, 500});
		square.setDimensions(new double[] {500, 500, 500});
		light = new TestLight();
		light.setPosition(comm.getPosition());
		light.setDimensions(new double[] {50, 50, 50});
		dirLight = new TestDirLight();
		dirLight.setPosition(new double[] {-5000, 10000, 0});
		dirLight.setDimensions(new double[] {1000, 1000, 1000});
		lucy = new TestLucy();
		lucy.setPosition(new double[] {4000, 4000, 4000});
		lucy.setDimensions(new double[] {4000, 4000, 4000});

		BufferedImage[] background = new BufferedImage[6];
		for (int i=0; i<6; i++) {
			try {
				background[i] = ImageIO.read(new File("graphics/tests/skybox/skybox" + i + ".png"));
			} catch (IOException e) {
				System.out.println("where is picture?");
				System.exit(1);
			}
		}
		addBackground(background);
		setActiveBackground1(background);
		setActiveBackground2(background);
		addEntity(floor);
		addEntity(neutral);
		addEntity(comm);
		addEntity(farComm);
		addEntity(highComm);
		addEntity(square);
		addEntity(light);
		addEntity(dirLight);
		addEntity(lucy);
	}

	@Override
	public void tick() {
		tickCount++;
		if (movingLeft) {
			comm.getPosition()[0] -= 10;
			if (comm.getPosition()[0] <= 0) {
				comm.getPosition()[0] = 0;
				movingLeft = false;
			}
		} else {
			comm.getPosition()[0] += 10;
			if (comm.getPosition()[0] >= GLGraphicsHandler.viewWidth) {
				comm.getPosition()[0] = GLGraphicsHandler.viewWidth;
				movingLeft = true;
			}
		}

		double x = (comm.getPosition()[0] + (1000 * Math.cos(Math.toRadians(tickCount))));
		double y = (comm.getPosition()[1] + (1000 * Math.sin(Math.toRadians(tickCount))));
		double z = (comm.getPosition()[2] + (1000 * Math.sin(Math.toRadians(tickCount))));
		double[] pos = light.getPosition();
		light.setPosition(new double[] {x, pos[1], z});

		if (tickCount % 5 == 0) {
			Animation anim = (Animation) lucy.getActiveTexture();
			anim.nextFrame();
		}
	}

	public void addSquare(double x, double y, double z) {
		Communism square = new Communism();
		square.setPosition(new double[] {x, y, z});
		square.setDimensions(new double[] {comm.getActiveTexture().getSprite().getWidth(), comm.getActiveTexture().getSprite().getHeight(), 500});

		addEntity(square);
	}
}
