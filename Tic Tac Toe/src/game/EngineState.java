/**
 * public domain do whatever you want with this
 */
package game;


/**
 * The different rendering state
 * @author Gal
 * @since 2016
 */
public enum EngineState
{
	/** Render a greeting menu */
	MENU,
	
	/** Render the game itself */
	GAME,
	
	/** Game's over, render winning screen */
	SCORE,
}
