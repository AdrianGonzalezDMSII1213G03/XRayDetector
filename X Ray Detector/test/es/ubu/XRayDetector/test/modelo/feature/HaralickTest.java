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
 * HaralickTest.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.test.modelo.feature;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import es.ubu.XRayDetector.modelo.feature.Haralick;
import ij.ImagePlus;
import ij.gui.Roi;

/**
 * Class that tests the methods of <b>Haralick</b>.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 *
 */
public class HaralickTest {
	
	/**
	 * Example image.
	 */
	private static ImagePlus img;
	
	/**
	 * Example ROI.
	 */
	private static Roi roi;
	
	/**
	 * Instance of Haralick class.
	 */
	private static Haralick har;
	
	/**
	 * Results vector.
	 */
	private static double[] haralickVector;
	
	/**
	 * Grey level co-ocurrence matrix.
	 */
	private static double[][] glcm;
	
	/**
	 * Empty constructor.
	 */
	public HaralickTest(){
		
	}
	
	/**
	 * Method that is executed when this class is created.
	 */
	@BeforeClass
	public static void setUpClass(){
		img = new ImagePlus("./res/test/img/ImgTESTFeature.BMP");
        roi = new Roi(0, 0, img.getWidth(), img.getHeight());
		har = new Haralick("0 degrees", 1);
		har.calcular(roi, img, null, null);
		haralickVector = har.getHaralickVector();
		glcm = har.getGlcm();
	}
	
	/**
	 * Method that tests the values of the glcm matrix.
	 */
	@Test
	public void glcmTest(){
		assertEquals(Math.round(glcm[0][0] * 100.0) / 100.0, 0.0, 0);
		assertEquals(Math.round(glcm[48][81] * 100.0) / 100.0, 0.0, 0);
		assertEquals(Math.round(glcm[90][35] * 100.0) / 100.0, 0.0, 0);
		assertEquals(Math.round(glcm[2][2] * 100.0) / 100.0, 0.0, 0);
	}
	
	/**
	 * Method that tests the angular second moment.
	 */
	@Test
	public void angularSecondMomentTest(){
		assertEquals(haralickVector[0], 0.00184057499999999, 0);
	}
	
	/**
	 * Test for contrast().
	 */
	@Test
	public void contrastTest(){
		assertEquals(haralickVector[1], 120.50009999999986, 0);
	}
	
	/**
	 * Test for correlation().
	 */
	@Test
	public void correlationTest(){
		assertEquals(haralickVector[2], 9.537341796286855E-4, 0);
	}
	
	/**
	 * Test for sumOfSquares().
	 */
	@Test
	public void sumOfSquaresTest(){
		assertEquals(haralickVector[3], 14860.284755143975, 0);
	}
	
	/**
	 * Test for inversDifferenceMoment().
	 */
	@Test
	public void inverseDifferenceMomentTest(){
		assertEquals(haralickVector[4], 0.39926055856274306, 0);
	}
	
	/**
	 * Test for sumAverage().
	 */
	@Test
	public void sumAverageTest(){
		assertEquals(haralickVector[5], 235.59250000000014, 0);
	}
	
	/**
	 * Test for sumEntropy().
	 */
	@Test
	public void sumEntropyTest(){
		assertEquals(haralickVector[6], 5.1992795882229235, 0);
	}
	
	/**
	 * Test for sumVariance().
	 */
	@Test
	public void sumVarianceTest(){
		assertEquals(haralickVector[7], 3687.0, 0);
	}
	
	/**
	 * Test for entropyTest().
	 */
	@Test
	public void entropyTest(){
		assertEquals(haralickVector[8], 6.707892008607002, 0);
	}
	
	/**
	 * Test for differenceVariance().
	 */
	@Test
	public void differenceVarianceTest(){
		assertEquals(haralickVector[9], 111.56777231000001, 0);
	}
	
	/**
	 * Test for differenceEntropy().
	 */
	@Test
	public void differenceEntropyTest(){
		assertEquals(haralickVector[10], 1.85172243005125, 0);
	}
	
	/**
	 * Test for imc_1().
	 */
	@Test
	public void imc_1Test(){
		assertEquals(haralickVector[11], -0.5167849235884179, 0);
	}
	
	/**
	 * Test imc_2().
	 */
	@Test
	public void imc_2Test(){
		assertEquals(haralickVector[12], 0.9953232966930922, 0);
	}
	
	/**
	 * Test maximalCorrelationCoefficient().
	 */
	@Test
	public void maximalCorrelationCoefficientTest(){
		//si está activado EJML
		//assertEquals(haralickVector[13], 0.035605798241495436, 0);
		
		//si no está activado EJML
		assertEquals(haralickVector[13], 0, 0);
	}

}
