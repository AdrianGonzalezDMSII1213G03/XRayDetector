package modelo;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import java.awt.Rectangle;


public class Standard extends Feature {

	private double[] standardVector;
	private int count = 0;
	private int coordX;
	private int coordY;
	
	//private List<String> features;
	//private int numStandard;
	
	public Standard() {
		super();		
	}

	@Override
	public void calcular(Roi roi, ImagePlus image, ImagePlus imageFd, ImagePlus imageSd) {
		//de momento, se calculan todos
		standardVector = new double[4];
		headVector = new String[4];
		IJ.run(image, "8-bit", "");
		
		headVector[count] = "mean";
		standardVector[count++] = mean(roi, image);
		headVector[count] = "standardDeviation";
		standardVector[count++] = standardDeviation(roi, image);
		headVector[count] = "firstDerivative";
		standardVector[count++] = firstDerivative(roi, imageFd);
		headVector[count] = "secondDerivative";
		standardVector[count++] = secondDerivative(roi, imageSd);
		
		setVectorResultados(standardVector);
	}

	/**
	 * Calculates the mean of all pixel values of the ROI.
	 * 
	 * @return mean
	 */
	public double mean(Roi roi, ImagePlus image) {
		double sum = 0, total = 0;
		
		Rectangle r = roi.getBounds();
		
		coordX = r.x;
		coordY = r.y;

		for (int y = coordY; y < coordY + r.height; y++) {
			for (int x = coordX; x < coordX + r.width; x++) {
				sum = sum + image.getProcessor().getPixel(x, y);
				total++;
			}
		}		

		return sum / total;
	}

	/**
	 * Calculates the standard deviation.
	 * 
	 * @return Standard deviation
	 */
	public double standardDeviation(Roi roi, ImagePlus image) {
		int total = 0;
		double sum = 0, power = 0, mean = 0;
		
		Rectangle r = roi.getBounds();
		
		coordX = r.x;
		coordY = r.y;

		for (int y = coordY; y < coordY + r.height; y++) {
			for (int x = coordX; x < coordX + r.width; x++) {
				sum = sum + image.getProcessor().getPixel(x, y);
				total++;
			}
		}

		mean = sum / total;

		total = 0;
		for (int y = coordY; y < coordY + r.height; y++) {
			for (int x = coordX; x < coordX + r.width; x++) {

				power = power
						+ Math.pow(
								(image.getProcessor().getPixel(x, y) - mean), 2);

				total++;
			}
		}

		// Tambien puede ser total-1, sería es una mejora
		return Math.sqrt(power / total);
	}

	/**
	 * Calculates the first derivative.
	 * 
	 * @return first derivative
	 */
	public double firstDerivative(Roi roi, ImagePlus imageFd) {
		double sum = 0;
		int total = 0;
		
		Rectangle r = roi.getBounds();
		
		coordX = r.x;
		coordY = r.y;
		
		for (int i = coordY; i < coordY + r.height; i++) {
			for (int j = coordX; j < coordX + r.width; j++) {
				sum = sum + Math.abs(imageFd.getProcessor().getPixel(j-coordX, i-coordY));
				total++;
			}
		}

		return sum / total;
	}

	/**
	 * Calculates the second derivative.
	 * 
	 * @return Second derivative
	 */
	public double secondDerivative(Roi roi, ImagePlus imageSd) {
		double sum = 0, total = 0;
		
		Rectangle r = roi.getBounds();
		
		coordX = r.x;
		coordY = r.y;
		
		for (int y = coordY; y < coordY + r.height; y++) {
			for (int x = coordX; x < coordX + r.width; x++) {
				sum = sum + imageSd.getProcessor().getPixel(x-coordX, y-coordY);
				total++;
			}
		}

		return sum / total;

	}
}
