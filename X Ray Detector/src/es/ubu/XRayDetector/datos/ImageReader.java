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
 * ImageReader.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */
package es.ubu.XRayDetector.datos;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;

/**
 * Class ImageReader
 * 
 * Class for load images at the application.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class ImageReader {
	
	/**
	 * The image.
	 */
	private ImagePlus imagen;

	/**
	 * Opens an image from the specified Path.
	 * 
	 * @param url Path to the image file
	 * @return Bytes per pixel
	 */
	public int abrirImagen(String url){
		Opener opener = new Opener();
		File f = new File(url);
		imagen = opener.openImage(f.getAbsolutePath());
		return imagen.getBytesPerPixel();
	}
	
	/**
	 * Gets the new opened image.
	 * 
	 * @return The opened image.
	 */
	public ImagePlus getImagen(){
		return imagen;
	}
}
