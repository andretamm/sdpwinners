package sdp.milestone;

import java.awt.Point;
import java.io.IOException;

import sdp.communication.CommsInterface;
import sdp.navigation.Movement;
import sdp.navigation.Rotation;
import sdp.strategy.CommandHelper;
import sdp.strategy.KickFrom;
import sdp.vision.WorldState;

public class Milestone3Score extends Thread {

	private WorldState mWorldState;
	private Movement mMovement;
	private Rotation mRotation;
	private CommsInterface mComms;
	private CommandHelper mCommandHelper;

	public Milestone3Score(CommandHelper ch, CommsInterface comms, WorldState worldstate) {
		mWorldState = worldstate;
		mMovement = ch.movement;
		mRotation = ch.rotation;
		mComms = comms;
		mCommandHelper = ch;
	}
	
	public boolean isInFrontOfBall(){
		if ( mWorldState.getOppositionGoalCentre().x > mWorldState.getOurPosition().x){
			if ( mWorldState.getBallPoint().x < mWorldState.getOurPosition().x ){
				return true;
			}
		}else if (mWorldState.getOppositionGoalCentre().x < mWorldState.getOurPosition().x){
			if ( mWorldState.getBallPoint().x > mWorldState.getOurPosition().x ){
				return true;
			}
		}
		return false;
	}

	public void run() {
		try {
			System.out.println("in milestone3");
			mainLoop:
			while (true) {  //change true to !someoneScored()?
				final double rotationTolerance = 0.2;
				final double rotationSpeed = 0.30;
				
				mRotation.stopRotating();
				mMovement.stopMoving();
				mMovement.setAvoidBall(true);

				// move to kick location
				mMovement.setTarget(KickFrom.whereToKickFrom(mWorldState, 80));
				System.out.println("move!");
				while (mMovement.isMoving()) {
					Thread.sleep(100);
				}

				// rotate to the right angle
				System.out.println("rotate to kick location");
				double targetAngle = mCommandHelper.absAngleTo(mWorldState.getBallPoint());
				mRotation.setTargetAngle(targetAngle, rotationSpeed, rotationTolerance);
				while (Math.abs(mRotation.angleToTarget()) > rotationTolerance) {
					Thread.sleep(20);
				}
				mRotation.stopRotating();
				
				System.out.println("To TARGET" + mRotation.angleToTarget());

				// don't avoid the ball
				mMovement.setAvoidBall(false);
				mMovement.setTarget(mWorldState.getBallPoint());
				
				//wait till we get to the ball
				while ( mMovement.isMoving() && mWorldState.getBallVisible() ){
					Thread.sleep(50);
				}
				
				//go to the goal
				mMovement.setTarget(mWorldState.getOppositionGoalCentre());

				System.out.println("Go to goal!");
				while (Math.abs(mWorldState.getOppositionGoalCentre().x-mWorldState.getOurPosition().x) > 180) {
					
					boolean isInFrontOfBall = isInFrontOfBall();
					
					boolean isBallFarAway =  mWorldState.getBallPoint().distance(mWorldState.getOurPosition()) > 75;
					System.out.print("see ball?" + mWorldState.getBallVisible() + "        ");
					System.out.print("front of ball?" + isInFrontOfBall + "        ");
					System.out.println("isBallFarAway?" + isBallFarAway);
					if ( mWorldState.getBallVisible() && isInFrontOfBall ){
						System.out.println("Go again!");
						continue mainLoop;
					}
					Thread.sleep(50);
					
				}
				mMovement.stopMoving();
				
				Point bottom = new Point(mWorldState.getOppositionGoalBottom().x, mWorldState.getOppositionGoalBottom().y + 5 ); 
				Point top = new Point(mWorldState.getOppositionGoalTop().x, mWorldState.getOppositionGoalTop().y - 5 ); 
				
				 boolean needsToRotate = (int)(Math.signum(mCommandHelper.ourAngleTo((bottom)))) ==
								         (int)(Math.signum(mCommandHelper.ourAngleTo(top)));
				
				if ( needsToRotate ){
					Thread.sleep(1000);
					// move to kick location
					mMovement.setTarget(KickFrom.whereToKickFrom(mWorldState, 50));
					System.out.println("move!");
					while (mMovement.isMoving()) {
						Thread.sleep(100);
					}
					
					//rotate to face the goal
					targetAngle = mCommandHelper.absAngleTo(mWorldState.getOppositionGoalCentre());
					mRotation.setTargetAngle(targetAngle, rotationSpeed, rotationTolerance);
					while (Math.abs(mRotation.angleToTarget()) > rotationTolerance) {
						Thread.sleep(100);
					}
					mRotation.stopRotating();
					
					mMovement.setTarget(mWorldState.getOppositionGoalCentre());
					Thread.sleep(300);
				}
				mComms.kick();
				
				mMovement.stopMoving();
				
				Thread.sleep(1000);
				
				if ( mCommandHelper.someoneScored() ){
					break;
				}
			}
		} catch (Exception e) { //end the thread if interrupted...
			try {
				mMovement.stopMoving();
				mRotation.stopRotating();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
