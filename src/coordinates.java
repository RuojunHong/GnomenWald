// the coordinate class creates and manipulates coordinates
public class coordinates {

	private double x;
	private double y;

	public coordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public coordinates() {
		this(0, 0);
	}

	// calculate the distance between two points
	public double length(coordinates a1, coordinates a2) {
		double length = Math.sqrt(Math.pow((a1.getX() - a2.getX()), 2)
				+ Math.pow((a1.getY() - a2.getY()), 2));
		return length;
	}

	// calculate the cosine value of two points
	public double cos(coordinates a1, coordinates a2) {
		double length = length(a1, a2);
		double cos_increment = (a1.getX() - a2.getX()) / length;

		return cos_increment;
	}

	// calculate the sine value of two points
	public double sin(coordinates a1, coordinates a2) {
		double length = length(a1, a2);
		double sin_increment = (a1.getY() - a2.getY()) / length;

		return sin_increment;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

}
