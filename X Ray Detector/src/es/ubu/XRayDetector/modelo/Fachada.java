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
 * Fachada.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo;

/**
 * Class Fachada.
 * 
 * Class that implements the facade pattern.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.ImageProcessor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import es.ubu.XRayDetector.datos.GestorArff;
import es.ubu.XRayDetector.datos.ImageReader;
import es.ubu.XRayDetector.modelo.preprocesamiento.Preprocesamiento;
import es.ubu.XRayDetector.modelo.preprocesamiento.Saliency;
import es.ubu.XRayDetector.modelo.ventana.VentanaAbstracta;
import es.ubu.XRayDetector.modelo.ventana.VentanaAleatoria;
import es.ubu.XRayDetector.modelo.ventana.VentanaDeslizante;
import es.ubu.XRayDetector.modelo.ventana.VentanaRegiones;
import es.ubu.XRayDetector.utils.Auto_Local_Threshold;
import es.ubu.XRayDetector.utils.Graphic;
import es.ubu.XRayDetector.utils.ParticleAnalyzer;
import es.ubu.XRayDetector.utils.Propiedades;
import es.ubu.XRayDetector.utils.Thresholder;

/**
 * <b>Facade</b> pattern. Class that controls the logic of the application.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class Fachada {
	
	/**
	 * Defect matrix values.
	 */
	private int[][] defectMatrix;
	/**
	 * Facade instance.
	 */
	private static Fachada INSTANCE = null;
	/**
	 * Image reader.
	 */
	private ImageReader ir;
	/**
	 * Array of threads.
	 */
	private Thread[] t;
	/**
	 * Image to be processed.
	 */
	private ImagePlus imagen;
	/**
	 * Application properties.
	 */
	private static Propiedades prop;
	/**
	 * Array of coordinates.
	 */
	private ArrayList<int[]> listaCoordenadas;
	/**
	 * Array of Rois.
	 */
	private Roi[] arrayRois;
	/**
	 * Binaryzed image.
	 */
	private BufferedImage imgBin;
	/**
	 * Results table.
	 */
	private ResultsTable myRT;
	/**
	 * Table model to set the results table.
	 */
	private DefaultTableModel tableModel;
	/**
	 * A RunTimeException to  be handled.
	 */
	private RuntimeException excepcion = null;
	
	/**
	 * Constructor class.
	 */
	private Fachada() {
		ir = new ImageReader();
		prop = Propiedades.getInstance();
	}
	
	/**
	 * Gets the image.
	 * 
	 * @return The image.
	 * @see #setImagen
	 */
	public ImagePlus getImagen(){
		return imagen;
	}
	
	/**
	 * Sets an image.
	 * 
	 * @param img The image to be set.
	 * @see #getImagen
	 */
	public void setImagen(ImagePlus img){
		imagen = img;
	}
	
	/**
	 * Get the number of threads available.
	 * 
	 * @return The number of threads available.
	 */
	public int getNumThreads(){
		if(t == null){
			return 0;
		}
		return t.length;
	}
	
	/**
	 * Gets an instance of Fachada, using Facade pattern.
	 * 
	 * @return A fachada instance.
	 */
	public static Fachada getInstance(){	//Singleton
		if(INSTANCE == null){
			return new Fachada();
		}
		else{
			return INSTANCE;
		}
	}
	
	/**
	 * Stops a task running at the moment.
	 */
	@SuppressWarnings("deprecation")
	public void stop(){
		 for (int ithread = 0; ithread < t.length; ithread++){ 
             if(t[ithread].isAlive()){
            	 t[ithread].stop(); 
             }
		 }
	}

	/**
	 * Loads an image into the application.
	 * 
	 * @param img The image path.
	 * @return Image loading verification.
	 */
	public String cargaImagen(String img){		
		int i = ir.abrirImagen(img);
		setImagen(ir.getImagen());
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
	
	/**
	 * Splits an image.
	 *  
	 * @param selection Selection to split if exists. Else entire image is split.
	 * @return Array of split images
	 */
	public ImagePlus[] divideImagen(Rectangle selection){
		int processors = Runtime.getRuntime().availableProcessors();
		int offset = prop.getTamVentana()/2;
		ImagePlus[] imagenes = new ImagePlus[processors];
		ImagePlus img = getImagen();		
		
		if(selection.height != 0 && selection.width != 0){	//hay una selección
			ImageProcessor ip =	img.duplicate().getProcessor();
			ip.setRoi(selection);
			ip = ip.crop();
			BufferedImage croppedImage = ip.getBufferedImage();
			img = new ImagePlus("croppedImage", croppedImage);
		}
		
		int tam = img.getHeight()/processors;
		
		if(processors == 1){
			imagenes[0] = img;
			return imagenes;
		}
		else{		
			for(int i=0; i < processors; i++) {
				ImageProcessor ip =	img.duplicate().getProcessor();
				
				if(i == 0){	//caso de primera división
					ip.setRoi(0, (i*tam), img.getWidth(), tam + offset);
				}
				else if(i == processors-1){	//caso de la última división
					ip.setRoi(0, (i*tam) - offset, img.getWidth(), tam + offset);
				}
				else{	//caso de divisiones intermedias
					ip.setRoi(0, (i*tam) - offset, img.getWidth(), tam + (2*offset));
				}
	
				ip = ip.crop();
				BufferedImage croppedImage = ip.getBufferedImage();
				imagenes[i] = new ImagePlus("croppedImage" + i, croppedImage);
				ip.resetRoi();
			}
			return imagenes;
		}
	}
	
	/**
	 * Gets the saliency result.
	 * 
	 * @param imagenes Array of images to process with saliency.
	 * @return sailency result.
	 */
	public ImagePlus[] getSaliency(ImagePlus[] imagenes){
		ImagePlus[] saliency = new ImagePlus[imagenes.length];
		for(int i=0; i<imagenes.length; i++){
			Preprocesamiento p = new Saliency(imagenes[i], 1);
			saliency[i] = new ImagePlus("", p.calcular());
		}
		return saliency;
	}
	
	/**
	 * Executes a window.
	 * 
	 * @param selection Seleccition set over the image.
	 * @param imgPanel Image panel. Image Viewer.
	 * @param progressBar Progress Bar to show the progress.
	 */
	public void ejecutaVentana(Rectangle selection, Graphic imgPanel, JProgressBar progressBar){
		int processors = Runtime.getRuntime().availableProcessors();
		listaCoordenadas = calcularUmbralesLocales(selection);		
		
		defectMatrix = new int[getImagen().getWidth()][getImagen().getHeight()];
		
		ImagePlus[] imagenes = divideImagen(selection);
		ImagePlus[] saliency = getSaliency(imagenes);
		ImagePlus[] convolucion = getImgConvolucion(imagenes, selection, getImagen());
		
		//convolucion de saliency
		Preprocesamiento p = new Saliency(getImagen(), 1);
		ImagePlus imgSaliency = new ImagePlus("", p.calcular());
		ImagePlus[] convolucionSaliency = getImgConvolucion(saliency, selection, imgSaliency);
		
		t = new VentanaAbstracta[processors];
		
		setMaxProgressBar(imagenes, progressBar);
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
    	    public void uncaughtException(Thread th, Throwable ex) {
    	    	if(!ex.getClass().toString().contains("ThreadDeath")){
	    	    	excepcion = new RuntimeException(ex);
	    	    	stop();
    	    	}
    	    }
    	};
				
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new VentanaDeslizante(imagenes[ithread], saliency[ithread], convolucion[ithread], convolucionSaliency[ithread],
            		ithread, selection, imgPanel, progressBar, defectMatrix, false);
            t[ithread].setUncaughtExceptionHandler(h);
            t[ithread].start();
        }  
  
        try{     
            for (int ithread = 0; ithread < t.length; ++ithread)  
                t[ithread].join();  
        }
        catch (InterruptedException ie){  
             
        }
        drawEdge(imgPanel);
	}
	

	/**
	 * 
	 * 
	 * @param imagenes Array of images to convolve.
	 * @param selection Selection set over the image. If not exist then the entire image is used.
	 * @param image Image to be convolved.
	 * @return Image convolved.
	 */
	private ImagePlus[] getImgConvolucion(ImagePlus[] imagenes,
			Rectangle selection, ImagePlus image) {
		
		ImagePlus[] conv = new ImagePlus[imagenes.length];
		
		if(selection.height == 0 && selection.width == 0){
			for(int i=0; i<imagenes.length; i++){
				conv[i] = imagenes[i].duplicate();
			}
		}
		else{		
			for(int i=0; i<imagenes.length; i++){
				int newX = selection.x - (prop.getTamVentana()/2);
				int newY = selection.y - (prop.getTamVentana()/2);
				int newHeight = selection.height + prop.getTamVentana();
				int newWidth = selection.width + prop.getTamVentana();
				
				if(newX < 0){
					newX = 0;
				}
				if(newY < 0){
					newY = 0;
				}
				if(newHeight > getImagen().getHeight()){
					newHeight = selection.height;
				}
				if(newWidth > getImagen().getWidth()){
					newWidth = selection.width;
				}
				
				ImageProcessor ip =	image.duplicate().getProcessor();
				ip.setRoi(newX, newY, newWidth, newHeight);
				ip = ip.crop();
				BufferedImage croppedImage = ip.getBufferedImage();
				conv[i] = new ImagePlus("croppedImage" + i, croppedImage);
				ip.resetRoi();
			}
		}
		return conv;
	}

	/**
	 * Executes the training.
	 * This trainning can be using an ARFF file, or an image directory for make a new ARFF file.
	 * 
	 * @param arff The ARFF file used for the training. If not exist an image directory is used.
	 * @param originalDirectory Image directory used for training. This is used if an ARFF file is not specified.
	 */
	public void ejecutaEntrenamiento(File arff, String originalDirectory){
		
		if(arff != null){	//entrenamos con un arff existente
			Instances data;
			try {
				GestorArff l = new GestorArff();
				data = l.leerArff(arff.getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			createModel(data, "arff_existente");
		}
		else{	//entrenamos con las imágenes	
			int processors = Runtime.getRuntime().availableProcessors();
			Rectangle r = new Rectangle(0, 0, 0, 0);
			ImagePlus[] mascaras = divideImagen(r);
			cargaImagen(originalDirectory);
			ImagePlus[] imagenes = divideImagen(r);
			ImagePlus[] saliency = getSaliency(imagenes);
			ImagePlus[] convolucion = getImgConvolucion(imagenes, r, getImagen());
			
			//convolucion de saliency
			Preprocesamiento p = new Saliency(getImagen(), 1);
			ImagePlus imgSaliency = new ImagePlus("", p.calcular());
			ImagePlus[] convolucionSaliency = getImgConvolucion(saliency, r, imgSaliency);
			
			t = new VentanaAbstracta[processors];
			
			Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
	    	    public void uncaughtException(Thread th, Throwable ex) {
	    	    	if(!ex.getClass().toString().contains("ThreadDeath")){
		    	    	excepcion = new RuntimeException(ex);
		    	    	stop();
	    	    	}
	    	    }
	    	};			
					
			for (int ithread = 0; ithread < t.length; ++ithread){    
	            t[ithread] = new VentanaAleatoria(mascaras[ithread], saliency[ithread], convolucion[ithread], convolucionSaliency[ithread], ithread);
	            ((VentanaAbstracta) t[ithread]).setImagenCompleta(imagenes[ithread]);
	            t[ithread].setUncaughtExceptionHandler(h);
	            t[ithread].start();
	        }  
	  
	        try{     
	            for (int ithread = 0; ithread < t.length; ++ithread)  
	                t[ithread].join();  
	        }
	        catch (InterruptedException ie){  
	            
	        }
	  
		}
	}
	


	/**
	 * Executes a training using an image directory.
	 * 
	 * @param originalDirectory The image directory path.
	 * @param maskDirectory The mask directory path.
	 * @param barra The progress bar.
	 * @param txtLog The application log.
	 * @param kit The HTML Editor Kit to handle the application log.
	 * @param doc The HTML Doc to handle the application log.
	 */
	public void ejecutarEntrenamientoDirectorio(String[] originalDirectory, String[] maskDirectory, JProgressBar barra, JTextPane txtLog, HTMLEditorKit kit, HTMLDocument doc){
		
		barra.setMaximum(originalDirectory.length);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				barra.setMaximum(originalDirectory.length-1);
			}
		}
		
		barra.setValue(0);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				
				try {
					File f = new File(originalDirectory[i]);
					kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Analizando imagen: " + f.getName() + "</p>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
					f = null;
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				cargaImagen(maskDirectory[i]);
				int tipoEntrenamiento = prop.getTipoEntrenamiento();
				switch(tipoEntrenamiento){
				case 0:
					ejecutaEntrenamiento(null, originalDirectory[i]);
					break;
				case 1:
					ejecutaEntrenamientoDeslizante(originalDirectory[i]);
					break;
				}
				
				barra.setValue(barra.getValue()+1);
			}
		}
		
		try {
			kit.insertHTML(doc, doc.getLength(), "<br><p class=\"normal\"> Fusionando ficheros ARFF</p><br>", 0, 0, null);
			txtLog.setCaretPosition(txtLog.getDocument().getLength());
		} catch (BadLocationException e1) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
		GestorArff l = new GestorArff();
		l.mergeArffFiles(t.length);
		Instances data = l.leerArff(prop.getPathArff());
		
		try {
			kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Creando modelo</p><br>", 0, 0, null);
			txtLog.setCaretPosition(txtLog.getDocument().getLength());
		} catch (BadLocationException e1) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
		createModel(data, String.valueOf(prop.getTamVentana()));
	}
	
	/**
	 * Executes a trining using a sliding window.
	 * 
	 * @param originalDirectory The image directory path.
	 */
	public void ejecutaEntrenamientoDeslizante(String originalDirectory){
		int processors = Runtime.getRuntime().availableProcessors();	
		Rectangle r = new Rectangle(0, 0, 0, 0);
		ImagePlus[] mascaras = divideImagen(r);
		cargaImagen(originalDirectory);
		ImagePlus[] imagenes = divideImagen(r);
		ImagePlus[] saliency = getSaliency(imagenes);
		ImagePlus[] convolucion = getImgConvolucion(imagenes, r, getImagen());
		
		//convolucion de saliency
		Preprocesamiento p = new Saliency(getImagen(), 1);
		ImagePlus imgSaliency = new ImagePlus("", p.calcular());
		ImagePlus[] convolucionSaliency = getImgConvolucion(saliency, r, imgSaliency);
		
		t = new VentanaAbstracta[processors];
		
		Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
    	    public void uncaughtException(Thread th, Throwable ex) {
    	    	if(!ex.getClass().toString().contains("ThreadDeath")){
	    	    	excepcion = new RuntimeException(ex);
	    	    	stop();
    	    	}
    	    }
    	};
				
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new VentanaDeslizante(mascaras[ithread], saliency[ithread], convolucion[ithread], convolucionSaliency[ithread],
            		ithread, r, null, null, defectMatrix, true);
            ((VentanaAbstracta) t[ithread]).setImagenCompleta(imagenes[ithread]);
            t[ithread].setUncaughtExceptionHandler(h);
            t[ithread].start();
        }  
  
        try{     
            for (int ithread = 0; ithread < t.length; ++ithread)  
                t[ithread].join();  
        }
        catch (InterruptedException ie){  
             
        }
	}
	
	/**
	 * Sets the maximum progress bar value.
	 * 
	 * @param imgs Array of images to calculate the number of windows by image.
	 * @param barra The progress bar to set the maximum value allowed.
	 */
	public void setMaxProgressBar(ImagePlus[] imgs, JProgressBar barra){
		int numTotalVentanas = 0;
		
		for(int i = 0; i<imgs.length; i++){
			numTotalVentanas += calcularNumVentanas(imgs[i]);
		}
		barra.setMaximum(numTotalVentanas);
		barra.setValue(0);
	}

	/**
	 * Calculate the number of windows by image.
	 * 
	 * @param image The image to calculate the number of windows.
	 * @return The number of windows available in the image.
	 */
	private int calcularNumVentanas(ImagePlus image) {
		int altura, anchura, salto;
		int altoVentana = prop.getTamVentana();
		salto = (int) (prop.getSalto()*altoVentana);
		altura = image.getHeight();
		anchura = image.getWidth();
		int a = ((anchura-altoVentana)/salto)+1;
		int b = ((altura-altoVentana)/salto)+1;
		int res = a*b;
		return res;
	}
	
	/**
	 * Creates a model training a classifier using bagging.
	 * 
	 * @param data Contains all the instances of the ARFF file
	 * @param sizeWindow The size of the window
	 */
	public void createModel(Instances data, String sizeWindow) {

		// se crea, opciones, setiputformat
		Classifier cls = null;
		//String separator = System.getProperty("file.separator");
		String path = prop.getPathModel();

		
		int opcionClasificacion = prop.getTipoClasificacion();
		
		switch(opcionClasificacion){
			case 0:
				//CLASIFICADOR CLASES NOMINALES (TRUE,FALSE)
				Classifier base;
				base = new REPTree();
				cls = new Bagging();
				((Bagging) cls).setNumIterations(25);
				((Bagging) cls).setBagSizePercent(100);
				((Bagging) cls).setNumExecutionSlots(Runtime.getRuntime().availableProcessors());
				((Bagging) cls).setClassifier(base);
				break;
			case 1:
				//REGRESIÓN LINEAL (CLASES NUMÉRICAS, 1,0)
				cls = new REPTree();
				break;
		}

		ObjectOutputStream oos = null;

		try {
			data.setClassIndex(data.numAttributes() - 1);
			cls.buildClassifier(data);

			/*if (arffName.contains("mejores"))
				oos = new ObjectOutputStream(new FileOutputStream((path
						+ separator + "Modelos" + separator + "Bagging_"
						+ "mejores_" + sizeWindow + ".model")));

			if (arffName.contains("todas"))*/
				oos = new ObjectOutputStream(new FileOutputStream((path + "todas_" + sizeWindow + ".model")));

			oos.writeObject(cls);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Calculate the local thresholds.
	 * 
	 * @param selection The selection set over the image. If not exists then the entire image is used.
	 * @return ArrayList of Local thresholds values. 
	 */
	public ArrayList<int[]> calcularUmbralesLocales(Rectangle selection){
		Auto_Local_Threshold alt = new Auto_Local_Threshold();
		alt.setRadius(15);
		ImagePlus img;
		
		if(selection.height != 0 && selection.width != 0){	//hay una selección
			ImageProcessor ip =	getImagen().duplicate().getProcessor();
			int x = selection.x - alt.getRadius();			
			int y = selection.y - alt.getRadius();			
			int height = selection.height + (alt.getRadius()*2);
			int width = selection.width + (alt.getRadius()*2);
			int iniX = alt.getRadius();
			int iniY = alt.getRadius();
			
			//Salirse por la izquierda
			if(x < 0){
				x = 0;
				width = selection.width + alt.getRadius();
				iniX = 0;
			}
			//Salirse por arriba
			if(y < 0){
				y = 0;
				height = selection.height + alt.getRadius();
				iniY = 0;
			}
			//Salirse por abajo
			if(y + height > getImagen().getHeight()){
				height = selection.height + alt.getRadius();
			}
			//Salirse por la derecha
			if(x + width > getImagen().getWidth()){
				width = selection.width + alt.getRadius();
			}
			
			Rectangle rec = new Rectangle(x, y, width, height);
			ip.setRoi(rec);
			
			ip = ip.crop();
			BufferedImage croppedImage = ip.getBufferedImage();
			img = new ImagePlus("croppedImage", croppedImage);
			alt.setImp(img);
			alt.run("MidGrey");
			Rectangle r = new Rectangle(alt.getRadius(), alt.getRadius(), selection.width, selection.height);
			ImagePlus im = alt.getImp();
			im.setRoi(r);
			ImageProcessor iproc = im.getProcessor();
			iproc = iproc.crop();
			croppedImage = iproc.getBufferedImage();
			im = new ImagePlus("croppedImage", croppedImage);
			Thresholder th = new Thresholder();
			th.setImage(im);
			th.run("");
			getArrayRois(im);
			return obtenerListaPixelesBlancos(img, selection.x, selection.y, selection.height, selection.width, iniX, iniY);
		}
		else{
			img = getImagen().duplicate();
			alt.setImp(img);
			alt.run("MidGrey");
			Thresholder th = new Thresholder();
			th.setImage(alt.getImp());
			th.run("");
			getArrayRois(alt.getImp());
			return obtenerListaPixelesBlancos(img, 0, 0, img.getHeight(), img.getWidth(), 0, 0);
		}
	}

	/**
	 * Gets the arrays of Rois available on the image. 
	 * 
	 * @param im2 The image.
	 */
	public void getArrayRois(ImagePlus im2) {
		ImagePlus im = im2.duplicate();
		
		int myMinSize = 8; // This is the minimum size of the particles
		int myMaxSize = 2000; // This is the maximum size of the particles
		double myMinCirc = 0.00; // This is the minimum circularity of the
		// particles
		double myMaxCirc = 1.00; // This is the maximum circularity of the
		// particles

		int myOptions = 0;
		
		// This provides the characteristics of the particle measurements
		//int myMeasurements = ParticleAnalyzer.AREA+ ParticleAnalyzer.PERIMETER+ParticleAnalyzer.CIRCULARITY+ParticleAnalyzer.ELLIPSE;
		//int myMeasurements = ParticleAnalyzer.FERET+ParticleAnalyzer.LIMIT;
		//int myMeasurements =0;
		int myMeasurements = ParticleAnalyzer.AREA+ ParticleAnalyzer.PERIMETER+ParticleAnalyzer.CIRCULARITY+ParticleAnalyzer.ELLIPSE+ParticleAnalyzer.FERET;
		
		/* This variable defines the results provided in the table */

		myRT = new ResultsTable(); // Here we create our empty
		// results table
		ParticleAnalyzer pa = new ParticleAnalyzer(myOptions, myMeasurements, myRT, myMinSize, myMaxSize, myMinCirc, myMaxCirc);

		RoiManager manager = new RoiManager(true);
		
		ParticleAnalyzer.setRoiManager(manager);

		pa.analyze(im); // This method runs our particle analyzer in our
		// imageplus imp //"and imageprocessor "ip"
		

		arrayRois = manager.getRoisAsArray();
	}
	

	
	/**
	 * Gets the ArrayList of white pixels.
	 * 
	 * @param img The image.
	 * @param xIni The initial X coordinate.
	 * @param yIni The Initial Y coordinate.
	 * @param height The height of the selection set.
	 * @param width The width of the selection set.
	 * @param iniY initial point in cropped image (x).
	 * @param iniX initial point in cropped image (y).
	 * @return An arraylist of white pixels.
	 */
	private ArrayList<int[]> obtenerListaPixelesBlancos(ImagePlus img, int xIni, int yIni, int height, int width, int iniX, int iniY) {
		ArrayList<int[]> listaCoordenadas = new ArrayList<int[]>();
		
		for(int j = iniY; j<height; j++){
			for(int i = iniX; i<width; i++){
				if(img.getProcessor().getPixel(i, j) == 255){	//pixel blanco
					listaCoordenadas.add(new int[]{i+xIni-iniX,j+yIni-iniY});
				}
			}
		}
		return listaCoordenadas;
	}

	/**
	 * Draw the edges of a defect.
	 * 
	 * @param imgPanel The panel of the image viewer.
	 */
	public void drawEdge(Graphic imgPanel) {
		int[][] defectsMatrix2 = new int[getImagen().getWidth()][getImagen().getHeight()];
		int[][] defectsMatrixDefinitiva;

		copiarMatrizDefectos(defectsMatrix2);

		binarizarMatriz(defectsMatrix2);
		
		if(prop.getTipoDeteccion() == 0){	//normal
			defectsMatrixDefinitiva = defectsMatrix2;
		}
		else{	//normal + umbrales locales
			defectsMatrixDefinitiva = obtenerMatrizInterseccion(defectsMatrix2, listaCoordenadas);
		}		

		BufferedImage bfrdImage = crearMascara(defectsMatrixDefinitiva);

		ImagePlus edgesImage = new ImagePlus("", bfrdImage);

		BufferedImage bufferedResult = establecerBordes(edgesImage);

		ImagePlus imagePlusResult = new ImagePlus("", bufferedResult);
		imgPanel.isEnded(true);
		imgPanel.setImage(imagePlusResult.getImage());
		imgPanel.repaint();
		
		calcularCaracteristicasGeometricas();
	}
	
	/**
	 * Draw the edges of a defect using regions.
	 * 
	 * @param imgPanel The panel of the image viewer.
	 */
	public void drawEdgeRegiones(Graphic imgPanel) {
		int[][] defectsMatrix2 = new int[getImagen().getWidth()][getImagen().getHeight()];
		copiarMatrizDefectos(defectsMatrix2);

		binarizarMatriz(defectsMatrix2);

		BufferedImage bfrdImage = crearMascara(defectsMatrix2);

		ImagePlus edgesImage = new ImagePlus("", bfrdImage);

		BufferedImage bufferedResult = establecerBordes(edgesImage);

		ImagePlus imagePlusResult = new ImagePlus("", bufferedResult);
		imgPanel.isEnded(true);
		imgPanel.setImage(imagePlusResult.getImage());
		imgPanel.repaint();
		
		calcularCaracteristicasGeometricas();
	}

	/**
	 * Gets the intersection matrix between the pixels mask, and the pixels marked as defect.
	 * 
	 * @param defectsMatrix The matrix of pixels marked as defect. 
	 * @param listaCoordenadas Arraylist of coordinates.
	 * @return The intersection between the pixels marked as defect, and the real defect pixels.
	 */
	private int[][] obtenerMatrizInterseccion(int[][] defectsMatrix,
			ArrayList<int[]> listaCoordenadas) {
		
		int[][] defectsMatrixDefinitiva = new int[getImagen().getWidth()][getImagen().getHeight()];
		
		Iterator<int[]> it = listaCoordenadas.iterator();
		while(it.hasNext()){
			int[] coord = it.next();
			if(defectsMatrix[coord[0]][coord[1]] == 1){
				defectsMatrixDefinitiva[coord[0]][coord[1]] = 1;
			}
		}
		return defectsMatrixDefinitiva;
	}

	/**
	 * Set the edges into an image.
	 * 
	 * @param edgesImage Image to set the edges.
	 * @return The image with the edges set.
	 */
	public BufferedImage establecerBordes(ImagePlus edgesImage) {
		// Detectar bordes
		IJ.run(edgesImage, "Find Edges", "");

		// Invertir colores
		IJ.run(edgesImage, "Invert", "");

		BufferedImage bufferedEdgesImage = imageToBufferedImage(edgesImage.getImage());

		BufferedImage backgroundImage = imageToBufferedImage(getImagen().getImage());

		// Poner fondo transparente
		BufferedImage transparentImage = makeColorTransparent(bufferedEdgesImage, Color.white);
		

		// Superponer las dos imagenes
		BufferedImage bufferedResult = overlayImages(backgroundImage, transparentImage);
		return bufferedResult;
	}

	/**
	 * Create a mask since a defect matrix.
	 * 
	 * @param defectsMatrix2 Defect matrix values.
	 * @return A new image mask.
	 */
	public BufferedImage crearMascara(int[][] defectsMatrix2) {
		BufferedImage bfrdImage = new BufferedImage(getImagen().getWidth(),
				getImagen().getHeight(), BufferedImage.TYPE_INT_RGB);
		imgBin = new BufferedImage(getImagen().getWidth(),
				getImagen().getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < getImagen().getHeight(); i++) {
			for (int j = 0; j < getImagen().getWidth(); j++) {
				if (defectsMatrix2[j][i] == 0){
					// Color blanco
					bfrdImage.setRGB(j, i, new Color(255, 255, 255).getRGB());
					imgBin.setRGB(j, i, Color.WHITE.getRGB());
				}

				if (defectsMatrix2[j][i] == 1){
					// Color amarillo
					bfrdImage.setRGB(j, i, new Color(255, 255, 0).getRGB());
					imgBin.setRGB(j, i, Color.BLACK.getRGB());
				}
			}
		}
		return bfrdImage;
	}

	/**
	 * Copy the matrix defect pixels value into another matrix.
	 * 
	 * @param defectsMatrix2 The copy of the defect matrix.
	 */
	public void copiarMatrizDefectos(int[][] defectsMatrix2) {
		for (int i = 0; i < getImagen().getHeight(); i++) {
			for (int j = 0; j < getImagen().getWidth(); j++) {
				defectsMatrix2[j][i] = defectMatrix[j][i];
			}
		}
	}

	/**
	 * Binary a Matrix. If the value of a matrix exceeds the threshold, the value is set to 1. Else the value is set to 0.
	 * 
	 * @param defectsMatrix2 The defect matrix.
	 */
	public void binarizarMatriz(int[][] defectsMatrix2) {
		for (int i = 0; i < getImagen().getHeight(); i++) {
			for (int j = 0; j < getImagen().getWidth(); j++) {

				if (defectMatrix[j][i] > prop.getUmbral()) {
					defectsMatrix2[j][i] = 1;
				} else
					defectsMatrix2[j][i] = 0;
			}
		}
	}
	
	/**
	 * Converts an image to a BufferedImage.
	 * 
	 * @param im Image to convert.
	 * @return bufferedImage BufferedImage converted.
	 */
	public BufferedImage imageToBufferedImage(Image im) {
		BufferedImage bi = new BufferedImage(im.getWidth(null),
				im.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}

	/**
	 * Converts a specified color to transparent.
	 * 
	 * @param bufferedImage Image to use
	 * @param color Color to make transparent
	 * @return image with the color specified transparent
	 */
	public BufferedImage makeColorTransparent(BufferedImage bufferedImage,
			Color color) {
		BufferedImage bufim = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bufim.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(bufferedImage, null, 0, 0);
		g.dispose();
		for (int i = 0; i < bufim.getHeight(); i++) {
			for (int j = 0; j < bufim.getWidth(); j++) {
				if (bufim.getRGB(j, i) == color.getRGB()) {
					bufim.setRGB(j, i, 0x8F1C1C);
				}
			}
		}
		return bufim;
	}

	/**
	 * This method overlays two images.
	 * 
	 * @param bgImage Image that will be the background
	 * @param fgImage Image that will be the foreground
	 * @return Image overlayed
	 */
	public BufferedImage overlayImages(BufferedImage bgImage,
			BufferedImage fgImage) {
		if (fgImage.getHeight() > bgImage.getHeight()
				|| fgImage.getWidth() > fgImage.getWidth()) {
			JOptionPane.showMessageDialog(null,
					"Foreground Image Is Bigger In One or Both Dimensions"
							+ "\nCannot proceed with overlay."
							+ "\n\n Please use smaller Image for foreground");
			return null;
		}
		Graphics2D g = bgImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(bgImage, 0, 0, null);
		g.drawImage(fgImage, 0, 0, null);
		g.dispose();
		return bgImage;
	}
	
	/**
	 * Executes a window using regions method.
	 * 
	 * @param selection The selection over the image. If don't exists the entire image is used.
	 * @param imgPanel The panel of the image viewer.
	 * @param progressBar The progress bar.
	 */
	public void ejecutaVentanaOpcionRegiones(Rectangle selection, Graphic imgPanel, JProgressBar progressBar){
		ArrayList<int[]> blancos = calcularUmbralesLocales(selection);
		int numProcessors = Runtime.getRuntime().availableProcessors();
		
		t = new VentanaAbstracta[numProcessors];
		defectMatrix = new int[getImagen().getWidth()][getImagen().getHeight()];
		progressBar.setMaximum(blancos.size());
		progressBar.setValue(0);
		
		int tamListas = blancos.size()/numProcessors;
		
		ImagePlus[] imagenes = new ImagePlus[1];
		
		if(selection.height != 0 && selection.width != 0){	//hay una selección
			ImageProcessor ip =	getImagen().duplicate().getProcessor();
			ip.setRoi(selection);
			ip = ip.crop();
			BufferedImage croppedImage = ip.getBufferedImage();
			imagenes[0] = new ImagePlus("croppedImage", croppedImage);
		}
		else{
			imagenes[0] = getImagen().duplicate();
		}		
		
		ImagePlus[] saliency = getSaliency(imagenes);
		ImagePlus[] convolucion = getImgConvolucion(imagenes, selection, getImagen());
		
		//convolucion de saliency
		Preprocesamiento p = new Saliency(getImagen(), 1);
		ImagePlus imgSaliency = new ImagePlus("", p.calcular());
		ImagePlus[] convolucionSaliency = getImgConvolucion(saliency, selection, imgSaliency);
		
		for (int ithread = 0; ithread < t.length; ++ithread){
			List<int[]> pixeles = new ArrayList<int[]>();
			if(ithread == t.length-1){
				pixeles.addAll(blancos.subList(ithread*tamListas, blancos.size()));
			}
			else{
				pixeles.addAll(blancos.subList(ithread*tamListas, ((ithread+1)*tamListas)-1));
			}
			
            t[ithread] = new VentanaRegiones(imagenes[0], saliency[0], convolucion[0], convolucionSaliency[0],
            		ithread, selection, imgPanel, progressBar, defectMatrix, pixeles);
            ((VentanaRegiones)t[ithread]).setArrayRois(arrayRois);
            t[ithread].start();
        }  
  
        try{     
            for (int ithread = 0; ithread < t.length; ++ithread)  
                t[ithread].join();  
        }
        catch (InterruptedException ie){  
             
        }
        drawEdgeRegiones(imgPanel);
	}
	
	/**
	 * Calculates the geometric characteristics of the defect. 
	 */
	public void calcularCaracteristicasGeometricas(){
		Thresholder th = new Thresholder();
		ImagePlus bin = new ImagePlus("bin", imgBin);
		th.setImage(bin);
		th.run("");
		getArrayRois(bin);
		
		tableModel = new DefaultTableModel(	new Object[][] {}, new String[] {"Regi\u00F3n", "\u00C1rea", "Per\u00EDmetro", "Circularidad", "Redondez", "Semieje Mayor", "Semieje Menor", "\u00C1ngulo", "Distancia Feret"}){				
				private static final long serialVersionUID = 1L;
				@SuppressWarnings("rawtypes")
				Class[] columnTypes = new Class[] {Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class};
				@SuppressWarnings({ "unchecked", "rawtypes" })
				public Class getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
		for(int i=0; i<arrayRois.length; i++){
			Object[] fila = {i, myRT.getValueAsDouble(ResultsTable.AREA, i), myRT.getValueAsDouble(ResultsTable.PERIMETER, i), myRT.getValueAsDouble(ResultsTable.CIRCULARITY, i), myRT.getValueAsDouble(ResultsTable.ROUNDNESS, i), myRT.getValueAsDouble(ResultsTable.MAJOR, i), myRT.getValueAsDouble(ResultsTable.MINOR, i), myRT.getValueAsDouble(ResultsTable.ANGLE, i), myRT.getValueAsDouble(ResultsTable.FERET, i)};
			tableModel.addRow(fila);
		}
		
	}
	
	/**
	 * Gets the table model of the results table.
	 * 
	 * @return the table model of the results table.
	 */
	public DefaultTableModel getTableModel(){
		return tableModel;
	}
	
	/**
	 * Gets the specified Roi.
	 * 
	 * @param index The index of the Roi.
	 * @return The Roi selected.
	 */
	public Roi getRoi(int index){
		return arrayRois[index];
	}
	
	/**
	 * Gets an array with all the Rois.
	 * 
	 * @return All the Rois available.
	 */
	public Roi[] getArrayRoisCompleto(){
		return arrayRois;
	}
	
	/**
	 * Gets a RunTime Exception.
	 * 
	 * @return A RuntimeException
	 * @see #setExcepcion
	 */
	public RuntimeException getExcepcion(){
		return excepcion;
	}
	
	/**
	 * Sets a RunTime Exception.
	 * 
	 * @param e A RunTime Exception.
	 * @see #getExcepcion
	 */
	public void setExcepcion(RuntimeException e){
		excepcion = e;
	}
	
	/**
	 * Gets the binary image.
	 * 
	 * @return A binarized image.
	 */
	public ImagePlus getImageBinarizada(){
		return new ImagePlus("i", imgBin);
	}
	
	/**
	 * Gets the precision and recall values.
	 * 
	 * @param path Path of the mask of the image used.
	 * @param selection The selection over the image. If don't exists the entire image is used.
	 * @return An array with the precission and recalll values. [0] == Precision. [1] == Recall.
	 */
	public double[] getPrecisionRecall(String path, Rectangle selection){
		int truePositive = 0, falsePositive = 0, falseNegative = 0;
		ImagePlus imBin = new ImagePlus("img", imgBin);
		ir.abrirImagen(path);
		ImagePlus mask = ir.getImagen();
		int[] pixelBin, pixelMask;
		double[] resultado = new double[2];
		
		for(int y=0; y<selection.height; y++){
			for(int x=0; x<selection.width; x++){
				pixelBin = imBin.getPixel(x+selection.x, y+selection.y);
				pixelMask = mask.getPixel(x+selection.x, y+selection.y);
			
				if(pixelBin[0] != 255 || pixelBin[1] != 255 || pixelBin[2] != 255){	//bin no es blanco
					if(pixelMask[0] != 255 || pixelMask[1] != 255 || pixelMask[2] != 255){	//mask no es blanco
						truePositive++;
					}
					else{	//mask es blanco
						falsePositive++;
					}
				}
				else{	//bin es blanco
					if(pixelMask[0] != 255 || pixelMask[1] != 255 || pixelMask[2] != 255){	//mask no es blanco
						falseNegative++;
					}
				}
			}
		}
		
		//precision
		try{
			resultado[0] = ((double)truePositive/(double)(truePositive+falsePositive));
		}
		catch(ArithmeticException e){
			resultado[0] = Double.NaN;
		}
		
		//recall
		try{
			resultado[1] = ((double)truePositive/(double)(truePositive+falseNegative));
		}
		catch(ArithmeticException e){
			resultado[1] = Double.NaN;
		}
		
		return resultado;
	}
}
