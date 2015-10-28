/**
 * Created 16 Jul 2015
 * @author Kevin Phair
 * 
 */
package nu.xdi.games.life;

import java.util.Random;
import java.util.Scanner;

/**
 * An implementation of John Conway's "Life" algorithm using a
 * 2-dimensional array of integers to represent the cells
 * 
 * Rules:
 * 	A live cell with fewer than two or more than three neighbours dies
 *	A live cell with two or three neighbours lives
 *	An empty cell with exactly three neighbours becomes live 
 * 
 * @author Kevin Phair
 * @version 1.0
 */
public class LifeApp {

	static final int DISH_HEIGHT = 16;
	static final int DISH_WIDTH = 16;

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		int[][] cells = new int[DISH_HEIGHT][DISH_WIDTH];
		
		//System.out.println((char)-1 % 8);
		
		initPopulation(cells);
		showPopulation(cells);
		while (true) {
			System.out.println("\nPress ENTER to show the next generation");
			scan.nextLine();
			cells = runSimulation(cells);
			if (cells == null) {
				System.out.println("Population is now static. Simulation over.");
				break;
			}
			System.out.println("\n\n\n\n\n\n\n\n");
			showPopulation(cells);
		}
		
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

	/**
	 * Show the population described in array
	 * @param array to show
	 */
	public static void showPopulation(int[][] a) {
		
		// Step through the rows of cells
		for (int[] i1 : a) {
			// Step through the columns in each row of cells
			for (int c : i1) {
				if (c > 0) {
					System.out.print("##");
				} else {
					System.out.print("  ");
				}
			}
			System.out.println();
		}
	}
}
