package movementTest;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import movement.CollisionEngine;
import movement.Entity;
import movement.Entity.MalformedEntityException;
import movement.GameListener;
import movement.mathDS.Force;
import movement.mathDS.Vector;
import movement.mathDS.Vector.MalformedVectorException;
import paintedPanel.PaintedPanel;

public class MovementDemo implements KeyListener, ActionListener, GameListener{
	static final double[][][] directionTable = new double[][][]
			{{{-0.7071,-0.7071,0}, {0,-1,0}, {0.7071,-0.7071,0}},
			{{-1,0,0},null, {1,0,0}},
			{{-0.7071,0.7071,0}, {0,1,0}, {0.7071,0.7071,0}}};
	boolean rightKey;
	boolean leftKey;
	boolean upKey;
	boolean downKey;
	int counter;
	PaintedPanel playerLabel;
	PaintedPanel chaserLabel;
	PaintedPanel chaserLabel2;
	PaintedPanel longWallLLabel;
	PaintedPanel longWallRLabel;
	PaintedPanel wideWallTLabel;
	PaintedPanel wideWallBLabel;
	Player player;
	ChaserEnemy chaser;
	ChaserEnemy chaser2;
	Timer tickTimer;
	TestWall longWallL;
	TestWall longWallR;
	TestWall wideWallT;
	TestWall wideWallB;
	List<Entity> roomContents = new ArrayList<Entity>();
	CollisionEngine collisionCheck;
	MovementDemo() throws MalformedEntityException, MalformedVectorException {
		createVariables();
	}

	private void createVariables() throws MalformedEntityException, MalformedVectorException {
		DemoMain.frame.requestFocus();
		DemoMain.frame.addKeyListener(this);
		playerLabel = new PaintedPanel();
		playerLabel.setOpaque(false);
		player = new Player();
		playerLabel.bgImage = new ImageIcon(player.getSprite());
		tickTimer = new Timer(10, this);
		collisionCheck = new CollisionEngine();
		player.setPosition(new float[] {100,125,50});
		playerLabel.setBounds(100, 150, 50, 50);
		chaserLabel = new PaintedPanel();
		chaserLabel.setOpaque(false);
		chaser = new ChaserEnemy(this);
		chaser.setPosition(new float[] {200,325,50});
		chaserLabel.bgImage = new ImageIcon(chaser.getSprite());
		chaserLabel.setBounds(200, 49, 50, 50);
		chaserLabel2 = new PaintedPanel();
		chaserLabel2.setOpaque(false);
		chaser2 = new ChaserEnemy(this);
		chaser2.setPosition(new float[] {300,325,50});
		chaserLabel2.bgImage = new ImageIcon(chaser2.getSprite());
		chaserLabel2.setBounds(100, 49, 50, 50);
		chaser2.setMass(1);
		longWallLLabel = new PaintedPanel();
		longWallRLabel = new PaintedPanel();
		wideWallTLabel = new PaintedPanel();
		wideWallBLabel = new PaintedPanel();
		longWallLLabel.setOpaque(false);
		longWallRLabel.setOpaque(false);
		wideWallTLabel.setOpaque(false);
		wideWallBLabel.setOpaque(false);
		longWallLLabel.setBounds(0,0, 20, 600);
		longWallRLabel.setBounds(580,0, 20, 600);
		wideWallTLabel.setBounds(0,300, 600, 20);
		wideWallBLabel.setBounds(0,580, 600, 20);
		longWallL = new TestWall(new Vector(1, new double[] {1,0,0}));
		longWallR = new TestWall(new Vector(1, new double[] {-1,0,0}));
		wideWallT = new TestWall(new Vector(1, new double[] {0,1,0}));
		wideWallB = new TestWall(new Vector(1, new double[] {0,-1,0}));
		longWallL.setPosition(new float[] {0,0,-35000});
		longWallR.setPosition(new float[] {580,0,-35000});
		wideWallT.setPosition(new float[] {0,300,-35000});
		wideWallB.setPosition(new float[] {0,580,-35000});
		longWallL.loadLongImage();
		longWallR.loadLongImage();
		wideWallT.loadWideImage();
		wideWallB.loadWideImage();
		longWallLLabel.bgImage = new ImageIcon(longWallL.getSprite()); 
		longWallRLabel.bgImage = new ImageIcon(longWallR.getSprite());
		wideWallTLabel.bgImage = new ImageIcon(wideWallT.getSprite());
		wideWallBLabel.bgImage = new ImageIcon(wideWallB.getSprite());
		DemoMain.mainPanel.add(playerLabel);
		DemoMain.mainPanel.add(chaserLabel);
		DemoMain.mainPanel.add(chaserLabel2);
		DemoMain.mainPanel.add(longWallLLabel);
		DemoMain.mainPanel.add(longWallRLabel);
		DemoMain.mainPanel.add(wideWallTLabel);
		DemoMain.mainPanel.add(wideWallBLabel);
		roomContents.add(player);
		roomContents.add(chaser);
		roomContents.add(chaser2);
		roomContents.add(longWallL);
		roomContents.add(longWallR);
		roomContents.add(wideWallT);
		roomContents.add(wideWallB);
		tickTimer.start();
		collisionCheck = new CollisionEngine(roomContents);
	}
	double[] calculateDirection() {
		int x = 1;
		int y = 1;
		if (upKey) y -= 1;
		if (downKey) y += 1;
		if (leftKey) x -= 1;
		if (rightKey) x += 1;
		return directionTable[y][x];
	}
	
