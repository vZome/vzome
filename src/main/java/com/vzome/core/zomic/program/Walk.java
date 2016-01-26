
package com.vzome.core.zomic.program;

import java.util .*;

import com.vzome.core.zomic.ZomicException;

public class Walk extends ZomicStatement implements Iterable<ZomicStatement>
{

	private List<ZomicStatement> stmts = new ArrayList<>();

	public  void accept( Visitor visitor ) throws ZomicException {
		visitor .visitWalk( this );
	}

	public  void addStatement( ZomicStatement stmt ) {
		stmts .add( stmt );
	}

    @Override
	public Iterator<ZomicStatement> iterator()
	{
	    return stmts .iterator();
	}

    @Deprecated	
    public Iterator<ZomicStatement> getStatements() {
		return this .iterator();
	}
	
	public int size(){
		return stmts .size();
	}


}


