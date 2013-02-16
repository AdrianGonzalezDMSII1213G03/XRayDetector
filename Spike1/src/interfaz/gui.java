package interfaz;

import ij.ImagePlus;

import java.awt.EventQueue;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JTextPane;

import utils.Graphic;

import modelo.Mediador;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;

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
		mediador = Mediador.getInstance();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
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
		return textPaneLog;
	}

	private JPanel getPanelLog() {
		JPanel panelLog = new JPanel();
		panelLog.setBorder(new TitledBorder(null, "Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelLog.setBounds(10, 218, 236, 343);
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
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new StopListener());
		panelProgreso.add(btnStop);
	}

	private void getProgressBar(JPanel panelProgreso) {
		panelProgreso_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		progressBar = new JProgressBar();
		progressBar.setMaximum(10000);	//10000 porque no admite float
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
		JButton btnEntrenarClasificador = new JButton("Entrenar clasificador");
		btnEntrenarClasificador.addActionListener(new EntrenarListener(frmXraydetector));
		panelControl.add(btnEntrenarClasificador);
	}

	private void getBtnAbrirImagen(JPanel panelControl) {
		JButton btnAbrirImagen = new JButton("Abrir imagen");
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
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes BMP, JPG, JPEG, PNG", "bmp", "jpg", "jpeg", "png");
	    	chooser.setFileFilter(filter);
			/*chooser.addChoosableFileFilter(new ImageFilter());
			chooser.setFileView(new ImageFileView());
			chooser.setAccessory(new ImagePreview(chooser));*/
	    	int answer = chooser.showOpenDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				image = chooser.getSelectedFile();
			}
			if(image != null){
				//Mediador m = Mediador.getInstance();
				mediador.cargaImagen(image.getAbsolutePath());
				ImagePlus img = new ImagePlus(image.getAbsolutePath());
				imgPanel.setImage(img.getImage());
				imgPanel.repaint();
				selection = imgPanel.coordenates();
				btnAnalizar.setEnabled(true);
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				StyleConstants.setBold(sa, true);	//Negrita
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "Imagen abierta correctamente", sa);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
	        
	    }
	}
	
	private class AnalizarImagenListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	//Mediador m = Mediador.getInstance();    	
	    	//mediador.ejecutaVentana();
	    	
	    	JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	    	chooser.setDialogTitle("Escoja el modelo para clasificar");
	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("Model", "model");
	    	chooser.setFileFilter(filter);
			/*chooser.addChoosableFileFilter(new ImageFilter());
			chooser.setFileView(new ImageFileView());
			chooser.setAccessory(new ImagePreview(chooser));*/
	    	int answer = chooser.showOpenDialog(null);
			if (answer == JFileChooser.APPROVE_OPTION) {
				model = chooser.getSelectedFile();
			}
			if(model != null){
				ThreadAnalizar threadAnalizar = new ThreadAnalizar();
		    	thread = new Thread(threadAnalizar);
		    	thread.start();
			}
	    }
	}
	
	private class ThreadAnalizar implements Runnable{

		@Override
		public void run() {
			mediador.ejecutaVentana(selection, imgPanel, model, progressBar);			
		}		
	}
	
	private class StopListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	if (thread != null){
	    		mediador.stop();
	    		//thread.interrupt();
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
	            		System.out.println("PELELE");
	            	}
	            	else if (button.getText().equals("Usar ARFF existente")){
	            		JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
	        	    	chooser.setDialogTitle("Escoja el ARFF para entrenar");
	        	    	FileNameExtensionFilter filter = new FileNameExtensionFilter("ARFF", "arff");
	        	    	chooser.setFileFilter(filter);
	        	    	int answer = chooser.showOpenDialog(null);
	        			if (answer == JFileChooser.APPROVE_OPTION) {
	        				arff = chooser.getSelectedFile();
	        			}
	        			if(arff != null){
	        				ThreadEntrenar threadEntrenar = new ThreadEntrenar(true);
	        		    	thread = new Thread(threadEntrenar);
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
			if(entrenarConArff){	//si queremos entrenar con un fichero existente
				mediador.ejecutaEntrenamiento(arff);
				SimpleAttributeSet sa = new  SimpleAttributeSet();	//Para definir estilos
				StyleConstants.setBold(sa, true);	//Negrita
				try {
					txtLog.getStyledDocument().insertString(txtLog.getStyledDocument().getLength(), "\nModelo entrenado correctamente" +
							"con el ARFF especificado", sa);
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else{	//entrenar con imágenes
				
			}
		}		
	}
}
