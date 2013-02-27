package modelo;

import ij.ImagePlus;

public abstract class VentanaAbstracta extends Thread{
	private int altura = 24;
	private int anchura = 24;
	private int getNumHilo;
	private ImagePlus img;
	private ImagePlus imgCompleta;
	
	public VentanaAbstracta(ImagePlus img, int numHilo) {
		this.img = img;
		this.getNumHilo = numHilo;
	}
	
	public abstract void run();
	
	public int getAltura(){
		return altura;
	}
	
	public int getAnchura(){
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

}
