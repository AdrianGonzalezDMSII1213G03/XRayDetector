package interfaz;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private double saltoOpciones;
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
			private JSlider slider;
			private JComboBox<String> comboBoxVentana;
			private JComboBox<String> comboBoxTipo;
			private JComboBox<String> comboBoxEntrenamiento;
			
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
				
				getDesktopPane(dialogo, frmOpcionesAvanzadas);	
				
			}

			public void getComboBoxEntrenamiento(
					JInternalFrame frmOpcionesAvanzadas) {
				comboBoxEntrenamiento = new JComboBox<String>();
				comboBoxEntrenamiento.setModel(new DefaultComboBoxModel<String>(new String[] {"Ventana aleatoria", "Ventana deslizante"}));
				comboBoxEntrenamiento.setBounds(149, 135, 205, 20);
				comboBoxEntrenamiento.setSelectedIndex(prop.getTipoEntrenamiento());
				comboBoxEntrenamiento.addActionListener(new ComboBoxTipoEntrenamientoListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxEntrenamiento);
			}

			public void getLblEntrenamiento(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblEntrenamiento = new JLabel("Ventana entrenamiento");
				lblEntrenamiento.setBounds(22, 141, 119, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblEntrenamiento);
			}

			public void getLabelSalto(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDelSalto = new JLabel("Tama\u00F1o del salto (%)");
				lblTamaoDelSalto.setBounds(22, 46, 119, 20);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDelSalto);
			}

			public void getLabelVentana(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTamaoDeVentana = new JLabel("Tama\u00F1o de ventana");
				lblTamaoDeVentana.setBounds(22, 11, 119, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTamaoDeVentana);
			}

			public JInternalFrame getInternalFrame() {
				JInternalFrame frmOpcionesAvanzadas = new JInternalFrame ();
				frmOpcionesAvanzadas.setTitle("Opciones avanzadas");
				frmOpcionesAvanzadas.setBounds(100, 100, 380, 240);
				frmOpcionesAvanzadas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frmOpcionesAvanzadas.getContentPane().setLayout(null);
				return frmOpcionesAvanzadas;
			}

			public void getDesktopPane(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JDesktopPane desktoppane = new JDesktopPane();
				desktoppane.add(frmOpcionesAvanzadas);
				dialogo.getContentPane().add(frmOpcionesAvanzadas.getContentPane());
				dialogo.setMinimumSize(new Dimension(380, 240));
				dialogo.setResizable(false);
				dialogo.pack();
				dialogo.setLocationRelativeTo(frame);
				dialogo.setVisible(true);
			}

			public void getBtnCancelar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnCancelar = new JButton("Cancelar");
				btnCancelar.setBounds(193, 178, 89, 23);
				btnCancelar.addActionListener(new CancelarListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnCancelar);
			}

			public void getBtnAceptar(JDialog dialogo,
					JInternalFrame frmOpcionesAvanzadas) {
				JButton btnAceptar = new JButton("Aceptar");
				btnAceptar.setBounds(94, 178, 89, 23);
				btnAceptar.addActionListener(new AceptarOpcionesListener(dialogo));
				frmOpcionesAvanzadas.getContentPane().add(btnAceptar);
			}

			public void getSliderSalto(JInternalFrame frmOpcionesAvanzadas) {
				slider = new JSlider();
				slider.setMajorTickSpacing(10);
				slider.setMinorTickSpacing(1);
				slider.setMinimum(10);
				slider.setPaintLabels(true);
				slider.setPaintTicks(true);
				slider.setBounds(141, 36, 213, 44);
				slider.addChangeListener(new SliderSaltoListener());				
				saltoOpciones = prop.getSalto();
				slider.setValue((int)(saltoOpciones*100));
				frmOpcionesAvanzadas.getContentPane().add(slider);
			}

			public void getComboBoxVentana(JInternalFrame frmOpcionesAvanzadas) {
				comboBoxVentana = new JComboBox<String>();
				comboBoxVentana.setModel(new DefaultComboBoxModel<String>(new String[] {"8", "12", "16", "24", "32", "48"}));
				comboBoxVentana.setSelectedItem(String.valueOf(prop.getTamVentana()));
				comboBoxVentana.setBounds(149, 8, 52, 20);
				comboBoxVentana.addActionListener(new ComboBoxTamVentanaListener());
				frmOpcionesAvanzadas.getContentPane().add(comboBoxVentana);
				tamVentanaOpciones = Integer.parseInt(comboBoxVentana.getSelectedItem().toString());
			}
			
			public void getLabelTipo(JInternalFrame frmOpcionesAvanzadas) {
				JLabel lblTipoDeDeteccion = new JLabel("Tipo de detecci\u00F3n");
				lblTipoDeDeteccion.setBounds(22, 99, 104, 14);
				frmOpcionesAvanzadas.getContentPane().add(lblTipoDeDeteccion);
			}
			
			public void getComboBoxTipo(JInternalFrame frmOpcionesAvanzadas){
				comboBoxTipo = new JComboBox<String>();
				comboBoxTipo.setModel(new DefaultComboBoxModel<String>(new String[] {"Normal", "Normal + umbrales locales", "Blancos en umbrales locales"}));
				comboBoxTipo.setSelectedIndex(prop.getTipoDeteccion());
				comboBoxTipo.addActionListener(new ComboBoxTipoDeteccionListener());
				comboBoxTipo.setBounds(149, 96, 205, 20);
				frmOpcionesAvanzadas.getContentPane().add(comboBoxTipo);
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
