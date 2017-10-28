/**
 * Define a parser grammar called ZomicParser
 */

parser grammar ZomicParser;

options { 
	tokenVocab=ZomicLexer;
} 

program
	:	stmt*	// maybe just warn if no code (e.g. all comments)
		EOF		// generates an error if we don't parse everything
	;


stmt
	:	directCommand
	|	nestedCommand
	|	compound_stmt
	;


// we could just make stmt recursive, but this may be useful for printing the parse tree
compound_stmt
	:	LBRACE 
		stmt* 
		RBRACE 
	;


directCommand
	:	strut_stmt
	|	rotate_stmt
	|	reflect_stmt
	|	scale_stmt
	|	build_stmt
	|	move_stmt
	|	label_stmt
	// remove unsupported feature from the grammar although supporting code is partially in place
	// |	destroy_stmt
	;


nestedCommand
	:	from_stmt
	|	branch_stmt
	|	repeat_stmt
	|	symmetry_stmt
	|	save_stmt
	;


strut_stmt
	:	strut_length_expr
		axis_expr
	;


label_stmt
	:	LABEL 
		IDENT
	;


scale_stmt
	:	SCALE 
		scale = INT
	(	LPAREN 
		algebraic_number_expr?
		RPAREN
	)?
	;


build_stmt
	:	BUILD
	;


// remove unsupported keyword from the grammar although supporting code is partially in place
//destroy_stmt
//	:	DESTROY
//	;


move_stmt
	:	MOVE
	;
	
	
rotate_stmt
	:	ROTATE
		steps = INT?
		AROUND
		axis_expr
	;


reflect_stmt
	:	REFLECT
		symmetry_center_expr
	;


from_stmt
	:	FROM
		stmt
	;


symmetry_stmt
	:	SYMMETRY
	(	AROUND
	  	axis_expr
	|	symmetry_center_expr
	)?	stmt
	;


repeat_stmt
	:	REPEAT 
		count = INT
		stmt
	;


branch_stmt
	:	BRANCH
		stmt
	;


save_stmt
	:	SAVE
		state =
	(	LOCATION
	|	SCALE
	|	ORIENTATION
	|	BUILD
	|	ALL
	)   stmt
	;


/*
	Begin Expressions
*/


strut_length_expr
	:	size_expr
		algebraic_number_expr?
		HALF?
	;


size_expr
	:	explicit_size_expr 
	|	named_size_expr
	;


algebraic_number_expr
	:	ones = INT
		phis = INT?
	;


explicit_size_expr
	:	SIZE
	(	scale = INT			// TODO: support the old 'size -99' syntax used only by the internal resources until they are updated
	// remove unsupported sizeRef feature from the grammar although supporting code is partially in place
	// |	sizeRef = IDENT		// TODO: Document or remove this option
	|	isVariableLength = QUESTIONMARK	// TODO: DJH proposed alternative variable length indicator, same as size -99 used to be
	)
	;

named_size_expr
	:	SHORT		# SizeShort
	| 	LONG		# SizeLong
	|	MEDIUM		# SizeMedium
	|				# SizeMedium // default is MEDIUM
	;


symmetry_center_expr
	:	THROUGH
	(	CENTER
	|	
	(	blue_alias_expr? // Optional, only for readability or to simplify changing script from "around" to "through" symmetry statement
		blueAxisIndexNumber = INT // only an index to a blue axis is allowed here
	)
	)
	;


axis_expr
	:	axis_name_expr
		index = axis_index_expr
	;


axis_index_expr
	:	indexNumber = INT	// Polarity is significant. +0 is different from -0 so don't use parseInt
		handedness = POLARITY?
	;


// In order to simplify support for aliases, ZomicASTCompiler expects axis_name 
//  to use only lower case parser tokens (e.g. red) 
//  instead of  UPPER CASE  lexer tokens (e.g. RED)
//  so don't be tempted to "optimize away" the extra layer by making them ALL CAPS.
axis_name_expr
	:	red_alias_expr
	|	blue_alias_expr
	|	yellow_alias_expr
	|	green
	|	orange
	|	purple
	|	black
// Maybe new Zomic colors in the future?
// Adjust the unit tests when these are uncommented
//	|	lavender
//	|	olive
//	|	maroon
//	|	rose
//	|	navy
//	|	turquoise
//	|	coral
//	|	sulfur

// not yet supported in ZomicNamingConvention
//	|	sand
//	|	apple
//	|	cinnamon
//	|	spruce
//	|	brown
	;


// NOTE: ZomicASTCompiler expects all color aliases
//  to use only Proper Case labels (e.g. # Red) 
//  and the label must correspond to the literal color name
red_alias_expr
	:	RED			# Red
	|	PENT		# Red
	|	PENTAGON	# Red
	;


blue_alias_expr
	:	BLUE		# Blue
	|	RECT		# Blue
	|	RECTANGLE	# Blue
	;


yellow_alias_expr
	:	YELLOW		# Yellow
	|	TRI			# Yellow
	|	TRIANGLE	# Yellow
	;


green		:	GREEN;
orange		:	ORANGE;
purple		:	PURPLE;
black		:	BLACK;

// Maybe new Zomic colors in the future?
// Adjust the unit tests when these are uncommented
//lavender	:	LAVENDER;
//olive    	:	OLIVE;
//maroon    	:	MAROON;
//rose    	:	ROSE;
//navy    	:	NAVY;
//turquoise   :	TURQUOISE;
//coral    	:	CORAL;
//sulfur    	:	SULFUR;

// not yet supported in ZomicNamingConvention
//sand		:	SAND;
///apple		:	APPLE;
//cinnamon	:	CINNAMON;
//spruce	:	SPRUCE;
//brown		:	BROWN;
