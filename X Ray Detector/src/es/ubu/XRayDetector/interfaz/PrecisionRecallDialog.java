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
 * PrecisionRecallDialog.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.interfaz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Dimension;
import java.text.DecimalFormat;

/**
 * Class PrecisionRecallDialog
 * 
 * Dialog to show the precision and recall values.
 * 
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class PrecisionRecallDialog extends JDialog {

	/**
	 * Variable for serialization.
	 */
	private static final long serialVersionUID = -6418063855941950271L;
	
	private String precision;
	private String recall;
	private JTextField tfPrecision;
	private JTextField tfRecall;


	/**
	 * Create the dialog.
	 * 
	 * @param p Precision and Recall Values
	 */
	public PrecisionRecallDialog(double[] p) {
		setMinimumSize(new Dimension(200, 160));
		setResizable(false);
		setPreferredSize(new Dimension(200, 160));
		
		setIconImage(new ImageIcon("./res/img/app/logoXRayDetector.png").getImage());
		
		getPrecisionRecallValues(p);
		
		initialize();
	}

	/**
	 * Gets the precission and recall values.
	 * 
	 * @param p Precision and Recall values
	 */
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
	
	/**
	 * Initializes the precision and recall dialog.
	 */
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
	
	/**
	 * Inner Class AceptarListener.
	 * 
	 * Listener for the accept button.
	 *  
	 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
	 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
	 * @version 2.0
	 */
	private class AceptarListener implements ActionListener{

		/**
		 * Precision and recall dialog instance.
		 */
		private PrecisionRecallDialog dia;
		
		/**
		 * Constructor class.
		 * 
		 * @param precisionRecallDialog Precision and recall dialog instance.
		 */
		public AceptarListener(PrecisionRecallDialog precisionRecallDialog) {
			dia = precisionRecallDialog;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			dia.dispose();		
		}
	}
}
