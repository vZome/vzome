package com.vzome.jsweet;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class JsRealizedModel implements RealizedModel {

    private final AlgebraicField field;

    public JsRealizedModel( AlgebraicField field )
    {
        this.field = field;
    }

    @Override
    public Iterator<Manifestation> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicField getField() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Manifestation findConstruction(Construction c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Manifestation removeConstruction(Construction c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Manifestation getManifestation(Construction c) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void show(Manifestation mManifestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide(Manifestation mManifestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(Manifestation m) {
        // TODO Auto-generated method stub

    }

    @Override
    public void remove(Manifestation mManifestation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setColor(Manifestation manifestation, Color color) {
        // TODO Auto-generated method stub

    }

}
