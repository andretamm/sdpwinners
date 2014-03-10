package behavior;

import java.awt.Point;

import common.Robot;

import constants.RobotColour;
import constants.RobotType;
import sdp.navigation.AStarPathfinding;
import sdp.vision.WorldState;

public class TestNavigation {
	public static void main(String[] args) {
		
		WorldState worldstate = new WorldState();
		worldstate.setBallX(50);
		worldstate.setBallY(50);
		
		worldstate.setColour(RobotColour.YELLOW);
		worldstate.setRobotX(new Robot(RobotColour.YELLOW, RobotType.DEFENDER), 80);
		worldstate.setRobotY(new Robot(RobotColour.YELLOW, RobotType.DEFENDER), 80);
		
		AStarPathfinding pathfinding = new AStarPathfinding(worldstate);
		
		pathfinding.setAvoidBall(true);
		pathfinding.setTarget(new Point(20, 20));
		
		for (Point p : pathfinding.getPath()) {
			System.out.println(p.getX() + " " + p.getY());
		}
	}
}
