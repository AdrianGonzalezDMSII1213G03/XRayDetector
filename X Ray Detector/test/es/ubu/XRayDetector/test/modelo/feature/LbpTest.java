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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import es.ubu.XRayDetector.modelo.feature.Lbp;
import ij.ImagePlus;
import ij.gui.Roi;

/**
 * Class that tests the methods of <b>Lbp</b>.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 *
 */
public class LbpTest {
	
	/**
	 * Example image.
	 */
	private ImagePlus img;
	
	/**
	 * Example ROI.
	 */
	private Roi roi;
	
	/**
	 * Instance of the Lbp class.
	 */
	private Lbp lbp;
	
	/**
	 * Constructor.
	 */
	public LbpTest(){
		
	}
	
	/**
	 * Method that is executed before every test.
	 * @throws Exception if an error occurs when opening the image
	 */
	@Before
    public void setUp() throws Exception {
        img = new ImagePlus("./res/test/img/ImgTESTFeature.BMP");
        roi = new Roi(0, 0, img.getWidth(), img.getHeight());
        lbp = new Lbp();
    }
	
	/**
	 * Method that tests the values of the 59 LBPs.
	 */
	@Test
	public void lbpTest(){
		double[] lbpVector = lbp.getHistogram(roi, img);
		
		assertEquals(lbpVector[0], 181.0, 0);
		assertEquals(lbpVector[1], 53.0, 0);
		assertEquals(lbpVector[2], 7.0, 0);
		assertEquals(lbpVector[3], 57.0, 0);
		assertEquals(lbpVector[4], 108.0, 0);
		assertEquals(lbpVector[5], 92.0, 0);
		assertEquals(lbpVector[6], 187.0, 0);
		assertEquals(lbpVector[7], 12.0, 0);
		assertEquals(lbpVector[8], 40.0, 0);
		assertEquals(lbpVector[9], 68.0, 0);
		assertEquals(lbpVector[10], 158.0, 0);
		assertEquals(lbpVector[11], 41.0, 0);
		assertEquals(lbpVector[12], 32.0, 0);
		assertEquals(lbpVector[13], 59.0, 0);
		assertEquals(lbpVector[14], 104.0, 0);
		assertEquals(lbpVector[15], 134.0, 0);
		assertEquals(lbpVector[16], 13.0, 0);
		assertEquals(lbpVector[17], 56.0, 0);
		assertEquals(lbpVector[18], 48.0, 0);
		assertEquals(lbpVector[19], 76.0, 0);
		assertEquals(lbpVector[20], 77.0, 0);
		assertEquals(lbpVector[21], 109.0, 0);
		assertEquals(lbpVector[22], 116.0, 0);
		assertEquals(lbpVector[23], 107.0, 0);
		assertEquals(lbpVector[24], 260.0, 0);
		assertEquals(lbpVector[25], 279.0, 0);
		assertEquals(lbpVector[26], 136.0, 0);
		assertEquals(lbpVector[27], 72.0, 0);
		assertEquals(lbpVector[28], 59.0, 0);
		assertEquals(lbpVector[29], 23.0, 0);
		assertEquals(lbpVector[30], 57.0, 0);
		assertEquals(lbpVector[31], 99.0, 0);
		assertEquals(lbpVector[32], 7.0, 0);
		assertEquals(lbpVector[33], 4.0, 0);
		assertEquals(lbpVector[34], 166.0, 0);
		assertEquals(lbpVector[35], 173.0, 0);
		assertEquals(lbpVector[36], 115.0, 0);
		assertEquals(lbpVector[37], 138.0, 0);
		assertEquals(lbpVector[38], 164.0, 0);
		assertEquals(lbpVector[39], 228.0, 0);
		assertEquals(lbpVector[40], 151.0, 0);
		assertEquals(lbpVector[41], 78.0, 0);
		assertEquals(lbpVector[42], 127.0, 0);
		assertEquals(lbpVector[43], 190.0, 0);
		assertEquals(lbpVector[44], 158.0, 0);
		assertEquals(lbpVector[45], 176.0, 0);
		assertEquals(lbpVector[46], 110.0, 0);
		assertEquals(lbpVector[47], 427.0, 0);
		assertEquals(lbpVector[48], 264.0, 0);
		assertEquals(lbpVector[49], 201.0, 0);
		assertEquals(lbpVector[50], 84.0, 0);
		assertEquals(lbpVector[51], 331.0, 0);
		assertEquals(lbpVector[52], 257.0, 0);
		assertEquals(lbpVector[53], 206.0, 0);
		assertEquals(lbpVector[54], 149.0, 0);
		assertEquals(lbpVector[55], 79.0, 0);
		assertEquals(lbpVector[56], 103.0, 0);
		assertEquals(lbpVector[57], 762.0, 0);
		assertEquals(lbpVector[58], 1836.0, 0);
		
	}
}
