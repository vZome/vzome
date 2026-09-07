//  Explicit registries for the legacy classes that are looked up by name at
//  runtime.  Edit and command names come out of the .vZome XML, so a bundler
//  cannot infer which classes are needed; listing them here keeps every one
//  reachable while still letting esbuild see the dependency edges.  The old
//  code indexed the com.vzome.core.edits / .commands / .editor namespace
//  objects with a runtime string, which no bundler can follow.
//
//  Generated from the class files; keep in sync when classes are added.

import { AdjustSelectionByClass } from './from-java/com/vzome/core/edits/AdjustSelectionByClass.js';
import { AdjustSelectionByOrbitLength } from './from-java/com/vzome/core/edits/AdjustSelectionByOrbitLength.js';
import { AffineHeptagon } from './from-java/com/vzome/core/edits/AffineHeptagon.js';
import { AffinePentagon } from './from-java/com/vzome/core/edits/AffinePentagon.js';
import { AffinePolygon } from './from-java/com/vzome/core/edits/AffinePolygon.js';
import { AffineTransformAll } from './from-java/com/vzome/core/edits/AffineTransformAll.js';
import { B4Polytope } from './from-java/com/vzome/core/edits/B4Polytope.js';
import { ColorManifestations } from './from-java/com/vzome/core/edits/ColorManifestations.js';
import { ColorMappers } from './from-java/com/vzome/core/edits/ColorMappers.js';
import { ConvexHull2d } from './from-java/com/vzome/core/edits/ConvexHull2d.js';
import { ConvexHull3d } from './from-java/com/vzome/core/edits/ConvexHull3d.js';
import { CrossProduct } from './from-java/com/vzome/core/edits/CrossProduct.js';
import { Delete } from './from-java/com/vzome/core/edits/Delete.js';
import { DeselectAll } from './from-java/com/vzome/core/edits/DeselectAll.js';
import { DodecagonSymmetry } from './from-java/com/vzome/core/edits/DodecagonSymmetry.js';
import { GhostSymmetry24Cell } from './from-java/com/vzome/core/edits/GhostSymmetry24Cell.js';
import { GroupSelection } from './from-java/com/vzome/core/edits/GroupSelection.js';
import { HeptagonSubdivision } from './from-java/com/vzome/core/edits/HeptagonSubdivision.js';
import { InvertSelection } from './from-java/com/vzome/core/edits/InvertSelection.js';
import { JoinPointPair } from './from-java/com/vzome/core/edits/JoinPointPair.js';
import { JoinPoints } from './from-java/com/vzome/core/edits/JoinPoints.js';
import { JoinSkewLines } from './from-java/com/vzome/core/edits/JoinSkewLines.js';
import { Label } from './from-java/com/vzome/core/edits/Label.js';
import { LinePlaneIntersect } from './from-java/com/vzome/core/edits/LinePlaneIntersect.js';
import { LoadVEF } from './from-java/com/vzome/core/edits/LoadVEF.js';
import { ManifestationColorMappers } from './from-java/com/vzome/core/edits/ManifestationColorMappers.js';
import { MapToColor } from './from-java/com/vzome/core/edits/MapToColor.js';
import { NewCentroid } from './from-java/com/vzome/core/edits/NewCentroid.js';
import { PanelCentroids } from './from-java/com/vzome/core/edits/PanelCentroids.js';
import { PanelPanelIntersection } from './from-java/com/vzome/core/edits/PanelPanelIntersection.js';
import { PanelPerimeters } from './from-java/com/vzome/core/edits/PanelPerimeters.js';
import { Parallelepiped } from './from-java/com/vzome/core/edits/Parallelepiped.js';
import { PolarZonohedron } from './from-java/com/vzome/core/edits/PolarZonohedron.js';
import { Polytope4d } from './from-java/com/vzome/core/edits/Polytope4d.js';
import { RealizeMetaParts } from './from-java/com/vzome/core/edits/RealizeMetaParts.js';
import { ReplaceWithShape } from './from-java/com/vzome/core/edits/ReplaceWithShape.js';
import { ReversePanel } from './from-java/com/vzome/core/edits/ReversePanel.js';
import { RunZomicScript } from './from-java/com/vzome/core/edits/RunZomicScript.js';
import { SelectAll } from './from-java/com/vzome/core/edits/SelectAll.js';
import { SelectAutomaticStruts } from './from-java/com/vzome/core/edits/SelectAutomaticStruts.js';
import { SelectByDiameter } from './from-java/com/vzome/core/edits/SelectByDiameter.js';
import { SelectByPlane } from './from-java/com/vzome/core/edits/SelectByPlane.js';
import { SelectByRadius } from './from-java/com/vzome/core/edits/SelectByRadius.js';
import { SelectCollinear } from './from-java/com/vzome/core/edits/SelectCollinear.js';
import { SelectCoplanar } from './from-java/com/vzome/core/edits/SelectCoplanar.js';
import { SelectManifestation } from './from-java/com/vzome/core/edits/SelectManifestation.js';
import { SelectNeighbors } from './from-java/com/vzome/core/edits/SelectNeighbors.js';
import { SelectParallelStruts } from './from-java/com/vzome/core/edits/SelectParallelStruts.js';
import { ShowHidden } from './from-java/com/vzome/core/edits/ShowHidden.js';
import { ShowNormals } from './from-java/com/vzome/core/edits/ShowNormals.js';
import { ShowPoint } from './from-java/com/vzome/core/edits/ShowPoint.js';
import { ShowVertices } from './from-java/com/vzome/core/edits/ShowVertices.js';
import { StrutCreation } from './from-java/com/vzome/core/edits/StrutCreation.js';
import { StrutIntersection } from './from-java/com/vzome/core/edits/StrutIntersection.js';
import { StrutMove } from './from-java/com/vzome/core/edits/StrutMove.js';
import { Symmetry4d } from './from-java/com/vzome/core/edits/Symmetry4d.js';
import { SymmetryAxisChange } from './from-java/com/vzome/core/edits/SymmetryAxisChange.js';
import { SymmetryCenterChange } from './from-java/com/vzome/core/edits/SymmetryCenterChange.js';
import { TransformSelection } from './from-java/com/vzome/core/edits/TransformSelection.js';
import { Validate2Manifold } from './from-java/com/vzome/core/edits/Validate2Manifold.js';
import { ValidateSelection } from './from-java/com/vzome/core/edits/ValidateSelection.js';

