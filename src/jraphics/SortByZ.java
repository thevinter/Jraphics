package jraphics;

import java.util.Comparator;

/*
 * An auxiliary class that implements a Comparator so we can apply Lambda functions to sort a list of triangles based on their median Z
 */

class SortByZ implements Comparator<Triangle> 
{ 
	
	public static final Comparator<Triangle> INSTANCE = new SortByZ();
	
	private double calcZ(Triangle tri) {
		return (tri.p.get(0).z + tri.p.get(1).z +tri.p.get(2).z) / 3.0;
	}

    public int compare(Triangle a, Triangle b) 
    { 
    	if(calcZ(a) < calcZ(b))
			return 1;
		else if (calcZ(a) > calcZ(b))
			return -1;
		else
			return 0;
    } 
} 