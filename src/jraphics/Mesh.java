package jraphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * A class to represent a mesh (a collection of triangles that creates a fully 3D object)
 */

public class Mesh {
	
	//The list of all of the triangles
	ArrayList<Triangle> tris = new ArrayList<>();
	
	//A method to parse a mesh straight from an .obj file
	public boolean loadFromObject(String path) {
		
		ArrayList<Vector3> verts = new ArrayList<>();
		File file = new File(path);
		Scanner input;
		String nextLine;
		String[] parts;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			return false;
		}
		input.useDelimiter("\r");
		while(input.hasNext()) {
		    nextLine = input.nextLine();
		    parts = nextLine.split(" ");

		    //If the line starts with a "v" then it's a vertex, so we store it
		    if(parts[0].equals("v")) {
		    	Vector3 v = new Vector3(Double.parseDouble(parts[1]),Double.parseDouble(parts[2]),Double.parseDouble(parts[3]));
		    	verts.add(v);
		    }
		    
		    //If the line starts with an "f" then it's a face, so we construct it using the vertexes
		    if(parts[0].equals("f")) {
		    	int[] f = new int[3];
		    	f[0] = Integer.parseInt(parts[1]);
		    	f[1] = Integer.parseInt(parts[2]);
		    	f[2] = Integer.parseInt(parts[3]);
		    	tris.add(new Triangle(verts.get(f[0] -1), verts.get(f[1]-1), verts.get(f[2]-1)));		    	
		    }
		}
		input.close();
		
		return true;
	}
}