import { AttributeMap } from './from-java/com/vzome/core/commands/AttributeMap.js';
import { CommandAxialSymmetry } from './from-java/com/vzome/core/commands/CommandAxialSymmetry.js';
import { CommandBuildAnchoredSegment } from './from-java/com/vzome/core/commands/CommandBuildAnchoredSegment.js';
import { CommandCentralSymmetry } from './from-java/com/vzome/core/commands/CommandCentralSymmetry.js';
import { CommandCentroid } from './from-java/com/vzome/core/commands/CommandCentroid.js';
import { CommandExecuteZomicScript } from './from-java/com/vzome/core/commands/CommandExecuteZomicScript.js';
import { CommandFreePoint } from './from-java/com/vzome/core/commands/CommandFreePoint.js';
import { CommandHide } from './from-java/com/vzome/core/commands/CommandHide.js';
import { CommandImportVEFData } from './from-java/com/vzome/core/commands/CommandImportVEFData.js';
import { CommandJoinPoints } from './from-java/com/vzome/core/commands/CommandJoinPoints.js';
import { CommandLinePlaneIntersect } from './from-java/com/vzome/core/commands/CommandLinePlaneIntersect.js';
import { CommandLoad } from './from-java/com/vzome/core/commands/CommandLoad.js';
import { CommandMidpoint } from './from-java/com/vzome/core/commands/CommandMidpoint.js';
import { CommandMirrorSymmetry } from './from-java/com/vzome/core/commands/CommandMirrorSymmetry.js';
import { CommandObliquePentagon } from './from-java/com/vzome/core/commands/CommandObliquePentagon.js';
import { CommandPolygon } from './from-java/com/vzome/core/commands/CommandPolygon.js';
import { CommandQuaternionSymmetry } from './from-java/com/vzome/core/commands/CommandQuaternionSymmetry.js';
import { CommandRotate } from './from-java/com/vzome/core/commands/CommandRotate.js';
import { CommandSetColor } from './from-java/com/vzome/core/commands/CommandSetColor.js';
import { CommandSymmetry } from './from-java/com/vzome/core/commands/CommandSymmetry.js';
import { CommandTauDivision } from './from-java/com/vzome/core/commands/CommandTauDivision.js';
import { CommandTetrahedralSymmetry } from './from-java/com/vzome/core/commands/CommandTetrahedralSymmetry.js';
import { CommandTranslate } from './from-java/com/vzome/core/commands/CommandTranslate.js';
import { CommandUniformH4Polytope } from './from-java/com/vzome/core/commands/CommandUniformH4Polytope.js';
import { CommandVanOss600Cell } from './from-java/com/vzome/core/commands/CommandVanOss600Cell.js';
import { XmlSaveFormat } from './from-java/com/vzome/core/commands/XmlSaveFormat.js';
import { XmlSymmetryFormat } from './from-java/com/vzome/core/commands/XmlSymmetryFormat.js';
import { ZomicVirtualMachine } from './from-java/com/vzome/core/commands/ZomicVirtualMachine.js';

