package modelo;

import java.awt.Rectangle;

import ij.ImagePlus;
import ij.gui.Roi;

public class Lbp extends Feature {
	
	public Lbp() {
		super();
	}


	@Override
	public void calcular(Roi roi, ImagePlus image, ImagePlus imageFd, ImagePlus imageSd) {
		double[] hist = getHistogram(roi, image);
		headVector = new String[]{"LBP"};
		
		setVectorResultados(hist);
	}

	/**
	 * Calculates the local binary patterns features.
	 * 
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
