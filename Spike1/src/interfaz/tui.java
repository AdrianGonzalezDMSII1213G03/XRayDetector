package interfaz;

import modelo.Mediador;

public class tui {

	private static String img = "./res/img/img1.BMP";
	private static Mediador mediador;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Se cargará la imagen " + img);
		mediador = Mediador.getInstance();
		try{
			System.out.println(mediador.cargaImagen(img));
			mediador.ejecutaVentana();
			mediador.ejecutaVentanaSaliency();
		}
		catch(Exception ex){
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}