import { ApplyTool } from './from-java/com/vzome/core/editor/ApplyTool.js';
import { BeginBlock } from './from-java/com/vzome/core/editor/BeginBlock.js';
import { Branch } from './from-java/com/vzome/core/editor/Branch.js';
import { CommandEdit } from './from-java/com/vzome/core/editor/CommandEdit.js';
import { Duplicator } from './from-java/com/vzome/core/editor/Duplicator.js';
import { EditHistory } from './from-java/com/vzome/core/editor/EditHistory.js';
import { EndBlock } from './from-java/com/vzome/core/editor/EndBlock.js';
import { SelectToolParameters } from './from-java/com/vzome/core/editor/SelectToolParameters.js';
import { SelectionImpl } from './from-java/com/vzome/core/editor/SelectionImpl.js';
import { SelectionSummary } from './from-java/com/vzome/core/editor/SelectionSummary.js';
import { Snapshot } from './from-java/com/vzome/core/editor/Snapshot.js';
import { SymmetrySystem } from './from-java/com/vzome/core/editor/SymmetrySystem.js';
import { ToolsModel } from './from-java/com/vzome/core/editor/ToolsModel.js';

export const editClasses = {
  AdjustSelectionByClass,
  AdjustSelectionByOrbitLength,
  AffineHeptagon,
  AffinePentagon,
  AffinePolygon,
  AffineTransformAll,
  B4Polytope,
  ColorManifestations,
  ColorMappers,
  ConvexHull2d,
  ConvexHull3d,
  CrossProduct,
  Delete,
  DeselectAll,
  DodecagonSymmetry,
  GhostSymmetry24Cell,
  GroupSelection,
  HeptagonSubdivision,
  InvertSelection,
  JoinPointPair,
  JoinPoints,
  JoinSkewLines,
  Label,
  LinePlaneIntersect,
  LoadVEF,
  ManifestationColorMappers,
  MapToColor,
  NewCentroid,
  PanelCentroids,
  PanelPanelIntersection,
  PanelPerimeters,
  Parallelepiped,
  PolarZonohedron,
  Polytope4d,
  RealizeMetaParts,
  ReplaceWithShape,
  ReversePanel,
  RunZomicScript,
  SelectAll,
  SelectAutomaticStruts,
  SelectByDiameter,
  SelectByPlane,
  SelectByRadius,
  SelectCollinear,
  SelectCoplanar,
  SelectManifestation,
  SelectNeighbors,
  SelectParallelStruts,
  ShowHidden,
  ShowNormals,
  ShowPoint,
  ShowVertices,
  StrutCreation,
  StrutIntersection,
  StrutMove,
  Symmetry4d,
  SymmetryAxisChange,
  SymmetryCenterChange,
  TransformSelection,
  Validate2Manifold,
  ValidateSelection
};

export const commandClasses = {
  AttributeMap,
  CommandAxialSymmetry,
  CommandBuildAnchoredSegment,
  CommandCentralSymmetry,
  CommandCentroid,
  CommandExecuteZomicScript,
  CommandFreePoint,
  CommandHide,
  CommandImportVEFData,
  CommandJoinPoints,
  CommandLinePlaneIntersect,
  CommandLoad,
  CommandMidpoint,
  CommandMirrorSymmetry,
  CommandObliquePentagon,
  CommandPolygon,
  CommandQuaternionSymmetry,
  CommandRotate,
  CommandSetColor,
  CommandSymmetry,
  CommandTauDivision,
  CommandTetrahedralSymmetry,
  CommandTranslate,
  CommandUniformH4Polytope,
  CommandVanOss600Cell,
  XmlSaveFormat,
  XmlSymmetryFormat,
  ZomicVirtualMachine
};

export const editorClasses = {
  ApplyTool,
  BeginBlock,
  Branch,
  CommandEdit,
  Duplicator,
  EditHistory,
  EndBlock,
  SelectToolParameters,
  SelectionImpl,
  SelectionSummary,
  Snapshot,
  SymmetrySystem,
  ToolsModel
};
