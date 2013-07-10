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
 * VentanaAleatoria.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.modelo.ventana;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Random;


/**
 * Window movement strategy. Random window.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @see VentanaAbstracta
 * @version 2.0
 */
public class VentanaAleatoria extends VentanaAbstracta {
	
	/**
	 * List of faulty pixels.
	 */
	public ArrayList<int []> listaDefectos = new ArrayList<int []>();
	
	/**
	 * List of non-faulty pixels.
	 */
	public ArrayList<int []> listaNoDefectos = new ArrayList<int []>();
	
	
	/**
	 * Class constructor.
	 * @param img Original image
	 * @param saliency Saliency map image
	 * @param convolucion Image to perform the convolution operation
	 * @param convolucionSaliency Saliency map image to perform the convolution operation
	 * @param numHilo Number of thread
	 */
	public VentanaAleatoria(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);
	}

	/* (non-Javadoc)
	 * @see es.ubu.XRayDetector.modelo.ventana.VentanaAbstracta#run()
	 */
	@Override
	public void run() {
		rellenarListas();
		//imprimeListas();
		seleccionarVentanas();		
		
	}

	/**
	 * Method that analyzes the image and fills the two list of pixels.
	 */
	private void rellenarListas() {
		int salto = (int) (getAlturaVentana()*getPropiedades().getSalto());
		int coordenadaX, coordenadaY;
		ImageProcessor ip = getImage().getProcessor();
		
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - getAlturaVentana(); coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - getAnchuraVentana(); coordenadaX += salto) {
				getImage().setRoi(coordenadaX, coordenadaY, getAnchuraVentana(), getAlturaVentana());
				int[] coordVentana = new int[]{(int)coordenadaX, (int)coordenadaY};
				
				if(getDefecto(getImage().duplicate())){
					listaDefectos.add(coordVentana);
				}
				else{
					listaNoDefectos.add(coordVentana);
				}
			}
		}
	}
	
	/**
	 * Method that retrieves pixels randomly from the two list of pixels
	 * and calculates features.
	 */
	private void seleccionarVentanas(){
		int nAttemps = 150;
		ArrayList<int []> copiaListaDefectos = listaDefectos;
		ArrayList<int []> copiaListaNoDefectos = listaNoDefectos;
		Random rand = new Random();
		
		for(int i=0; i < nAttemps; i++){
			if(copiaListaDefectos.size() > 0 && copiaListaNoDefectos.size() > 0){
				int cola = rand.nextInt(10);
				//System.out.println("\tCola: " + cola);
				if(cola <= 5){
					seleccionarItemLista(copiaListaDefectos, rand, true);
				}
				else{
					seleccionarItemLista(copiaListaNoDefectos, rand, false);
				}
			}
			else if(copiaListaDefectos.size() > 0){
				seleccionarItemLista(copiaListaDefectos, rand, true);
			}
			else if (copiaListaNoDefectos.size() > 0){
				seleccionarItemLista(copiaListaNoDefectos, rand, false);
			}
		}
	}

	/**
	 * Method that retrieves one pixel of the specified list.
	 * @param lista List of pixels
	 * @param rand Random number generator
	 * @param defect Indicates if the pixel is faulty or not
	 */
	public void seleccionarItemLista(ArrayList<int[]> lista, Random rand, boolean defect) {
		int randIndex = rand.nextInt(lista.size());
		int [] coordVentana = lista.get(randIndex);
		calcularCaracteristicas(coordVentana[0], coordVentana[1], defect);
		lista.remove(randIndex);
	}
	
	/**
	 * Method that performs the feature calculation.
	 * @param coordenadaX X coordinate of the window
	 * @param coordenadaY X coordinate of the window
	 * @param defect If the pixel is faulty or not
	 */
	private void calcularCaracteristicas(int coordenadaX, int coordenadaY, boolean defect){
		
		ejecutarCalculos(coordenadaX, coordenadaY, getImagenCompleta());
		generarArff(coordenadaX, coordenadaY, defect);
	}
	
	

}
