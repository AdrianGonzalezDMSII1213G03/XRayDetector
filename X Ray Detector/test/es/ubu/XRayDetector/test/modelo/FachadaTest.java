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
 * FachadaTest.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.test.modelo;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import ij.ImagePlus;

import java.awt.Rectangle;
import java.io.File;
import java.util.Arrays;

import javax.swing.JProgressBar;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.junit.Before;
import org.junit.Test;

import es.ubu.XRayDetector.modelo.Fachada;
import es.ubu.XRayDetector.utils.Graphic;
import es.ubu.XRayDetector.utils.Propiedades;


/**
 * Class that tests the methods of <b>Fachada</b>.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 *
 */
public class FachadaTest{

	/**
	 * Instance of Fachada.
	 */
	private static Fachada fachada;
	
	/**
	 * Constructor.
	 */
	public FachadaTest(){

	}
	
	/**
	 * Method executed before all tests.
	 */
	@Before
	public void setUp(){
		fachada = Fachada.getInstance();
	}
	
	/**
     * Method that tests the correct load of an image.
     */
	@Test
    public void testAbrirImagen(){
		assertEquals(fachada.cargaImagen("./res/test/img/ImgTEST.BMP"),"Imagen abierta correctamente. Bytes por pixel: 1");		
	}
	
	/**
     * Method that tests the incorrect load of an image.
     * @throws NullPointerException 
     */
	@Test (expected=NullPointerException.class)
    public void testAbrirImagenException() throws NullPointerException {
		assertNull(fachada.cargaImagen("./res/test/img/ImgTEST2.BMP"));
	}
	
	/**
     * Method that tests the extraction of the dimensions of a loaded image.
     */
	@Test
    public void testGetImagen(){
		fachada.cargaImagen("./res/test/img/ImgTEST.BMP");
		assertEquals(fachada.getImagen().getHeight(), 570);
		assertEquals(fachada.getImagen().getWidth(), 736);
	}
	
	/**
     * Test of the detection process, using a selection and the normal option.
     */
	@Test
    public void testAnalizarNormalSeleccion(){
		fachada.cargaImagen("./res/test/img/ImgTEST.BMP");
		File image = new File("./res/test/img/ImgTEST.BMP");
		ImagePlus img = new ImagePlus(image.getAbsolutePath());
		Graphic imgPanel = new Graphic(img.getImage());
		Rectangle sel = imgPanel.coordenates();
		sel.setBounds(0, 0, 20, 20);
		JProgressBar progressBar = new JProgressBar();
		fachada.ejecutaVentana(sel, imgPanel, progressBar);
	}
	
	/**
     * Test of the detection process, using a whole image with the pixels
     * list option.
     */
	@Test
    public void testAnalizarRegionesCompleta(){
		fachada.cargaImagen("./res/test/img/ImgTESTFeature.BMP");
		File image = new File("./res/test/img/ImgTESTFeature.BMP");
		ImagePlus img = new ImagePlus(image.getAbsolutePath());
		Graphic imgPanel = new Graphic(img.getImage());
		Rectangle sel = imgPanel.coordenates();
		sel.setBounds(0, 0, 0, 0);
		JProgressBar progressBar = new JProgressBar();
		fachada.ejecutaVentanaOpcionRegiones(sel, imgPanel, progressBar);
	}
	
	/**
     * Test of the detection process, using a selection and the pixels list
     * option.
     */
	@Test
    public void testAnalizarRegionesSeleccion(){
		fachada.cargaImagen("./res/test/img/ImgTEST.BMP");
		File image = new File("./res/test/img/ImgTEST.BMP");
		ImagePlus img = new ImagePlus(image.getAbsolutePath());
		Graphic imgPanel = new Graphic(img.getImage());
		Rectangle sel = imgPanel.coordenates();
		sel.setBounds(0, 0, 20, 20);
		JProgressBar progressBar = new JProgressBar();
		fachada.ejecutaVentanaOpcionRegiones(sel, imgPanel, progressBar);
	}
	
	/**
     * Test of the training process, using an existing ARFF file.
     */
	@Test
    public void testAnalizarEntrenamientoARFF(){
		Propiedades p = Propiedades.getInstance();
		p.setPathModel("/res/test/arff/");
		File arff = new File("/res/test/arff/Arff_entrenamientoTEST.arff");
		fachada.ejecutaEntrenamiento(arff, null);
		File f = new File("/res/test/arff/todas_arff_existente.model");
		assertTrue(f.exists());
		p.setPathModel("/res/model/todas_24.model");
	}
	
	/**
	 * Incorrect test of the training process, using a corrupted ARFF file.
	 */
	@Test (expected=RuntimeException.class)
    public void testAnalizarEntrenamientoARFFMalo(){
		File arff = new File("/res/test/arff/Arff_entrenamientoTESTmalo.arff");
		fachada.ejecutaEntrenamiento(arff, null);
	}
	
