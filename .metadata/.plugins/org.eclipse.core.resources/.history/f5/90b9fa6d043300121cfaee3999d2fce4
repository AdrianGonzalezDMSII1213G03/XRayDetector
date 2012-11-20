package datos;

import ij.ImagePlus;
import ij.io.Opener;
import java.io.File;


public class FileOpener {

	public int abrirImagen(String url){
		Opener opener = new Opener();
		File f = new File(url);
		ImagePlus ip = opener.openImage(f.getAbsolutePath());
		return ip.getBytesPerPixel();
	}
}
