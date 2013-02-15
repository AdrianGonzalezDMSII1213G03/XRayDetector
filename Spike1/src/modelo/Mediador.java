package modelo;



import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import utils.Graphic;
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
	
	public void ejecutaVentana(Rectangle selection, Graphic imgPanel, File model){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen(selection);
		t = new VentanaAbstracta[processors];
				
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new VentanaDeslizante(imagenes[ithread], ithread, selection, imgPanel, model);
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
	
	public void ejecutaEntrenamiento(){
		int processors = Runtime.getRuntime().availableProcessors();
		Rectangle r = new Rectangle(0, 0, 0, 0);
		ImagePlus[] mascaras = divideImagen(r);
		fr.abrirImagen("./res/img/img1.BMP");
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
