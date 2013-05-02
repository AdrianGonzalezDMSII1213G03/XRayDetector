package datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

import utils.Propiedades;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class GestorArff {
	
	private static Propiedades prop;
	
	public GestorArff(){
		prop = Propiedades.getInstance();
	}

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
