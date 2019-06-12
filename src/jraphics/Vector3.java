package jraphics;


/**
 *  * A class that represents a Vector in a three-dimensional space and its magnitude (usually it's 1, so it's a point)
 *
 * @author Nikita Brancatisano, Nicola Bettinzoli, Alex Cominelli
 */
public class Vector3 {
	public double x,y,z,w;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
	
	public Vector3() {
		this.x = this.y = this.z = 0;
		this.w = 1;
	}
}
