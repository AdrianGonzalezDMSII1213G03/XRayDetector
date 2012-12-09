package modelo;

import ij.gui.Roi;

public abstract class Feature {
	
	private Roi roi;

	public Feature(Roi roi){
		this.roi = roi;
	}
	
	public Roi getRoi(){
		return roi;
	}
	
	public abstract void calcular();
	
}
