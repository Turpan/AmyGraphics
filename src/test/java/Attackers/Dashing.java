package Attackers;


public interface Dashing {
	boolean canDash();
	boolean isDashing();
	void setDashCoolDown(int dashCoolDown);
	int getDashCoolDown();
	void setDashCounter(int dashCounter);
	int getDashCounter();
	void setDashCoolDownCount(double dashCoolDownCount);
	double getDashCoolDownCount();
	void dashCoolDownTick();
	void dashTick();
	void dash(double[] direction);
}
