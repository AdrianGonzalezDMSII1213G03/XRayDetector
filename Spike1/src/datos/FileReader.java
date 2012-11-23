package datos;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.awt.Color;
import java.io.File;


public class FileReader {
	
	private ImagePlus imagen;
	private ImagePlus copiaImagen;
	private ImageProcessor ip;

	public int abrirImagen(String url){
		Opener opener = new Opener();
		File f = new File(url);
		imagen = opener.openImage(f.getAbsolutePath());
		ip = imagen.getProcessor();
		copiaImagen = imagen.duplicate();
		IJ.run(copiaImagen, "RGB Color", "");
		IJ.setForegroundColor(0, 255, 121);
		return imagen.getBytesPerPixel();
	}
	
	public int getAnchura(){
		return imagen.getWidth();
	}
	
	public int getAltura(){
		return imagen.getHeight();
	}
	
	public void setRoi(int coordX, int coordY, int anchura, int altura){
		imagen.setRoi(coordX, coordY, anchura, altura);
		copiaImagen.setRoi(coordX, coordY, anchura, altura);
	}
	
	public Roi getRoi(){
		return copiaImagen.getRoi();
	}
	
	public void dibujaRoi(){
		copiaImagen.getProcessor().draw(copiaImagen.getRoi());
	}
	
	public void guardaCopia(){
		IJ.saveAs(copiaImagen, "BMP", "./res/img/copia.BMP");
	}
	
	public void cambiaColor(Color c){
		copiaImagen.getRoi().setFillColor(c);
		copiaImagen.getProcessor().setColor(c);
	}
	
	public void getPixelCentralRoi(){
		//System.out.println("MCPIXEL X: "+ imagen.getRoi().getBounds().getCenterX() + "\tMCPIXEL Y: " + imagen.getRoi().getBounds().getCenterY());
		System.out.println(ip.getPixel((int)imagen.getRoi().getBounds().getCenterX(), (int)imagen.getRoi().getBounds().getCenterY()));
		copiaImagen.getProcessor().putPixel((int)imagen.getRoi().getBounds().getCenterX(), (int)imagen.getRoi().getBounds().getCenterY(), 0);
	}
}
