
package com.vzome.core.zomic.program;

import java.util .*;

import com.vzome.core.zomic.ZomicException;

public class Walk extends ZomicStatement{

	private List stmts = new ArrayList();

	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitWalk( this );
	}

	public  void addStatement( ZomicStatement stmt ) {
		stmts .add( stmt );
	}

	public Iterator getStatements() {
		return stmts .iterator();
	}
	
	public int size(){
		return stmts .size();
	}


}


