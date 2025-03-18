// Generated from /Users/vorth/vZome/vzome-all/core/src/main/antlr/com/vzome/core/grammar/zomic/ZomicParser.g4 by ANTLR 4.7.1
// jshint ignore: start
var antlr4 = require('antlr4/index');

// This class defines a complete generic visitor for a parse tree produced by ZomicParser.

function ZomicParserVisitor() {
	antlr4.tree.ParseTreeVisitor.call(this);
	return this;
}

ZomicParserVisitor.prototype = Object.create(antlr4.tree.ParseTreeVisitor.prototype);
ZomicParserVisitor.prototype.constructor = ZomicParserVisitor;

// Visit a parse tree produced by ZomicParser#program.
ZomicParserVisitor.prototype.visitProgram = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#stmt.
ZomicParserVisitor.prototype.visitStmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#compound_stmt.
ZomicParserVisitor.prototype.visitCompound_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#directCommand.
ZomicParserVisitor.prototype.visitDirectCommand = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#nestedCommand.
ZomicParserVisitor.prototype.visitNestedCommand = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#strut_stmt.
ZomicParserVisitor.prototype.visitStrut_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#label_stmt.
ZomicParserVisitor.prototype.visitLabel_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#scale_stmt.
ZomicParserVisitor.prototype.visitScale_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#build_stmt.
ZomicParserVisitor.prototype.visitBuild_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#move_stmt.
ZomicParserVisitor.prototype.visitMove_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#rotate_stmt.
ZomicParserVisitor.prototype.visitRotate_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#reflect_stmt.
ZomicParserVisitor.prototype.visitReflect_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#from_stmt.
ZomicParserVisitor.prototype.visitFrom_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#symmetry_stmt.
ZomicParserVisitor.prototype.visitSymmetry_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#repeat_stmt.
ZomicParserVisitor.prototype.visitRepeat_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#branch_stmt.
ZomicParserVisitor.prototype.visitBranch_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#save_stmt.
ZomicParserVisitor.prototype.visitSave_stmt = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#strut_length_expr.
ZomicParserVisitor.prototype.visitStrut_length_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#size_expr.
ZomicParserVisitor.prototype.visitSize_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#algebraic_number_expr.
ZomicParserVisitor.prototype.visitAlgebraic_number_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#explicit_size_expr.
ZomicParserVisitor.prototype.visitExplicit_size_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#SizeShort.
ZomicParserVisitor.prototype.visitSizeShort = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#SizeLong.
ZomicParserVisitor.prototype.visitSizeLong = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#SizeMedium.
ZomicParserVisitor.prototype.visitSizeMedium = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#symmetry_center_expr.
ZomicParserVisitor.prototype.visitSymmetry_center_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#axis_expr.
ZomicParserVisitor.prototype.visitAxis_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#axis_index_expr.
ZomicParserVisitor.prototype.visitAxis_index_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#axis_name_expr.
ZomicParserVisitor.prototype.visitAxis_name_expr = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#Red.
ZomicParserVisitor.prototype.visitRed = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#Blue.
ZomicParserVisitor.prototype.visitBlue = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#Yellow.
ZomicParserVisitor.prototype.visitYellow = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#green.
ZomicParserVisitor.prototype.visitGreen = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#orange.
ZomicParserVisitor.prototype.visitOrange = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#purple.
ZomicParserVisitor.prototype.visitPurple = function(ctx) {
  return this.visitChildren(ctx);
};


// Visit a parse tree produced by ZomicParser#black.
ZomicParserVisitor.prototype.visitBlack = function(ctx) {
  return this.visitChildren(ctx);
};



exports.ZomicParserVisitor = ZomicParserVisitor;