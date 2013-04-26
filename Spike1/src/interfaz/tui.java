package interfaz;

import modelo.Fachada;

public class Tui {

	private static String img = "./res/img/img1.BMP";
	private static String img_mask = "./res/img/img1_mask.jpg";
	private static Fachada mediador;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Se cargará la imagen " + img_mask);
		mediador = Fachada.getInstance();
		try{
			System.out.println(mediador.cargaImagen(img));
			//mediador.ejecutaVentana();
			//mediador.ejecutaVentanaSaliency();
			//mediador.ejecutaEntrenamiento();
		}
		catch(Exception ex){
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

}
