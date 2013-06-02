package interfaz;

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

import modelo.Fachada;

import org.apache.commons.io.FileUtils;

import utils.Graphic;
import utils.MyLogHandler;
import utils.Propiedades;

public class PanelAplicacion{

	private JFrame frmXraydetector;
	private JPanel panelProgreso_1;
	private Graphic imgPanel;
	private JTextPane txtLog;
	private JButton btnAnalizar;
	private static Fachada fachada;
	private Thread thread;
	private Rectangle selection;
	private Rectangle selectionCopy;
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
    private JButton btnGuardarImagenAnalizada;
    private JButton btnGuardarImagenBinarizada;
    private JButton btnPrecisionRecall;
    private JTable tablaResultados;
    private JPanel panelTabla_1;
    private ImagePlus copiaEdges;
    

	/**
	 * Create the application.
	 */
	public PanelAplicacion() {
		initialize();
		prop = Propiedades.getInstance();
		fachada = Fachada.getInstance();		
	}
	
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

	public void getButtonPrecisionRecall(JPanel panelAnalizarResultados) {
		btnPrecisionRecall = new JButton("<html><CENTER>Precision & Recall</CENTER></html>");
		btnPrecisionRecall.setPreferredSize(new Dimension(230, 36));
		btnPrecisionRecall.setMinimumSize(new Dimension(80, 30));		
		btnPrecisionRecall.addActionListener(new PrecisionRecallListener());
		btnPrecisionRecall.setEnabled(false);
		panelAnalizarResultados.add(btnPrecisionRecall);
	}

	public void getButtonGuardarImagenBinarizada(JPanel panelAnalizarResultados) {
		btnGuardarImagenBinarizada = new JButton("<html><CENTER>Guardar defectos<br>binarizados</CENTER></html>");
		btnGuardarImagenBinarizada.addActionListener(new GuardarImagenBinarizadaListener());
		btnGuardarImagenBinarizada.setEnabled(false);
		panelAnalizarResultados.add(btnGuardarImagenBinarizada);
	}

	public void getButtonGuardarImagenAnalizada(JPanel panelAnalizarResultados) {
		btnGuardarImagenAnalizada = new JButton("<html><CENTER>Guardar imagen<br>analizada</CENTER></html>");
		btnGuardarImagenAnalizada.addActionListener(new GuardarImagenAnalizadaListener());
		btnGuardarImagenAnalizada.setEnabled(false);
		panelAnalizarResultados.add(btnGuardarImagenAnalizada);
	}

