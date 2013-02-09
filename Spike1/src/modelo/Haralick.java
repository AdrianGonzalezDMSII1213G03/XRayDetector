package modelo;

import java.awt.Rectangle;
import java.util.Arrays;


import ij.IJ;
import ij.ImagePlus;

import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;
//import cern.colt.matrix.linalg.EigenvalueDecomposition;
//import cern.colt.matrix.DoubleFactory2D;
//import cern.colt.matrix.DoubleMatrix1D;
//import cern.colt.matrix.DoubleMatrix2D;

public class Haralick extends Feature {
	
	private int SIZE = 256;
	private int count = 0;
	private double[][] glcm = new double[SIZE][SIZE];
	private double[] px_y = new double[SIZE]; // Px-y(i)
	private double[] pxy = new double[2 * SIZE]; // Px+y(i)
	private double hx = 0; // HXY1 statistics
	private double hy = 0; // HXY2 statistics
	private double hxy1 = 0; // HXY1 statistics
	private double hxy2 = 0; // HXY2 statistics */
	private double[] p_x = new double[SIZE]; // p_x statistics
	private double[] p_y = new double[SIZE]; // p_y statistics
	private double[] haralickVector;
	private String[] headVector;
	
	public Haralick(ImagePlus image) {
		super(image);
	}

	/**
	 * First constructor of Haralick.
	 * 
	 * @param image
	 *            ROI to extract haralick features
	 * @param selectedStep
	 *            Selected step
	 * @param step
	 *            Step
	 */
	public Haralick(ImagePlus image, String selectedStep, int step) {
		super(image, selectedStep, step);
		
	}


	/**
	 * Returns a vector with haralick features.
	 * 
	 * @return vector with haralick features
	 */
	public double[] getHaralickVector() {
		return haralickVector;
	}

	/**
	 * Return a string vector with the header of haralick features.
	 * 
	 * @return string vector with the header of haralick features
	 */
	public String[] getHead() {
		return headVector;
	}

	/**
	 * Calculates a gray level co-occurrence matrix.
	 */
	public void glcm() {

		// This part get all the pixel values into the pixel [ ] array via the
		// Image Processor
		IJ.run(getImage(), "8-bit", "");
		byte[] pixels = (byte[]) (getImage().getProcessor().getPixels());
		int width = getImage().getWidth();
		Rectangle r = getImage().getProcessor().getRoi();

		// The variable a holds the value of the pixel where the Image Processor
		// is sitting its attention
		// The variable b holds the value of the pixel which is the neighbor to
		// the pixel where the Image Processor is sitting its attention

		int a;
		int b;

		double pixelCounter = 0;

		// This part computes the Gray Level Correlation Matrix based in the
		// step selected by the user

		int offset, i;

		if (getSelectedStep().equals("0 degrees")) {
			for (int y = r.y; y < (r.y + r.height); y++) {
				offset = y * width;
				for (int x = r.x; x < (r.x + r.width); x++) {
					i = offset + x;

					a = 0xff & pixels[i];
					b = 0xff & (getImage().getProcessor().getPixel(x + getStep(), y));

					glcm[a][b] += 1;
					glcm[b][a] += 1;
					pixelCounter += 2;
				}
			}
		}

		if (getSelectedStep().equals("90 degrees")) {
			for (int y = r.y; y < (r.y + r.height); y++) {
				offset = y * width;
				for (int x = r.x; x < (r.x + r.width); x++) {
					i = offset + x;
					a = 0xff & pixels[i];
					b = 0xff & (getImage().getProcessor().getPixel(x, y - getStep()));
					glcm[a][b] += 1;
					glcm[b][a] += 1;
					pixelCounter += 2;
				}
			}
		}

		if (getSelectedStep().equals("180 degrees")) {
			for (int y = r.y; y < (r.y + r.height); y++) {
				offset = y * width;
				for (int x = r.x; x < (r.x + r.width); x++) {
					i = offset + x;
					a = 0xff & pixels[i];
					b = 0xff & (getImage().getProcessor().getPixel(x - getStep(), y));
					glcm[a][b] += 1;
					glcm[b][a] += 1;
					pixelCounter += 2;
				}
			}
		}

		if (getSelectedStep().equals("270 degrees")) {
			for (int y = r.y; y < (r.y + r.height); y++) {
				offset = y * width;
				for (int x = r.x; x < (r.x + r.width); x++) {
					i = offset + x;
					a = 0xff & pixels[i];
					b = 0xff & (getImage().getProcessor().getPixel(x, y + getStep()));
					glcm[a][b] += 1;
					glcm[b][a] += 1;
					pixelCounter += 2;
				}
			}
		}

		for (a = 0; a < SIZE; a++) {
			for (b = 0; b < SIZE; b++) {
				glcm[a][b] = (glcm[a][b]) / (pixelCounter);
			}
		}
	}

