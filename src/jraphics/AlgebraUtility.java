package jraphics;

/**
 * 
 * @author Nikita Brancatisano
 *
 */
public class AlgebraUtility {
	
	/**
	 * Counts how many non-null elements are present in an array
	 * @param array The array we want to count the null elements of
	 * @return The number of null elements
	 */
	public static int countNotNull(Object[] array) {
		int notNull = 0;
		for(int i = 0; i < array.length; i++) {
			if(array[i] != null)
				notNull++;
		}			
		return notNull;
	}

	/**
	 * Sum of two Vectors
	 * @param v1 The first vector
	 * @param v2 The second vector
	 * @return The sum of the two vectors
	 */
	public static Vector3 VectorAdd (Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	/**
	 * Subtraction of two Vectors
	 * @param v1 The first vector
	 * @param v2 The second vector
	 * @return The subtraction of the two vectors
	 */
	public static Vector3 VectorSub (Vector3 v1, Vector3 v2) {
		return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	/**
	 * Multiplication of a Vector and a scalar
	 * @param v A vector
	 * @param k A scalar
	 * @return The result of the multiplication between the vector and the scalar
	 */
	public static Vector3 VectorMul (Vector3 v, double k) {
		return new Vector3(v.x * k, v.y * k, v.z * k);
	}

	/**
	 * Division of a Vector by a scalar
	 * @param v1 A vector
	 * @param k A scalar
	 * @return The result of the division of the vector by the scalar
	 */
	public static Vector3 VectorDiv (Vector3 v1, double k) {
		return new Vector3(v1.x / k, v1.y / k, v1.z / k);
	}

	/**
	 * Dot product between two vectors
	 * @param v1 The first vector
	 * @param v2 The second vector
	 * @return A scalar. The result of the dot product between the two vectors
	 */
	public static double VectorDotProduct (Vector3 v1, Vector3 v2) {
		return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
	}

	/**
	 * Does the sqrt of the dot product of a vector with himself)
	 * @param v The vector we want to find the length of
	 * @return The length of the vector
	 */
	public static double VectorLength (Vector3 v) {
		return Math.sqrt(VectorDotProduct(v,v));
	}

	/**
	 * Normalizes a vector
	 * @param v The vector we want to normalize
	 * @return A unit vector with the same properties as the initial one
	 */
	public static Vector3 VectorNormalize (Vector3 v) {
		double l = VectorLength(v);
		return new Vector3(v.x / l, v.y / l, v.z / l);
	}

	/**
	 * Cross product between to vectors
	 * @param v1 The first vector
	 * @param v2 The second vector
	 * @return A new vector which is the cross product between the two vectors
	 */ 
	public static Vector3 VectorCrossProduct (Vector3 v1, Vector3 v2) {
		Vector3 v = new Vector3(0,0,0);
		v.x = v1.y * v2.z - v1.z * v2.y;
		v.y = v1.z * v2.x - v1.x * v2.z;
		v.z = v1.x * v2.y - v1.y * v2.x;
		return v;
	}

	/**
	 * Creates a 4x4 identity matrix
	 * @return A 4x4 identity matrix
	 */
	public static Mat4x4 MatrixMakeIdentity() {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = 1;
		m.m[1][1] = 1;
		m.m[2][2] = 1;
		m.m[3][3] = 1;
		return m;
	}

	/**
	 * Rotates a 4x4 matrix by the provided angle on the X axis using a rotational matrix 
	 * @param fAngleRad The angle we want to rotate by
	 * @return The 4x4 rotated matrix
	 */
	public static Mat4x4 MatrixRotationX(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = 1;
		m.m[1][1] = Math.cos(fAngleRad);
		m.m[1][2] = Math.sin(fAngleRad);
		m.m[2][1] = -Math.sin(fAngleRad);
		m.m[2][2] = Math.cos(fAngleRad);
		m.m[3][3] = 1;	
		return m;
	}

	/**
	 * Rotates a 4x4 matrix by the provided angle on the Y axis using a rotational matrix 
	 * @param fAngleRad The angle we want to rotate by
	 * @return The 4x4 rotated matrix
	 */
	public static Mat4x4 MatrixRotationY(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = Math.cos(fAngleRad);
		m.m[0][2] = Math.sin(fAngleRad);
		m.m[2][0] = -Math.sin(fAngleRad);
		m.m[1][1] = 1;
		m.m[2][2] = Math.cos(fAngleRad);
		m.m[3][3] = 1;
		return m;
	}

	/**
	 * Rotates a matrix by the provided angle on the Z axis using a rotational matrix 
	 * @param fAngleRad The angle we want to rotate by
	 * @return The 4x4 rotated matrix
	 */
	public static Mat4x4 MatrixRotationZ(double fAngleRad) {
		Mat4x4 m = new Mat4x4();
		m.m[0][0] = Math.cos(fAngleRad);
		m.m[0][1] = Math.sin(fAngleRad);
		m.m[1][0] = -Math.sin(fAngleRad);
		m.m[1][1] = Math.cos(fAngleRad);
		m.m[2][2] = 1;
		m.m[3][3] = 1;
		return m;
	}

	/**
	 * Creates a 4x4 translation matrix for the desired coordinates
	 * @param x Translation on the x axis
	 * @param y Translation on the y axis
	 * @param z Translation on the z axis
	 * @return The 4x4 translation matrix
	 */
	public static Mat4x4 MatrixMakeTranslation (double x, double y, double z) {
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

	/**
	 * Creates a 4x4 projection matrix that allows us to project world coordinates (x,y,z) into view coordinates (x,y)
	 * @param fFovDegrees Degrees of the desired Field Of View
	 * @param fAspectRatio The aspect ratio of the screen
	 * @param fNear The nearest plane of the camera
	 * @param fFar The farthest plane of the camera
	 * @return A 4x4 projection matrix
	 */
	public static Mat4x4 MatrixMakeProjection(double fFovDegrees, double fAspectRatio, double fNear, double fFar)
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

	/**
	 * Multiplies two 4x4 matrixes
	 * @param m1 The first matrix
	 * @param m2 The second matrix
	 * @return The 4x4 matrix that is the result of the multiplication
	 */
	public static Mat4x4 MatrixMultiplyMatrix (Mat4x4 m1, Mat4x4 m2) {
		Mat4x4 m = new Mat4x4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m.m[j][i] = m1.m[j][0] * m2.m[0][i] + m1.m[j][1] * m2.m[1][i] + m1.m[j][2] * m2.m[2][i] + m1.m[j][3] * m2.m[3][i];
			}
		}
		return m;
	}

	/**
	 * Creates a 4x4 matrix that allows our camera to point at a point
	 * @param pos The position of the camera in world space
	 * @param target The position of the target in world space
	 * @param up The up direction of our camera, needed for reference
	 * @return A 4x4 translation matrix that represents the act of looking at a point
	 */
	public static Mat4x4 MatrixPointAt (Vector3 pos, Vector3 target, Vector3 up) {
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

	/**
	 * A function to "invert" a 4x4 matrix. It works only for Rotation/Translation matrixes
	 * @param m A 4x4 rotation or translation matrix
	 * @return The inverted 4x4 matrix
	 */
	public static Mat4x4 MatrixQuickInverse(Mat4x4 m)
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

	/**
	 * Multiplies the matrix for a vector
	 * @param m A matrix
	 * @param i A vector
	 * @return The vector that is the result of the multiplication
	 */
	public static Vector3 MatrixMultiplyVector (Mat4x4 m, Vector3 i) {
		Vector3 v = new Vector3(0,0,0);
		v.x = i.x * m.m[0][0] + i.y * m.m[1][0] + i.z * m.m[2][0] + i.w * m.m[3][0];
		v.y = i.x * m.m[0][1] + i.y * m.m[1][1] + i.z * m.m[2][1] + i.w * m.m[3][1];
		v.z = i.x * m.m[0][2] + i.y * m.m[1][2] + i.z * m.m[2][2] + i.w * m.m[3][2];
		v.w = i.x * m.m[0][3] + i.y * m.m[1][3] + i.z * m.m[2][3] + i.w * m.m[3][3];
		return v;
	}
	
}
