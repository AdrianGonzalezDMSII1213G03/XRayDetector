package modelo;

import utils.Propiedades;
import ij.ImagePlus;

public abstract class VentanaAbstracta extends Thread{
	private int altura;
	private int anchura;
	private int getNumHilo;
	private ImagePlus img;
	private ImagePlus saliency;
	private ImagePlus imgCompleta;
	private ImagePlus imgConvolucion;
	private ImagePlus imgConvolucionSaliency;
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

}
