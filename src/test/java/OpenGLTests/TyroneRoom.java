package OpenGLTests;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGLGraphics.IO.ButtonState;
import movement.Entity;
import movement.Entity.MalformedEntityException;
import movement.GameListener;
import movement.Room;
import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;

public class TyroneRoom extends Room implements GameListener {
	
	static final double[][][] directionTable = new double[][][]
			{{{0.7071,0.7071,0}, {0,1,0}, {-0.7071,0.7071,0}},
			{{1,0,0},null, {-1,0,0}},
			{{0.7071,-0.7071,0}, {0,-1,0}, {-0.7071,-0.7071,0}}};
	
	private Player player;
	
	private ChaserEnemy chaser;
	
	private ChaserEnemy chaser2;
	
	public TyroneRoom() {
		super();
		
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
		
		try {
			createEntitys();
		} catch (MalformedEntityException | MalformedVectorException e) {
			System.exit(-1);
		}
	}
	
	@Override
	public void tick() {
		try { 
			player.applyConstantForces();
			chaser.applyConstantForces();
			chaser2.applyConstantForces();
//			System.out.println(player.getMass() *Math.pow(player.getVelocity().getMagnitude(),2) + chaser.getMass() * Math.pow(chaser.getVelocity().getMagnitude(),2) + chaser2.getMass() * Math.pow(chaser2.getVelocity().getMagnitude(),2));
			checkCollision();
			player.tick();
			chaser.tick();
			chaser2.tick();
		} catch (MalformedVectorException | MalformedEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		processInput();
	}
	
	private void createEntitys() throws MalformedEntityException, MalformedVectorException {
		player = new Player();
		player.setPosition(new float[] {100,125,50});
		
		chaser = new ChaserEnemy(this);
//		chaser.setMass(22.5);
//		chaser.setBaseMoveForce(1000000);
		chaser.setPosition(new float[] {200,361,50});
		
		chaser2 = new ChaserEnemy(this);
//		chaser2.setBaseMoveForce(1000);
		chaser2.setPosition(new float[] {200,409,50});
		
		TestWall longWallL = new TestWall(false);
		TestWall longWallR = new TestWall(false);
		TestWall wideWallT = new TestWall(true);
		TestWall wideWallB = new TestWall(true);
		longWallL.setPosition(new float[] {0,0,-350});
		longWallR.setPosition(new float[] {580,0,-350});
		wideWallT.setPosition(new float[] {0,300,-350});
		wideWallB.setPosition(new float[] {0,580,-350});
		
		TestDirLight dirLight = new TestDirLight();
		dirLight.setPosition(new float[] {-5000, 10000, 0});
		dirLight.setDimensions(new double[] {1000, 1000, 1000});
		
		addEntity(player);
		addEntity(chaser);
		addEntity(chaser2);
		
		addEntity(longWallL);
		addEntity(longWallR);
		addEntity(wideWallT);
		addEntity(wideWallB);
		
		addEntity(dirLight);
	}
	
	private double[] calculateDirection() {
		int x = 1;
		int y = 1;
		if (ButtonState.getPlayerMoveUpPressed()) y -= 1;
		if (ButtonState.getPlayerMoveDownPressed()) y += 1;
		if (ButtonState.getPlayerMoveLeftPressed()) x -= 1;
		if (ButtonState.getPlayerMoveRightPressed()) x += 1;
		return directionTable[y][x];
	}
	
	private void processInput() {
		if (ButtonState.getPlayerDashPressed()) {
			try {
				player.dash(calculateDirection());
			} catch (MalformedVectorException e1) {
				e1.printStackTrace();
			}
		}
		
		if (ButtonState.getStopPressed()) {
			try {
				System.out.print(chaser2.getPosition()[0]);
				System.out.print(chaser2.getPosition()[1]);
				System.out.println(chaser2.getPosition()[2]);
				player.stop();
				chaser.stop();
				chaser2.stop();
			} catch (MalformedVectorException e1) {
				e1.printStackTrace();
			}
		}
		
		if (ButtonState.getPlayerMoveUpPressed() || ButtonState.getPlayerMoveDownPressed() || 
				ButtonState.getPlayerMoveLeftPressed() || ButtonState.getPlayerMoveRightPressed()) {
			try {
				var direction = calculateDirection();
				if (direction != null) { 
					var reverseDir = new double[] {-direction[0], -direction[1],-direction[2]};
					chaser2.locomote(direction);
					chaser.locomote(reverseDir);
				}
			} catch (MalformedVectorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public Dimension getPlayerLocation() {
		return new Dimension((int) player.getPosition()[0], (int) player.getPosition()[1]);
	}

	@Override
	public void createEntity(Entity entity) {
		this.addEntity(entity);
	}
}
