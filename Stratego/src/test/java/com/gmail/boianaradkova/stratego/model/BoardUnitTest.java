package com.gmail.boianaradkova.stratego.model;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for the board.
 *
 * @author Boyana Kantarska
 */
public final class BoardUnitTest {
	@Test
	public void testLegalConstruction() {
	}

	@Test
	public void testIllegalConstruction() {
	}

	/**
	 * Test cases for unused pieces during setup stage.
	 */
	@Test
	public void checkForUnusedPieces() {
		Board board = new Board();

		board.state(Board.State.SETUP);

		/* Pieces are not used yet. */
		List<Piece> pieces = board.unused();
		assertEquals("All pieces should not be unused!", 80, pieces.size());

		/* Place all pices on the board. */
		while (pieces.size() > 0) {
			if (board.place(pieces.get(0), (int) (Math.random() * 10), (int) (Math.random() * 10)) == false) {
				continue;
			}

			pieces.remove(0);
		}

		//TODO Check for half of the pieces to be used.

		/* When all pieces are on the board there should not be unused pieces. */
		pieces = board.unused();
		assertEquals("All pieces should be unused!", 0, pieces.size());

		//TODO Check for half of the pieces to be used.

		/* Remove all pieces from the board. */
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				board.remove(x, y);
			}
		}

		/* Pieces are not used anymore. */
		pieces = board.unused();
		assertEquals("All pieces should not be unused!", 80, pieces.size());
	}

	/**
	 * Test cases for empty cells on the board.
	 */
	@Test
	public void checkForEmptyCell() {
		Board board = new Board();

		/* Test outside of the board. */
		try {
			board.isEmpty(-1, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.isEmpty(0, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.isEmpty(-1, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.isEmpty(Integer.MAX_VALUE, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.isEmpty(0, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.isEmpty(Integer.MAX_VALUE, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}

		/* Select random piece of pieces, which are not on the board. */
		List<Piece> pieces = board.unused();
		Collections.shuffle(pieces);

		/* Test when the cell is not empty. */
		board.place(pieces.get(0), 0, 0);
		assertEquals("Cell should not be empty!", false, board.isEmpty(0, 0));

		/* Test when the cell is empty. */
		board.remove(0, 0);
		assertEquals("Cell should be empty!", true, board.isEmpty(0, 0));
	}

	/**
	 * Test of the pieces placement during setup stage.
	 */
	@Test
	public void piecesPlacement() {
		Board board = new Board();
		List<Piece> list = board.unused();
		Collections.shuffle(list);
		Piece piece = list.get(0);

		/* Test for invalid piece placement. */
		try {
			board.place(null, 0, 0);
			fail("Invalid piece exception was not thrown!");
		} catch (RuntimeException e) {
		}

		/* Test outside of the board. */
		try {
			board.place(piece, -1, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.place(piece, 0, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.place(piece, -1, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.place(piece, Integer.MAX_VALUE, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.place(piece, 0, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.place(piece, Integer.MAX_VALUE, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}

		int x = -1;
		int y = -1;
		Cell[][] cells = board.cells();

		/* Select lake position. */
		do {
			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);
		} while (cells[x][y].territory() != Cell.Territory.LAKE);

		/* Test for invalid lakes placement. */
		assertEquals("Lakes are forbidden for placement!", false, board.place(piece, x, y));

		/* Place some piece somewhere on the board. */
		do {
			Collections.shuffle(list);
			piece = list.get(0);

			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);
		} while (board.place(piece, x, y) == false);

		/* Select another piece. */
		list = board.unused();
		Collections.shuffle(list);
		piece = list.get(0);

		/* Test for invalid placement on occupied cell. */
		assertEquals("Occupied cells are forbidden for placement!", false, board.place(piece, x, y));

		/* Select red territory and blue piece. */
		do {
			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);

			Collections.shuffle(list);
			piece = list.get(0);
		}
		while (board.isEmpty(x, y) == false || cells[x][y].territory() != Cell.Territory.RED || piece.color() != Piece.Color.BLUE);

		/* Test for opponent's territory placement. */
		assertEquals("Opponent's cells are forbidden for placement!", false, board.place(piece, x, y));

		/* Select blue territory and red piece. */
		do {
			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);

			Collections.shuffle(list);
			piece = list.get(0);
		}
		while (board.isEmpty(x, y) == false || cells[x][y].territory() != Cell.Territory.BLUE || piece.color() != Piece.Color.RED);

		/* Test for opponent's territory placement. */
		assertEquals("Opponent's cells are forbidden for placement!", false, board.place(piece, x, y));

		/* Select piece and territory with the same color. */

		/* Select territory and piece from the same color. */
		while (true) {
			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);

			Collections.shuffle(list);
			piece = list.get(0);

			/* Search for empty cell. */
			if (board.isEmpty(x, y) == false) {
				continue;
			}

			/* Search fof cell and territory with same colors. */
			if (cells[x][y].territory() == Cell.Territory.BLUE && piece.color() == Piece.Color.RED) {
				continue;
			}

			/* Search fof cell and territory with same colors. */
			if (cells[x][y].territory() == Cell.Territory.RED && piece.color() == Piece.Color.BLUE) {
				continue;
			}

			/* Brake searching if empty cell with proper color as the piece color is found. */
			break;
		}

		/* Test for opponent's territory placement. */
		assertEquals("Valid placement was not possible!", true, board.place(piece, x, y));
	}

	/**
	 * Test of the pieces removal during play stage.
	 */
	@Test
	public void piecesRemoval() {
		Board board = new Board();

		/* Test outside of the board. */
		try {
			board.remove(-1, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.remove(0, -1);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.remove(-1, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.remove(Integer.MAX_VALUE, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.remove(0, Integer.MAX_VALUE);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}
		try {
			board.remove(Integer.MAX_VALUE, 0);
			fail("Invalid coordinates exception was not thrown!");
		} catch (RuntimeException e) {
		}

		/* Test for empty cell piece removal. */
		assertEquals("Inpossible remove from an empty cell!", false, board.remove(0, 0));

		/* Select territory and piece from the same color. */
		int x = -1;
		int y = -1;
		Piece piece = null;
		Cell[][] cells = board.cells();
		List<Piece> list = board.unused();
		while (true) {
			x = (int) (Math.random() * cells.length);
			y = (int) (Math.random() * cells[x].length);

			Collections.shuffle(list);
			piece = list.get(0);

			/* Search for empty cell. */
			if (board.isEmpty(x, y) == false) {
				continue;
			}

			/* Search fof cell and territory with same colors. */
			if (cells[x][y].territory() == Cell.Territory.BLUE && piece.color() == Piece.Color.RED) {
				continue;
			}

			/* Search fof cell and territory with same colors. */
			if (cells[x][y].territory() == Cell.Territory.RED && piece.color() == Piece.Color.BLUE) {
				continue;
			}

			/* Brake searching if empty cell with proper color as the piece color is found. */
			break;
		}
		board.place(piece, x, y);

		/* Test for occupied by piece cell removal. */
		assertEquals("It should be a valid removal!", true, board.remove(x, y));
	}
}
