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
 * SaliencyTest.java
 * Copyright (C) 2012 Alan Blanco Ã?lamo and Victor Barbero GarcÃ­a. Spain
 */

package es.ubu.XRayDetector.test.modelo.preprocesamiento;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import es.ubu.XRayDetector.modelo.preprocesamiento.Saliency;

import ij.ImagePlus;

/**
 * This class tests Saliency class.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class SaliencyTest {
	
	/**
	 * Instance of class Saliency.
	 */
	private static Saliency saliency;
	
	/**
	 * Example image.
	 */
	private static ImagePlus sal;
	
	/**
	 * Method that is executed when this class is created.
	 */
	@BeforeClass
	public static void setUpClass(){
		ImagePlus img = new ImagePlus("./res/test/img/ImgTESTFeature.BMP");
		saliency = new Saliency(img, 2);
		sal = new ImagePlus("", saliency.calcular());
	}
	
	/**
	 * Test for saliencyMap().
	 */
	@Test
	public void saliencyMapTest() {
		assertEquals(sal.getProcessor().getPixel(0, 0), 0);
		assertEquals(sal.getProcessor().getPixel(99, 89), 0);
		assertEquals(sal.getProcessor().getPixel(24, 46), 64);
		assertEquals(sal.getProcessor().getPixel(100, 50), 0);
		assertEquals(sal.getProcessor().getPixel(50, 100), 0);
		assertEquals(sal.getProcessor().getPixel(48, 84), 52);
		assertEquals(sal.getProcessor().getPixel(19, 44), 47);
		assertEquals(sal.getProcessor().getPixel(94, 82), 0);
		assertEquals(sal.getProcessor().getPixel(100, 100), 0);
	}

}
