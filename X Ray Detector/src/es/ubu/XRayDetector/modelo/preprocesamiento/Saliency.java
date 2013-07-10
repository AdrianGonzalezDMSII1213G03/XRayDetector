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
 * Saliency.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.preprocesamiento;

import ij.ImagePlus;
import java.awt.image.BufferedImage;
import ij.process.ShortProcessor;

/**
 * Class Saliency.
 * 
 * A preprocessor image type.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class Saliency extends Preprocesamiento {

	/**
	 * Constructor method.
	 * 
	 * @param image The image to be preprocessed.
	 * @param type Type of preprocessing set.
	 */
	public Saliency(ImagePlus image, int type) {
		super(image, type);
	}

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.preprocesamiento.Preprocesamiento#calcular()
	 */
	@Override
	public BufferedImage calcular() {
		return saliencyMap(getImage(), getType());
	}

	/**
	 * This method creates a saliency map from an ImagePlus image.
	 * 
	 * @param image Image to make saliency map
	 * @param type Sigma
	 * @return Saliency map image
	 */
	public BufferedImage saliencyMap(ImagePlus image, int type) {
		int N_sigma = 0, w, N = image
				.getHeight(), M = image.getWidth();
		int[] sigma = null;
		int[][] sat = new int[N][M];

		// cumsum(I)
		rellenarMatrizPixeles(image, N, M, sat);

		// cumsum(cumsum((I),2))
		sumaColumnaAnterior(N, M, sat);

		// Por defecto es 1
		switch (type) {
			case 1:
				int[] sigma1 = { 12, 24, 28, 48, 56, 112 };
				N_sigma = sigma1.length;
				sigma = sigma1.clone();
				break;
	
			// Falta el caso 2 (opcional)
			case 2:
				if (N > M)
					w = N;
				else
					w = M;
	
				int[] sigma2 = { w / 8, w / 4, w / 2 };
				N_sigma = sigma2.length;
				sigma = sigma2.clone();
				break;
	
			case 3:
				int[] sigma3 = { 7, 9, 11, 13, 17, 19 };
				N_sigma = sigma3.length;
				sigma = sigma3.clone();
				break;
		}

		double[][][] J_off = new double[N][M][N_sigma];

		calcularEscalas(image, N_sigma, N, M, sigma, sat, J_off);

		// Suma las 6 matrices en una
		ShortProcessor image2 = new ShortProcessor(M, N);

		sumarMatrices(N_sigma, N, M, J_off, image2);

		return image2.getBufferedImage();
	}

	/**
	 * Add two matrix.
	 * 
	 * @param N_sigma Maximum sigma value.
	 * @param N Size of a matrix.
	 * @param M Size of a matrix.
	 * @param J_off Array of scale values.
	 * @param image2 Image with the saliency calculated.
	 */
	private void sumarMatrices(int N_sigma, int N, int M, double[][][] J_off,
			ShortProcessor image2) {
		double value;
		int scales;
		
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				value = 0;
				for (scales = 0; scales < N_sigma; scales++) {
					value = value + J_off[i][j][scales];
				}

				// Hay que usarlo, si no queda mal el Saliency
				if (value > 255) {
					value = 255;
				}

				// Este set es para el ShortProcessor
				image2.set(j, i, (int) value);
			}
		}
	}

	/**
	 * Calculate the scales.
	 * 
	 * @param image Image to use with saliency preprocessor.
	 * @param N_sigma Maximum sigma value.
	 * @param N First dimension array size. 
	 * @param M Second dimendion array size.
	 * @param sigma Array of sigma values.
	 * @param sat Array of pixel values.
	 * @param J_off Array of scale values.
	 */
	private void calcularEscalas(ImagePlus image, int N_sigma, int N, int M,
			int[] sigma, int[][] sat, double[][][] J_off) {
		int s;
		for (int scales = 0; scales < N_sigma; scales++) {
			s = sigma[scales] + 1;
			double[][] subWindow = new double[N][M];

			calculaXYMaxMin(s, N, M, sat, subWindow);

			double[][] surround = new double[N][M];
			double[][] repmat = new double[N][M];

			calcularRepmat(s, N, M, repmat);

			calcularSurround(image, N, M, subWindow, surround, repmat);

			// establece los maximos para J_off
			establecerMaxJOff(image, N, M, J_off, scales, surround);
		}
	}

	/**
	 * Sets the maximum scale.
	 * 
	 * @param image The image used in saliency.
	 * @param N First dimension matrix size.
	 * @param M Second dimension matrix size.
	 * @param J_off Matrix of scales.
	 * @param scales Scales.
	 * @param surround The surrounding value.
	 */
	private void establecerMaxJOff(ImagePlus image, int N, int M,
			double[][][] J_off, int scales, double[][] surround) {
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				if (surround[i][j] - image.getProcessor().getPixel(j, i) < 0)
					J_off[i][j][scales] = 0;
				else
					J_off[i][j][scales] = surround[i][j]
							- image.getProcessor().getPixel(j, i);
			}
		}
	}

	/**
	 * Calculates the surround value.
	 * 
	 * @param image The image used in saliency.
	 * @param N First dimension matrix size.
	 * @param M Second dimension matrix size.
	 * @param subWindow subWindow.
	 * @param surround The surrounding value.
	 * @param repmat The repmat value.
	 */
	private void calcularSurround(ImagePlus image, int N, int M,
			double[][] subWindow, double[][] surround, double[][] repmat) {
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				surround[i][j] = (subWindow[i][j] - image.getProcessor()
						.getPixel(j, i)) / repmat[i][j];
			}
		}
	}

	/**
	 * Calculates the repmat value.
	 * 
	 * @param s Exponent value.
	 * @param N First dimension matrix size.
	 * @param M Second dimension matrix size.
	 * @param repmat Array of repmat values.
	 */
	private void calcularRepmat(int s, int N, int M, double[][] repmat) {
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				repmat[i][j] = Math.pow((2 * s) + 1, 2) - 1;
			}
		}
	}

	/**
	 * Calculatethe maximum and minimum XY. 
	 * 
	 * @param s The surround margin.
	 * @param N First dimension size.
	 * @param M Second dimension size.
	 * @param sat Pixels value.
	 * @param subWindow Subwindow.
	 */
	private void calculaXYMaxMin(int s, int N, int M, int[][] sat,
			double[][] subWindow) {
		int x_min;
		int x_max;
		int y_min;
		int y_max;
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < M; y++) {

				// max
				if (1 > x - s)
					x_min = 1;
				else
					x_min = x - s;

				// min
				if (N - 1 > x + s)
					x_max = x + s;
				else
					x_max = N - 1;

				// max
				if (1 > y - s)
					y_min = 1;
				else
					y_min = y - s;

				// min
				if (M - 1 > y + s)
					y_max = y + s;
				else
					y_max = M - 1;

				subWindow[x][y] = sat[x_min - 1][y_min - 1]
						+ sat[x_max][y_max] - sat[x_min - 1][y_max]
						- sat[x_max][y_min - 1];
			}
		}
	}

	/**
	 * Adds the previous column.
	 * 
	 * @param N Firs dimension size.
	 * @param M Second dimension size.
	 * @param sat Value of the pixel.
	 */
	private void sumaColumnaAnterior(int N, int M, int[][] sat) {
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < M; i++) {
				if (i > 0)
					sat[j][i] = sat[j][i] + sat[j][i - 1];
			}
		}
	}

	/**
	 * Fill a matrix with the pixel values.
	 * 
	 * @param image Image to extract the pixel values.
	 * @param N First dimension size.
	 * @param M Second dimension size.
	 * @param sat The pixel values.
	 */
	private void rellenarMatrizPixeles(ImagePlus image, int N, int M, int[][] sat) {
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (j == 0)
					sat[j][i] = image.getProcessor().getPixel(i, j);
				else
					sat[j][i] = image.getProcessor().getPixel(i, j)
							+ sat[j - 1][i];
			}
		}
	}
}
