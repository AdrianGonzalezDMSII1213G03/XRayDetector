package modelo;



import java.awt.image.BufferedImage;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import datos.FileReader;

public class Mediador {
	
	private static Mediador INSTANCE = null;
	private FileReader fr;
	
	private Mediador() {
		fr = new FileReader();
	}
	
	public static Mediador getInstance(){	//Singleton
		if(INSTANCE == null){
			return new Mediador();
		}
		else{
			return INSTANCE;
		}
	}

	public String cargaImagen(String img){		
		int i = fr.abrirImagen(img);
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
	
	public ImagePlus[] divideImagen(){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = new ImagePlus[processors];
		ImagePlus img = fr.getImagen();		
		int tam = img.getHeight()/processors;
		//System.out.println("Proc: " + processors + "\tTam: " + tam);
		//System.out.println("Width: " + img.getWidth());
		for(int i=0; i < processors; i++) {
			ImageProcessor ip =	img.duplicate().getProcessor();
			ip.setRoi(0, i*tam, img.getWidth(), tam);
			//System.out.println("Vuelta"+i+": " + ip.getRoi().getHeight() + ", " + ip.getRoi().getWidth());
			//System.out.println("Vuelta"+i+": "+i*tam+", "+img.getWidth());
			ip = ip.crop();
			BufferedImage croppedImage = ip.getBufferedImage();
			imagenes[i] = new ImagePlus("croppedImage" + i, croppedImage);
			ip.resetRoi();
		}
		return imagenes;
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
	
	public void ejecutaVentana(){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen();
		Thread[] t = new Ventana[processors];
		
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new Ventana(imagenes[ithread]);
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
	
	public void ejecutaVentanaSaliency(){
		int processors = Runtime.getRuntime().availableProcessors();
		ImagePlus[] imagenes = divideImagen();
		ImagePlus[] saliency = getSaliency(imagenes);
		Thread[] t = new Ventana[processors];
		
		for (int ithread = 0; ithread < t.length; ++ithread){    
            t[ithread] = new Ventana(saliency[ithread]);
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
