package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandUniformH4Polytope;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.AntiprismSymmetry;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction.Listener;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.IcosahedralToolFactory;
import com.vzome.core.tools.InversionToolFactory;
import com.vzome.core.tools.LineReflectionToolFactory;
import com.vzome.core.tools.LinearMapToolFactory;
import com.vzome.core.tools.MirrorToolFactory;
import com.vzome.core.tools.ProjectionToolFactory;
import com.vzome.core.tools.RotationToolFactory;
import com.vzome.core.tools.ScalingToolFactory;
import com.vzome.core.tools.SymmetryToolFactory;
import com.vzome.core.tools.TranslationToolFactory;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.AntiprismShapes;
import com.vzome.core.viewing.OctahedralShapes;

/**
 * Everything here is stateless, or at worst, a cache (like Shapes).
 * An instance of this can be shared by many DocumentModels.
 * This is why it does not have tool factories, though it does
 * dictate what tool factories will be present.
 *
 * @author David Hall
 *
 */
public class PolygonFieldApplication extends DefaultFieldApplication
{
    public PolygonFieldApplication( PolygonField field )
    {
        super( field );
        symmetryPerspectives.add( new AntiprismSymmetryPerspective());    
        
        if( field .polygonSides() == 5) { // TODO: eventually use getField().getGoldenRatio() != null here 
            // when we eventually generalize icosa symm and strut rendering to work with any 5N-gon.
            // Until then, enable icosa symm only for polygon(5) since it only works in that case for now.
            icosahedralPerspective = new IcosahedralSymmetryPerspective(getField());
            symmetryPerspectives.add(icosahedralPerspective);
            H4 = new QuaternionicSymmetry( "H_4", "com/vzome/core/math/symmetry/H4roots.vef", getField() );
        } else {
            icosahedralPerspective = null;
            H4 = null;
        }
        symmetryPerspectives.add(super.getDefaultSymmetryPerspective());
    }

    @Override
    public PolygonField getField() {
        // This cast to PolygonField is safe 
        // because a PolygonField is used in the c'tor
        return (PolygonField) super.getField();
    }
    
    private class AntiprismSymmetryPerspective extends AbstractSymmetryPerspective 
    {
        AntiprismSymmetryPerspective() {
            super(new AntiprismSymmetry(getField()).createStandardOrbits( "blue" ));
            AbstractShapes thinAntiprismShapes = new AntiprismShapes( "thin", "thin antiprism", getSymmetry() );
            AbstractShapes antiprismShapes = new AntiprismShapes( "antiprism", "antiprism", getSymmetry() );
            // TODO: Trapezohedron connectors (dual of Antiprism)
            AbstractShapes octahedralShapes = new OctahedralShapes( "octahedral", "octahedral", symmetry );
            
            // this is the order they will be shown on the dialog
            setDefaultGeometry(thinAntiprismShapes);
            addShapes(antiprismShapes);
            addShapes(octahedralShapes);
        }
        
        @Override
        public String getLabel() {
            return "antiprism";
        }

        @Override
        public AntiprismSymmetry getSymmetry() {
            // This cast to AntiprismSymmetry is safe 
            // because an AntiprismSymmetry is used in the c'tor
            return (AntiprismSymmetry) super.getSymmetry();
        }
        
        @Override
        public List<Tool.Factory> createToolFactories(Tool.Kind kind, ToolsModel tools) {
        	final boolean isTrivial = this.symmetry.isTrivial();
            List<Tool.Factory> result = new ArrayList<>();
            switch (kind) {

            case SYMMETRY:
                result.add(new SymmetryToolFactory(tools, this.symmetry));
                if(isTrivial) {
                	// InversionTool should be OK as long as there is no embedding
                	// which is always the case when PolygonField.getField().isEven().
                	result .add( new InversionToolFactory( tools ) );
                }
                result.add(new LineReflectionToolFactory(tools));
                result.add(new MirrorToolFactory(tools));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry));
                break;

            case TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry));
                result.add(new RotationToolFactory(tools, this.symmetry));
                result.add(new TranslationToolFactory(tools));
                if(isTrivial) {
                	// ProjectionTool should be OK as long as there is no embedding
                	// which is always the case when PolygonField.getField().isEven().
                	// TODO: I think if we make the ProjectionToolFactory check that neither 
                	// the projection plane's normal nor the projection direction (usually the same as the normal)
                	// are changed by the embedding (I think that might be an eigenvector of the embedding matrix)
                	// then we could allow ProjectionTools in non-trivial embeddings as well.
                	// For now, just allow it for the trivial embeddings.
                	result .add( new ProjectionToolFactory( tools ) );
                }
                break;

            case LINEAR_MAP:
                result.add(new LinearMapToolFactory(tools, this.symmetry, false));
                break;

            default:
                break;
            }
            return result;
        }

        @Override
        public List<Tool> predefineTools(Tool.Kind kind, ToolsModel tools) {
            List<Tool> result = new ArrayList<>();
            switch (kind) {

            case SYMMETRY:
                result.add(new SymmetryToolFactory(tools, this.symmetry).createPredefinedTool("polygonal antiprism around origin"));
                result.add(new MirrorToolFactory(tools).createPredefinedTool("reflection through XY plane"));
                result.add(new AxialSymmetryToolFactory(tools, this.symmetry, true).createPredefinedTool("symmetry around red through origin"));
                break;

            case TRANSFORM:
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale down"));
                result.add(new ScalingToolFactory(tools, this.symmetry).createPredefinedTool("scale up"));
                result.add(new RotationToolFactory(tools, this.symmetry, true).createPredefinedTool("rotate around red through origin"));
                break;

            default:
                break;
            }
            return result;
        }

        private final Command axialsymm = new CommandAxialSymmetry( symmetry );

        @Override
        public Command getLegacyCommand(String action) {
            switch (action) {
            case "axialsymm":
                return axialsymm;
            default:
                return super.getLegacyCommand(action);
            }
        }

        @Override
        public String getModelResourcePath() {
            // getTrackballModelStream() dynamically modifies this template file
            return "org/vorthmann/zome/app/antiprism-trackball-template.vZome";
        }
    };

    ArrayList<SymmetryPerspective> symmetryPerspectives = new ArrayList<>();
    
    @Override
    public Collection<SymmetryPerspective> getSymmetryPerspectives() {
        return symmetryPerspectives;
    }

    private final IcosahedralSymmetryPerspective icosahedralPerspective;

    @Override
    public SymmetryPerspective getDefaultSymmetryPerspective() {
        return (icosahedralPerspective == null) ? symmetryPerspectives.get(0) : icosahedralPerspective;
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
        if(icosahedralPerspective != null) {
            IcosahedralSymmetry symm = icosahedralPerspective .getSymmetry();
            // symm matters for this one, since it is final in the tool
            toolFactories .put( "AxialStretchTool", new AxialStretchTool.Factory( tools, symm, false, false, false ) );
            // this one has to replace the same-named factory in the base class
            toolFactories .put( "SymmetryTool", new IcosahedralToolFactory( tools, symm ) );
        }        
    }
    
    private final QuaternionicSymmetry H4;
    
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
