package behavior;

import ourcommunication.Server;
import common.Robot;
import sdp.vision.WorldState;
import lejos.robotics.subsumption.Behavior;

public abstract class GeneralBehavior implements Behavior {
	protected boolean isActive = false;
	protected WorldState ws;
	protected Robot r;
	protected Server s;
	
	public GeneralBehavior(WorldState ws, Robot r, Server s) {
		this.ws = ws;
		this.r = r;
		this.s = s;
	}
	
	@Override
	public void suppress() {
		setInActive();
	}
	
	public Robot getRobot() {
		return r;
	}

	public void setRobot(Robot r) {
		this.r = r;
	}
	
	public void setActive() {
		this.isActive = true;
	}
	
	public void setInActive() {
		this.isActive = false;
	}
	
	public boolean isActive() {
		return this.isActive;
	}

	public WorldState getWorldState() {
		return ws;
	}

	public void setWorldState(WorldState ws) {
		this.ws = ws;
	}
	
	
}
