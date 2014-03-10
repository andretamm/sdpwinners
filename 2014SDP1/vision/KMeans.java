package vision;

import java.awt.Point;
import java.util.ArrayList;

import vision.Position;

/**
 * Performs clustering tasks.
 * @author Thomas Wallace
 */

public class KMeans {

	/**
	 * Takes two initial cluster centres. Clusters the points among the two, returning the position 
	 * of the 'good' cluster.
	 * 
	 * @param points The (x,y) values of the points to be clustered
	 * @param initialBad Initial co-ords of the cluster to be discarded
	 * @param initialGood Initial co-ords of the cluster to be returned
	 * @param cutoff End the kmeans when the clusters move less than this between steps
	 * @throws Exception If one cluster has no points
	 */
	public static Point findOne(ArrayList<Point> points, Point initialBad, Point initialGood, double cutoff){
		assert(points.size()>0);
		Point oldBad = initialBad;
		Point oldGood = initialGood;
		int badX=0;
		int badY=0;
		int goodX=0;
		int goodY=0;
		int numBad=0;
		int numGood=0;
		boolean done=false;
		
		while (!done) {
			//g.setColor(new Color(0xFF000000));
			//g.drawOval((int) oldBad.getX()-2, (int) oldBad.getY()-2, 4, 4);
			//g.setColor(new Color(0xFFFFFFFF));
			//g.drawOval((int) oldGood.getX()-2, (int) oldGood.getY()-2, 4, 4);
			//System.out.println("oldBad:("+oldBad.getX()+", "+oldBad.getY()+")");
			//System.out.println("oldGood:("+oldGood.getX()+", "+oldGood.getY()+")");
			for (int i=0; (i<points.size()); i++) {
				if ((Position.sqrdEuclidDist((int) points.get(i).getX(), (int) points.get(i).getY(), (int) oldBad.getX(), (int) oldBad.getY()))
					< (Position.sqrdEuclidDist((int) points.get(i).getX(), (int) points.get(i).getY(), (int) oldGood.getX(),(int)  oldGood.getY()))) {
					badX=(int) (badX+points.get(i).getX());
					badY=(int) (badY+points.get(i).getY());
					numBad=numBad+1;
				}
				else {
					goodX=(int) (goodX+points.get(i).getX());
					goodY=(int) (goodY+points.get(i).getY());
					numGood=numGood+1;
				}
			}
			if (numGood==0) {
				// No points left, return the initial good point instead
				System.err.println("Clustering error: the returned cluster has no points");
				return initialGood;
			}
			else {
				if (numBad==0) {
					if (Point.distanceSq(oldGood.getX(), oldGood.getY(), goodX/numGood, goodY/numGood)<cutoff) {
						done=true;
					}
					oldGood = new Point(goodX/numGood, goodY/numGood);
				}
				else {
					if ((Point.distanceSq(oldBad.getX(), oldBad.getY(), badX/numBad, badY/numBad)
							+Point.distanceSq(oldGood.getX(), oldGood.getY(), goodX/numGood, goodY/numGood))<cutoff) {
						done=true;
					}
					oldBad = new Point(badX/numBad, badY/numBad);
					oldGood = new Point(goodX/numGood, goodY/numGood);
				}
			}
		}
		
		return oldGood;
	}

}
