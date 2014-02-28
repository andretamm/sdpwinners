public class MoveDirection {

	public static int EAST = 0;
	public static int NORTH = 1;
	public static int WEST = 2;
	public static int SOUTH = 3;

	static int forward = (byte) 1;
	static int backward = (byte) 2;
	static int off = (byte) 0;

	static double MAXIMUMSPEED = 200;

	public static void main(String[] args) {
		diagonalSpeeds(45);
	}

	/*
	 * Given an angle in radians the method prints the address of the wheel and
	 * speed it need to move in to achieve the desired angle of movement.
	 */

	public static void diagonalSpeeds(double mMoveDirection) {
		String eastDirection;
		String northDirection;
		String westDirection;
		String southDirection;

		// Get the sin and cos of the movement direction
		mMoveDirection = Math.toRadians(mMoveDirection);

		final double cosDirection = Math.cos(mMoveDirection);
		final double sinDirection = Math.sin(mMoveDirection);

		System.out.println("cosDirection = " + cosDirection);
		System.out.println("sinDirection = " + sinDirection);

		double[] motorSpeed = new double[4];
		double maxSpeed = 0;

		for (int i = 0; i < 4; ++i) {
			double angle = Math.PI;

			/*
			 * 0 -> 0x01 - EAST 1 -> 0x03 - NORTH 2 -> 0x07 - WEST 3 -> 0x05 -
			 * SOUTH
			 */
			if (i == EAST) {
				angle = Math.PI / 2;
			}

			if (i == NORTH) {
				angle = Math.PI;
			}

			if (i == WEST) {
				angle = Math.PI / 2;
			}

			if (i == NORTH) {
				angle = Math.PI;
			}

			motorSpeed[i] = 0;
			motorSpeed[i] += Math.cos(angle) * cosDirection + Math.sin(angle)
					* sinDirection;

			maxSpeed = Math.max(Math.abs(motorSpeed[i]), maxSpeed);
		}

		for (int i = 0; i < motorSpeed.length; ++i) {
			motorSpeed[i] *= MAXIMUMSPEED / maxSpeed;
		}

		for (int i = 0; i < 4; ++i) {
			motorSpeed[i] = Math.min(MAXIMUMSPEED,
					(int) Math.round(motorSpeed[i]));
		}

		System.out.println("EAST: " + motorSpeed[0]);
		System.out.println("NORTH: " + motorSpeed[1]);
		System.out.println("WEST: " + motorSpeed[2]);
		System.out.println("SOUTH: " + motorSpeed[3]);

		System.out.println();

		System.out.println("EAST: " + Math.abs(motorSpeed[0]) + " "
				+ returnDirection(motorSpeed[0], EAST));
		System.out.println("NORTH: " + Math.abs(motorSpeed[1]) + " "
				+ returnDirection(motorSpeed[1], NORTH));
		System.out.println("WEST: " + Math.abs(motorSpeed[2]) + " "
				+ returnDirection(motorSpeed[2], WEST));
		System.out.println("SOUTH: " + Math.abs(motorSpeed[3]) + " "
				+ returnDirection(motorSpeed[3], SOUTH));

	}

	public static int returnDirection(double motorSpeed, int motor) {
		System.out.println("Calling with motorSpeed: " + motorSpeed
				+ " and motor: " + motor);
		int direction = off;

		if (motorSpeed > 0) {
			if (motor == EAST) {
				direction = forward;
			}

			if (motor == NORTH) {
				direction = forward;
			}

			if (motor == WEST) {
				direction = backward;
			}

			if (motor == SOUTH) {
				direction = backward;
			}
		} else if (motorSpeed < 0) {
			if (motor == EAST) {
				direction = backward;
			}

			if (motor == NORTH) {
				direction = backward;
			}

			if (motor == WEST) {
				direction = forward;
			}

			if (motor == SOUTH) {
				direction = forward;
			}
		}

		return direction;
	}


}
