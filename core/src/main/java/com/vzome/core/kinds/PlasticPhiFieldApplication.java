package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PlasticPhiField;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.IcosahedralToolFactory;

public class PlasticPhiFieldApplication extends DefaultFieldApplication {

    private final IcosahedralSymmetryPerspective icosahedralPerspective;
    protected final List<SymmetryPerspective> symmetryPerspectives = new ArrayList<>();
    private final QuaternionicSymmetry H4;
    
    public PlasticPhiFieldApplication(PlasticPhiField field) {
        super(field);
        // Icosahedral Symmetry and H4  QuaternionicSymmetry don't work with getGoldenRatio() yet. 
        // They mostly all assume that the first irrational is the golden ratio 
        // so we can't actually use this class or any others where that isn't true
        // until we fix all of the golden related constructs to use  getGoldenRatio() appropriately.
        icosahedralPerspective = new IcosahedralSymmetryPerspective(getField());
        symmetryPerspectives.add(icosahedralPerspective);
        symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
        H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", getField() );
    }

    @Override
    public PlasticPhiField getField() {
        // This cast to PlasticPhiField is safe 
        // because a PlasticPhiField is used in the c'tor
        return (PlasticPhiField) super.getField();
    }

    @Override
    public Collection<SymmetryPerspective> getSymmetryPerspectives() {
        return symmetryPerspectives;
    }
    
    @Override
    public SymmetryPerspective getDefaultSymmetryPerspective() {
        return icosahedralPerspective;
    }
    
    @Override
    public SymmetryPerspective getSymmetryPerspective(String symmName) {
        for(SymmetryPerspective sp : symmetryPerspectives) {
            if(sp.getName().equals(symmName)) {
                return sp;
            }
        }
        return super.getSymmetryPerspective(symmName);
    }

    @Override
    public void registerToolFactories( Map<String, Factory> toolFactories, ToolsModel tools )
    {
        // register the default tool factories
        super.registerToolFactories( toolFactories, tools );
        
        // add any tools that are unique for this field
        IcosahedralSymmetry symm = icosahedralPerspective .getSymmetry();
        // symm matters for this one, since it is final in the tool
        toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );
        // this one has to replace the same-named factory in the base class
        toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
    }

    @Override
    public QuaternionicSymmetry getQuaternionSymmetry( String name )
    {
        switch ( name ) {

        case "H_4":
            return this .H4;

        default:
            return null;
        }
    }

    private CommandUniformH4Polytope h4Builder = null;
    
    @Override
    public void constructPolytope( String groupName, int index, int edgesToRender, AlgebraicNumber[] edgeScales, Listener listener )
    {
        switch ( groupName ) {

        case "H4":
            if ( this .h4Builder == null ) {
                QuaternionicSymmetry qsymm = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", this .getField() );
                this .h4Builder = new CommandUniformH4Polytope( this .getField(), qsymm, 0 );
            }
            this .h4Builder .generate( index, edgesToRender, edgeScales, listener );
            break;

        default:
            super .constructPolytope( groupName, index, edgesToRender, edgeScales, listener );
            break;
        }
    }

}

