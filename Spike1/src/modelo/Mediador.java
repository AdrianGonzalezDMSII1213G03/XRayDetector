package modelo;



import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.commons.io.FileUtils;

import utils.Graphic;
import utils.Propiedades;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import datos.ImageReader;

public class Mediador {
	
	private static Mediador INSTANCE = null;
	private ImageReader ir;
	private Thread[] t;
	private ImagePlus imagen;
	private static Propiedades prop;
	
	private Mediador() {
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
	
	public static Mediador getInstance(){	//Singleton
		if(INSTANCE == null){
			return new Mediador();
		}
		else{
			return INSTANCE;
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stop(){
		 for (int ithread = 0; ithread < t.length; ++ithread)  
             t[ithread].stop();
	}

	public String cargaImagen(String img){		
		int i = ir.abrirImagen(img);
		setImagen(ir.getImagen());
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
	
	public ImagePlus[] divideImagen(Rectangle selection){
		int processors = Runtime.getRuntime().availableProcessors();
		int offset = 10;
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
			IJ.saveAs(saliency[i], "BMP", "./res/img/" + "saliency_hilo_" + i);
		}
		return saliency;
	}
	
	public void ejecutaVentana(Rectangle selection, Graphic imgPanel, JProgressBar progressBar){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen(selection);
		t = new VentanaAbstracta[processors];
		
		setMaxProgressBar(imagenes, progressBar);
				
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new VentanaDeslizante(imagenes[ithread], ithread, selection, imgPanel, progressBar);
            t[ithread].start();
        }  
  
        try{     
            for (int ithread = 0; ithread < t.length; ++ithread)  
                t[ithread].join();  
        }
        catch (InterruptedException ie){  
            throw new RuntimeException(ie);  
        }
	}
	
	public void ejecutaVentanaSaliency(Rectangle selection, Graphic imgPanel){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen(selection);
		ImagePlus[] saliency = getSaliency(imagenes);
		t = new VentanaAbstracta[processors];
		
		for (int ithread = 0; ithread < t.length; ++ithread){    
            //t[ithread] = new VentanaDeslizante(saliency[ithread], ithread);
            t[ithread].start();
        }  
  
        try{     
            for (int ithread = 0; ithread < t.length; ++ithread)  
                t[ithread].join();  
        }
        catch (InterruptedException ie){  
            throw new RuntimeException(ie);  
        }
	}
	
	public void ejecutaEntrenamiento(File arff, String originalDirectory){
		
		if(arff != null){	//entrenamos con un arff existente
			Instances data;
			try {
				data = leerArff(arff.getAbsolutePath());
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
			t = new VentanaAbstracta[processors];
			
					
			for (int ithread = 0; ithread < t.length; ++ithread){    
	            t[ithread] = new VentanaAleatoria(mascaras[ithread], ithread);
	            ((VentanaAleatoria) t[ithread]).setImagenCompleta(imagenes[ithread]);
	            t[ithread].start();
	        }  
	  
	        try{     
	            for (int ithread = 0; ithread < t.length; ++ithread)  
	                t[ithread].join();  
	        }
	        catch (InterruptedException ie){  
	            throw new RuntimeException(ie);  
	        }
	  
		}
	}
	
	private void mergeArffFiles() {
		File[] ficheros = new File[t.length];
		for (int ithread = 0; ithread < t.length; ++ithread){
			File file = new File("./res/arff/Arff_entrenamiento" + ithread + ".arff");
			ficheros[ithread] = file;
		}
		// File to write
		//File fileOutput = new File("./res/arff/Arff_entrenamiento.arff");	//de momento, a pelo
		File fileOutput = new File(prop.getPathArff());

		for(int i=0; i<ficheros.length; i++){
			// Read the file like string
			try {
				String file1Str = FileUtils.readFileToString(ficheros[i]);
				if(i==0){
					FileUtils.write(fileOutput, file1Str);
				}
				else{
					FileUtils.write(fileOutput, file1Str, true);
				}
				ficheros[i].delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void ejecutarEntrenamientoDirectorio(String[] originalDirectory, String[] maskDirectory, JProgressBar barra, JTextPane txtLog){
		
		barra.setMaximum(originalDirectory.length);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				barra.setMaximum(originalDirectory.length-1);
			}
		}
		
		barra.setValue(0);
		
		SimpleAttributeSet sa = new  SimpleAttributeSet();
		StyleConstants.setBold(sa, false);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				//System.out.println("Or: " + originalDirectory[i] + " Mask: " + maskDirectory[i]);
				
				try {
					File f = new File(originalDirectory[i]);					
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Analizando imagen: " + f.getName() + "\n", sa);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
					f = null;
				} catch (BadLocationException e1) {
					throw new RuntimeException();
				}
				
				cargaImagen(maskDirectory[i]);
				ejecutaEntrenamiento(null, originalDirectory[i]);
				barra.setValue(barra.getValue()+1);
			}
		}
		try {
			txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "\nFusionando ficheros ARFF\n\n", sa);
			txtLog.setCaretPosition(txtLog.getDocument().getLength());
		} catch (BadLocationException e1) {
			throw new RuntimeException();
		}
		mergeArffFiles();
		Instances data = leerArff(prop.getPathArff());
		
		try {
			txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Creando modelo\n\n", sa);
			txtLog.setCaretPosition(txtLog.getDocument().getLength());
		} catch (BadLocationException e1) {
			throw new RuntimeException();
		}
		createModel(data, String.valueOf(prop.getTamVentana()));
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
		Classifier cls;
		//String separator = System.getProperty("file.separator");
		//String path = System.getProperty("user.dir");
		String path = "./res/model/";

		Classifier base;
		base = new REPTree();

		cls = new Bagging();
		((Bagging) cls).setNumIterations(10);
		((Bagging) cls).setBagSizePercent(10);
		((Bagging) cls).setClassifier(base);

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

		}
	}
	
	public Instances leerArff (String url){
		BufferedReader reader = null;		
		try {
			reader = new BufferedReader(new FileReader(url));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		ArffReader arff = null;		
		try {
			arff = new ArffReader(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		Instances data = arff.getData();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
}
