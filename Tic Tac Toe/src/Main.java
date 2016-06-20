/**
 * public domain do whatever you want with this
 */
import game.Game;
import javax.swing.*;
import utils.LocaleManager;

/**
 * Starting point for the game
 * 
 * @author Gal
 * @since 2016
 */
public class Main
{
	/**
	 * Main method
	 * @param args Console arguments
	 */
	public static void main( String[] args )
	{
		SwingUtilities.invokeLater( ( ) -> makeGUI( ) );
	}
	
	/**
	 * Makes the JFrame in which everything is displayed
	 */
	private static final void makeGUI()
	{
		JFrame frame = new JFrame( LocaleManager.getLocale( ).getKey( "title" ) );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setResizable( false );
		
		Game game = new Game( );
		frame.add( game );
		frame.pack( );
		frame.setLocationRelativeTo( null );
		
		frame.setVisible( true );
		
		game.start( );
	}
	
}
