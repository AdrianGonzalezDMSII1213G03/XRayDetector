package interfaz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;
import java.text.DecimalFormat;

public class PrecisionRecallDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6418063855941950271L;
	
	private String precision;
	private String recall;
	private JTextField tfPrecision;
	private JTextField tfRecall;


	/**
	 * Create the dialog.
	 */
	public PrecisionRecallDialog(double[] p) {
		setMinimumSize(new Dimension(200, 160));
		setResizable(false);
		setPreferredSize(new Dimension(200, 160));
		
		getPrecisionRecallValues(p);
		
		initialize();
	}

	public void getPrecisionRecallValues(double[] p) {
		DecimalFormat df = new DecimalFormat("#.######");
		
		if(Double.isNaN(p[0])){
			precision = "NaN";
		}
		else{
			precision = df.format(p[0]);
		}
		
		if(Double.isNaN(p[1])){
			recall = "NaN";
		}
		else{
			recall = df.format(p[1]);
		}
	}
	
	public void initialize(){
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setTitle("Precision & Recall");
		getContentPane().setLayout(null);
		
		JLabel lblPrecision = new JLabel("Precision:");
		lblPrecision.setBounds(10, 24, 57, 14);
		getContentPane().add(lblPrecision);
		
		
		
		tfPrecision = new JTextField();
		tfPrecision.setBounds(77, 20, 100, 22);
		tfPrecision.setEditable(false);
		getContentPane().add(tfPrecision);
		tfPrecision.setText(precision);
		
		JLabel lblRecall = new JLabel("Recall:");
		lblRecall.setBounds(10, 67, 46, 14);
		getContentPane().add(lblRecall);
		
		tfRecall = new JTextField();
		tfRecall.setEditable(false);
		tfRecall.setBounds(77, 63, 100, 22);
		getContentPane().add(tfRecall);
		tfRecall.setText(recall);
		
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBounds(55, 100, 89, 23);
		btnAceptar.addActionListener(new AceptarListener(this));
		getContentPane().add(btnAceptar);
		
	}
	
	private class AceptarListener implements ActionListener{

		private PrecisionRecallDialog dia;
		
		public AceptarListener(PrecisionRecallDialog precisionRecallDialog) {
			dia = precisionRecallDialog;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			dia.dispose();		
		}
	}
}
