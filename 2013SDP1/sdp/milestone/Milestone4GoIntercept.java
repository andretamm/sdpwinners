package sdp.milestone;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import sdp.navigation.Movement;
import sdp.strategy.CommandHelper;
import sdp.strategy.Intercept;
import sdp.vision.NoAngleException;
import sdp.vision.WorldState;

/*
 * @author: Simona Cartuta
 *  
 */

public class Milestone4GoIntercept extends Thread {
	private WorldState mWorldState;
	private Movement mMovement;
	static CommandHelper mCommhelp;
	//static CommsClient mCommsClient;
	
	public Milestone4GoIntercept(WorldState worldstate, CommandHelper commhelp) {
		mWorldState = worldstate;
		mCommhelp = commhelp;
		mMovement = mCommhelp.movement;
	}
	
	private void goToInterceptPoint() throws IOException, InterruptedException, NoAngleException {
		
		//TODO: implement getInterceptPoint();
		
		ArrayList<Point> interceptPoints = Intercept.interceptPoint(mWorldState);
		Point interceptPoint = interceptPoints.get(0);
		mCommhelp.facePoint(interceptPoint, 0.1, (float)0.2);
	    System.out.println("Should be facing point");
		mMovement.setTarget(interceptPoint);
	}

	public void run(){
		try{
			mMovement.setAvoidBall(false);

			
			do{
				goToInterceptPoint();
				Thread.sleep(50);
			}
			while (mMovement.isMoving());
			
		}catch(IOException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoAngleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
