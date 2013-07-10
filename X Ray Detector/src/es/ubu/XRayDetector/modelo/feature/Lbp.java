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
 * Lbp.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.feature;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;

/**
 * Class that calculates the LBP features.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @see Feature
 * @version 2.0
 */
public class Lbp extends Feature {
	
	/**
	 * Class constructor.
	 */
	public Lbp() {
		super();
	}


	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.feature.Feature#calcular(ij.gui.Roi, ij.ImagePlus, ij.ImagePlus, ij.ImagePlus)
	 */
	@Override
	public void calcular(Roi roi, ImagePlus image, ImagePlus imageFd, ImagePlus imageSd) {
		double[] hist = getHistogram(roi, image);
		headVector = new String[]{"LBP"};
		
		setVectorResultados(hist);
	}

	/**
	 * Calculates the local binary patterns features.
	 * @param roi Region of interest
	 * @param image Image
	 * @return vector with 59 lbp values
	 */
	public double[] getHistogram(Roi roi, ImagePlus image) {
		int[] patternVector = { 0, 1, 2, 3, 4, 6, 7, 8, 12, 14, 15, 16, 24, 28,
				30, 31, 32, 48, 56, 60, 62, 63, 64, 96, 112, 120, 124, 126,
				127, 128, 129, 131, 136, 142, 159, 191, 192, 193, 195, 199,
				207, 223, 224, 225, 227, 231, 239, 240, 241, 243, 247, 248,
				249, 251, 252, 253, 254, 255 };
		double[] lbpVector = new double[59];
		double[] pattern = new double[8];
		int count;
		double decimal;
		boolean found;
		
		Rectangle r = roi.getBounds();
		
		int coordX = r.x;
		int coordY = r.y;
		
		for (int y = coordY + 1; y < coordY + r.height - 1; y++) {
			for (int x = coordX + 1; x < coordX + r.width -1; x++) {
				
				// Vector con las posiciones vecinas
				int[] positions = { x, y - 1, x + 1, y - 1, x + 1, y, x + 1,
						y + 1, x, y + 1, x - 1, y + 1, x - 1, y, x - 1, y - 1 };
				count = 0;
				decimal = 0;
				found = false;

				for (int i = 0; i < positions.length; i = i + 2) {
					if (image.getProcessor().getPixel(positions[i],
							positions[i + 1]) >= image.getProcessor().getPixel(
							x, y)) {
						pattern[count] = 1;
					} else
						pattern[count] = 0;

					decimal = decimal + pattern[count] * (Math.pow(2, count));
					count++;
				}

				for (int j = 0; j < patternVector.length; j++) {
					if (decimal == patternVector[j]) {
						lbpVector[j] = lbpVector[j] + 1;
						found = true;
					}
				}
				if (found == false)
					lbpVector[58] = lbpVector[58] + 1;
			}
		}

		return lbpVector;
	}

}
