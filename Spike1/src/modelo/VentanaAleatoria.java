package modelo;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class VentanaAleatoria extends VentanaAbstracta {
	
	public ArrayList<int []> listaDefectos = new ArrayList<int []>();
	public ArrayList<int []> listaNoDefectos = new ArrayList<int []>();
	String grades[] = { "0 degrees", "90 degrees", "180 degrees",
	"270 degrees" };
	private double[] meanVector;
	private double[] rangeVector;
	private double[] lbp;
	Feature ftStandard, ftHaralick, ftLbp;
	double means[], ranges[], vector0[] = null, vector90[] = null, vector180[] = null, vector270[] = null;
	boolean initializedNormal = false;
	ImagePlus copiaStandard;

	public VentanaAleatoria(ImagePlus img, int numHilo) {
		super(img, numHilo);
	}

	@Override
	public void run() {
		rellenarListas();
		//imprimeListas();
		seleccionarVentanas();		
		
	}

	@SuppressWarnings("unused")
	private void imprimeListas() {
		Iterator<int []> it = listaDefectos.iterator();
		while(it.hasNext()){
			int[] coordCentro = it.next();
			System.out.println("Coordenada defecto en hilo " + getNumHilo() + "[" + coordCentro[0] + "," + coordCentro[1] + "]");
		}

		Iterator<int []> it2 = listaNoDefectos.iterator();
		while(it2.hasNext()){
			int[] coordCentro = it2.next();
			System.out.println("Coordenada no defecto en hilo " + getNumHilo() + "[" + coordCentro[0] + "," + coordCentro[1] + "]");
		}
	}

	private void rellenarListas() {
		int salto = (int) (getAltura()*0.8);
		int coordenadaX, coordenadaY;
		ImageProcessor ip = getImage().getProcessor();
		
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - getAltura(); coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - getAnchura(); coordenadaX += salto) {
				getImage().setRoi(coordenadaX, coordenadaY, getAnchura(), getAltura());
				int[] coordCentro = new int[]{(int)ip.getRoi().getCenterX(), (int)ip.getRoi().getCenterY()};
				
				if(getDefecto(getImage().duplicate())){
					listaDefectos.add(coordCentro);
				}
				else{
					listaNoDefectos.add(coordCentro);
				}
			}
		}
	}
	
	/**
	 * This method checks in the mask if the region of interest analyzed has a
	 * colored defect. It returns true if the region has defects. If there is no
	 * defects, it returns false.
	 * 
	 * @param img
	 *            mask with colored defects
	 * @return true if it has defects, false if not
	 */
	private boolean getDefecto(ImagePlus img){

		//boolean defect = false;
		int defectVector[];

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				defectVector = img.getPixel(x, y);
				//System.out.println("Valor pixel: [" + defectVector[1] + "," + defectVector[2] + "," + defectVector[3] + "]");
				// El valor 0 del vector, guarda el valor en escala de grises.
				// Por eso no nos interesa.
				if ((defectVector[1] != 255 && defectVector[1] != 0)
						|| (defectVector[2] != 255 && defectVector[2] != 0)) {
					return true;
				}
			}
		}

		return false;
	}
	
	private void seleccionarVentanas(){
		int nAttemps = 150;
		ArrayList<int []> copiaListaDefectos = listaDefectos;
		ArrayList<int []> copiaListaNoDefectos = listaNoDefectos;
		
		for(int i=0; i < nAttemps; i++){
			if(copiaListaDefectos.size() > 0 && copiaListaNoDefectos.size() > 0){
				int cola = (int) Math.random()*10;
				//System.out.println("\tCola: " + cola);
				if(cola <= 5){
					int randIndex = ((int) Math.random()*copiaListaDefectos.size());
					int [] coordVentana = copiaListaDefectos.get(randIndex);
					calcularCaracteristicas(coordVentana[0], coordVentana[1], true);
					copiaListaDefectos.remove(randIndex);
				}
				else{
					int randIndex = ((int) Math.random()*copiaListaNoDefectos.size());
					int [] coordVentana = copiaListaNoDefectos.get(randIndex);
					calcularCaracteristicas(coordVentana[0], coordVentana[1], false);
					copiaListaNoDefectos.remove(randIndex);
				}
			}
			else if(copiaListaDefectos.size() > 0){
				int randIndex = ((int) Math.random()*copiaListaDefectos.size());
				int [] coordVentana = copiaListaDefectos.get(randIndex);
				calcularCaracteristicas(coordVentana[0], coordVentana[1], true);
				copiaListaDefectos.remove(randIndex);
			}
			else if (copiaListaNoDefectos.size() > 0){
				int randIndex = ((int) Math.random()*copiaListaNoDefectos.size());
				int [] coordVentana = copiaListaNoDefectos.get(randIndex);
				calcularCaracteristicas(coordVentana[0], coordVentana[1], false);
				copiaListaNoDefectos.remove(randIndex);
			}
		}
	}
	
	private void calcularCaracteristicas(int coordenadaX, int coordenadaY, boolean defect){
		copiaStandard = getImagenCompleta().duplicate();
		ftStandard = new Standard(getImagenCompleta());
		ftStandard.setImagenCompleta(copiaStandard);
		ftStandard.getImage().setRoi(coordenadaX, coordenadaY, getAnchura(), getAltura());
		ftStandard.calcular();
		
		ftLbp = new Lbp(getImagenCompleta());
		ftLbp.getImage().setRoi(coordenadaX, coordenadaY, getAnchura(), getAltura());
		ftLbp.calcular();
		lbp = ftLbp.getVectorResultados();
					
		int total = 0;
		for (int step = 1; step < 6; step++) {
			for (int w = 0; w < 4; w++) {
			
				ftHaralick = new Haralick(getImagenCompleta(), grades[w], step);
				ftHaralick.getImage().setRoi(coordenadaX, coordenadaY, getAnchura(), getAltura());
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
		}
		crearArff(null, defect);
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
	
	public void crearArff(int[] coordenates, boolean defect){

		String featuresString;
		File outputFile;
		FileWriter arffFile;

		featuresString = generateFeatures(coordenates, defect);

		outputFile = new File("./res/arff/Arff_entrenamiento" + getNumHilo() + ".arff");
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
				if(getNumHilo() == 0){
					String headerFile = getHeader(false);
					arffFile = new FileWriter(outputFile);
					arffFile.write(headerFile);
				}
				else{
					arffFile = new FileWriter(outputFile);
				}
				
				System.out.println(outputFile.getPath());
				
				

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
						+ " REAL\n";
			}
		}

		/*if (standardSaliency != null) {
			for (int i = 0; i < standardSaliency.getHead().length; i++) {
				attributesString += "@attribute "
						+ standardSaliency.getHead()[i] + "(S)" + " REAL\n";
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
							+ "_mean" + j + " REAL\n";
				}
			}

			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < ftHaralick.getHead().length; i++) {
					attributesString += "@attribute " + ftHaralick.getHead()[i]
							+ "_range" + j + " REAL\n";
				}
			}
		}

		/*if (haralickSaliency != null) {
			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < haralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ haralickSaliency.getHead()[i] + "_mean" + j
							+ "(S)" + " REAL\n";
				}
			}

			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < haralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ haralickSaliency.getHead()[i] + "_range" + j
							+ "(S)" + " REAL\n";
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
						+ lbpHead[i] + ") REAL\n";
			}
		}

		/*if (lbpSaliency != null) {
			for (int i = 0; i < lbpSaliencyHead.length; i++) {
				attributesString += "@attribute " + lbpSaliency.getHead() + "("
						+ lbpSaliencyHead[i] + ")(S) REAL\n";
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
