package sdp.milestone;

import java.io.IOException;

import sdp.navigation.Movement;
import sdp.strategy.CommandHelper;
import sdp.vision.WorldState;

public class Milestone3GoToBall extends Thread {

	private WorldState mWorldState;
	private Movement mMovement;

	public Milestone3GoToBall(WorldState worldstate, CommandHelper ch) {
		mWorldState = worldstate;
		mMovement = ch.movement;
	}
	//TODO Alter for Attacker Robot
	private void setTarget() throws IOException{
		if ( mWorldState.getOurDefenderPosition().distance(mWorldState.getOppositionDefenderPosition()) < 80 ){
			mMovement.setTarget(mWorldState.getOurGoalCentre());
		}else{
			mMovement.setTarget(mWorldState.getBallPoint());
		}
	}

	public void run(){
		try{
			mMovement.setAvoidBall(false);

			
			do{
				setTarget();
				Thread.sleep(50);
			}
			while (mMovement.isMoving());
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
