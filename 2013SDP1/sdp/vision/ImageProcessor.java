package sdp.vision;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import common.Robot;

import constants.Colours;
import constants.Quadrant;
import constants.RobotColour;
import constants.RobotType;
import sdp.strategy.KickFrom;

/**
 * Based off group 5 2012 code, with large pieces of group 7 2012.
 * @author Thomas Wallace
 */
public class ImageProcessor {
	
	private Point lineFromUs;
	private Point lineFromOpponent;

        private WorldState worldState;
        private ThresholdsState ts;
        private BackgroundSubtraction bgSub = null;
        
        //Pixel indices of the pitch area
        private int top=0;
        private int bottom=480;
        private int left=0;
        private int right=640;
        
        public ImageProcessor(WorldState worldState, ThresholdsState ts) {
                this.worldState = worldState;
                this.ts = ts;
                
                //create the background subtracter
                bgSub = new BackgroundSubtraction();
                //TODO Alter for two robots Attacker
                lineFromUs = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOurDefenderPosition());
                lineFromOpponent = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOppositionDefenderPosition());
        }
        
        /**
         * Pipeline for methods that affect/read the camera image
         * @param image The camera image
         */
        public void processImage(BufferedImage image) {
        	
    		this.top=(int) worldState.getOuterPitchTopLeft().getY();
    		this.bottom=(int) worldState.getOuterPitchBottomRight().getY();
    		this.left=(int) worldState.getOuterPitchTopLeft().getX();
    		this.right=(int) worldState.getOuterPitchBottomRight().getX();

                //remove barrel distortion
                if (worldState.isShowNoDistortion()) {
                        DistortionFix.removeBarrelDistortion(image);
                }
                
				//normalise RGB vectors
				if (worldState.getNormaliseRGB()) {
					Normalisation.normaliseBufferedImage(image, top, bottom, left, right);
				}

                //subtract background
                if (worldState.subtractBackground()) {
                        try {
                                image=bgSub.subtractBackground(image, top, bottom, left, right);
                                image=bgSub.imageStandardization(image, top, bottom, left, right);
                        } catch (Exception e1) {
                                System.out.println("Failed to subtract background.");
                                e1.printStackTrace();
                                worldState.setSubtractBackground(false);
                        }
                }
                
                if (worldState.getRemoveShadows()) {
					Deshadow.deshadowImage(worldState, image, top, bottom, left, right);
				}
				
				/* ----------------------------- */
				/* THIS NEEDS TO BE PARALLELISED */
				/* ----------------------------- */
				// TODO MAKE PARALLEL
				// Everything is accessed through pitch
				PitchPoints pitch = new PitchPoints(worldState);
				
                if (worldState.isFindRobotsAndBall()) {
                	
                	//threshold to find green plates and grey dots
                    Thresholder.secondaryThresholds(image, pitch, ts, worldState);
                    
                    //threshold to find ball and robot Ts
                    Thresholder.initialThresholds(image, pitch, ts, worldState);
                    
                	//locate the robot Ts and the ball
                    // TODO
                    // Divide into 4 quadrants
                    
                	findRobotsAndBall(pitch);
                    
                    //get orientation of the two robots
                	allOrientation(pitch);
                }
                else {
                    //threshold all points in image, and collect matches in ObjectPoints op
                    Thresholder.simpleThresholds(image, pitch, ts, worldState, top, bottom, left, right);
                }
                
                /* ----------------------------- */
                /* END PARALLELISATION */
                /* ----------------------------- */
                
                //transfer the readied data in objectpoints op to the worldstate
                updateWorldState(pitch, worldState);
                //calculates and stores new object velocities in worldstate, stores point and timestamp history as well
                updateWorldStateVelocities(worldState);
                
                //Print the debug threshold graphics to screen
                Display.thresholds(image, pitch, ts);

            	//Display the custom drawables as stored in worldstate
                Display.renderDrawables(worldState, image);
                
                //Print the plate edges and other markers to the screen
                Display.markers(ts, image, pitch, worldState);
                //transfer the readied data in objectpoints op to the worldstate
                updateWorldState(pitch, worldState);
                //calculates and stores new object velocities in worldstate, stores point and timestamp history as well
                updateWorldStateVelocities(worldState);
        }
        
        /**
         * Stores the coords in ObjectPoints into the Worldstate, first applying barrel distortion correction
         * @param pp The results of the vision methods. Not yet barrel corrected.
         * @param ws The worldstate into which this data should be stored. This will be barrelcorrected.
         */
        public void updateWorldState(PitchPoints pp, WorldState ws) {
            Point ballP = DistortionFix.barrelCorrected(pp.getBallPosition());
            ws.setBallX((int) ballP.getX());
            ws.setBallY((int) ballP.getY());
            
            for (Robot r : Robot.listAll()) {
        		Point position = DistortionFix.barrelCorrected(pp.getRobotPosition(r.colour, r.type));
                ws.setRobotX(r, (int) position.getX());
                ws.setRobotY(r, (int) position.getY());
                ws.setRobotOrientation(r.type, r.colour, pp.getRobotOrientation(r.colour, r.type));
            }
        }
        
        /**
         * Updates the object histories, including timestamps, and uses the new history to compute new velocities.
         * @param ws The worldstate in which the velocities and histories should be updated
         */
        public void updateWorldStateVelocities(WorldState ws){
            Point[] ballHistory = ws.getBallHistory();
            Point currentBall = new Point(ws.ballX, ws.ballY);  //there seems to be 
            long[] ballTimes = ws.getBallTimes();
            updateHistory(ballHistory, ballTimes, currentBall);
            Point2D.Double ballVelocity = calcVelocity(ws.getBallHistory(),ws.getBallTimes());
            ws.setBallVelocity(ballVelocity);
            
            // Change velocities and histories for all the robots
            for (Robot r: Robot.listAll()) {
            	Point[] history = ws.getRobotHistory(r);
            	Point current = ws.getRobotPoint(r);
            	long[] timestamps = ws.getRobotTimestamps(r);
            	
            	// Add current location and timestamp
            	updateHistory(history, timestamps, current);
            	
            	// Find the robot's velocity
            	Point2D.Double velocity = calcVelocity(history, timestamps);
            	ws.setRobotVelocity(r, velocity);
            }
        }

        /**
         * Sets the orientation of the robots.
         * This data is stored in worldstate
         * @param image Use the robots in this image
         * @param op Relevant lists of points, which will be updated
         */
        public void allOrientation(PitchPoints pitchPoints) {        
            /*
            //For later
            //Create a list of all points that could be in the blue robot plate. Likewise for yellow.
            allocatePlatePoints(op.getGreenPoints(), op.getBlueGreenPlate(), op.getYellowGreenPlate(), op.getBlue(), op.getYellow());
            */
        	
        	for (RobotType rType : constants.RobotType.values()) {
        		for (RobotColour rColour : constants.RobotColour.values()) {
                    try {
                    	ArrayList<Point> greyPoints = pitchPoints.getColouredPoints(rColour, rType, Colours.GRAY);
                    	ArrayList<Point> greenPoints = pitchPoints.getColouredPoints(rColour, rType, Colours.GREEN);
                    	 
                    	double orientation = Orientation.findRobotOrientation(greyPoints, greenPoints, pitchPoints.getRobotQuadrant(rColour, rType));
                    	pitchPoints.setRobotOrientation(rColour, rType, orientation);
                    } catch (NoAngleException e) {
                    	//System.out.print("Blue robot NoAngleException: " + e.getMessage());
                    	//System.out.println("op.getBlueOrientation():" + op.getBlueOrientation());
                    	//System.out.println("blue position:(" + op.getBlue().getX() +", "+ op.getBlue().getY()+")");
                    }
        		}
        		
        	}
            
        }
        
        /**
         * Adds to each robot all the points that are close enough to be part of that robots green plate
         * @param plate All points that are plate coloured
         * @param robot1Plate Is set to all points that could be in robot 1's plate
         * @param robot2Plate Is set to all points that could be in robot 2's plate
         * @param robot1 The centre of robot 1
         * @param robot2 The centre of robot 2
         */
        public void allocatePlatePoints(ArrayList<Point> plate, ArrayList<Point> robot1Plate, ArrayList<Point> robot2Plate, Point robot1, Point robot2) {
            //allocate green points to blue robot or yellow robot
            for (int i=0; (i<plate.size()); i++) {
                if (Point.distance(robot1.getX(), robot1.getY(), plate.get(i).getX(), plate.get(i).getY()) < 32) {
                	robot1Plate.add(new Point((int) plate.get(i).getX(), (int) plate.get(i).getY()));
                } 
                if (Point.distance(robot2.getX(), robot2.getY(), plate.get(i).getX(), plate.get(i).getY()) < 32){
                	robot2Plate.add(new Point((int) plate.get(i).getX(), (int) plate.get(i).getY()));
                }
            }
        }
        
        /**
         * Returns the corners of a rectangular plate
         * @param plate The points making up the plate
         * @param robot1 The centre of the robot associated with this plate
         * @param robot2 The centre of the other robot
         * @return The four corners of the plate
         * @throws Exception If there are less than four points in the plate - how can we have unique corners?
         */
        public Point[] getSmallCorners(ArrayList<Point> plate, Point robot1, Point robot2) throws Exception {
            if (plate.size()<4) {
            	throw new Exception("Plate contains less than four corners");
            }
    		Point[] plate4Points = Plate.getCorners(plate, robot1, robot2);
            for (int i=0; i<plate4Points.length; i++) {
                plate4Points[i]=new Point((int) (plate4Points[i].getX()*0.88+robot1.getX()*0.12), (int) (plate4Points[i].getY()*0.88+robot1.getY()*0.12));
            }
            return plate4Points;
        }
        
        /**
         * Adds to greyPointsWithinPlate every point greyPoints which is contained by the quadrilateral plate4Points
         * @param plate4Points Four points defining a quadrilateral
         * @param greyPoints Will check which of these points are contained by plate4Points
         * @param greyPointsWithinPlate The grey points found inside plate4Points
         */
        public void greyPointsWithinPlate(Point[] plate4Points, ArrayList<Point> greyPoints, ArrayList<Point> greyPointsWithinPlate) {
			
            int[] rectbX={(int) (plate4Points[0].getX()),(int) (plate4Points[3].getX()), (int) (plate4Points[1].getX()), (int) (plate4Points[2].getX())};
            int[] rectbY={(int) (plate4Points[0].getY()), (int) (plate4Points[3].getY()), (int) (plate4Points[1].getY()), (int) (plate4Points[2].getY())};
            Polygon pblue = new Polygon(rectbX, rectbY, 4);

            //Find grey points within the rectangular plate         
            for (int i = 0; i < greyPoints.size(); i++) {
                if (pblue.contains(greyPoints.get(i))) {
                        greyPointsWithinPlate.add(greyPoints.get(i));
                }
            }
        }
        
        /**
         * Find the location of the ball and store it in PitchPoints.
         * @param pitch
         */
        public void findBall(PitchPoints pitch) {
        	int LINE = 50;
        	
        	/*
        	 * NB!!
        	 * All the Exception catch blocks are here to deal with the case if findMean gets an
        	 * empty list as an input. In that case we try to guesstimate where the ball is.
        	 */
        	
        	try {
        		// Get the position of the ball
				pitch.setBallPosition(Position.findMean(pitch.getPoints(Colours.RED)));
                Position.ballFilterPoints(pitch.getPoints(Colours.RED), pitch.getBallPosition());
                
                try {
                	//TODO Alter for Attacker Robot
                	worldState.setBallVisible(true);
    				pitch.setBallPosition(Position.findMean(pitch.getPoints(Colours.RED)));
                    lineFromUs = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOurDefenderPosition());
                    lineFromOpponent = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOppositionDefenderPosition());
                } catch (Exception e2) {
                	//No points left after filtering
                	if (KickFrom.distanceFromOrigin(lineFromUs) > LINE && KickFrom.distanceFromOrigin(lineFromOpponent) > LINE) {
                		//System.out.println("Assumming ball is where it was 1");
                		pitch.setBallPosition(new Point(worldState.getBallXVision(), worldState.getBallYVision()));
                	} else if (KickFrom.distanceFromOrigin(lineFromUs) <= LINE) {
                		//System.out.println("Assumming ball is moving with us 1");
                		//TODO Alter for Attacker Robot
                		pitch.setBallPosition(new Point( (int) (worldState.getOurDefenderXVision() + lineFromUs.getX()), (int) (worldState.getOurDefenderYVision() + lineFromUs.getY())));
                	} else if (KickFrom.distanceFromOrigin(lineFromOpponent) <= LINE) {
                		//System.out.println("Assumming ball is moving with them 1");
                		//TODO Alter for Attacker Robot
                		pitch.setBallPosition(new Point( (int) (worldState.getOppositionDefenderXVision() + lineFromOpponent.getX()), (int) (worldState.getOppositionDefenderYVision() + lineFromOpponent.getY())));
                	}
                	worldState.setBallVisible(false);
                }
			} catch (Exception e2) {
            	if (KickFrom.distanceFromOrigin(lineFromUs) > LINE && KickFrom.distanceFromOrigin(lineFromOpponent) > LINE) {
            		//System.out.println("Assumming ball is where it was 2");
            		pitch.setBallPosition(new Point(worldState.getBallXVision(), worldState.getBallYVision()));
            	} else if (KickFrom.distanceFromOrigin(lineFromUs) <= LINE) {
            		//System.out.println("Assumming ball is moving with us 2: " + (int) (worldState.getOurXVision() + lineFromUs.getX()) + " " + (int) (worldState.getOurYVision() + lineFromUs.getY()));
            		pitch.setBallPosition(new Point( (int) (worldState.getOurDefenderXVision() + lineFromUs.getX()), (int) (worldState.getOurDefenderYVision() + lineFromUs.getY())));
            		//TODO Alter for Attacker Robot
            	} else if (KickFrom.distanceFromOrigin(lineFromOpponent) <= LINE) {
            		//System.out.println("Assumming ball is moving with them 2");
            		//TODO Alter for Attacker Robot
            		pitch.setBallPosition(new Point( (int) (worldState.getOppositionDefenderXVision() + lineFromOpponent.getX()), (int) (worldState.getOppositionDefenderYVision() + lineFromOpponent.getY())));
            	}
            	worldState.setBallVisible(false);
			}
        }
        
        /**
         * Sets the positions of the centroids of the Robots and the Ball
         * @param pitch
         */
        public void findRobotsAndBall(PitchPoints pitch) {
        	findBall(pitch);
        	
        	for (RobotColour rc: RobotColour.values()) {
        		Colours c;
        	
        		if (rc == RobotColour.BLUE) {
        			c = Colours.BLUE;
        		} else {
        			c = Colours.YELLOW;
        		}
        		
        		for (RobotType rt: RobotType.values()) {
//        			System.out.println(rt + " " + rc + " " + pitch.getColouredPoints(rc, rt, c).size());
        			
        			// Filter out any coloured points too close to the ball
                	if (worldState.getBallVisible()) {
        	            Position.filterOutCircle(pitch.getColouredPoints(rc, rt, c), 
        	            					     pitch.getBallPosition(), 
        	            					     WorldState.ballRadius);  
        			}  
        			
        			try {
        				// Set the robot position to be the middle of the coloured points
						pitch.setRobotPosition(rc, rt, Position.findMean(pitch.getColouredPoints(rc, rt, c)));
						
						// Do K-Means magic
						/*pitch.setRobotPosition(
								rc, 
     			                rt, 
     			                KMeans.findOne(
     			                		pitch.getColouredPoints(rc, rt, c), 
     			                		new Point(worldState.getRobotX(rt, rc), worldState.getRobotY(rt, rc)), 
     			                		new Point((int) pitch.getRobotPosition(rc, rt).getX(), (int) pitch.getRobotPosition(rc, rt).getY()),
     			                		2));*/
						
						// Remove points that are too far from where we currently think the robot centre is
						Position.filterPoints(pitch.getColouredPoints(rc, rt, c),
								  			  pitch.getRobotPosition(rc, rt));
						
						// The robot position is the mean of the coloured points after filtering 
						pitch.setRobotPosition(rc, rt, Position.findMean(pitch.getColouredPoints(rc, rt, c)));
					} catch (Exception e) {
						// We don't have any coloured points left for this robot, set it to its "default" position
						// TODO set this to be its previous location instead together with copious error reporting
//						System.err.println("No points found for the robot (" + rc + " " + rt + ")");
						pitch.setRobotPosition(rc, rt, worldState.getDefaultPoint(rc));
					}
        		}
        	}
        }
        
        /**
         * Updates the given history and timestamps arrays. NB! This is done in-place
         * so your original array will be changed. This is (usually) what you want if you're
         * e.g. updating some values in a RobotMap.
         * 
         * The new timestamp will be gotten with System.currentTimeMillis()
         * @param history History of location
         * @param times History of timestamps for those locations
         * @param current The current location of the object
         */
        public void updateHistory(Point[] history, long[] times, Point current) {
        	for (int i = 0; i < history.length-1; i++) {
        		history[i] = history[i+1];
        		times[i] = times[i+1];
        	}
        	
        	history[history.length-1] = current;
        	times[history.length-1] = System.currentTimeMillis();
        }
        
        public Point2D.Double calcVelocity(Point[] history, long[] times) {
        	/*Point[] velo = new Point[4];
        	Point averageVelocity = new Point(0,0);
        	for (int i = 0; i < history.length-1; i++) {
        		velo[i] = new Point();
        		velo[i].setLocation((history[i+1].x - history[i].x)/((double)times[i]), (history[i+1].y - history[i].y)/((double)times[i]));
        		averageVelocity.setLocation(averageVelocity.x+, y)
//        	}*/
//        	Point2D.Double velo = new Point2D.Double();
//        	velo.setLocation((history[2].x-history[0].x)/(double)(times[2]-times[0]),
//        				(history[2].y-history[0].y)/(double)(times[2]-times[0]));
        	double historyx = 0;
        	double historyy = 0;
        	Long historyTimes = (long) 0;
        	for (int i=0; (i<3); i++) {
        		historyx=historyx+history[i+2].getX()-history[i].getX();
        		historyy=historyy+history[i+2].getY()-history[i].getY();
        		historyTimes=historyTimes+times[i+2]-times[i];
        	}
        	historyx = historyx/3;
        	historyy = historyy/3;
        	historyTimes = historyTimes/3;
        	Point2D.Double velo = new Point2D.Double(historyx/historyTimes, historyy/historyTimes);
        	return velo;
        }
}