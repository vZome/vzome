package org.vorthmann.ui;

public interface Configuration
{
	public abstract String getProperty( String propName );

	public abstract boolean propertyIsTrue( String propName );

	public abstract boolean userHasEntitlement( String propName );

	public abstract Configuration getConfiguration( String name );
}