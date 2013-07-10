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
 * Standard.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.feature;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import java.awt.Rectangle;


/**
 * Class that calculates the Standard features.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @see Feature
 * @version 2.0
 */
public class Standard extends Feature {

	/**
	 * Results vector.
	 */
	private double[] standardVector;
	
	/**
	 * Feature counter.
	 */
	private int count = 0;
	
	/**
	 * X coordinate.
	 */
	private int coordX;
	
	/**
	 * Y coordinate
	 */
	private int coordY;
	
	/**
	 * Constructor.
	 */
	public Standard() {
		super();		
	}

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.feature.Feature#calcular(ij.gui.Roi, ij.ImagePlus, ij.ImagePlus, ij.ImagePlus)
	 */
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
	 * @param roi Region of interest
	 * @param image Image
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
	 * @param roi Region of interest
	 * @param image Image 
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
	 * @param roi Region of interest
	 * @param imageFd First derivative image
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
	 * @param roi Region of interest
	 * @param imageSd Second derivative image
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
