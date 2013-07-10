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
 * AcercaDeDialog.java
 * Copyright (C) 2013 Joaquín Bravo Panadero and Adrián González Duarte. Spain
 */

package es.ubu.XRayDetector.interfaz;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;

/**
 * Class AcercaDeDialog.
 * 
 * Class about the application and programmers information
 * @author <a href="mailto:jbp0023@alu.ubu.es"> Joaquín Bravo Panadero </a>
 * @author <a href="mailto:agd0048@alu.ubu.es"> Adrián Gonzalez Duarte </a>
 * @version 2.0
 */
public class AcercaDeDialog extends JDialog {

	/**
	 * Varieble for serialization.
	 */
	private static final long serialVersionUID = 7661152252092804706L;
	/**
	 * The panel with the "About" information.
	 */
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public AcercaDeDialog() {
		initialize();
	}

	/**
	 * Initializes the dialog.
	 */
	private void initialize() {
		setTitle("Acerca de");
		setIconImage(new ImageIcon("./res/img/app/logoXRayDetector.png").getImage());
		setBounds(100, 100, 385, 267);
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream("./res/img/app/logoXRayDetector.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		contentPanel.setLayout(new BorderLayout(0, 0));
		getLblImage(image);
		
		JPanel panelTexto = getPanelTexto();
		
		getLblXRay(panelTexto);
		
		getLblAutor1(panelTexto);
		
		getLblAutor2(panelTexto);
		
		getBtnAceptar(panelTexto);
	}

	/**
	 * Gets the label image.
	 * 
	 * @param image The label image.
	 */
	private void getLblImage(BufferedImage image) {
		JLabel lblimage = new JLabel(new ImageIcon(image));
		contentPanel.add(lblimage, BorderLayout.WEST);
	}

	private void getBtnAceptar(JPanel panelTexto) {
		JButton btnAceptar = new JButton("Aceptar");
		btnAceptar.setBounds(54, 184, 89, 23);
		btnAceptar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();				
			}
			
		});
		panelTexto.add(btnAceptar);
	}

	/**
	 * Gets the label of the author.
	 * 
	 * @param panelTexto The author.
	 */
	private void getLblAutor1(JPanel panelTexto) {
		JLabel lblJoaqunBravoPanadero = new JLabel("Joaqu\u00EDn Bravo Panadero");
		lblJoaqunBravoPanadero.setBounds(43, 112, 118, 14);
		panelTexto.add(lblJoaqunBravoPanadero);
	}

	/**
	 * Gets the label of the author.
	 * 
	 * @param panelTexto The author.
	 */
	private void getLblAutor2(JPanel panelTexto) {
		JLabel lblAdrinGonzlezDuarte = new JLabel("Adri\u00E1n Gonz\u00E1lez Duarte");
		lblAdrinGonzlezDuarte.setBounds(43, 87, 118, 14);
		panelTexto.add(lblAdrinGonzlezDuarte);
	}

	/**
	 * Gets the label with the name of the application, its version and the year. 
	 * 
	 * @param panelTexto the name of the application and its version
	 */
	private void getLblXRay(JPanel panelTexto) {
		JLabel lblXraydetector = new JLabel("XRayDetector 2.0 - 2013");
		lblXraydetector.setBounds(40, 62, 120, 14);
		panelTexto.add(lblXraydetector);
	}

	/**
	 * Gets the panel "About".
	 * 
	 * @return The panel "about".
	 */
	private JPanel getPanelTexto() {
		JPanel panelTexto = new JPanel();
		panelTexto.setPreferredSize(new Dimension(200, 50));
		contentPanel.add(panelTexto, BorderLayout.EAST);
		panelTexto.setLayout(null);
		return panelTexto;
	}
}
