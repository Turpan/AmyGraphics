package OpenGLTests;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import amyGLGraphics.IO.EventManager;
import movement.Entity;
import movement.GameListener;
import movement.Room;

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
			createEntitys();
		}

		@Override
		public void tick() {
			player.applyConstantForces();
			chaser.applyConstantForces();
			chaser2.applyConstantForces();
			//System.out.println(player.getMass() *Math.pow(player.getVelocity().getMagnitude(),2) + chaser.getMass() * Math.pow(chaser.getVelocity().getMagnitude(),2) + chaser2.getMass() * Math.pow(chaser2.getVelocity().getMagnitude(),2));
			checkCollision();
			player.tick();
			chaser.tick();
			chaser2.tick();

			processInput();
		}

		private void createEntitys() {
			player = new Player();
			player.setPosition(new double[] {100,125,50});

			chaser = new ChaserEnemy(this);
			//		chaser.setMass(22.5);
			//		chaser.setBaseMoveForce(1000000);
			chaser.setPosition(new double[] {200,361,50});

			chaser2 = new ChaserEnemy(this);
			//		chaser2.setBaseMoveForce(1000);
			chaser2.setPosition(new double[] {200,409,50});

			TestWall longWallL = new TestWall(false);
			TestWall longWallR = new TestWall(false);
			TestWall wideWallT = new TestWall(true);
			TestWall wideWallB = new TestWall(true);
			longWallL.setPosition(new double[] {0,0,-350});
			longWallR.setPosition(new double[] {580,0,-350});
			wideWallT.setPosition(new double[] {0,300,-350});
			wideWallB.setPosition(new double[] {0,580,-350});

			TestDirLight dirLight = new TestDirLight();
			dirLight.setPosition(new double[] {-5000, 10000, 0});
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
			if (EventManager.getManagerInstance().getMoveUp().isPressed()) y -= 1;
			if (EventManager.getManagerInstance().getMoveDown().isPressed()) y += 1;
			if (EventManager.getManagerInstance().getMoveLeft().isPressed()) x -= 1;
			if (EventManager.getManagerInstance().getMoveRight().isPressed()) x += 1;
			return directionTable[y][x];
		}

		private void processInput() {
			/*if (ButtonState.getPlayerDashPressed()) {
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
		}*/

			if (EventManager.getManagerInstance().getMoveUp().isPressed() ||
					EventManager.getManagerInstance().getMoveDown().isPressed() ||
					EventManager.getManagerInstance().getMoveLeft().isPressed() ||
					EventManager.getManagerInstance().getMoveRight().isPressed()) {
					var direction = calculateDirection();
					if (direction != null) {
						var reverseDir = new double[] {-direction[0], -direction[1],-direction[2]};
						chaser2.locomote(direction);
						chaser.locomote(reverseDir);
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
