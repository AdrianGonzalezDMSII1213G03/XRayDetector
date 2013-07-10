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
 * GestorARFF.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import es.ubu.XRayDetector.utils.Propiedades;

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

/**
 * Class GestorArff
 * 
 * Class to manage the ARFF files.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
 
public class GestorArff {
	
	/**
	 * The application properties.
	 */
	private static Propiedades prop;
	
	/**
	 * The class constructor.
	 */
	public GestorArff(){
		prop = Propiedades.getInstance();
	}

	/**
	 * Reads an ARFF file.
	 * 
	 * @param url The PATH of the ARFF file.
	 * @return The instances include in the ARFF file.
	 */
	public Instances leerArff (String url){
		BufferedReader reader = null;		
		try {
			reader = new BufferedReader(new FileReader(url));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}		
		ArffReader arff = null;		
		try {
			arff = new ArffReader(reader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		Instances data = arff.getData();
		data.setClassIndex(data.numAttributes() - 1);
		
		return data;
	}
	
	/**
	 * Merge several ARFF files into an unique ARFF file.
	 * 
	 * @param longitud The number of ARFF files available.
	 */
	public void mergeArffFiles(int longitud) {
		File[] ficheros = new File[longitud];
		for (int ithread = 0; ithread < longitud; ++ithread){
			File file = new File("./res/arff/Arff_entrenamiento" + ithread + ".arff");
			ficheros[ithread] = file;
		}
		// File to write
		//File fileOutput = new File("./res/arff/Arff_entrenamiento.arff");	//de momento, a pelo
		File fileOutput = new File(prop.getPathArff());

		for(int i=0; i<ficheros.length; i++){
			// Read the file like string
			try {
				String file1Str = FileUtils.readFileToString(ficheros[i]);
				if(i==0){
					FileUtils.write(fileOutput, file1Str);
				}
				else{
					FileUtils.write(fileOutput, file1Str, true);
				}
				ficheros[i].delete();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}		
	}
	
	/**
	 * Creates a new ARFF file.
	 * 
	 * @param numHilo The number of the thread which manages the new ARFF file.
	 * @param featuresString The features extracted from the image.
	 * @param header The header of the file.
	 */
	public void crearArff(int numHilo, String featuresString, String header){

		File outputFile;
		FileWriter arffFile;

		outputFile = new File("./res/arff/Arff_entrenamiento" + numHilo + ".arff");
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
				if(numHilo == 0){
					arffFile = new FileWriter(outputFile);
					arffFile.write(header);
				}
				else{
					arffFile = new FileWriter(outputFile);
				}
			} else {
				// si ya esta creado se escribe a continuacion
				arffFile = new FileWriter(outputFile, true);
			}
			arffFile.write(featuresString + "\n");
			arffFile.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
