package com.vzome.core.zomic;



/** * An exception thrown by the Zomic parser or interpreter. * A ZomeException may wrap another exception. * Handlers are obligated to "unwrap" a ZomeException * before reporting it, by calling getCulprit(). */
@SuppressWarnings("serial")
public class ZomicException extends Exception{

	private  Exception m_culprit;

	public  ZomicException( Exception culprit ) {
		super( "wrapped" );

			m_culprit = culprit;
		}

	public  ZomicException( String msg ) {
		super( msg );
	}

	/**	 * Return the original culprit wrapped by this ZomeException.	 * Arbitrarily deep wrapping will be unwrapped by a single call	 * to getCulprit.  If there is no culprit, returns this ZomeException.	 */
	public  Exception getCulprit() {
		if ( m_culprit == null ) {
			return this;
		}
		if ( m_culprit instanceof ZomicException ) {
			return ((ZomicException) m_culprit) .getCulprit();
		}
		else {
			return m_culprit;
		}
	}


}


