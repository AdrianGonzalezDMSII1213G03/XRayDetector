package modelo;

import ij.ImagePlus;


public abstract class Feature {
	
	private ImagePlus image;

	public Feature(ImagePlus image){
		this.image = image;
	}
		
	public ImagePlus getImage(){
		return image;
	}
	
	public abstract void calcular();
	
}
