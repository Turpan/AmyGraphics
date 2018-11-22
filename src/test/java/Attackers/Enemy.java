package Attackers;

import movement.GameListener;
import movement.Movable;
import movement.mathDS.Vector;

public abstract class Enemy extends Attacker {
	private GameListener listener;
	private double attackCooldown;
	private double attackCounter;
	
	public Enemy(GameListener listener){
		super();
		setListener(listener);
	}
	protected Enemy(Enemy enemy){
		super(enemy);
		setListener(enemy.getListener());
		setAttackCooldown(enemy.getAttackCooldown());
		setAttackCounter(enemy.getAttackCounter());
	}
	
	public double getAttackCooldown() {
		return attackCooldown;
	}
	public void setAttackCooldown(double attackCooldown) {
		this.attackCooldown = attackCooldown;
	}
	public double getAttackCounter() {
		return attackCounter;
	}
	public void setAttackCounter(double attackCounter) {
		this.attackCounter = attackCounter;
	}
	public GameListener getListener() {
		return listener;
	}
	private void setListener(GameListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		this.listener = listener;
	}
	@Override
	public abstract Enemy clone();
	
	protected double[] calculateDirection() {
		float[] target = getDesiredPosition();
		double[] location = getPosition();
		double[] output = new double[Vector.DIMENSIONS];
		for (int i=0;i<Vector.DIMENSIONS;i++) {
			output[i] = target[i] - location[i];
		}
		return output;
	}
	@Override
	public void tick(){
		if (isActive()) {
			super.tick();
			if (canAttack() && attackReady()) {
				attack();
			} else if (!attackReady()) {
				attackCoolDownTick();
			}
		}
	}
	public boolean attackReady() {
		return (attackCounter == 0);
	}
	protected void attack() {
		getListener().createEntity(createAttack());
		attackCooldown();
	}
	protected void attackCooldown() {
		setAttackCounter(getAttackCooldown());
	}
	protected void attackCoolDownTick() {
		setAttackCounter(getAttackCounter() - Movable.TIMESCALE);
		if (getAttackCounter() < 0) {
			setAttackCounter(0);
		}
	}
	protected abstract float[] getDesiredPosition();
	public abstract boolean isActive();
	public abstract boolean canAttack();
	protected abstract Projectile createAttack();
}
