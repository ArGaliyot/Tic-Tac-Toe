/**
 * public domain do whatever you want with this
 */
package game;

import java.awt.*;

/**
 * A cell upon the screen
 * 
 * @author Gal
 * @since 2016
 */
public class Cell
{
	private static final Color SOLID_COLOR = Color.white;
	private static final Color GHOST_COLOR = Color.DARK_GRAY;
	
	private int			x, y, width, height;
	private boolean		hint;
	private Rectangle	area;
	private CellContent	content;
	
	public Cell( int x, int y, int width, int height )
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		content = CellContent.EMPTY;
		area = new Rectangle( x, y, width, height );
	}
	
	/**
	 * Get the current item in the cell
	 * 
	 * @return The appropriate {@link CellContent} item
	 */
	public CellContent getContent()
	{
		return content;
	}
	
	/**
	 * Sets the content of the cell
	 * 
	 * @param content
	 *            The appropriate {@link CellContent} item
	 */
	public void setContent( CellContent content )
	{
		this.content = content;
	}
	
	/**
	 * Updates the cell on screen
	 * 
	 * @param g
	 *            {@link Graphics2D} object used for drawing
	 */
	public void render( Graphics2D g )
	{
		g.setColor( hint ? GHOST_COLOR : SOLID_COLOR );
		
		int size = 200; // font size
		g.setFont( new Font( "Monospaced", Font.BOLD, size ) );
		
		int xOffset = width / 2 - 2 * size / 5;
		int yOffset = height / 2 + 2 * size / 5;
		
		String drawString;
		
		switch ( content )
		{
			case CROSS:
				drawString = "X";
			break;
			
			case NAUGHT:
				drawString = "O";
			break;
			
			default:
				drawString = "";
		}
		
		g.drawString( drawString, x + xOffset, y + yOffset );
	}
	
	/**
	 * Gets the physical area of the cell
	 * @return A {@link Rectangle} of the cell's area
	 */
	public Rectangle getArea()
	{
		return area;
	}
	
	/**
	 * If set to true, the contents will be rendered in a ghostly fashion 
	 * @param flag boolean
	 */
	public void hintMode(boolean flag)
	{
		hint = flag;
	}
	
	public boolean getHintMode()
	{
		return hint;
	}
}
