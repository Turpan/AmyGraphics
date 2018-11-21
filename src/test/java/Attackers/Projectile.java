package Attackers;


public abstract class Projectile extends Attacker {
	public Projectile(double damage) {
		super();
		setDamage(damage);
		setMaxHealth(0);
	}
}
