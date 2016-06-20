/**
 * public domain do whatever you want with this
 */
package game;


/**
 * Portrays the current board state
 * @author Gal
 * @since 2016
 */
public enum BoardState
{
	/** No player wins, and there's still places left to play at */
	ONGOING,
	
	/** No player wins, but there's no places to play at */
	TIE,
	
	/** Cross managed to get three in a row */
	CROSS_WINS,
	
	/** Naught managed to get three in a row */
	NAUGHT_WINS,
}
