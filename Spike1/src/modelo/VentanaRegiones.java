package modelo;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.JProgressBar;

import utils.Graphic;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class VentanaRegiones extends VentanaAbstracta {
	
	private List<int[]> listaPixeles;
	private Graphic imgPanel;
	private Rectangle selection;
	private Roi[] arrayRois;
	
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
			
//			synchronized(this){
//			for(int i=0; i<arrayRois.length; i++){
//				imgPanel.addRectangle(arrayRois[i].getBounds().x + selection.x, arrayRois[i].getBounds().y + selection.y, arrayRois[i].getBounds().width, arrayRois[i].getBounds().height);
//				imgPanel.repaint();
//			}
//			}
			
			if(coordX >= 0 && coordY >= 0 && coordX <= (getImage().getProcessor().getWidth() - getAnchuraVentana())
					&& coordY <= (getImage().getProcessor().getHeight() - getAlturaVentana())){
				//comprobar a qué región pertenece el píxel
				int index = getIndexRoi(coordX, coordY);
				System.out.println("Coord0: " + coordX + " Coord1: " + coordY + " index: " + index);
				
				if(index != -1){				
					pintarVentana(coordX, coordY);
					ip.setRoi(coordX, coordY, getAnchuraVentana(), getAlturaVentana());
					ejecutarCalculos(coordX, coordY, getImage());								
					double clase = clasificar();
					imprimeRes(coordX, coordY, clase);
				}
				else{
					System.out.println("No encontrado");
				}
			}
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
	
	public void setArrayRois(Roi[] array){
		arrayRois = array;
	}
	
	public Roi[] getArrayRois(){
		return arrayRois;
	}
}
