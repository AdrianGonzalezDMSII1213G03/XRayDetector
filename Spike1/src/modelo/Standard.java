package modelo;

import ij.IJ;
import ij.ImagePlus;

public class Standard extends Feature {

	private double[] standardVector;
	private String[] headVector;
	private int count = 0;
	private int coordX;
	private int coordY;
	
	//private List<String> features;
	//private int numStandard;
	
	public Standard(ImagePlus image) {
		super(image);
		IJ.run(image, "8-bit", "");
	}

	@Override
	public void calcular() {
		//de momento, se calculan todos
		standardVector = new double[4];
		headVector = new String[4];
		
		coordX = getImage().getRoi().getBounds().x;
		coordY = getImage().getRoi().getBounds().y;
		
		standardVector[count] = mean();
		standardVector[count] = standardDeviation();
		standardVector[count] = firstDerivative();
		standardVector[count] = secondDerivative();
		
		setVectorResultados(standardVector);
		
		//para probar
		//imprimeResultados();
	}
	
	//sólo para probar que hace algo
	/*private void imprimeResultados() {
		for(int i = 0; i < standardVector.length; i++){
			System.out.println("Valor de " + headVector[i] + ": " + standardVector[i]);
		}
	}*/

	/**
	 * Calculates the mean of all pixel values of the ROI.
	 * 
	 * @return mean
	 */
	public double mean() {
		double sum = 0, total = 0;

		for (int y = coordY; y < coordY + getImage().getRoi().getBounds().height; y++) {
			for (int x = coordX; x < coordX + getImage().getRoi().getBounds().width; x++) {
				sum = sum + getImage().getProcessor().getPixel(x, y);
				total++;
			}
		}

		headVector[count] = "mean";
		count++;

		return sum / total;
	}

	/**
	 * Calculates the standard deviation.
	 * 
	 * @return Standard deviation
	 */
	public double standardDeviation() {
		int total = 0;
		double sum = 0, power = 0, mean = 0;

		for (int y = coordY; y < coordY + getImage().getRoi().getBounds().height; y++) {
			for (int x = coordX; x < coordX + getImage().getRoi().getBounds().width; x++) {
				sum = sum + getImage().getProcessor().getPixel(x, y);
				total++;
			}
		}

		mean = sum / total;

		total = 0;
		for (int y = coordY; y < coordY + getImage().getRoi().getBounds().height; y++) {
			for (int x = coordX; x < coordX + getImage().getRoi().getBounds().width; x++) {

				power = power
						+ Math.pow(
								(getImage().getProcessor().getPixel(x, y) - mean), 2);

				total++;
			}
		}

		headVector[count] = "standardDeviation";

		count++;

		// Tambien puede ser total-1, sería es una mejora
		return Math.sqrt(power / total);
	}

	/**
	 * Calculates the first derivative.
	 * 
	 * @return first derivative
	 */
	public double firstDerivative() {
		int m = 15; // tamaño de la mascara
		double sigma = m / 8.5;
		double gx[][] = new double[m][m];
		double gy[][] = new double[m][m];
		double y0[][] = new double[getImage().getRoi().getBounds().height][getImage().getRoi().getBounds().height];
		double y1[][] = new double[getImage().getRoi().getBounds().width][getImage().getRoi().getBounds().width];
		float conv1[] = new float[m * m];
		float conv2[] = new float[m * m];
		int k = 0, total = 0;
		double s2, c, x, x2, y, y2, ex, sum = 0, max;

		s2 = Math.pow(sigma, 2);
		c = (m - 1) / 2;

		for (int i = 1; i <= m; i++) {
			x = i - c;
			x2 = Math.pow(i - c, 2);
			for (int j = 1; j <= m; j++) {
				y = j - c;
				y2 = Math.pow(j - c, 2);
				ex = Math.pow(Math.E, (-(x2 + y2) / 2 / s2));
				gx[i - 1][j - 1] = y * ex;
				gy[i - 1][j - 1] = x * ex;
			}
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				sum = sum + Math.abs(gx[i][j]);
			}

		}
		max = sum / 2 * (0.3192 * m - 0.3543);

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				gx[i][j] = gx[i][j] / max;
				conv1[k] = (float) gx[i][j];
				gy[i][j] = gy[i][j] / max;
				conv2[k] = (float) gy[i][j];
				k++;
			}
		}

		ImagePlus copy1 = getImagenConvolucion().duplicate();
		ImagePlus copy2 = getImagenConvolucion().duplicate();
		//IJ.saveAs(copy1, "BMP", "./res/img/" + "copia_imagen_standard");

		// Al hacer estos convolve luego con el getPixel de abajo salen valores
		// entre 0 y 255
		// En el equivalente de matlab (Yx e Yy) salen valores decimales
		// negativos y positivos
		copy1.getProcessor().convolve(conv1, m, m);
		copy2.getProcessor().convolve(conv2, m, m);
		
		//copy1.getProcessor().setRoi(coordX, coordY, getImage().getRoi().getBounds().width, getImage().getRoi().getBounds().height);

		for (int i = coordY; i < coordY + getImage().getRoi().getBounds().height; i++) {
			for (int j = coordX; j < coordX + getImage().getRoi().getBounds().width; j++) {
				y0[i-coordY][j-coordX] = Math
						.sqrt((copy1.getProcessor().getPixel(j-coordX, i-coordY) * copy1
								.getProcessor().getPixel(j-coordX, i-coordY))
								+ (copy2.getProcessor().getPixel(j-coordX, i-coordY) * copy2
										.getProcessor().getPixel(j-coordX, i-coordY)));
			}
		}

		for (int i = (int) c; i < getImage().getRoi().getBounds().height; i++) {
			for (int j = (int) c; j < getImage().getRoi().getBounds().width; j++) {
				y1[i][j] = y0[i][j];
			}
		}

		sum = 0;
		for (int i = coordY; i < coordY + getImage().getRoi().getBounds().height; i++) {
			for (int j = coordX; j < coordX + getImage().getRoi().getBounds().width; j++) {
				sum = sum + Math.abs(y1[i-coordY][j-coordX]);
				total++;
			}
		}

		headVector[count] = "firstDerivative";

		count++;

		return sum / total;

	}

	/**
	 * Calculates the second derivative.
	 * 
	 * @return Second derivative
	 */
	public double secondDerivative() {
		int[] kernel = { 0, 1, 0, 1, -4, 1, 0, 1, 0 };
		double sum = 0, total = 0;

		ImagePlus copy = getImagenConvolucion().duplicate();
		copy.getProcessor().convolve3x3(kernel);
		
		//copy.getProcessor().setRoi(coordX, coordY, getImage().getRoi().getBounds().width, getImage().getRoi().getBounds().height);

		for (int y = coordY; y < coordY + getImage().getRoi().getBounds().height; y++) {
			for (int x = coordX; x < coordX + getImage().getRoi().getBounds().width; x++) {
				sum = sum + copy.getProcessor().getPixel(x-coordX, y-coordY);
				total++;
			}
		}

		headVector[count] = "secondDerivative";

		count++;

		return sum / total;

	}

	@Override
	public String[] getHead() {
		return headVector;
	}
}
