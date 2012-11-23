package modelo;



import datos.FileReader;

public class Mediador {
	
	private static Mediador INSTANCE = null;
	private FileReader fr;
	
	private Mediador() {
		fr = new FileReader();
	}
	
	public static Mediador getInstance(){	//Singleton
		if(INSTANCE == null){
			return new Mediador();
		}
		else{
			return INSTANCE;
		}
	}

	public String cargaImagen(String img){		
		int i = fr.abrirImagen(img);
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
	
	public void ejecutaVentana(){
		Ventana v = new Ventana(fr);
		v.run();
	}
}
