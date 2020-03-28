package com.vzome.core.kinds;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.commands.Command;
import com.vzome.core.commands.CommandSymmetry;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;

public abstract class AbstractSymmetryPerspective implements SymmetryPerspective {

    protected final Symmetry symmetry;

    private final List<Shapes> geometries = new ArrayList<>();

    private Shapes defaultShapes = null;

    public AbstractSymmetryPerspective(Symmetry symmetry) {
        this.symmetry = symmetry;
        this.symmetry.computeOrbitDots();
    }

    @Override
    public Symmetry getSymmetry() {
        return this.symmetry;
    }

    @Override
    public String getName() {
        return getSymmetry().getName();
    }

    protected void addShapes(Shapes shapes) {
        Shapes old = getGeometry(shapes.getName());
        if(old != null) {
            this.geometries.remove(old);
        }
        this.geometries.add(shapes);
    }

    protected void clearShapes() {
        this.geometries.clear();
        defaultShapes= null;
    }

    @Override
    public List<Shapes> getGeometries() {
        return this.geometries;
    }

    private Shapes getGeometry(String name) {
        for(Shapes shapes : geometries) {
            if(shapes.getName().equals(name)) {
                return shapes;
            }
        }
        return null;
    }

    public void setDefaultGeometry(Shapes shapes) {
        this.defaultShapes = shapes;
        addShapes(shapes);
    }

    @Override
    public Shapes getDefaultGeometry() {
        return this.defaultShapes;
    }

    @Override
    public Command getLegacyCommand( String action )
    {
        switch ( action ) {
        case "octasymm":
            // this will be availble to all SymmetryPerspectives even if they are not Octahedral
            return new CommandSymmetry( new OctahedralSymmetry(getSymmetry().getField(), "blue") );

        default:
            return null;
        }
    }

}
