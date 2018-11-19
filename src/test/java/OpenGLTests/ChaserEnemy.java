package OpenGLTests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Attackers.Enemy;
import Attackers.Projectile;
import amyGraphics.Texture;
import movement.GameListener;
import movement.Entity.MalformedEntityException;
import movement.Shapes.Ellipse;
import movement.Shapes.OutlineShape;
import movement.mathDS.Vector.MalformedVectorException;

public class ChaserEnemy extends Enemy {
	static final double MASS = 10;
	static final double BASEMOVEFORCE = 500;
	static final double COEFFICIENT_OF_RESTITUTION = .0;	
	static final double COEFFICIENT_OF_DRAG = 0.005;			
	static final double  COEFFICIENT_OF_FRICTION = 0.5; 
	
	public ChaserEnemy(GameListener listener) throws MalformedEntityException, MalformedVectorException {
		super(listener);
		setMass(MASS);
		setBaseMoveForce(BASEMOVEFORCE);
		setCoF(COEFFICIENT_OF_FRICTION);
		setCoD(COEFFICIENT_OF_DRAG);
		setCoR(COEFFICIENT_OF_RESTITUTION);
		loadImage();
		setDimensions(new double[] {50,50,50});
		setOutline((OutlineShape)(new Ellipse(getDimensions())));
	}
	private void loadImage() throws MalformedEntityException {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("graphics/evilman.png"));
		} catch (IOException e) {
			System.exit(1);
		}
		Texture texture = new Texture(img);
		texture.setWidth(img.getWidth());
		texture.setHeight(img.getHeight());
		addTexture(texture);
		setActiveTexture(texture);
	}
	@Override
	public float[] getDesiredPosition() {
		return new float[] {getListener().getPlayerLocation().width, getListener().getPlayerLocation().height,0};
	}
	@Override
	public boolean isActive() {
		// TODO
		return true;
	}
	@Override
	public boolean canAttack() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	protected Projectile createAttack() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void collision() {
		// TODO Auto-generated method stub
		
	}
}
