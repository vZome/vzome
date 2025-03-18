
import antlr4 from 'antlr4';
import ZomicLexer from './ZomicLexer.js';
import ZomicParser from './ZomicParser.js';
import ZomicParserListener from './ZomicParserListener.js';

export const interpretScript = ( script, language, offset, symmetry, effects, vzomePkg ) =>
{
  if ( language !== 'zomic' )
    throw new Error( `${language} is not a supported script language.` );

  const chars = new antlr4.InputStream( script );
  const lexer = new ZomicLexer( chars );
  const tokens = new antlr4.CommonTokenStream( lexer );
  const parser = new ZomicParser( tokens );
  const tree = parser.program();
  const compiler = new ZomicCompiler( symmetry, vzomePkg );
  antlr4.tree.ParseTreeWalker.DEFAULT.walk( compiler, tree );
  const program = compiler .getProgram();

  const builder = new vzomePkg.core.commands.ZomicVirtualMachine( offset, effects, symmetry );
  program .accept( new vzomePkg.core.zomic.Interpreter( builder, symmetry ) );
}

class ZomicCompiler extends ZomicParserListener
{
  constructor( symmetry, vzomePkg ) {
    super();
    this.state = new vzomePkg.core.zomic.ZomicCompilerState( symmetry );
    this.pkg = vzomePkg.core.zomic;
    this.render = vzomePkg.core.render;
    this.program = this.pkg.program;
  }

  getProgram() {
    return this.state.getProgram();
  }

	enterProgram(ctx) {
		this .state .prepareStatement( new this.program.Walk() );
		this .state .prepareStatement( new this.program.Walk() );	// old parser had an extra Walk on the stack so we will too
	}

	exitProgram(ctx) {
    this .state .commitLastStatement();	// old parser had an extra Walk on the stack so we will too
		if( this .state .getProgram() .size() === 0 ) {
			throw new Error( "We should always have a Walk by the time we get here!" );		
		}
	}

	enterCompound_stmt(ctx) {
    this .state .prepareStatement( new this.program.Walk() );
	}

	exitCompound_stmt(ctx) {
    this .state .commitLastStatement();
	}

	enterStrut_stmt(ctx) {
    this .state .prepareMoveTemplate();
	}

	exitStrut_stmt(ctx) {
    const template = this.state .popTemplate();
    try {
      this .state .commit(template.generate());
    } catch ( e) {
      throw new Error( e.getMessage() );
    }
  }

	exitLabel_stmt(ctx) {
    this .state .commit( new this.program.Label( ctx.IDENT() .text ) );
	}

	enterScale_stmt(ctx) {
    this .state .prepareScaleTemplate();
	}

	exitScale_stmt(ctx) {
		if( !! ctx.scale ) {
			this .state .setCurrentScale( parseInt( ctx.scale.text ) );
		}
		const template = this.state .popTemplate();
		this .state .commit( template.generate() );
	}

  exitBuild_stmt(ctx) {
    this .state .commit( new this.program.Build( /*build*/ true, /*destroy*/ false ) );
	}

  exitMove_stmt(ctx) {
    this .state .commit( new this.program.Build(/*build*/ false, /*destroy*/ false) );
	}

	enterRotate_stmt(ctx) {
    this .state .prepareRotateTemplate();
	}

	exitRotate_stmt(ctx) {
    try {
      const template = this.state .popTemplate();
      if ( !! ctx.steps ) {
        template.steps = parseInt( ctx.steps.text );
      }
      this .state .commit( template.generate() );
    } catch (e) {
      throw new Error( e.getMessage() );
    }
  }

	enterReflect_stmt(ctx) {
		const isThruCenter = ctx.symmetry_center_expr() .CENTER() !== null;
		this .state .prepareReflectTemplate(isThruCenter);
	}

	exitReflect_stmt(ctx) {
    const template = this.state .popTemplate();
		try {
      this .state .commit( template.generate() );
    } catch ( e) {
      throw new Error( e.getMessage() );
    }
	}

	enterFrom_stmt(ctx) {
    this .state .prepareStatement( new this.program.Save( this.render.ZomicEventHandler.ACTION ) );
    this .state .prepareStatement( new this.program.Walk() );
    this .state .commit( new this.program.Build(/*build*/ false, /*destroy*/ false) );
  }

	exitFrom_stmt(ctx) {
    this .state .commitLastStatement();
    this .state .commitLastStatement();
  }

	enterSymmetry_stmt(ctx) {
    let symmetryMode;
		if( ctx.axis_expr() !== null ) {
			symmetryMode = this.pkg.ZomicCompilerState.SymmetryModeEnum.RotateAroundAxis;
		} else if( ctx.symmetry_center_expr() === null ) {
			symmetryMode = this.pkg.ZomicCompilerState.SymmetryModeEnum.Icosahedral;
		} else if(ctx.symmetry_center_expr().blueAxisIndexNumber !== null) {
			symmetryMode = this.pkg.ZomicCompilerState.SymmetryModeEnum.MirrorThroughBlueAxis;
		} else if(ctx.symmetry_center_expr().CENTER() !== null) {
			symmetryMode = this.pkg.ZomicCompilerState.SymmetryModeEnum.ReflectThroughOrigin;
		} else {
			throw new Error( "Unexpected symmetry mode: " + ctx.text );
		}
		// push a SymmetryTemplate on the Templates stack to collect the Symmetry parameters
		this .state .prepareSymmetryTemplate(symmetryMode);
		// push an actual Symmetry statement on the Statements stack to collect the body
		this .state .prepareStatement(new this.program.Symmetry());
	}

