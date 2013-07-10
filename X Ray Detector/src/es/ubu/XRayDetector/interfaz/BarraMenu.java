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
 * BarraMenu.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.interfaz;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.ubu.XRayDetector.utils.MyLogHandler;
import es.ubu.XRayDetector.utils.Propiedades;


/**
 * Class BarraMenu.
 * 
 * The Menu Bar Object.
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class BarraMenu extends JMenuBar {

	/**
	 * Variable for serialization.
	 */
	private static final long serialVersionUID = 1064988928226623879L;
	/**
	 * The main application panel.
	 */
	private PanelAplicacion pan;
	/**
	 * Options menu.
	 */
	private JMenu mnOpciones;
	/**
	 * Advanced menu options.
	 */
	private JMenuItem mntmOpcionesAvanzadas;
	/**
	 * Help menu.
	 */
	private JMenu mnAyuda;
	/**
	 * Online help item from the help menu.
	 */
	private JMenuItem mntmAyudaEnLnea;
	/**
	 * About item from the help menu.
	 */
	private JMenuItem mntmAcercaDe;
	/**
	 * Size of the window.
	 */
	private int tamVentanaOpciones;
	/**
	 * Detection type.
	 */
	private int tipoDeteccion;
	/**
	 * Learning type.
	 */
	private int tipoEntrenamiento;
	/**
	 * Classification type.
	 */
	private int tipoClasificacion;
	/**
	 * Heuristic type.
	 */
	private int heuristicaVentanaDefectuosa;
	/**
	 * Features type.
	 */
	private int caracteristicas;
	/**
	 * The proportional jumping between windows.
	 */
	private double saltoOpciones;
	/**
	 * Percentage of defected pixels.
	 */
	private double porcentajePixelesMalos;
	/**
	 * The application propieties.
	 */
	private static Propiedades prop;
	
	/**
	 * Constructor class.
	 *  
	 * @param pan The application main panel.
	 */
	public BarraMenu(PanelAplicacion pan){
		this.pan = pan;
		prop = Propiedades.getInstance();
		initialize();
	}

	/**
	 * Initializes the menu bar.
	 */
	private void initialize() {
		getMenu();
		
		try {
			File fichero = new File("./res/ayuda/ayuda.hs");
			URL hsURL = fichero.toURI().toURL();
			HelpSet hs =  new HelpSet(null, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			hb.enableHelpOnButton(mntmAyudaEnLnea, "ventanaentrenamientodeteccion", hs);
		}
		catch (MalformedURLException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		} 
		catch (HelpSetException e) {
			MyLogHandler.writeException(e);
			e.printStackTrace();
		}

		mntmAcercaDe = new JMenuItem("Acerca de");
		mntmAcercaDe.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AcercaDeDialog d = new AcercaDeDialog();
				d.setLocationRelativeTo(pan.getFrameXRayDetector());
				d.setVisible(true);
			}			
		});
		mnAyuda.add(mntmAcercaDe);
		
	}

	/**
	 * Gets the menu bar.
	 */
	public void getMenu() {
		JMenu mnArchivo = new JMenu("Archivo");
		this.add(mnArchivo);
		
		JMenuItem mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener(new SalirListener());
		mnArchivo.add(mntmSalir);
		mnOpciones = new JMenu("Opciones");
		this.add(mnOpciones);

		mntmOpcionesAvanzadas = new JMenuItem("Opciones avanzadas");
		mntmOpcionesAvanzadas.addActionListener(new OpcionesAvanzadasListener(pan.getFrameXRayDetector()));
		mnOpciones.add(mntmOpcionesAvanzadas);


		mnAyuda = new JMenu("Ayuda");
		this.add(mnAyuda);

		mntmAyudaEnLnea = new JMenuItem("Ayuda en línea");
		mnAyuda.add(mntmAyudaEnLnea);
	}
	
	
	
	
	/**
	 * Inner Class SalirListener.
	 * 
	 * Listener for exiting from the application
	 *  
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class SalirListener implements ActionListener{
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
	    public void actionPerformed (ActionEvent e){
	    	pan.getFrameXRayDetector().dispose();
	    }
	}
	
	/**
	 * Inner Class OpcionesAvanzadasListener.
	 * 
	 * Listener for open the advanced options of the application.
	 *  
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class OpcionesAvanzadasListener implements ActionListener{
		    
			/**
			 * Main application frame.
			 */
			private Frame frame;
			/**
			 * Slider to establish the window jumping. 
			 */
			private JSlider sliderSalto;
			/**
			 * Combobox to establish the window size.
			 */
			private JComboBox<String> comboBoxVentana;
			/**
			 * Combobox to establish the window type.
			 */
			private JComboBox<String> comboBoxTipo;
			/**
			 * Combobox to establish the window type.
			 */
			private JComboBox<String> comboBoxEntrenamiento;
			/**
			 * Combobox to establish the classification type.
			 */
			private JComboBox<String> comboBoxClasificacion;
			/**
			 * Combobox to establish the defected window type.
			 */
			private JComboBox <String> comboBoxTipoVentanaDefectuosa;
			/**
			 * Combobox to establish the selected features.
			 */
			private JComboBox<String> comboBoxCaracteristicas;
			/**
			 * Slider to establish the percentage of the defected pixels.
			 */
			private JSlider sliderPxDefectuosos;
			
			/**
			 * Constructor class.
			 * 
			 * @param fr Main application frame.
			 */
			public OpcionesAvanzadasListener(Frame fr) {
				frame = fr;
			}

			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JDialog dialogo = new JDialog(frame, "Opciones avanzadas", true);				
				
				dialogo.setIconImage(new ImageIcon("./res/img/app/logoXRayDetector.png").getImage());
				
				JInternalFrame frmOpcionesAvanzadas = getInternalFrame();				
				
				getLabelVentana(frmOpcionesAvanzadas);
				
				getLabelSalto(frmOpcionesAvanzadas);
				
				getComboBoxVentana(frmOpcionesAvanzadas);
				
				getSliderSalto(frmOpcionesAvanzadas);
				
				getBtnAceptar(dialogo, frmOpcionesAvanzadas);
				
				getBtnCancelar(dialogo, frmOpcionesAvanzadas);
				
				getLabelTipo(frmOpcionesAvanzadas);
				
				getComboBoxTipo(frmOpcionesAvanzadas);					
				
				getLblEntrenamiento(frmOpcionesAvanzadas);
				
				getComboBoxEntrenamiento(frmOpcionesAvanzadas);
				
				getLabelClasificacion(frmOpcionesAvanzadas);
				
				getComboBoxClasificacion(frmOpcionesAvanzadas);
				
				getLabelHeuristica(frmOpcionesAvanzadas);
				
				getComboBoxHeuristica(frmOpcionesAvanzadas);
				
				getLabelPorcentajePixelesMal(frmOpcionesAvanzadas);
				
				getSliderPorcentajePixelesMal(frmOpcionesAvanzadas);
				
				getLabelCaracteristicas(frmOpcionesAvanzadas);
				
				getComboBoxCaracteristicas(frmOpcionesAvanzadas);
				
				getDesktopPane(dialogo, frmOpcionesAvanzadas);
				
			}

			/**
			 * Gets the advanced options combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getComboBoxCaracteristicas(
					JInternalFrame frmOpcionesAvanzadas) {
				comboBoxCaracteristicas = new JComboBox<String>();
				comboBoxCaracteristicas.setModel(new DefaultComboBoxModel<String>(new String[] {"Todas", "Mejores"}));
				comboBoxCaracteristicas.setBounds(179, 267, 205, 20);
				comboBoxCaracteristicas.setSelectedIndex(prop.getTipoCaracteristicas());
				comboBoxCaracteristicas.addActionListener(new ComboBoxCaracteristicasListener());
				comboBoxCaracteristicas.setToolTipText("Selecciona si se van a calcular todas las características o sólo las mejores");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxCaracteristicas);
			}

			/**
			 * Gets the features label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelCaracteristicas(
					JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblCaractersticas = new JLabel("Caracter\u00EDsticas");
				lblCaractersticas.setBounds(22, 270, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblCaractersticas);
			}

			/**
			 * Gets the percentage defected pixels slider.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getSliderPorcentajePixelesMal(JInternalFrame frmOpcionesAvanzadas) {
				sliderPxDefectuosos = new JSlider();
				sliderPxDefectuosos.setPaintTicks(true);
				sliderPxDefectuosos.setPaintLabels(true);
				sliderPxDefectuosos.setMinorTickSpacing(1);
				sliderPxDefectuosos.setMinimum(10);
				sliderPxDefectuosos.setMajorTickSpacing(10);
				sliderPxDefectuosos.setBounds(169, 212, 213, 44);
				sliderPxDefectuosos.addChangeListener(new SliderPorcentajePixelesListener());				
				porcentajePixelesMalos = prop.getPorcentajePixeles();
				sliderPxDefectuosos.setValue((int)(porcentajePixelesMalos*100));
				sliderPxDefectuosos.setToolTipText("Establece el porcentaje de píxeles que van a ser considerados defectuosos");
				frmOpcionesAvanzadas.getContentPane().add(sliderPxDefectuosos);
			}

			/**
			 * Gets the percentaje defected pixels label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelPorcentajePixelesMal(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblPorcentajePixelesDefectuosos = new JLabel("Porcentaje pxs defectuosos");
				lblPorcentajePixelesDefectuosos.setBounds(22, 220, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblPorcentajePixelesDefectuosos);
			}

			/**
			 * Gets the heuristic combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getComboBoxHeuristica(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxTipoVentanaDefectuosa = new JComboBox <String> ();
				comboBoxTipoVentanaDefectuosa.setModel(new DefaultComboBoxModel <String> (new String[] {"Porcentaje pixeles malos en ventana", "Porcentaje vecinos malos pixel central"}));
				comboBoxTipoVentanaDefectuosa.setBounds(179, 184, 205, 20);
				comboBoxTipoVentanaDefectuosa.setSelectedIndex(prop.getTipoVentanaDefectuosa());
				comboBoxTipoVentanaDefectuosa.addActionListener(new ComboBoxHeuristicaVentanaListener());
				comboBoxTipoVentanaDefectuosa.setToolTipText("Establece el tipo de estrategia de etiquetado de los defectos");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxTipoVentanaDefectuosa);
			}

			/**
			 * Gets the heuristic label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelHeuristica(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblHeursticaVentanaDefectuosa = new JLabel("Heur\u00EDstica ventana defectuosa");
				lblHeursticaVentanaDefectuosa.setBounds(22, 187, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblHeursticaVentanaDefectuosa);
			}

			/**
			 * Gets the classification type combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getComboBoxClasificacion(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxClasificacion = new JComboBox<String>();
				comboBoxClasificacion.setModel(new DefaultComboBoxModel<String>(new String[] {"Clases Nominales", "Regresi\u00F3n"}));
				comboBoxClasificacion.setBounds(179, 153, 205, 20);
				comboBoxClasificacion.setSelectedIndex(prop.getTipoClasificacion());
				comboBoxClasificacion.addActionListener(new ComboBoxTipoClasificacionListener());
				comboBoxClasificacion.setToolTipText("Establece el tipo de clasificación");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxClasificacion);
			}

			/**
			 * Gets the classification type label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelClasificacion(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTipoDeClasificacion = new JLabel("Tipo de clasificaci\u00F3n");
				lblTipoDeClasificacion.setBounds(22, 156, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTipoDeClasificacion);
			}

			/**
			 * Gets the learning type combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getComboBoxEntrenamiento(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxEntrenamiento = new JComboBox<String>();
				comboBoxEntrenamiento.setModel(new DefaultComboBoxModel<String>(new String[] {"Ventana aleatoria", "Ventana deslizante"}));
				comboBoxEntrenamiento.setBounds(179, 122, 205, 20);
				comboBoxEntrenamiento.setSelectedIndex(prop.getTipoEntrenamiento());
				comboBoxEntrenamiento.addActionListener(new ComboBoxTipoEntrenamientoListener());
				comboBoxEntrenamiento.setToolTipText("Establece el tipo de ventana que se usará en el entrenamiento");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxEntrenamiento);
			}

			/**
			 * Gets the learning type label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLblEntrenamiento(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblEntrenamiento = new JLabel("Ventana entrenamiento");
				lblEntrenamiento.setBounds(22, 125, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblEntrenamiento);
			}

			/**
			 * Gets the percentage jumping label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelSalto(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDelSalto = new JLabel("Tama\u00F1o del salto (%)");
				lblTamaoDelSalto.setBounds(22, 45, 147, 20);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDelSalto);
			}

			/**
			 * Gets the window size label.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options. 
			 */
			public void getLabelVentana(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDeVentana = new JLabel("Tama\u00F1o de ventana");
				lblTamaoDeVentana.setBounds(22, 11, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDeVentana);
			}

			/**
			 * Gets the advanced options internal frame. 
			 * 
			 * @return the internal frame.
			 */
			public JInternalFrame getInternalFrame() {
				JInternalFrame frmOpcionesAvanzadas = new JInternalFrame ();
				frmOpcionesAvanzadas.setTitle("Opciones avanzadas");
				frmOpcionesAvanzadas.setBounds(100, 100, 400, 380);
				frmOpcionesAvanzadas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frmOpcionesAvanzadas.getContentPane().setLayout(null);
				return frmOpcionesAvanzadas;
			}

			/**
			 * Gets the advanced options internal panel.
			 * 
			 * @param dialogo Advanced options dialog.
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getDesktopPane(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JDesktopPane desktoppane = new JDesktopPane();
				desktoppane.add(frmOpcionesAvanzadas);
				dialogo.getContentPane().add(frmOpcionesAvanzadas.getContentPane());
				dialogo.setMinimumSize(new Dimension(400, 380));
				dialogo.setResizable(false);
				dialogo.pack();
				dialogo.setLocationRelativeTo(frame);
				dialogo.setVisible(true);
			}

			/**
			 * Gets the cancel button.
			 * 
			 * @param dialogo Advanced options dialog.
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getBtnCancelar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnCancelar = new JButton("Cancelar");
				btnCancelar.setBounds(220, 318, 89, 23);
				btnCancelar.addActionListener(new CancelarListener(dialogo));
				btnCancelar.setToolTipText("Cancela los cambios");
				frmOpcionesAvanzadas.getContentPane().add(btnCancelar);
			}

			/**
			 * Gets the accept button.
			 * 
			 * @param dialogo Advanced options dialog.
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getBtnAceptar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnAceptar = new JButton("Aceptar");
				btnAceptar.setBounds(80, 318, 89, 23);
				btnAceptar.addActionListener(new AceptarOpcionesListener(dialogo));
				btnAceptar.setToolTipText("Guarda los cambios");
				frmOpcionesAvanzadas.getContentPane().add(btnAceptar);
			}

			/**
			 * Gets the percentage window jumping slider.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getSliderSalto(JInternalFrame frmOpcionesAvanzadas) {
				sliderSalto = new JSlider();
				sliderSalto.setMajorTickSpacing(10);
				sliderSalto.setMinorTickSpacing(1);
				sliderSalto.setMinimum(10);
				sliderSalto.setPaintLabels(true);
				sliderSalto.setPaintTicks(true);
				sliderSalto.setBounds(171, 36, 213, 44);
				sliderSalto.addChangeListener(new SliderSaltoListener());				
				saltoOpciones = prop.getSalto();
				sliderSalto.setValue((int)(saltoOpciones*100));
				sliderSalto.setToolTipText("Establece el tamaño del salto entre ventanas");
				frmOpcionesAvanzadas.getContentPane().add(sliderSalto);
			}

			/**
			 * Gets the window size combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getComboBoxVentana(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxVentana = new JComboBox<String>();
				comboBoxVentana.setModel(new DefaultComboBoxModel<String>(new String[] {"8", "12", "16", "24", "32", "48"}));
				comboBoxVentana.setSelectedItem(String.valueOf(prop.getTamVentana()));
				comboBoxVentana.setBounds(179, 8, 52, 20);
				comboBoxVentana.addActionListener(new ComboBoxTamVentanaListener());
				comboBoxVentana.setToolTipText("Establece el tamaño de la ventana");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxVentana);
				tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());
			}
			
			/**
			 * Gets the label detection type.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getLabelTipo(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTipoDeDeteccion = new JLabel("Tipo de detecci\u00F3n");
				lblTipoDeDeteccion.setBounds(22, 94, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTipoDeDeteccion);
			}
			
			/**
			 * Gets the detection type combobox.
			 * 
			 * @param frmOpcionesAvanzadas Internal frame advanced options
			 */
			public void getComboBoxTipo(JInternalFrame frmOpcionesAvanzadas){
				comboBoxTipo = new JComboBox<String>();
				comboBoxTipo.setModel(new DefaultComboBoxModel<String>(new String[] {"Normal", "Normal + umbrales locales", "Blancos en umbrales locales"}));
				comboBoxTipo.setSelectedIndex(prop.getTipoDeteccion());
				comboBoxTipo.addActionListener(new ComboBoxTipoDeteccionListener());
				comboBoxTipo.setBounds(179, 91, 205, 20);
				comboBoxTipo.setToolTipText("Establece la estrategia de detección de defectos");
				frmOpcionesAvanzadas.getContentPane().add(comboBoxTipo);
			}
			
			
			/**
			 * Inner Class SliderSaltoListener.
			 * 
			 * Listener for the window jumping slider.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class SliderSaltoListener implements ChangeListener{

				/* (non-Javadoc)
				 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
				 */
				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					if (!sliderSalto.getValueIsAdjusting()) {
						saltoOpciones = (double)sliderSalto.getValue()/100;
					}
					
				}		
			}
			
			/**
			 * Inner Class SliderPorcentagePixelesListener.
			 * 
			 * Listener for the defected pixels percentage slider.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class SliderPorcentajePixelesListener implements ChangeListener{

				/* (non-Javadoc)
				 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
				 */
				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					if (!sliderPxDefectuosos.getValueIsAdjusting()) {
						porcentajePixelesMalos = (double)sliderPxDefectuosos.getValue()/100;
					}
					
				}		
			}
			
			/**
			 * Inner Class ComboBoxTamVentanaListener.
			 * 
			 * Listener for the window size comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxTamVentanaListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());					
				}
			}
			
			/**
			 * Inner Class ComboBoxTipoDeteccionListener.
			 * 
			 * Listener for the detection type comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxTipoDeteccionListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoDeteccion = comboBoxTipo.getSelectedIndex();					
				}
			}
			
			/**
			 * Inner Class ComboBoxTipoEntrenamientoListener.
			 * 
			 * Listener for the learning type comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxTipoEntrenamientoListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoEntrenamiento = comboBoxEntrenamiento.getSelectedIndex();					
				}
			}
			
			/**
			 * Inner Class ComboBoxTipoClasificacionListener.
			 * 
			 * Listener for the classification type comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxTipoClasificacionListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoClasificacion = comboBoxClasificacion.getSelectedIndex();					
				}
			}
			
			/**
			 * Inner Class ComboBoxHeuristicaVentanaListener.
			 * 
			 * Listener for the heuristic window type comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxHeuristicaVentanaListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					heuristicaVentanaDefectuosa = comboBoxTipoVentanaDefectuosa.getSelectedIndex();					
				}
			}
			
			/**
			 * Inner Class ComboBoxCaracteristicasListener.
			 * 
			 * Listener for the selected features type comboBox.
			 *  
			 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
			 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
			 * @version 2.0
			 */
			private class ComboBoxCaracteristicasListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					caracteristicas = comboBoxCaracteristicas.getSelectedIndex();					
				}
			}
	}
	
	/**
	 * Inner Class AceptarOpcionesListener.
	 * 
	 * Listener for the accept button. Sets the new propieties in the application.
	 *  
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class AceptarOpcionesListener implements ActionListener{
		
		/**
		 * Advanced Options dialog.
		 */
		private JDialog dialog;
		
		/**
		 * Constructor Class.
		 * 
		 * @param dialog Advanced Options dialog
		 */
		public AceptarOpcionesListener(JDialog dialog){
			this.dialog = dialog;
		}
		
	    /* (non-Javadoc)
	     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	     */
	    public void actionPerformed (ActionEvent e){
	    	prop.setSalto(saltoOpciones);
	    	prop.setTamVentana(tamVentanaOpciones);
	    	prop.setTipoDeteccion(tipoDeteccion);
	    	prop.setTipoEntrenamiento(tipoEntrenamiento);
	    	prop.setTipoClasificacion(tipoClasificacion);
	    	prop.setTipoVentanaDefectuosa(heuristicaVentanaDefectuosa);
	    	prop.setPorcentajePixeles(porcentajePixelesMalos);
	    	prop.setTipoCaracteristicas(caracteristicas);
	    	dialog.dispose();
	    }
	}
	
	/**
	 * Inner Class CancelarListener.
	 * 
	 * Listener for the cancel button.
	 *  
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class CancelarListener implements ActionListener{
		
		/**
		 * Advanced Options dialog.
		 */
		private JDialog dial;
		
		/**
		 * Constructor Class.
		 * 
		 * @param dialogo Advanced Options dialog
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

}
