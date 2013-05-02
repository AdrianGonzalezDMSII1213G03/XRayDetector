package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Clase MyLogHandler.
 * Clase registro de log.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class MyLogHandler {
	
	/**
	 * Instancia del tipo MyLogHandler que utiliza el patrón Singelton.
	 */
	private static MyLogHandler log;
	
	/**
	 * Instancia del tipo Logger.
	 */
	private static Logger logger;

	
	
	/**
	* Constructor de la clase.
	*
	* @throws IOException Error de entrada/salida
	*/
	private MyLogHandler(){
		try{
			boolean append = true;
			FileHandler handler = new FileHandler( "res/log/registro.log", append );
			logger = Logger.getLogger( "util.MyLogHandler" );
			logger.addHandler( handler );
			logger.severe( "Registro grave" );
			logger.warning("Advertencia ");
			logger.info( "Información" );
			logger.config( "Registro de configuracion" );
			logger.fine( "Registro fine" );
			logger.finer( "registro finer" );
			logger.finest( "registro finest" );
		}
		catch( IOException e ){
		}
	}
	
	/**
	* Método que obtiene la instancia del log.
	* 
	* @return Logger para lanzar informes de fallos y notificaciones.
	*/
	public static Logger getLogger(){
		if( log==null ){
			log = new MyLogHandler();
		}
		return logger;
	}
	
	public static void writeException(Exception e){
		Date date = new Date();
		StringWriter sWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(sWriter));
		logger.logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
	}
}
