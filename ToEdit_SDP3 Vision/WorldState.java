public class WorldState {
	
	private int direction; // 0 = right, 1 = left.
	private int colour; // 0 = yellow, 1 = blue
	private int pitch; // 0 = main, 1 = side room
	private int blueGoalkeeperX;
	private int blueGoalkeeperY;
	private int blueStrikerX;
	private int blueStrikerY;
	private int yellowGoalkeeperX;
	private int yellowGoalkeeperY;
	private int yellowStrikerX;
	private int yellowStrikerY;
	private int ballX;
	private int ballY;
	private float blueGoalkeeperOrientation;
	private float blueStrikerOrientation;
	private float yellowGoalkeeperOrientation;
	private float yellowStrikerOrientation;
	private long counter;
  
	public WorldState() {
		
		/* control properties */
		this.direction = 0;
		this.colour = 0;
		this.pitch = 0;
		
		/* object properties */
		this.blueGoalkeeperX = 0;
		this.blueGoalkeeperY = 0;
		this.blueStrikerX = 0;
		this.blueStrikerY = 0;
		this.yellowGoalkeeperX = 0;
		this.yellowGoalkeeperY = 0;
		this.yellowStrikerX = 0;
		this.yellowStrikerY = 0;
		this.ballX = 0;
		this.ballY = 0;
		this.blueGoalkeeperOrientation = 0;
		this.blueStrikerOrientation = 0;
		this.yellowGoalkeeperOrientation = 0;
		this.yellowStrikerOrientation = 0;
	}
	
	
	public int getBlueGoalkeeperX() {
		return blueGoalkeeperX;
	}
	
	public int getBlueStrikerX() {
		return this.blueStrikerX;
	}
	public void setBlueGoalkeeperX(int blueX) {
		this.blueGoalkeeperX = blueX;
	}
	
	public void setBlueStrikerX(int blueX) {
		this.blueStrikerX = blueX;
	}
	
	
	public int getBlueGoalkeeperY() {
		return blueGoalkeeperY;
	}
	
	public int getBlueStrikerY() {
		return this.blueStrikerY;
	}
	
	public void setBlueGoalkeeperY(int blueY) {
		this.blueGoalkeeperY = blueY;
	}
	
	public void setBlueStrikerY(int blueY) {
		this.blueStrikerY = blueY;
	}
	
	
	public int getYellowGoalkeeperX() {
		return yellowGoalkeeperX;
	}
	
	public int getYellowStrikerX() {
		return this.yellowStrikerX;
	}
	public void setYellowGoalkeeperX(int blueX) {
		this.yellowGoalkeeperX = blueX;
	}
	
	public void setYellowStrikerX(int blueX) {
		this.yellowStrikerX = blueX;
	}
	
	
	public int getYellowGoalkeeperY() {
		return yellowGoalkeeperY;
	}
	
	public int getYellowStrikerY() {
		return this.yellowStrikerY;
	}
	
	public void setYellowGoalkeeperY(int blueY) {
		this.yellowGoalkeeperY = blueY;
	}
	
	public void setYellowStrikerY(int blueY) {
		this.yellowStrikerY = blueY;
	}
	
	
	
	public int getBallX() {
		return ballX;
	}
	public void setBallX(int ballX) {
		this.ballX = ballX;
	}
	public int getBallY() {
		return ballY;
	}
	public void setBallY(int ballY) {
		this.ballY = ballY;
	}
	
	

	public float getBlueGoalkeeperOrientation() {
		return blueGoalkeeperOrientation;
	}

	public void setBlueGoalkeeperOrientation(float blueOrientation) {
		this.blueGoalkeeperOrientation = blueOrientation;
	}
	
	public float getBlueStrikerOrientation() {
		return blueStrikerOrientation;
	}

	public void setBlueStrikerOrientation(float blueOrientation) {
		this.blueStrikerOrientation = blueOrientation;
	}
	
	

	public float getYellowGoalkeeperOrientation() {
		return yellowGoalkeeperOrientation;
	}

	public void setYellowGoalkeeperOrientation(float yellowOrientation) {
		this.yellowGoalkeeperOrientation = yellowOrientation;
	}
	
	public float getYellowStrikerOrientation() {
		return yellowStrikerOrientation;
	}

	public void setYellowStrikerOrientation(float yellowOrientation) {
		this.yellowStrikerOrientation = yellowOrientation;
	}
	

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}
  
  public void updateCounter() {
    this.counter++;
  }
  
  public long getCounter() {
    return this.counter;
  }
	
}