	public void keyPressed(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_W) {
			upKey = true;
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			downKey = true;
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			leftKey = true;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			rightKey = true;
		} else if (e.getKeyCode() == KeyEvent.VK_P) {
			try {
				player.stop();
				chaser.stop();
				chaser2.stop();
			} catch (MalformedVectorException e1) {
				e1.printStackTrace();
			}
		}
		else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				try {
					player.dash(calculateDirection());
				} catch (MalformedVectorException e1) {
					e1.printStackTrace();
				
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			upKey = false;
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			downKey = false;
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			leftKey = false;
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			rightKey = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		counter ++;
		try {
		if ((rightKey || leftKey || upKey || downKey) && calculateDirection() != null) {
			chaser2.locomote(calculateDirection());
		} 
		player.applyConstantForces();
		chaser.applyConstantForces();
		chaser2.applyConstantForces();
//		System.out.println(player.getMass() *Math.pow(player.getVelocity().getMagnitude(),2) + chaser.getMass() * Math.pow(chaser.getVelocity().getMagnitude(),2) + chaser2.getMass() * Math.pow(chaser2.getVelocity().getMagnitude(),2));
		player.tick();
		chaser.tick();
		chaser2.tick();
		collisionCheck.checkCollision();
		} catch (MalformedVectorException | MalformedEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		chaserLabel.setBounds((int) Math.round(chaser.getPosition()[0]), (int) Math.round(chaser.getPosition()[1]), (int)chaser.getDimensions()[0], (int)chaser.getDimensions()[1]);
		chaserLabel2.setBounds((int) Math.round(chaser2.getPosition()[0]), (int) Math.round(chaser2.getPosition()[1]), (int)chaser2.getDimensions()[0], (int)chaser2.getDimensions()[1]);
		playerLabel.setBounds((int) Math.round(player.getPosition()[0]), (int) Math.round(player.getPosition()[1]), (int)player.getDimensions()[0], (int)player.getDimensions()[1]);
		DemoMain.mainPanel.repaint();
		
	}

	@Override
	public Dimension getPlayerLocation() {
		return new Dimension((int) player.getPosition()[0], (int) player.getPosition()[1]);
	}

	@Override
	public void removeEntity(Entity entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createEntity(Entity entity) {
		// TODO Auto-generated method stub
		
	}
}
