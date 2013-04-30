package interfaz;

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

import utils.Propiedades;

public class BarraMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1064988928226623879L;
	private PanelAplicacion pan;
	private JMenu mnOpciones;
	private JMenuItem mntmOpcionesAvanzadas;
	private JMenu mnAyuda;
	private JMenuItem mntmAyudaEnLnea;
	private JMenuItem mntmAcercaDe;
	private int tamVentanaOpciones;
	private int tipoDeteccion;
	private int tipoEntrenamiento;
	private int tipoClasificacion;
	private int heuristicaVentanaDefectuosa;
	private double saltoOpciones;
	private double porcentajePixelesMalos;
	private static Propiedades prop;
	
	public BarraMenu(PanelAplicacion pan){
		this.pan = pan;
		prop = Propiedades.getInstance();
		initialize();
	}

	private void initialize() {
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

		mntmAyudaEnLnea = new JMenuItem("Ayuda en l\u00EDnea");
		mnAyuda.add(mntmAyudaEnLnea);
		
		try {
			File fichero = new File("./res/ayuda/ayuda.hs");
			URL hsURL = fichero.toURI().toURL();
			HelpSet hs =  new HelpSet(null, hsURL);
			HelpBroker hb = hs.createHelpBroker();
			hb.enableHelpOnButton(mntmAyudaEnLnea, "ventanaentrenamientodeteccion", hs);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		catch (HelpSetException e) {
			e.printStackTrace();
		}

		mntmAcercaDe = new JMenuItem("Acerca de");
		mnAyuda.add(mntmAcercaDe);
		
	}
	
	private class SalirListener implements ActionListener{
	    public void actionPerformed (ActionEvent e){
	    	pan.getFrameXRayDetector().dispose();
	    }
	}
	
	private class OpcionesAvanzadasListener implements ActionListener{
		    
			private Frame frame;
			private JSlider sliderSalto;
			private JComboBox<String> comboBoxVentana;
			private JComboBox<String> comboBoxTipo;
			private JComboBox<String> comboBoxEntrenamiento;
			private JComboBox<String> comboBoxClasificacion;
			private JComboBox <String> comboBoxTipoVentanaDefectuosa;
			private JSlider sliderPxDefectuosos;
			
			public OpcionesAvanzadasListener(Frame fr) {
				frame = fr;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				
				JDialog dialogo = new JDialog(frame, "Opciones avanzadas", true);				
				
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
				
				getDesktopPane(dialogo, frmOpcionesAvanzadas);
				
			}

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
				frmOpcionesAvanzadas.getContentPane().add(sliderPxDefectuosos);
			}

			public void getLabelPorcentajePixelesMal(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblPorcentajePixelesDefectuosos = new JLabel("Porcentaje pxs defectuosos");
				lblPorcentajePixelesDefectuosos.setBounds(22, 220, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblPorcentajePixelesDefectuosos);
			}

			public void getComboBoxHeuristica(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxTipoVentanaDefectuosa = new JComboBox <String> ();
				comboBoxTipoVentanaDefectuosa.setModel(new DefaultComboBoxModel <String> (new String[] {"Porcentaje pixeles malos en ventana", "Porcentaje vecinos malos pixel central"}));
				comboBoxTipoVentanaDefectuosa.setBounds(179, 184, 205, 20);
				comboBoxTipoVentanaDefectuosa.setSelectedIndex(prop.getTipoVentanaDefectuosa());
				comboBoxTipoVentanaDefectuosa.addActionListener(new ComboBoxHeuristicaVentanaListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxTipoVentanaDefectuosa);
			}

			public void getLabelHeuristica(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblHeursticaVentanaDefectuosa = new JLabel("Heur\u00EDstica ventana defectuosa");
				lblHeursticaVentanaDefectuosa.setBounds(22, 187, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblHeursticaVentanaDefectuosa);
			}

			public void getComboBoxClasificacion(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxClasificacion = new JComboBox<String>();
				comboBoxClasificacion.setModel(new DefaultComboBoxModel<String>(new String[] {"Clases Nominales", "Regresi\u00F3n"}));
				comboBoxClasificacion.setBounds(179, 153, 205, 20);
				comboBoxClasificacion.setSelectedIndex(prop.getTipoClasificacion());
				comboBoxClasificacion.addActionListener(new ComboBoxTipoClasificacionListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxClasificacion);
			}

			public void getLabelClasificacion(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTipoDeClasificacion = new JLabel("Tipo de clasificaci\u00F3n");
				lblTipoDeClasificacion.setBounds(22, 156, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTipoDeClasificacion);
			}

			public void getComboBoxEntrenamiento(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxEntrenamiento = new JComboBox<String>();
				comboBoxEntrenamiento.setModel(new DefaultComboBoxModel<String>(new String[] {"Ventana aleatoria", "Ventana deslizante"}));
				comboBoxEntrenamiento.setBounds(179, 122, 205, 20);
				comboBoxEntrenamiento.setSelectedIndex(prop.getTipoEntrenamiento());
				comboBoxEntrenamiento.addActionListener(new ComboBoxTipoEntrenamientoListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxEntrenamiento);
			}

			public void getLblEntrenamiento(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblEntrenamiento = new JLabel("Ventana entrenamiento");
				lblEntrenamiento.setBounds(22, 125, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblEntrenamiento);
			}

			public void getLabelSalto(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDelSalto = new JLabel("Tama\u00F1o del salto (%)");
				lblTamaoDelSalto.setBounds(22, 45, 147, 20);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDelSalto);
			}

			public void getLabelVentana(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDeVentana = new JLabel("Tama\u00F1o de ventana");
				lblTamaoDeVentana.setBounds(22, 11, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDeVentana);
			}

			public JInternalFrame getInternalFrame() {
				JInternalFrame frmOpcionesAvanzadas = new JInternalFrame ();
				frmOpcionesAvanzadas.setTitle("Opciones avanzadas");
				frmOpcionesAvanzadas.setBounds(100, 100, 400, 330);
				frmOpcionesAvanzadas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frmOpcionesAvanzadas.getContentPane().setLayout(null);
				return frmOpcionesAvanzadas;
			}

			public void getDesktopPane(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JDesktopPane desktoppane = new JDesktopPane();
				desktoppane.add(frmOpcionesAvanzadas);
				dialogo.getContentPane().add(frmOpcionesAvanzadas.getContentPane());
				dialogo.setMinimumSize(new Dimension(400, 330));
				dialogo.setResizable(false);
				dialogo.pack();
				dialogo.setLocationRelativeTo(frame);
				dialogo.setVisible(true);
			}

			public void getBtnCancelar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnCancelar = new JButton("Cancelar");
				btnCancelar.setBounds(220, 268, 89, 23);
				btnCancelar.addActionListener(new CancelarListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnCancelar);
			}

			public void getBtnAceptar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnAceptar = new JButton("Aceptar");
				btnAceptar.setBounds(80, 268, 89, 23);
				btnAceptar.addActionListener(new AceptarOpcionesListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnAceptar);
			}

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
				frmOpcionesAvanzadas.getContentPane().add(sliderSalto);
			}

			public void getComboBoxVentana(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxVentana = new JComboBox<String>();
				comboBoxVentana.setModel(new DefaultComboBoxModel<String>(new String[] {"8", "12", "16", "24", "32", "48"}));
				comboBoxVentana.setSelectedItem(String.valueOf(prop.getTamVentana()));
				comboBoxVentana.setBounds(179, 8, 52, 20);
				comboBoxVentana.addActionListener(new ComboBoxTamVentanaListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxVentana);
				tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());
			}
			
			public void getLabelTipo(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTipoDeDeteccion = new JLabel("Tipo de detecci\u00F3n");
				lblTipoDeDeteccion.setBounds(22, 94, 147, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTipoDeDeteccion);
			}
			
			public void getComboBoxTipo(JInternalFrame frmOpcionesAvanzadas){
				comboBoxTipo = new JComboBox<String>();
				comboBoxTipo.setModel(new DefaultComboBoxModel<String>(new String[] {"Normal", "Normal + umbrales locales", "Blancos en umbrales locales"}));
				comboBoxTipo.setSelectedIndex(prop.getTipoDeteccion());
				comboBoxTipo.addActionListener(new ComboBoxTipoDeteccionListener());
				comboBoxTipo.setBounds(179, 91, 205, 20);
				frmOpcionesAvanzadas.getContentPane().add(comboBoxTipo);
			}
			
			private class SliderSaltoListener implements ChangeListener{

				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					if (!sliderSalto.getValueIsAdjusting()) {
						saltoOpciones = (double)sliderSalto.getValue()/100;
					}
					
				}		
			}
			
			private class SliderPorcentajePixelesListener implements ChangeListener{

				@Override
				public void stateChanged(ChangeEvent arg0) {
					
					if (!sliderPxDefectuosos.getValueIsAdjusting()) {
						porcentajePixelesMalos = (double)sliderPxDefectuosos.getValue()/100;
					}
					
				}		
			}
			
			private class ComboBoxTamVentanaListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());					
				}
			}
			
			private class ComboBoxTipoDeteccionListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoDeteccion = comboBoxTipo.getSelectedIndex();					
				}
			}
			
			private class ComboBoxTipoEntrenamientoListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoEntrenamiento = comboBoxEntrenamiento.getSelectedIndex();					
				}
			}
			
			private class ComboBoxTipoClasificacionListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					tipoClasificacion = comboBoxClasificacion.getSelectedIndex();					
				}
			}
			
			private class ComboBoxHeuristicaVentanaListener implements ActionListener{

				@Override
				public void actionPerformed(ActionEvent e) {
					heuristicaVentanaDefectuosa = comboBoxTipoVentanaDefectuosa.getSelectedIndex();					
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
	    	prop.setTipoDeteccion(tipoDeteccion);
	    	prop.setTipoEntrenamiento(tipoEntrenamiento);
	    	prop.setTipoClasificacion(tipoClasificacion);
	    	prop.setTipoVentanaDefectuosa(heuristicaVentanaDefectuosa);
	    	prop.setPorcentajePixeles(porcentajePixelesMalos);
	    	dialog.dispose();
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

}