	/**
	 * This method calculates statistics.
	 */
	public void calculateStatistics() {

		Arrays.fill(px_y, 0); // Px-y(i)
		Arrays.fill(pxy, 0); // Px+y(i)

		for (int y = 0; y < SIZE; y++){
			for (int x = 0; x < SIZE; x++) {
				px_y[Math.abs(x - y)] += glcm[x][y];
				pxy[x + y] += glcm[x][y];
				p_x[x] += glcm[x][y];
				p_y[y] += glcm[x][y];
			}
		}

		for (int x = 0; x < SIZE; x++) {
			// hx y hy
			// Preguntamos esto porque si es 0 el logaritmo sale mal.
			if (p_x[x] != 0)
				hx += p_x[x] * Math.log(p_x[x]);
			if (p_y[x] != 0)
				hy += p_y[x] * Math.log(p_y[x]);

			// hxy1 y hxy2
			for (int y = 0; y < SIZE; y++) {
				if (p_x[x] != 0 && p_y[y] != 0) {
					hxy1 += glcm[x][y] * Math.log((p_x[x] * p_y[y]));
					hxy2 += p_x[x] * p_y[y] * Math.log((p_x[x] * p_y[y]));
				}
			}
		}
		hx *= -1;
		hy *= -1;
		hxy1 *= -1;
		hxy2 *= -1;
	}

