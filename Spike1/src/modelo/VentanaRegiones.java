package modelo;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.JProgressBar;

import utils.Graphic;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class VentanaRegiones extends VentanaAbstracta {
	
	private List<int[]> listaPixeles;
	private Graphic imgPanel;
	private Rectangle selection;
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
		
		Iterator<int[]> it = listaPixeles.iterator();
		while(it.hasNext()){
			int[] coord = it.next();
			int coordX = (coord[0] - selection.x) - getAnchuraVentana()/2;
			int coordY = (coord[1] - selection.y) - getAlturaVentana()/2;
			
			if(coordX >= 0 && coordY >= 0 && coordX <= (getImage().getProcessor().getWidth() - getAnchuraVentana())
					&& coordY <= (getImage().getProcessor().getHeight() - getAlturaVentana())){
				pintarVentana(coordX, coordY);
				ip.setRoi(coordX, coordY, getAnchuraVentana(), getAlturaVentana());
				ejecutarCalculos(coordX, coordY, getImage());								
				double clase = clasificar();
				imprimeRes(coordX, coordY, clase);
			}
			setPorcentajeBarra();
		}
	}

	private synchronized void pintarVentana(int coordenadaX, int coordenadaY) {
		
		int y = coordenadaY + selection.y;
//		if(getNumHilo() == Runtime.getRuntime().availableProcessors() - 1){
//			y -= getPropiedades().getTamVentana();	//para contrarrestar el solapamiento y que las ventanas no se salgan de la selección
//		}

		imgPanel.drawWindow(coordenadaX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
		imgPanel.repaint();
	}
	
	private void imprimeRes(int coordX, int coordY, double prob) {
		
		//para la coordenada Y, hay que determinar en qué trozo de la imagen estamos analizando
		int y = coordY + selection.y;
//		if(getNumHilo() == Runtime.getRuntime().availableProcessors() - 1){
//			y -= getPropiedades().getTamVentana();	//para contrarrestar el solapamiento y que las ventanas no se salgan de la selección
//		}
		
		//CLASIFICACIÓN CLASE NOMINAL
		if(prob == 0){
			imgPanel.addRectangle(coordX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
			imgPanel.repaint();
			rellenarMatrizDefectos(coordX+ selection.x, y);
		}
		
		//REGRESIÓN
//		if(prob >= 0.5){
//			imgPanel.addRectangle(coordX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
//			imgPanel.repaint();
//			rellenarMatrizDefectos(coordX+ selection.x, y);
//		}
	}

}
