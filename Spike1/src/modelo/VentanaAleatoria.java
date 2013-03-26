package modelo;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Random;

import datos.GestorArff;

public class VentanaAleatoria extends VentanaAbstracta {
	
	public ArrayList<int []> listaDefectos = new ArrayList<int []>();
	public ArrayList<int []> listaNoDefectos = new ArrayList<int []>();
	public VentanaAleatoria(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);
	}

	@Override
	public void run() {
		rellenarListas();
		//imprimeListas();
		seleccionarVentanas();		
		
	}

	private void rellenarListas() {
		int salto = (int) (getAlturaVentana()*getPropiedades().getSalto());
		int coordenadaX, coordenadaY;
		ImageProcessor ip = getImage().getProcessor();
		
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - getAlturaVentana(); coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - getAnchuraVentana(); coordenadaX += salto) {
				getImage().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
				int[] coordVentana = new int[]{(int)coordenadaX, (int)coordenadaY};
				
				if(getDefecto(getImage().duplicate())){
					listaDefectos.add(coordVentana);
				}
				else{
					listaNoDefectos.add(coordVentana);
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

		
		//	1 Pixel mal	 			COMO EL CULO
		/*
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
		*/
		
		//	>50% Pixel mal			BASTANTE BIEN
		/*
		int numPixVentana = getAlturaVentana() * getAnchuraVentana();
		int pixelDef = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				defectVector = img.getPixel(x, y);
				//System.out.println("Valor pixel: [" + defectVector[1] + "," + defectVector[2] + "," + defectVector[3] + "]");
				// El valor 0 del vector, guarda el valor en escala de grises.
				// Por eso no nos interesa.
				if ((defectVector[1] != 255 && defectVector[1] != 0)
						|| (defectVector[2] != 255 && defectVector[2] != 0)) {
					pixelDef++;
					if(pixelDef>(numPixVentana/2)){
						return true;
					}					
				}
			}
		}
		*/
		
		//Pixel Central malo	ACEPTABLE PERO FALTA PRECISION	
//		int x = (int) img.getProcessor().getRoi().getCenterX();
//		int y = (int) img.getProcessor().getRoi().getCenterY();
//		defectVector = img.getPixel(x, y);
//		if ((defectVector[1] != 255 && defectVector[1] != 0)
//				|| (defectVector[2] != 255 && defectVector[2] != 0)) {
//			return true;
//		}					
		
		//Pixel Central + Vecinos Malo	BASTANTE BIEN
		int x = (int) img.getProcessor().getRoi().getCenterX();
		int y = (int) img.getProcessor().getRoi().getCenterY();
		int pixelDef = 0;
		//región de vecinos de 3x3
		for (int i=-1; i<2;  i++){
			for (int j=-1; j<2;  j++){
				defectVector = img.getPixel(x+j, y+i);
				if ((defectVector[1] != 255 && defectVector[1] != 0)
						|| (defectVector[2] != 255 && defectVector[2] != 0)) {
					pixelDef++;
					if(pixelDef> 5){	//porcentaje de la región del centro de la ventana
						return true;
					}				
				}
			}
		}
		
		return false;
	}
	
	private void seleccionarVentanas(){
		int nAttemps = 150;
		ArrayList<int []> copiaListaDefectos = listaDefectos;
		ArrayList<int []> copiaListaNoDefectos = listaNoDefectos;
		Random rand = new Random();
		
		for(int i=0; i < nAttemps; i++){
			if(copiaListaDefectos.size() > 0 && copiaListaNoDefectos.size() > 0){
				int cola = rand.nextInt(10);
				//System.out.println("\tCola: " + cola);
				if(cola <= 5){
					seleccionarItemLista(copiaListaDefectos, rand, true);
				}
				else{
					seleccionarItemLista(copiaListaNoDefectos, rand, false);
				}
			}
			else if(copiaListaDefectos.size() > 0){
				seleccionarItemLista(copiaListaDefectos, rand, true);
			}
			else if (copiaListaNoDefectos.size() > 0){
				seleccionarItemLista(copiaListaNoDefectos, rand, false);
			}
		}
	}

	public void seleccionarItemLista(ArrayList<int[]> lista, Random rand, boolean defect) {
		int randIndex = rand.nextInt(lista.size());
		int [] coordVentana = lista.get(randIndex);
		calcularCaracteristicas(coordVentana[0], coordVentana[1], defect);
		lista.remove(randIndex);
	}
	
	private void calcularCaracteristicas(int coordenadaX, int coordenadaY, boolean defect){
		
		ejecutarCalculos(coordenadaX, coordenadaY, getImagenCompleta());
		generarArff(coordenadaX, coordenadaY, defect);
	}

	public void generarArff(int coordenadaX, int coordenadaY, boolean defect) {
		String featuresString = generateFeatures(null, defect);
		String headerFile = null;
		if(getNumHilo() == 0){
			headerFile = getHeader(false);
		}
		GestorArff garff = new GestorArff();
		garff.crearArff(getNumHilo(), featuresString, headerFile);
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
		header += "%       (a) Creador: Adrian Gonzalez Duarte\n";
		header += "%       (b) Creador: Joaquin Bravo Panadero\n";
		header += "@relation deteccionDefectos \n";
		if (coordenadas) {
			header += "@attribute coordenadasX INTEGER\n";
			header += "@attribute coordenadasY INTEGER\n";
		}
		header += getStandardAttributes();
		header += getHaralickAttributes();
		header += getLbpAttributes();
		
		//CLASIFICACIÓN CON CLASE NOMINAL
		//header += "@ATTRIBUTE class {true, false}\n";
		
		//REGRESIÓN
		header += "@ATTRIBUTE class numeric\n";
		
		header += "@data\n";
		return header;
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

		if (ftStandardSaliency != null) {
			for (int i = 0; i < ftStandardSaliency.getHead().length; i++) {
				attributesString += "@attribute "
						+ ftStandardSaliency.getHead()[i] + "(S)" + " REAL\n";
			}
		}
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

		if (ftHaralickSaliency != null) {
			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ ftHaralickSaliency.getHead()[i] + "_mean" + j
							+ "(S)" + " REAL\n";
				}
			}

			for (int j = 1; j < 6; j++) {
				for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
					attributesString += "@attribute "
							+ ftHaralickSaliency.getHead()[i] + "_range" + j
							+ "(S)" + " REAL\n";
				}
			}
		}

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

		if (lbpSaliency != null) {
			for (int i = 0; i < lbpHead.length; i++) {
				attributesString += "@attribute " + ftLbpSaliency.getHead()[0] + "("
						+ lbpHead[i] + ")(S) REAL\n";
			}
		}

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

		if (ftStandardSaliency != null) {
			for (int i = 0; i < ftStandardSaliency.getVectorResultados().length; i++) {
				features += ftStandardSaliency.getVectorResultados()[i];
				features += ", ";
			}
		}

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

		if (ftHaralickSaliency != null) {
			for (int i = 0; i < meanVectorSaliency.length; i++) {
				features += meanVectorSaliency[i];
				features += ", ";
			}

			for (int i = 0; i < rangeVectorSaliency.length; i++) {
				features += rangeVectorSaliency[i];
				features += ", ";
			}
		}

		if (ftLbp != null) {
			for (int i = 0; i < lbp.length; i++) {
				features += lbp[i];
				features += ", ";
			}
		}

		if (ftLbpSaliency != null) {
			for (int i = 0; i < lbpSaliency.length; i++) {
				features += lbpSaliency[i];
				features += ", ";
			}
		}
		
		//CLASIFICACIÓN CLASE NOMINAL (TRUE, FALSE)
		//features += defect;
		
		//REGRESIÓN (CLASE 1,0)
		if(defect){
			features += "1";
		}
		else{
			features += "0";
		}

		return features;
	}
	
	

}
