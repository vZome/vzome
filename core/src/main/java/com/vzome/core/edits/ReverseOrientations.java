package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class ReverseOrientations extends ChangeManifestations {

	public ReverseOrientations(EditorModel editorModel) {
		super(editorModel);
	}

	@Override
	public void perform() throws Failure {
		// I don't know that it matters, but we're going to remake all of the
		// selected parts in the order they were selected.
		// Keep a reference to all of the selected parts that we are going to delete.
		ArrayList<Manifestation> parts = new ArrayList<>();
		for (Manifestation man : this.getRenderedSelection()) {
			if (man instanceof Strut || man instanceof Panel) {
				parts.add(man);
			}
			unselect(man); // including balls.
		}
		redo(); // commit the unselects

		for (Manifestation man : parts) {
			this.deleteManifestation(man);
		}
		redo(); // commit the deletes

		for (Manifestation man : parts) {
			if (man instanceof Strut) {
				manifestConstruction(getReversedSegment((Strut) man));
			} else if (man instanceof Panel) {
				manifestConstruction(getReversedPolygon((Panel) man));
			}
		}
		redo(); // commit the new reversed parts
	}

	Segment getReversedSegment(Strut strut) {
		// Just swap start and end points.
		// In most cases, this is visually imperceptable,
		// but it can be seen in some chiral struts
		return new SegmentJoiningPoints(new FreePoint(strut.getEnd()), new FreePoint(strut.getLocation()));
	}

	Polygon getReversedPolygon(Panel panel) {
		List<Point> vertices = new ArrayList<>(panel.getVertexCount());
		for (AlgebraicVector vertex : panel) {
			vertices.add(new FreePoint(vertex));
		}
		List<Point> reversed = new ArrayList<>(panel.getVertexCount());
		// A panel will always have at least 3 vertices.
		// The first three vertices are the basis for determining the panel's normal.
		// Therefore, they can't be collinear. Other vertices could be collinear.
		// To be sure the new panel doesn't have its first three vertices collinear,
		// keep the same first three vertices, but reverse their winding order.
		reversed.add(vertices.get(2));
		reversed.add(vertices.get(1)); // the original vertex 1 remains as vertex 1
		reversed.add(vertices.get(0));
		// then add the remaining vertices in reverse order from the end of the list
		for (int i = vertices.size() - 1; i > 2; --i) {
			reversed.add(vertices.get(i));
		}
		return new PolygonFromVertices(reversed);
	}

	@Override
	protected String getXmlElementName() {
		return "ReverseOrientations";
	}
}
