/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * MyLogHandler.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Exception log handler.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class MyLogHandler {
	
	/**
	 * MyLogHandler instance used by the <i>Singleton</i> pattern.
	 */
	private static MyLogHandler log;
	
	/**
	 * Java logger instance.
	 */
	private static Logger logger;

	
	
	/**
	* Constructor.
	*
	* @throws IOException Input/Output error
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
	* Obtains the logger instance.
	* 
	* @return Logger used in order to capture exceptions
	*/
	public static Logger getLogger(){
		if( log==null ){
			log = new MyLogHandler();
		}
		return logger;
	}
	
	/**
	 * Method that writes an expection in the log file.
	 * @param e Exception to be written
	 */
	public static void writeException(Exception e){
		Date date = new Date();
		StringWriter sWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(sWriter));
		getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
	}
}
