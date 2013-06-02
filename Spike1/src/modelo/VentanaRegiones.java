package modelo;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.swing.JProgressBar;

import utils.Graphic;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;

public class VentanaRegiones extends VentanaAbstracta {
	
	private List<int[]> listaPixeles;
	private Graphic imgPanel;
	private Rectangle selection;
	private Roi[] arrayRois;
	
	private static int MIN = 8;
	
	public VentanaRegiones(ImagePlus img, ImagePlus saliency,
			ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo, Rectangle selection, Graphic imgPanel, JProgressBar progressBar, int[][] defectMatrix, List<int[]> pixeles) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);

		listaPixeles = pixeles;
		this.imgPanel = imgPanel;
		this.selection = selection;
		this.progressBar = progressBar;		
		this.defectMatrix = defectMatrix;
	}

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
			
			int coordX = (coord[0] - selection.x - 15) - (getAnchuraVentana()/2);
			int coordY = (coord[1] - selection.y - 15) - (getAlturaVentana()/2);
			
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

	private synchronized void pintarVentana(int coordenadaX, int coordenadaY) {
		
		int y = coordenadaY + selection.y;

		imgPanel.drawWindow(coordenadaX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
		imgPanel.repaint();
	}
	
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
	
	public void setArrayRois(Roi[] array){
		arrayRois = array;
	}
	
	public Roi[] getArrayRois(){
		return arrayRois;
	}
}
