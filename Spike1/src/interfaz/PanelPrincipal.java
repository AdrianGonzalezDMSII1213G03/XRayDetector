package interfaz;

import java.awt.EventQueue;
import javax.swing.JMenuBar;

public class PanelPrincipal {
	
	private PanelAplicacion pan;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PanelPrincipal window = new PanelPrincipal();
					window.pan.getFrameXRayDetector().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PanelPrincipal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		pan = new PanelAplicacion();
		JMenuBar menuBar = new BarraMenu(pan);
		pan.getFrameXRayDetector().setJMenuBar(menuBar);		
	}
	
	

}
