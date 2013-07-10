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
 * VentanaDeslizante.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.ventana;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Rectangle;

import javax.swing.JProgressBar;

import es.ubu.XRayDetector.utils.Graphic;



/**
 * Window movement strategy. Sliding window.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @see VentanaAbstracta
 * @version 2.0
 */
public class VentanaDeslizante extends VentanaAbstracta{

	/**
	 * Component that stores the image shown on the interface.
	 */
	private Graphic imgPanel;
	
	/**
	 * Selection made by user.
	 */
	private Rectangle selection;
	
	/**
	 * Flag that indicates if the window is being used in the training
	 * process or in the detection process.
	 */
	private boolean entrenamiento; 
	
	/**
	 * Class constructor.
	 * @param img Original image
	 * @param saliency Saliency map image
	 * @param convolucion Image to perform the convolution operation
	 * @param convolucionSaliency Saliency map image to perform the convolution operation
	 * @param numHilo Number of thread
	 * @param sel User selection
	 * @param imgPanel Grphics instance with the image
	 * @param progressBar ProgressBar where the progress is going to be written
	 * @param defectMatrix Matrix indicating the number of windows that consider a pixel as faulty
	 * @param entr Training flag
	 */
	public VentanaDeslizante(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo, Rectangle sel, Graphic imgPanel, JProgressBar progressBar, int[][] defectMatrix, boolean entr) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);
		this.imgPanel = imgPanel;
		this.selection = sel;
		this.progressBar = progressBar;		
		this.defectMatrix = defectMatrix;
		entrenamiento = entr;
	}

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.ventana.VentanaAbstracta#run()
	 */
	public void run(){
		int salto = (int) (getAlturaVentana()*getPropiedades().getSalto());
		int coordenadaX = 0, coordenadaY = 0, altura = 0, anchura = 0;
		ImageProcessor ip = getImage().getProcessor();
		
		altura = ip.getHeight();
		anchura = ip.getWidth();
		
		for (coordenadaY = 0;coordenadaY <= altura - getAlturaVentana(); coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= anchura - getAnchuraVentana(); coordenadaX += salto) {
				if (entrenamiento){
					ip.setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
					ejecutarCalculos(coordenadaX, coordenadaY, getImage());
					boolean defect = getDefecto(getImage().duplicate());
					generarArff(coordenadaX, coordenadaY, defect);
				}
				else{
					pintarVentana(coordenadaX, coordenadaY);
					ip.setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
					
					ejecutarCalculos(coordenadaX, coordenadaY, getImage());
					double clase = clasificar();
					imprimeRes(coordenadaX, coordenadaY, clase);
					setPorcentajeBarra();
				}
			}
		}
	}

	/**
	 * Method that draws a rectangle (window) on the image (synchronized).
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY Y coordinate of the window
	 */
	private synchronized void pintarVentana(int coordenadaX, int coordenadaY) {
		
		int y = coordenadaY + selection.y + getNumHilo()*getImage().getHeight();
		if(getNumHilo() == Runtime.getRuntime().availableProcessors() - 1){
			y -= getPropiedades().getTamVentana();	//para contrarrestar el solapamiento y que las ventanas no se salgan de la selección
		}

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
		int y = coordY + selection.y + getNumHilo()*getImage().getHeight();
		if(getNumHilo() == Runtime.getRuntime().availableProcessors() - 1){
			y -= getPropiedades().getTamVentana();	//para contrarrestar el solapamiento y que las ventanas no se salgan de la selección
		}
		
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
}
