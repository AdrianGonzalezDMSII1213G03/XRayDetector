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
 * TestSuite.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */
 
 package es.ubu.XRayDetector.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.ubu.XRayDetector.test.modelo.FachadaTest;
import es.ubu.XRayDetector.test.modelo.feature.HaralickTest;
import es.ubu.XRayDetector.test.modelo.feature.LbpTest;
import es.ubu.XRayDetector.test.modelo.feature.StandardTest;
import es.ubu.XRayDetector.test.modelo.preprocesamiento.SaliencyTest;
import es.ubu.XRayDetector.test.utils.PropiedadesTest;

/**
 * Class that launches all of the project tests.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
@RunWith(Suite.class)
@SuiteClasses( { PropiedadesTest.class, StandardTest.class, LbpTest.class, HaralickTest.class, SaliencyTest.class, FachadaTest.class })
public class TestSuite {

}
