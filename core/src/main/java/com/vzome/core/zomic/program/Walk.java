package com.vzome.core.zomic.program;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.zomic.ZomicException;

public class Walk extends ZomicStatement implements Iterable<ZomicStatement>
{

	private List<ZomicStatement> stmts = new ArrayList<>();

    @Override
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

	public int size(){
		return stmts .size();
	}

}
