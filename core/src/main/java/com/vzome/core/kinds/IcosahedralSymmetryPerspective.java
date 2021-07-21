package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.List;

import com.vzome.api.Tool;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandAxialSymmetry;
import com.vzome.core.commands.CommandQuaternionSymmetry;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.commands.CommandTetrahedralSymmetry;
import com.vzome.core.commands.CommandVanOss600Cell;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.tools.AxialStretchTool;
import com.vzome.core.tools.AxialSymmetryToolFactory;
import com.vzome.core.tools.ChordRatioTool;
import com.vzome.core.tools.IcosahedralToolFactory;
import com.vzome.core.tools.InversionTool;
import com.vzome.core.tools.LinearMapTool;
import com.vzome.core.tools.MirrorTool;
import com.vzome.core.tools.ProjectionTool;
import com.vzome.core.tools.RotationTool;
import com.vzome.core.tools.ScalingTool;
import com.vzome.core.tools.TetrahedralToolFactory;
import com.vzome.core.tools.TranslationTool;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;

public class IcosahedralSymmetryPerspective extends AbstractSymmetryPerspective {

    private final QuaternionicSymmetry qSymmH4;
    private final QuaternionicSymmetry qSymmH4_ROT;
    private final QuaternionicSymmetry qSymmT2;

    private final Command cmdIcosasymm;
    private final Command cmdTetrasymm;
    private final Command cmdAxialsymm;
    private final Command cmdH4symmetry;
    private final Command cmdH4rotations;
    private final Command cmdIxTsymmetry;
    private final Command cmdTxTsymmetry;
    private final Command cmdVanOss600cell;

    public IcosahedralSymmetryPerspective(AlgebraicField field) {
        this( new IcosahedralSymmetry(field) );
    }
    
    protected IcosahedralSymmetryPerspective(IcosahedralSymmetry symm) {
        super(symm);

        final AbstractShapes icosadefaultShapes = new ExportedVEFShapes(null, "default", "solid connectors", this.symmetry);
        final AbstractShapes lifelikeShapes = new ExportedVEFShapes(null, "lifelike", "lifelike", this.symmetry, icosadefaultShapes);
        final AbstractShapes tinyShapes = new ExportedVEFShapes(null, "tiny", "tiny connectors", this.symmetry);
        final AbstractShapes tinyDodecs = new ExportedVEFShapes(null, "dodecs", "small dodecahedra", "tiny dodecahedra", this.symmetry, tinyShapes);
        final AbstractShapes bigZome = new ExportedVEFShapes(null, "bigzome", "Big Zome", this.symmetry, tinyShapes);
        final AbstractShapes noTwist = new ExportedVEFShapes( null, "noTwist", "no-twist 121 zone", this.symmetry, true );
        final AbstractShapes vienne2 = new ExportedVEFShapes(null, "vienne2", "Vienne", this.symmetry, icosadefaultShapes);
        final AbstractShapes vienne3 = new ExportedVEFShapes(null, "vienne3", "Vienne lifelike", this.symmetry, vienne2);
        final AbstractShapes vienne = new ExportedVEFShapes( null, "vienne", "Vienne 121 zone", this.symmetry, true );
        
        // this is the order they will be shown on the dialog
        addShapes(icosadefaultShapes); 
        addShapes(lifelikeShapes); 
        addShapes(tinyShapes); 
        addShapes(tinyDodecs); 
        addShapes(bigZome); 
        addShapes(noTwist); 
        addShapes(vienne2); 
        addShapes(vienne3); 
        addShapes(vienne);
        setDefaultGeometry(icosadefaultShapes);

        AlgebraicField field = this.symmetry.getField();
        qSymmH4 = new QuaternionicSymmetry("H_4", "com/vzome/core/math/symmetry/H4roots.vef", field);
        qSymmH4_ROT = new QuaternionicSymmetry("H4_ROT", "com/vzome/core/math/symmetry/H4roots-rotationalSubgroup.vef", field);
        qSymmT2 = new QuaternionicSymmetry("2T", "com/vzome/core/math/symmetry/binaryTetrahedralGroup.vef", field);

        cmdIcosasymm = new CommandSymmetry(this.symmetry);
        cmdTetrasymm = new CommandTetrahedralSymmetry(this.symmetry);
        cmdAxialsymm = new CommandAxialSymmetry(this.symmetry);
        cmdH4symmetry = new CommandQuaternionSymmetry(qSymmH4, qSymmH4);
        cmdH4rotations = new CommandQuaternionSymmetry(qSymmH4_ROT, qSymmH4_ROT);
        cmdIxTsymmetry = new CommandQuaternionSymmetry(qSymmH4, qSymmT2);
        cmdTxTsymmetry = new CommandQuaternionSymmetry(qSymmT2, qSymmT2);
        cmdVanOss600cell = new CommandVanOss600Cell();
    }

