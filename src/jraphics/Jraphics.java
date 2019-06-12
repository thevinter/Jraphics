package jraphics;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.*;

/**
 * 
 * A simple 3D rendering engine for .obj files made in Java. It features movement, lightning, clipping and parsing from files.
 * Collisions are not implemented.
 * 
 * This code is protected by GNU GPLv3, this means you must attribute me if you use it.
 * 
 * @author Nikita Brancatisano, Nicola Bettinzoli, Alex Cominelli
 */
public class Jraphics extends JPanel{
	Thread t;

	
	private static final long serialVersionUID = 1L;
	private static final int viewOffset = 5;
	private static final Color polyColor = Color.ORANGE;
	
	private static boolean isRunning = true;
	
	public ArrayList<Triangle> vecTrianglesToRaster = new ArrayList<Triangle>();
	public Mesh meshCube;
	public Mat4x4 matProj;
	public JFrame frame;
	
	private double fElapsedTime = 0;
	private long cTimeOld = 0;
	private Vector3 vCamera = new Vector3(0,0,0);
	private Vector3 vLookDir;
	private float fYaw;
	private float moveSpeed = 30;
	private float rotateSpeed = 4;
	//private float fTheta = 0;

	/**
	 * Override of the paintComponent method so it performs the calculations needed to draw the mesh.
	 */
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		doGameUpdates(g);	
	}
	
	
	public void gameLoop() {

		while(isRunning) {
			frame.repaint();
			long cTimeNow = System.currentTimeMillis();
			if(cTimeOld != 0)
				fElapsedTime = (double)((cTimeNow - cTimeOld) * 0.001);
			//fTheta += fElapsedTime;
			cTimeOld = cTimeNow;
			frame.repaint();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Main function that performs all the required calculations to project a 3D object into screen space
	 * @param g The graphics of the panel we're in
	 */
	private void doGameUpdates(Graphics g) {

		//Creation of some rotational matrixes
		Mat4x4 matRotZ = new Mat4x4();
		Mat4x4 matRotX = new Mat4x4();

		matRotZ = AlgebraUtility.MatrixRotationZ(Math.PI);
		matRotX = AlgebraUtility.MatrixRotationX(0);

		//Creation of a translation matrix so our model isn't at 0,0,0
		Mat4x4 matTrans ;
		matTrans = AlgebraUtility.MatrixMakeTranslation(0,2,viewOffset);

		//Creation of a world matrix that represents all the transformations of the objects
		Mat4x4 matWorld;
		matWorld = AlgebraUtility.MatrixMakeIdentity();
		matWorld = AlgebraUtility.MatrixMultiplyMatrix(matRotZ, matRotX);
		matWorld = AlgebraUtility.MatrixMultiplyMatrix(matWorld, matTrans);

		//Vectors needed for the camera
		Vector3 vUp = new Vector3(0,1,0);

		//Temp target
		Vector3 vTarget = new Vector3(0,0,1);

		//Rotation of the camera to look at the target
		Mat4x4 matCameraRot = AlgebraUtility.MatrixRotationY(fYaw);
		vLookDir = AlgebraUtility.MatrixMultiplyVector(matCameraRot, vTarget);
		vTarget = AlgebraUtility.VectorAdd(vCamera, vLookDir);
		Mat4x4 matCamera = AlgebraUtility.MatrixPointAt(vCamera, vTarget, vUp);
		Mat4x4 matView = AlgebraUtility.MatrixQuickInverse(matCamera);

		//Temp list to store triangles that we still have to sort
		vecTrianglesToRaster = new ArrayList<>();

		//Loop that works on all of the triangles in our mesh
		for(Triangle tri : meshCube.tris) {

			//Creation of some empty triangles needed for our transformation
			Triangle triProjected = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));
			Triangle triTransformed = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));
			Triangle triViewed = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));

			//Translating all the vertexes of the tri using the world matrix
			triTransformed.p[0] = AlgebraUtility.MatrixMultiplyVector(matWorld, tri.p[0]);
			triTransformed.p[1] = AlgebraUtility.MatrixMultiplyVector(matWorld, tri.p[1]);
			triTransformed.p[2] = AlgebraUtility.MatrixMultiplyVector(matWorld, tri.p[2]);

			//Creation of a normal and two lines
			Vector3 normal = new Vector3(0,0,0); Vector3 line1 = new Vector3 (0,0,0); Vector3 line2 = new Vector3(0,0,0);

			//Auxialiary lines needed to calculate the normal of each triangle
			line1 = AlgebraUtility.VectorSub(triTransformed.p[1], triTransformed.p[0]);
			line2 = AlgebraUtility.VectorSub(triTransformed.p[2], triTransformed.p[0]);

			//Calculation of the normal using the cross product
			normal = AlgebraUtility.VectorCrossProduct(line1, line2);

			//Normalization of the normal
			normal = AlgebraUtility.VectorNormalize(normal);

			//A vector used by the camera
			Vector3 vCameraRay = AlgebraUtility.VectorSub(triTransformed.p[0], vCamera);

			//If the projection of the normal on the view is lesser than 0 then we can actually see the triangle so we draw it
			if(AlgebraUtility.VectorDotProduct(normal, vCameraRay) < 0) {

				//Auxiliary vector we use to hardcode the light direction (coming from the player)
				Vector3 light_direction = new Vector3(0,1,-1);

				//We normalize it so we can work with unit vectors
				light_direction = AlgebraUtility.VectorNormalize(light_direction);

				//Here we calculate the dot product of the light and the normal so we can see how much the triangle deviates from our view
				double dp = Math.max(0.1, AlgebraUtility.VectorDotProduct(light_direction, normal));

				triViewed.p[0] = AlgebraUtility.MatrixMultiplyVector(matView, triTransformed.p[0]);
				triViewed.p[1] = AlgebraUtility.MatrixMultiplyVector(matView, triTransformed.p[1]);
				triViewed.p[2] = AlgebraUtility.MatrixMultiplyVector(matView, triTransformed.p[2]);

				//We clip each triangle with the near plane. This could return us 0,1 or 2 new triangles
				Triangle[] clipped = new Triangle[2];
				clipped = TriangleClipAgainstPlane(new Vector3(0,0,0.1), new Vector3(0,0,0.1), triViewed);
				int nClippedTriangles = AlgebraUtility.countNotNull(clipped);
				
				//We only save the clipped triangles
				for(int n = 0; n < nClippedTriangles; n++) {

					//We project each triangle using the projection matrix
					triProjected.p[0] = AlgebraUtility.MatrixMultiplyVector(matProj, clipped[n].p[0]);
					triProjected.p[1] = AlgebraUtility.MatrixMultiplyVector(matProj, clipped[n].p[1]);
					triProjected.p[2] = AlgebraUtility.MatrixMultiplyVector(matProj, clipped[n].p[2]);

					//Extra math
					triProjected.p[0] = AlgebraUtility.VectorDiv(triProjected.p[0], triProjected.p[0].w);
					triProjected.p[1] = AlgebraUtility.VectorDiv(triProjected.p[1], triProjected.p[1].w);
					triProjected.p[2] = AlgebraUtility.VectorDiv(triProjected.p[2], triProjected.p[2].w);

					//We offset the view by 1,1,0
					Vector3 vOffsetView = new Vector3(1,1,0);
					triProjected.p[0] = AlgebraUtility.VectorAdd(triProjected.p[0], vOffsetView);
					triProjected.p[1] = AlgebraUtility.VectorAdd(triProjected.p[1], vOffsetView);
					triProjected.p[2] = AlgebraUtility.VectorAdd(triProjected.p[2], vOffsetView);

					//We scale the coordinates based on our panel size
					triProjected.p[0].x *= 0.5 * (double) frame.getSize().width;
					triProjected.p[0].y *= 0.5 * (double) frame.getSize().height;
					triProjected.p[1].x *= 0.5 * (double) frame.getSize().width;
					triProjected.p[1].y *= 0.5 * (double) frame.getSize().height;
					triProjected.p[2].x *= 0.5 * (double) frame.getSize().width;
					triProjected.p[2].y *= 0.5 * (double) frame.getSize().height;

					//We create a temp triangle that is equivalent to the projected one so we can paint the inside
					Triangle temp = new Triangle(triProjected.p[0], triProjected.p[1], triProjected.p[2]);

					//We get the color based on the main color and the dot product of the normal and the light we calculated before
					temp.color = getColor(polyColor, 1-dp);

					//We add the triangle to the list we created before so we can sort it
					vecTrianglesToRaster.add(temp);	

				}
			}									
		}		

		//We sort the list so the triangles with the greatest Z get drawn last
		Collections.sort(vecTrianglesToRaster, SortByZ.INSTANCE);		

		//For each triangle in the sorted list we draw them
		for(Triangle tri : vecTrianglesToRaster) {

			Triangle[] clipped = new Triangle[2];
			Queue<Triangle> listTri = new LinkedList<>();
			listTri.add(tri);
			int newTriangles = 1;

			for (int p = 0; p < 4; p++)
			{
				int nTrisToAdd = 0;
				while (newTriangles > 0)
				{
					// Take triangle from front of queue
					Triangle test = listTri.remove();
					newTriangles--;

					/*
					 * Clip it against a plane. We only need to test each subsequent plane, against subsequent new triangles
					 * as all triangles after a plane clip are guaranteed to lie on the inside of the plane.
					 */
					switch (p)
					{
					case 0:	
						clipped = TriangleClipAgainstPlane(new Vector3(0,0,0), new Vector3(0,1,0), test);
						nTrisToAdd = AlgebraUtility.countNotNull(clipped);
						break;
					case 1:	
						clipped = TriangleClipAgainstPlane(new Vector3(0,frame.getSize().height - 1, 0), new Vector3(0,-1,0), test); 
						nTrisToAdd = AlgebraUtility.countNotNull(clipped);
						break;
					case 2:	
						clipped = TriangleClipAgainstPlane(new Vector3(0,0,0), new Vector3(1,0,0), test);
						nTrisToAdd = AlgebraUtility.countNotNull(clipped);
						break;
					case 3:	
						clipped = TriangleClipAgainstPlane(new Vector3(frame.getSize().width -1,0,0), new Vector3(-1,0,0), test);
						nTrisToAdd = AlgebraUtility.countNotNull(clipped);
						break;
					}

					/*
					 * Clipping may yield a variable number of triangles, so add these new ones to the back of the queue for subsequent
					 * clipping against next planes
					 */
					for (int w = 0; w < nTrisToAdd; w++)
						listTri.add(clipped[w]);
				}
				newTriangles = listTri.size();
			}

			for (Triangle t : listTri) {
						
				//We store the points of the triangles in a temporary array so it's easier to work with them
				int[] xPoints = {(int)t.p[0].x,  (int)t.p[1].x,  (int)t.p[2].x};
				int[] yPoints = {(int)t.p[0].y, (int)t.p[1].y, (int)t.p[2].y};

				//We set the color that we calculated before
				g.setColor(tri.color);

				//We create two polygons (one for the outline and one for the mesh)
				Polygon p = new Polygon(xPoints,yPoints, 3);;
				//xPolygon p_out = new Polygon(xPoints,yPoints, 3);

				//We draw them both and we paint the mesh one with our color and then we draw the outline
				g.drawPolygon(p);
				g .fillPolygon(p);
				g.setColor(polyColor);
				//g.drawPolygon(p_out);	
			}

		}
	}


	/**
	 * Method to calculate the color based on the brightness of each face (it's based on the dot product). The greater the projection is the greater the brightness is.
	 * 
	 * @param color The initial color of the triangle
	 * @param lum The brightness of the triangle (1 - completely bright, 0 - completely black)
	 * @return The new color
	 */
	public static Color getColor(Color color, double lum) {

		//We get the RGB values of the colors
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int alpha = color.getAlpha();

		//If we need to change the color (if the projection is less than 0.95) then we calculate the new RGB values that are based on the lum
		if(lum > 0.05) {
			red = (int) Math.round(Math.max(0, color.getRed() - 255 * lum));
			green = (int) Math.round(Math.max(0, color.getGreen() - 255 * lum));
			blue = (int) Math.round(Math.max(0, color.getBlue() - 255 * lum));
		}

		//We return the color
		return new Color(red, green, blue, alpha);
	}


	/**
	 * Translates the camera on the Y axis
	 * @param y The direction of the translation (1 - up, -1 down)
	 */
	public void updateCameraY(int y) {
		vCamera.y -= (moveSpeed * fElapsedTime) * y;
	}
	
	/**
	 * Translates the camera on the X axis
	 * @param y The direction of the translation (1 - left, -1 right)
	 */
	public void updateCameraX(int x) {
		vCamera.x += (moveSpeed * fElapsedTime) * x;
	}

	/**
	 * Sets the Yaw (rotation) of the camera
	 * @param dir The direction of the rotation (1 - left, -1 right)
	 */
	public void setYaw(int dir) {
		fYaw += (rotateSpeed * fElapsedTime) * dir;
	}

	/**
	 * Moves the camera
	 * @param dir The direction of the movement (1 - forward, -1 - backwards)
	 */
	public void move(int dir) {
		Vector3 vForward = AlgebraUtility.VectorMul(vLookDir, moveSpeed * fElapsedTime * dir);
		vCamera = AlgebraUtility.VectorAdd(vCamera, vForward);
	}

	/**
	 * A function that returns the intersection point of a segment and a plane
	 * @param plane_p The plane we want to find the intersection of 
	 * @param plane_n The normal plane of the line
	 * @param lineStart The starting point of the segment
	 * @param lineEnd The ending point of the segment
	 * @return The point of intersection
	 */
	public Vector3 VectorIntersectPlane(Vector3 plane_p, Vector3 plane_n, Vector3 lineStart, Vector3 lineEnd) {
		plane_n = AlgebraUtility.VectorNormalize(plane_n);
		double plane_d = -AlgebraUtility.VectorDotProduct(plane_n, plane_p);
		double ad = AlgebraUtility.VectorDotProduct(lineStart, plane_n);
		double bd = AlgebraUtility.VectorDotProduct(lineEnd, plane_n);
		double t = (-plane_d - ad) / (bd - ad);
		Vector3 lineStartToEnd = AlgebraUtility.VectorSub (lineEnd, lineStart);
		Vector3 lineToIntersect = AlgebraUtility.VectorMul (lineStartToEnd, t);
		return AlgebraUtility.VectorAdd(lineStart, lineToIntersect);
	}

	/**
	 * A function that calculates the distance between a point and a plane (if it's negative then the point is "outside" of the plane)
	 * @param p The point we want to check
	 * @param plane_n The normal of the line for the point
	 * @param plane_p The plane we want to check
	 * @return The distance between the point p and the plane_p
	 */ 
	private double dist(Vector3 p, Vector3 plane_n, Vector3 plane_p) {
		Vector3 n = AlgebraUtility.VectorNormalize(p);
		return (plane_n.x * n.x + plane_n.y * n.y + plane_n.z * n.z - AlgebraUtility.VectorDotProduct(plane_n, plane_p));
	}

	/**
	 * Clips a triangle with a plane and returns an array of the clipped triangles. Depending on the position on the triangle there could be 4 outcomes
	 * - If the triangle is completely outside: The array contains 2 null elements
	 * - If the triangle is completely inside: The array contains the original triangle
	 * - The triangle clips with 1 point inside the view: The array contains 1 triangle, which points are the two intersection points and the inside point
	 * - The triangle clips with 2 points inside the view: The array contains 2 triangles, which points are calculated connecting 2 opposing points of the 
	 * 	 clipped quad
	 * @param plane_p The plane we're clipping against
	 * @param plane_n The normal of the triangle
	 * @param inTri The triangle we're clipping
	 * @return The array containing the new triangles	
	 */
	public Triangle[] TriangleClipAgainstPlane(Vector3 plane_p, Vector3 plane_n, Triangle inTri) {
		plane_n = AlgebraUtility.VectorNormalize(plane_n);
		Triangle[] outTri = new Triangle[2];
		outTri[0] = outTri[1] = null;
		Vector3[] insidePoints = new Vector3[3];
		Vector3[] outsidePoints = new Vector3[3];
		int nInsidePointCount = 0;
		int nOutsidePointCount = 0;

		double d0 = dist(inTri.p[0], plane_n, plane_p);
		double d1 = dist(inTri.p[1], plane_n, plane_p);
		double d2 = dist(inTri.p[2], plane_n, plane_p);

		if(d0 >= 0) { insidePoints[nInsidePointCount++] = inTri.p[0]; }
		else { outsidePoints[nOutsidePointCount++] = inTri.p[0]; }
		if(d1 >= 0) { insidePoints[nInsidePointCount++] = inTri.p[1]; }
		else { outsidePoints[nOutsidePointCount++] = inTri.p[1]; }
		if(d2 >= 0) { insidePoints[nInsidePointCount++] = inTri.p[2]; }
		else { outsidePoints[nOutsidePointCount++] = inTri.p[2]; }

		if(nInsidePointCount == 0) {
			return outTri;
		}

		if(nInsidePointCount == 3) {
			outTri[0] = inTri;
			return outTri;
		}

		if(nInsidePointCount == 1 && nOutsidePointCount == 2) {
			Triangle tempTri = new Triangle();
			tempTri.p[0] = insidePoints[0];
			tempTri.p[1] = VectorIntersectPlane(plane_p,plane_n, insidePoints[0], outsidePoints[0]);
			tempTri.p[2] = VectorIntersectPlane(plane_p, plane_n, insidePoints[0], outsidePoints[1]);
			outTri[0] = tempTri;
			return outTri;
		}

		if(nInsidePointCount == 2 && nOutsidePointCount == 1) {
			Triangle tempTri1 = new Triangle();
			Triangle tempTri2 = new Triangle();

			tempTri1.p[0] = insidePoints[0];
			tempTri1.p[1] = insidePoints[1];
			tempTri1.p[2] = VectorIntersectPlane(plane_p, plane_n, insidePoints[0], outsidePoints[0]);

			tempTri2.p[0] = insidePoints[1];
			tempTri2.p[1] = tempTri1.p[2];
			tempTri2.p[2] = VectorIntersectPlane(plane_p, plane_n, insidePoints[1], outsidePoints[0]);
			outTri[0] = tempTri1;
			outTri[1] = tempTri2;
			return outTri;
		}

		return outTri;	
	}

}