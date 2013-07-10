/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * Feature.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.feature;

import ij.ImagePlus;
import ij.gui.Roi;


/**
 * Superclass of the <i>Strategy</i> pattern that controls the features.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public abstract class Feature {
	
	/**
	 * Step (Haralick).
	 */
	private int step;
	
	/**
	 * Selected step Haralick).
	 */
	private String selectedStep;
	
	/**
	 * Results.
	 */
	private double[] vectorResultados;
	
	/**
	 * Header vector.
	 */
	protected String[] headVector;

	/**
	 * Empty constructor.
	 */
	public Feature(){
		
	}
	
	/**
	 * Class constructor.
	 * @param selectedStep Selected step
	 * @param step step
	 */
	public Feature(String selectedStep, int step){
		this.selectedStep = selectedStep;
		this.step = step;
	}
	
	/**
	 * Gets the current step.
	 * @return Step
	 */
	public int getStep(){
		return step;
	}
	
	/**
	 * Gets the current selected step.
	 * @return Selected step
	 */
	public String getSelectedStep(){
		return selectedStep;
	}
	
	/**
	 * Method that returns the results.
	 * @return Results
	 * @see #setVectorResultados
	 */
	public double[] getVectorResultados(){
		return vectorResultados;
	}
	
	/**
	 * Method that sets a result vector.
	 * @param v Vector
	 * @see #getVectorResultados
	 */
	public void setVectorResultados(double[] v){
		vectorResultados = v;
	}
	
	/**
	 * Abstract method. It indicates what is going to calculate and how.
	 * @param roi ROI in image
	 * @param image Image
	 * @param imageFd First derivative image
	 * @param imageSd Second derivative image
	 */
	public abstract void calcular(Roi roi, ImagePlus image, ImagePlus imageFd,
			ImagePlus imageSd);

	/**
	 * Method that returns the header vector.
	 * @return Header vector
	 */
	public String[] getHead() {
		return headVector;
	}
}
