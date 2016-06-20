/**
 * public domain do whatever you want with this
 */
package utils;

import java.io.*;
import java.util.Properties;


/**
 * Class in charge of loading Strings from a localization file
 * @author Gal
 * @since 2016
 */
public class LocaleManager
{	
	public static final LocaleManager EN_US = new LocaleManager("en_US");	
	
	private static final String DEFAULTS = "assets/defaults.txt";
	private static LocaleManager locale;
	
	private final String name;
	private Properties file;
	
	/**
	 * Creates a localization manager for a certain localization
	 * @param name The locale name
	 */
	private LocaleManager(String name)
	{
		this.name = name;
		
		file = new Properties();
		
		// load default
		try (FileInputStream input = new FileInputStream(DEFAULTS))
		{			
			file.load( input );
		} catch (Exception e) { } // there should be no issues here
		
		// load the localization		
		try (FileInputStream input = new FileInputStream("assets/" + name + ".txt"))
		{
			file.load(input);
		}
		catch (IOException e)
		{
			System.err.println( "Localization not found: " + name );
			e.printStackTrace( );
		}
		catch (SecurityException e)
		{
			System.err.println( "Cannot open file..." );
		}
		
	}
	
	/**
	 * Gets the currently loaded locale.<br>
	 * If no locale is loaded, it loads the en_US one by default.
	 * @return The currently loaded localization manager
	 */
	public static LocaleManager getLocale()
	{
		if (locale == null)
		{
			System.err.println( "No locale loaded!" );
			
			// Fixes a bug with Eclipse,
			// regarding printing to System.err and System.out
			try { Thread.sleep( 10 ); } catch (Exception e) { }
			
			// load en_US by default
			loadLocale(EN_US);
		}
		
		return locale;
	}
	
	/**
	 * Change the locale
	 * @param newLocale The desired locale to change to
	 */
	public static void loadLocale(LocaleManager newLocale)
	{
		System.out.println("Loading locale: " + newLocale.name);
		locale = newLocale;
	}
	
	/**
	 * Get a localized string
	 * @param key The key for the string to localize
	 * @return The localized string
	 */
	public String getKey(String key)
	{
		return file.getProperty( key );
	}
	
}
