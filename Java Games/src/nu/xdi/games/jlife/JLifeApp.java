package nu.xdi.games.jlife;

/**
 * A 2D graphical implementation of John Conway's life. (c) 2015, Kevin Phair.
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;

import java.util.Random;
import java.util.Date;

public class JLifeApp extends JComponent implements ActionListener {
	private int width,height;
	private JFrame window;
	private Timer t;

	static final int DISH_HEIGHT = 100;
	static final int DISH_WIDTH = 100;
	static final int CELL_SIZE = 5;

	static int[][] cells;

	public static void main(String[] args) {

		cells = new int[DISH_HEIGHT][DISH_WIDTH];
		initPopulation(cells);
		new JLifeApp().startProgram();

	}

	public void startProgram () {
		window = new JFrame("Test window");
		window.add(this);
		window.setSize(new Dimension(CELL_SIZE + DISH_WIDTH * CELL_SIZE, 28 + DISH_HEIGHT * CELL_SIZE));
		window.setResizable(false);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t = new Timer(40, this);
		t.start();
		window.setVisible(true);
	}	
	
	public static void initPopulation(int[][] a) {

		// Step through the rows of cells
		for (int row = 0; row < a.length; ++row) {
			// Step through the columns in each row of cells
			for (int col = 0; col < a[row].length; ++col) {
				if (new Random().nextInt(100) < 30) {
					a[row][col] = 1;
				} else {
					a[row][col] = 0;
				}
			}
		}
	}
	
	/**
	 * Run one cycle of the life simulation
	 * @param array to be processed
	 */
	public static int[][] runSimulation(int[][] a) {
		
		int[][] tempArray = new int[DISH_HEIGHT][DISH_WIDTH];

		// Step through the rows of cells
		for (int row = 0; row < a.length; ++row) {
			// Step through the columns in each row of cells
			for (int col = 0; col < a[row].length; ++col) {
				int n = countNeighbours(a, row, col);
				/* Rules:
				 * 	A live cell with fewer than two or more than three neighbours dies
				 *	A live cell with two or three neighbours lives
				 *	An empty cell with exactly three neighbours becomes live 
				 */
				if (n < 2 || n > 3) {
					tempArray[row][col] = 0;
				} else if (n == 2) {
					tempArray[row][col] = a[row][col];
				} else if (n == 3) {
					tempArray[row][col] = 1;
				}
			}
		}
		// Check to see if the new array is the same as the old one
		for (int row = 0; row < a.length; ++row) {
			for (int col = 0; col < a[row].length; ++col) {
				if (a[row][col] != tempArray[row][col]) return tempArray;
			}
		}
		return null;
	}

	public static int countNeighbours(int[][] a, int row, int col) {
		int count = 0;
		int height = a.length;
		int width = a[0].length;
		int r, c;
		
		for (int x = -1; x < 2; ++x) {
			for (int y = -1; y < 2; ++y) {
				if (x != 0 || y != 0) {
					r = ((char)(row - y)) % height;
					c = ((char)(col - x)) % width;
					if (a[r][c] > 0) count++;
				}
			}
		}

		return count;
	}
	
	public void actionPerformed (ActionEvent ae) {
		cells = runSimulation(cells);
		repaint();
	}

	protected void paintComponent(Graphics g) {
		paintChildren(g);
	}

	protected void paintChildren(Graphics g) {
		showCells(g);
	}

	private void showCells (Graphics g) {

		// Step through the rows of cells
		for (int row = 0; row < cells.length; ++row) {

			// Step through the columns in each row of cells
			for (int col = 0; col < cells[row].length; ++col) {

				if (cells[row][col] > 0) {
					g.setColor(Color.RED);
					g.fillOval (col * CELL_SIZE, row * CELL_SIZE, 1 + CELL_SIZE * 3 / 2, 1 + CELL_SIZE * 3 / 2);
				}
			}
		}
	}	
}