	/**
	 * Calculates angular second moment.
	 * 
	 * @return ancular second moment
	 */
	public double angularSecondMoment() {
		double asm = 0.0;
		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				asm = asm + (glcm[a][b] * glcm[a][b]);
			}
		}
		headVector[count] = "angularSecondMoment";
		count++;
		return asm;
	}

	/**
	 * Calculates contrast.
	 * 
	 * @return contrast
	 */
	public double contrast() {
		double contrast = 0.0;
		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				contrast = contrast + (a - b) * (a - b) * (glcm[a][b]);
			}
		}
		headVector[count] = "contrast";
		count++;

		return contrast;
	}

	/**
	 * Calculates the correlation.
	 * 
	 * @return correlation
	 */
	public double correlation() {
		// First step in the calculations will be to calculate px [] and py []
		double correlation = 0.0;
		double px = 0;
		double py = 0;
		double stdevx = 0.0;
		double stdevy = 0.0;

		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				px = px + a * glcm[a][b];
				py = py + b * glcm[a][b];
			}
		}

		// Ahora calcula las desviaciones standard
		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				stdevx = stdevx + (a - px) * (a - px) * glcm[a][b];
				stdevy = stdevy + (b - py) * (b - py) * glcm[a][b];
			}
		}

		// Calcula el parametro de correlacion
		correlation = calcularParametroCorrelacion(correlation, px, py, stdevx,	stdevy);
		headVector[count] = "correlation";
		count++;

		return correlation;
	}

	private double calcularParametroCorrelacion(double correlation, double px,
			double py, double stdevx, double stdevy) {
		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				correlation = correlation
						+ ((a - px) * (b - py) * glcm[a][b] / (stdevx * stdevy));
			}
		}
		return correlation;
	}

	/**
	 * Calculates the sum of squares.
	 * 
	 * @return sum of squares
	 */
	public double sumOfSquares() {
		double mean = 0;
		double variance = 0;

		for (int b = 0; b < SIZE; b++)
			for (int a = 0; a < SIZE; a++)
				mean += glcm[a][b];

		mean /= SIZE * SIZE;

		for (int b = 0; b < SIZE; b++)
			for (int a = 0; a < SIZE; a++)
				variance += (a - mean) * (a - mean) * glcm[a][b];

		headVector[count] = "sumOfSquares";
		count++;

		return variance;
	}

	/**
	 * Calculates the inverse difference moment.
	 * 
	 * @return inverse difference moment
	 */
	public double inverseDifferenceMoment() {
		double idm = 0.0;
		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				idm = idm + (glcm[a][b] / (1 + (a - b) * (a - b)));
			}
		}
		headVector[count] = "inverseDifferenceMoment";
		count++;

		return idm;
	}

	/**
	 * Calculates the sum average.
	 * 
	 * @return sum average
	 */
	public double sumAverage() {
		double sumAvg = 0;
		for (int k = 2; k < 2 * SIZE - 1; k++)
			sumAvg += k * pxy[k];

		headVector[count] = "sumAverage";
		count++;

		return sumAvg;
	}

	/**
	 * Calculates the sum entropy.
	 * 
	 * @return sum entropy
	 */
	public double sumEntropy() {
		double entropysrc = 0;
		for (int k = 2; k < 2 * SIZE - 1; k++) {
			if (pxy[k] == 0)
				continue;
			entropysrc += pxy[k] * Math.log(pxy[k]);
		}

		entropysrc *= -1;
		headVector[count] = "sumEntropy";
		count++;

		return entropysrc;
	}

	/**
	 * Calculates the sum variance.
	 * 
	 * @return sum variance
	 */
	public double sumVariance() {
		double sumAvg = 0;
		int sumVar = 0;

		for (int k = 2; k < 2 * SIZE - 1; k++)
			sumAvg += k * pxy[k];

		for (int k = 2; k < 2 * SIZE - 1; k++)
			sumVar += (k - sumAvg) * (k - sumAvg) * pxy[k];

		headVector[count] = "sumVariance";
		count++;

		return sumVar;

	}

	/**
	 * Calculates the entropy.
	 * 
	 * @return entropy
	 */
	public double entropy() {
		double entropy = 0.0;

		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				if (glcm[a][b] != 0) {
					entropy = entropy + (glcm[a][b] * (Math.log(glcm[a][b])));
				}
			}
		}
		entropy *= -1;

		headVector[count] = "entropy";
		count++;

		return entropy;

	}

	/**
	 * Calculates the difference variance.
	 * 
	 * @return difference variance
	 */
	public double differenceVariance() {
		int k;
		double mean = 0;
		double difVar = 0;

		for (k = 0; k < SIZE - 1; k++)
			mean += k * px_y[k];

		for (k = 0; k < SIZE - 1; k++)
			difVar += (k - mean) * (k - mean) * px_y[k];

		headVector[count] = "differenceVariance";
		count++;

		return difVar;

	}

	/**
	 * Calculates the difference entropy.
	 * 
	 * @return difference entropy
	 */
	public double differenceEntropy() {
		double entropydiff = 0;
		for (int k = 0; k < SIZE - 1; k++) {
			if (px_y[k] == 0)
				continue;
			entropydiff += px_y[k] * Math.log(px_y[k]);
		}
		entropydiff *= -1;
		headVector[count] = "differenceEntropy";
		count++;

		return entropydiff;
	}

	/**
	 * Calculates the information measure of correlation 1.
	 * 
	 * @return information measure of correlation 1
	 */
	public double imc_1() {
		double entropy = 0.0;

		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				if (glcm[a][b] != 0) {
					entropy = entropy + (glcm[a][b] * (Math.log(glcm[a][b])));
				}
			}
		}
		entropy *= -1;

		headVector[count] = "imc_1";
		count++;

		return (entropy - hxy1) / Math.max(hx, hy);
	}

	/**
	 * Calculates the information measure of correlation 2.
	 * 
	 * @return information measure of correlation 2
	 */
	public double imc_2() {
		double entropy = 0.0;

		for (int a = 0; a < SIZE; a++) {
			for (int b = 0; b < SIZE; b++) {
				if (glcm[a][b] != 0) {
					entropy = entropy + (glcm[a][b] * (Math.log(glcm[a][b])));
				}
			}
		}
		entropy *= -1;

		headVector[count] = "imc_2";
		count++;

		return Math.sqrt(1 - Math.exp(-2 * (hxy2 - entropy)));

	}

	/**
	 * Calculates the maximal correlation coefficient.
	 * 
	 * @return maximal correlation coefficient
	 */
	public double maximalCorrelationCoefficient() {
		double[][] Q = new double[SIZE][SIZE];
		double max = -2000, secondMax = -2001;
		double s, mcc = 0;

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				s = 0;
				for (int k = 0; k < SIZE; k++) {
					if (p_x[x] != 0 && p_y[k] != 0)
						s = s + (glcm[x][k] * glcm[y][k]) / p_x[x] * p_y[k];
				}
				Q[x][y] = s;
			}
		}
		
		//WEKA
		Matrix matrix = new Matrix(Q);
		double[] values = new double[matrix.getColumnDimension()];
		
		EigenvalueDecomposition eigenValues = new EigenvalueDecomposition(matrix);
		values = eigenValues.getRealEigenvalues();
		
		//COLT
		/*DoubleFactory2D f = DoubleFactory2D.dense;
		
		DoubleMatrix2D matrix = f.make(Q);
		
		EigenvalueDecomposition eigenValues = new EigenvalueDecomposition(matrix);
		DoubleMatrix1D values = eigenValues.getRealEigenvalues();*/


		for (int i = 0; i < matrix.getColumnDimension(); i++) {
			if (values[i] > max) {
				secondMax = max;
				max = values[i];
			}

			if (values[i] > secondMax && values[i] < max)
				secondMax = values[i];
		}

		if (secondMax == 0)
			mcc = 0;
		else
			mcc = Math.sqrt(secondMax);

		headVector[count] = "maximalCorrelationCoefficient";
		count++;

		return mcc;
	}

	/**
	 * Returns the gray level co-occurrence matrix.
	 * 
	 * @return gray level co-occurrence matrix
	 */
	public double[][] getGlcm(){
		return glcm;
	}

	@Override
	public void calcular() {
		haralickVector = new double[14]; // Cambiar si se descomenta la ultima
		headVector = new String[14]; // Cambiar si se descomenta la ultima

		glcm();
		calculateStatistics();
		haralickVector[0] = angularSecondMoment();
		haralickVector[1] = contrast();
		haralickVector[2] = correlation();
		haralickVector[3] = sumOfSquares();
		haralickVector[4] = inverseDifferenceMoment();
		haralickVector[5] = sumAverage();
		haralickVector[6] = sumEntropy();
		haralickVector[7] = sumVariance();
		haralickVector[8] = entropy();
		haralickVector[9] = differenceVariance();
		haralickVector[10] = differenceEntropy();
		haralickVector[11] = imc_1();
		haralickVector[12] = imc_2();
		//haralickVector[13] = maximalCorrelationCoefficient();
		haralickVector[13] = 0;
		
		setVectorResultados(haralickVector);
		//imprimeResultados();
	}
	
	//sólo para probar que hace algo
	/*private void imprimeResultados() {
		for(int i = 0; i < haralickVector.length; i++){
			System.out.println("Valor de " + headVector[i] + ": " + haralickVector[i]);
		}
	}*/

}
