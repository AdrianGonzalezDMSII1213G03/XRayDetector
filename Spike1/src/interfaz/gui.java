package interfaz;

import ij.ImagePlus;

import java.awt.EventQueue;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
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
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.swing.JSlider;

import org.apache.commons.io.FileUtils;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

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
	private HTMLEditorKit kit;
    private HTMLDocument doc;
    private JPanel panelLog_1;
    private JButton btnLimpiarLog;
    private JButton btnExportarLog;
    private JMenu mnOpciones;
    private JMenuItem mntmOpcionesAvanzadas;
    private JMenu mnAyuda;
    private JMenuItem mntmAyudaEnLnea;
    private JMenuItem mntmAcercaDe;
    private int tamVentanaOpciones;
    private double saltoOpciones;

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
		
		getBotonExportarLog();
		getBotonLimpiarLog();
		
		JPanel panelSlider = getPanelSlider();
		
		getSlider(panelSlider);
		
		JMenuBar menuBar = new JMenuBar();
		frmXraydetector.setJMenuBar(menuBar);
		
		JMenu mnArchivo = new JMenu("Archivo");
		menuBar.add(mnArchivo);
		
		JMenuItem mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener(new SalirListener());
		mnArchivo.add(mntmSalir);
		mnOpciones = new JMenu("Opciones");
		menuBar.add(mnOpciones);

		mntmOpcionesAvanzadas = new JMenuItem("Opciones avanzadas");
		mntmOpcionesAvanzadas.addActionListener(new OpcionesAvanzadasListener(frmXraydetector));
		mnOpciones.add(mntmOpcionesAvanzadas);


		mnAyuda = new JMenu("Ayuda");
		menuBar.add(mnAyuda);

		mntmAyudaEnLnea = new JMenuItem("Ayuda en l\u00EDnea");
		mnAyuda.add(mntmAyudaEnLnea);

		mntmAcercaDe = new JMenuItem("Acerca de");
		mnAyuda.add(mntmAcercaDe);
	}

	public void getBotonLimpiarLog() {
		btnLimpiarLog = new JButton("Limpiar Log");
		btnLimpiarLog.setBounds(10, 212, 107, 36);
		btnLimpiarLog.addActionListener(new LimpiarLogListener());
		panelLog_1.add(btnLimpiarLog);
	}

	public void getBotonExportarLog() {
		btnExportarLog = new JButton("Exportar Log");
		btnExportarLog.setBounds(120, 212, 107, 36);
		btnExportarLog.addActionListener(new ExportarLogListener());
		panelLog_1.add(btnExportarLog);
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
		textPaneLog.setBounds(4, 4, 209, 186);
		textPaneLog.setEditable(false);
		panelLog.add(textPaneLog);
		textPaneLog.setContentType("text/html");
		kit = new HTMLEditorKit();
	    doc = new HTMLDocument();
	    panelLog_1.setLayout(null);
	    textPaneLog.setEditorKit(kit);
	    textPaneLog.setDocument(doc);
	    
		textPaneLog.setText("<!DOCTYPE html>" +
							"<html>"+
							"<head>"+
							"<style>"+
							"p.normal {font-weight:normal;}"+
							"p.error {font-weight:bold; color:red}"+
							"p.exito {font-weight:bold; color:green}"+
							"p.stop {font-weight:bold; color:blue}"+
							"</style>"+
							"</head>"+
							"<body>");
		JScrollPane scroll = new JScrollPane(textPaneLog);
		scroll.setBounds(10, 16, 217, 186);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelLog.add(scroll);
		return textPaneLog;
	}

	private JPanel getPanelLog() {
		panelLog_1 = new JPanel();
		panelLog_1.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog_1.setBounds(10, 302, 236, 259);
		frmXraydetector.getContentPane().add(panelLog_1);
		return panelLog_1;
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
		frmXraydetector.setBounds(100, 100, 1024, 619);
		frmXraydetector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmXraydetector.getContentPane().setLayout(null);
		frmXraydetector.setLocationRelativeTo(null);
	}
	
	private class CargarImagenListener implements ActionListener{
	    
		public void actionPerformed (ActionEvent e){
	    	File image = null;
	    	
	    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la imagen deseada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes BMP, JPG, JPEG, PNG", "bmp", "jpg", "jpeg", "png");
	    	chooser.setFileFilter(filter);
	    	chooser.setAcceptAllFileFilterUsed(false);
	    	int answer = chooser.showOpenDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				image = chooser.getSelectedFile();
			}
			if(image != null){
				imagenAbierta = true;
				mediador.cargaImagen(image.getAbsolutePath());
				img = new ImagePlus(image.getAbsolutePath());
				imgPanel.setImage(img.getImage());
				imgPanel.repaint();
				selection = imgPanel.coordenates();
				btnAnalizar.setEnabled(true);
				slider.setEnabled(false);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Imagen abierta correctamente</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				} catch (IOException e1) {
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
				//mediador.drawEdge(imgPanel);	primera opción
				mediador.drawEdgeRegiones(imgPanel);	//segunda opción
			}
			
		}		
	}
	
	private class AnalizarImagenListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	int opcion;
	    	
	    	//si no se ha seleccionado una región, mostramos un aviso
	    	if(selection.height == 0 && selection.width == 0){
		    	opcion = JOptionPane.showOptionDialog(
		    			   null,
		    			   "Aviso: no ha elegido ninguna región.\n" +
		    			   "El proceso puede tardar mucho. ¿Desea continuar de todos modos?", 
		    			   "Aviso",
		    			   JOptionPane.YES_NO_CANCEL_OPTION,
		    			   JOptionPane.WARNING_MESSAGE,
		    			   null,    // null para icono por defecto.
		    			   new Object[] { "Continuar", "Cancelar"},"Cancelar");
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
		    	int answer = chooser.showOpenDialog(null);
				if (answer == JFileChooser.APPROVE_OPTION) {
					model = chooser.getSelectedFile();
				}
				else{
					model = null;
				}
				if(model != null){					
					prop.setPathModel(model.getAbsolutePath());

					try {
						kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Modelo cargado correctamente</p><br>", 0, 0, null);
						txtLog.setCaretPosition(txtLog.getDocument().getLength());
					} catch (BadLocationException e1) {
						Date date = new Date();
						StringWriter sWriter = new StringWriter();
						e1.printStackTrace(new PrintWriter(sWriter));
						MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
						e1.printStackTrace();
					} catch (IOException e1) {
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
			
			try {
				kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Iniciando proceso de análisis</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (BadLocationException e1) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e1.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
				e1.printStackTrace();
			} catch (IOException e1) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e1.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
				e1.printStackTrace();
			}
			
			//mediador.ejecutaVentana(selection, imgPanel, progressBar); primera opción
			mediador.ejecutaVentanaOpcionRegiones(selection, imgPanel, progressBar);	//segunda opción
			
			if(!parado){
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Proceso de análisis finalizado correctamente</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				} catch (IOException e1) {
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
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"stop\"> Proceso detenido</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				} catch (IOException e1) {
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
			JDialog dialogo = new JDialog(frame, "Escoja opción", true);
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
	        					
	        					try {
									kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Directorio incorrecto</p><br>", 0, 0, null);
									txtLog.setCaretPosition(txtLog.getDocument().getLength());
								} catch (BadLocationException e1) {
									Date date = new Date();
									StringWriter sWriter = new StringWriter();
									e1.printStackTrace(new PrintWriter(sWriter));
									MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
									e1.printStackTrace();
								} catch (IOException e1) {
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
		        						try {
											kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Error</p><br>", 0, 0, null);
											txtLog.setCaretPosition(txtLog.getDocument().getLength());
										} catch (BadLocationException e) {
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
        				
	        				try {
								kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> ARFF abierto correctamente</p><br>", 0, 0, null);
								txtLog.setCaretPosition(txtLog.getDocument().getLength());
							} catch (BadLocationException e1) {
								Date date = new Date();
								StringWriter sWriter = new StringWriter();
								e1.printStackTrace(new PrintWriter(sWriter));
								MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
								e1.printStackTrace();
							} catch (IOException e1) {
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
	        		    	    	try {
										kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Error</p><br>", 0, 0, null);
										txtLog.setCaretPosition(txtLog.getDocument().getLength());
									} catch (BadLocationException e) {
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
	
	private class SalirListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	frmXraydetector.dispose();
	    }
	}
	
	private class OpcionesAvanzadasListener implements ActionListener{
		    
			private Frame frame;
			private JSlider slider;
			private JComboBox<String> comboBoxVentana;
			
			public OpcionesAvanzadasListener(Frame fr) {
				frame = fr;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				
				JDialog dialogo = new JDialog(frame, "Opciones avanzadas", true);
				
				
				JInternalFrame frmOpcionesAvanzadas = new JInternalFrame ();
				frmOpcionesAvanzadas.setTitle("Opciones avanzadas");
				frmOpcionesAvanzadas.setBounds(100, 100, 380, 170);
				frmOpcionesAvanzadas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frmOpcionesAvanzadas.getContentPane().setLayout(null);
				
				
				JLabel lblTamaoDeVentana = new JLabel("Tama\u00F1o de ventana");
				lblTamaoDeVentana.setBounds(22, 11, 119, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDeVentana);
				
				JLabel lblTamaoDelSalto = new JLabel("Tama\u00F1o del salto (%)");
				lblTamaoDelSalto.setBounds(22, 46, 119, 20);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDelSalto);
				
				comboBoxVentana = new JComboBox<String>();
				comboBoxVentana.setModel(new DefaultComboBoxModel<String>(new String[] {"8", "12", "16", "24", "32", "48"}));
				comboBoxVentana.setSelectedIndex(0);
				comboBoxVentana.setBounds(149, 8, 52, 20);
				comboBoxVentana.addActionListener(new ComboBoxTamVentanaListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxVentana);
				tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());
				
				slider = new JSlider();
				slider.setMajorTickSpacing(10);
				slider.setMinorTickSpacing(1);
				slider.setMinimum(10);
				slider.setPaintLabels(true);
				slider.setPaintTicks(true);
				slider.setBounds(141, 36, 213, 44);
				slider.addChangeListener(new SliderSaltoListener());
				saltoOpciones = 0.5;
				frmOpcionesAvanzadas.getContentPane().add(slider);
				
				JButton btnAceptar = new JButton("Aceptar");
				btnAceptar.setBounds(94, 102, 89, 23);
				btnAceptar.addActionListener(new AceptarOpcionesListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnAceptar);
				
				JButton btnCancelar = new JButton("Cancelar");
				btnCancelar.setBounds(193, 102, 89, 23);
				btnCancelar.addActionListener(new CancelarListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnCancelar);
				
				JDesktopPane desktoppane = new JDesktopPane();
				desktoppane.add(frmOpcionesAvanzadas);
				dialogo.getContentPane().add(frmOpcionesAvanzadas.getContentPane());
				dialogo.setMinimumSize(new Dimension(380, 170));
				dialogo.setResizable(false);
				dialogo.pack();
				dialogo.setLocationRelativeTo(frame);
				dialogo.setVisible(true);
			}
			
			private class SliderSaltoListener implements ChangeListener{

				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					if (!slider.getValueIsAdjusting()) {
						saltoOpciones = (double)slider.getValue()/100;
					}
					
				}		
			}
			
			private class ComboBoxTamVentanaListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());					
				}
			}
	}
	
	private class AceptarOpcionesListener implements ActionListener{
		
		private JDialog dialog;
		
		public AceptarOpcionesListener(JDialog dialog){
			this.dialog = dialog;
		}
		
	    public void actionPerformed (ActionEvent e){
	    	prop.setSalto(saltoOpciones);
	    	prop.setTamVentana(tamVentanaOpciones);
	    	dialog.dispose();
	    }
	}
	
	private class LimpiarLogListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	txtLog.setText("<!DOCTYPE html>" +
					"<html>"+
					"<head>"+
					"<style>"+
					"p.normal {font-weight:normal;}"+
					"p.error {font-weight:bold; color:red}"+
					"p.exito {font-weight:bold; color:green}"+
					"p.stop {font-weight:bold; color:blue}"+
					"</style>"+
					"</head>"+
					"<body>");
	    }
	}
	
	private class ExportarLogListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	String s = txtLog.getText();
	    	s += "</body></html>";
	    	try {
				FileUtils.writeStringToFile(new File("./res/log/html/log.html"), s);
				kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Log exportado con éxito</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (IOException e1) {
				e1.printStackTrace();
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Fallo al exportar el log</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e2) {
					e2.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}				
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
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
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Inicio del proceso de entrenamiento</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
				mediador.ejecutaEntrenamiento(arff, null);
				
				if(!parado){
					
					try {
						kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Modelo entrenado correctamente" +
								"con el ARFF especificado</p><br>", 0, 0, null);
						txtLog.setCaretPosition(txtLog.getDocument().getLength());
					} catch (BadLocationException e) {
						throw new RuntimeException(e);
					} catch (IOException e) {
						throw new RuntimeException(e);
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
			else{	//entrenar con imágenes
				// Listado de archivos del directorio de originales
							
				String originalList[] = originalDirectory.list();
				Arrays.sort(originalList);
				
				// Listado de archivos del directorio de mascaras
				String maskList[] = maskDirectory.list();
				Arrays.sort(maskList);
				
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				
				if (originalList.length != maskList.length) {
					mostrarErrorNumImagenes(originalList, maskList, sa);
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
						mostrarErrorNombresFicheros(sa);
					}
					else {						
						try {
							kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Directorio abierto correctamente" +
									"con el ARFF especificado</p><br>", 0, 0, null);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						
						File[] originalFiles = originalDirectory.listFiles();
						File[] maskFiles = maskDirectory.listFiles();
						originalList = new String[originalFiles.length];
						maskList = new String[maskFiles.length];
						
						for(int i=0; i<originalFiles.length; i++){
							originalList[i] = originalFiles[i].getAbsolutePath();
							maskList[i] = maskFiles[i].getAbsolutePath();
						}
						
						try {
							kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Iniciando proceso de entrenamiento" +
									"con el ARFF especificado</p><br>", 0, 0, null);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						
						mediador.ejecutarEntrenamientoDirectorio(originalList, maskList, progressBar, txtLog, kit, doc);
						
						if(!parado){
														
							try {
								kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Proceso de entrenamiento finalizado con éxito </p><br>", 0, 0, null);
								txtLog.setCaretPosition(txtLog.getDocument().getLength());
							} catch (BadLocationException e) {
								throw new RuntimeException(e);
							} catch (IOException e) {
								throw new RuntimeException(e);
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

		private void mostrarErrorNombresFicheros(SimpleAttributeSet sa) {
			JOptionPane
					.showMessageDialog(
							null,
							"Los nombres de las imágenes originales no coinciden con los de las máscaras.",
							"Error", 1);
			try {
				kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
						"Los nombres de las imágenes originales no coinciden con los de las máscaras</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (BadLocationException e1) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e1.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
				e1.printStackTrace();
			} catch (IOException e1) {
				Date date = new Date();
				StringWriter sWriter = new StringWriter();
				e1.printStackTrace(new PrintWriter(sWriter));
				MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
				e1.printStackTrace();
			}
		}

		private void mostrarErrorNumImagenes(String[] originalList,
				String[] maskList, SimpleAttributeSet sa) {
			
			if (originalList.length > maskList.length){
				JOptionPane.showMessageDialog(null,
						"El número de imágenes originales y máscaras no coinciden.\n"
								+ "Faltan máscaras", "Error", 1);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
							"Faltan máscaras</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				} catch (IOException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				}
			}
			else{
				JOptionPane.showMessageDialog(null,
						"El número de imágenes originales y máscaras no coinciden.\n"
								+ "Faltan originales", "Error", 1);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
							"Faltan imágenes originales</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				} catch (IOException e1) {
					Date date = new Date();
					StringWriter sWriter = new StringWriter();
					e1.printStackTrace(new PrintWriter(sWriter));
					MyLogHandler.getLogger().logrb(Level.SEVERE, date.toString(), "Error: ", sWriter.getBuffer().toString(), e1.toString());
					e1.printStackTrace();
				}
			}
		}		
	}
}
