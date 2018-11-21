package movement;

import movement.Shapes.OutlineShape;

public abstract class Collidable extends Entity{
	public abstract void collision(Movable m);				//for extra collisional effects.
	public abstract OutlineShape getOutline();
	public abstract void setOutline(OutlineShape outline);
}
