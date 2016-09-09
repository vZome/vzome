

package org.vorthmann.ui;


import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;

/**
* Present a simple graphic to the user upon launch of the application, to 
* provide a faster initial response than is possible with the main window.
* 
* <P>Adapted from an 
* <a href=http://developer.java.sun.com/developer/qow/archive/24/index.html>item</a> 
* on Sun's Java Developer Connection.
*
* <P>This splash screen appears within about 2.5 seconds on a development 
* machine. The main screen takes about 6.0 seconds to load, so use of a splash 
* screen cuts down the initial display delay by about 55 percent. 
*
* @used.By {@link stocksmonitor.ApplicationUI}
* @to.do Can the performance be improved to 1.0 second?
* @author <a href="http://www.javapractices.com/">javapractices.com</a>
*/
public final class SplashScreen extends Frame
{

	/**
	 * @param aImageId must have content, and is used by  
	 * <code>Class.getResource</code> to retrieve the splash screen image.
	 */
	public SplashScreen( String aImageId ) {
		/* Implementation Note
		 * Args.checkForContent is not called here, in an attempt to minimize 
		 * class loading.
		 */
		if ( aImageId == null || aImageId.trim().length() == 0 ){
			throw new IllegalArgumentException("Image Id does not have content.");
		}
		fImageId = aImageId;
	}

	/**
	 * Show the splash screen to the end user.
	 *
	 * <P>Once this method returns, the splash screen is realized, which means 
	 * that almost all work on the splash screen should proceed through the event 
	 * dispatch thread. In particular, any call to <code>dispose</code> for the 
	 * splash screen must be performed in the event dispatch thread.
	 */
	public void splash()
	{
		initImageAndTracker();
		setSize(fImage.getWidth(null), fImage.getHeight(null));
		center();

		fMediaTracker.addImage(fImage, 0);
		try {
			fMediaTracker.waitForID(0);
		}
		catch(InterruptedException ie){
			System.out.println("Cannot track image load.");
		}

		new SplashWindow( SplashScreen.this, fImage );
	}


	// PRIVATE//
	private final String fImageId;
	private MediaTracker fMediaTracker;
	private Image fImage;

	private void initImageAndTracker()
	{
		fMediaTracker = new MediaTracker(this);
		URL imageURL = Thread .currentThread() .getContextClassLoader() .getResource(fImageId);
		fImage = Toolkit.getDefaultToolkit().getImage(imageURL);
	}

	/**
	 * Centers the frame on the screen.
	 *
	 * This centering service is more or less in {@link UiUtil}; this duplication 
	 * is justified only because the use of {@link UiUtil} would entail more 
	 * class loading, which is not desirable for a splash screen.
	 */
	private void center()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle frame = getBounds();
		setLocation((screen.width - frame.width)/2, (screen.height - frame.height)/2);
	}

	private class SplashWindow extends Window
	{
		SplashWindow( Frame aParent, Image aImage )
		{
			super( aParent );
			fImage = aImage;
			setSize(fImage.getWidth(null), fImage.getHeight(null));
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			Rectangle window = getBounds();
			setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);
			setVisible(true);
		}

		@Override
		public void paint( Graphics graphics )
		{
			if ( fImage != null ) {
				graphics .drawImage( fImage, 0, 0, this );
			}
		}

		private Image fImage;
	}
}
 