	public JPanel getPanelAnalizarResultados() {
		JPanel panelAnalizarResultados = new JPanel();
		panelAnalizarResultados.setBorder(new TitledBorder(null, "Analizar resultados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelAnalizarResultados.setBounds(10, 302, 262, 112);
		panelAnalizarResultados.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		frmXraydetector.getContentPane().add(panelAnalizarResultados);
		return panelAnalizarResultados;
	}

	public void getPanelTabla() {
		panelTabla_1 = new JPanel();
		panelTabla_1.setBorder(new TitledBorder(null, "Tabla de resultados", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelTabla_1.setBounds(282, 572, 750, 173);
		frmXraydetector.getContentPane().add(panelTabla_1);
	}

	public void getTablaResultados(JPanel panelTabla) {
		tablaResultados = new JTable();
		tablaResultados.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Regi\u00F3n", "\u00C1rea", "Per\u00EDmetro", "Circularidad", "Redondez", "Semieje Mayor", "Semieje Menor", "\u00C1ngulo", "Distancia Feret"
			}
		) {
			/**
			 * 
			 */
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

	public void getBotonLimpiarLog() {
		btnLimpiarLog = new JButton("Limpiar Log");
		btnLimpiarLog.setMaximumSize(new Dimension(95, 30));
		btnLimpiarLog.setMinimumSize(new Dimension(95, 30));
		btnLimpiarLog.setPreferredSize(new Dimension(95, 30));
		btnLimpiarLog.setBounds(10, 281, 116, 30);
		btnLimpiarLog.addActionListener(new LimpiarLogListener());
		panelLog_1.add(btnLimpiarLog);
	}

	public void getBotonExportarLog() {
		btnExportarLog = new JButton("Exportar Log");
		btnExportarLog.setBounds(136, 281, 116, 30);
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
		panelSlider.setBounds(10, 218, 262, 73);
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

	private JPanel getPanelLog() {
		panelLog_1 = new JPanel();
		panelLog_1.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog_1.setBounds(10, 425, 262, 320);
		frmXraydetector.getContentPane().add(panelLog_1);
		return panelLog_1;
	}

	private JPanel getPanelImagen() {
		JPanel panelImagen = new JPanel();
		panelImagen.setBorder(new TitledBorder(null, "Imagen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelImagen.setBounds(282, 11, 750, 550);
		frmXraydetector.getContentPane().add(panelImagen);
		panelImagen.setLayout(new GridLayout(1, 0, 0, 0));
		return panelImagen;
	}

	private void getBtnStop(JPanel panelProgreso) {
		btnStop = new JButton("Stop");
		btnStop.setPreferredSize(new Dimension(60, 30));
		btnStop.setMinimumSize(new Dimension(60, 30));
		btnStop.setMaximumSize(new Dimension(60, 30));
		btnStop.addActionListener(new StopListener());
		btnStop.setEnabled(false);
		panelProgreso.add(btnStop);
	}

	private void getProgressBar(JPanel panelProgreso) {
		panelProgreso_1.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 5));
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(150, 16));
		panelProgreso.add(progressBar);
	}

	private JPanel getPanelProgreso() {
		panelProgreso_1 = new JPanel();
		panelProgreso_1.setBorder(new TitledBorder(null, "Progreso", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelProgreso_1.setBounds(10, 147, 262, 60);
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
		panelControl.setBounds(10, 11, 262, 125);
		frmXraydetector.getContentPane().add(panelControl);
		panelControl.setLayout(new GridLayout(0, 1, 0, 4));
		return panelControl;
	}

	public void getJFramePrincipal() {
		frmXraydetector = new JFrame();
		frmXraydetector.setTitle("XRayDetector");
		frmXraydetector.setBounds(100, 100, 1048, 800);
		frmXraydetector.setResizable(false);
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
	
	private class SliderListener implements ChangeListener{

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
	
	private class AnalizarImagenListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
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
	
	private class ThreadAnalizar implements Runnable{

		@Override
		public void run() {
			
			try {
				kit.insertHTML(doc, doc.getLength(), "<p class=\"normal\"> Iniciando proceso de an�lisis</p><br>", 0, 0, null);
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
					fachada.ejecutaVentanaOpcionRegiones(selection, imgPanel, progressBar);	//segunda opci�n
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
						kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Proceso de an�lisis finalizado correctamente</p><br>", 0, 0, null);
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
	
	private class StopListener implements ActionListener{
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
	        				// que ah� van a estar las imagenes para entrenar
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
				kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Log exportado con �xito</p><br>", 0, 0, null);
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
	
	private class GuardarImagenAnalizadaListener implements ActionListener{
		
		public void actionPerformed (ActionEvent e){
	    	
	    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la ubicaci�n para guardar la imagen analizada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Im�genes JPG", "jpg");
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

	private class GuardarImagenBinarizadaListener implements ActionListener{
		
		public void actionPerformed (ActionEvent e){
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la ubicaci�n para guardar la imagen binarizada");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Im�genes JPG", "jpg");
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
	
	private class PrecisionRecallListener implements ActionListener{
		
		public void actionPerformed (ActionEvent e){
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja la m�scara correspondiente a la imagen");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Im�genes JPG", "jpg");
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
			else{	//entrenar con im�genes
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
									kit.insertHTML(doc, doc.getLength(), "<p class=\"exito\"> Proceso de entrenamiento finalizado con �xito </p><br>", 0, 0, null);
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

		private void mostrarErrorNombresFicheros() {
			JOptionPane
					.showMessageDialog(
							null,
							"Los nombres de las im�genes originales no coinciden con los de las m�scaras.",
							"Error", 1);
			try {
				kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
						"Los nombres de las im�genes originales no coinciden con los de las m�scaras</p><br>", 0, 0, null);
				txtLog.setCaretPosition(txtLog.getDocument().getLength());
			} catch (BadLocationException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			} catch (IOException e1) {
				MyLogHandler.writeException(e1);
				e1.printStackTrace();
			}
		}

		private void mostrarErrorNumImagenes(String[] originalList,
				String[] maskList) {
			
			if (originalList.length > maskList.length){
				JOptionPane.showMessageDialog(null,
						"El n�mero de im�genes originales y m�scaras no coinciden.\n"
								+ "Faltan m�scaras", "Error", 1);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
							"Faltan m�scaras</p><br>", 0, 0, null);
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
						"El n�mero de im�genes originales y m�scaras no coinciden.\n"
								+ "Faltan originales", "Error", 1);
				
				try {
					kit.insertHTML(doc, doc.getLength(), "<p class=\"error\"> Proceso fallido: " +
							"Faltan im�genes originales</p><br>", 0, 0, null);
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
	
	private class TableMouseListener extends MouseAdapter{ 
		public void mouseClicked(MouseEvent e){
			
			//restaurar imagen original
			imgPanel.setImage(copiaEdges.getImage());
			imgPanel.repaint();
			
			//pintar la selecci�n
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
