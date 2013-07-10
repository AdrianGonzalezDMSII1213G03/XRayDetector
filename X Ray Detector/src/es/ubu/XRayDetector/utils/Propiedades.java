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
 * Propiedades.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class that manages the properties of the application.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 1.0
 */
public class Propiedades {
	
	/**
	 * Instance of this class, used by the <i>Singleton</i> pattern.
	 */
	private static Propiedades instance;
	
	/**
	 * Java Properties instance.
	 */
	private Properties propiedades;
	
	/**
	 * Private constructot.
	 */
	private Propiedades(){
		cargarPropiedades();
	}
	


	/**
	 * Method that retrieves the instance of this class.
	 * @return Instance of this class
	 */
	public static Propiedades getInstance(){
		if (instance == null){
			return instance = new Propiedades();
		}
		else{
			return instance;
		}
	}
	
	/**
	 * Creates the properties file in case it does not exist, or
	 * it loads it if it already exists.
	 */
	private void cargarPropiedades() {
		File f = new File("./res/config/config.properties");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				MyLogHandler.writeException(e);
				e.printStackTrace();
			}
			cargarOpcionesPorDefecto();
		}
		else{
			propiedades = new Properties();
			try {
				propiedades.load(new FileInputStream("./res/config/config.properties"));
			} catch (FileNotFoundException e) {
				MyLogHandler.writeException(e);
				e.printStackTrace();
			} catch (IOException e) {
				MyLogHandler.writeException(e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method that creates some default options.
	 */
	private void cargarOpcionesPorDefecto() {
		propiedades = new Properties();
		
		propiedades.setProperty("tamVentana", "24");
		propiedades.setProperty("salto", "0.7");
		propiedades.setProperty("umbral", "8");
		propiedades.setProperty("tipoDeteccion", "0");
		propiedades.setProperty("tipoEntrenamiento", "0");
		propiedades.setProperty("tipoClasificacion", "0");
		propiedades.setProperty("tipoVentanaDefectuosa", "0");
		propiedades.setProperty("tipoCaracteristicas", "0");
		propiedades.setProperty("porcentajePixeles", "0.5");
		propiedades.setProperty("pathArff", "./res/arff/Arff_entrenamiento.arff");
		propiedades.setProperty("pathModel", ".res/model/todas_24.model");
		
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the size of the window.
	 * @return Size of the window.
	 * @see #setTamVentana
	 */
	public int getTamVentana(){		
		return Integer.parseInt(propiedades.getProperty("tamVentana"));
	}
	
	/**
	 * Retrieves the size of the window movement.
	 * @return Size of window movement
	 * @see #setSalto
	 */
	public double getSalto(){
		return Float.parseFloat(propiedades.getProperty("salto"));
	}
	
	/**
	 * Retrieves the defects threshold.
	 * @return Threshold
	 * @see #setUmbral
	 */
	public int getUmbral(){
		return Integer.parseInt(propiedades.getProperty("umbral"));
	}
	
	/**
	 * Retrieves the type of detection.
	 * @return Type of detection
	 * @see #setTipoDeteccion
	 */
	public int getTipoDeteccion(){
		return Integer.parseInt(propiedades.getProperty("tipoDeteccion"));
	}
	
	/**
	 * Retrieves the type of training.
	 * 
	 * @return Training type
	 * @see #setTipoEntrenamiento
	 */
	public int getTipoEntrenamiento(){		
		return Integer.parseInt(propiedades.getProperty("tipoEntrenamiento"));
	}
	
	/**
	 * Retrieves the classification type.
	 * @return Classification type
	 * @see #setTipoClasificacion
	 */
	public int getTipoClasificacion(){		
		return Integer.parseInt(propiedades.getProperty("tipoClasificacion"));
	}
	
	/**
	 * Retrieves the heuristic used in order to determine whether a window has
	 * a defect or not.
	 * @return Heuristic
	 * @see #setTipoVentanaDefectuosa
	 */
	public int getTipoVentanaDefectuosa(){
		return Integer.parseInt(propiedades.getProperty("tipoVentanaDefectuosa"));
	}
	
	/**
	 * Retrieves the percentage of the total pixels that have to be faulty.
	 * @return Percentage of pixels
	 * @see #setPorcentajePixeles
	 */
	public double getPorcentajePixeles(){
		return Float.parseFloat(propiedades.getProperty("porcentajePixeles"));
	}
	
	/**
	 * Retrieves the caracteristics option.
	 * @return Caracteristics option
	 * @see #setTipoCaracteristicas
	 */
	public int getTipoCaracteristicas(){
		return Integer.parseInt(propiedades.getProperty("tipoCaracteristicas"));
	}
	
	/**
	 * Sets the defects threshold.
	 * @param umbral Selected threshold
	 * @see #getUmbral
	 */
	public void setUmbral(int umbral){
		propiedades.setProperty("umbral", String.valueOf(umbral));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the training type.
	 * @param tipo Training type
	 * @see #getTipoEntrenamiento
	 */
	public void setTipoEntrenamiento(int tipo){
		propiedades.setProperty("tipoEntrenamiento", String.valueOf(tipo));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the classification type.
	 * @param tipo Classification type
	 * @see #getTipoClasificacion
	 */
	public void setTipoClasificacion(int tipo){
		propiedades.setProperty("tipoClasificacion", String.valueOf(tipo));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the detection type.
	 * @param tipo Detection type
	 * @see #getTipoDeteccion
	 */
	public void setTipoDeteccion(int tipo){
		propiedades.setProperty("tipoDeteccion", String.valueOf(tipo));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the window movement.
	 * @param salto Window movement
	 * @see #getSalto
	 */
	public void setSalto(double salto){
		propiedades.setProperty("salto", String.valueOf(salto));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the size of the window.
	 * @param tam Size of window
	 * @see #getTamVentana
	 */
	public void setTamVentana(int tam){
		propiedades.setProperty("tamVentana", String.valueOf(tam));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the path to store the ARFF file.
	 * @return path to the arff file
	 */
	public String getPathArff(){
		return propiedades.getProperty("pathArff");
	}
	
	/**
	 * Retrieves the path to store the model files.
	 * @return path to the model files
	 * @see #setPathModel
	 */
	public String getPathModel(){
		return propiedades.getProperty("pathModel");
	}
	
	/**
	 * Sets the path to store model files.
	 * @param model Path to model files
	 * @see #getPathModel
	 */
	public void setPathModel(String model){
		propiedades.setProperty("pathModel", model);
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the heuristic of faulty windows.
	 * @param tipo Heuristic
	 * @see #getTipoVentanaDefectuosa
	 */
	public void setTipoVentanaDefectuosa(int tipo){
		propiedades.setProperty("tipoVentanaDefectuosa", String.valueOf(tipo));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the percentage of faulty pixels in window.
	 * @param porcentaje Percentage
	 * @see #getPorcentajePixeles
	 */
	public void setPorcentajePixeles(double porcentaje){
		propiedades.setProperty("porcentajePixeles", String.valueOf(porcentaje));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the caracteristic option.
	 * @param tipo Caracteristics option
	 * @see #getTipoCaracteristicas
	 */
	public void setTipoCaracteristicas(int tipo){
		propiedades.setProperty("tipoCaracteristicas", String.valueOf(tipo));
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IOException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	}
}
