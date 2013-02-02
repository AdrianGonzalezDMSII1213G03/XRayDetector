package modelo;

import ij.ImagePlus;


public abstract class Feature {
	
	private ImagePlus image;
	private int step;
	private String selectedStep;
	private double[] vectorResultados;
	private ImagePlus imagenCompleta;

	public Feature(ImagePlus image){
		this.image = image;
	}
	
	public Feature(ImagePlus image, String selectedStep, int step){
		this.image = image;
		this.selectedStep = selectedStep;
		this.step = step;
	}
		
	public ImagePlus getImage(){
		return image;
	}
	
	public int getStep(){
		return step;
	}
	
	public String getSelectedStep(){
		return selectedStep;
	}
	
	public double[] getVectorResultados(){
		return vectorResultados;
	}
	
	public void setVectorResultados(double[] v){
		vectorResultados = v;
	}
	
	public void setImagenCompleta(ImagePlus img){
		imagenCompleta = img;
	}
	
	public ImagePlus getImagenCompleta(){
		return imagenCompleta;
	}
	
	public abstract void calcular();
	public abstract String[] getHead();
	
}
