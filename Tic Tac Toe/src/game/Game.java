/**
 * public domain do whatever you want with this
 */
package game;

import input.Mouse;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import javax.swing.*;
import utils.LocaleManager;

/**
 * @author Gal
 * @since 2016
 */
public class Game extends JPanel implements Runnable
{
	
	public static int	GAME_WIDTH	= 800;
	public static int	GAME_HEIGHT	= 600;
	
	private Cell[][]	cells		= new Cell[3][3];
	private Thread		thread;
	private JFrame		frame;
	private Mouse		mouse;
	private EngineState	engineState;
	private BoardState	boardState;
	private boolean		running;
	private boolean		xTurn;
	
	/**
	 * Make a new Game instance
	 */
	public Game()
	{
		setPreferredSize( new Dimension( GAME_WIDTH, GAME_HEIGHT ) );
		running = false;
	}
	
	/**
	 * Must be called in order to start the game engine
	 */
	public void start()
	{
		if ( running ) { return; }
		running = true;
		xTurn = true; // x starts!
		engineState = EngineState.MENU;
		boardState = BoardState.ONGOING;
		
		// screen related stuff
		setBackground( Color.BLACK );
		
		// get the frame we're on
		frame = (JFrame) SwingUtilities.getWindowAncestor( this );
		
		// add the mouse
		mouse = new Mouse( );
		addMouseListener( mouse );
		addMouseMotionListener( mouse );
		
		// make the cells array
		for ( int x = 0; x < cells.length; ++x )
		{
			for ( int y = 0; y < cells[0].length; ++y )
			{
				int widthSpacer = frame.getWidth( ) / cells.length;
				int heightSpacer = frame.getHeight( ) / cells[0].length;
				
				int xPos = x * widthSpacer;
				int yPos = y * heightSpacer;
				
				cells[x][y] = new Cell( xPos, yPos, widthSpacer, heightSpacer );
			}
		}
		
		// start the thread
		thread = new Thread( this, "Game-Thread" );
		thread.start( );
	}
	
	@Override
	public void run()
	{
		// common
		int second = 1_000_000_000;
		
		// ups stuff
		long last_ups = System.nanoTime( );
		int ups = 60; // updates per second
		double delta = second / ups; // 1 giga (m. inverse of nano) over 60 (desired updates per sec)
		double counter = 0.0D;
		
		// fps stuff
		long last_fps = System.nanoTime( );
		int fps = 0;
		while ( running )
		{
			long now_ups = System.nanoTime( );
			counter += ( now_ups - last_ups ) / delta;
			last_ups = now_ups;
			
			if ( counter >= 1 )
			{
				update( );
				counter-- ;
			}
			
			render( );
			fps++ ;
			
			long now_fps = System.nanoTime( );
			if ( now_fps - last_fps >= second )
			{
				last_fps = now_fps;
				
				frame.setTitle( LocaleManager.getLocale( ).getKey( "title" )
						+ " | FPS: " + fps );
				fps = 0;
			}
			
		}
	}
	
	/**
	 * Calls the respective update method according to game state
	 */
	private void update()
	{
		BoardState bs = getBoardState( );
		
		switch ( bs )
		{
			case TIE:
			case CROSS_WINS:
			case NAUGHT_WINS: // fall through
				engineState = EngineState.SCORE;
				
			default:
				// do nothing;
		}
		
		switch ( engineState )
		{
			case MENU:
				updateMenu( );
			break;
			
			case GAME:
				updateGame( );
			break;
			
			default:
				// do nothing
		}
	}
	
