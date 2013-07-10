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
 * VentanaRegiones.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.ventana;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.swing.JProgressBar;

import es.ubu.XRayDetector.utils.Graphic;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;

/**
 * Window movement strategy. List of white pixels in local thresholding image.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @see VentanaAbstracta
 * @version 1.0
 */
public class VentanaRegiones extends VentanaAbstracta {
	
	/**
	 * Lis of white pixels.
	 */
	private List<int[]> listaPixeles;
	/**
	 * Component that stores the image shown on the interface.
	 */
	private Graphic imgPanel;
	
	/**
	 * Selection made by user.
	 */
	private Rectangle selection;
	
	/**
	 * ROIs that the threshloding process has identified.
	 */
	private Roi[] arrayRois;
	
	/**
	 * Minimum pixels of ROI that are going to be considered.
	 */
	private static int MIN = 8;
	
	/**
	 * Class constructor.
	 * @param img Original image
	 * @param saliency Saliency map image
	 * @param convolucion Image to perform the convolution operation
	 * @param convolucionSaliency Saliency map image to perform the convolution operation
	 * @param numHilo Number of thread
	 * @param selection User selection
	 * @param imgPanel Graphics instance with the image
	 * @param progressBar ProgressBar where the progress is going to be written
	 * @param defectMatrix Matrix indicating the number of windows that consider a pixel as faulty
	 * @param pixeles List of white pixels
	 */
	public VentanaRegiones(ImagePlus img, ImagePlus saliency,
			ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo, Rectangle selection, Graphic imgPanel, JProgressBar progressBar, int[][] defectMatrix, List<int[]> pixeles) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);

		listaPixeles = pixeles;
		this.imgPanel = imgPanel;
		this.selection = selection;
		this.progressBar = progressBar;		
		this.defectMatrix = defectMatrix;
	}

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.ventana.VentanaAbstracta#run()
	 */
	@Override
	public void run() {
		ImageProcessor ip = getImage().getProcessor();
		Hashtable <Integer, Integer> tablaPixelsPorRoi = new Hashtable<Integer, Integer>();
		Hashtable <Integer, Integer> tablaPixelsConsideradosRoi = new Hashtable<Integer, Integer>();
		Random rand = new Random();
		
		for (int i = 0; i < arrayRois.length; i++ ){
			ip.setRoi(arrayRois[i]);
			Analyzer an = new Analyzer(getImage());
			an.measure();			
			ResultsTable rt = Analyzer.getResultsTable();
			int numPixelPorRoi = (int) (rt.getValueAsDouble(ResultsTable.AREA, 0) * 0.05);
			if (numPixelPorRoi < MIN){
				numPixelPorRoi = MIN;
			}
			tablaPixelsPorRoi.put(i, numPixelPorRoi);
			tablaPixelsConsideradosRoi.put(i, 0);
			ip.resetRoi();
		}
		
		Collections.shuffle(listaPixeles);
		
		while(!listaPixeles.isEmpty()){
			int randIndex = rand.nextInt(listaPixeles.size());
			int[] coord = listaPixeles.get(randIndex);
			
			int coordX = (coord[0] - selection.x) - (getAnchuraVentana()/2);
			int coordY = (coord[1] - selection.y) - (getAlturaVentana()/2);
			
			if(coordX >= 0 && coordY >= 0 && coordX <= (getImage().getProcessor().getWidth() - getAnchuraVentana())
					&& coordY <= (getImage().getProcessor().getHeight() - getAlturaVentana())){
				//comprobar a qué región pertenece el píxel
				int index = getIndexRoi(coordX, coordY);
				
				if(index != -1){
					if(tablaPixelsConsideradosRoi.get(index) < tablaPixelsPorRoi.get(index)){
						pintarVentana(coordX, coordY);
						ip.setRoi(coordX, coordY, getAnchuraVentana(), getAlturaVentana());
						ejecutarCalculos(coordX, coordY, getImage());								
						double clase = clasificar();
						imprimeRes(coordX, coordY, clase);
						tablaPixelsConsideradosRoi.put(index, (tablaPixelsConsideradosRoi.get(index))+1);
					}
				}
			}
			listaPixeles.remove(randIndex);
			setPorcentajeBarra();
		}
	}

	/**
	 * Method that determines the ROI containing the pixel.
	 * @param coordX X coordinate of the pixel
	 * @param coordY Y coordinate of the pixel
	 * @return Index of the ROI in the array
	 */
	private int getIndexRoi(int coordX, int coordY) {
		Roi roi;
		for(int i=0; i<arrayRois.length; i++){
			roi = arrayRois[i];
			if(roi.contains(coordX, coordY)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Method that draws a rectangle (window) on the image (synchronized).
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 */
	private synchronized void pintarVentana(int coordenadaX, int coordenadaY) {
		
		int y = coordenadaY + selection.y;

		imgPanel.drawWindow(coordenadaX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
		imgPanel.repaint();
	}
	
	/**
	 * Method that draws a green rectangle (window) on the image (synchronized).
	 * @param coordX X coordinate of the window
	 * @param coordY Y coordinate of the window
	 * @param prob Classification result
	 */
	private void imprimeRes(int coordX, int coordY, double prob) {
		
		//para la coordenada Y, hay que determinar en qué trozo de la imagen estamos analizando
		int y = coordY + selection.y;
		
		int opcion = getPropiedades().getTipoClasificacion();
		
		switch (opcion) {
			case 0:
				//CLASIFICACIÓN CLASE NOMINAL
				if(prob == 0){
					imgPanel.addRectangle(coordX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
					imgPanel.repaint();
					rellenarMatrizDefectos(coordX+ selection.x, y);
				}
				break;
	
			case 1:
				//REGRESIÓN
				if(prob >= 0.5){
					imgPanel.addRectangle(coordX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
					imgPanel.repaint();
					rellenarMatrizDefectos(coordX+ selection.x, y);
				}
				break;
		}
	}
	
	/**
	 * Method that sets the array of ROIs.
	 * @param array Array of ROIs
	 * @see #getArrayRois
	 */
	public void setArrayRois(Roi[] array){
		arrayRois = array;
	}
	
	/**
	 * Method that retrieves the array of ROIs.
	 * @return Array of ROIs
	 * @see #setArrayRois
	 */
	public Roi[] getArrayRois(){
		return arrayRois;
	}
}
