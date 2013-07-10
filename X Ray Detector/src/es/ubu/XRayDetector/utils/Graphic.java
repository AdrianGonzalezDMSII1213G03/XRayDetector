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

package es.ubu.XRayDetector.utils;

import ij.ImagePlus;
import ij.gui.Overlay;
import ij.gui.Roi;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * This class draws the graphics on a panel and handles mouse listeners for the
 * selection of regions of interest.
 * 
 * 
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Graphic extends JPanel {

	private Rectangle selection = new Rectangle(0, 0, 0, 0);
//	private Rectangle selection2 = new Rectangle(0, 0, 0, 0);
	private boolean isSelectionMode = false, ended;
	private Point initialPoint = new Point(0, 0);
	int w, h;
	private double coorX, coorY, width, height;
	private Image image;
	
	
	/**
	 * Thread-safe ArrayList where the rectangles to be drawed are stored.
	 * Modified by Adrián González Duarte and Joaquín Bravo Panadero
	 */
	private CopyOnWriteArrayList<Rectangle2D.Double> rectangleList;
	private Rectangle2D window;
	private boolean ctrlPresionado = false;
	private JTable tablaResultados;
	private Roi[] arrayRois;
	private Image copia;
	private boolean trabajando = true;

	/**
	 * Create the panel.
	 * 
	 * @param image
	 *            image to be set
	 */
	public Graphic(Image image) {
		this.image = image;
		this.addMouseMotionListener(createMouseMotionListener());
		this.addMouseListener(createMouseListener());
		this.addKeyListener(createKeyListener());

		w = image.getWidth(null);
		h = image.getHeight(null);
		rectangleList = new CopyOnWriteArrayList<Rectangle2D.Double>();
	}

	/**
	 * Sets an image to be drawed.
	 * 
	 * @see #getImage
	 * 
	 * @param image
	 *            Image to be set
	 */
	public void setImage(Image image) {
		this.image = image;
		rectangleList = new CopyOnWriteArrayList<Rectangle2D.Double>();
		w = image.getWidth(null);
		h = image.getHeight(null);
	}
	
	/**
	 * Sets the copy of an image.
	 * 
	 * @param image Copy of an image
	 */
	public void setImageCopy(Image image) {
		copia = image;
		setImage(copia);
	}
	
	/**
	 * Sets a Table Results.
	 * 
	 * @param tabla table to be set
	 */
	public void setTablaResultados(JTable tabla) {
		tablaResultados = tabla;
	}
	
	/**
	 * Method that sets a Roi array which stores a number of ROIs.
	 * @param array Roi array
	 */
	public void setArrayRois(Roi[] array){
		arrayRois = array;
	}
	
	/**
	 * Sets the flag <b>working</b>, which indicates if a selection can be made.
	 * @param t flag
	 */
	public void setFlagTrabajando(boolean t){
		trabajando = t;
	}

	/**
	 * Creates a mouse listener to save the area of selection in the image.
	 * 
	 * @return adapter
	 */
	private MouseMotionListener createMouseMotionListener() {
		MouseMotionListener adapter = new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (isSelectionMode && !trabajando) {
					if (e.getPoint().x > initialPoint.x)
						selection.width = e.getPoint().x - initialPoint.x;
					if (e.getPoint().y > initialPoint.y)
						selection.height = e.getPoint().y - initialPoint.y;
					if (e.getPoint().x < initialPoint.x) {
						selection.width = initialPoint.x - e.getPoint().x;
						selection.x = e.getPoint().x;
					}
					if (e.getPoint().y < initialPoint.y) {
						selection.height = initialPoint.y - e.getPoint().y;
						selection.y = e.getPoint().y;
					}
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		};
		return adapter;
	}

	/**
	 * Creates a new dimension with the size of the open image.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(w, h);
	}

	/**
	 * Creates a new mouse listener to know if is a selection.
	 * 
	 * @return adapter
	 */
	private MouseListener createMouseListener() {
		MouseAdapter adapter = new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				isSelectionMode = false;
			}

			public void mousePressed(MouseEvent e) {
				if(!trabajando && !ctrlPresionado){
					isSelectionMode = true;
					selection.setLocation(e.getPoint());
					selection.setSize(0, 0);
					initialPoint = e.getPoint();
	
					repaint();
				}
			}
			
			public void mouseClicked(MouseEvent e){
				if(ctrlPresionado && !trabajando){
					Point coordenadas = e.getPoint();
					int index = getRoi(coordenadas);
					if(index != -1){
						tablaResultados.setRowSelectionInterval(index, index);
						
						//restaurar imagen original
						setImage(copia);
						repaint();
						
						//pintar la selección
						Roi roi = arrayRois[index];
						int alpha = 63;
						Color c = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), alpha);
						roi.setFillColor(c);
						ImagePlus im = new ImagePlus("overlay", copia);
						im.setOverlay(new Overlay(roi));
						im = im.flatten();
						im.updateAndDraw();
						setImage(im.getImage());
						repaint();
					}
				}
			}
		};
		return adapter;
	}
	
	/**
	 * Returns a ROI that contains the point.
	 * @param coordenadas point
	 * @return Roi that contains the point
	 */
	public int getRoi(Point coordenadas) {
		Roi roi;
		if(arrayRois != null){
			for(int i=0; i<arrayRois.length; i++){
				roi = arrayRois[i];
				if(roi.contains(coordenadas.x, coordenadas.y)){
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Creates a KeyListener that allows to select a defect.
	 * @return KeyListener
	 */
	private KeyListener createKeyListener(){
		KeyAdapter adapter = new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.isControlDown() && !trabajando){
					ctrlPresionado = true;
					e.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
			
			public void keyReleased(KeyEvent e){
				ctrlPresionado = false;
				e.getComponent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		};
		return adapter;
	}

	/**
	 * This method draws the image and repaint the selection square of the mouse
	 * listener.
	 */
	public synchronized void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// Graphics2D g2d3 = (Graphics2D) g;
		Rectangle2D rectangle = null;

		g.drawImage(image, 0, 0, null);

		w = image.getWidth(null);
		h = image.getHeight(null);

		if (selection.getX() < 0) {
			coorX = 0;
			width = selection.getWidth() + selection.getX();
		} else {
			coorX = selection.getX();
			width = selection.getWidth();
		}

		if ((selection.getX() + selection.getWidth()) >= w) {
			coorX = selection.getX();
			width = w - selection.getX() - 1;
		}

		if (selection.getY() < 0) {
			coorY = 0;

			height = selection.getHeight() + selection.getY();
		} else {
			coorY = selection.getY();
			height = selection.getHeight();
		}

		if ((selection.getY() + selection.getHeight()) >= h) {
			coorY = selection.getY();
			height = h - selection.getY() - 1;
		}

		if (ended == false) {

			rectangle = new Rectangle2D.Double(coorX, coorY, width, height);
			g2d.setColor(Color.yellow);

			g2d.draw(rectangle);

			if (window != null) {
				g2d.setColor(Color.yellow);
				g2d.draw(window);
			}

			try {
				for (Rectangle2D.Double rect : rectangleList) {
					g2d.setColor(Color.green);
					g2d.draw(rect);
				}
			} catch (ConcurrentModificationException e) {
				MyLogHandler.writeException(e);
				e.printStackTrace();
			}

		} else {
			coorX = 0;
			coorY = 0;
			width = 0;
			height = 0;
			window = null;
			ended = false;

			selection.width = 0;
			selection.x = 0;
			selection.height = 0;
			selection.y = 0;
		}
	}

	/**
	 * Adds a rectangle that marks a defect to be painted.
	 * 
	 * @param coorX
	 *            Coordinates X of the rectangle
	 * @param coorY
	 *            Coordinate Y of the rectangle
	 * @param width
	 *            Width of the rectangle
	 * @param height
	 *            Height of the rectangle
	 */
	public void addRectangle(int coorX, int coorY, int width, int height) {
		rectangleList.add(new Rectangle2D.Double(coorX, coorY, width, height));
	}

	/**
	 * Draws the sliding window into the graphics.
	 * 
	 * @param coorX
	 *            Coordinates X of the window
	 * @param coorY
	 *            Coordinates Y of the window
	 * @param width
	 *            Width of the window
	 * @param height
	 *            Height of the window
	 * @return window
	 */
	public Rectangle2D drawWindow(int coorX, int coorY, int width, int height) {
		window = new Rectangle2D.Double(coorX, coorY, width, height);

		return window;
	}

	/**
	 * Returns the selection area, with the coordinates and size of the ROI.
	 * 
	 * @return selection area
	 */
	public Rectangle coordenates() {
		//return selection2;
		return selection;
	}

	/**
	 * Returns true if the detection is ended or false if not.
	 * 
	 * @param b
	 *            true if the detection is ended or false if not
	 */
	public void isEnded(boolean b) {
		ended = b;
	}

	/**
	 * This method clear the rectangles in the graphics.
	 */
	public void clear() {
		coorX = 0;
		coorY = 0;
		width = 0;
		height = 0;
		window = null;
		ended = false;

		selection.width = 0;
		selection.x = 0;
		selection.height = 0;
		selection.y = 0;
	}

	/**
	 * This method returns the image draw in the visor.
	 * 
	 * @see #setImage
	 * 
	 * @return image of the visor
	 */
	public Image getImage() {
		return image;
	}
}
