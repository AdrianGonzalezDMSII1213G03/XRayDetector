package modelo;

import ij.ImagePlus;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JProgressBar;

import datos.GestorArff;

import utils.MyLogHandler;
import utils.Propiedades;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public abstract class VentanaAbstracta extends Thread{
	private int altura;
	private int anchura;
	private int getNumHilo;
	private ImagePlus img;
	private ImagePlus saliency;
	private ImagePlus imgCompleta;
	private ImagePlus imgConvolucion;
	private ImagePlus imgConvolucionSaliency;
	protected String grades[] = { "0 degrees", "90 degrees", "180 degrees",
		"270 degrees" };
	protected double[] meanVector;
	protected double[] rangeVector;
	protected double[] meanVectorSaliency;
	protected double[] rangeVectorSaliency;
	protected Feature ftStandard;
	protected Feature ftHaralick;
	protected Feature ftLbp;
	protected Feature ftStandardSaliency;
	protected Feature ftHaralickSaliency;
	protected Feature ftLbpSaliency;
	protected double[] lbp;
	protected double[] lbpSaliency;
	protected double means[];
	protected double ranges[];
	protected double meansSaliency[];
	protected double rangesSaliency[];
	protected double vector0[] = null;
	protected double vector90[] = null;
	protected double vector180[] = null;
	protected double vector270[] = null;
	protected double vector0sal[] = null;
	protected double vector90sal[] = null;
	protected double vector180sal[] = null;
	protected double vector270sal[] = null;
	protected boolean initializedNormal = false;
	protected JProgressBar progressBar;
	protected int[][] defectMatrix;
	private static Propiedades prop;
	
	public VentanaAbstracta(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo) {
		this.img = img;
		this.getNumHilo = numHilo;
		this.saliency = saliency;
		this.imgConvolucion = convolucion;
		this.imgConvolucionSaliency = convolucionSaliency;
		prop = Propiedades.getInstance();
		altura = anchura = prop.getTamVentana();
	}
	
	public abstract void run();
	
	public int getAlturaVentana(){
		return altura;
	}
	
	public int getAnchuraVentana(){
		return anchura;
	}
	
	public int getNumHilo(){
		return getNumHilo;
	}
	
	public void setAltura(int alt){
		altura = alt;
	}
	
	public void setAnchura(int anch){
		anchura = anch;
	}
	
	public void setNumHilo(int n){
		getNumHilo = n;
	}
	
	public ImagePlus getImage(){
		return img;
	}
	
	public void setImage(ImagePlus im){
		img = im;
	}
	
	public void setImagenCompleta(ImagePlus im){
		imgCompleta = im;
	}
	
	public ImagePlus getImagenCompleta(){
		return imgCompleta;
	}
	
	public void setSaliency(ImagePlus im){
		saliency = im;
	}
	
	public ImagePlus getSaliency(){
		return saliency;
	}
	
	public void setConvolucion(ImagePlus im){
		imgConvolucion = im;
	}
	
	public ImagePlus getConvolucion(){
		return imgConvolucion;
	}
	
	public void setConvolucionSaliency(ImagePlus im){
		imgConvolucionSaliency = im;
	}
	
	public ImagePlus getConvolucionSaliency(){
		return imgConvolucionSaliency;
	}
	
	public Propiedades getPropiedades(){
		return prop;
	}

	public void calcularHaralickSaliency(int coordenadaX, int coordenadaY,
			int step, int w) {
		ftHaralickSaliency = new Haralick(getSaliency(), grades[w], step);
		ftHaralickSaliency.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftHaralickSaliency.calcular();
	}

	public void calcularHaralick(int coordenadaX, int coordenadaY, int step, int w, ImagePlus imagen) {
		ftHaralick = new Haralick(imagen, grades[w], step);
		ftHaralick.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftHaralick.calcular();
	}

	public void calcularLbpSaliency(int coordenadaX, int coordenadaY) {
		ftLbpSaliency = new Lbp(getSaliency());
		ftLbpSaliency.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftLbpSaliency.calcular();
		lbpSaliency = ftLbpSaliency.getVectorResultados();
	}

	public void calcularLbp(int coordenadaX, int coordenadaY, ImagePlus imagen) {
		ftLbp = new Lbp(imagen);
		ftLbp.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftLbp.calcular();
		lbp = ftLbp.getVectorResultados();
	}

	public void calcularStandardSaliency(int coordenadaX, int coordenadaY) {
		ImagePlus copiaStandardSaliency = getConvolucionSaliency().duplicate();
		ftStandardSaliency = new Standard(getSaliency());
		ftStandardSaliency.setImagenConvolucion(copiaStandardSaliency);
		ftStandardSaliency.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftStandardSaliency.calcular();
	}

	public void calcularStandard(int coordenadaX, int coordenadaY, ImagePlus imagen) {
		ImagePlus copiaStandard = getConvolucion().duplicate();
		ftStandard = new Standard(imagen);
		ftStandard.setImagenConvolucion(copiaStandard);
		ftStandard.getImagen().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftStandard.calcular();
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
	public double[] calculateMean(double[] vector0, double[] vector90, double[] vector180,
			double[] vector270) {
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
	public double[] calculateRange(double[] vector0, double[] vector90, double[] vector180,
			double[] vector270) {
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

	protected Classifier abrirModelo() {
		URL url = null;
		File model = new File(getPropiedades().getPathModel());
		try {
			url = model.toURI().toURL();
		} catch (MalformedURLException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		ObjectInputStream file = null;
		try {
			file = new ObjectInputStream(url.openStream());
		} catch (IOException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		Classifier classifier = null;
		try {
			classifier = (AbstractClassifier) file.readObject();
		} catch (IOException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		try {
			file.close();
		} catch (IOException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		return classifier;
	}

	protected Instance crearInstancia() {
		double newVals[] = new double[407];
		int count = 0;
		
		if (ftStandard != null) {
			for (int i = 0; i < ftStandard.getVectorResultados().length; i++) {
				newVals[count] = ftStandard.getVectorResultados()[i];
				count++;
			}
		}
	
		if (ftStandardSaliency != null) {
			for (int i = 0; i < ftStandardSaliency.getVectorResultados().length; i++) {
				newVals[count] = ftStandardSaliency.getVectorResultados()[i];
				count++;
			}
		}
	
		if (meanVector != null) {
			for (int i = 0; i < meanVector.length; i++) {
				newVals[count] = meanVector[i];
				count++;
			}
		}
	
		if (rangeVector != null) {
			for (int i = 0; i < rangeVector.length; i++) {
				newVals[count] = rangeVector[i];
				count++;
			}
		}
	
		if (meanVectorSaliency != null) {
			for (int i = 0; i < meanVectorSaliency.length; i++) {
				newVals[count] = meanVectorSaliency[i];
				count++;
			}
		}
	
		if (rangeVectorSaliency != null) {
			for (int i = 0; i < rangeVectorSaliency.length; i++) {
				newVals[count] = rangeVectorSaliency[i];
				count++;
			}
		}
	
		if (ftLbp != null) {
			for (int i = 0; i < ftLbp.getVectorResultados().length; i++) {
				newVals[count] = ftLbp.getVectorResultados()[i];
				count++;
			}
		}
	
		if (ftLbpSaliency != null) {
			for (int i = 0; i < ftLbpSaliency.getVectorResultados().length; i++) {
				newVals[count] = ftLbpSaliency.getVectorResultados()[i];
				count++;
			}
		}
		// newVals es el vector de doubles donde tienes los datos de las medias
		// etc.
		Instance instance = new DenseInstance(1, newVals);
		instance.setDataset(getHeader());
		
//		synchronized(this){
//			System.out.print("Hilo " + getNumHilo() + ": ");
//			for(int i=0; i<instance.numAttributes();i++){
//				System.out.print(instance.value(i) + ", ");
//			}
//			System.out.println("");
//		}
		
//		synchronized(this){
//		System.out.println("Hilo " + getNumHilo() + ":");
//		System.out.println("std: " + ftStandard.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftStandard.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("std sal: " + ftStandardSaliency.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftStandardSaliency.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("lbp: " + ftLbp.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftLbp.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("lbp sal: " + ftLbpSaliency.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftLbpSaliency.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("har: " + ftHaralick.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftHaralick.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("har sal: " + ftHaralickSaliency.getImagen().getProcessor().getRoi().getBounds().x + ", " + ftHaralickSaliency.getImagen().getProcessor().getRoi().getBounds().y);
//		System.out.println("---------------------------------------------------");
//		}
		return instance;
	}

	/**
	 * This method gets the header of the features.
	 * @return header with features features header
	 */
	public Instances getHeader() {
		int capacity = 100000;
	
		//List<String> featuresCopy = null;
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		ArrayList<String> defect = new ArrayList<String>();
	
		defect.add("true");
		defect.add("false");
	
		/*if (features != null) {
			featuresCopy = new ArrayList<String>(features);
	
			for (int i = 0; i < featuresCopy.size(); i++) {
				String rest = featuresCopy.get(i).substring(1);
				char first = featuresCopy.get(i).charAt(0);
				first = Character.toLowerCase(first);
				featuresCopy.set(i, (first + rest).replaceAll(" ", ""));
				featuresCopy.set(i, featuresCopy.get(i).replace("lbp", "LBP"));
			}
		}*/
	
		for (int j = 0; j < ftStandard.getHead().length; j++) {
			//if (features == null
				//	|| featuresCopy.contains(ftStandard.getHead()[j]))
				atts.add(new Attribute(ftStandard.getHead()[j]));
		}
	
		for (int j = 0; j < ftStandardSaliency.getHead().length; j++) {
			//if (features == null
					//|| featuresCopy.contains(ftStandard.getHead()[j] + "(S)"))
				atts.add(new Attribute(ftStandardSaliency.getHead()[j] + "(S)"));
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralick.getHead().length; i++) {
				//if (features == null
					//	|| featuresCopy.contains(ftHaralick.getHead()[i]))
					atts.add(new Attribute(ftHaralick.getHead()[i] + "_mean" + j));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralick.getHead().length; i++) {
				//if (features == null
					//	|| featuresCopy.contains(ftHaralick.getHead()[i]))
					atts.add(new Attribute(ftHaralick.getHead()[i] + "_range" + j));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
				//if (features == null
					//	|| featuresCopy.contains(ftHaralick.getHead()[i] + "(S)"))
					atts.add(new Attribute(ftHaralickSaliency.getHead()[i] + "_mean" + j
							+ "(S)"));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
				//if (features == null
					//	|| featuresCopy.contains(ftHaralick.getHead()[i] + "(S)"))
					atts.add(new Attribute(ftHaralickSaliency.getHead()[i] + "_range" + j
							+ "(S)"));
			}
		}
	
		for (int j = 1; j < 60; j++) {
			//if (features == null
				//	|| featuresCopy.contains(ftLbp.getHead() + "_" + j))
				atts.add(new Attribute(ftLbp.getHead() + "(" + j + ")"));
		}
	
		for (int j = 1; j < 60; j++) {
			//if (features == null
				//	|| featuresCopy.contains(lbp.getHead() + "_" + j + "(S)"))
				atts.add(new Attribute(ftLbpSaliency.getHead() + "(" + j + ")(S)"));
		}
	
		atts.add(new Attribute("Defecto", defect));
	
		// Capacidad es el nÃºmero de instancias.
		Instances header = new Instances("NuevaInstancia", atts, capacity);
		// Establecer la clase
		header.setClassIndex(header.numAttributes() - 1);
	
		return header;
	}

	protected synchronized void setPorcentajeBarra() {		
		progressBar.setValue(progressBar.getValue() + 1);
		progressBar.repaint();		
	}

	public synchronized void rellenarMatrizDefectos(int coordX, int coordY) {
		for (int i = coordY; i < coordY + getAlturaVentana(); i++) {
			for (int j = coordX; j < coordX + getAnchuraVentana(); j++) {
				defectMatrix[j][i]++;
			}
		}
	}

	public void ejecutarCalculos(int coordenadaX, int coordenadaY, ImagePlus imagen) {		
		calcularStandard(coordenadaX, coordenadaY, imagen);				
		calcularStandardSaliency(coordenadaX, coordenadaY);				
		calcularLbp(coordenadaX, coordenadaY, imagen);				
		calcularLbpSaliency(coordenadaX, coordenadaY);
					
		int total = 0;
		for (int step = 1; step < 6; step++) {
			for (int w = 0; w < 4; w++) {
			
				calcularHaralick(coordenadaX, coordenadaY, step, w, imagen);						
				calcularHaralickSaliency(coordenadaX, coordenadaY, step, w);
				
				switch (w) {
				case 0:
					vector0 = ftHaralick.getVectorResultados();
					vector0sal = ftHaralickSaliency.getVectorResultados();
					break;
				case 1:
					vector90 = ftHaralick.getVectorResultados();
					vector90sal = ftHaralickSaliency.getVectorResultados();
					break;
				case 2:
					vector180 = ftHaralick.getVectorResultados();
					vector180sal = ftHaralickSaliency.getVectorResultados();
					break;
				case 3:
					vector270 = ftHaralick.getVectorResultados();
					vector270sal = ftHaralickSaliency.getVectorResultados();
					break;
				}
	
			}
	
			means = calculateMean(vector0, vector90, vector180, vector270);
			ranges = calculateRange(vector0, vector90, vector180, vector270);
			
			meansSaliency = calculateMean(vector0sal, vector90sal, vector180sal, vector270sal);
			rangesSaliency = calculateRange(vector0sal, vector90sal, vector180sal, vector270sal);
	
			if (initializedNormal == false) {
				meanVector = new double[ftHaralick.getVectorResultados().length * 5];
				rangeVector = new double[ftHaralick.getVectorResultados().length * 5];
				meanVectorSaliency = new double[ftHaralickSaliency.getVectorResultados().length * 5];
				rangeVectorSaliency = new double[ftHaralickSaliency.getVectorResultados().length * 5];
				initializedNormal = true;
			}
			for (int k = 0; k < means.length; k++) {
				// Sale un vector que contiene los 5 steps de medias
				meanVector[total] = means[k];
				rangeVector[total] = ranges[k];
				meanVectorSaliency[total] = meansSaliency[k];
				rangeVectorSaliency[total] = rangesSaliency[k];
				total++;
			}
		}
	}

	public double clasificar() {
		Instance instancia = crearInstancia();
		Classifier clas = abrirModelo();
		double clase = 0;
		try {
			clase = clas.classifyInstance(instancia);
		} catch (Exception e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		return clase;
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
	 * This method checks in the mask if the region of interest analyzed has a
	 * colored defect. It returns true if the region has defects. If there is no
	 * defects, it returns false.
	 * 
	 * @param img
	 *            mask with colored defects
	 * @return true if it has defects, false if not
	 */
	protected boolean getDefecto(ImagePlus img) {
	
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
	
}
