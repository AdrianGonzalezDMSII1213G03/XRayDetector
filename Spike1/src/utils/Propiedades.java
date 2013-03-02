package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

public class Propiedades {
	
	private static Propiedades instance;
	private Properties propiedades;
	
	private Propiedades(){
		cargarPropiedades();
	}
	


	public static Propiedades getInstance(){
		if (instance == null){
			return instance = new Propiedades();
		}
		else{
			return instance;
		}
	}
	
	private void cargarPropiedades() {
		File f = new File("config.properties");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
				e.printStackTrace();
			}
			cargarOpcionesPorDefecto();
		}
		else{
			propiedades = new Properties();
			try {
				propiedades.load(new FileInputStream("./res/config/config.properties"));
			} catch (FileNotFoundException e) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
				e.printStackTrace();
			}
		}
		
		
		
	}



	private void cargarOpcionesPorDefecto() {
		propiedades = new Properties();
		
		propiedades.setProperty("tamVentana", "24");
		propiedades.setProperty("salto", "0.7");
		propiedades.setProperty("umbral", "8");
		propiedades.setProperty("pathArff", "./res/arff/Arff_entrenamiento.arff");
		propiedades.setProperty("pathModel", ".res/model/todas_24.model");
		
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
	}
	
	public int getTamVentana(){		
		return Integer.parseInt(propiedades.getProperty("tamVentana"));
	}
	
	public float getSalto(){
		return Float.parseFloat(propiedades.getProperty("salto"));
	}
	
	public int getUmbral(){
		return Integer.parseInt(propiedades.getProperty("umbral"));
	}
	
	public String getPathArff(){
		return propiedades.getProperty("pathArff");
	}
	
	public String getPathModel(){
		return propiedades.getProperty("pathModel");
	}
	
	public void setPathModel(String model){
		propiedades.setProperty("pathModel", model);
		try {
			propiedades.store(new FileOutputStream("./res/config/config.properties"), null);
		} catch (FileNotFoundException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
	}

}
