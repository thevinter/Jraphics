package jraphics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * A class that represents a Triangle (a face). It is made of 3 vectors that connect the vertexes.
 */

public class Triangle {
	public Vector3[] p = new Vector3[3];
	public Color color;
	public Triangle(Vector3 a, Vector3 b, Vector3 c) {
		p[0] = a;
		p[1] = b;
		p[2] = c;
	}
	
	public Triangle() {
		p[0] = new Vector3();
		p[1] = new Vector3();
		p[2] = new Vector3();	
	}
}
