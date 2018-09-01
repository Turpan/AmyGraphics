package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGLGraphics.entitys.GLEntity;
import amyGraphics.Animation;
import movement.Entity.MalformedEntityException;
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
	public CommunismRoom() throws MalformedEntityException {
		super();
		floor = new Communism();
		floor.setPosition(new float[] {-2000, -1000, -5000});
		floor.setDimensions(new double[] {15000, 100, 15000});
		neutral = new Neutral();
		neutral.setPosition(new float[] {-1000, 0, 2600});
		neutral.setDimensions(new double[] {10000, 4000, 100});
		comm = new Communism();
		comm.setPosition(new float[] {GLEntity.viewWidth / 2, GLEntity.viewHeight / 2, 1000});
		comm.setDimensions(new double[] {comm.getTexture().getSprite().getWidth(), comm.getTexture().getSprite().getHeight(), 500});
		farComm = new Communism();
		farComm.setPosition(new float[] {GLEntity.viewWidth / 2, GLEntity.viewHeight / 2, 10000});
		farComm.setDimensions(new double[] {comm.getTexture().getSprite().getWidth(), comm.getTexture().getSprite().getHeight(), 500});
		highComm = new Communism();
		highComm.setPosition(new float[] {GLEntity.viewWidth / 2, 3000, 2000});
		highComm.setDimensions(new double[] {comm.getTexture().getSprite().getWidth(), comm.getTexture().getSprite().getHeight(), 500});
		square = new TestSquare();
		square.setPosition(new float[] {0, 0, 500});
		square.setDimensions(new double[] {500, 500, 500});
		light = new TestLight();
		light.setPosition(comm.getPosition());
		light.setDimensions(new double[] {50, 50, 50});
		dirLight = new TestDirLight();
		dirLight.setPosition(new float[] {-5000, 10000, 0});
		dirLight.setDimensions(new double[] {1000, 1000, 1000});
		lucy = new TestLucy();
		lucy.setPosition(new float[] {4000, 4000, 4000});
		lucy.setDimensions(new double[] {4000, 4000, 4000});
		
		BufferedImage[] background = new BufferedImage[6];
		for (int i=0; i<6; i++) {
			try {
				background[i] = ImageIO.read(new File("graphics/skybox/skybox" + i + ".png"));
			} catch (IOException e) {
				System.out.println("where is picture?");
				System.exit(1);
			}
		}
		setBackground(background);
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
			if (comm.getPosition()[0] >= GLEntity.viewWidth) {
				comm.getPosition()[0] = GLEntity.viewWidth;
				movingLeft = true;
			}
		}
		
		float x = (float) (comm.getPosition()[0] + (1000 * Math.cos(Math.toRadians(tickCount))));
		float y = (float) (comm.getPosition()[1] + (1000 * Math.sin(Math.toRadians(tickCount))));
		float z = (float) (comm.getPosition()[2] + (1000 * Math.sin(Math.toRadians(tickCount))));
		float[] pos = light.getPosition();
		try {
			light.setPosition(new float[] {x, pos[1], z});
		} catch (MalformedEntityException e) {
			System.exit(-1);
		}
		
		if (tickCount % 5 == 0) {
			Animation anim = (Animation) lucy.getTexture();
			anim.nextFrame();
		}
	}
	
	public void addSquare(float x, float y, float z) {
		Communism square = new Communism();
		try {
			square.setPosition(new float[] {x, y, z});
			square.setDimensions(new double[] {comm.getTexture().getSprite().getWidth(), comm.getTexture().getSprite().getHeight(), 500});
		} catch (MalformedEntityException e) {
			System.exit(-1);
		}
		addEntity(square);
	}
}
