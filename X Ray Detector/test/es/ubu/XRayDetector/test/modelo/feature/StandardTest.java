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
 * LbpTest.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.test.modelo.feature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import es.ubu.XRayDetector.modelo.feature.Standard;
import es.ubu.XRayDetector.utils.Differentials_;
import ij.ImagePlus;
import ij.gui.Roi;

/**
 * Class that tests the methods of <b>Standard</b>.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 *
 */
public class StandardTest{

	/**
	 * Example image.
	 */
	private ImagePlus img;
	
	/**
	 * Example ROI.
	 */
	private Roi roi;
	
	/**
	 * Instance of the Standard class.
	 */
	private Standard ftStandard;
	
	/**
	 * Instance of the Differentials class.
	 */
	private Differentials_ diff;
	
	/**
	 * Constructor.
	 */
	public StandardTest(){
		
	}
	
	/**
	 * Method that is executed before every test.
	 * @throws Exception if an error occurs when opening the image
	 */
	@Before
    public void setUp() throws Exception {
        img = new ImagePlus("./res/test/img/ImgTESTFeature.BMP");
        roi = new Roi(0, 0, img.getWidth(), img.getHeight());
        ftStandard = new Standard();
        diff = new Differentials_();
    }
	
	/**
	 * Method that tests the value of mean.
	 */
	@Test
	public void meanTest(){
		assertEquals(ftStandard.mean(roi, img), 118.5989, 0);
	}
	
	/**
	 * Method that tests the value of standard deviation.
	 */
	@Test
	public void stdDeviationTest(){
		assertEquals(ftStandard.standardDeviation(roi, img), 30.429449860127214, 0);
	}
	
	/**
	 * Method that tests the first derivative value.
	 */
	@Test
	public void firstDerivativeTest(){
		diff.setImp(img.duplicate());
		Differentials_.setOperation(Differentials_.GRADIENT_MAGNITUDE);
		diff.run("");
		assertEquals(ftStandard.firstDerivative(roi, diff.getImp()), 1.0755781807221E9, 0);
	}
	
	/**
	 * Method that tests the second derivative value.
	 */
	@Test
	public void secondDerivativeTest(){
		diff.setImp(img.duplicate());
		Differentials_.setOperation(Differentials_.LAPLACIAN);
		diff.run("");
		assertEquals(ftStandard.secondDerivative(roi, diff.getImp()), 2.24500320928E7, 0);
	}

}
