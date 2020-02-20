package com.vzome.core.kinds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.generic.Utilities;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;
import com.vzome.fields.sqrtphi.SqrtPhiFieldApplication;

public class FieldApplicationTest
{
    
    private static Set<FieldApplication> getTestFieldApplications() {
        Set<FieldApplication> result = new HashSet<>();
        result.add( new GoldenFieldApplication());
        result.add( new RootTwoFieldApplication());
        result.add( new RootThreeFieldApplication());
        result.add( new HeptagonFieldApplication());
        result.add( new SqrtPhiFieldApplication());
        result.add( new SnubDodecFieldApplication());
        return result;
    }
    
    @Test
    public void testFieldApplications()
    {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(FieldApplication app : getTestFieldApplications()) {
            final String appName = app.getName();
            Collection<SymmetryPerspective> perspectives = app.getSymmetryPerspectives();
            assertFalse(perspectives.isEmpty());
            SymmetryPerspective defaultPerspective = app.getDefaultSymmetryPerspective();
            assertNotNull(defaultPerspective);
            assertTrue(perspectives.contains(defaultPerspective));
            testCommands(app);
            
            Set<String> names = new HashSet<>();
            for(SymmetryPerspective perspective : perspectives) {
                testSymmetryPerspective(perspective, appName);
                String name = perspective.getName();
                assertFalse("SymmetryPerspective names should be unique", names.contains(name));
                names.add(name);
                testCommands(perspective, appName);
                testGeometries(perspective, appName);
                testModelResourcePath(perspective, appName);
            }
        }
        assertNull( null);
    }

    private void testSymmetryPerspective(SymmetryPerspective perspective, String appName) 
    {
        final String symmName = perspective.getSymmetry().getName();
        final String name = perspective.getName();
        assertEquals(appName + ".symmetryName", symmName, name);
        
        switch(name) {
        case "octahedral":
            break;
            
        case "icosahedral":
            break;
            
        case "dodecagonal":
            break;
            
        case "pentagonal":
            break;
            
        case "heptagonal antiprism":
            break;
            
        case "heptagonal antiprism corrected":
            break;
            
        case "synestructics":
            break;
            
        default:
            fail(appName + " has an unexpected perspective name: " + name);
        }
    }
    
    private void testCommands(FieldApplication app) {
        testCommand(app, "octasymm");
    }

    private void testCommands(SymmetryPerspective perspective, String appName) {
        testCommand(perspective, "octasymm");
    }

    private void testCommand(FieldApplication app, String action) {
        testCommand(app.getName(), action, app.getLegacyCommand(action));
    }

    private void testCommand(SymmetryPerspective perspective, String action) {
        testCommand(perspective.getName(), action, perspective.getLegacyCommand(action));
    }

    private void testCommand(String source, String action, Command command) {
        assertNotNull(source + ".getLegacyCommand( " + action + " )", command);
    }

    private void testGeometries(SymmetryPerspective perspective, String appName) 
    {
        assertNotNull(perspective);
        String name = perspective.getName();
        List<Shapes> geometries = perspective.getGeometries();
        Shapes defaultGeometry = perspective.getDefaultGeometry();
        assertNotNull(name, defaultGeometry);
        assertFalse(name, geometries.isEmpty());
        if(!geometries.contains(defaultGeometry)) {
            assertTrue(appName + "." + name + " SymmetryPerspective must contain defaultGeometry: " + defaultGeometry.getName(), geometries.contains(defaultGeometry));
        }
        for(Shapes shapes : geometries) {
            testConnectorShapes(perspective.getSymmetry(), shapes);
        }
    }

    private void testConnectorShapes(Symmetry symmetry, Shapes shapes) 
    {
        assertNotNull(shapes);
        String name = shapes.getName();
        assertNotNull(name + ".getConnectorShape()", shapes.getConnectorShape());
        AlgebraicField field = symmetry.getField();
        AlgebraicNumber length = field.createPower(1);
        for(Direction dir : symmetry) {
            assertNotNull(name + ".getStrutShape()", shapes.getStrutShape(dir, length));
        }
    }
    
    private void testModelResourcePath(SymmetryPerspective perspective, String appName)
    {
        String name = perspective.getName();
        String path = perspective.getModelResourcePath();
        String msg = appName + "." + name + ":\t\t" + path;
//        System.out.println(msg);
        assertTrue(msg, path.matches("^org/vorthmann/zome/app/.*\\.vZome"));
    }
}
