package modelo;

import ij.ImagePlus;
import java.awt.image.BufferedImage;
import ij.process.ShortProcessor;

public class Saliency extends Preprocesamiento {

	public Saliency(ImagePlus image, int type) {
		super(image, type);
	}

	@Override
	public BufferedImage calcular() {
		return saliencyMap(getImage(), getType());
	}
	/**
	 * This method creates a saliency map from an ImagePlus image.
	 * 
	 * @param image
	 *            Image to make saliency map
	 * @param type
	 *            Sigma
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

	private void calcularSurround(ImagePlus image, int N, int M,
			double[][] subWindow, double[][] surround, double[][] repmat) {
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				surround[i][j] = (subWindow[i][j] - image.getProcessor()
						.getPixel(j, i)) / repmat[i][j];
			}
		}
	}

	private void calcularRepmat(int s, int N, int M, double[][] repmat) {
		for (int j = 0; j < M; j++) {
			for (int i = 0; i < N; i++) {
				repmat[i][j] = Math.pow((2 * s) + 1, 2) - 1;
			}
		}
	}

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

	private void sumaColumnaAnterior(int N, int M, int[][] sat) {
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < M; i++) {
				if (i > 0)
					sat[j][i] = sat[j][i] + sat[j][i - 1];
			}
		}
	}

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
