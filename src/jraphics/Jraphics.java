package jraphics;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
	
import javax.swing.*;

public class Jraphics extends JPanel{

	private static final long serialVersionUID = 1L;
	private static boolean isRunning = true;
	public Mesh meshCube;
	public Mat4x4 matProj;
	public JFrame frame;
	final static int TARGET_FPS = 60;
	final static long OPTIMAL_TIME = 1000000000 / TARGET_FPS; 
	private double fElapsedTime = 0;
	private float fTheta = 0;
	private long cTimeOld = 0;
	private Vector3 vCamera = new Vector3(0,0,0);
	private Vector3 vLookDir;
	public ArrayList<Triangle> vecTrianglesToRaster = new ArrayList<Triangle>();
	private float fYaw;
	private float moveSpeed = 30;
	private float rotateSpeed = 4;
	
	//Override of the paintComponent method so it performs additional functions
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.BLACK);
		doGameUpdates(g);	
	}
	
	//Method to calculate the color based on the brightness of each face (it's based on the dot product). The greater the projection is the greater the lum is.
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
	
	/*
	 * Lot of auxiliary functions that are used to work with Vectors and Matrixes
	 */
	
	//Sum of two Vectors
	public Vector3 VectorAdd (Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	//Subtraction of two Vectors
	public Vector3 VectorSub (Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	
	//Multiplication of a Vector and a scalar
	public Vector3 VectorMul (Vector3 v, double k) {
		return new Vector3(v.x * k, v.y * k, v.z * k);
	}
	
	//Division of a Vector and a scalar
	public Vector3 VectorDiv (Vector3 v1, double k) {
		return new Vector3(v1.x / k, v1.y / k, v1.z / k);
	}
	
	//Dot product between two vectors. Returns a scalar
	public double VectorDotProduct (Vector3 v1, Vector3 v2) {
		return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
	}
	
	//Returns the length of a Vector (by doing the sqrt of the dot product with himself)
	public double VectorLength (Vector3 v) {
		return Math.sqrt(VectorDotProduct(v,v));
	}
	
	//Normalizes a Vector returning a unit Vector with the same properties 
	public Vector3 VectorNormalize (Vector3 v) {
		double l = VectorLength(v);
		return new Vector3(v.x / l, v.y / l, v.z / l);
	}
	
	//Returns the cross product between two vectors
	public Vector3 VectorCrossProduct (Vector3 v1, Vector3 v2) {
		Vector3 v = new Vector3(0,0,0);
		v.x = v1.y * v2.z - v1.z * v2.y;
		v.y = v1.z * v2.x - v1.x * v2.z;
		v.z = v1.x * v2.y - v1.y * v2.x;
		return v;
	}
	
	//Function that generates an Identity matrix
	public Mat4x4 MatrixMakeIdentity() {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = 1;
		m.m[1][1] = 1;
		m.m[2][2] = 1;
		m.m[3][3] = 1;
		return m;
	}
	
	//Rotation matrix for the X axis
	public Mat4x4 MatrixRotationX(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = 1;
		m.m[1][1] = Math.cos(fAngleRad);
		m.m[1][2] = Math.sin(fAngleRad);
		m.m[2][1] = -Math.sin(fAngleRad);
		m.m[2][2] = Math.cos(fAngleRad);
		m.m[3][3] = 1;	
		return m;
	}
	
	//Rotation matrix for the Y axis
	public Mat4x4 MatrixRotationY(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = Math.cos(fAngleRad);
		m.m[0][2] = Math.sin(fAngleRad);
		m.m[2][0] = -Math.sin(fAngleRad);
		m.m[1][1] = 1;
		m.m[2][2] = Math.cos(fAngleRad);
		m.m[3][3] = 1;
		return m;
	}
	
	//Rotation matrix for the Z axis 
	public Mat4x4 MatrixRotationZ(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = Math.cos(fAngleRad);
		m.m[0][1] = Math.sin(fAngleRad);
		m.m[1][0] = -Math.sin(fAngleRad);
		m.m[1][1] = Math.cos(fAngleRad);
		m.m[2][2] = 1;
		m.m[3][3] = 1;
		return m;
	}
	
	//Function that creates a translation matrix on three coordinates
	public Mat4x4 MatrixMakeTranslation (double x, double y, double z) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = 1;
		m.m[1][1] = 1;
		m.m[2][2] = 1;
		m.m[3][3] = 1;
		m.m[3][0] = x;
		m.m[3][1] = y;
		m.m[3][2] = z;
		return m;
	}
	
	//Function that creates a projection matrix based on the FOV of the camera, the aspect ratio of the screen and far and near planes
	public Mat4x4 MatrixMakeProjection(double fFovDegrees, double fAspectRatio, double fNear, double fFar)
	{
		double fFovRad = 1.0 / Math.tan(fFovDegrees * 0.5 / 180.0 * 3.14159);
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = fAspectRatio * fFovRad;
		m.m[1][1] = fFovRad;
		m.m[2][2] = fFar / (fFar - fNear);
		m.m[3][2] = (-fFar * fNear) / (fFar - fNear);
		m.m[2][3] = 1.0f;
		m.m[3][3] = 0.0f;
		return m;
	}
	
	//Function that allows to multiply two matrixes
	public Mat4x4 MatrixMultiplyMatrix (Mat4x4 m1, Mat4x4 m2) {
		Mat4x4 m = new Mat4x4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m.m[j][i] = m1.m[j][0] * m2.m[0][i] + m1.m[j][1] * m2.m[1][i] + m1.m[j][2] * m2.m[2][i] + m1.m[j][3] * m2.m[3][i];
			}
		}
		return m;
	}
	
	//Creates a Matrix that allows our camera to point at a point
	public Mat4x4 MatrixPointAt (Vector3 pos, Vector3 target, Vector3 up) {
		Vector3 newForward = VectorSub(target,pos);
		newForward = VectorNormalize(newForward);
		
		Vector3 a = VectorMul(newForward, VectorDotProduct(up, newForward));
		Vector3 newUp = VectorSub(up, a);
		newUp = VectorNormalize(newUp);
		
		Vector3 newRight = VectorCrossProduct(newUp, newForward);
		
		//Constructs a dimensioning and translation Matrix	
		Mat4x4 matrix = new Mat4x4();
		matrix.m[0][0] = newRight.x;	matrix.m[0][1] = newRight.y;	matrix.m[0][2] = newRight.z;	matrix.m[0][3] = 0;
		matrix.m[1][0] = newUp.x;		matrix.m[1][1] = newUp.y;		matrix.m[1][2] = newUp.z;		matrix.m[1][3] = 0;
		matrix.m[2][0] = newForward.x;	matrix.m[2][1] = newForward.y;	matrix.m[2][2] = newForward.z;	matrix.m[2][3] = 0;
		matrix.m[3][0] = pos.x;			matrix.m[3][1] = pos.y;			matrix.m[3][2] = pos.z;			matrix.m[3][3] = 1;
		return matrix;
	}
	
	//A function to "invert" a matrix
	public Mat4x4 MatrixQuickInverse(Mat4x4 m) //Works only for Rotation/Translation Matrixes
	{
		Mat4x4 matrix = new Mat4x4();
		matrix.m[0][0] = m.m[0][0]; matrix.m[0][1] = m.m[1][0]; matrix.m[0][2] = m.m[2][0]; matrix.m[0][3] = 0;
		matrix.m[1][0] = m.m[0][1]; matrix.m[1][1] = m.m[1][1]; matrix.m[1][2] = m.m[2][1]; matrix.m[1][3] = 0;
		matrix.m[2][0] = m.m[0][2]; matrix.m[2][1] = m.m[1][2]; matrix.m[2][2] = m.m[2][2]; matrix.m[2][3] = 0;
		matrix.m[3][0] = -(m.m[3][0] * matrix.m[0][0] + m.m[3][1] * matrix.m[1][0] + m.m[3][2] * matrix.m[2][0]);
		matrix.m[3][1] = -(m.m[3][0] * matrix.m[0][1] + m.m[3][1] * matrix.m[1][1] + m.m[3][2] * matrix.m[2][1]);
		matrix.m[3][2] = -(m.m[3][0] * matrix.m[0][2] + m.m[3][1] * matrix.m[1][2] + m.m[3][2] * matrix.m[2][2]);
		matrix.m[3][3] = 1;
		return matrix;
	}
		
	//Function to multiply a Matrix for a Vector
	public Vector3 MatrixMultiplyVector (Mat4x4 m, Vector3 i) {
		Vector3 v = new Vector3(0,0,0);
		v.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + i.w * m.m[3][0];
		v.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + i.w * m.m[3][1];
		v.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + i.w * m.m[3][2];
		v.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + i.w * m.m[3][3];
		return v;
	}
	
	/*
	 * Main function that performs all of the 3D calculations
	 */
	private void doGameUpdates(Graphics g) {
				
				//Creation of some rotational matrixes
				Mat4x4 matRotZ = new Mat4x4();
				Mat4x4 matRotX = new Mat4x4();
				
				matRotZ = MatrixRotationZ(1);
				matRotX = MatrixRotationX(1);
				
				//Creation of a translation matrix so our model isn't at 0,0,0
				Mat4x4 matTrans ;
				matTrans = MatrixMakeTranslation(0,0,10);
				
				//Creation of a world matrix that represents all the transformations of the objects
				Mat4x4 matWorld;
				matWorld = MatrixMakeIdentity();
				matWorld = MatrixMultiplyMatrix(matRotZ, matRotX);
				matWorld = MatrixMultiplyMatrix(matWorld, matTrans);
				
				//Vectors needed for the camera
				Vector3 vUp = new Vector3(0,1,0);
				Vector3 vTarget = new Vector3(0,0,1);
				Mat4x4 matCameraRot = MatrixRotationY(fYaw);
				vLookDir = MatrixMultiplyVector(matCameraRot, vTarget);
				vTarget = VectorAdd(vCamera, vLookDir);
				
				Mat4x4 matCamera = MatrixPointAt(vCamera, vTarget, vUp);
				
				Mat4x4 matView = MatrixQuickInverse(matCamera);
				
				//Temp list to store triangles that we still have to sort
				vecTrianglesToRaster = new ArrayList<>();
				
				//Loop that works on all of the triangles in our mesh
				for(Triangle tri : meshCube.tris) {
					
					//Creation of some empty triangles needed for our transformation
					Triangle triProjected = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));
					Triangle triTransformed = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));
					Triangle triViewed = new Triangle(new Vector3(0,0,0), new Vector3(0,0,0), new Vector3(0,0,0));
					
					//Translating all the vertexes of the tri using the world matrix
					triTransformed.p[0] = MatrixMultiplyVector(matWorld, tri.p[0]);
					triTransformed.p[1] = MatrixMultiplyVector(matWorld, tri.p[1]);
					triTransformed.p[2] = MatrixMultiplyVector(matWorld, tri.p[2]);
					
					//Creation of a normal and two lines
					Vector3 normal = new Vector3(0,0,0); Vector3 line1 = new Vector3 (0,0,0); Vector3 line2 = new Vector3(0,0,0);
					
					//Auxialiary lines needed to calculate the normal of each triangle
					line1 = VectorSub(triTransformed.p[1], triTransformed.p[0]);
					line2 = VectorSub(triTransformed.p[2], triTransformed.p[0]);
					
					//Calculation of the normal using the cross product
					normal = VectorCrossProduct(line1, line2);
					
					//Normalization of the normal
					normal = VectorNormalize(normal);
					
					//A vector used by the camera
					Vector3 vCameraRay = VectorSub(triTransformed.p[0], vCamera);
			        
					//If the projection of the normal on the view is lesser than 0 then we can actually see the triangle so we draw it
			        if(VectorDotProduct(normal, vCameraRay) < 0) {
							
			        		//Auxiliary vector we use to hardcode the light direction (coming from the player)
							Vector3 light_direction = new Vector3(0,1,-1);
							
							//We normalize it so we can work with unit vectors
							light_direction = VectorNormalize(light_direction);
							
							//Here we calculate the dot product of the light and the normal so we can see how much the triangle deviates from our view
							double dp = Math.max(0.1, VectorDotProduct(light_direction, normal));
							
							triViewed.p[0] = MatrixMultiplyVector(matView, triTransformed.p[0]);
							triViewed.p[1] = MatrixMultiplyVector(matView, triTransformed.p[1]);
							triViewed.p[2] = MatrixMultiplyVector(matView, triTransformed.p[2]);

							
							Triangle[] clipped = new Triangle[2];
							clipped = TriangleClipAgainstPlane(new Vector3(0,0,0.1), new Vector3(0,0,0.1), triViewed);
							int nClippedTriangles = countNotNull(clipped);
							
							for(int n = 0; n < nClippedTriangles; n++) {
								
							
							//We project each triangle using the projection matrix
							triProjected.p[0] = MatrixMultiplyVector(matProj, clipped[n].p[0]);
							triProjected.p[1] = MatrixMultiplyVector(matProj, clipped[n].p[1]);
							triProjected.p[2] = MatrixMultiplyVector(matProj, clipped[n].p[2]);
							
							//Extra math
							triProjected.p[0] = VectorDiv(triProjected.p[0], triProjected.p[0].w);
							triProjected.p[1] = VectorDiv(triProjected.p[1], triProjected.p[1].w);
							triProjected.p[2] = VectorDiv(triProjected.p[2], triProjected.p[2].w);
							
							//We offset the view by 1,1,0
							Vector3 vOffsetView = new Vector3(1,1,0);
							triProjected.p[0] = VectorAdd(triProjected.p[0], vOffsetView);
							triProjected.p[1] = VectorAdd(triProjected.p[1], vOffsetView);
							triProjected.p[2] = VectorAdd(triProjected.p[2], vOffsetView);
							
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
							temp.color = getColor(Color.GREEN, 1-dp);
							
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

								// Clip it against a plane. We only need to test each 
								// subsequent plane, against subsequent new triangles
								// as all triangles after a plane clip are guaranteed
								// to lie on the inside of the plane. I like how this
								// comment is almost completely and utterly justified
								switch (p)
								{
								case 0:	
									clipped = TriangleClipAgainstPlane(new Vector3(0,0,0), new Vector3(0,1,0), test);
									nTrisToAdd = countNotNull(clipped);
									break;
								case 1:	
									clipped = TriangleClipAgainstPlane(new Vector3(0,frame.getSize().height - 1, 0), new Vector3(0,-1,0), test); 
									nTrisToAdd = countNotNull(clipped);
									break;
								case 2:	
									clipped = TriangleClipAgainstPlane(new Vector3(0,0,0), new Vector3(1,0,0), test);
									nTrisToAdd = countNotNull(clipped);
									break;
								case 3:	
									clipped = TriangleClipAgainstPlane(new Vector3(frame.getSize().width -1,0,0), new Vector3(-1,0,0), test);
									nTrisToAdd = countNotNull(clipped);
									break;
								}

								// Clipping may yield a variable number of triangles, so
								// add these new ones to the back of the queue for subsequent
								// clipping against next planes
								for (int w = 0; w < nTrisToAdd; w++)
									listTri.add(clipped[w]);
							}
							newTriangles = listTri.size();
						}
						
						for (Triangle t : listTri) {
//							
							//We store the points of the triangles in a temporary array so it's easier to work with them
							int[] xPoints = {(int)t.p[0].x,  (int)t.p[1].x,  (int)t.p[2].x};
							int[] yPoints = {(int)t.p[0].y, (int)t.p[1].y, (int)t.p[2].y};
							
							//We set the color that we calculated before
							g.setColor(tri.color);
							
							//We create two polygons (one for the outline and one for the mesh)
							Polygon p = new Polygon(xPoints,yPoints, 3);;
							Polygon p_out = new Polygon(xPoints,yPoints, 3);
							
							//We draw them both and we paint the mesh one with our color and then we draw the outline
							g.drawPolygon(p);
							g .fillPolygon(p);
							g.setColor(Color.BLACK);
							g.drawPolygon(p_out);	
						}
	
				}
	}


	public int countNotNull(Triangle[] array) {
		int notNull = 0;
		for(int i = 0; i < array.length; i++) {
			if(array[i] != null)
				notNull++;
		}			
		return notNull;
	}
	
	public void updateCameraY(int y) {
		vCamera.y -= (moveSpeed * fElapsedTime) * y;
	}
	
	public void updateCameraX(int x) {
		vCamera.x += (moveSpeed * fElapsedTime) * x;

	}
	
	public void setYaw(int dir) {
		fYaw += (rotateSpeed * fElapsedTime) * dir;
	}
	
	public void move(int dir) {
		Vector3 vForward = VectorMul(vLookDir, moveSpeed * fElapsedTime * dir);
		vCamera = VectorAdd(vCamera, vForward);
	}
	
	public Vector3 VectorIntersectPlane(Vector3 plane_p, Vector3 plane_n, Vector3 lineStart, Vector3 lineEnd) {
		plane_n = VectorNormalize(plane_n);
		double plane_d = -VectorDotProduct(plane_n, plane_p);
		double ad = VectorDotProduct(lineStart, plane_n);
		double bd = VectorDotProduct(lineEnd, plane_n);
		double t = (-plane_d - ad) / (bd - ad);
		Vector3 lineStartToEnd = VectorSub (lineEnd, lineStart);
		Vector3 lineToIntersect = VectorMul (lineStartToEnd, t);
		return VectorAdd(lineStart, lineToIntersect);
	}
	
	private double dist(Vector3 p, Vector3 plane_n, Vector3 plane_p) {
		Vector3 n = VectorNormalize(p);
		return (plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - VectorDotProduct(plane_n, plane_p));
	}
	
	public Triangle[] TriangleClipAgainstPlane(Vector3 plane_p, Vector3 plane_n, Triangle inTri) {
		plane_n = VectorNormalize(plane_n);
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
	
	public void gameLoop() {
		
		while(isRunning) {
			frame.repaint();
			long cTimeNow = System.currentTimeMillis();
			if(cTimeOld != 0)
				fElapsedTime = (double)((cTimeNow - cTimeOld) * 0.001);
		    fTheta += fElapsedTime;
		    cTimeOld = cTimeNow;
		    frame.repaint();
		    try {
		    	Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}