	/**
	 * Calls the respective render method according to game state
	 */
	private void render()
	{
		if ( !frame.isVisible( ) ) { return; }
		
		BufferStrategy bs = frame.getBufferStrategy( );
		if ( bs == null )
		{
			frame.createBufferStrategy( 3 );
			return;
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics( );
		
		// enable anti-aliasing
		RenderingHints aaHint = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
		g.setRenderingHints( aaHint );
		
		// set background to black
		g.setColor( Color.BLACK );
		g.fillRect( 0, 0, frame.getWidth( ), frame.getHeight( ) );
		
		switch ( engineState )
		{
			case MENU:
				renderMenu( g );
			break;
			
			case GAME:
				renderGame( g );
			break;
			
			case SCORE:
				renderScore( g );
			break;
			
			default:
				// do nothing
		}
		
		// cleanup
		g.dispose( );
		bs.show( );
	}
	
	/**
	 * Menu updater
	 */
	private void updateMenu()
	{
		if (mouse.isButtonDown( MouseEvent.BUTTON1 ))
		{
			try
			{
				Thread.sleep( 100 );
			}
			catch ( InterruptedException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			engineState = EngineState.GAME;
		}
	}
	
	/**
	 * Menu renderer
	 */
	private void renderMenu( Graphics2D g )
	{
		g.setColor( Color.GREEN );
		g.setFont( new Font( "Consolas", Font.PLAIN, 50 ) );
		
		int base = 2 * getHeight() / 5;
		int spacer = 80;
		
		
		centerString( g, LocaleManager.getLocale( ).getKey( "title" ), getWidth( ), base);
		centerString( g, LocaleManager.getLocale( ).getKey( "prompt" ), getWidth( ), base + spacer); 
	}
	
	/**
	 * Takes care of updating the game logic, called 60 times a second, no more
	 */
	private void updateGame()
	{
		Point mouseLocation = new Point( mouse.getX( ), mouse.getY( ) );
		
		//System.out.println(mouseLocation);
		
		for ( Cell[] arr: cells )
		{
			for ( Cell c: arr )
			{
				
				// are we over an empty cell?
				// Not a single statement in order to prevent glitchy graphics
				if ( c.getArea( ).contains( mouseLocation ) )
				{
					if ( c.getHintMode( )
							|| c.getContent( ) == CellContent.EMPTY )
					{
						boolean mouseDown = mouse
								.isButtonDown( MouseEvent.BUTTON1 );
						if ( mouseDown )
						{
							c.hintMode( false );
						}
						else
						{
							c.hintMode( true );
						}
						
						// set the shape
						if ( xTurn )
						{
							c.setContent( CellContent.CROSS );
						}
						else
						{
							c.setContent( CellContent.NAUGHT );
						}
						
						if ( mouseDown )
						{
							xTurn = !xTurn;
						}
					}
				}
				else if ( c.getHintMode( ) )
				{
					// if it's a hint then just re-empty it
					c.setContent( CellContent.EMPTY );
				}
			}
		}
	}
	
	/**
	 * Takes care of rendering stuff onto the screen, no limitations on this
	 */
	private void renderGame( Graphics2D g )
	{
		int stroke = 3; // line width for the grid
		drawGrid( g, Color.DARK_GRAY, stroke );
		
		// do the rendering for the cells
		for ( Cell[] arr: cells )
		{
			for ( Cell c: arr )
			{
				c.render( g );
			}
		}
	}
	
	private void renderScore( Graphics2D g )
	{
		BoardState bs = getBoardState( );
		
		String render = "";
		
		switch ( bs )
		{
			case CROSS_WINS:
				render = LocaleManager.getLocale( ).getKey( "x_wins" );
			break;
			
			case NAUGHT_WINS:
				render = LocaleManager.getLocale( ).getKey( "o_wins" );
			break;
			
			case TIE:
				render = LocaleManager.getLocale( ).getKey( "tied" );
			
			default:
				// you shouldn't be here ever
		}
		
		g.setColor( Color.YELLOW );
		g.setFont( new Font("Consolas", Font.PLAIN, 80) );
		centerString(g, render, getWidth(), getHeight() / 2);
		
	}
	
	/**
	 * Draws the Tic Tac Toe grid on all of the screen
	 * 
	 * @param g
	 *            {@link Graphics2D} object used to draw
	 * @param color
	 *            Lines' color
	 * @param strokeSize
	 *            Line stroke size (line width)
	 */
	private void drawGrid( Graphics2D g, Color color, int strokeSize )
	{
		int w = frame.getWidth( );
		int h = frame.getHeight( );
		
		g.setColor( color );
		g.setStroke( new BasicStroke( strokeSize ) );
		
		// horizontal
		g.drawLine( 0, h / 3, w, h / 3 );
		g.drawLine( 0, 2 * h / 3, w, 2 * h / 3 );
		
		// vertical
		g.drawLine( w / 3, 0, w / 3, h );
		g.drawLine( 2 * w / 3, 0, 2 * w / 3, h );
	}
	
	/**
	 * Returns the current state of the game
	 * 
	 * @return {@link BoardState} portraying the game's state
	 */
	public BoardState getBoardState()
	{
		// if it's game don't check again
		switch ( boardState )
		{
			case CROSS_WINS:
			case NAUGHT_WINS: // fall through
			case TIE: // fall through
				return boardState;
				
			default:
				// keep going
		}
		
		// check horizontal
		for ( int y = 0; y < cells[1].length; ++y )
		{
			// we only have to check the middle pieces
			Cell mid = cells[1][y];
			if ( !mid.getHintMode( ) && mid.getContent( ) != CellContent.EMPTY )
			{
				// now we check the other two, once we know the middle pieces are in fact not empty
				Cell prev = cells[0][y];
				Cell next = cells[2][y];
				
				// checks for three in a row
				// not in ternary to make it less obscure
				if ( !prev.getHintMode( ) && !next.getHintMode( )
						&& prev.getContent( ) == mid.getContent( )
						&& mid.getContent( ) == next.getContent( ) )
				{
					// if this is true, we have three in a row, but we don't know if it's X or O wins yet.
					
					// Return CROSS_WINS if X wins else NAUGHT_WINS
					return ( mid.getContent( ) == CellContent.CROSS ) ? BoardState.CROSS_WINS
							: BoardState.NAUGHT_WINS;
				}
			}
		}
		
		// check vertical
		for ( int x = 0; x < cells.length; ++x )
		{
			// again we only have to check the middle pieces
			Cell mid = cells[x][1];
			if ( !mid.getHintMode( ) && mid.getContent( ) != CellContent.EMPTY )
			{
				// now we check the other two, once we know the middle pieces are in fact not empty
				Cell prev = cells[x][0];
				Cell next = cells[x][2];
				
				// checks for three in a row
				// not in ternary to make it less obscure
				if ( !prev.getHintMode( ) && !next.getHintMode( )
						&& prev.getContent( ) == mid.getContent( )
						&& mid.getContent( ) == next.getContent( ) )
				{
					// if this is true, we have three in a row, but we don't know if it's X or O wins yet.
					// Return CROSS_WINS if X wins else NAUGHT_WINS
					return ( mid.getContent( ) == CellContent.CROSS ) ? BoardState.CROSS_WINS
							: BoardState.NAUGHT_WINS;
				}
			}
		}
		
		// check verticals
		Cell mid = cells[1][1];
		
		// check the middle one first
		if ( !mid.getHintMode( ) && mid.getContent( ) != CellContent.EMPTY )
		{
			// forward vertical (/)
			Cell up = cells[2][0];
			Cell down = cells[0][2];
			
			if ( !up.getHintMode( ) && !down.getHintMode( )
					&& up.getContent( ) == mid.getContent( )
					&& mid.getContent( ) == down.getContent( ) ) { return ( mid
					.getContent( ) == CellContent.CROSS ) ? BoardState.CROSS_WINS
					: BoardState.NAUGHT_WINS; }
			
			// backward vertical (\)
			up = cells[0][0];
			down = cells[2][2];
			
			if ( !up.getHintMode( ) && !down.getHintMode( )
					&& up.getContent( ) == mid.getContent( )
					&& mid.getContent( ) == down.getContent( ) ) { return ( mid
					.getContent( ) == CellContent.CROSS ) ? BoardState.CROSS_WINS
					: BoardState.NAUGHT_WINS; }
		}
		
		// check if the thing's full
		int count = 0;
		for ( Cell[] arr: cells )
		{
			for ( Cell c: arr )
			{
				if ( c.getContent( ) != CellContent.EMPTY && !c.getHintMode( ) )
				{
					count++ ;
				}
			}
		}
		
		return count == 9 ? BoardState.TIE : BoardState.ONGOING;
	}
	
	/**
	 * Vertically centers a string on-screen
	 * 
	 * @param g
	 *            {@link Graphics2D}object used to render
	 * @param str
	 *            The string to draw
	 * @param width
	 *            Context's width
	 * @param y
	 *            height to draw at
	 */
	public static void centerString( Graphics2D g, String str, int width, int y )
	{
		FontMetrics fm = g.getFontMetrics( );
		int strInPixels = (int) fm.getStringBounds( str, g ).getWidth( );
		
		g.drawString( str, ( width - strInPixels ) / 2, y );
	}
}
