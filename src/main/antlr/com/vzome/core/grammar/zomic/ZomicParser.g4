/**
 * Define a parser grammar called ZomicParser
 */

parser grammar ZomicParser;

options { 
	tokenVocab=ZomicLexer;
} 

//tokens {
//	EMPTY
//}

program 
	:	
	(
		//unexpected_stmt | 
		stmt
	)? EOF;	// generate an error if we don't handle everything up to EOF, but OK if no code (e.g. all comments)

stmt
	:	compound_stmt
	|	direct_stmt
	|	nested_stmt
	;
	
compound_stmt :
	LBRACE stmt* RBRACE
	;

direct_stmt
	:	strut_stmt
	|	rotate_stmt
	|	reflect_stmt
	|	scale_stmt
	|	build_stmt
	|	move_stmt
	|	destroy_stmt
	|   label_stmt
	;


nested_stmt
	:	from_stmt
	|	branch_stmt
	|	repeat_stmt
	|	symmetry_stmt
	|	save_stmt
	;
	
//unexpected_stmt
//	:	UNEXPECTED_CHAR;

strut_stmt
	:	
//			{
//				xml.startElement( "strut" );
//			}
		size_expr
		length_expr?
		half_axis_expr |
		axis_expr
//			{
//				xml .endElement();
//			}
	;	
	
label_stmt
	:	LABEL id=IDENT
//			{
//				xml .startElement( "label" );
//				xml .attribute( "id", id.getText() );
//				xml .endElement();
//			}
	;
	
scale_stmt
	:	SCALE i=INT
//			{
//				xml .startElement( "scale" );
//				String size = i .getText();
//				if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
//					size = size .substring( 0, size.length()-1 );
//				xml .attribute( "size", size );
//			}
		(LPAREN length_expr RPAREN)?
//			{
//				xml .endElement();
//			}
	;

build_stmt
	:	BUILD
//				xml .startElement( "build" );
//				xml .attribute( "build", "on" );
//				xml .endElement();
//			}
	;
	
destroy_stmt
	:	DESTROY
//			{
//				xml .startElement( "build" );
//				xml .attribute( "destroy", "on" );
//				xml .endElement();
//			}
	;
	
move_stmt
	:	MOVE
//			{
//				xml .startElement( "build" );
//				xml .endElement();
//			}
	;	
	
	
rotate_stmt
	:	ROTATE
//			{
//				xml .startElement( "rotate" );
//			}
		( i=INT
//			{
//				String size = i .getText();
//				if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
//					size = size .substring( 0, size.length()-1 );
//				xml .attribute( "steps", size );
//			}
		)?
		AROUND axis_expr
//			{
//				xml .endElement();
//			}
	;
	
reflect_stmt
	:	REFLECT
//			{
//				xml .startElement( "reflect" );
//			}
		symmetry_center_expr
//			{
//				xml .endElement();
//			}
	;
	
from_stmt
	:	FROM
//			{
//				xml .startElement( "save" );
//				xml .attribute( "state", "build" );
//					xml .startElement( "model" );
//						xml .startElement( "build" );
//						xml .attribute( "mode", "off" );
//						xml .endElement();
//			}
		stmt
//			{
//					xml .endElement();
//				xml .endElement();
//			}
	;
	
symmetry_stmt
	:	SYMMETRY
//			{
//				xml .startElement( "symmetry" );
//			}
		(	AROUND
//			{
//				xml .startElement( "around" );
//			} 
		  	axis_expr
//			{
//				xml .endElement();
//			}
		|	symmetry_center_expr
		)?
		stmt
//			{
//				xml .endElement();
//			}
	;

repeat_stmt

	:	REPEAT i=INT
//			{
//				xml .startElement( "repeat" );
//					String axisNum = i.getText();
//					if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) )
//						axisNum = axisNum .substring( 1 );
//					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
//						axisNum = axisNum .substring( 0, axisNum.length()-1 );
//				xml .attribute( "count", axisNum );
//			}
		stmt
//			{
//				xml .endElement();
//			}
	;


branch_stmt

	:	BRANCH
//			{
//				xml .startElement( "save" );
//				xml .attribute( "state", "location" );
//			}
		stmt
//			{
//				xml .endElement();
//			}
	;


save_stmt

	:	SAVE
//			{
//				xml .startElement( "save" );
//			}
	(	LOCATION
//			{
//				xml .attribute( "state", "location" );
//			}
	|	SCALE
//			{
//				xml .attribute( "state", "scale" );
//			}
	|	ORIENTATION
//			{
//				xml .attribute( "state", "orientation" );
//			}
	|	BUILD
//			{
//				xml .attribute( "state", "build" );
//			}
	|	ALL
	)	
		stmt
