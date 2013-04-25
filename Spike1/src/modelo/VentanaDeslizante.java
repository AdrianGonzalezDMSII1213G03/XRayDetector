package modelo;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Rectangle;

import javax.swing.JProgressBar;


import utils.Graphic;

public class VentanaDeslizante extends VentanaAbstracta{

	private Graphic imgPanel;
	private Rectangle selection;
	private boolean entrenamiento; 
	
	public VentanaDeslizante(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo, Rectangle sel, Graphic imgPanel, JProgressBar progressBar, int[][] defectMatrix, boolean entr) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);
		this.imgPanel = imgPanel;
		this.selection = sel;
		this.progressBar = progressBar;		
		this.defectMatrix = defectMatrix;
		entrenamiento = entr;
	}

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

	private synchronized void pintarVentana(int coordenadaX, int coordenadaY) {
		
		int y = coordenadaY + selection.y + getNumHilo()*getImage().getHeight();
		if(getNumHilo() == Runtime.getRuntime().availableProcessors() - 1){
			y -= getPropiedades().getTamVentana();	//para contrarrestar el solapamiento y que las ventanas no se salgan de la selección
		}

		imgPanel.drawWindow(coordenadaX + selection.x, y, getAnchuraVentana(), getAlturaVentana());
		imgPanel.repaint();
	}
	
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