	exitSymmetry_stmt(ctx) {
    const template = this.state .popTemplate();
		// SymmetryTemplate.generate() will apply collected template params 
		// to the Symmetry statement that's already on the Statement stack collecting nested statements in its body.
		// The Symmetry statement that's returned is the same object that's still on the stetements stack,
		// so we can just ignore the return value here.
		try {
      template.generate();
    } catch ( e) {
      throw new Error( e.getMessage() );
    }
		// Now commit the Symmetry statement and pop it off of the Statements stack.
		this .state .commitLastStatement();
	}

	enterRepeat_stmt(ctx) {
		// negative numbers are allowed but the sign is silently removed
    const txt = ctx.count.text;
    const count = parseInt( txt );
    this .state .prepareStatement( new this.program.Repeat( Math.abs( count )) );
	}

	exitRepeat_stmt(ctx) {
    this .state .commitLastStatement();
	}

	enterBranch_stmt(ctx) {
    this .state .prepareStatement( new this.program.Save( this.render.ZomicEventHandler.LOCATION ) );
	}

	exitBranch_stmt(ctx) {
    this .state .commitLastStatement();
	}

	enterSave_stmt(ctx) {
		let state = 0;
		switch(ctx.state.text) {
			case "orientation":
				state = this.render.ZomicEventHandler.ORIENTATION;
				break;
			case "scale":
				state = this.render.ZomicEventHandler.SCALE;
				break;
			case "location":
				state = this.render.ZomicEventHandler.LOCATION;
				break;
			case "build":
				state = this.render.ZomicEventHandler.ACTION;
				break;
			case "all":
				state = this.render.ZomicEventHandler.ALL;
				break;
			default:
				throw new Error( "Unexpected save parameter: " + ctx.state.text );
		}
		this .state .prepareStatement( new this.program.Save( state ) );
	}

	exitSave_stmt(ctx) {
    this .state .commitLastStatement();
	}

	exitStrut_length_expr(ctx) {
		if(ctx.HALF() !== null) {
			(this.state .peekTemplate()).denominator = 2;
		}
	}

	exitAlgebraic_number_expr(ctx) {
    const template = this.state .peekTemplate();
		template.ones = parseInt(ctx.ones.text);
		if(ctx.phis !== null) {
			template.phis = parseInt(ctx.phis.text);
		}
	}

	exitExplicit_size_expr(ctx) {
		if( ctx.scale !== null ) {
			this .state .setCurrentScale( parseInt(ctx.scale.text) );
		}
		const template = this.state .peekTemplate();
		// remove unsupported feature from the grammar although supporting code is partially in place
    //		if( ctx.sizeRef != null ) {
    //			String sizeRef = ctx.sizeRef.text();
    //			template.sizeRef = sizeRef;
    //			logger.warning("Ignoring undocumented sizeRef = '" + sizeRef + "'.");
    //		}
		if( ctx.isVariableLength !== null ) {
			template.isVariableLength( true );
		}
	}

	exitSizeShort(ctx) {
    this .state .setCurrentScale( this.pkg.ZomicNamingConvention.SHORT );
	}

  exitSizeLong(ctx) {
    this .state .setCurrentScale( this.pkg.ZomicNamingConvention.LONG );
	}

  exitSizeMedium(ctx) {
    this .state .setCurrentScale( this.pkg.ZomicNamingConvention.MEDIUM );
	}

	exitSymmetry_center_expr(ctx) {
    const elements = this.state .peekTemplate();
		if ( ctx.blueAxisIndexNumber !== null ) { 
			elements.setIndexNumber( ctx.blueAxisIndexNumber.text );
		}
	}

	exitAxis_index_expr(ctx) {
    const elements = this.state .peekTemplate();
		elements.setIndexNumber(ctx.INT());
		if ( ctx.handedness !== null ) { 
			elements.setHandedness(ctx.handedness.text);
		}
	}

	exitRed(ctx) {
    this.state .peekTemplate() .setAxisColor( "red" );
	}

  exitBlue(ctx) {
    this.state .peekTemplate() .setAxisColor( "blue" );
	}

  exitYellow(ctx) {
    this.state .peekTemplate() .setAxisColor( "yellow" );
	}

  exitGreen(ctx) {
    this.state .peekTemplate() .setAxisColor( "green" );
	}

  exitOrange(ctx) {
    this.state .peekTemplate() .setAxisColor( "orange" );
	}

	exitPurple(ctx) {
    this.state .peekTemplate() .setAxisColor( "purple" );
	}

	exitBlack(ctx) {
    this.state .peekTemplate() .setAxisColor( "black" );
	}
}