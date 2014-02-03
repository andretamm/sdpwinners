package sdp.vision;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

//                this.top=pitchConstants.topBuffer;
//                this.bottom=image.getHeight()-pitchConstants.bottomBuffer;
//                this.left=pitchConstants.leftBuffer;
//                this.right=image.getWidth()-pitchConstants.rightBuffer;
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
                
                ObjectPoints op = new ObjectPoints();
                
				if (worldState.getRemoveShadows()) {
					Deshadow.deshadowImage(worldState, image, top, bottom, left, right);
				}
                
                if (worldState.isFindRobotsAndBall()) {
                    //threshold to find ball and robot Ts
                    Thresholder.initialThresholds(image, op, ts, top, bottom, left, right);
                	//locate the robot Ts and the ball
                	findRobotsAndBall(op);      
                    //threshold to find green plates and grey dots
                    Thresholder.secondaryThresholds(image, op, ts, worldState, top, bottom, left, right);
                    //get orientation of the two robots
                	allOrientation(op);
                }
                else {
                    //threshold all points in image, and collect matches in ObjectPoints op
                    Thresholder.simpleThresholds(image, op, ts, worldState, top, bottom, left, right);
                }
                
                //transfer the readied data in objectpoints op to the worldstate
                updateWorldState(op, worldState);
                //calculates and stores new object velocities in worldstate, stores point and timestamp history as well
                updateWorldStateVelocities(worldState);
                
                //Print the debug threshold graphics to screen
                Display.thresholds(image, op, ts);

            	//Display the custom drawables as stored in worldstate
                Display.renderDrawables(worldState, image);
                
                //Print the plate edges and other markers to the screen
                Display.markers(ts, image, op, worldState);
                //transfer the readied data in objectpoints op to the worldstate
                updateWorldState(op, worldState);
                //calculates and stores new object velocities in worldstate, stores point and timestamp history as well
                updateWorldStateVelocities(worldState);
        }
        
        /**
         * Stores the coords in ObjectPoints into the Worldstate, first applying barrel distortion correction
         * @param op The results of the vision methods. Not yet barrel corrected.
         * @param ws The worldstate into which this data should be stored. This will be barrelcorrected.
         */
        public void updateWorldState(ObjectPoints op, WorldState ws) {
            Point ballP = DistortionFix.barrelCorrected(op.getBall());
            ws.setBallX((int) ballP.getX());
            ws.setBallY((int) ballP.getY());

            Point blueP = DistortionFix.barrelCorrected(op.getBlue());
            ws.setBlueDefenderX((int) blueP.getX());
            ws.setBlueDefenderY((int) blueP.getY());
            ws.setBlueDefenderOrientation(op.getBlueOrientation());
            
            Point yellowP = DistortionFix.barrelCorrected(op.getYellow());
            ws.setYellowDefenderX((int) yellowP.getX());
            ws.setYellowDefenderY((int) yellowP.getY());
            ws.setYellowDefenderOrientation(op.getYellowOrientation());
        }
        
        /**
         * Updates the object histories, including timestamps, and uses the new history to compute new velocities.
         * @param ws The worldstate in which the velocities and histories should be updated
         */
        public void updateWorldStateVelocities(WorldState ws){
            Point[] ballHistory = ws.getBallHistory();
//          Point currentBall = ws.getBallPoint();
            Point currentBall = new Point(ws.getBallPoint().x, ws.getBallPoint().y);  //there seems to be 
            long[] ballTimes = ws.getBallTimes();
            updateHistory(ballHistory, ballTimes, currentBall, "ball");
            Point2D.Double ballVelocity = calcVelocity(ws.getBallHistory(),ws.getBallTimes());
            ws.setBallVelocity(ballVelocity);
            
            //TODO Alter for Attacker Robot
            Point[] ourHistory = ws.getOurDefenderHistory();
            Point ourCurrent = ws.getOurDefenderPosition();
            long[] ourTimes = ws.getOurTimes();
            updateHistory(ourHistory, ourTimes, ourCurrent, "our");
            Point2D.Double ourVelocity = calcVelocity(ws.getOurDefenderHistory(),ws.getOurTimes());
            ws.setBallVelocity(ourVelocity);
            
            //TODO Alter for Attacker Robot
            Point[] oppositionHistory = ws.getBallHistory();
            Point oppositionCurrent = ws.getBallPoint();
            long[] oppositionTimes = ws.getOppositionTimes();
            updateHistory(oppositionHistory, oppositionTimes, oppositionCurrent, "opp");
            Point2D.Double oppVelocity = calcVelocity(ws.getOppositionDefenderHistory(),ws.getOppositionTimes());
            ws.setBallVelocity(oppVelocity);
        }

        /**
         * Sets the orientation of the robots.
         * This data is stored in worldstate
         * @param image Use the robots in this image
         * @param op Relevant lists of points, which will be updated
         */
        public void allOrientation(ObjectPoints op) {        
            
            //Create a list of all points that could be in the blue robot plate. Likewise for yellow.
            allocatePlatePoints(op.getGreenPoints(), op.getBlueGreenPlate(), op.getYellowGreenPlate(), op.getBlue(), op.getYellow());
            
            //find blue robot plate corners
            try {
				op.setBlueGreenPlate4Points(getSmallCorners(op.getBlueGreenPlate(), op.getBlue(), op.getYellow()));
				greyPointsWithinPlate(op.getBlueGreenPlate4Points(), op.getGreyPoints(), op.getBlueGreyPoints());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}

            //find yellow robot plate corners
			try {
				op.setYellowGreenPlate4Points(getSmallCorners(op.getYellowGreenPlate(), op.getYellow(), op.getBlue()));
				greyPointsWithinPlate(op.getYellowGreenPlate4Points(), op.getGreyPoints(), op.getYellowGreyPoints());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
            
            /* Attempt to find the blue robot's orientation. */
            try {
                    op.setBlueOrientation((float) Orientation.findOrient(op.getBlue(), op.getBluePoints(), op.getBlueGreyPoints(), op.getBlueGreenPlate4Points(), 120, 500, true));
            } catch (NoAngleException e) {
//            	System.out.print("Blue robot NoAngleException: " + e.getMessage());
//            	System.out.println("op.getBlueOrientation():" + op.getBlueOrientation());
//            	System.out.println("blue position:(" + op.getBlue().getX() +", "+ op.getBlue().getY()+")");
            } catch (Exception e) {
            	//System.out.print("Blue robot Exception: " + e.getMessage());
            	//e.printStackTrace();
            }

            /* Attempt to find the yellow robot's orientation. */
            try {
                    op.setYellowOrientation((float) Orientation.findOrient(op.getYellow(), op.getYellowPoints(), op.getYellowGreyPoints(), op.getYellowGreenPlate4Points(), 120, 500, false));
            } catch (NoAngleException e) {
//            	System.out.print("Yellow robot NoAngleException: " + e.getMessage());
//            	System.out.println("op.getYellowOrientation():" + op.getYellowOrientation());
//           	System.out.println("yellow position:(" + op.getYellow().getX() +", "+ op.getYellow().getY()+")");
            } catch (Exception e) {
            	//System.out.print("Yellow robot Exception: " + e.getMessage());
            	//e.printStackTrace();
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
    		Point[] plate4Points = BackupPlate.getCorners(plate, robot1, robot2);
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
         * Sets the position of the centroid of the blue T, the yellow T, and the ball
         * @param op
         */
        public void findRobotsAndBall(ObjectPoints op) {
        	int LINE = 50;
        	try {
				op.setBall(Position.findMean(op.getBallPoints()));
				System.out.println(Position.findMean(op.getBallPoints()));
                Position.ballFilterPoints(op.getBallPoints(), op.getBall());
                try {
                	
                	//TODO Alter for Attacker Robot
                	worldState.setBallVisible(true);
    				op.setBall(Position.findMean(op.getBallPoints()));
                    lineFromUs = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOurDefenderPosition());
                    lineFromOpponent = KickFrom.subtractPoints(worldState.getBallPoint(), worldState.getOppositionDefenderPosition());
                } catch (Exception e2) {
                	//No points left after filtering
                	if (KickFrom.distanceFromOrigin(lineFromUs) > LINE && KickFrom.distanceFromOrigin(lineFromOpponent) > LINE) {
                		//System.out.println("Assumming ball is where it was 1");
                		op.setBall(new Point(worldState.getBallXVision(), worldState.getBallYVision()));
                	} else if (KickFrom.distanceFromOrigin(lineFromUs) <= LINE) {
                		//System.out.println("Assumming ball is moving with us 1");
                		//TODO Alter for Attacker Robot
                		op.setBall(new Point( (int) (worldState.getOurDefenderXVision() + lineFromUs.getX()), (int) (worldState.getOurDefenderYVision() + lineFromUs.getY())));
                	} else if (KickFrom.distanceFromOrigin(lineFromOpponent) <= LINE) {
                		//System.out.println("Assumming ball is moving with them 1");
                		//TODO Alter for Attacker Robot
                		op.setBall(new Point( (int) (worldState.getOppositionDefenderXVision() + lineFromOpponent.getX()), (int) (worldState.getOppositionDefenderYVision() + lineFromOpponent.getY())));
                	}
                	worldState.setBallVisible(false);
                }
			} catch (Exception e2) {
            	if (KickFrom.distanceFromOrigin(lineFromUs) > LINE && KickFrom.distanceFromOrigin(lineFromOpponent) > LINE) {
            		//System.out.println("Assumming ball is where it was 2");
            		op.setBall(new Point(worldState.getBallXVision(), worldState.getBallYVision()));
            	} else if (KickFrom.distanceFromOrigin(lineFromUs) <= LINE) {
            		//System.out.println("Assumming ball is moving with us 2: " + (int) (worldState.getOurXVision() + lineFromUs.getX()) + " " + (int) (worldState.getOurYVision() + lineFromUs.getY()));
            		op.setBall(new Point( (int) (worldState.getOurDefenderXVision() + lineFromUs.getX()), (int) (worldState.getOurDefenderYVision() + lineFromUs.getY())));
            		//TODO Alter for Attacker Robot
            	} else if (KickFrom.distanceFromOrigin(lineFromOpponent) <= LINE) {
            		//System.out.println("Assumming ball is moving with them 2");
            		//TODO Alter for Attacker Robot
            		op.setBall(new Point( (int) (worldState.getOppositionDefenderXVision() + lineFromOpponent.getX()), (int) (worldState.getOppositionDefenderYVision() + lineFromOpponent.getY())));
            	}
            	worldState.setBallVisible(false);
			}
			//this probably isn't a ball identification
			//if (op.getBallPoints().size()<2) {worldState.setBallVisible(false);}

			try {
				op.setBlue(Position.findMean(op.getBluePoints()));
                Position.filterPoints(op.getBluePoints(), op.getBlue());
                try {
    				op.setBlue(Position.findMean(op.getBluePoints()));
    			} catch (Exception e2) {
    				//System.out.println("Exception: Error finding mean of blue robot points");
    			}
			} catch (Exception e2) {
				//No points left after filtering
            	op.setBlue(worldState.getDefaultPoint(RobotColour.BLUE));
				//System.out.println("Exception: No points left in blue robot after filtering");
			}      
			
            //Filter out any yellow points too close to the ball
			if (worldState.getBallVisible()) {
	            Position.filterOutCircle(op.getYellowPoints(), op.getBall(), WorldState.ballRadius);  
			}          
			
            try {
				op.setYellow(Position.findMean(op.getYellowPoints()));
				
                //Filter out any yellow points that make up the blue robot
                try {
                	op.setYellow(KMeans.findOne(op.getYellowPoints(), new Point(worldState.getBlueDefenderXVision(), worldState.getBlueDefenderYVision()), 
                                        new Point((int) op.getYellow().getX(), (int) op.getYellow().getY()), 2));
                } catch (Exception e) {
                        //System.out.println("Kmeans to find yellow centre failed: "+e.getMessage());
                        e.printStackTrace();
                }
                
                Position.filterPoints(op.getYellowPoints(), op.getYellow());
                try {
					op.setYellow(Position.findMean(op.getYellowPoints()));
				} catch (Exception e2) {
					//No points left after filtering
    				//System.out.println("Exception: Error finding mean of yellow robot points");
				}
			} catch (Exception e2) {
				//No points left after filtering
				//System.out.println("Exception: No points left in yellow robot after filtering");
            	op.setYellow(worldState.getDefaultPoint(RobotColour.YELLOW));
			}
        }
        
        public void updateHistory(Point[] history, long[] times, Point current, String obj) {
        	for (int i = 0; i < history.length-1; i++) {
        		history[i] = history[i+1];
        		times[i] = times[i+1];
        	}
        	history[history.length-1] = current;
        	times[history.length-1] = System.currentTimeMillis();
        	
        	//TODO Alter for Attacker Robot
        	if (obj.equals("ball")){
        		worldState.setBallHistory(history);
        		worldState.setBallTimes(times);
        	} else if (obj.equals("our")){
        		worldState.setOurDefenderHistory(history);
        		worldState.setOurTimes(times);
          	} else if (obj.equals("opp")) {
          		worldState.setOppositionDefenderHistory(history);
        		worldState.setOppositionTimes(times);
          	}
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