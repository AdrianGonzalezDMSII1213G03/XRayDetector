package modelo;



import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;

import utils.Graphic;
import weka.core.Instances;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import datos.FileReader;

public class Mediador {
	
	private static Mediador INSTANCE = null;
	private FileReader fr;
	private Thread[] t;
	private ImagePlus imagen;
	
	private Mediador() {
		fr = new FileReader();
	}
	
	public ImagePlus getImagen(){
		return imagen;
	}
	
	public void setImagen(ImagePlus img){
		imagen = img;
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
		int i = fr.abrirImagen(img);
		setImagen(fr.getImagen());
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
	
	public void ejecutaVentana(Rectangle selection, Graphic imgPanel, File model, JProgressBar progressBar){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen(selection);
		t = new VentanaAbstracta[processors];
		
		setMaxProgressBar(imagenes, progressBar);
				
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new VentanaDeslizante(imagenes[ithread], ithread, selection, imgPanel, model, progressBar);
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
			VentanaAbstracta va = new VentanaAleatoria(null, 0);
			Instances data = va.leerArff(arff.getAbsolutePath());
			va.createModel(data, "arff_existente");
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
	
	public void ejecutarEntrenamientoDirectorio(String[] originalDirectory, String[] maskDirectory, JProgressBar barra){
		
		barra.setMaximum(originalDirectory.length);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				barra.setMaximum(originalDirectory.length-1);
			}
		}
		
		barra.setValue(0);
		
		for(int i=0; i < originalDirectory.length; i++){
			if(!originalDirectory[i].contains("Thumbs.db")){
				System.out.println("Or: " + originalDirectory[i] + " Mask: " + maskDirectory[i]);
				cargaImagen(maskDirectory[i]);
				ejecutaEntrenamiento(null, originalDirectory[i]);
				barra.setValue(barra.getValue()+1);
			}
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
		int altoVentana = 24;	//de momento, a pelo
		salto = (int) (0.7*altoVentana);	//de momento, a pelo. después, se hará con el fichero de opciones
		altura = image.getHeight();
		anchura = image.getWidth();
		int a = ((anchura-altoVentana)/salto)+1;
		int b = ((altura-altoVentana)/salto)+1;
		int res = a*b;
		System.out.println("Ancho: "+anchura+" Alto: "+altura+" Salto: " +salto+ "Total ventanas: " +res);
		return res;
	}
}
