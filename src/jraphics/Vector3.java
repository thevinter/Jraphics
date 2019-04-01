package jraphics;

/*
 * A class that represents a Vector in a three-dimensional space and its magnitude (usually 1 -> point)
 */

public class Vector3 {
	public double x,y,z,w;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
}