package movement;

import movement.Shapes.OutlineShape;

public abstract class Collidable extends Entity{	
	//filled with all the little cases for what to do when collide with different types of things.
	public abstract void collision();
	public abstract OutlineShape getOutline();
	public abstract void setOutline(OutlineShape outline);
}