	/**
     * Test of the training process with a set of images.
     */
	@Test
    public void testAnalizarEntrenamientoConImagenes(){
		File originalDirectory = new File("/res/test/img/Entrenar Test/Originales");
		File maskDirectory = new File(originalDirectory.getAbsolutePath().replace("Originales", "Mascaras"));
		String originalList[] = originalDirectory.list();
		Arrays.sort(originalList);
		String maskList[] = maskDirectory.list();
		Arrays.sort(maskList);
		File[] originalFiles = originalDirectory.listFiles();
		File[] maskFiles = maskDirectory.listFiles();
		originalList = new String[originalFiles.length];
		maskList = new String[maskFiles.length];
		
		for(int i=0; i<originalFiles.length; i++){
			originalList[i] = originalFiles[i].getAbsolutePath();
			maskList[i] = maskFiles[i].getAbsolutePath();
		}
		
		JProgressBar progressBar = new JProgressBar();
		JTextPane txtLog = new JTextPane();
		HTMLEditorKit kit = new HTMLEditorKit();
	    HTMLDocument doc = new HTMLDocument();
	    Propiedades p = Propiedades.getInstance();
	    p.setPathModel("/res/test/arff/");
		fachada.ejecutarEntrenamientoDirectorio(originalList, maskList, progressBar, txtLog, kit, doc);
		File f = new File("/res/test/arff/todas_"+p.getTamVentana()+".model");
		assertTrue(f.exists());
		p.setPathModel("/res/model/todas_24.model");
	}
	
	/**
	 * Test of the training process with a set of images, using the sliding window option.
	 */
	@Test
    public void testEntrenamientoConImagenesDeslizante(){
		File originalDirectory = new File("/res/test/img/Entrenar Test/Originales");
		File maskDirectory = new File(originalDirectory.getAbsolutePath().replace("Originales", "Mascaras"));
		String originalList[] = originalDirectory.list();
		Arrays.sort(originalList);
		String maskList[] = maskDirectory.list();
		Arrays.sort(maskList);
		File[] originalFiles = originalDirectory.listFiles();
		File[] maskFiles = maskDirectory.listFiles();
		originalList = new String[originalFiles.length];
		maskList = new String[maskFiles.length];
		
		for(int i=0; i<originalFiles.length; i++){
			originalList[i] = originalFiles[i].getAbsolutePath();
			maskList[i] = maskFiles[i].getAbsolutePath();
		}
		
		JProgressBar progressBar = new JProgressBar();
		JTextPane txtLog = new JTextPane();
		HTMLEditorKit kit = new HTMLEditorKit();
	    HTMLDocument doc = new HTMLDocument();
	    Propiedades p = Propiedades.getInstance();
	    p.setPathModel("/res/test/arff/");
	    p.setTipoEntrenamiento(1);
	    p.setTipoVentanaDefectuosa(1);
	    p.setTamVentana(32);
	    p.setSalto(0.75);
		fachada.ejecutarEntrenamientoDirectorio(originalList, maskList, progressBar, txtLog, kit, doc);
		File f = new File("/res/test/arff/todas_"+p.getTamVentana()+".model");
		assertTrue(f.exists());
		p.setPathModel("/res/model/todas_24.model");
		p.setTipoEntrenamiento(0);
	    p.setTipoVentanaDefectuosa(0);
	    p.setTamVentana(12);
	    p.setSalto(0.2);
	}
	
	/**
     * Testo of the detection process, using a whole image and the normal option.
     */
	@Test
    public void testAnalizarNormalCompleta(){
		fachada.cargaImagen("./res/test/img/ImgTESTFeature.BMP");
		File image = new File("./res/test/img/ImgTESTFeature.BMP");
		ImagePlus img = new ImagePlus(image.getAbsolutePath());
		Graphic imgPanel = new Graphic(img.getImage());
		Rectangle sel = imgPanel.coordenates();
		sel.setBounds(0, 0, 0, 0);
		JProgressBar progressBar = new JProgressBar();
		fachada.ejecutaVentana(sel, imgPanel, progressBar);
	}
	
	/**
     * Test of the precision and recall metrics.
     */
	@Test
    public void testPrecisionRecall(){
		fachada.cargaImagen("./res/test/img/Entrenar Test/Originales/imagen integrada00_16_40.BMP");
		File image = new File("./res/test/img/Entrenar Test/Originales/imagen integrada00_16_40.BMP");
		ImagePlus img = new ImagePlus(image.getAbsolutePath());
		Graphic imgPanel = new Graphic(img.getImage());
		Rectangle sel = imgPanel.coordenates();
		sel.setBounds(0, 0, 20, 20);
		JProgressBar progressBar = new JProgressBar();
		fachada.ejecutaVentanaOpcionRegiones(sel, imgPanel, progressBar);
		File mask = new File("./res/test/img/Entrenar Test/Mascaras/imagen integrada00_16_40.jpg");
		Rectangle selectionCopy = new Rectangle();
		selectionCopy.height = sel.height;
		selectionCopy.width = sel.width;
		selectionCopy.x = sel.x;
		selectionCopy.y = sel.y;
		double[] pr = fachada.getPrecisionRecall(mask.getAbsolutePath(), selectionCopy);
		assertNotNull(pr);
		assertTrue(Double.isNaN(pr[0]));
		assertTrue(Double.isNaN(pr[1]));
	}
	
	/**
	 * Method that tests the retrieval of the stored exception.
	 */
	@Test
	public void testExcepcion(){
		fachada.setExcepcion(new RuntimeException("test"));
		assertEquals("test", fachada.getExcepcion().getMessage());
	}
	
	/**
	 * Method that tests the retrieval of the binarized defects image.
	 */
	@Test
	public void testImagenBinarizada(){
		assertNotNull(fachada.getImageBinarizada());
	}	
}
