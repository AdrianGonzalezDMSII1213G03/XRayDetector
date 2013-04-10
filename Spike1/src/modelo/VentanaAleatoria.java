package modelo;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Random;


public class VentanaAleatoria extends VentanaAbstracta {
	
	public ArrayList<int []> listaDefectos = new ArrayList<int []>();
	public ArrayList<int []> listaNoDefectos = new ArrayList<int []>();
	public VentanaAleatoria(ImagePlus img, ImagePlus saliency, ImagePlus convolucion, ImagePlus convolucionSaliency, int numHilo) {
		super(img, saliency, convolucion, convolucionSaliency, numHilo);
	}

	@Override
	public void run() {
		rellenarListas();
		//imprimeListas();
		seleccionarVentanas();		
		
	}

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

	public void seleccionarItemLista(ArrayList<int[]> lista, Random rand, boolean defect) {
		int randIndex = rand.nextInt(lista.size());
		int [] coordVentana = lista.get(randIndex);
		calcularCaracteristicas(coordVentana[0], coordVentana[1], defect);
		lista.remove(randIndex);
	}
	
	private void calcularCaracteristicas(int coordenadaX, int coordenadaY, boolean defect){
		
		ejecutarCalculos(coordenadaX, coordenadaY, getImagenCompleta());
		generarArff(coordenadaX, coordenadaY, defect);
	}
	
	

}
