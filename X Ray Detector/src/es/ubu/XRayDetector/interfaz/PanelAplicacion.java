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
 * PanelAplicacion.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.interfaz;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


import org.apache.commons.io.FileUtils;

import es.ubu.XRayDetector.modelo.Fachada;
import es.ubu.XRayDetector.utils.Graphic;
import es.ubu.XRayDetector.utils.MyLogHandler;
import es.ubu.XRayDetector.utils.Propiedades;

import javax.swing.SwingConstants;

/**
 * Class PanelAplicacion.
 * 
 * Class for the main panel application.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class PanelAplicacion{

	/**
	 * The principal frame of the application.
	 */
	private JFrame frmXraydetector;
	/**
	 * The principal panel of the application.
	 */
	private JPanel panelProgreso_1;
	/**
	 * Object graphic for draw on a panel and handle mouse listeners.
	 */
	private Graphic imgPanel;
	/**
	 * The aplplication log.
	 */
	private JTextPane txtLog;
	/**
	 * Analyze button.
	 */
	private JButton btnAnalizar;
	/**
	 * Facade between GUI and model objects.
	 */
	private static Fachada fachada;
	/**
	 * GUI thread.
	 */
	private Thread thread;
	/**
	 * Selection established over the image viewer. 
	 */
	private Rectangle selection;
	/**
	 * Copy of the selection.
	 */
	private Rectangle selectionCopy;
	/**
	 * Model used/created by the application for trainning and analyzing tasks.  
	 */
	private File model;
	/**
	 * ARFF file created/used for training task. Contains the instances.
	 */
	private File arff;
	/**
	 * Progress Bar to show the progress state of the application.
	 */
	private JProgressBar progressBar;
	/**
	 * Path of the directory masks.
	 */
	private File maskDirectory;
	/**
	 * Path of the directory images.
	 */
	private File originalDirectory;
	/**
	 * Verify if an image and a mask refers to the same image.
	 */
	private boolean sameFiles = false;
	/**
	 * Trainning button.
	 */
	private JButton btnEntrenarClasificador;
	/**
	 * Open image button.
	 */
	private JButton btnAbrirImagen;
	/**
	 * Verify if an image is open.
	 */
	private boolean imagenAbierta;
	/**
	 * Stop button. 
	 */
	private JButton btnStop;
	/**
	 * The image loaded.
	 */
	private ImagePlus img;
	/**
	 * Verify if a task is running
	 */
	private boolean parado = false;
	/**
	 * The application properties.
	 * 
	 */
	private static Propiedades prop;
	/**
	 * The tolerance edge slider.
	 */
	private JSlider slider;
	/**
	 * HTML kit for the log.
	 */
	private HTMLEditorKit kit;
    /**
     * HTML document for the log.
     */
    private HTMLDocument doc;
    /**
     * The panel log.
     */
    private JPanel panelLog_1;
    /**
     * Clear log button.
     */
    private JButton btnLimpiarLog;
    /**
     * Export log button.
     */
    private JButton btnExportarLog;
    /**
     * Save analyzed image button.
     */
    private JButton btnGuardarImagenAnalizada;
    /**
     * Save binary image button.
     */
    private JButton btnGuardarImagenBinarizada;
    /**
     * Precision and recall button.
     */
    private JButton btnPrecisionRecall;
    /**
     * The results table.
     */
    private JTable tablaResultados;
    /**
     * The table panel.
     */
    private JPanel panelTabla_1;
    /**
     * Copy of the edges image.
     */
    private ImagePlus copiaEdges;
    

	/**
	 * Create the panel application.
	 */
	public PanelAplicacion() {
		initialize();
		prop = Propiedades.getInstance();
		fachada = Fachada.getInstance();		
	}
	
	/**
	 * Gets the main frame application.
	 * 
	 * @return The main frame application.
	 */
	public JFrame getFrameXRayDetector(){
		return frmXraydetector;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			MyLogHandler.writeException(e);
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
		
		getPanelTabla();
		
		getTablaResultados(panelTabla_1);
		imgPanel.setFocusable(true);
		
		JPanel panelAnalizarResultados = getPanelAnalizarResultados();
		
		getButtonGuardarImagenAnalizada(panelAnalizarResultados);
					
		getButtonGuardarImagenBinarizada(panelAnalizarResultados);
		
		getButtonPrecisionRecall(panelAnalizarResultados);
		
		selectionCopy = new Rectangle();
		
		try {
			File fichero = new File("./res/ayuda/ayuda.hs");
			URL hsURL = fichero.toURI().toURL();
			HelpSet hs =  new HelpSet(null, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			hb.enableHelpKey(frmXraydetector.getContentPane(), "ventanaentrenamientodeteccion", hs);
		}
		catch (MalformedURLException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} 
		catch (HelpSetException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}
	
	}

	/**
	 * Gets the precision recall button.
	 * 
	 * @param panelAnalizarResultados Panel results to extract data results.
	 */
	public void getButtonPrecisionRecall(JPanel panelAnalizarResultados) {
		btnPrecisionRecall = new JButton("<html><CENTER>Precision & Recall</CENTER></html>");
		btnPrecisionRecall.setToolTipText("Calcula las medidas de precision & recall");
		btnPrecisionRecall.setPreferredSize(new Dimension(230, 36));
		btnPrecisionRecall.setMinimumSize(new Dimension(80, 30));		
		btnPrecisionRecall.addActionListener(new PrecisionRecallListener());
		btnPrecisionRecall.setEnabled(false);
		panelAnalizarResultados.add(btnPrecisionRecall);
	}

	/**
	 * Gets the save binary image button.
	 * 
	 * @param panelAnalizarResultados The results panel.
	 */
	public void getButtonGuardarImagenBinarizada(JPanel panelAnalizarResultados) {
		btnGuardarImagenBinarizada = new JButton("<html><CENTER>Guardar defectos<br>binarizados</CENTER></html>");
		btnGuardarImagenBinarizada.setToolTipText("Guarda una imagen de los defectos detectados binarizados");
		btnGuardarImagenBinarizada.addActionListener(new GuardarImagenBinarizadaListener());
		btnGuardarImagenBinarizada.setEnabled(false);
		panelAnalizarResultados.add(btnGuardarImagenBinarizada);
	}

	/**
	 * Gets the save analyzed image button.
	 * 
	 * @param panelAnalizarResultados The results panel.
	 */
	public void getButtonGuardarImagenAnalizada(JPanel panelAnalizarResultados) {
		btnGuardarImagenAnalizada = new JButton("<html><CENTER>Guardar imagen<br>analizada</CENTER></html>");
		btnGuardarImagenAnalizada.setToolTipText("Guarda la imagen del visor");
		btnGuardarImagenAnalizada.addActionListener(new GuardarImagenAnalizadaListener());
		btnGuardarImagenAnalizada.setEnabled(false);
		panelAnalizarResultados.add(btnGuardarImagenAnalizada);
	}

	/**
	 * Gets the analyze results.
	 * 
	 * @return The panel results.
	 */
	public JPanel getPanelAnalizarResultados() {
		JPanel panelAnalizarResultados = new JPanel();
		panelAnalizarResultados.setBorder(new TitledBorder(null, "Analizar resultados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAnalizarResultados.setBounds(10, 302, 262, 112);
		panelAnalizarResultados.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		frmXraydetector.getContentPane().add(panelAnalizarResultados);
		return panelAnalizarResultados;
	}

	/**
	 * Gets the results table panel.
	 */
	public void getPanelTabla() {
		panelTabla_1 = new JPanel();
		panelTabla_1.setBorder(new TitledBorder(null, "Tabla de resultados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTabla_1.setBounds(282, 572, 750, 173);
		frmXraydetector.getContentPane().add(panelTabla_1);
	}

	/**
	 * Gets the image results panel.
	 * 
	 * @param panelTabla Results table panel
	 */
	public void getTablaResultados(JPanel panelTabla) {
		tablaResultados = new JTable();
		tablaResultados.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Regi\u00F3n", "\u00C1rea", "Per\u00EDmetro", "Circularidad", "Redondez", "Semieje Mayor", "Semieje Menor", "\u00C1ngulo", "Distancia Feret"
			}
		) {
			private static final long serialVersionUID = 1L;
			@SuppressWarnings("rawtypes")
			Class[] columnTypes = new Class[] {
				Integer.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class, Double.class
			};
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		resizeColumnas();
		tablaResultados.setEnabled(false);
		tablaResultados.setRowSelectionAllowed(true);
		tablaResultados.addMouseListener(new TableMouseListener());
		panelTabla_1.setLayout(null);
		JScrollPane scrlPane = new JScrollPane(tablaResultados);
		scrlPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrlPane.setBounds(10, 21, 730, 141);
		panelTabla.add(scrlPane);
		imgPanel.setTablaResultados(tablaResultados);
	}

	/**
	 * Resize the columns width.
	 */
	public void resizeColumnas() {
		tablaResultados.getColumnModel().getColumn(0).setPreferredWidth(60);
		tablaResultados.getColumnModel().getColumn(1).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(2).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(3).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(4).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(5).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(6).setPreferredWidth(100);
		tablaResultados.getColumnModel().getColumn(7).setPreferredWidth(60);
		tablaResultados.getColumnModel().getColumn(8).setPreferredWidth(100);
	}

	/**
	 * Gets the clear log button.
	 */
	public void getBotonLimpiarLog() {
		btnLimpiarLog = new JButton("Limpiar Log");
		btnLimpiarLog.setToolTipText("Limpia el \u00E1rea de texto del log");
		btnLimpiarLog.setMaximumSize(new Dimension(95, 30));
		btnLimpiarLog.setMinimumSize(new Dimension(95, 30));
		btnLimpiarLog.setPreferredSize(new Dimension(95, 30));
		btnLimpiarLog.setBounds(10, 281, 116, 30);
		btnLimpiarLog.addActionListener(new LimpiarLogListener());
		btnLimpiarLog.setIcon(new ImageIcon("./res/img/app/trash.png"));
		btnLimpiarLog.setHorizontalTextPosition(SwingConstants.LEFT);
		btnLimpiarLog.setIconTextGap(5);
		panelLog_1.add(btnLimpiarLog);
	}

	/**
	 * Gets the export log button.
	 */
	public void getBotonExportarLog() {
		btnExportarLog = new JButton("Exportar");
		btnExportarLog.setToolTipText("Permite exportar el log a un HTML");
		btnExportarLog.setBounds(136, 281, 116, 30);
		btnExportarLog.addActionListener(new ExportarLogListener());
		btnExportarLog.setIcon(new ImageIcon("./res/img/app/exporthtml.png"));
		btnExportarLog.setHorizontalTextPosition(SwingConstants.LEFT);
		btnExportarLog.setIconTextGap(5);
		panelLog_1.add(btnExportarLog);
	}

	/**
	 * Gets the tolerance slider.
	 * 
	 * @param panelSlider Panel of the slider.
	 */
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

	/**
	 * Gets the slider panel.
	 * 
	 * @return the slider panel
	 */
	public JPanel getPanelSlider() {
		JPanel panelSlider = new JPanel();
		panelSlider.setBorder(new TitledBorder(null, "Nivel de tolerancia", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelSlider.setBounds(10, 218, 262, 73);
		frmXraydetector.getContentPane().add(panelSlider);
		panelSlider.setLayout(new GridLayout(1, 1, 0, 0));
		return panelSlider;
	}

	/**
	 * Gets the image panel with the image loaded.
	 * 
	 * @param panelImagen The image panel.
	 */
	private void getImgPanel(JPanel panelImagen) {
		ImagePlus imagAux = new ImagePlus("./res/img/app/logoGris.png");
		Image imag = imagAux.getImage();
		
		imgPanel = new Graphic(imag);
		panelImagen.add(imgPanel);
	}

	/**
	 * Gets the application log.
	 * 
	 * @param panelLog The panel of the log.
	 * @return The application log.
	 */
	private JTextPane getTxtLog(JPanel panelLog) {
		JTextPane textPaneLog = new JTextPane();
		textPaneLog.setBounds(4, 4, 209, 195);
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
		scroll.setBounds(10, 16, 242, 254);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panelLog.add(scroll);
		return textPaneLog;
	}

	/**
	 * Gets the application panel log.
	 * 
	 * @return the application panel log.
	 */
	private JPanel getPanelLog() {
		panelLog_1 = new JPanel();
		panelLog_1.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog_1.setBounds(10, 425, 262, 320);
		frmXraydetector.getContentPane().add(panelLog_1);
		return panelLog_1;
	}

	/**
	 * Gets the image panel.
	 * 
	 * @return the image panel.
	 */
	private JPanel getPanelImagen() {
		JPanel panelImagen = new JPanel();
		panelImagen.setBorder(new TitledBorder(null, "Visor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelImagen.setBounds(282, 11, 750, 550);
		frmXraydetector.getContentPane().add(panelImagen);
		panelImagen.setLayout(new GridLayout(1, 0, 0, 0));
		return panelImagen;
	}

	/**
	 * Gets the stop button.
	 * 
	 * @param panelProgreso The progress panel.
	 */
	private void getBtnStop(JPanel panelProgreso) {
		btnStop = new JButton();
		btnStop.setToolTipText("Detiene el proceso en ejecuci\u00F3n");
		btnStop.setPreferredSize(new Dimension(60, 30));
		btnStop.setMinimumSize(new Dimension(60, 30));
		btnStop.setMaximumSize(new Dimension(60, 30));
		btnStop.addActionListener(new StopListener());
		btnStop.setEnabled(false);
		btnStop.setIcon(new ImageIcon("./res/img/app/stop.png"));
		panelProgreso.add(btnStop);
	}

	/**
	 * Gets the progress bar.
	 * 
	 * @param panelProgreso The panel progress,
	 */
	private void getProgressBar(JPanel panelProgreso) {
		panelProgreso_1.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 5));
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(150, 16));
		panelProgreso.add(progressBar);
	}

	/**
	 * GHets the progress panel.
	 * 
	 * @return the progress panel.
	 */
	private JPanel getPanelProgreso() {
		panelProgreso_1 = new JPanel();
		panelProgreso_1.setBorder(new TitledBorder(null, "Progreso", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelProgreso_1.setBounds(10, 147, 262, 60);
		frmXraydetector.getContentPane().add(panelProgreso_1);
		return panelProgreso_1;
	}

	/**
	 * Gets the analyze button.
	 * 
	 * @param panelControl The control panel.
	 */
	private void getBtnAnalizar(JPanel panelControl) {
		btnAnalizar = new JButton("Analizar imagen");
		btnAnalizar.setToolTipText("Inicia el proceso de an\u00E1lisis sobre la imagen");
		btnAnalizar.setEnabled(false);
		btnAnalizar.addActionListener(new AnalizarImagenListener());
		btnAnalizar.setIcon(new ImageIcon("./res/img/app/play.png"));
		btnAnalizar.setHorizontalTextPosition(SwingConstants.LEFT);
		btnAnalizar.setIconTextGap(10);
		panelControl.add(btnAnalizar);
	}

	/**
	 * Gets the trainning button.
	 * 
	 * @param panelControl The control panel.
	 */
	private void getBtnEntrenar(JPanel panelControl) {
		btnEntrenarClasificador = new JButton("Entrenar clasificador");
		btnEntrenarClasificador.setToolTipText("Permite entrenar un clasificador");
		btnEntrenarClasificador.addActionListener(new EntrenarListener(frmXraydetector));
		btnEntrenarClasificador.setIcon(new ImageIcon("./res/img/app/weka.png"));
		btnEntrenarClasificador.setHorizontalTextPosition(SwingConstants.LEFT);
		btnEntrenarClasificador.setIconTextGap(10);
		panelControl.add(btnEntrenarClasificador);
	}

	/**
	 * Gets the load image button.
	 * 
	 * @param panelControl The control panel.
	 */
	private void getBtnAbrirImagen(JPanel panelControl) {
		btnAbrirImagen = new JButton("Abrir imagen");
		btnAbrirImagen.setIconTextGap(10);
		btnAbrirImagen.setIcon(new ImageIcon("./res/img/app/folderopen.png"));
		btnAbrirImagen.setToolTipText("Permite seleccionar una imagen para analizar");
		btnAbrirImagen.addActionListener(new CargarImagenListener());
		btnAbrirImagen.setHorizontalTextPosition(SwingConstants.LEFT);
		panelControl.add(btnAbrirImagen);
	}

	/**
	 * Gets the control panel.
	 * 
	 * @return the control panel.
	 */
	private JPanel getPanelControl() {
		JPanel panelControl = new JPanel();
		panelControl.setBorder(new TitledBorder(null, "Control", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelControl.setBounds(10, 11, 262, 125);
		frmXraydetector.getContentPane().add(panelControl);
		panelControl.setLayout(new GridLayout(0, 1, 0, 4));
		return panelControl;
	}

	/**
	 * Gets the application main frame.
	 */
	public void getJFramePrincipal() {
		frmXraydetector = new JFrame();
		frmXraydetector.setTitle("XRayDetector");
		frmXraydetector.setBounds(100, 100, 1048, 800);
		frmXraydetector.setResizable(false);
		frmXraydetector.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmXraydetector.getContentPane().setLayout(null);
		frmXraydetector.setLocationRelativeTo(null);
		frmXraydetector.setIconImage(new ImageIcon("./res/img/app/logoXRayDetector.png").getImage());
	}
	
	/**
	 * Class CargarImagenListener.
	 * 
	 * Class for the load imagen listener.
	 * 
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class CargarImagenListener implements ActionListener{
	    
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
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
				fachada.cargaImagen(image.getAbsolutePath());
				img = new ImagePlus(image.getAbsolutePath());
				imgPanel.setImage(img.getImage());
				imgPanel.repaint();
				imgPanel.setFlagTrabajando(false);
				selection = imgPanel.coordenates();
				btnAnalizar.setEnabled(true);
				slider.setEnabled(false);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Imagen abierta correctamente</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				} catch (IOException e1) {
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				}
			}
	    }
	}
	
	/**
	 * Class SliderListener.
	 * 
	 * Class for the tolerance slider listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class SliderListener implements ChangeListener{

		/* (non-Javadoc)
		 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
		 */
		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (!slider.getValueIsAdjusting()) {
				prop.setUmbral(slider.getValue());
				
				int opcion = prop.getTipoDeteccion();
				
				switch(opcion){
					case 0:
						fachada.drawEdge(imgPanel);	//normal
						break;
					case 1:
						fachada.drawEdge(imgPanel);	//normal + umbrales locales
						break;
					case 2:
						fachada.drawEdgeRegiones(imgPanel);	//blancos en umbrales locales
						break;
				}
				tablaResultados.setModel(fachada.getTableModel());
				resizeColumnas();
				copiaEdges = new ImagePlus("copiaEdges", imgPanel.getImage());
				imgPanel.setImageCopy(copiaEdges.getImage());
				imgPanel.grabFocus();
				imgPanel.setArrayRois(fachada.getArrayRoisCompleto());
			}
			
		}		
	}
	
	/**
	 * Class AnalizarImagenListener..
	 * 
	 * Class for the analyze image listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class AnalizarImagenListener implements ActionListener{
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
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
						MyLogHandler.writeException(e1);
						e1.printStackTrace();
					} catch (IOException e1) {
						MyLogHandler.writeException(e1);
						e1.printStackTrace();
					}
					
					slider.setEnabled(false);
					btnEntrenarClasificador.setEnabled(false);
					btnAbrirImagen.setEnabled(false);
					btnAnalizar.setEnabled(false);
					btnStop.setEnabled(true);
					btnExportarLog.setEnabled(false);
					btnLimpiarLog.setEnabled(false);
					tablaResultados.setEnabled(false);
					btnGuardarImagenAnalizada.setEnabled(false);
					btnGuardarImagenBinarizada.setEnabled(false);
					btnPrecisionRecall.setEnabled(false);
					imgPanel.setImage(img.getImage());
					imgPanel.repaint();
					imgPanel.setFlagTrabajando(true);
					selectionCopy.height = selection.height;
					selectionCopy.width = selection.width;
					selectionCopy.x = selection.x;
					selectionCopy.y = selection.y;
					((DefaultTableModel) (tablaResultados.getModel())).getDataVector().clear();
					((DefaultTableModel) (tablaResultados.getModel())).fireTableDataChanged();
					ThreadAnalizar threadAnalizar = new ThreadAnalizar();
			    	thread = new Thread(threadAnalizar);
			    	Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
    		    	    public void uncaughtException(Thread th, Throwable ex) {
    		    	    	if(!ex.getClass().toString().contains("ThreadDeath")){
	    		    	    	JOptionPane.showMessageDialog(
	    		    	    			   null,
	    		    	    			   "Se ha producido un error: "+ ex.getMessage(),
	    		    	    			   "Error",
	    		    	    			   JOptionPane.ERROR_MESSAGE);
	    		    	    	parado = false;
	    		    			btnEntrenarClasificador.setEnabled(true);
	    		    			btnAbrirImagen.setEnabled(true);
	    		    			btnStop.setEnabled(false);
	    		    			btnExportarLog.setEnabled(true);
	    		    			btnLimpiarLog.setEnabled(true);
	    		    			tablaResultados.setEnabled(true);
	    		    			imgPanel.setFlagTrabajando(false);
	    		    			
	    		    			if(imagenAbierta){
	    		    				btnAnalizar.setEnabled(true);
	    		    			}
	    		    			else{
	    		    				btnAnalizar.setEnabled(false);
	    		    			}
	    						try {
									kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Error</p><br>", 0, 0, null);
									txtLog.setCaretPosition(txtLog.getDocument().getLength());
								} catch (BadLocationException e) {
									MyLogHandler.writeException(e);
									e.printStackTrace();
								} catch (IOException e) {
									MyLogHandler.writeException(e);
									e.printStackTrace();
								}
    		    	    	}
    		    	    }
    		    	};
    		    	thread.setUncaughtExceptionHandler(h);
			    	thread.start();
				}
	    	}
	    }
	}
	
	/**
	 * Class ThreadAnalizar.
	 * 
	 * Class for handling the analyze thread.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class ThreadAnalizar implements Runnable{

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			
			try {
				kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Iniciando proceso de análisis</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (BadLocationException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			} catch (IOException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			}
			
			int opcion = prop.getTipoDeteccion();
			
			switch(opcion){
				case 0:
					fachada.ejecutaVentana(selection, imgPanel, progressBar); //normal
					break;
				case 1:
					fachada.ejecutaVentana(selection, imgPanel, progressBar); //normal + umbrales locales -> la llamada es igual
					break;
				case 2:
					fachada.ejecutaVentanaOpcionRegiones(selection, imgPanel, progressBar);	//segunda opción
					break;
			}
			
			if(fachada.getExcepcion() != null){	//se han producido excepciones
    	    	System.out.println("2 " + fachada.getExcepcion().getClass().toString());
				RuntimeException e = fachada.getExcepcion();
				fachada.setExcepcion(null);
				throw e;				
			}
			else{
				
				if(!parado){
					tablaResultados.setModel(fachada.getTableModel());
					resizeColumnas();
					copiaEdges = new ImagePlus("copiaEdges", imgPanel.getImage());
					imgPanel.setImageCopy(copiaEdges.getImage());
					imgPanel.grabFocus();
					imgPanel.setArrayRois(fachada.getArrayRoisCompleto());
					try {
						kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Proceso de análisis finalizado correctamente</p><br>", 0, 0, null);
						txtLog.setCaretPosition(txtLog.getDocument().getLength());
					} catch (BadLocationException e1) {
						MyLogHandler.writeException(e1);
						e1.printStackTrace();
					} catch (IOException e1) {
						MyLogHandler.writeException(e1);
						e1.printStackTrace();
					}
				}
			}
				
			parado = false;
			btnEntrenarClasificador.setEnabled(true);
			btnAbrirImagen.setEnabled(true);
			btnStop.setEnabled(false);
			slider.setValue(prop.getUmbral());
			slider.setEnabled(true);
			btnExportarLog.setEnabled(true);
			btnLimpiarLog.setEnabled(true);
			tablaResultados.setEnabled(true);
			btnGuardarImagenAnalizada.setEnabled(true);
			btnGuardarImagenBinarizada.setEnabled(true);
			btnPrecisionRecall.setEnabled(true);
			imgPanel.setFlagTrabajando(false);
			if(imagenAbierta){
				btnAnalizar.setEnabled(true);
			}
			else{
				btnAnalizar.setEnabled(false);
			}
		}		
	}
	
	/**
	 * Class StopListener.
	 * 
	 * Class for handling the stop listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class StopListener implements ActionListener{
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
	    @SuppressWarnings("deprecation")
		public void actionPerformed (ActionEvent e){
	    	if (thread != null){
	    		parado = true;
	    		if(fachada.getNumThreads() > 0){
	    			fachada.stop();
	    		}
	    		slider.setEnabled(false);
	    		btnEntrenarClasificador.setEnabled(true);
				btnAbrirImagen.setEnabled(true);
				btnStop.setEnabled(false);
				btnExportarLog.setEnabled(true);
				btnLimpiarLog.setEnabled(true);
				tablaResultados.setEnabled(false);
				imgPanel.setFlagTrabajando(false);
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
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				} catch (IOException e1) {
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				}
				
				thread.stop();
	    	}
	    }
	}
	
	/**
	 * Class EntrenarListener.
	 * 
	 * Class for handling training listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class EntrenarListener implements ActionListener{
	    
		/**
		 * Frame for the training options dialog.
		 */
		private Frame frame;
		
		/**
		 * Constructor method.
		 * 
		 * @param fr The dialog prame.
		 */
		public EntrenarListener(Frame fr) {
			frame = fr;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
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
	
	/**
	 * Class CancelarListener.
	 * 
	 * Class for handling the cancel option listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class CancelarListener implements ActionListener{
		
		/**
		 * Dialog to be canceled.
		 */
		private JDialog dial;
		
	    /**
	     * Constructor method.
	     * 
	     * @param dialogo The dialog to be canceled.
	     */
	    public CancelarListener(JDialog dialogo) {
			dial = dialogo;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed (ActionEvent e){
	    	dial.setVisible(false);
	    }
	}
	
	/**
	 * Class AceptarListener.
	 * 
	 * Class for handling the accept option listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class AceptarListener implements ActionListener{
		
		/**
		 * Dialog to be accepted
		 */
		private JDialog dial;
		/**
		 * Options to be accepted.
		 */
		private Enumeration<AbstractButton> listaBtn;
		
	    /**
	     * Constructor method.
	     * 
	     * @param dialogo Dialog to be accepted.
	     * @param list The option to be accepted.
	     */
	    public AceptarListener(JDialog dialogo, Enumeration<AbstractButton> list) {
			dial = dialogo;
			listaBtn = list;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
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
									MyLogHandler.writeException(e1);
									e1.printStackTrace();
								} catch (IOException e1) {
									MyLogHandler.writeException(e1);
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
		        		    	    	if(!ex.getClass().toString().contains("ThreadDeath")){
			        		    	    	JOptionPane.showMessageDialog(
			        		    	    			   null,
			        		    	    			   "Se ha producido un error: "+ ex.getMessage(),
			        		    	    			   "Error",
			        		    	    			   JOptionPane.ERROR_MESSAGE);
			        						try {
												kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Error</p><br>", 0, 0, null);
												txtLog.setCaretPosition(txtLog.getDocument().getLength());
											} catch (BadLocationException e) {
												MyLogHandler.writeException(e);
												e.printStackTrace();
											} catch (IOException e) {
												MyLogHandler.writeException(e);
												e.printStackTrace();
											}
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
								MyLogHandler.writeException(e1);
								e1.printStackTrace();
							} catch (IOException e1) {
								MyLogHandler.writeException(e1);
								e1.printStackTrace();
							}

	        				ThreadEntrenar threadEntrenar = new ThreadEntrenar(true);
	        		    	thread = new Thread(threadEntrenar);
	        				btnEntrenarClasificador.setEnabled(false);
	        				btnAbrirImagen.setEnabled(false);
	        				btnAnalizar.setEnabled(false);
	        				btnStop.setEnabled(true);
	        				slider.setEnabled(false);
	        				btnExportarLog.setEnabled(false);
	    					btnLimpiarLog.setEnabled(false);
	    					tablaResultados.setEnabled(false);
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
										MyLogHandler.writeException(e);
										e.printStackTrace();
									} catch (IOException e) {
										MyLogHandler.writeException(e);
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
	
	/**
	 * Class LimpiarLogListener.
	 * 
	 * Class for handling the log cleaning.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class LimpiarLogListener implements ActionListener{
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
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
	
	/**
	 * Class ExportarLogListener.
	 * 
	 * Class for handling the data log export log.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class ExportarLogListener implements ActionListener{
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
	    public void actionPerformed (ActionEvent e){
	    	String s = txtLog.getText();
	    	s += "</body></html>";
	    	try {
				FileUtils.writeStringToFile(new File("./res/log/html/log.html"), s);
				kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Log exportado con éxito</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (IOException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Fallo al exportar el log</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e2) {
					MyLogHandler.writeException(e2);
					e2.printStackTrace();
				} catch (IOException e2) {
					MyLogHandler.writeException(e2);
					e2.printStackTrace();
				}				
			} catch (BadLocationException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			}
	    }
    }
	
	/**
	 * Class GuardarImagenAnalizadaListener.
	 * 
	 * Class for handling the analyzed image saving.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class GuardarImagenAnalizadaListener implements ActionListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed (ActionEvent e){
	    	
	    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la ubicación para guardar la imagen analizada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes JPG", "jpg");
	    	chooser.setFileFilter(filter);
	    	chooser.setAcceptAllFileFilterUsed(false);
	    	int answer = chooser.showSaveDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				File JFC = chooser.getSelectedFile();
                String PATH = JFC.getAbsolutePath();//obtenemos el path del archivo a guardar
                ImagePlus i = new ImagePlus("img", imgPanel.getImage());                
                IJ.saveAs(i, "JPG", PATH);
                try {
    				kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Imagen exportada correctamente</p><br>", 0, 0, null);
    				txtLog.setCaretPosition(txtLog.getDocument().getLength());
    			} catch (BadLocationException e1) {
    				MyLogHandler.writeException(e1);
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				MyLogHandler.writeException(e1);
    				e1.printStackTrace();
    			}
			}
	    }
	}

	/**
	 * Class GuardarImagenBinarizadaListener.
	 * 
	 * Class for handling the binaryzed image saving.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class GuardarImagenBinarizadaListener implements ActionListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed (ActionEvent e){
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la ubicación para guardar la imagen binarizada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes JPG", "jpg");
	    	chooser.setFileFilter(filter);
	    	chooser.setAcceptAllFileFilterUsed(false);
	    	int answer = chooser.showSaveDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				File JFC = chooser.getSelectedFile();
                String PATH = JFC.getAbsolutePath();//obtenemos el path del archivo a guardar                
                IJ.saveAs(fachada.getImageBinarizada(), "JPG", PATH);
                try {
    				kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Imagen exportada correctamente</p><br>", 0, 0, null);
    				txtLog.setCaretPosition(txtLog.getDocument().getLength());
    			} catch (BadLocationException e1) {
    				MyLogHandler.writeException(e1);
    				e1.printStackTrace();
    			} catch (IOException e1) {
    				MyLogHandler.writeException(e1);
    				e1.printStackTrace();
    			}
			}
	    }
	}
	
	/**
	 * Class PrecisionRecallListener.
	 * 
	 * Class for handling the precision and recall listener.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class PrecisionRecallListener implements ActionListener{
		
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed (ActionEvent e){
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la máscara correspondiente a la imagen");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes JPG", "jpg");
	    	chooser.setFileFilter(filter);
	    	chooser.setAcceptAllFileFilterUsed(false);
	    	int answer = chooser.showOpenDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				File JFC = chooser.getSelectedFile();
                String PATH = JFC.getAbsolutePath();
                PrecisionRecallDialog diag = new PrecisionRecallDialog(fachada.getPrecisionRecall(PATH, selectionCopy));
                diag.setLocationRelativeTo(frmXraydetector);
                diag.setVisible(true);                
			}
	    }
	}


	/**
	 * Class ThreadEntrenar.
	 * 
	 * Class for handling the training thread.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class ThreadEntrenar implements Runnable{
		
		/**
		 * Verify if the training is from an ARFF file or not.
		 */
		private boolean entrenarConArff;

		/**
		 * Constructor method.
		 * 
		 * @param b ARFF file is used.
		 */
		public ThreadEntrenar(boolean b) {
			super();
			entrenarConArff = b;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
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
				
				fachada.ejecutaEntrenamiento(arff, null);
				
				if(fachada.getExcepcion() != null){	//se han producido excepciones
					RuntimeException e = fachada.getExcepcion();
					fachada.setExcepcion(null);
					throw e;				
				}
				else{
					
					if(!parado){
						
						try {
							kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Modelo entrenado correctamente" +
									" con el ARFF especificado</p><br>", 0, 0, null);
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
					btnExportarLog.setEnabled(true);
					btnLimpiarLog.setEnabled(true);
					tablaResultados.setEnabled(false);
					if(imagenAbierta){
						btnAnalizar.setEnabled(true);
					}
					else{
						btnAnalizar.setEnabled(false);
					}
				}
			}
			else{	//entrenar con imágenes
				// Listado de archivos del directorio de originales
							
				String originalList[] = originalDirectory.list();
				Arrays.sort(originalList);
				
				// Listado de archivos del directorio de mascaras
				String maskList[] = maskDirectory.list();
				Arrays.sort(maskList);
				
				if (originalList.length != maskList.length) {
					mostrarErrorNumImagenes(originalList, maskList);
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
						mostrarErrorNombresFicheros();
					}
					else {						
						try {
							kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Directorio abierto correctamente</p><br>", 0, 0, null);
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
									" con el nuevo ARFF especificado</p><br>", 0, 0, null);
							txtLog.setCaretPosition(txtLog.getDocument().getLength());
						} catch (BadLocationException e) {
							throw new RuntimeException(e);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						
						fachada.ejecutarEntrenamientoDirectorio(originalList, maskList, progressBar, txtLog, kit, doc);
						
						if(fachada.getExcepcion() != null){	//se han producido excepciones
							RuntimeException e = fachada.getExcepcion();
							fachada.setExcepcion(null);
							throw e;				
						}
						else{						
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
				}
				parado = false;
				btnEntrenarClasificador.setEnabled(true);
				btnAbrirImagen.setEnabled(true);
				btnStop.setEnabled(false);
				btnExportarLog.setEnabled(true);
				btnLimpiarLog.setEnabled(true);
				tablaResultados.setEnabled(false);
				if(imagenAbierta){
					btnAnalizar.setEnabled(true);
				}
				else{
					btnAnalizar.setEnabled(false);
				}
			}
		}

		/**
		 * Verify if an image file name is equal to its mask file name.
		 */
		private void mostrarErrorNombresFicheros() {
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
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			} catch (IOException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			}
		}

		/**
		 * Show an error if the image file name and its mask file name are not equal.
		 * 
		 * @param originalList Array of images.
		 * @param maskList Array associated of masks.
		 */
		private void mostrarErrorNumImagenes(String[] originalList,
				String[] maskList) {
			
			if (originalList.length > maskList.length){
				JOptionPane.showMessageDialog(null,
						"El número de imágenes originales y máscaras no coinciden.\n"
								+ "Faltan máscaras", "Error", 1);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
							"Faltan máscaras</p><br>", 0, 0, null);
					txtLog.setCaretPosition(txtLog.getDocument().getLength());
				} catch (BadLocationException e1) {
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				} catch (IOException e1) {
					MyLogHandler.writeException(e1);
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
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				} catch (IOException e1) {
					MyLogHandler.writeException(e1);
					e1.printStackTrace();
				}
			}
		}		
	}
	
	/**
	 * Class TableMouseListener.
	 * 
	 * Class for handle a mouse clicked event in the results table.
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class TableMouseListener extends MouseAdapter{ 
		/* (non-Javadoc)
		 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e){
			
			//restaurar imagen original
			imgPanel.setImage(copiaEdges.getImage());
			imgPanel.repaint();
			
			//pintar la selección
			int fila = tablaResultados.rowAtPoint(e.getPoint());
			Roi roi = fachada.getRoi(fila);
			int alpha = 63;
			Color c = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), alpha);
			roi.setFillColor(c);
			ImagePlus im = copiaEdges.duplicate();
			im.setOverlay(new Overlay(roi));
			im = im.flatten();
			im.updateAndDraw();
			imgPanel.setImage(im.getImage());
			imgPanel.repaint();
			
			imgPanel.grabFocus();
	    }
	}
}