    @Override
    public IcosahedralSymmetry getSymmetry() {
        return (IcosahedralSymmetry) this.symmetry;
    }

    @Override
    public List<Tool.Factory> createToolFactories(Tool.Kind kind, ToolsModel tools) {
        List<Tool.Factory> result = new ArrayList<>();
        IcosahedralSymmetry icosaSymm = getSymmetry();
        switch (kind) {
        case SYMMETRY:
            result.add(new IcosahedralToolFactory(tools, icosaSymm));
            result.add(new TetrahedralToolFactory(tools, icosaSymm));
            result.add(new InversionTool.Factory(tools));
            result.add(new MirrorTool.Factory(tools));
            result.add(new AxialSymmetryToolFactory(tools, icosaSymm));
            break;
        case TRANSFORM:
            result.add(new ScalingTool.Factory(tools, icosaSymm));
            result.add(new RotationTool.Factory(tools, icosaSymm));
//            result.add(new PlaneSelectionTool.Factory(tools));
            result.add(new TranslationTool.Factory(tools));
            result.add(new ProjectionTool.Factory(tools));
            break;
        case CHORD_RATIO:
            result.add(new ChordRatioTool.Factory(tools, icosaSymm));
            break;
        case LINEAR_MAP:
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, true, true));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, false, true));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, true, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, true, false, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, false, true, false));
            result.add(new AxialStretchTool.Factory(tools, icosaSymm, false, false, false));
            result.add(new LinearMapTool.Factory(tools, icosaSymm, false));
            break;
        default:
            break;
        }
        return result;
    }

    @Override
    public List<Tool> predefineTools(Tool.Kind kind, ToolsModel tools) {
        List<Tool> result = new ArrayList<>();
        IcosahedralSymmetry icosaSymm = getSymmetry();
        switch (kind) {
        case SYMMETRY:
            result.add(new IcosahedralToolFactory(tools, icosaSymm).createPredefinedTool("icosahedral around origin"));
            result.add(new TetrahedralToolFactory(tools, icosaSymm).createPredefinedTool("tetrahedral around origin"));
            result.add(new InversionTool.Factory(tools).createPredefinedTool("reflection through origin"));
            result.add(new MirrorTool.Factory(tools).createPredefinedTool("reflection through XY plane"));
            result.add(new AxialSymmetryToolFactory(tools, icosaSymm).createPredefinedTool("symmetry around red through origin"));
            break;
        case TRANSFORM:
            result.add(new ScalingTool.Factory(tools, icosaSymm).createPredefinedTool("scale down"));
            result.add(new ScalingTool.Factory(tools, icosaSymm).createPredefinedTool("scale up"));
            result.add(new RotationTool.Factory(tools, icosaSymm).createPredefinedTool("rotate around red through origin"));
            result.add(new TranslationTool.Factory(tools).createPredefinedTool("b1 move along +X"));
            break;
        case CHORD_RATIO:
            result.add(new ChordRatioTool.Factory(tools, icosaSymm).createPredefinedTool("parallelagram"));
            result.add(new ChordRatioTool.Factory(tools, icosaSymm).createPredefinedTool("affine pentagon"));
            result.add(new ChordRatioTool.Factory(tools, icosaSymm).createPredefinedTool("affine hexagon"));
            result.add(new ChordRatioTool.Factory(tools, icosaSymm).createPredefinedTool("affine decagon"));
            result.add(new ChordRatioTool.Factory(tools, icosaSymm).createPredefinedTool("parabola"));
            break;
        default:
            break;
        }
        return result;
    }

    @Override
    public Command getLegacyCommand(String action) {
        switch (action) {
        case "icosasymm":
            return cmdIcosasymm;
        case "tetrasymm":
            return cmdTetrasymm;
        case "axialsymm":
            return cmdAxialsymm;
        case "h4symmetry":
            return cmdH4symmetry;
        case "h4rotations":
            return cmdH4rotations;
        case "IxTsymmetry":
            return cmdIxTsymmetry;
        case "TxTsymmetry":
            return cmdTxTsymmetry;
        case "vanOss600cell":
            return cmdVanOss600cell;
        default:
            return super.getLegacyCommand(action);
        }
    }

    public QuaternionicSymmetry getQuaternionSymmetry(String name) {
        switch (name) {
        case "H_4":
            return this.qSymmH4;
        case "H4_ROT":
            return this.qSymmH4_ROT;
        case "2T":
            return this.qSymmT2;
        default:
            return null;
        }
    }

    @Override
    public String getModelResourcePath() {
//        if (propertyIsTrue("rzome.trackball"))
//            return "org/vorthmann/zome/app/rZomeTrackball-vef.vZome";
//        else if (userHasEntitlement("developer.extras"))
//            return "org/vorthmann/zome/app/icosahedral-developer.vZome";
        return "org/vorthmann/zome/app/icosahedral-vef.vZome";
    }

}
