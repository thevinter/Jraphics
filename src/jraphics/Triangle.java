package jraphics;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;

/*
 * A class that represents a Triangle (a face). It is made of 3 vectors that connect the vertexes.
 */

public class Triangle {
	public ArrayList<Vector3> p;
	public Color color;
	public Triangle(Vector3 a, Vector3 b, Vector3 c) {
		p = new ArrayList<>();
		p.add(a);
		p.add(b);
		p.add(c);
	}
	
	public Triangle() {
		p = new ArrayList<>();
		p.add(new Vector3());
		p.add(new Vector3());
		p.add(new Vector3());	}
}
