package modelo;

import ij.ImagePlus;


public abstract class Feature {
	
	private ImagePlus image;
	private int step;
	private String selectedStep;
	private double[] vectorResultados;
	private ImagePlus imagenConvolucion;

	public Feature(ImagePlus image){
		this.image = image;
	}
	
	public Feature(ImagePlus image, String selectedStep, int step){
		this.image = image;
		this.selectedStep = selectedStep;
		this.step = step;
	}
		
	public ImagePlus getImagen(){
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
	
	public void setImagenConvolucion(ImagePlus img){
		imagenConvolucion = img;
	}
	
	public ImagePlus getImagenConvolucion(){
		return imagenConvolucion;
	}
	
	public abstract void calcular();
	public abstract String[] getHead();
	
}
