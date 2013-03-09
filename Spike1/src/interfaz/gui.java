package interfaz;

import ij.ImagePlus;

import java.awt.EventQueue;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JTextPane;

import utils.MyLogHandler;
import utils.Graphic;
import utils.Propiedades;

import modelo.Mediador;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.swing.JSlider;

public class Gui {

	private JFrame frmXraydetector;
	private JPanel panelProgreso_1;
	private Graphic imgPanel;
	private JTextPane txtLog;
	private JButton btnAnalizar;
	private static Mediador mediador;
	private Thread thread;
	private Rectangle selection;
	private File model;
	private File arff;
	private JProgressBar progressBar;
	private File maskDirectory;
	private File originalDirectory;
	private boolean sameFiles = false;
	private JButton btnEntrenarClasificador;
	private JButton btnAbrirImagen;
	private boolean imagenAbierta;
	private JButton btnStop;
	private ImagePlus img;
	private boolean parado = false;
	private static Propiedades prop;
	private JSlider slider;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frmXraydetector.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
		prop = Propiedades.getInstance();
		mediador = Mediador.getInstance();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (InstantiationException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			Date date = new Date();
			StringWriter sWriter = new StringWriter();
			e.printStackTrace(new PrintWriter(sWriter));
			MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e.toString());
			e.printStackTrace();
		}
		
		getJFramePrincipal();
		
		JPanel panelControl = getPanelControl();
		
		getBtnAbrirImagen(panelControl);
		
		getBtnEntrenar(panelControl);
		
		getBtnAnalizar(panelControl);
		
		JPanel panelProgreso = getPanelProgreso();
		
		getProgressBar(panelProgreso);
		
		getBtnStop(panelProgreso);
		
		JPanel panelImagen = getPanelImagen();
		
		getImgPanel(panelImagen);
		
		
		JPanel panelLog = getPanelLog();
			
		txtLog = getTxtLog(panelLog);
		
		JPanel panelSlider = getPanelSlider();
		
		getSlider(panelSlider);
	}

	public void getSlider(JPanel panelSlider) {
		slider = new JSlider();
		slider.setEnabled(false);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(4);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setToolTipText("Rango de toleracia de ajuste al defecto");
		slider.setValue(14);
		slider.setMinimum(4);
		slider.setMaximum(24);
		slider.addChangeListener(new SliderListener());
		panelSlider.add(slider);
	}

	public JPanel getPanelSlider() {
		JPanel panelSlider = new JPanel();
		panelSlider.setBorder(new TitledBorder(null, "Nivel de tolerancia", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSlider.setBounds(10, 218, 236, 73);
		frmXraydetector.getContentPane().add(panelSlider);
		panelSlider.setLayout(new GridLayout(1, 1, 0, 0));
		return panelSlider;
	}

	private void getImgPanel(JPanel panelImagen) {
		ImagePlus imagAux = new ImagePlus("./res/img/app/logoGris.png");
		Image imag = imagAux.getImage();
		
		imgPanel = new Graphic(imag);
		panelImagen.add(imgPanel);
	}

	private JTextPane getTxtLog(JPanel panelLog) {
		JTextPane textPaneLog = new JTextPane();
		textPaneLog.setEditable(false);
		panelLog.add(textPaneLog);
		JScrollPane scroll = new JScrollPane(textPaneLog);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelLog.add(scroll);
		return textPaneLog;
	}

	private JPanel getPanelLog() {
		JPanel panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog.setBounds(10, 302, 236, 259);
		frmXraydetector.getContentPane().add(panelLog);
		panelLog.setLayout(new GridLayout(1, 0, 0, 0));		
		return panelLog;
	}

	private JPanel getPanelImagen() {
		JPanel panelImagen = new JPanel();
		panelImagen.setBorder(new TitledBorder(null, "Imagen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelImagen.setBounds(256, 11, 750, 550);
		frmXraydetector.getContentPane().add(panelImagen);
		panelImagen.setLayout(new GridLayout(1, 0, 0, 0));
		return panelImagen;
	}

	private void getBtnStop(JPanel panelProgreso) {
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new StopListener());
		btnStop.setEnabled(false);
		panelProgreso.add(btnStop);
	}

	private void getProgressBar(JPanel panelProgreso) {
		panelProgreso_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		progressBar = new JProgressBar();
		panelProgreso.add(progressBar);
	}

	private JPanel getPanelProgreso() {
		panelProgreso_1 = new JPanel();
		panelProgreso_1.setBorder(new TitledBorder(null, "Progreso", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelProgreso_1.setBounds(10, 147, 236, 60);
		frmXraydetector.getContentPane().add(panelProgreso_1);
		return panelProgreso_1;
	}

	private void getBtnAnalizar(JPanel panelControl) {
		btnAnalizar = new JButton("Analizar imagen");
		btnAnalizar.setEnabled(false);
		btnAnalizar.addActionListener(new AnalizarImagenListener());
		panelControl.add(btnAnalizar);
	}

	private void getBtnEntrenar(JPanel panelControl) {
		btnEntrenarClasificador = new JButton("Entrenar clasificador");
		btnEntrenarClasificador.addActionListener(new EntrenarListener(frmXraydetector));
		panelControl.add(btnEntrenarClasificador);
	}

	private void getBtnAbrirImagen(JPanel panelControl) {
		btnAbrirImagen = new JButton("Abrir imagen");
		btnAbrirImagen.addActionListener(new CargarImagenListener());
		panelControl.add(btnAbrirImagen);
	}

	private JPanel getPanelControl() {
		JPanel panelControl = new JPanel();
		panelControl.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelControl.setBounds(10, 11, 236, 125);
		frmXraydetector.getContentPane().add(panelControl);
		panelControl.setLayout(new GridLayout(0, 1, 0, 4));
		return panelControl;
	}

	private void getJFramePrincipal() {
		frmXraydetector = new JFrame();
		frmXraydetector.setTitle("XRayDetector");
		frmXraydetector.setBounds(100, 100, 1024, 600);
		frmXraydetector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmXraydetector.getContentPane().setLayout(null);
		frmXraydetector.setLocationRelativeTo(null);
	}
	
	private class CargarImagenListener implements ActionListener{
	    
		public void actionPerformed (ActionEvent e){
	    	File image = null;
	    	
	    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la imagen deseada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Im�genes BMP, JPG, JPEG, PNG", "bmp", "jpg", "jpeg", "png");
	    	chooser.setFileFilter(filter);
	    	chooser.setAcceptAllFileFilterUsed(false);
			/*chooser.addChoosableFileFilter(new ImageFilter());
			chooser.setFileView(new ImageFileView());
			chooser.setAccessory(new ImagePreview(chooser));*/
	    	int answer = chooser.showOpenDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				image = chooser.getSelectedFile();
			}
			if(image != null){
				//Mediador m = Mediador.getInstance();
				imagenAbierta = true;
				mediador.cargaImagen(image.getAbsolutePath());
				img = new ImagePlus(image.getAbsolutePath());
				imgPanel.setImage(img.getImage());
				imgPanel.repaint();
				selection = imgPanel.coordenates();
				btnAnalizar.setEnabled(true);
				slider.setEnabled(false);
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				StyleConstants.setBold(sa, true);	//Negrita
				StyleConstants.setForeground(sa, Color.GREEN.darker());
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Imagen abierta correctamente\n\n", sa);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				}
			}
	    }
	}
	
	private class SliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (!slider.getValueIsAdjusting()) {
				prop.setUmbral(slider.getValue());
				mediador.drawEdge(imgPanel);
			}
			
		}
		
		
	}
	
	private class AnalizarImagenListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	//Mediador m = Mediador.getInstance();    	
	    	//mediador.ejecutaVentana();
	    	int opcion;
	    	//si no se ha seleccionado una regi�n, mostramos un aviso
	    	if(selection.height == 0 && selection.width == 0){
		    	opcion = JOptionPane.showOptionDialog(
		    			   null,
		    			   "Aviso: no ha elegido ninguna regi�n.\n" +
		    			   "El proceso puede tardar mucho. �Desea continuar de todos modos?", 
		    			   "Aviso",
		    			   JOptionPane.YES_NO_CANCEL_OPTION,
		    			   JOptionPane.WARNING_MESSAGE,
		    			   null,    // null para icono por defecto.
		    			   new Object[] { "Continuar", "Cancelar"},   // null para YES, NO y CANCEL
		    			   "Cancelar");
	    	}
	    	else{
	    		opcion = 0;
	    	}
	    	if(opcion == 0){
		    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
		    	chooser.setDialogTitle("Escoja el modelo para clasificar");
		    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Model", "model");
		    	chooser.setFileFilter(filter);
		    	chooser.setAcceptAllFileFilterUsed(false);
				/*chooser.addChoosableFileFilter(new ImageFilter());
				chooser.setFileView(new ImageFileView());
				chooser.setAccessory(new ImagePreview(chooser));*/
		    	int answer = chooser.showOpenDialog(null);
				if (answer == JFileChooser.APPROVE_OPTION) {
					model = chooser.getSelectedFile();
				}
				else{
					model = null;
				}
				if(model != null){
					
					prop.setPathModel(model.getAbsolutePath());
					SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
					StyleConstants.setBold(sa, true);	//Negrita
					StyleConstants.setForeground(sa, Color.GREEN.darker());
					try {
						txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Modelo cargado correctamente\n\n", sa);
						txtLog.setCaretPosition(txtLog.getDocument().getLength());
					} catch (BadLocationException e1) {
						Date date = new Date();
						StringWriter sWriter = new StringWriter();
						e1.printStackTrace(new PrintWriter(sWriter));
						MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
						e1.printStackTrace();
					}
					
					slider.setEnabled(false);
					btnEntrenarClasificador.setEnabled(false);
					btnAbrirImagen.setEnabled(false);
					btnAnalizar.setEnabled(false);
					btnStop.setEnabled(true);
					imgPanel.setImage(img.getImage());
					imgPanel.repaint();
					ThreadAnalizar threadAnalizar = new ThreadAnalizar();
			    	thread = new Thread(threadAnalizar);
			    	thread.start();
				}
	    	}
	    }
	}
	
	private class ThreadAnalizar implements Runnable{

		@Override
		public void run() {
			
			SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos			
			
			try {
				txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Iniciando proceso de an�lisis\n\n", sa);
			} catch (BadLocationException e1) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e1.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
				e1.printStackTrace();
			}
			
			mediador.ejecutaVentana(selection, imgPanel, progressBar);
			
			if(!parado){
				StyleConstants.setBold(sa, true);	//Negrita
				StyleConstants.setForeground(sa, Color.GREEN.darker());
				
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso de an�lisis finalizado con �xito\n\n", sa);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				}
			}
			
			parado = false;
			btnEntrenarClasificador.setEnabled(true);
			btnAbrirImagen.setEnabled(true);
			btnStop.setEnabled(false);
			slider.setEnabled(true);
			if(imagenAbierta){
				btnAnalizar.setEnabled(true);
			}
			else{
				btnAnalizar.setEnabled(false);
			}
		}		
	}
	
	private class StopListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	if (thread != null){
	    		parado = true;
	    		if(mediador.getNumThreads() > 0){
	    			mediador.stop();
	    		}
	    		slider.setEnabled(false);
	    		btnEntrenarClasificador.setEnabled(true);
				btnAbrirImagen.setEnabled(true);
				btnStop.setEnabled(false);
				if(imagenAbierta){
					btnAnalizar.setEnabled(true);
				}
				else{
					btnAnalizar.setEnabled(false);
				}
				if(img != null){
					imgPanel.setImage(img.getImage());
					imgPanel.repaint();
				}
				progressBar.setValue(0);
				
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos	
				StyleConstants.setBold(sa, true);	//Negrita
				StyleConstants.setForeground(sa, Color.BLUE.darker());
				
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso detenido\n\n", sa);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				}
				
				thread.interrupt();
	    	}
	    }
	}
	
	private class EntrenarListener implements ActionListener{
	    
		private Frame frame;
		
		public EntrenarListener(Frame fr) {
			frame = fr;
		}

		public void actionPerformed (ActionEvent e){
			JDialog dialogo = new JDialog(frame, "Escoja opci�n", true);
			dialogo.getContentPane().setLayout(new BorderLayout());
			
			JPanel panelRadio = new JPanel();
			panelRadio.setLayout(new GridLayout(2,1));
			dialogo.getContentPane().add(panelRadio, BorderLayout.CENTER);
			
			JRadioButton btnNuevoArff = new JRadioButton("Crear nuevo ARFF");
			panelRadio.add(btnNuevoArff);
			JRadioButton btnUsarArff = new JRadioButton("Usar ARFF existente");
			panelRadio.add(btnUsarArff);
			
			ButtonGroup grupoBotonesOpcion = new ButtonGroup();
			grupoBotonesOpcion.add(btnNuevoArff);
			grupoBotonesOpcion.add(btnUsarArff);
			grupoBotonesOpcion.setSelected(btnNuevoArff.getModel(), true);
			
			JPanel panelBotones = new JPanel();
			panelBotones.setLayout(new GridLayout(1,2,8,8));
			JButton btnAceptar = new JButton("Aceptar");
			
			Enumeration<AbstractButton> list = grupoBotonesOpcion.getElements();
			
			btnAceptar.addActionListener(new AceptarListener(dialogo, list));
			panelBotones.add(btnAceptar);
			JButton btnCancelar = new JButton("Cancelar");
			btnCancelar.addActionListener(new CancelarListener(dialogo));
			panelBotones.add(btnCancelar);
			dialogo.getContentPane().add(panelBotones, BorderLayout.SOUTH);
			
			dialogo.pack();
			dialogo.setLocationRelativeTo(frame);
			dialogo.setVisible(true);
		}
	}
	
	private class CancelarListener implements ActionListener{
		
		private JDialog dial;
		
	    public CancelarListener(JDialog dialogo) {
			dial = dialogo;
		}

		public void actionPerformed (ActionEvent e){
	    	dial.setVisible(false);
	    }
	}
	
	private class AceptarListener implements ActionListener{
		
		private JDialog dial;
		private Enumeration<AbstractButton> listaBtn;
		
	    public AceptarListener(JDialog dialogo, Enumeration<AbstractButton> list) {
			dial = dialogo;
			listaBtn = list;
		}

		public void actionPerformed (ActionEvent e){
			
			while(listaBtn.hasMoreElements()) {
	            AbstractButton button = listaBtn.nextElement();

	            if (button.isSelected()) {
	            	if (button.getText().equals("Crear nuevo ARFF")){
	            		originalDirectory = null;
	            		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	            		// Para que solo se puedan abrir directorios
	            		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            		chooser.setDialogTitle("Selecciona una carpeta de imagenes originales");
	            		int answer = chooser.showOpenDialog(null);
	            		if (answer == JFileChooser.APPROVE_OPTION) {
	            			originalDirectory = chooser.getSelectedFile().getAbsoluteFile();
	            			
	            			// Comprobamos que el directorio acabe en Entrenar/Originales ya
	        				// que ahí van a estar las imagenes para entrenar
	        				if (originalDirectory == null
	        						|| !originalDirectory.getPath().contains("Originales")) {
	        					JOptionPane
	        							.showMessageDialog(
	        									null,
	        									"No has seleccionado una carpeta correcta para entrenar.",
	        									"Error", 1);
	        					sameFiles = false;
	        					SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
	        					StyleConstants.setBold(sa, true);	//Negrita
	        					StyleConstants.setForeground(sa, Color.RED);
	        					try {
	        						txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Directorio incorrecto\n\n", sa);
	        						txtLog.setCaretPosition(txtLog.getDocument().getLength());
	        					} catch (BadLocationException e1) {
	        						Date date = new Date();
	        						StringWriter sWriter = new StringWriter();
	        						e1.printStackTrace(new PrintWriter(sWriter));
	        						MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
	        						e1.printStackTrace();
	        					}

	        				}
	        				else {
	        					// Se reemplaza Originales por Mascaras ya que el directorio
	        					// de las mascaras es igual, solo cambia eso
	        					maskDirectory = new File(originalDirectory.getAbsolutePath()
	        							.replace("Originales", "Mascaras"));
	        					
	        					ThreadEntrenar threadEntrenar = new ThreadEntrenar(false);
	        					slider.setEnabled(false);
		        		    	thread = new Thread(threadEntrenar);
		        		    	Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
		        		    	    public void uncaughtException(Thread th, Throwable ex) {
		        		    	    	System.out.println("Excepcion capturada en interfaz");
		        		    	    	JOptionPane.showMessageDialog(
		        		    	    			   null,
		        		    	    			   "Se ha producido un error: "+ ex.getMessage(),
		        		    	    			   "Error",
		        		    	    			   JOptionPane.ERROR_MESSAGE);
		        		    	    	SimpleAttributeSet sa = new  SimpleAttributeSet();
		        		    	    	StyleConstants.setBold(sa, true);	//Negrita
		        						StyleConstants.setForeground(sa, Color.RED);
		        						try {
		        							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Error\n\n", sa);
		        							txtLog.setCaretPosition(txtLog.getDocument().getLength());
		        						} catch (BadLocationException e1) {
		        							throw new RuntimeException();
		        						}
		        		    	    }
		        		    	};
		        		    	thread.setUncaughtExceptionHandler(h);
		        		    	thread.start();
	        				}
	            		}
	            	}
	            	else if (button.getText().equals("Usar ARFF existente")){
	            		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	        	    	chooser.setDialogTitle("Escoja el ARFF para entrenar");
	        	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("ARFF", "arff");
	        	    	chooser.setFileFilter(filter);
	        	    	chooser.setAcceptAllFileFilterUsed(false);
	        	    	int answer = chooser.showOpenDialog(null);
	        			if (answer == JFileChooser.APPROVE_OPTION) {
	        				arff = chooser.getSelectedFile();
	        			}
	        			else{
	        				arff=null;
	        				btnEntrenarClasificador.setEnabled(true);
		    				btnAbrirImagen.setEnabled(true);
		    				btnStop.setEnabled(false);
		    				if(imagenAbierta){
		    					btnAnalizar.setEnabled(true);
		    				}
		    				else{
		    					btnAnalizar.setEnabled(false);
		    				}
	        			}
	        			
	        			if(arff != null){
	        				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
	        				StyleConstants.setBold(sa, true);	//Negrita
	        				StyleConstants.setForeground(sa, Color.GREEN.darker());
	        				try {
	        					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "ARFF abierto correctamente\n\n", sa);
	        					txtLog.setCaretPosition(txtLog.getDocument().getLength());
	        				} catch (BadLocationException e1) {
	        					Date date = new Date();
	        					StringWriter sWriter = new StringWriter();
	        					e1.printStackTrace(new PrintWriter(sWriter));
	        					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
	        					e1.printStackTrace();
	        				}

	        				ThreadEntrenar threadEntrenar = new ThreadEntrenar(true);
	        		    	thread = new Thread(threadEntrenar);
	        				btnEntrenarClasificador.setEnabled(false);
	        				btnAbrirImagen.setEnabled(false);
	        				btnAnalizar.setEnabled(false);
	        				btnStop.setEnabled(true);
	        				slider.setEnabled(false);
	        		    	Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
	        		    	    public void uncaughtException(Thread th, Throwable ex) {
	        		    	    	JOptionPane.showMessageDialog(
	        		    	    			   null,
	        		    	    			   "Se ha producido un error: "+ ex.getMessage(),
	        		    	    			   "Error",
	        		    	    			   JOptionPane.ERROR_MESSAGE);
	        		    	    	SimpleAttributeSet sa = new  SimpleAttributeSet();
	        		    	    	StyleConstants.setBold(sa, true);	//Negrita
	        						StyleConstants.setForeground(sa, Color.RED);
	        						try {
	        							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Error\n\n", sa);
	        							txtLog.setCaretPosition(txtLog.getDocument().getLength());
	        						} catch (BadLocationException e1) {
	        							throw new RuntimeException();
	        						}
	        		    	    }
	        		    	};
	        		    	thread.setUncaughtExceptionHandler(h);
	        		    	thread.start();
	        			}
	        			
	            	}
	            }
	        }
			dial.setVisible(false);
			
	    }
	}
	
	private class ThreadEntrenar implements Runnable{
		
		private boolean entrenarConArff;

		public ThreadEntrenar(boolean b) {
			super();
			entrenarConArff = b;
		}

		@Override
		public void run() {
			btnStop.setEnabled(true);
			if(entrenarConArff){	//si queremos entrenar con un fichero existente
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Inicio del proceso de entrenamiento\n\n", sa);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					throw new RuntimeException();
				}
				mediador.ejecutaEntrenamiento(arff, null);
				
				if(!parado){
					StyleConstants.setBold(sa, true);	//Negrita
					StyleConstants.setForeground(sa, Color.GREEN.darker());
					try {
						txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Modelo entrenado correctamente" +
								"con el ARFF especificado\n\n", sa);
						txtLog.setCaretPosition(txtLog.getDocument().getLength());
					} catch (BadLocationException e1) {
						throw new RuntimeException();
					}
				}
				btnEntrenarClasificador.setEnabled(true);
				btnAbrirImagen.setEnabled(true);
				btnStop.setEnabled(false);
				if(imagenAbierta){
					btnAnalizar.setEnabled(true);
				}
				else{
					btnAnalizar.setEnabled(false);
				}
			}
			else{	//entrenar con im�genes
				// Listado de archivos del directorio de originales
							
				String originalList[] = originalDirectory.list();
				Arrays.sort(originalList);
				
				// Listado de archivos del directorio de mascaras
				String maskList[] = maskDirectory.list();
				Arrays.sort(maskList);
				
				//System.out.println("Or: " + originalList.length + " Mask: " + maskList.length);
				
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				
				if (originalList.length != maskList.length) {
					StyleConstants.setBold(sa, true);	//Negrita
					StyleConstants.setForeground(sa, Color.RED);
					
					if (originalList.length > maskList.length){
						JOptionPane.showMessageDialog(null,
								"El n�mero de im�genes originales y m�scaras no coinciden.\n"
										+ "Faltan m�scaras", "Error", 1);
						try {
							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso fallido: " +
									"Faltan m�scaras\n\n", sa);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e1) {
							throw new RuntimeException();
						}
					}
					else{
						JOptionPane.showMessageDialog(null,
								"El n�mero de im�genes originales y m�scaras no coinciden.\n"
										+ "Faltan originales", "Error", 1);
						try {
							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso fallido: " +
									"Faltan im�genes originales\n\n", sa);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e1) {
							throw new RuntimeException();
						}
					}
				} else {
					sameFiles = true;
					for (int i = 0; i < originalList.length
							&& sameFiles == true; i++) {
						// Quitar extension jpg
						String originalName = originalList[i].substring(0,
								originalList[i].lastIndexOf("."));
						// Quitar extension bmp
						String maskName = maskList[i].substring(0,
								maskList[i].lastIndexOf("."));

						// Se comprueba que cada original y su mascara
						// correspondiente tienen el mismo nombre
						if (!originalName.equals(maskName))
							sameFiles = false;
					}
					if (sameFiles == false){
						JOptionPane
								.showMessageDialog(
										null,
										"Los nombres de las im�genes originales no coinciden con los de las m�scaras.",
										"Error", 1);
						StyleConstants.setBold(sa, true);	//Negrita
						StyleConstants.setForeground(sa, Color.RED);
						try {
							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso fallido: " +
									"Los nombres de las im�genes originales no coinciden con los de las m�scaras\n\n", sa);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e1) {
							throw new RuntimeException();
						}
					}
					else {
						StyleConstants.setBold(sa, true);	//Negrita
						StyleConstants.setForeground(sa, Color.GREEN.darker());
						try {
							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Directorio abierto correctamente\n\n", sa);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e1) {
							throw new RuntimeException();
						}
						
						File[] originalFiles = originalDirectory.listFiles();
						File[] maskFiles = maskDirectory.listFiles();
						originalList = new String[originalFiles.length];
						maskList = new String[maskFiles.length];
						
						for(int i=0; i<originalFiles.length; i++){
							originalList[i] = originalFiles[i].getAbsolutePath();
							maskList[i] = maskFiles[i].getAbsolutePath();
						}
						
						StyleConstants.setBold(sa, false);
						StyleConstants.setForeground(sa, Color.BLACK);
						try {
							txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Iniciando proceso de entrenamiento\n\n", sa);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e1) {
							throw new RuntimeException();
						}
						mediador.ejecutarEntrenamientoDirectorio(originalList, maskList, progressBar, txtLog);
						
						if(!parado){
							StyleConstants.setBold(sa, true);
							StyleConstants.setForeground(sa, Color.GREEN.darker());
							try {
								txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Proceso de entrenamiento finalizado con �xito\n\n", sa);
								txtLog.setCaretPosition(txtLog.getDocument().getLength());
							} catch (BadLocationException e1) {
								throw new RuntimeException();
							}
						}
					}
				}
				parado = false;
				btnEntrenarClasificador.setEnabled(true);
				btnAbrirImagen.setEnabled(true);
				btnStop.setEnabled(false);
				if(imagenAbierta){
					btnAnalizar.setEnabled(true);
				}
				else{
					btnAnalizar.setEnabled(false);
				}
			}
		}		
	}
}
