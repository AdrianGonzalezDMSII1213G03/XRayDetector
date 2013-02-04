package modelo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class VentanaAleatoria extends VentanaAbstracta {
	
	public ArrayList<int []> listaDefectos = new ArrayList<int []>();
	public ArrayList<int []> listaNoDefectos = new ArrayList<int []>();

	public VentanaAleatoria(ImagePlus img, int numHilo) {
		super(img, numHilo);
	}

	@Override
	public void run() {
		rellenarListas();
		imprimeListas();
	}

	private void imprimeListas() {
		Iterator<int []> it = listaDefectos.iterator();
		while(it.hasNext()){
			int[] coordCentro = it.next();
			System.out.println("Coordenada defecto en hilo " + getNumHilo() + "[" + coordCentro[0] + "," + coordCentro[1] + "]");
		}

		Iterator<int []> it2 = listaNoDefectos.iterator();
		while(it2.hasNext()){
			int[] coordCentro = it2.next();
			System.out.println("Coordenada no defecto en hilo " + getNumHilo() + "[" + coordCentro[0] + "," + coordCentro[1] + "]");
		}
	}

	private void rellenarListas() {
		int salto = (int) (getAltura()*0.8);
		int coordenadaX, coordenadaY;
		ImageProcessor ip = getImage().getProcessor();
		
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - getAltura(); coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - getAnchura(); coordenadaX += salto) {
				getImage().setRoi(coordenadaX, coordenadaY, getAnchura(), getAltura());
				int[] coordCentro = new int[]{(int)ip.getRoi().getCenterX(), (int)ip.getRoi().getCenterY()};
				
				if(getDefecto(getImage().duplicate())){
					listaDefectos.add(coordCentro);
				}
				else{
					listaNoDefectos.add(coordCentro);
				}
			}
		}
	}
	
	/**
	 * This method check in the mask if the region of interest analyzed has a
	 * colored defect. It returns true if the region has defects. If there is no
	 * defects, it returns false.
	 * 
	 * @param img
	 *            mask with colored defects
	 * @return true if has defects, false if not
	 */
	private boolean getDefecto(ImagePlus img){

		//boolean defect = false;
		int defectVector[];

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				defectVector = img.getPixel(x, y);
				//System.out.println("Valor pixel: [" + defectVector[1] + "," + defectVector[2] + "," + defectVector[3] + "]");
				// El valor 0 del vector, guarda el valor en escala de grises.
				// Por eso no nos interesa.
				if ((defectVector[1] != 255 && defectVector[1] != 0)
						|| (defectVector[2] != 255 && defectVector[2] != 0)) {
					return true;
				}
			}
		}

		return false;
	}

}
