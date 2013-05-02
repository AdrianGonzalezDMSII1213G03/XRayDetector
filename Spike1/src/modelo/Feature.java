package modelo;

import ij.ImagePlus;
import ij.gui.Roi;


public abstract class Feature {
	
	private int step;
	private String selectedStep;
	private double[] vectorResultados;
	protected String[] headVector;

	public Feature(){
		
	}
	
	public Feature(String selectedStep, int step){
		this.selectedStep = selectedStep;
		this.step = step;
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
	
	public abstract void calcular(Roi roi, ImagePlus image, ImagePlus imageFd,
			ImagePlus imageSd);

	public String[] getHead() {
		return headVector;
	}
}
