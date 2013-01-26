package modelo;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Ventana extends Thread{

	private int altura = 8;
	private int anchura = 8;
	private int numHilo;
	//private FileReader fileReader;
	private ImagePlus img;
	private ImagePlus copiaImagen;
	String grades[] = { "0 degrees", "90 degrees", "180 degrees",
	"270 degrees" };
	private double[] meanVector;
	private double[] rangeVector;
	private double[] lbp;
	Feature ftStandard, ftHaralick, ftLbp;
	
	
	public Ventana(ImagePlus img, int numHilo) {
		this.img = img;
		this.numHilo = numHilo;
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
				
				ftStandard = new Standard(img);
				ftStandard.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ftStandard.calcular();
				
				ftLbp = new Lbp(img);
				ftLbp.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ftLbp.calcular();
				lbp = ftLbp.getVectorResultados();
				
				int[] coordCentro = new int[]{(int)ip.getRoi().getCenterX(), (int)ip.getRoi().getCenterY() + numHilo*img.getHeight()};
				crearArff(coordCentro);
				
							
				/*int total = 0;
				for (int step = 1; step < 6; step++) {
					for (int w = 0; w < 4; w++) {
					
						ftHaralick = new Haralick(img, grades[w], step);
						ftHaralick.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
						ftHaralick.calcular();
						
						switch (w) {
						case 0:
							vector0 = ftHaralick.getVectorResultados();
							break;
						case 1:
							vector90 = ftHaralick.getVectorResultados();
							break;
						case 2:
							vector180 = ftHaralick.getVectorResultados();
							break;
						case 3:
							vector270 = ftHaralick.getVectorResultados();
							break;
						}

					}

					means = calculateMean(vector0, vector90, vector180, vector270);
					ranges = calculateRange(vector0, vector90, vector180, vector270);

					if (initializedNormal == false) {
						meanVector = new double[ftHaralick.getVectorResultados().length * 5];
						rangeVector = new double[ftHaralick.getVectorResultados().length * 5];
						initializedNormal = true;
					}
					for (int k = 0; k < means.length; k++) {
						// Sale un vector que contiene los 5 steps de medias
						meanVector[total] = means[k];
						rangeVector[total] = ranges[k];
						total++;
					}
				}*/
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
	
	/**
	 * Method that generates a header for the file that will store the
	 * characteristics.
	 * 
	 * @return string with the header
	 */
	public String getHeader(boolean coordenadas) {
		String header = new String();
		header += "% 1. Titulo: Deteccion de defectos en piezas metalicas \n%\n";
		header += "% 2. Fuentes:\n";
		header += "%       (a) Creador: Alan Blanco Alamo\n";
		header += "%       (b) Creador: Victor Barbero Garcia\n";
		header += "@relation deteccionDefectos \n";
		if (coordenadas) {
			header += "@attribute coordenadasX INTEGER\n";
			header += "@attribute coordenadasY INTEGER\n";
		}
		header += getStandardAttributes();
		header += getHaralickAttributes();
		header += getLbpAttributes();
		header += "@ATTRIBUTE class {true, false}\n";
		header += "@data\n";
		return header;
	}
	
	public synchronized void crearArff(int[] coordenates){

		String featuresString;
		File outputFile;
		FileWriter arffFile;

		featuresString = generateFeatures(coordenates, false);

		outputFile = new File("./res/arff/Arff_prueba.arff");
		try {
			if (!outputFile.exists()) {
				String headerFile = getHeader(true);
				System.out.println(outputFile.getPath());
				outputFile.createNewFile();
				arffFile = new FileWriter(outputFile);
				arffFile.write(headerFile);

			} else {
				// si ya esta creado se escribe a continuacion
				arffFile = new FileWriter(outputFile, true);
			}

			arffFile.write(featuresString + "\n");
			arffFile.close();
		} catch (IOException e) {
			System.out.println("Problema con los ficheros");
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that generates a string with the name and type of each standard
	 * attribute. In Weka arff format.
	 * 
	 * @return string with the information of all attributes
	 */
	private String getStandardAttributes() {
		String attributesString = new String();
		if (ftStandard != null) {
			for (int i = 0; i < ftStandard.getHead().length; i++) {
				attributesString += "@attribute " + ftStandard.getHead()[i]
						+ " INTEGER\n";
			}
		}

		/*if (standardSaliency != null) {
			for (int i = 0; i < standardSaliency.getHead().length; i++) {
				attributesString += "@attribute "
						+ standardSaliency.getHead()[i] + "(S)" + " INTEGER\n";
			}
		}*/
		return attributesString;
	}

	/**
	 * Method that generates a string with the name and type of each haralick
	 * attribute. In Weka arff format.
	 * 
	 * @return string with the information of all attributes
	 */
	private String getHaralickAttributes() {
		String attributesString = new String();

		if (ftHaralick != null) {
			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < ftHaralick.getHead().length; i++) {
					attributesString += "@attribute " + ftHaralick.getHead()[i]
							+ "_mean" + j + " INTEGER\n";
				}
			}

			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < ftHaralick.getHead().length; i++) {
					attributesString += "@attribute " + ftHaralick.getHead()[i]
							+ "_range" + j + " INTEGER\n";
				}
			}
		}

		/*if (haralickSaliency != null) {
			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < haralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ haralickSaliency.getHead()[i] + "_mean" + j
							+ "(S)" + " INTEGER\n";
				}
			}

			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < haralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ haralickSaliency.getHead()[i] + "_range" + j
							+ "(S)" + " INTEGER\n";
				}
			}
		}*/

		return attributesString;
	}

	/**
	 * Method that generates a string with the name and type of each lbp
	 * attribute. In Weka arff format.
	 * 
	 * @return string with the information of all attributes
	 */
	private String getLbpAttributes() {
		String attributesString = new String();
		int[] lbpHead = new int[59];
		
		for (int x = 0; x < 59; x++) {
			lbpHead[x] = x + 1;
		}

		if (lbp != null) {
			for (int i = 0; i < lbpHead.length; i++) {
				attributesString += "@attribute " + ftLbp.getHead()[0] + "("
						+ lbpHead[i] + ") INTEGER\n";
			}
		}

		/*if (lbpSaliency != null) {
			for (int i = 0; i < lbpSaliencyHead.length; i++) {
				attributesString += "@attribute " + lbpSaliency.getHead() + "("
						+ lbpSaliencyHead[i] + ")(S) INTEGER\n";
			}
		}*/

		return attributesString;
	}
	
	/**
	 * Method that generates a string of features.
	 * 
	 * @return string of features
	 */
	public String generateFeatures(int[] coordenates, boolean defect) {
		String features = new String();
		if (coordenates != null) {
			features += coordenates[0];
			features += ", ";
			features += coordenates[1];
			features += ", ";
		}

		if (ftStandard != null) {
			for (int i = 0; i < ftStandard.getVectorResultados().length; i++) {
				features += ftStandard.getVectorResultados()[i];
				features += ", ";
			}
		}

		/*if (standardSaliency != null) {
			for (int i = 0; i < standardSaliency.getStandardVector().length; i++) {
				features += standardSaliency.getStandardVector()[i];
				features += ", ";
			}
		}*/

		if (ftHaralick != null) {
			for (int i = 0; i < meanVector.length; i++) {
				features += meanVector[i];
				features += ", ";
			}

			for (int i = 0; i < rangeVector.length; i++) {
				features += rangeVector[i];
				features += ", ";
			}
		}

		/*if (haralickSaliency != null) {
			for (int i = 0; i < saliencyMeanVector.length; i++) {
				features += saliencyMeanVector[i];
				features += ", ";
			}

			for (int i = 0; i < saliencyRangeVector.length; i++) {
				features += saliencyRangeVector[i];
				features += ", ";
			}
		}*/

		if (ftLbp != null) {
			for (int i = 0; i < lbp.length; i++) {
				features += lbp[i];
				features += ", ";
			}
		}

		/*if (lbpSaliency != null) {
			for (int i = 0; i < lbpSaliencyVector.length; i++) {
				features += lbpSaliencyVector[i];
				features += ", ";
			}
		}*/

		features += defect;

		return features;
	}
}
