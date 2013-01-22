package modelo;

import java.awt.image.BufferedImage;

import ij.ImagePlus;

public abstract class Preprocesamiento {
	private ImagePlus image;
	private int type;

	public Preprocesamiento(ImagePlus image, int type){
		this.image = image;
		this.type = type;
	}
	
	public ImagePlus getImage(){
		return image;
	}
	
	public int getType(){
		return type;
	}
	
	public abstract BufferedImage calcular();
}
