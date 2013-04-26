package modelo;



import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.process.FloatProcessor;
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

import utils.Auto_Local_Threshold;
import utils.Graphic;
import utils.ParticleAnalyzer;
import utils.Thresholder;

import utils.Propiedades;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import datos.GestorArff;
import datos.ImageReader;

public class Fachada {
	
	private int[][] defectMatrix;
	private static Fachada INSTANCE = null;
	private ImageReader ir;
	private Thread[] t;
	private ImagePlus imagen;
	private static Propiedades prop;
	private ArrayList<int[]> listaCoordenadas;
	private Roi[] arrayRois;
	private BufferedImage imgBin;
	private ResultsTable myRT;
	private DefaultTableModel tableModel;
	private RuntimeException excepcion = null;
	
	private Fachada() {
		ir = new ImageReader();
		prop = Propiedades.getInstance();
	}
	
	public ImagePlus getImagen(){
		return imagen;
	}
	
	public void setImagen(ImagePlus img){
		imagen = img;
	}
	
	public int getNumThreads(){
		if(t == null){
			return 0;
		}
		return t.length;
	}
	
	public static Fachada getInstance(){	//Singleton
		if(INSTANCE == null){
			return new Fachada();
		}
		else{
			return INSTANCE;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stop(){
		 for (int ithread = 0; ithread < t.length; ithread++){ 
             if(t[ithread].isAlive()){
            	 t[ithread].stop(); 
             }
		 }
	}

	public String cargaImagen(String img){		
		int i = ir.abrirImagen(img);
		setImagen(ir.getImagen());
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
	
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
	
				//System.out.println("Vuelta"+i+": " + ip.getRoi().getHeight() + ", " + ip.getRoi().getWidth());
				//System.out.println("Vuelta"+i+": "+i*tam+", "+img.getWidth());
				ip = ip.crop();
				BufferedImage croppedImage = ip.getBufferedImage();
				imagenes[i] = new ImagePlus("croppedImage" + i, croppedImage);
				ip.resetRoi();
			}
			return imagenes;
		}
	}
	