//			{
//				xml .endElement();
//			}
	;

symmetry_center_expr
	:   THROUGH
//			{
//				xml .startElement( "through" );
//			}
			( CENTER
//				{
//					// leave the index attribute off
//				}
			| num=INT
//				{
//					String axisNum = num.getText();
//					if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) ) {
//						axisNum = axisNum .substring( 1 );
//					}
//					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
//						axisNum = axisNum .substring( 0, axisNum.length()-1 );
//					xml .attribute( "index", axisNum );
//				}
			)
//			{
//				xml .endElement();
//			}
	;

length_expr
	:  ones=INT		
//									{ String value = ones.getText();
//											if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
//												value = value .substring( 0, value.length()-1 );
//											xml .attribute( "lengthOnes", value );
//										}
	(	phis=INT			
//									{ value = phis.getText();
//											if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
//												value = value .substring( 0, value.length()-1 );
//											xml .attribute( "lengthPhis", value );
//										}
	)?
	;
		
size_expr
	// returns TBD...
	// TODO: Try to get away from java  specific
	//@init { bool useDefault = true; }
	//@after { if( useDefault ) { Token = MEDIUM; /* this is probably the wrong syntax */}; }
	:	//(
		explicit_size_expr //) { useDefault = false; }
	|	named_size_expr //) { useDefault = false; }
	//|	default_size_expr // ??? maybe
	//|   '->' MEDIUM // This may be the right syntax to return a default value when none is found
	//)
//	finally {
//		
//	}
	;

//default_size_expr : EMPTY;// -> MEDIUM;		//{ xml .attribute( "scale", Integer.toString( Constants.MEDIUM ) ); }

explicit_size_expr
	// TODO: Should we specifically generate a unique token for the "size -99" command so it can get hooked easier and more obviously?
	:	SIZE 
			i=INT
//				{
//					String axisNum = i.getText();
//					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
//						axisNum = axisNum .substring( 0, axisNum.length()-1 );
//									xml .attribute( "scale", axisNum );
//								}
		|	id=IDENT 
//				{ xml .attribute( "sizeRef", id.getText() ); }
			;

named_size_expr
	:	SHORT				//{ xml .attribute( "scale", Integer.toString( Constants.SHORT ) ); }
	| 	LONG				//{ xml .attribute( "scale", Integer.toString( Constants.LONG ) ); }
	|	MEDIUM | NULL		//{ xml .attribute( "scale", Integer.toString( Constants.MEDIUM ) ); } 
//	|						//{ xml .attribute( "scale", Integer.toString( Constants.MEDIUM ) ); /* DEFAULT */}
	;

half_axis_expr	
	:		HALF 				//{ xml .attribute( "half", "true" ); }
	(		blue_axis 
	| 		green
	);

axis_expr
	:	axis_name num=INT
//		{
//			String axisNum = num.getText();
/*
			if ( axisNum .startsWith( "-" ) ) {
				xml .attribute( "sense", "minus" );
				axisNum = axisNum .substring( 1 );
			}
			else {
				xml .attribute( "sense", "plus" );
				if ( axisNum .startsWith( "+" ) )
					axisNum = axisNum .substring( 1 );
			}
*/
		pol=handedness? 
//		{
//			if ( (pol != null) && ("=" == pol.getText()) ) {
//				xml.attribute( "handedness", "minus" );
//			}
//		}

//			xml .attribute( "index", axisNum );
			//// balancing startElement in axis_name
//			xml .endElement();
//		}
	;
	
handedness	: POLARITY ;

axis_name
	: red_axis				
	| blue_axis
	| yellow_axis
	| green
	| orange
	| purple
	| black
	;

red_axis
	: RED | PENT | PENTAGON		//{ xml .startElement( "red" ); }
	;

blue_axis
	: BLUE | RECT | RECTANGLE	//{ xml .startElement( "blue" ); }
	;

yellow_axis
	: YELLOW | TRI | TRIANGLE	//{ xml .startElement( "yellow" ); }
	;


green	:	GREEN;		//{ xml .startElement( "green" ); }
orange	:	ORANGE;		//{ xml .startElement( "orange" ); }
purple	:	PURPLE;		//{ xml .startElement( "purple" ); }
black	:	BLACK;		//{ xml .startElement( "black" ); }
