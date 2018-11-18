package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Attackers.Dashing;
import amyGraphics.Texture;
import movement.SelfPropelled;
import movement.Shapes.*;
import movement.mathDS.Force;
import movement.mathDS.Vector.MalformedVectorException;

public class Player extends SelfPropelled implements Dashing {

	static final double MASS =5;
	static final double BASEMOVEFORCE = 100;
	static final double COEFFICIENT_OF_RESTITUTION = 1;	//'bounciness' Used for collisions. See Controllable. Not the whole story, as I've attempted to disallow objects enterring other objects, regardless of CoR
	static final double COEFFICIENT_OF_DRAG = 0.00;			//coefficient of proportionality between quadratic drag and speed
	static final double  COEFFICIENT_OF_FRICTION = 0.; 	//the coefficient of proportionality between the constant drag and mass
	static final int DASHCOOLDOWN = 10;
	static final int DASHLENGTH = 3;
	static final int DASHMAGNITUDE = 2000;
	
	int dashCoolDown;
	double dashDirection;
	int dashCounter; //this will be used for checking i frames
	double dashCoolDownCount;
	Force dashForce;
	public Player() throws MalformedEntityException, MalformedVectorException {
		setBaseMoveForce(BASEMOVEFORCE);
		setMass(MASS);
		setDashCoolDown(DASHCOOLDOWN);
		setCoF(COEFFICIENT_OF_FRICTION);
		setCoD(COEFFICIENT_OF_DRAG);
		setCoR(COEFFICIENT_OF_RESTITUTION);
		loadImage();
		setDimensions(new double[] {50,50,50});
		setOutline(new Ellipse(getDimensions()));
	}
	private void loadImage() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/funnyman.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		
		setTexture(texture);
	}
	@Override
	public void tick() throws MalformedVectorException, MalformedEntityException{
		dashCoolDownTick();
		dashTick();
		if (isDashing()) {
			applyForce(dashForce);
			
		}		
		super.tick();
	}
	@Override
	public boolean canDash() {
		return (getDashCoolDownCount() <= 0);
	}
	@Override
	public boolean isDashing() {
		return (getDashCounter() > 0);
	}
	@Override
	public void setDashCoolDown(int dashCoolDown) {
		this.dashCoolDown = dashCoolDown;
	}
	@Override
	public void setDashCounter(int dashCounter) {
		this.dashCounter= dashCounter;
	}
	@Override
	public int getDashCoolDown() {
		return dashCoolDown;
	}
	@Override
	public int getDashCounter() {
		return dashCounter;
	}
	@Override
	public void setDashCoolDownCount(double dashCoolDownCount) {
		this.dashCoolDownCount = dashCoolDownCount;
	}
	@Override
	public double getDashCoolDownCount() {
		return dashCoolDownCount;
	}
	@Override
	public void dashCoolDownTick() {
		setDashCoolDownCount(getDashCoolDownCount() - TIMESCALE);
	}
	@Override
	public void dashTick() {
		setDashCounter(getDashCounter() - 1);
	}
	@Override
	public void dash(double[] direction) throws MalformedVectorException {
		if (!canDash()) {
			return;
		}
		setDashCounter(DASHLENGTH);
		dashForce = new Force(DASHMAGNITUDE, new double[] {0,1,0});
		setDashCoolDownCount(getDashCoolDown());
	}
	@Override
	public void collision() {
		// TODO Auto-generated method stub
		// you got to fill this in rone
	}
}