	public ImagePlus[] getSaliency(ImagePlus[] imagenes){
		ImagePlus[] saliency = new ImagePlus[imagenes.length];
		for(int i=0; i<imagenes.length; i++){
			Preprocesamiento p = new Saliency(imagenes[i], 1);
			saliency[i] = new ImagePlus("", p.calcular());
		}
		return saliency;
	}
	
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
    	    	excepcion = new RuntimeException(ex);
    	    	stop();
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
				//System.out.println("Valores: newX: " + newX + " newY: " + newY + " NewW: " + newWidth + " newH: " + newHeight);
				BufferedImage croppedImage = ip.getBufferedImage();
				conv[i] = new ImagePlus("croppedImage" + i, croppedImage);
				//System.out.println("conv[i]: H: " + conv[i].getHeight() + " W: " + conv[i].getWidth());
				ip.resetRoi();
			}
		}
		return conv;
	}

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
	    	    	excepcion = new RuntimeException(ex);
	    	    	stop();
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
				//System.out.println("Or: " + originalDirectory[i] + " Mask: " + maskDirectory[i]);
				
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
    	    	excepcion = new RuntimeException(ex);
    	    	stop();
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
	
	public void setMaxProgressBar(ImagePlus[] imgs, JProgressBar barra){
		int numTotalVentanas = 0;
		
		for(int i = 0; i<imgs.length; i++){
			numTotalVentanas += calcularNumVentanas(imgs[i]);
		}
		barra.setMaximum(numTotalVentanas);
		barra.setValue(0);
	}

	private int calcularNumVentanas(ImagePlus image) {
		int altura, anchura, salto;
		int altoVentana = prop.getTamVentana();
		salto = (int) (prop.getSalto()*altoVentana);
		altura = image.getHeight();
		anchura = image.getWidth();
		int a = ((anchura-altoVentana)/salto)+1;
		int b = ((altura-altoVentana)/salto)+1;
		int res = a*b;
		//System.out.println("Ancho: "+anchura+" Alto: "+altura+" Salto: " +salto+ "Total ventanas: " +res);
		return res;
	}
	
	/**
	 * Creates a model training a classifier using bagging.
	 * 
	 * @param data
	 *            Contains all the instances of the arff
	 * @param sizeWindow
	 *            The size of the window
	 */
	public void createModel(Instances data, String sizeWindow) {

		// se crea, opciones, setiputformat
		Classifier cls = null;
		//String separator = System.getProperty("file.separator");
		//String path = System.getProperty("user.dir");
		String path = "./res/model/";

		
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
				oos = new ObjectOutputStream(new FileOutputStream((path + "todas_regresion_" + sizeWindow + ".model")));

			oos.writeObject(cls);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
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
			
			//Salirse por la izquierda
			if(x < 0){
				x = 0;
				width = selection.width + alt.getRadius();
			}
			//Salirse por arriba
			if(y < 0){
				y = 0;
				height = selection.height + alt.getRadius();
			}
			//Salirse por abajo
			if(y + height > getImagen().getHeight()){
				height = selection.height + alt.getRadius();
			}
			//Salirse por la derecha
			if(x + width > getImagen().getWidth()){
				width = selection.width + alt.getRadius();
			}

			/*
			if(x < 0 || y < 0 || height + y > getImagen().getHeight() || width + x > getImagen().getWidth()){
				ip.setRoi(selection); //si no entra la seleccion+radio, cogemos la selección normal
			}
			*/
//			else{				
			Rectangle rec = new Rectangle(x, y, width, height);
			ip.setRoi(rec);
//			}
			
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
			return obtenerListaPixelesBlancos(img, selection.x, selection.y, selection.height, selection.width);
		}
		else{
			img = getImagen().duplicate();
			alt.setImp(img);
			alt.run("MidGrey");
			Thresholder th = new Thresholder();
			th.setImage(alt.getImp());
			th.run("");
			getArrayRois(alt.getImp());
			return obtenerListaPixelesBlancos(img, 0, 0, img.getHeight(), img.getWidth());
		}
	}

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
		/** In this method we create our particle analyzer with our properties */

		RoiManager manager = new RoiManager(true);
		
		ParticleAnalyzer.setRoiManager(manager);

		pa.analyze(im); // This method runs our particle analyzer in our
		// imageplus imp //"and imageprocessor "ip"
		

		arrayRois = manager.getRoisAsArray();
	}
	

	
	private ArrayList<int[]> obtenerListaPixelesBlancos(ImagePlus img, int xIni, int yIni, int height, int width) {
		ArrayList<int[]> listaCoordenadas = new ArrayList<int[]>();
		
		for(int j = 0; j<height; j++){
			for(int i = 0; i<width; i++){
				if(img.getProcessor().getPixel(i, j) == 255){	//pixel blanco
					listaCoordenadas.add(new int[]{i+xIni,j+yIni});
				}
			}
		}
		return listaCoordenadas;
	}

	public void drawEdge(Graphic imgPanel) {
		int[][] defectsMatrix2 = new int[getImagen().getWidth()][getImagen().getHeight()];
		int[][] defectsMatrixDefinitiva;
		//defect = false;

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
		guardarMapaCalor();
		
		calcularCaracteristicasGeometricas();
	}
	
	//duplicado
	public void drawEdgeRegiones(Graphic imgPanel) {
		int[][] defectsMatrix2 = new int[getImagen().getWidth()][getImagen().getHeight()];
		//defect = false;

		copiarMatrizDefectos(defectsMatrix2);

		binarizarMatriz(defectsMatrix2);

		BufferedImage bfrdImage = crearMascara(defectsMatrix2);

		ImagePlus edgesImage = new ImagePlus("", bfrdImage);

		BufferedImage bufferedResult = establecerBordes(edgesImage);

		ImagePlus imagePlusResult = new ImagePlus("", bufferedResult);
		imgPanel.isEnded(true);
		imgPanel.setImage(imagePlusResult.getImage());
		imgPanel.repaint();
		guardarMapaCalor();
		
		calcularCaracteristicasGeometricas();
	}

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

	public void copiarMatrizDefectos(int[][] defectsMatrix2) {
		for (int i = 0; i < getImagen().getHeight(); i++) {
			for (int j = 0; j < getImagen().getWidth(); j++) {
				defectsMatrix2[j][i] = defectMatrix[j][i];
			}
		}
	}

	public void binarizarMatriz(int[][] defectsMatrix2) {
		for (int i = 0; i < getImagen().getHeight(); i++) {
			for (int j = 0; j < getImagen().getWidth(); j++) {

				if (defectMatrix[j][i] > prop.getUmbral()) {
					defectsMatrix2[j][i] = 1;
					//defect = true;
				} else
					defectsMatrix2[j][i] = 0;
			}
		}
	}
	
	/**
	 * This method converts an image of the BufferedImage to Image.
	 * 
	 * @param im
	 *            Image to converts
	 * @return bufferedImage
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
	 * @param bufferedImage
	 *            Image to work with
	 * @param color
	 *            Color that wish make transparent
	 * @return image with that color transparent
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
	 * @param bgImage
	 *            Image that will be the background
	 * @param fgImage
	 *            Image that will be the foreground
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
	
	//Pruebas
	public void guardarMapaCalor(){
		FloatProcessor fp = new FloatProcessor(defectMatrix);
		ImagePlus i = new ImagePlus("mapa_calor", fp.createImage());
		IJ.saveAs(i, "BMP", "./res/img/" + i.getTitle());
	}
	
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
	
	public DefaultTableModel getTableModel(){
		return tableModel;
	}
	
	public Roi getRoi(int index){
		return arrayRois[index];
	}
	
	public Roi[] getArrayRoisCompleto(){
		return arrayRois;
	}
	
	public RuntimeException getExcepcion(){
		return excepcion;
	}
	
	public void setExcepcion(RuntimeException e){
		excepcion = e;
	}
}
