package movement;

import movement.Shapes.OutlineShape;

public abstract class Collidable extends Entity{
	private OutlineShape outline;
	
	public Collidable() {
	}
	protected Collidable(Collidable collidable) {
		super(collidable);
		setOutline(collidable.getOutline());
	}
	
	public abstract void collision(Movable m);				//for extra collisional effects.
	public void setOutline(OutlineShape outline) {
		this.outline = outline;
	}
	public OutlineShape getOutline() {
		return outline;
	}
	@Override
	public abstract Collidable clone();
}
