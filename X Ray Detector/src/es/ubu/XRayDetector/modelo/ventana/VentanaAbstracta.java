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
 * VentanaAbstracta.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.ventana;

import ij.ImagePlus;
import ij.gui.Roi;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import es.ubu.XRayDetector.datos.GestorArff;
import es.ubu.XRayDetector.modelo.feature.Feature;
import es.ubu.XRayDetector.modelo.feature.Haralick;
import es.ubu.XRayDetector.modelo.feature.Lbp;
import es.ubu.XRayDetector.modelo.feature.Standard;
import es.ubu.XRayDetector.utils.Differentials_;
import es.ubu.XRayDetector.utils.Propiedades;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Superclass of the <i>Strategy</i> pattern that controls the types of windows.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * 
 * @see Thread
 * @version 1.0
 */
public abstract class VentanaAbstracta extends Thread{
	
	/**
	 * Height of the window.
	 */
	private int altura;
	
	/**
	 * Width of the window.
	 */
	private int anchura;
	
	/**
	 * Number of thread.
	 */
	private int getNumHilo;
	
	/**
	 * Original image.
	 */
	private ImagePlus img;
	
	/**
	 * Saliency map image.
	 */
	private ImagePlus saliency;
	
	/**
	 * Complete image.
	 */
	private ImagePlus imgCompleta;
	
	/**
	 * Image with an additional margin to perform the convloution operation.
	 */
	private ImagePlus imgConvolucion;
	
	/**
	 * Saliency map image with an additional margin to perform the convloution operation.
	 */
	private ImagePlus imgConvolucionSaliency;
	
	/**
	 * Strings containing the names of the grades (Haralick).
	 */
	protected String grades[] = { "0 degrees", "90 degrees", "180 degrees",
		"270 degrees" };
	
	/**
	 * Array with the values of the means (Haralick).
	 */
	protected double[] meanVector;
	
	/**
	 * Array with the values of the ranges (Haralick).
	 */
	protected double[] rangeVector;
	
	/**
	 * Array with the values of the means (Saliency, Haralick).
	 */
	protected double[] meanVectorSaliency;
	
	/**
	 * Array with the values of the ranges (Saliency, Haralick).
	 */
	protected double[] rangeVectorSaliency;
	
	/**
	 * Standard features.
	 */
	protected Feature ftStandard;
	
	/**
	 * Haralick features.
	 */
	protected Feature ftHaralick;
	
	/**
	 * LBP features.
	 */
	protected Feature ftLbp;
	
	/**
	 * Standard features, saliency map.
	 */
	protected Feature ftStandardSaliency;
	
	/**
	 * Haralick features, saliency.
	 */
	protected Feature ftHaralickSaliency;
	
	/**
	 * LBP features, saliency.
	 */
	protected Feature ftLbpSaliency;
	
	/**
	 * LBP values.
	 */
	protected double[] lbp;
	
	/**
	 * LBP values, saliency.
	 */
	protected double[] lbpSaliency;
	
	/**
	 * Means values.
	 */
	protected double means[];
	
	/**
	 * Ranges values.
	 */
	protected double ranges[];
	
	/**
	 * Means values, saliency.
	 */
	protected double meansSaliency[];
	
	/**
	 * Ranges values, saliency.
	 */
	protected double rangesSaliency[];
	
	/**
	 * Values for 0 degrees.
	 */
	protected double vector0[] = null;
	
	/**
	 * Values for 90 degrees.
	 */
	protected double vector90[] = null;
	
	/**
	 * Values for 180 degrees.
	 */
	protected double vector180[] = null;
	
	/**
	 * Values for 170 degrees.
	 */
	protected double vector270[] = null;
	
	/**
	 * Values for 0 degrees, saliency.
	 */
	protected double vector0sal[] = null;

	/**
	 * Values for 90 degrees, saliency.
	 */
	protected double vector90sal[] = null;

	/**
	 * Values for 180 degrees, saliency.
	 */
	protected double vector180sal[] = null;

	/**
	 * Values for 270 degrees, saliency.
	 */
	protected double vector270sal[] = null;
	
