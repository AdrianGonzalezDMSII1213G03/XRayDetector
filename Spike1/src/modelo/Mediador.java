package modelo;



import datos.FileOpener;

public class Mediador {
	
	private static Mediador INSTANCE = null;
	
	private Mediador() {
		
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
		FileOpener fo = new FileOpener();
		int i = fo.abrirImagen(img);
		return new String("Imagen abierta correctamente. Bytes por pixel: " + i);
	}
}
