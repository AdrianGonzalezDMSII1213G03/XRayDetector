package modelo;

import utils.Propiedades;
import ij.ImagePlus;

public abstract class VentanaAbstracta extends Thread{
	private int altura;
	private int anchura;
	private int getNumHilo;
	private ImagePlus img;
	private ImagePlus imgCompleta;
	private static Propiedades prop;
	
	public VentanaAbstracta(ImagePlus img, int numHilo) {
		this.img = img;
		this.getNumHilo = numHilo;
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
	
	public Propiedades getPropiedades(){
		return prop;
	}

}
