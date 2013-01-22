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
		int N_sigma = 0, w, s, x_min, x_max, y_min, y_max, N = image
				.getHeight(), M = image.getWidth();
		int[] sigma = null;
		int[][] sat = new int[N][M];

		// cumsum(I)
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (j == 0)
					sat[j][i] = image.getProcessor().getPixel(i, j);
				else
					sat[j][i] = image.getProcessor().getPixel(i, j)
							+ sat[j - 1][i];
			}
		}

		// cumsum(cumsum((I),2))
		for (int j = 0; j < N; j++) {
			for (int i = 0; i < M; i++) {

				if (i > 0)
					sat[j][i] = sat[j][i] + sat[j][i - 1];
			}
		}

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

		for (int scales = 0; scales < N_sigma; scales++) {
			s = sigma[scales] + 1;
			double[][] subWindow = new double[N][M];

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

			double[][] surround = new double[N][M];
			double[][] repmat = new double[N][M];

			for (int j = 0; j < M; j++) {
				for (int i = 0; i < N; i++) {
					repmat[i][j] = Math.pow((2 * s) + 1, 2) - 1;
				}
			}

			for (int j = 0; j < M; j++) {
				for (int i = 0; i < N; i++) {
					surround[i][j] = (subWindow[i][j] - image.getProcessor()
							.getPixel(j, i)) / repmat[i][j];
				}
			}

			// establece los maximos para J_off
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

		// Suma las 6 matrices en una
		double value;
		int scales;

		ShortProcessor image2 = new ShortProcessor(M, N);

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

		return image2.getBufferedImage();
	}

}
