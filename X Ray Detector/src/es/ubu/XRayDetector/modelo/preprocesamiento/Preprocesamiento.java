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
 * Preprocesamiento.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.preprocesamiento;

import java.awt.image.BufferedImage;

import ij.ImagePlus;

/**
 * Class Preprocesamiento.
 * 
 * Abstract class for the common preprocessing methods.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public abstract class Preprocesamiento {
	/**
	 * Image to be preprocessed.
	 */
	private ImagePlus image;
	/**
	 * Preprocessing type.
	 */
	private int type;

	/**
	 * Constructs a Preprocesamiento object.
	 *
	 * @param image  the image to preprocess
	 * @param type   the preprocessing type
	 */
	public Preprocesamiento(ImagePlus image, int type){
		this.image = image;
		this.type = type;
	}
	
	/**
	 * The image tp be preprocessed.
	 * 
	 * @return the preprocessed image.
	 */
	public ImagePlus getImage(){
		return image;
	}
	
	/**
	 * The type of preprocessing.
	 * 
	 * @return The type of preprocessin used.
	 */
	public int getType(){
		return type;
	}
	
	/**
	 * Executes the preprocessing task.
	 * 
	 * @return The preprocessed image.
	 */
	public abstract BufferedImage calcular();
}
