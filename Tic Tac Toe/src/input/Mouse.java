/**
 * public domain do whatever you want with this
 */
package input;

import java.awt.MouseInfo;
import java.awt.event.*;


/**
 * @author Gal
 * @since 2016
 */
public class Mouse extends MouseAdapter
{
	/** Tracks the state of the mouse buttons, true means held */
	private boolean[] keys = new boolean[MouseInfo.getNumberOfButtons( )];
	private int x, y;
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed( MouseEvent e )
	{
		keys[e.getButton( )] = true;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased( MouseEvent e )
	{
		keys[e.getButton( )] = false;
	}
	
	/**
	 * Gets the state of the requested mouse button
	 * @param key The mouse button ID
	 * @return true if held, false otherwise
	 */
	public boolean isButtonDown(int key)
	{
		return keys[key];
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved( MouseEvent e )
	{
		x = e.getX( );
		y = e.getY( );
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}
