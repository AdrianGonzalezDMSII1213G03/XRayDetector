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
 * PropiedadesTest.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import es.ubu.XRayDetector.utils.Propiedades;

/**
 * Method that tests the storage and retrieval of the properties.
 * Test of the methods in <b>Propiedades</b>.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class PropiedadesTest {
	
	/**
	 * Instance of the properties class.
	 */
	private static Propiedades prop;
	
	/**
	 * Method that is executed when this class is created.
	 */
	@BeforeClass
	public static void setUpClass(){
		prop = Propiedades.getInstance();
	}
	
	/**
	 * Method that tests getInstance().
	 */
	@Test
	public void getInstanceTest(){
		Propiedades p = Propiedades.getInstance();
		assertTrue(p != null);
		assertEquals(p, prop);
	}
	
	/**
	 * Method that tests the value of the threshold.
	 */
	@Test
	public void umbralTest(){
		int u = 10;
		prop.setUmbral(u);
		assertEquals(u, prop.getUmbral());
	}
	
	/**
	 * Method that tests the training type value.
	 */
	@Test
	public void tipoEntrenamientoTest(){
		int u = 0;
		prop.setTipoEntrenamiento(u);
		assertEquals(u, prop.getTipoEntrenamiento());
	}
	
	/**
	 * Method that tests the size of the window.
	 */
	@Test
	public void tamVentanaTest(){
		int u = 12;
		prop.setTamVentana(u);
		assertEquals(u, prop.getTamVentana());
	}
	
	/**
	 * Method that tests the value of the window movement.
	 */
	@Test
	public void saltoTest(){
		double u = 0.2;
		prop.setSalto(u);
		assertEquals(u, prop.getSalto(), 1);
	}
	
	/**
	 * Method that tests the detection type value.
	 */
	@Test
	public void tipoDeteccionTest(){
		int u = 0;
		prop.setTipoDeteccion(u);
		assertEquals(u, prop.getTipoDeteccion());
	}
	
	/**
	 * Method that tests the faulty window heuristic value.
	 */
	@Test
	public void tipoVentanaDefectuosaTest(){
		int u = 0;
		prop.setTipoVentanaDefectuosa(u);
		assertEquals(u, prop.getTipoVentanaDefectuosa());
	}
	
	/**
	 * Method that tests the percentage value.
	 */
	@Test
	public void porcentajePixelsTest(){
		float u = (float) 50.0;
		prop.setPorcentajePixeles(u);
		assertEquals(u, prop.getPorcentajePixeles(), 0);
	}
	
	/**
	 * Method that tests the characteristics type value.
	 */
	@Test
	public void tipoCaracteristicasTest(){
		int u = 0;
		prop.setTipoCaracteristicas(u);
		assertEquals(u, prop.getTipoCaracteristicas());
	}
	
	/**
	 * Method that tests the path to the ARFF files.
	 */
	@Test
	public void pathArffTest(){
		String pathArff = "./res/arff/Arff_entrenamiento.arff";
		assertEquals(pathArff, prop.getPathArff());
	}
	
	/**
	 * Method that tests the path to the model files.
	 */
	@Test
	public void pathModelTest(){
		String pathModel = "./res/model/todas_24.model";
		prop.setPathModel(pathModel);
		assertEquals(pathModel, prop.getPathModel());
	}

}