	/**
	 * Flag that indicates if the process has been initialized as normal.
	 */
	protected boolean initializedNormal = false;
	
	/**
	 * ProgressBar where the process progress is going to be written.
	 */
	protected JProgressBar progressBar;
	
	/**
	 * Matrix where the defects are going to be stored.
	 */
	protected int[][] defectMatrix;
	
	/**
	 * Application properties.
	 */
	private static Propiedades prop;
	
	/**
	 * Class constructor.
	 * @param img Original image
	 * @param saliency Saliency map image
	 * @param convolucion Image to perform the convolution process
	 * @param convolucionSaliency salicny image to perform the convolution process
	 * @param numHilo Number of this thread
	 */
	public VentanaAbstracta(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo) {
		this.img = img;
		this.getNumHilo = numHilo;
		this.saliency = saliency;
		this.imgConvolucion = convolucion;
		this.imgConvolucionSaliency = convolucionSaliency;
		prop = Propiedades.getInstance();
		altura = anchura = prop.getTamVentana();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public abstract void run();
	
	/**
	 * Method that retrieves the height of the window.
	 * @return Height of the window
	 */
	public int getAlturaVentana(){
		return altura;
	}
	
	/**
	 * Method that retrieves the width of the window.
	 * @return Width of the window
	 */
	public int getAnchuraVentana(){
		return anchura;
	}
	
	/**
	 * Method that retrieves the number of this thread.
	 * @return Number of thread
	 * @see #setNumHilo
	 */
	public int getNumHilo(){
		return getNumHilo;
	}
	
	/**
	 * Method that sets the height of the window.
	 * @param alt Height of the window
	 */
	public void setAltura(int alt){
		altura = alt;
	}
	
	/**
	 * Method that sets the width of the window.
	 * @param anch Width of the window
	 */
	public void setAnchura(int anch){
		anchura = anch;
	}
	
	/**
	 * Method that sets the number of this thread.
	 * @param n Number of thread
	 * @see #getNumHilo
	 */
	public void setNumHilo(int n){
		getNumHilo = n;
	}
	
	/**
	 * Method that retrieves the image.
	 * @return Image
	 * @see #setImage
	 */
	public ImagePlus getImage(){
		return img;
	}
	
	/**
	 * Method that sets the image.
	 * @param im Image
	 * @see #getImage
	 */
	public void setImage(ImagePlus im){
		img = im;
	}
	
	/**
	 * Method that sets the complete image.
	 * @param im Complete image
	 * @see #getImagenCompleta
	 */
	public void setImagenCompleta(ImagePlus im){
		imgCompleta = im;
	}
	
	/**
	 * Method that retrieves the complete image.
	 * @return Complete image
	 * @see #setImagenCompleta
	 */
	public ImagePlus getImagenCompleta(){
		return imgCompleta;
	}
	
	/**
	 * Method that sets the saliency map image.
	 * @param im Saliency map image
	 * @see #getSaliency
	 */
	public void setSaliency(ImagePlus im){
		saliency = im;
	}
	
	/**
	 * Method that retrieves the saliency map image.
	 * @return Saliency map image
	 * @see #setSaliency
	 */
	public ImagePlus getSaliency(){
		return saliency;
	}
	
	/**
	 * Method that sets the image to perform the convolution process.
	 * @param im Image
	 * @see #getConvolucion
	 */
	public void setConvolucion(ImagePlus im){
		imgConvolucion = im;
	}
	
	/**
	 * Method that retrieves the image to perform the convolution process.
	 * @return Image
	 * @see #setConvolucion
	 */
	public ImagePlus getConvolucion(){
		return imgConvolucion;
	}
	
	/**
	 * Method that sets the saliency map image to perform the convolution process.
	 * @param im Saliency map image
	 * @see #getConvolucionSaliency
	 */
	public void setConvolucionSaliency(ImagePlus im){
		imgConvolucionSaliency = im;
	}
	
	/**
	 * Method that retrieves the saliency map image to perform the convolution process.
	 * @return Saliency map image
	 * @see #setConvolucionSaliency
	 */
	public ImagePlus getConvolucionSaliency(){
		return imgConvolucionSaliency;
	}
	
	/**
	 * Method that retrieves the properties instance.
	 * @return Properties instance
	 */
	public Propiedades getPropiedades(){
		return prop;
	}

	/**
	 * Calculation of all the Haralick features, saliency map.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 * @param step Selected step
	 * @param w Step
	 */
	public void calcularHaralickSaliency(int coordenadaX, int coordenadaY,
			int step, int w) {
		ftHaralickSaliency = new Haralick(grades[w], step);
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftHaralickSaliency.calcular(roi, getSaliency(), null, null);
	}

	/**
	 * Calculation of all the Haralick features.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 * @param step Selected step
	 * @param w step
	 * @param imagen Image
	 */
	public void calcularHaralick(int coordenadaX, int coordenadaY, int step, int w, ImagePlus imagen) {
		ftHaralick = new Haralick(grades[w], step);
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftHaralick.calcular(roi, imagen, null, null);
	}

	/**
	 * Calculation of all LBP features, saliency map.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 */
	public void calcularLbpSaliency(int coordenadaX, int coordenadaY) {
		ftLbpSaliency = new Lbp();
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftLbpSaliency.calcular(roi, getSaliency(), null, null);
		lbpSaliency = ftLbpSaliency.getVectorResultados();
	}

	/**
	 * Calculation of all LBP features.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 * @param imagen Image
	 */
	public void calcularLbp(int coordenadaX, int coordenadaY, ImagePlus imagen) {
		ftLbp = new Lbp();
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftLbp.calcular(roi, imagen, null, null);
		lbp = ftLbp.getVectorResultados();
	}

	/**
	 * Calculation of all Standard features, saliency map.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 */
	public void calcularStandardSaliency(int coordenadaX, int coordenadaY) {
		ImagePlus copiaStandardSaliency = getConvolucionSaliency().duplicate();
		ftStandardSaliency = new Standard();
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftStandardSaliency.calcular(roi, getSaliency(), getImageFd(copiaStandardSaliency), getImageSd(copiaStandardSaliency));
	}

	/**
	 * Calculation of all Standard features.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 * @param imagen Image
	 */
	public void calcularStandard(int coordenadaX, int coordenadaY, ImagePlus imagen) {
		ImagePlus copiaStandard = getConvolucion().duplicate();
		ftStandard = new Standard();
		Roi roi = new Roi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
		ftStandard.calcular(roi, imagen, getImageFd(copiaStandard), getImageSd(copiaStandard));
	}
	
	/**
	 * Calculation of the first derivative filter.
	 * @param image Image
	 * @return Filtered image
	 */
	public ImagePlus getImageFd(ImagePlus image){
		//NUEVA VERSIÓN: USANDO DIFFERENTIALS_
		Differentials_ diff = new Differentials_();
        diff.setImp(image.duplicate());
        Differentials_.setOperation(Differentials_.GRADIENT_MAGNITUDE);
        diff.run("");
        return diff.getImp();
	}
	
	/**
	 * Calculation of the second derivative.
	 * @param image Image
	 * @return Filtered image
	 */
	public ImagePlus getImageSd(ImagePlus image){
		//NUEVA VERSIÓN: USANDO DIFFERENTIALS_
		Differentials_ diff = new Differentials_();
        diff.setImp(image.duplicate());
        Differentials_.setOperation(Differentials_.LAPLACIAN);
        diff.run("");
        return diff.getImp();
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

	/**
	 * Method that opens a model.
	 * @return Classifier contained in a .model file
	 */
	protected Classifier abrirModelo() {
		URL url = null;
		File model = new File(getPropiedades().getPathModel());
		try {
			url = model.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		ObjectInputStream file = null;
		try {
			file = new ObjectInputStream(url.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Classifier classifier = null;
		try {
			classifier = (AbstractClassifier) file.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			file.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return classifier;
	}

	/**
	 * Method that creates an instance which can be classified by Weka.
	 * @return Instance with the features values
	 */
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
		// newVals es el vector de doubles donde tienes los datos de las medias etc.
		Instance instance = new DenseInstance(1, newVals);
		List<String> feat;
		if(prop.getTipoCaracteristicas() == 0){ //todas
			feat = null;
		}
		else{	//mejores
			feat = obtainFeatures();
		}
		instance.setDataset(getHeader(feat));
		return instance;
	}

	/**
	 * This method gets the headers of the features.
	 * @param features  a List of features
	 * @return header with features headers
	 */
	public Instances getHeader(List<String> features) {
		int capacity = 100000;
	
		List<String> featuresCopy = null;
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		ArrayList<String> defect = new ArrayList<String>();
	
		defect.add("true");
		defect.add("false");
	
		if (features != null) {
			featuresCopy = new ArrayList<String>(features);
	
			for (int i = 0; i < featuresCopy.size(); i++) {
				String rest = featuresCopy.get(i).substring(1);
				char first = featuresCopy.get(i).charAt(0);
				first = Character.toLowerCase(first);
				featuresCopy.set(i, (first + rest).replaceAll(" ", ""));
			}
		}
	
		for (int j = 0; j < ftStandard.getHead().length; j++) {
			if (features == null
					|| featuresCopy.contains(ftStandard.getHead()[j]))
				atts.add(new Attribute(ftStandard.getHead()[j]));
		}
	
		for (int j = 0; j < ftStandardSaliency.getHead().length; j++) {
			if (features == null
					|| featuresCopy.contains(ftStandard.getHead()[j] + "(S)"))
				atts.add(new Attribute(ftStandardSaliency.getHead()[j] + "(S)"));
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralick.getHead().length; i++) {
				if (features == null
						|| featuresCopy.contains(ftHaralick.getHead()[i]))
					atts.add(new Attribute(ftHaralick.getHead()[i] + "_mean" + j));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralick.getHead().length; i++) {
				if (features == null
						|| featuresCopy.contains(ftHaralick.getHead()[i]))
					atts.add(new Attribute(ftHaralick.getHead()[i] + "_range" + j));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
				if (features == null
						|| featuresCopy.contains(ftHaralick.getHead()[i] + "(S)"))
					atts.add(new Attribute(ftHaralickSaliency.getHead()[i] + "_mean" + j
							+ "(S)"));
			}
		}
	
		for (int j = 1; j < 6; j++) {
			for (int i = 0; i < ftHaralickSaliency.getHead().length; i++) {
				if (features == null
						|| featuresCopy.contains(ftHaralick.getHead()[i] + "(S)"))
					atts.add(new Attribute(ftHaralickSaliency.getHead()[i] + "_range" + j
							+ "(S)"));
			}
		}
	
		for (int j = 1; j < 60; j++) {
			if (features == null
					|| featuresCopy.contains(ftLbp.getHead() + "_" + j))
				atts.add(new Attribute(ftLbp.getHead() + "(" + j + ")"));
		}
	
		for (int j = 1; j < 60; j++) {
			if (features == null
					|| featuresCopy.contains(ftLbpSaliency.getHead() + "_" + j + "(S)"))
				atts.add(new Attribute(ftLbpSaliency.getHead() + "(" + j + ")(S)"));
		}
	
		atts.add(new Attribute("Defecto", defect));
	
		// Capacidad es el nÃºmero de instancias.
		Instances header = new Instances("NuevaInstancia", atts, capacity);
		// Establecer la clase
		header.setClassIndex(header.numAttributes() - 1);
	
		return header;
	}
	
	/**
	 * Creates a list of best features.
	 * 
	 * @return list with the features
	 */
	public List<String> obtainFeatures() {
		List<String> features = new ArrayList<String>();
		String[] bestFeatures = null;
		
		int tam = prop.getTamVentana();
		switch(tam){
		case 12:
			if(prop.getTipoVentanaDefectuosa() == 0){	//ventana
				bestFeatures = new String[]{"mean", "firstDerivative", "secondDerivative(S)", "differenceVariance_mean1",
						"imc_1_mean1", "correlation_mean2", "differenceVariance_mean2", "imc_1_mean4",
						"correlation_mean5", "correlation_range1", "inverseDifferenceMoment_range1",
						"sumVariance_range1", "contrast_range2", "inverseDifferenceMoment_range2",
						"inverseDifferenceMoment_range3", "inverseDifferenceMoment_range4", "imc_1_range4",
						"contrast_range5", "sumOfSquares_range5", "inverseDifferenceMoment_range5",
						"differenceEntropy_mean1(S)", "sumEntropy_mean2(S)", "differenceEntropy_mean2(S)",
						"differenceEntropy_mean3(S)", "LBP(7)", "LBP(30)", "LBP(32)", "LBP(38)", "LBP(31)(S)", "LBP(41)(S)"};
			}
			else{	//vecinos
				bestFeatures = new String[]{"standardDeviation", "differenceVariance_mean1", "correlation_mean2", "sumEntropy_mean2",
						"correlation_mean4", "inverseDifferenceMoment_mean4", "correlation_mean5", "sumOfSquares_mean5", "correlation_range1",
						"inverseDifferenceMoment_range2", "inverseDifferenceMoment_range3", "angularSecondMoment_range4", "sumOfSquares_range4",
						"differenceVariance_range4", "sumOfSquares_range5", "inverseDifferenceMoment_range5", "differenceEntropy_range5",
						"imc_2_range5", "correlation_mean2(S)", "differenceEntropy_mean2(S)", "inverseDifferenceMoment_mean5(S)",
						"LBP(37)", "LBP(26)(S)", "LBP(27)(S)", "LBP(40)(S)", "LBP(44)(S)", "LBP(47)(S)", "LBP(52)(S)", "LBP(55)(S)"};
			}
			break;
			
		case 16:
			if(prop.getTipoVentanaDefectuosa() == 0){	//ventana
				bestFeatures = new String[]{"firstDerivative", "secondDerivative", "differenceVariance_mean1", "imc_1_mean1", "correlation_mean2",
						"differenceEntropy_mean2", "imc_1_mean2", "sumAverage_mean5", "inverseDifferenceMoment_range1", "inverseDifferenceMoment_range2",
						"inverseDifferenceMoment_range4", "sumVariance_range4", "differenceEntropy_mean1(S)", "inverseDifferenceMoment_mean5(S)",
						"differenceEntropy_range1(S)", "differenceEntropy_range4(S)", "LBP(4)", "LBP(6)", "LBP(19)", "LBP(30)", "LBP(32)",
						"LBP(44)", "LBP(15)(S)", "LBP(34)(S)"};
			}
			else{	//vecinos
				bestFeatures = new String[]{"standardDeviation", "imc_1_mean1", "sumAverage_mean2", "imc_2_mean3", "differenceVariance_mean4", "imc_2_mean4",
						"angularSecondMoment_range1", "correlation_range1", "inverseDifferenceMoment_range1", "inverseDifferenceMoment_range2", "inverseDifferenceMoment_range3",
						"sumAverage_range3", "sumOfSquares_range4", "inverseDifferenceMoment_range4", "differenceVariance_range4", "sumOfSquares_range5", "inverseDifferenceMoment_range5",
						"differenceVariance_range5", "correlation_mean2(S)", "differenceEntropy_mean3(S)", "differenceEntropy_mean4(S)", "angularSecondMoment_mean5(S)",
						"inverseDifferenceMoment_range4(S)", "LBP(7)", "LBP(48)", "LBP(20)(S)", "LBP(26)(S)", "LBP(27)(S)", "LBP(40)(S)", "LBP(47)(S)", "LBP(52)(S)"};
			}
			break;
			
		case 24:
			if(prop.getTipoVentanaDefectuosa() == 0){	//ventana
				bestFeatures = new String[]{"firstDerivative", "secondDerivative", "firstDerivative(S)", "secondDerivative(S)", "sumOfSquares_mean1", "differenceVariance_mean1",
						"inverseDifferenceMoment_mean3", "sumAverage_mean3", "inverseDifferenceMoment_mean4", "contrast_range1", "inverseDifferenceMoment_range1", "inverseDifferenceMoment_range2",
						"inverseDifferenceMoment_range3", "imc_1_range4", "inverseDifferenceMoment_range5", "sumVariance_range5", "angularSecondMoment_mean5(S)", "differenceEntropy_range1(S)",
						"LBP(4)", "LBP(7)", "LBP(14)", "LBP(17)", "LBP(18)", "LBP(18)", "LBP(19)", "LBP(20)", "LBP(21)", "LBP(26)", "LBP(32)", "LBP(38)", "LBP(39)", "LBP(44)", "LBP(6)(S)", "LBP(14)(S)",
						"LBP(17)(S)", "LBP(23)(S)", "LBP(39)(S)", "LBP(40)(S)"};
			}
			else{	//vecinos
				bestFeatures = new String[]{"firstDerivative", "correlation_mean1", "imc_1_mean3", "differenceVariance_mean4", "imc_1_mean4", "sumOfSquares_mean5", "inverseDifferenceMoment_range1",
						"differenceVariance_range1", "inverseDifferenceMoment_range2", "inverseDifferenceMoment_range3", "sumOfSquares_range4", "inverseDifferenceMoment_range4", "angularSecondMoment_range5",
						"inverseDifferenceMoment_range5", "sumAverage_range5", "differenceEntropy_mean2(S)", "differenceEntropy_mean3(S)", "angularSecondMoment_mean4(S)", "angularSecondMoment_range5(S)",
						"LBP(7)", "LBP(10)", "LBP(38)", "LBP(43)", "LBP(20)(S)", "LBP(21)(S)", "LBP(27)(S)", "LBP(29)(S)", "LBP(40)(S)", "LBP(52)(S)", "LBP(59)(S)"};
			}
			break;
			
		case 32:
			if(prop.getTipoVentanaDefectuosa() == 0){	//ventana
				bestFeatures = new String[]{"firstDerivative", "secondDerivative", "firstDerivative(S)", "secondDerivative(S)", "inverseDifferenceMoment_mean1", "inverseDifferenceMoment_range1",
						"inverseDifferenceMoment_range4", "imc_1_range4", "sumOfSquares_range5", "LBP(17)", "LBP(18)", "LBP(20)", "LBP(30)", "LBP(34)", "LBP(38)", "LBP(39)", "LBP(8)(S)",
						"LBP(17)(S)", "LBP(40)(S)"};
			}
			else{	//vecinos
				bestFeatures = new String[]{"secondDerivative", "secondDerivative(S)", "sumOfSquares_mean1", "inverseDifferenceMoment_mean1", "differenceVariance_mean1", "correlation_mean2",
						"differenceVariance_mean2", "differenceVariance_mean4", "inverseDifferenceMoment_range1", "sumAverage_range1", "imc_1_range1", "inverseDifferenceMoment_range2",
						"inverseDifferenceMoment_range3", "inverseDifferenceMoment_range4", "imc_1_range4", "inverseDifferenceMoment_range5", "entropy_mean1(S)", "differenceEntropy_mean1(S)",
						"correlation_mean4(S)", "inverseDifferenceMoment_mean5(S)", "angularSecondMoment_range1(S)", "angularSecondMoment_range2(S)", "angularSecondMoment_range5(S)",
						"inverseDifferenceMoment_range5(S)", "LBP(7)", "LBP(14)", "LBP(15)", "LBP(38)", "LBP(6)(S)", "LBP(13)(S)", "LBP(14)(S)", "LBP(20)(S)", "LBP(21)(S)", "LBP(34)(S)",
						"LBP(39)(S)", "LBP(40)(S)", "LBP(52)(S)", "LBP(55)(S)"};
			}
			break;
		}

		features = new ArrayList<String>();

		for (int i = 0; i < bestFeatures.length; i++) {
			features.add(bestFeatures[i]);
		}
		return features;
	}

	/**
	 * Methods that sets the percentage of the ProgressBar (synchoronized).
	 */
	protected synchronized void setPorcentajeBarra() {		
		progressBar.setValue(progressBar.getValue() + 1);
		progressBar.repaint();		
	}

	/**
	 * Method that increases the amount of windows that indicate that this
	 * pixel is faulty.
	 * @param coordX X coordinate of the pixel
	 * @param coordY Y coordinate of the pixel
	 */
	public synchronized void rellenarMatrizDefectos(int coordX, int coordY) {
		for (int i = coordY; i < coordY + getAlturaVentana(); i++) {
			for (int j = coordX; j < coordX + getAnchuraVentana(); j++) {
				defectMatrix[j][i]++;
			}
		}
	}

	/**
	 * Method that calculates all the features.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 * @param imagen Image
	 */
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

	/**
	 * Method that uses a classifier to classifiy an instance.
	 * @return Value of the classifier
	 */
	public double clasificar() {
		Instance instancia = crearInstancia();
		Classifier clas = abrirModelo();
		double clase = 0;
		try {
			clase = clas.classifyInstance(instancia);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return clase;
	}

	/**
	 * Method that creates an ARFF file and writes instances in it.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the 
	 * @param defect Value of the defect (class)
	 */
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
	 * @param coordinates  the coordinates of the window
	 * @param defect if the window has a defect or not
	 * @return string of features
	 */
	public String generateFeatures(int[] coordinates, boolean defect) {
		String features = new String();
		if (coordinates != null) {
			features += coordinates[0];
			features += ", ";
			features += coordinates[1];
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
		
		int opcionClasificacion = prop.getTipoClasificacion();
		
		switch(opcionClasificacion){
			case 0:
				//CLASIFICACIÓN CLASE NOMINAL (TRUE, FALSE)
				features += defect;
				break;
			case 1:
				//REGRESIÓN (CLASE 1,0)
				if(defect){
					features += "1";
				}
				else{
					features += "0";
				}
				break;
		}
		return features;
	}

	/**
	 * Method that generates a header for the file that will store the
	 * characteristics.
	 * @param coordenadas  if the coordinates have to be included
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
		
		int opcionClasificacion = prop.getTipoClasificacion();
		
		switch(opcionClasificacion){
			case 0:
				//CLASIFICACIÓN CON CLASE NOMINAL
				header += "@ATTRIBUTE class {true, false}\n";
				break;
			case 1:
				//REGRESIÓN (CLASE 1,0)
				header += "@ATTRIBUTE class numeric\n";
				break;
		}
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
	
		int heuristica = prop.getTipoVentanaDefectuosa();

		switch (heuristica) {
			case 0:
				return porcentajeVentanaMal(img);
			case 1:
				return porcentajeVecinosMal(img);
		}
		
		return false;
	}

	/**
	 * One of the heuristics that determines when a window is faulty.
	 * The window is faulty if a percentage of the pixels in the window
	 * are faulty.
	 * @param img Image
	 * @return True if this window is faulty, false if not
	 */
	public boolean porcentajeVecinosMal(ImagePlus img) {
		int[] defectVector;
		//Pixel Central + Vecinos Malo
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
					if(pixelDef > (prop.getPorcentajePixeles()*9)){	//porcentaje de la región del centro de la ventana
						return true;
					}				
				}
			}
		}
		return false;
	}

	/**
	 * One of the heuristics that determines when a window is faulty.
	 * The window is faulty when a percentage of a 3x3 region around
	 * the central pixel are faulty.
	 * @param img Image
	 * @return True if this window is faulty, false if not
	 */
	public boolean porcentajeVentanaMal(ImagePlus img) {
		int[] defectVector;
		// Porcentaje de Pixel mal
		int numPixVentana = getAlturaVentana() * getAnchuraVentana();
		int pixelDef = 0;
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				defectVector = img.getPixel(x, y);
				// El valor 0 del vector, guarda el valor en escala de grises.
				// Por eso no nos interesa.
				if ((defectVector[1] != 255 && defectVector[1] != 0)
						|| (defectVector[2] != 255 && defectVector[2] != 0)) {
					pixelDef++;
					if(pixelDef>(prop.getPorcentajePixeles()*numPixVentana)){
						return true;
					}					
				}
			}
		}
		return false;
	}
	
}
