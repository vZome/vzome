

package com.vzome.core.construction;

/**
 * @author Scott Vorthmann
 */
public interface ConstructionChanges
{
    void constructionAdded( Construction c );
    
    void constructionRemoved( Construction c );
    
    void constructionHidden( Construction c );
    
    void constructionRevealed( Construction c );
}
