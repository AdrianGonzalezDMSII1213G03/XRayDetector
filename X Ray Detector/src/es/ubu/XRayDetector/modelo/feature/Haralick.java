/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * Haralick.java
 * Copyright (C) 2013 Joaqu�n Bravo Panadero and Adri�n Gonz�lez Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.feature;

import java.awt.Rectangle;
import java.util.Arrays;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.EigenDecomposition;
import org.ejml.ops.CommonOps;


import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

//import weka.core.matrix.EigenvalueDecomposition;
//import weka.core.matrix.Matrix;
//import cern.colt.matrix.linalg.EigenvalueDecomposition;
//import cern.colt.matrix.DoubleFactory2D;
//import cern.colt.matrix.DoubleMatrix1D;
//import cern.colt.matrix.DoubleMatrix2D;

/**
 * Class that calculates the Haralick features.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaqu�n Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adri�n Gonzalez Duarte </a>
 * @see Feature
 * @version 2.0
 */
public class Haralick extends Feature {
	
	/**
	 * Size of arrays.
	 */
	private int SIZE = 256;
	
	/**
	 * Features counter.
	 */
	private int count = 0;
	
	/**
	 * Gray level co-ocurrence matrix.
	 */
	private double[][] glcm = new double[SIZE][SIZE];
	
	/**
	 * Px-y(i).
	 */
	private double[] px_y = new double[SIZE];
	
	/**
	 * Px+y(i).
	 */
	private double[] pxy = new double[2 * SIZE];
	
	/**
	 * HXY1 statistics.
	 */
	private double hx = 0;
	
	/**
	 * HXY2 statistics.
	 */
	private double hy = 0;
	
	/**
	 * HXY1 statistics.
	 */
	private double hxy1 = 0;
	
	/**
	 * HXY2 statistics.
	 */
	private double hxy2 = 0;
	
	/**
	 * p_x statistics.
	 */
	private double[] p_x = new double[SIZE];
	
	/**
	 * p_y statistics.
	 */
	private double[] p_y = new double[SIZE];
	
	/**
	 * Results vector.
	 */
	private double[] haralickVector;
	
	/**
	 * Class constructor.
	 */
	public Haralick() {
		super();
	}

	/**
	 * First constructor of Haralick.
	 * 
	 * @param selectedStep
	 *            Selected step
	 * @param step
	 *            Step
	 */
	public Haralick(String selectedStep, int step) {
		super(selectedStep, step);
		
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
	 * Calculates a gray level co-occurrence matrix.
	 * @param roi Region of interest
	 * @param image Image
	 */
	public void glcm(Roi roi, ImagePlus image) {

		// This part get all the pixel values into the pixel [ ] array via the
		// Image Processor
		IJ.run(image, "8-bit", "");
		byte[] pixels = (byte[]) (image.getProcessor().getPixels());
		int width = image.getWidth();
		Rectangle r = roi.getBounds();

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
					b = 0xff & (image.getProcessor().getPixel(x + getStep(), y));

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
					b = 0xff & (image.getProcessor().getPixel(x, y - getStep()));
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
					b = 0xff & (image.getProcessor().getPixel(x - getStep(), y));
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
					b = 0xff & (image.getProcessor().getPixel(x, y + getStep()));
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
		
		return correlation;
	}

	/**
	 * Calculation of the correlation parameter.
	 * @param correlation Correlation value
	 * @param px PX values
	 * @param py PY values
	 * @param stdevx Standard deviation (x)
	 * @param stdevy Standard deviation (y)
	 * @return Correlation parameter
	 */
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

		return Math.sqrt(1 - Math.exp(-2 * (hxy2 - entropy)));

	}

	/**
	 * Calculates the maximal correlation coefficient.
	 * New version using <b>EJML</b>.
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
//		Matrix matrix = new Matrix(Q);
//		double[] values = new double[matrix.getColumnDimension()];
//		
//		EigenvalueDecomposition eigenValues = new EigenvalueDecomposition(matrix);
//		values = eigenValues.getRealEigenvalues();
		
		//COLT
		/*DoubleFactory2D f = DoubleFactory2D.dense;
		
		DoubleMatrix2D matrix = f.make(Q);
		
		EigenvalueDecomposition eigenValues = new EigenvalueDecomposition(matrix);
		DoubleMatrix1D values = eigenValues.getRealEigenvalues();*/
		
		DenseMatrix64F matrix = new DenseMatrix64F(Q);
		double[] values = new double[matrix.numCols];
		
		if(CommonOps.det(matrix) == 0){
			DenseMatrix64F id = CommonOps.identity(matrix.numRows, matrix.numCols);
			CommonOps.scale(0.001, id);
			CommonOps.add(matrix, id, matrix);
		}
		
		EigenDecomposition<DenseMatrix64F> evd = DecompositionFactory.eig(matrix.numCols, false);
		
		
		if(!evd.decompose(matrix)){
			System.out.println("Det: " + CommonOps.det(matrix));
			 throw new RuntimeException("Decomposition failed");
		}
		
		for(int i = 0; i<evd.getNumberOfEigenvalues(); i++){
			values[i] = evd.getEigenvalue(i).real;
		}
		

		for (int i = 0; i < matrix.numCols; i++) {
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

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.feature.Feature#calcular(ij.gui.Roi, ij.ImagePlus, ij.ImagePlus, ij.ImagePlus)
	 */
	@Override
	public void calcular(Roi roi, ImagePlus image, ImagePlus imageFd, ImagePlus imageSd) {
		haralickVector = new double[14];
		headVector = new String[14];

		glcm(roi, image);
		calculateStatistics();
		headVector[count] = "angularSecondMoment";
		haralickVector[count++] = angularSecondMoment();
		headVector[count] = "contrast";
		haralickVector[count++] = contrast();
		headVector[count] = "correlation";
		haralickVector[count++] = correlation();
		headVector[count] = "sumOfSquares";
		haralickVector[count++] = sumOfSquares();
		headVector[count] = "inverseDifferenceMoment";
		haralickVector[count++] = inverseDifferenceMoment();
		headVector[count] = "sumAverage";
		haralickVector[count++] = sumAverage();
		headVector[count] = "sumEntropy";
		haralickVector[count++] = sumEntropy();
		headVector[count] = "sumVariance";
		haralickVector[count++] = sumVariance();
		headVector[count] = "entropy";
		haralickVector[count++] = entropy();
		headVector[count] = "differenceVariance";
		haralickVector[count++] = differenceVariance();
		headVector[count] = "differenceEntropy";
		haralickVector[count++] = differenceEntropy();
		headVector[count] = "imc_1";
		haralickVector[count++] = imc_1();
		headVector[count] = "imc_2";
		haralickVector[count++] = imc_2();
		headVector[count] = "maximalCorrelationCoefficient";
		//haralickVector[count++] = maximalCorrelationCoefficient();
		haralickVector[count++] = 0;
		
		setVectorResultados(haralickVector);
	}

}
