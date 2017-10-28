
package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public class Repeat extends Nested{

	private  int repetitions;

	public  Repeat( int repetitions ) {
		super();

			this .repetitions = repetitions;
		}

    @Override
	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitRepeat( this, repetitions );
	}


}


