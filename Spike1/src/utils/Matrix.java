package utils;

public class Matrix implements Cloneable {
	
	/**
	 *  matrix dimension. */
	public int n; // dimensión
	private double[][] x;

	/**
	 * Constructor to create a matrix of N dimension.
	 * 
	 * @param n
	 *            Dimension for the matrix
	 */
	public Matrix(int n) {
		this.n = n;
		x = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				x[i][j] = 0.0;
			}
		}
	}

	/**
	 * Constructor for matrix.
	 * 
	 * @param x
	 *            Matrix
	 */
	public Matrix(double[][] x) {
		this.x = x;
		n = x.length;
	}

	/**
	 * This method clones a matrix.
	 * 
	 * @return cloned matrix
	 */
	public Object clone() {
		Matrix obj = null;

		try {
			obj = (Matrix) super.clone();
		} catch (CloneNotSupportedException ex) {
			System.out.println(" no se puede duplicar");
		}

		// aqui esta la clave para clonar la matriz bidimensional
		obj.x = obj.x.clone();
		for (int i = 0; i < obj.x.length; i++) {
			obj.x[i] = obj.x[i].clone();
		}
		return obj;
	}

	/**
	 * This method can make the product of two matrices.
	 * 
	 * @param a
	 *            First matrix
	 * @param b
	 *            Second matrix
	 * @return Product of matrices
	 */
	public Matrix product(Matrix a, Matrix b) {
		Matrix result = new Matrix(a.n);

		for (int i = 0; i < a.n; i++) {
			for (int j = 0; j < a.n; j++) {
				for (int k = 0; k < a.n; k++) {
					result.x[i][j] += a.x[i][k] * b.x[k][j];
				}
			}
		}
		return result;
	}

	/**
	 * This method is able to transpose a matrix.
	 * 
	 * @param a
	 *            Matrix to transpose
	 * @return Transposed matrix
	 */
	public Matrix transposed(Matrix a) {
		int n = a.n; // dimensión
		Matrix d = new Matrix(a.n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				d.x[i][j] = a.x[j][i];
			}
		}
		return d;
	}

	/**
	 * This method is able to calculate eigen-values of a matrix.
	 * 
	 * @param values
	 *            Values for the matrix
	 * @param maxIter
	 *            Iterations
	 * @return Eigen-values
	 */
	public Matrix eigenvalues(double[] values, int maxIter) {
		final double CERO = 1e-8;
		double max, tolerance, sumsq;
		double x, y, z, c, s;
		int count = 0;
		int i, j, k, l;

		Matrix a = (Matrix) clone(); // matriz copia
		Matrix p = new Matrix(n);
		Matrix q = new Matrix(n);

		// matriz unidad
		for (i = 0; i < n; i++) {
			q.x[i][i] = 1.0;
		}
		do {
			k = 0;
			l = 1;
			max = Math.abs(a.x[k][1]);
			for (i = 0; i < n - 1; i++) {
				for (j = i + 1; j < n; j++) {
					if (Math.abs(a.x[i][j]) > max) {
						k = i;
						l = j;
						max = Math.abs(a.x[i][j]);
					}
				}
			}
			sumsq = 0.0;
			for (i = 0; i < n; i++) {
				sumsq += a.x[i][i] * a.x[i][i];
			}
			tolerance = 0.0001 * Math.sqrt(sumsq) / n;
			if (max < tolerance)
				break;

			// calcula la matriz ortogonal de p
			// inicialmente es la matriz unidad
			for (i = 0; i < n; i++) {
				for (j = 0; j < n; j++) {
					p.x[i][j] = 0.0;
				}
			}

			for (i = 0; i < n; i++) {
				p.x[i][i] = 1.0;
			}

			y = a.x[k][k] - a.x[l][l];
			if (Math.abs(y) < CERO) {
				c = s = Math.sin(Math.PI / 4);
			} else {
				x = 2 * a.x[k][l];
				z = Math.sqrt(x * x + y * y);
				c = Math.sqrt((z + y) / (2 * z));
				s = sign(x / y) * Math.sqrt((z - y) / (2 * z));
			}

			p.x[k][k] = c;
			p.x[l][l] = c;
			p.x[k][l] = s;
			p.x[l][k] = -s;
			a = product(p, product(a, transposed(p)));
			q = product(q, transposed(p));
			count++;
		} while (count < maxIter);

		// valores propios
		for (i = 0; i < n; i++) {
			values[i] = (double) Math.round(a.x[i][i] * 1000) / 1000;
		}

		// vectores propios
		return q;
	}

	/**
	 * Calculates the sign.
	 * 
	 * @param x
	 *            value
	 * @return sign
	 */
	private int sign(double x) {
		return (x > 0 ? 1 : -1);
	}

	/**
	 * Returns the matrix.
	 * 
	 * @param mat
	 *            Matrix
	 * 
	 * @return matrix
	 */
	public double[][] getMatrix(Matrix mat) {
		return x;
	}
}
