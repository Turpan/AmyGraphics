package movement;

import movement.mathDS.Vector.MalformedVectorException;

public abstract class Projectile extends Attacker {
	public Projectile(double damage) throws MalformedVectorException {
		super();
		setDamage(damage);
		setMaxHealth(0);
	}
}
