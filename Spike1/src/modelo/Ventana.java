package modelo;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Color;

public class Ventana extends Thread{

	private int altura = 8;
	private int anchura = 8;
	//private FileReader fileReader;
	private ImagePlus img;
	private ImagePlus copiaImagen;
	String grades[] = { "0 degrees", "90 degrees", "180 degrees",
	"270 degrees" };
	private double[] meanVector;
	private double[] rangeVector;
	
	
	public Ventana(ImagePlus img) {
		this.img = img;
		copiaImagen = this.img.duplicate();
		IJ.run(copiaImagen, "RGB Color", "");
		IJ.setForegroundColor(0, 255, 121);
	}

	@SuppressWarnings("static-access")
	public void run(){;
		int salto = (int) (altura*0.8);
		int coordenadaX, coordenadaY, color = 0;
		Color c = null;
		ImageProcessor ip = img.getProcessor();
		double means[], ranges[], vector0[] = null, vector90[] = null, vector180[] = null, vector270[] = null;
		boolean initializedNormal = false;
		
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - altura; coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - anchura; coordenadaX += salto) {
				ip.setRoi(coordenadaX, coordenadaY, anchura, altura);
				copiaImagen.setRoi(coordenadaX, coordenadaY, anchura, altura);
				switch(color){
					case 0:
						c = c.BLUE;
						color = 1;
						break;
					case 1:
						c = c.RED;
						color = 2;
						break;
					case 2:
						c = c.GREEN;
						color = 0;
						break;
				}
				cambiaColor(c);
				dibujaRoi();
				
				Feature ft = new Standard(img);
				ft.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ft.calcular();
				
				Feature ft2 = new Lbp(img);
				ft2.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ft2.calcular();
				
				Feature ft3 = null;
				
				int total = 0;
				for (int step = 1; step < 6; step++) {
					for (int w = 0; w < 4; w++) {
					
						ft3 = new Haralick(img, grades[w], step);
						ft3.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
						ft3.calcular();
						
						switch (w) {
						case 0:
							vector0 = ft3.getVectorResultados();
							break;
						case 1:
							vector90 = ft3.getVectorResultados();
							break;
						case 2:
							vector180 = ft3.getVectorResultados();
							break;
						case 3:
							vector270 = ft3.getVectorResultados();
							break;
						}

					}

					means = calculateMean(vector0, vector90, vector180, vector270);
					ranges = calculateRange(vector0, vector90, vector180, vector270);

					if (initializedNormal == false) {
						meanVector = new double[ft3.getVectorResultados().length * 5];
						rangeVector = new double[ft3.getVectorResultados().length * 5];
						initializedNormal = true;
					}
					for (int k = 0; k < means.length; k++) {
						// Sale un vector que contiene los 5 steps de medias
						meanVector[total] = means[k];
						rangeVector[total] = ranges[k];
						total++;
					}
				}
			}
		}
		guardaCopia();
	}
	
	public void dibujaRoi(){
		copiaImagen.getProcessor().draw(copiaImagen.getRoi());
	}
	
	public void cambiaColor(Color c){
		copiaImagen.getRoi().setFillColor(c);
		copiaImagen.getProcessor().setColor(c);
	}
	
	public void guardaCopia(){
		IJ.saveAs(copiaImagen, "BMP", "./res/img/" + img.getTitle() + "_copia");
	}
	
	/**
	 * This method calculates the mean of each box from four vectors.
	 * 
	 * @param vector0
	 *            Vector with the features for the 0 grades
	 * @param vector90
	 *            Vector with the features for the 90 grades
	 * @param vector180
	 *            Vector with the features for the 180 grades
	 * @param vector270
	 *            Vector with the features for the 027 grades
	 * @return vector with the mean
	 */
	public double[] calculateMean(double[] vector0, double[] vector90,
			double[] vector180, double[] vector270) {
		double[] mean = new double[vector0.length];

		for (int i = 0; i < vector0.length; i++) {
			mean[i] = (vector0[i] + vector90[i] + vector180[i] + vector270[i]) / 4;
		}

		return mean;
	}

	/**
	 * Calculates the range.
	 * 
	 * @param vector0
	 *            Vector with the features for the 0 grades
	 * @param vector90
	 *            Vector with the features for the 90 grades
	 * @param vector180
	 *            Vector with the features for the 180 grades
	 * @param vector270
	 *            Vector with the features for the 027 grades
	 * @return vector with the range
	 */
	public double[] calculateRange(double[] vector0, double[] vector90,
			double[] vector180, double[] vector270) {
		double[] range = new double[vector0.length];
		double[] compareVector = new double[4];
		double max;

		for (int i = 0; i < vector0.length; i++) {
			compareVector[0] = Math.abs(vector0[i]);
			compareVector[1] = Math.abs(vector90[i]);
			compareVector[2] = Math.abs(vector180[i]);
			compareVector[3] = Math.abs(vector270[i]);
			max = -2000;

			for (int j = 0; j < compareVector.length; j++) {
				if (compareVector[j] > max) {
					max = compareVector[j];
				}
			}
			range[i] = max;
		}
		return range;
	}
}
