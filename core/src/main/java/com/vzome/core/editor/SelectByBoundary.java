package com.vzome.core.editor;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public abstract class SelectByBoundary extends ChangeManifestations {
    
    public SelectByBoundary(Selection selection, RealizedModel realized) {
        super(selection, realized);
    }

    public abstract String usage();
    
    @Override
    protected void fail(String message) throws Failure {
        final StringBuilder errorMsg = new StringBuilder();
        String usage = usage();
        if(usage != null) {
            errorMsg.append(usage);
        }
        if(message != null) {
            if(errorMsg.length() > 0) {
                errorMsg.append("\n");
            }
            errorMsg.append(message);
        }
        super.fail(errorMsg.toString());
    }

    /**
     * Sets the boundary criteria based on the selection
     * @return null if successful, otherwise a string describing the error.
     */
    protected abstract String setBoundary();
    
    @Override
    public void perform() throws Command.Failure {
        // Derived classes should setOrderedSelection() if applicable
        String errMsg = setBoundary();
        if(errMsg != null) {
            fail(errMsg); // throw an exception and we're done.
        }

        unselectAll();
        
        selectBoundedManifestations();
        redo(); // commit any selects
    }
    
    protected void selectBoundedManifestations() {
        for(Connector connector : this.getConnectors()) {
            if( boundaryContains(connector) ) {
                select(connector);
            }
        }
        for(Strut strut : this.getStruts()) {
            if( boundaryContains(strut) ) {
                select(strut);
            }
        }
        for(Panel panel : this.getPanels()) {
            if( boundaryContains(panel) ) {
                select(panel);
            }
        }
    }
    
    private boolean boundaryContains(Connector connector) {
        return boundaryContains( connector.getLocation() );
    }

    private boolean boundaryContains(Strut strut) {
        return boundaryContains( strut.getLocation() ) && boundaryContains( strut.getEnd() );
    }

    private boolean boundaryContains(Panel panel) {
        for(AlgebraicVector vertex : panel) {
            if(! boundaryContains( vertex )) {
                return false;
            }
        }
        return true;
    }

    protected abstract boolean boundaryContains(AlgebraicVector v);
}
