package com.vzome.core.zomic.program;

import com.vzome.core.zomic.ZomicException;

public  abstract class ZomicStatement {

	public abstract void accept( Visitor visitor ) throws ZomicException;

    public void setErrors( String[] errors ){
        mErrors = errors;
    }
    
    public String[] getErrors(){
        return mErrors;
    }
    
    protected String[] mErrors;

}
