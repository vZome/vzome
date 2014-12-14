
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public class Symmetry extends Nested
{
	private Permute permute;

	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitSymmetry( this, permute );
	}

	public void setPermute( Permute permute ) {
		this .permute = permute;
	}

	public Permute getPermute(){
		return this.permute;
	}
}


