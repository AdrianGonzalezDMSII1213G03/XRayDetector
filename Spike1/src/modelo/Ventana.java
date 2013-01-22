package modelo;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.awt.Color;

public class Ventana extends Thread{

	private int altura = 8;
	private int anchura = 8;
	//private FileReader fileReader;
	private ImagePlus img;
	private ImagePlus copiaImagen;
	
	
	public Ventana(ImagePlus img) {
		this.img = img;
		copiaImagen = this.img.duplicate();
		IJ.run(copiaImagen, "RGB Color", "");
		IJ.setForegroundColor(0, 255, 121);
	}

	@SuppressWarnings("static-access")
	public void run(){;
		int salto = (int) (altura*0.8);
		int coordenadaX, coordenadaY, color = 0;
		Color c = null;
		ImageProcessor ip = img.getProcessor();
		for (coordenadaY = 0; coordenadaY <= ip.getHeight() - altura; coordenadaY += salto) {
			for (coordenadaX = 0; coordenadaX <= ip.getWidth() - anchura; coordenadaX += salto) {
				ip.setRoi(coordenadaX, coordenadaY, anchura, altura);
				copiaImagen.setRoi(coordenadaX, coordenadaY, anchura, altura);
				switch(color){
					case 0:
						c = c.BLUE;
						color = 1;
						break;
					case 1:
						c = c.RED;
						color = 2;
						break;
					case 2:
						c = c.GREEN;
						color = 0;
						break;
				}
				cambiaColor(c);
				dibujaRoi();
				
				Feature ft = new Standard(img);
				ft.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ft.calcular();
				
				Feature ft2 = new Lbp(img);
				ft2.getImage().setRoi(coordenadaX, coordenadaY, anchura, altura);
				ft2.calcular();
			}
		}
		guardaCopia();
	}
	
	public void dibujaRoi(){
		copiaImagen.getProcessor().draw(copiaImagen.getRoi());
	}
	
	public void cambiaColor(Color c){
		copiaImagen.getRoi().setFillColor(c);
		copiaImagen.getProcessor().setColor(c);
	}
	
	public void guardaCopia(){
		IJ.saveAs(copiaImagen, "BMP", "./res/img/" + img.getTitle() + "_copia");
	}
}
