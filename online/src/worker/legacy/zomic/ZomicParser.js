// Generated from /Users/vorth/vZome/vzome-all/core/src/main/antlr/com/vzome/core/grammar/zomic/ZomicParser.g4 by ANTLR 4.13.1
// jshint ignore: start
import antlr4 from 'antlr4';
import ZomicParserListener from './ZomicParserListener.js';
const serializedATN = [4,1,48,229,2,0,7,0,2,1,7,1,2,2,7,2,2,3,7,3,2,4,7,
4,2,5,7,5,2,6,7,6,2,7,7,7,2,8,7,8,2,9,7,9,2,10,7,10,2,11,7,11,2,12,7,12,
2,13,7,13,2,14,7,14,2,15,7,15,2,16,7,16,2,17,7,17,2,18,7,18,2,19,7,19,2,
20,7,20,2,21,7,21,2,22,7,22,2,23,7,23,2,24,7,24,2,25,7,25,2,26,7,26,2,27,
7,27,2,28,7,28,2,29,7,29,2,30,7,30,2,31,7,31,2,32,7,32,1,0,5,0,68,8,0,10,
0,12,0,71,9,0,1,0,1,0,1,1,1,1,1,1,3,1,78,8,1,1,2,1,2,5,2,82,8,2,10,2,12,
2,85,9,2,1,2,1,2,1,3,1,3,1,3,1,3,1,3,1,3,1,3,3,3,96,8,3,1,4,1,4,1,4,1,4,
1,4,3,4,103,8,4,1,5,1,5,1,5,1,6,1,6,1,6,1,7,1,7,1,7,1,7,3,7,115,8,7,1,7,
3,7,118,8,7,1,8,1,8,1,9,1,9,1,10,1,10,3,10,126,8,10,1,10,1,10,1,10,1,11,
1,11,1,11,1,12,1,12,1,12,1,13,1,13,1,13,1,13,3,13,141,8,13,1,13,1,13,1,14,
1,14,1,14,1,14,1,15,1,15,1,15,1,16,1,16,1,16,1,16,1,17,1,17,3,17,158,8,17,
1,17,3,17,161,8,17,1,18,1,18,3,18,165,8,18,1,19,1,19,3,19,169,8,19,1,20,
1,20,1,20,3,20,174,8,20,1,21,1,21,1,21,1,21,3,21,180,8,21,1,22,1,22,1,22,
3,22,185,8,22,1,22,3,22,188,8,22,1,23,1,23,1,23,1,24,1,24,3,24,195,8,24,
1,25,1,25,1,25,1,25,1,25,1,25,1,25,3,25,204,8,25,1,26,1,26,1,26,3,26,209,
8,26,1,27,1,27,1,27,3,27,214,8,27,1,28,1,28,1,28,3,28,219,8,28,1,29,1,29,
1,30,1,30,1,31,1,31,1,32,1,32,1,32,0,0,33,0,2,4,6,8,10,12,14,16,18,20,22,
24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64,0,1,2,0,2,
3,15,17,237,0,69,1,0,0,0,2,77,1,0,0,0,4,79,1,0,0,0,6,95,1,0,0,0,8,102,1,
0,0,0,10,104,1,0,0,0,12,107,1,0,0,0,14,110,1,0,0,0,16,119,1,0,0,0,18,121,
1,0,0,0,20,123,1,0,0,0,22,130,1,0,0,0,24,133,1,0,0,0,26,136,1,0,0,0,28,144,
1,0,0,0,30,148,1,0,0,0,32,151,1,0,0,0,34,155,1,0,0,0,36,164,1,0,0,0,38,166,
1,0,0,0,40,170,1,0,0,0,42,179,1,0,0,0,44,181,1,0,0,0,46,189,1,0,0,0,48,192,
1,0,0,0,50,203,1,0,0,0,52,208,1,0,0,0,54,213,1,0,0,0,56,218,1,0,0,0,58,220,
1,0,0,0,60,222,1,0,0,0,62,224,1,0,0,0,64,226,1,0,0,0,66,68,3,2,1,0,67,66,
1,0,0,0,68,71,1,0,0,0,69,67,1,0,0,0,69,70,1,0,0,0,70,72,1,0,0,0,71,69,1,
0,0,0,72,73,5,0,0,1,73,1,1,0,0,0,74,78,3,6,3,0,75,78,3,8,4,0,76,78,3,4,2,
0,77,74,1,0,0,0,77,75,1,0,0,0,77,76,1,0,0,0,78,3,1,0,0,0,79,83,5,36,0,0,
80,82,3,2,1,0,81,80,1,0,0,0,82,85,1,0,0,0,83,81,1,0,0,0,83,84,1,0,0,0,84,
86,1,0,0,0,85,83,1,0,0,0,86,87,5,37,0,0,87,5,1,0,0,0,88,96,3,10,5,0,89,96,
3,20,10,0,90,96,3,22,11,0,91,96,3,14,7,0,92,96,3,16,8,0,93,96,3,18,9,0,94,
96,3,12,6,0,95,88,1,0,0,0,95,89,1,0,0,0,95,90,1,0,0,0,95,91,1,0,0,0,95,92,
1,0,0,0,95,93,1,0,0,0,95,94,1,0,0,0,96,7,1,0,0,0,97,103,3,24,12,0,98,103,
3,30,15,0,99,103,3,28,14,0,100,103,3,26,13,0,101,103,3,32,16,0,102,97,1,
0,0,0,102,98,1,0,0,0,102,99,1,0,0,0,102,100,1,0,0,0,102,101,1,0,0,0,103,
9,1,0,0,0,104,105,3,34,17,0,105,106,3,46,23,0,106,11,1,0,0,0,107,108,5,1,
0,0,108,109,5,47,0,0,109,13,1,0,0,0,110,111,5,2,0,0,111,117,5,43,0,0,112,
114,5,38,0,0,113,115,3,38,19,0,114,113,1,0,0,0,114,115,1,0,0,0,115,116,1,
0,0,0,116,118,5,39,0,0,117,112,1,0,0,0,117,118,1,0,0,0,118,15,1,0,0,0,119,
120,5,3,0,0,120,17,1,0,0,0,121,122,5,4,0,0,122,19,1,0,0,0,123,125,5,5,0,
0,124,126,5,43,0,0,125,124,1,0,0,0,125,126,1,0,0,0,126,127,1,0,0,0,127,128,
5,6,0,0,128,129,3,46,23,0,129,21,1,0,0,0,130,131,5,7,0,0,131,132,3,44,22,
0,132,23,1,0,0,0,133,134,5,8,0,0,134,135,3,2,1,0,135,25,1,0,0,0,136,140,
5,9,0,0,137,138,5,6,0,0,138,141,3,46,23,0,139,141,3,44,22,0,140,137,1,0,
0,0,140,139,1,0,0,0,140,141,1,0,0,0,141,142,1,0,0,0,142,143,3,2,1,0,143,
27,1,0,0,0,144,145,5,12,0,0,145,146,5,43,0,0,146,147,3,2,1,0,147,29,1,0,
0,0,148,149,5,13,0,0,149,150,3,2,1,0,150,31,1,0,0,0,151,152,5,14,0,0,152,
153,7,0,0,0,153,154,3,2,1,0,154,33,1,0,0,0,155,157,3,36,18,0,156,158,3,38,
19,0,157,156,1,0,0,0,157,158,1,0,0,0,158,160,1,0,0,0,159,161,5,18,0,0,160,
159,1,0,0,0,160,161,1,0,0,0,161,35,1,0,0,0,162,165,3,40,20,0,163,165,3,42,
21,0,164,162,1,0,0,0,164,163,1,0,0,0,165,37,1,0,0,0,166,168,5,43,0,0,167,
169,5,43,0,0,168,167,1,0,0,0,168,169,1,0,0,0,169,39,1,0,0,0,170,173,5,19,
0,0,171,174,5,43,0,0,172,174,5,40,0,0,173,171,1,0,0,0,173,172,1,0,0,0,174,
41,1,0,0,0,175,180,5,20,0,0,176,180,5,22,0,0,177,180,5,21,0,0,178,180,1,
0,0,0,179,175,1,0,0,0,179,176,1,0,0,0,179,177,1,0,0,0,179,178,1,0,0,0,180,
43,1,0,0,0,181,187,5,10,0,0,182,188,5,11,0,0,183,185,3,54,27,0,184,183,1,
0,0,0,184,185,1,0,0,0,185,186,1,0,0,0,186,188,5,43,0,0,187,182,1,0,0,0,187,
184,1,0,0,0,188,45,1,0,0,0,189,190,3,50,25,0,190,191,3,48,24,0,191,47,1,
0,0,0,192,194,5,43,0,0,193,195,5,44,0,0,194,193,1,0,0,0,194,195,1,0,0,0,
195,49,1,0,0,0,196,204,3,52,26,0,197,204,3,54,27,0,198,204,3,56,28,0,199,
204,3,58,29,0,200,204,3,60,30,0,201,204,3,62,31,0,202,204,3,64,32,0,203,
196,1,0,0,0,203,197,1,0,0,0,203,198,1,0,0,0,203,199,1,0,0,0,203,200,1,0,
0,0,203,201,1,0,0,0,203,202,1,0,0,0,204,51,1,0,0,0,205,209,5,27,0,0,206,
209,5,28,0,0,207,209,5,29,0,0,208,205,1,0,0,0,208,206,1,0,0,0,208,207,1,
0,0,0,209,53,1,0,0,0,210,214,5,30,0,0,211,214,5,31,0,0,212,214,5,32,0,0,
213,210,1,0,0,0,213,211,1,0,0,0,213,212,1,0,0,0,214,55,1,0,0,0,215,219,5,
33,0,0,216,219,5,34,0,0,217,219,5,35,0,0,218,215,1,0,0,0,218,216,1,0,0,0,
218,217,1,0,0,0,219,57,1,0,0,0,220,221,5,23,0,0,221,59,1,0,0,0,222,223,5,
24,0,0,223,61,1,0,0,0,224,225,5,25,0,0,225,63,1,0,0,0,226,227,5,26,0,0,227,
65,1,0,0,0,22,69,77,83,95,102,114,117,125,140,157,160,164,168,173,179,184,
187,194,203,208,213,218];


const atn = new antlr4.atn.ATNDeserializer().deserialize(serializedATN);

const decisionsToDFA = atn.decisionToState.map( (ds, index) => new antlr4.dfa.DFA(ds, index) );

const sharedContextCache = new antlr4.atn.PredictionContextCache();

export default class ZomicParser extends antlr4.Parser {

    static grammarFileName = "ZomicParser.g4";
    static literalNames = [ null, "'label'", "'scale'", "'build'", "'move'", 
                            "'rotate'", "'around'", "'reflect'", "'from'", 
                            "'symmetry'", "'through'", "'center'", "'repeat'", 
                            "'branch'", "'save'", "'location'", "'orientation'", 
                            "'all'", "'half'", "'size'", "'short'", "'medium'", 
                            "'long'", "'green'", "'orange'", "'purple'", 
                            "'black'", "'red'", "'pent'", "'pentagon'", 
                            "'blue'", "'rect'", "'rectangle'", "'yellow'", 
                            "'tri'", "'triangle'", "'{'", "'}'", "'('", 
                            "')'", "'?'" ];
    static symbolicNames = [ null, "LABEL", "SCALE", "BUILD", "MOVE", "ROTATE", 
                             "AROUND", "REFLECT", "FROM", "SYMMETRY", "THROUGH", 
                             "CENTER", "REPEAT", "BRANCH", "SAVE", "LOCATION", 
                             "ORIENTATION", "ALL", "HALF", "SIZE", "SHORT", 
                             "MEDIUM", "LONG", "GREEN", "ORANGE", "PURPLE", 
                             "BLACK", "RED", "PENT", "PENTAGON", "BLUE", 
                             "RECT", "RECTANGLE", "YELLOW", "TRI", "TRIANGLE", 
                             "LBRACE", "RBRACE", "LPAREN", "RPAREN", "QUESTIONMARK", 
                             "WS", "EOL", "INT", "POLARITY", "SL_COMMENT", 
                             "ML_COMMENT", "IDENT", "UNEXPECTED_CHAR" ];
    static ruleNames = [ "program", "stmt", "compound_stmt", "directCommand", 
                         "nestedCommand", "strut_stmt", "label_stmt", "scale_stmt", 
                         "build_stmt", "move_stmt", "rotate_stmt", "reflect_stmt", 
                         "from_stmt", "symmetry_stmt", "repeat_stmt", "branch_stmt", 
                         "save_stmt", "strut_length_expr", "size_expr", 
                         "algebraic_number_expr", "explicit_size_expr", 
                         "named_size_expr", "symmetry_center_expr", "axis_expr", 
                         "axis_index_expr", "axis_name_expr", "red_alias_expr", 
                         "blue_alias_expr", "yellow_alias_expr", "green", 
                         "orange", "purple", "black" ];

    constructor(input) {
        super(input);
        this._interp = new antlr4.atn.ParserATNSimulator(this, atn, decisionsToDFA, sharedContextCache);
        this.ruleNames = ZomicParser.ruleNames;
        this.literalNames = ZomicParser.literalNames;
        this.symbolicNames = ZomicParser.symbolicNames;
    }



	program() {
	    let localctx = new ProgramContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 0, ZomicParser.RULE_program);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 69;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        while((((_la) & ~0x1f) === 0 && ((1 << _la) & 4294734782) !== 0) || ((((_la - 32)) & ~0x1f) === 0 && ((1 << (_la - 32)) & 2079) !== 0)) {
	            this.state = 66;
	            this.stmt();
	            this.state = 71;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	        }
	        this.state = 72;
	        this.match(ZomicParser.EOF);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	stmt() {
	    let localctx = new StmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 2, ZomicParser.RULE_stmt);
	    try {
	        this.state = 77;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 1:
	        case 2:
	        case 3:
	        case 4:
	        case 5:
	        case 7:
	        case 18:
	        case 19:
	        case 20:
	        case 21:
	        case 22:
	        case 23:
	        case 24:
	        case 25:
	        case 26:
	        case 27:
	        case 28:
	        case 29:
	        case 30:
	        case 31:
	        case 32:
	        case 33:
	        case 34:
	        case 35:
	        case 43:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 74;
	            this.directCommand();
	            break;
	        case 8:
	        case 9:
	        case 12:
	        case 13:
	        case 14:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 75;
	            this.nestedCommand();
	            break;
	        case 36:
	            this.enterOuterAlt(localctx, 3);
	            this.state = 76;
	            this.compound_stmt();
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	compound_stmt() {
	    let localctx = new Compound_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 4, ZomicParser.RULE_compound_stmt);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 79;
	        this.match(ZomicParser.LBRACE);
	        this.state = 83;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        while((((_la) & ~0x1f) === 0 && ((1 << _la) & 4294734782) !== 0) || ((((_la - 32)) & ~0x1f) === 0 && ((1 << (_la - 32)) & 2079) !== 0)) {
	            this.state = 80;
	            this.stmt();
	            this.state = 85;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	        }
	        this.state = 86;
	        this.match(ZomicParser.RBRACE);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	directCommand() {
	    let localctx = new DirectCommandContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 6, ZomicParser.RULE_directCommand);
	    try {
	        this.state = 95;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 18:
	        case 19:
	        case 20:
	        case 21:
	        case 22:
	        case 23:
	        case 24:
	        case 25:
	        case 26:
	        case 27:
	        case 28:
	        case 29:
	        case 30:
	        case 31:
	        case 32:
	        case 33:
	        case 34:
	        case 35:
	        case 43:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 88;
	            this.strut_stmt();
	            break;
	        case 5:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 89;
	            this.rotate_stmt();
	            break;
	        case 7:
	            this.enterOuterAlt(localctx, 3);
	            this.state = 90;
	            this.reflect_stmt();
	            break;
	        case 2:
	            this.enterOuterAlt(localctx, 4);
	            this.state = 91;
	            this.scale_stmt();
	            break;
	        case 3:
	            this.enterOuterAlt(localctx, 5);
	            this.state = 92;
	            this.build_stmt();
	            break;
	        case 4:
	            this.enterOuterAlt(localctx, 6);
	            this.state = 93;
	            this.move_stmt();
	            break;
	        case 1:
	            this.enterOuterAlt(localctx, 7);
	            this.state = 94;
	            this.label_stmt();
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	nestedCommand() {
	    let localctx = new NestedCommandContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 8, ZomicParser.RULE_nestedCommand);
	    try {
	        this.state = 102;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 8:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 97;
	            this.from_stmt();
	            break;
	        case 13:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 98;
	            this.branch_stmt();
	            break;
	        case 12:
	            this.enterOuterAlt(localctx, 3);
	            this.state = 99;
	            this.repeat_stmt();
	            break;
	        case 9:
	            this.enterOuterAlt(localctx, 4);
	            this.state = 100;
	            this.symmetry_stmt();
	            break;
	        case 14:
	            this.enterOuterAlt(localctx, 5);
	            this.state = 101;
	            this.save_stmt();
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	strut_stmt() {
	    let localctx = new Strut_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 10, ZomicParser.RULE_strut_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 104;
	        this.strut_length_expr();
	        this.state = 105;
	        this.axis_expr();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	label_stmt() {
	    let localctx = new Label_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 12, ZomicParser.RULE_label_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 107;
	        this.match(ZomicParser.LABEL);
	        this.state = 108;
	        this.match(ZomicParser.IDENT);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	scale_stmt() {
	    let localctx = new Scale_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 14, ZomicParser.RULE_scale_stmt);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 110;
	        this.match(ZomicParser.SCALE);
	        this.state = 111;
	        localctx.scale = this.match(ZomicParser.INT);
	        this.state = 117;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===38) {
	            this.state = 112;
	            this.match(ZomicParser.LPAREN);
	            this.state = 114;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	            if(_la===43) {
	                this.state = 113;
	                this.algebraic_number_expr();
	            }

	            this.state = 116;
	            this.match(ZomicParser.RPAREN);
	        }

	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	build_stmt() {
	    let localctx = new Build_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 16, ZomicParser.RULE_build_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 119;
	        this.match(ZomicParser.BUILD);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	move_stmt() {
	    let localctx = new Move_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 18, ZomicParser.RULE_move_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 121;
	        this.match(ZomicParser.MOVE);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	rotate_stmt() {
	    let localctx = new Rotate_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 20, ZomicParser.RULE_rotate_stmt);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 123;
	        this.match(ZomicParser.ROTATE);
	        this.state = 125;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===43) {
	            this.state = 124;
	            localctx.steps = this.match(ZomicParser.INT);
	        }

	        this.state = 127;
	        this.match(ZomicParser.AROUND);
	        this.state = 128;
	        this.axis_expr();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	reflect_stmt() {
	    let localctx = new Reflect_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 22, ZomicParser.RULE_reflect_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 130;
	        this.match(ZomicParser.REFLECT);
	        this.state = 131;
	        this.symmetry_center_expr();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	from_stmt() {
	    let localctx = new From_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 24, ZomicParser.RULE_from_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 133;
	        this.match(ZomicParser.FROM);
	        this.state = 134;
	        this.stmt();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	symmetry_stmt() {
	    let localctx = new Symmetry_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 26, ZomicParser.RULE_symmetry_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 136;
	        this.match(ZomicParser.SYMMETRY);
	        this.state = 140;
	        this._errHandler.sync(this);
	        switch (this._input.LA(1)) {
	        case 6:
	        	this.state = 137;
	        	this.match(ZomicParser.AROUND);
	        	this.state = 138;
	        	this.axis_expr();
	        	break;
	        case 10:
	        	this.state = 139;
	        	this.symmetry_center_expr();
	        	break;
	        case 1:
	        case 2:
	        case 3:
	        case 4:
	        case 5:
	        case 7:
	        case 8:
	        case 9:
	        case 12:
	        case 13:
	        case 14:
	        case 18:
	        case 19:
	        case 20:
	        case 21:
	        case 22:
	        case 23:
	        case 24:
	        case 25:
	        case 26:
	        case 27:
	        case 28:
	        case 29:
	        case 30:
	        case 31:
	        case 32:
	        case 33:
	        case 34:
	        case 35:
	        case 36:
	        case 43:
	        	break;
	        default:
	        	break;
	        }
	        this.state = 142;
	        this.stmt();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	repeat_stmt() {
	    let localctx = new Repeat_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 28, ZomicParser.RULE_repeat_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 144;
	        this.match(ZomicParser.REPEAT);
	        this.state = 145;
	        localctx.count = this.match(ZomicParser.INT);
	        this.state = 146;
	        this.stmt();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	branch_stmt() {
	    let localctx = new Branch_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 30, ZomicParser.RULE_branch_stmt);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 148;
	        this.match(ZomicParser.BRANCH);
	        this.state = 149;
	        this.stmt();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	save_stmt() {
	    let localctx = new Save_stmtContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 32, ZomicParser.RULE_save_stmt);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 151;
	        this.match(ZomicParser.SAVE);
	        this.state = 152;
	        localctx.state = this._input.LT(1);
	        _la = this._input.LA(1);
	        if(!((((_la) & ~0x1f) === 0 && ((1 << _la) & 229388) !== 0))) {
	            localctx.state = this._errHandler.recoverInline(this);
	        }
	        else {
	        	this._errHandler.reportMatch(this);
	            this.consume();
	        }
	        this.state = 153;
	        this.stmt();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	strut_length_expr() {
	    let localctx = new Strut_length_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 34, ZomicParser.RULE_strut_length_expr);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 155;
	        this.size_expr();
	        this.state = 157;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===43) {
	            this.state = 156;
	            this.algebraic_number_expr();
	        }

	        this.state = 160;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===18) {
	            this.state = 159;
	            this.match(ZomicParser.HALF);
	        }

	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	size_expr() {
	    let localctx = new Size_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 36, ZomicParser.RULE_size_expr);
	    try {
	        this.state = 164;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 19:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 162;
	            this.explicit_size_expr();
	            break;
	        case 18:
	        case 20:
	        case 21:
	        case 22:
	        case 23:
	        case 24:
	        case 25:
	        case 26:
	        case 27:
	        case 28:
	        case 29:
	        case 30:
	        case 31:
	        case 32:
	        case 33:
	        case 34:
	        case 35:
	        case 43:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 163;
	            this.named_size_expr();
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	algebraic_number_expr() {
	    let localctx = new Algebraic_number_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 38, ZomicParser.RULE_algebraic_number_expr);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 166;
	        localctx.ones = this.match(ZomicParser.INT);
	        this.state = 168;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===43) {
	            this.state = 167;
	            localctx.phis = this.match(ZomicParser.INT);
	        }

	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	explicit_size_expr() {
	    let localctx = new Explicit_size_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 40, ZomicParser.RULE_explicit_size_expr);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 170;
	        this.match(ZomicParser.SIZE);
	        this.state = 173;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 43:
	            this.state = 171;
	            localctx.scale = this.match(ZomicParser.INT);
	            break;
	        case 40:
	            this.state = 172;
	            localctx.isVariableLength = this.match(ZomicParser.QUESTIONMARK);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	named_size_expr() {
	    let localctx = new Named_size_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 42, ZomicParser.RULE_named_size_expr);
	    try {
	        this.state = 179;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 20:
	            localctx = new SizeShortContext(this, localctx);
	            this.enterOuterAlt(localctx, 1);
	            this.state = 175;
	            this.match(ZomicParser.SHORT);
	            break;
	        case 22:
	            localctx = new SizeLongContext(this, localctx);
	            this.enterOuterAlt(localctx, 2);
	            this.state = 176;
	            this.match(ZomicParser.LONG);
	            break;
	        case 21:
	            localctx = new SizeMediumContext(this, localctx);
	            this.enterOuterAlt(localctx, 3);
	            this.state = 177;
	            this.match(ZomicParser.MEDIUM);
	            break;
	        case 18:
	        case 23:
	        case 24:
	        case 25:
	        case 26:
	        case 27:
	        case 28:
	        case 29:
	        case 30:
	        case 31:
	        case 32:
	        case 33:
	        case 34:
	        case 35:
	        case 43:
	            localctx = new SizeMediumContext(this, localctx);
	            this.enterOuterAlt(localctx, 4);

	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	symmetry_center_expr() {
	    let localctx = new Symmetry_center_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 44, ZomicParser.RULE_symmetry_center_expr);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 181;
	        this.match(ZomicParser.THROUGH);
	        this.state = 187;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 11:
	            this.state = 182;
	            this.match(ZomicParser.CENTER);
	            break;
	        case 30:
	        case 31:
	        case 32:
	        case 43:
	            this.state = 184;
	            this._errHandler.sync(this);
	            _la = this._input.LA(1);
	            if(((((_la - 30)) & ~0x1f) === 0 && ((1 << (_la - 30)) & 7) !== 0)) {
	                this.state = 183;
	                this.blue_alias_expr();
	            }

	            this.state = 186;
	            localctx.blueAxisIndexNumber = this.match(ZomicParser.INT);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	axis_expr() {
	    let localctx = new Axis_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 46, ZomicParser.RULE_axis_expr);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 189;
	        this.axis_name_expr();
	        this.state = 190;
	        localctx.index = this.axis_index_expr();
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	axis_index_expr() {
	    let localctx = new Axis_index_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 48, ZomicParser.RULE_axis_index_expr);
	    var _la = 0;
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 192;
	        localctx.indexNumber = this.match(ZomicParser.INT);
	        this.state = 194;
	        this._errHandler.sync(this);
	        _la = this._input.LA(1);
	        if(_la===44) {
	            this.state = 193;
	            localctx.handedness = this.match(ZomicParser.POLARITY);
	        }

	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	axis_name_expr() {
	    let localctx = new Axis_name_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 50, ZomicParser.RULE_axis_name_expr);
	    try {
	        this.state = 203;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 27:
	        case 28:
	        case 29:
	            this.enterOuterAlt(localctx, 1);
	            this.state = 196;
	            this.red_alias_expr();
	            break;
	        case 30:
	        case 31:
	        case 32:
	            this.enterOuterAlt(localctx, 2);
	            this.state = 197;
	            this.blue_alias_expr();
	            break;
	        case 33:
	        case 34:
	        case 35:
	            this.enterOuterAlt(localctx, 3);
	            this.state = 198;
	            this.yellow_alias_expr();
	            break;
	        case 23:
	            this.enterOuterAlt(localctx, 4);
	            this.state = 199;
	            this.green();
	            break;
	        case 24:
	            this.enterOuterAlt(localctx, 5);
	            this.state = 200;
	            this.orange();
	            break;
	        case 25:
	            this.enterOuterAlt(localctx, 6);
	            this.state = 201;
	            this.purple();
	            break;
	        case 26:
	            this.enterOuterAlt(localctx, 7);
	            this.state = 202;
	            this.black();
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	red_alias_expr() {
	    let localctx = new Red_alias_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 52, ZomicParser.RULE_red_alias_expr);
	    try {
	        this.state = 208;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 27:
	            localctx = new RedContext(this, localctx);
	            this.enterOuterAlt(localctx, 1);
	            this.state = 205;
	            this.match(ZomicParser.RED);
	            break;
	        case 28:
	            localctx = new RedContext(this, localctx);
	            this.enterOuterAlt(localctx, 2);
	            this.state = 206;
	            this.match(ZomicParser.PENT);
	            break;
	        case 29:
	            localctx = new RedContext(this, localctx);
	            this.enterOuterAlt(localctx, 3);
	            this.state = 207;
	            this.match(ZomicParser.PENTAGON);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	blue_alias_expr() {
	    let localctx = new Blue_alias_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 54, ZomicParser.RULE_blue_alias_expr);
	    try {
	        this.state = 213;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 30:
	            localctx = new BlueContext(this, localctx);
	            this.enterOuterAlt(localctx, 1);
	            this.state = 210;
	            this.match(ZomicParser.BLUE);
	            break;
	        case 31:
	            localctx = new BlueContext(this, localctx);
	            this.enterOuterAlt(localctx, 2);
	            this.state = 211;
	            this.match(ZomicParser.RECT);
	            break;
	        case 32:
	            localctx = new BlueContext(this, localctx);
	            this.enterOuterAlt(localctx, 3);
	            this.state = 212;
	            this.match(ZomicParser.RECTANGLE);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	yellow_alias_expr() {
	    let localctx = new Yellow_alias_exprContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 56, ZomicParser.RULE_yellow_alias_expr);
	    try {
	        this.state = 218;
	        this._errHandler.sync(this);
	        switch(this._input.LA(1)) {
	        case 33:
	            localctx = new YellowContext(this, localctx);
	            this.enterOuterAlt(localctx, 1);
	            this.state = 215;
	            this.match(ZomicParser.YELLOW);
	            break;
	        case 34:
	            localctx = new YellowContext(this, localctx);
	            this.enterOuterAlt(localctx, 2);
	            this.state = 216;
	            this.match(ZomicParser.TRI);
	            break;
	        case 35:
	            localctx = new YellowContext(this, localctx);
	            this.enterOuterAlt(localctx, 3);
	            this.state = 217;
	            this.match(ZomicParser.TRIANGLE);
	            break;
	        default:
	            throw new antlr4.error.NoViableAltException(this);
	        }
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	green() {
	    let localctx = new GreenContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 58, ZomicParser.RULE_green);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 220;
	        this.match(ZomicParser.GREEN);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	orange() {
	    let localctx = new OrangeContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 60, ZomicParser.RULE_orange);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 222;
	        this.match(ZomicParser.ORANGE);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	purple() {
	    let localctx = new PurpleContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 62, ZomicParser.RULE_purple);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 224;
	        this.match(ZomicParser.PURPLE);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}



	black() {
	    let localctx = new BlackContext(this, this._ctx, this.state);
	    this.enterRule(localctx, 64, ZomicParser.RULE_black);
	    try {
	        this.enterOuterAlt(localctx, 1);
	        this.state = 226;
	        this.match(ZomicParser.BLACK);
	    } catch (re) {
	    	if(re instanceof antlr4.error.RecognitionException) {
		        localctx.exception = re;
		        this._errHandler.reportError(this, re);
		        this._errHandler.recover(this, re);
		    } else {
		    	throw re;
		    }
	    } finally {
	        this.exitRule();
	    }
	    return localctx;
	}


}

ZomicParser.EOF = antlr4.Token.EOF;
ZomicParser.LABEL = 1;
ZomicParser.SCALE = 2;
ZomicParser.BUILD = 3;
ZomicParser.MOVE = 4;
ZomicParser.ROTATE = 5;
ZomicParser.AROUND = 6;
ZomicParser.REFLECT = 7;
ZomicParser.FROM = 8;
ZomicParser.SYMMETRY = 9;
ZomicParser.THROUGH = 10;
ZomicParser.CENTER = 11;
ZomicParser.REPEAT = 12;
ZomicParser.BRANCH = 13;
ZomicParser.SAVE = 14;
ZomicParser.LOCATION = 15;
ZomicParser.ORIENTATION = 16;
ZomicParser.ALL = 17;
ZomicParser.HALF = 18;
ZomicParser.SIZE = 19;
ZomicParser.SHORT = 20;
ZomicParser.MEDIUM = 21;
ZomicParser.LONG = 22;
ZomicParser.GREEN = 23;
ZomicParser.ORANGE = 24;
ZomicParser.PURPLE = 25;
ZomicParser.BLACK = 26;
ZomicParser.RED = 27;
ZomicParser.PENT = 28;
ZomicParser.PENTAGON = 29;
ZomicParser.BLUE = 30;
ZomicParser.RECT = 31;
ZomicParser.RECTANGLE = 32;
ZomicParser.YELLOW = 33;
ZomicParser.TRI = 34;
ZomicParser.TRIANGLE = 35;
ZomicParser.LBRACE = 36;
ZomicParser.RBRACE = 37;
ZomicParser.LPAREN = 38;
ZomicParser.RPAREN = 39;
ZomicParser.QUESTIONMARK = 40;
ZomicParser.WS = 41;
ZomicParser.EOL = 42;
ZomicParser.INT = 43;
ZomicParser.POLARITY = 44;
ZomicParser.SL_COMMENT = 45;
ZomicParser.ML_COMMENT = 46;
ZomicParser.IDENT = 47;
ZomicParser.UNEXPECTED_CHAR = 48;

ZomicParser.RULE_program = 0;
ZomicParser.RULE_stmt = 1;
ZomicParser.RULE_compound_stmt = 2;
ZomicParser.RULE_directCommand = 3;
ZomicParser.RULE_nestedCommand = 4;
ZomicParser.RULE_strut_stmt = 5;
ZomicParser.RULE_label_stmt = 6;
ZomicParser.RULE_scale_stmt = 7;
ZomicParser.RULE_build_stmt = 8;
ZomicParser.RULE_move_stmt = 9;
ZomicParser.RULE_rotate_stmt = 10;
ZomicParser.RULE_reflect_stmt = 11;
ZomicParser.RULE_from_stmt = 12;
ZomicParser.RULE_symmetry_stmt = 13;
ZomicParser.RULE_repeat_stmt = 14;
ZomicParser.RULE_branch_stmt = 15;
ZomicParser.RULE_save_stmt = 16;
ZomicParser.RULE_strut_length_expr = 17;
ZomicParser.RULE_size_expr = 18;
ZomicParser.RULE_algebraic_number_expr = 19;
ZomicParser.RULE_explicit_size_expr = 20;
ZomicParser.RULE_named_size_expr = 21;
ZomicParser.RULE_symmetry_center_expr = 22;
ZomicParser.RULE_axis_expr = 23;
ZomicParser.RULE_axis_index_expr = 24;
ZomicParser.RULE_axis_name_expr = 25;
ZomicParser.RULE_red_alias_expr = 26;
ZomicParser.RULE_blue_alias_expr = 27;
ZomicParser.RULE_yellow_alias_expr = 28;
ZomicParser.RULE_green = 29;
ZomicParser.RULE_orange = 30;
ZomicParser.RULE_purple = 31;
ZomicParser.RULE_black = 32;

class ProgramContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_program;
    }

	EOF() {
	    return this.getToken(ZomicParser.EOF, 0);
	};

	stmt = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(StmtContext);
	    } else {
	        return this.getTypedRuleContext(StmtContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterProgram(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitProgram(this);
		}
	}


}



class StmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_stmt;
    }

	directCommand() {
	    return this.getTypedRuleContext(DirectCommandContext,0);
	};

	nestedCommand() {
	    return this.getTypedRuleContext(NestedCommandContext,0);
	};

	compound_stmt() {
	    return this.getTypedRuleContext(Compound_stmtContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterStmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitStmt(this);
		}
	}


}



class Compound_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_compound_stmt;
    }

	LBRACE() {
	    return this.getToken(ZomicParser.LBRACE, 0);
	};

	RBRACE() {
	    return this.getToken(ZomicParser.RBRACE, 0);
	};

	stmt = function(i) {
	    if(i===undefined) {
	        i = null;
	    }
	    if(i===null) {
	        return this.getTypedRuleContexts(StmtContext);
	    } else {
	        return this.getTypedRuleContext(StmtContext,i);
	    }
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterCompound_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitCompound_stmt(this);
		}
	}


}



class DirectCommandContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_directCommand;
    }

	strut_stmt() {
	    return this.getTypedRuleContext(Strut_stmtContext,0);
	};

	rotate_stmt() {
	    return this.getTypedRuleContext(Rotate_stmtContext,0);
	};

	reflect_stmt() {
	    return this.getTypedRuleContext(Reflect_stmtContext,0);
	};

	scale_stmt() {
	    return this.getTypedRuleContext(Scale_stmtContext,0);
	};

	build_stmt() {
	    return this.getTypedRuleContext(Build_stmtContext,0);
	};

	move_stmt() {
	    return this.getTypedRuleContext(Move_stmtContext,0);
	};

	label_stmt() {
	    return this.getTypedRuleContext(Label_stmtContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterDirectCommand(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitDirectCommand(this);
		}
	}


}



class NestedCommandContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_nestedCommand;
    }

	from_stmt() {
	    return this.getTypedRuleContext(From_stmtContext,0);
	};

	branch_stmt() {
	    return this.getTypedRuleContext(Branch_stmtContext,0);
	};

	repeat_stmt() {
	    return this.getTypedRuleContext(Repeat_stmtContext,0);
	};

	symmetry_stmt() {
	    return this.getTypedRuleContext(Symmetry_stmtContext,0);
	};

	save_stmt() {
	    return this.getTypedRuleContext(Save_stmtContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterNestedCommand(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitNestedCommand(this);
		}
	}


}



class Strut_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_strut_stmt;
    }

	strut_length_expr() {
	    return this.getTypedRuleContext(Strut_length_exprContext,0);
	};

	axis_expr() {
	    return this.getTypedRuleContext(Axis_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterStrut_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitStrut_stmt(this);
		}
	}


}



class Label_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_label_stmt;
    }

	LABEL() {
	    return this.getToken(ZomicParser.LABEL, 0);
	};

	IDENT() {
	    return this.getToken(ZomicParser.IDENT, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterLabel_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitLabel_stmt(this);
		}
	}


}



class Scale_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_scale_stmt;
        this.scale = null;
    }

	SCALE() {
	    return this.getToken(ZomicParser.SCALE, 0);
	};

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	LPAREN() {
	    return this.getToken(ZomicParser.LPAREN, 0);
	};

	RPAREN() {
	    return this.getToken(ZomicParser.RPAREN, 0);
	};

	algebraic_number_expr() {
	    return this.getTypedRuleContext(Algebraic_number_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterScale_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitScale_stmt(this);
		}
	}


}



class Build_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_build_stmt;
    }

	BUILD() {
	    return this.getToken(ZomicParser.BUILD, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterBuild_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitBuild_stmt(this);
		}
	}


}



class Move_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_move_stmt;
    }

	MOVE() {
	    return this.getToken(ZomicParser.MOVE, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterMove_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitMove_stmt(this);
		}
	}


}



class Rotate_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_rotate_stmt;
        this.steps = null;
    }

	ROTATE() {
	    return this.getToken(ZomicParser.ROTATE, 0);
	};

	AROUND() {
	    return this.getToken(ZomicParser.AROUND, 0);
	};

	axis_expr() {
	    return this.getTypedRuleContext(Axis_exprContext,0);
	};

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterRotate_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitRotate_stmt(this);
		}
	}


}



class Reflect_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_reflect_stmt;
    }

	REFLECT() {
	    return this.getToken(ZomicParser.REFLECT, 0);
	};

	symmetry_center_expr() {
	    return this.getTypedRuleContext(Symmetry_center_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterReflect_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitReflect_stmt(this);
		}
	}


}



class From_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_from_stmt;
    }

	FROM() {
	    return this.getToken(ZomicParser.FROM, 0);
	};

	stmt() {
	    return this.getTypedRuleContext(StmtContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterFrom_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitFrom_stmt(this);
		}
	}


}



class Symmetry_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_symmetry_stmt;
    }

	SYMMETRY() {
	    return this.getToken(ZomicParser.SYMMETRY, 0);
	};

	stmt() {
	    return this.getTypedRuleContext(StmtContext,0);
	};

	AROUND() {
	    return this.getToken(ZomicParser.AROUND, 0);
	};

	axis_expr() {
	    return this.getTypedRuleContext(Axis_exprContext,0);
	};

	symmetry_center_expr() {
	    return this.getTypedRuleContext(Symmetry_center_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSymmetry_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSymmetry_stmt(this);
		}
	}


}



class Repeat_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_repeat_stmt;
        this.count = null;
    }

	REPEAT() {
	    return this.getToken(ZomicParser.REPEAT, 0);
	};

	stmt() {
	    return this.getTypedRuleContext(StmtContext,0);
	};

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterRepeat_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitRepeat_stmt(this);
		}
	}


}



class Branch_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_branch_stmt;
    }

	BRANCH() {
	    return this.getToken(ZomicParser.BRANCH, 0);
	};

	stmt() {
	    return this.getTypedRuleContext(StmtContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterBranch_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitBranch_stmt(this);
		}
	}


}



class Save_stmtContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_save_stmt;
        this.state = null;
    }

	SAVE() {
	    return this.getToken(ZomicParser.SAVE, 0);
	};

	stmt() {
	    return this.getTypedRuleContext(StmtContext,0);
	};

	LOCATION() {
	    return this.getToken(ZomicParser.LOCATION, 0);
	};

	SCALE() {
	    return this.getToken(ZomicParser.SCALE, 0);
	};

	ORIENTATION() {
	    return this.getToken(ZomicParser.ORIENTATION, 0);
	};

	BUILD() {
	    return this.getToken(ZomicParser.BUILD, 0);
	};

	ALL() {
	    return this.getToken(ZomicParser.ALL, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSave_stmt(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSave_stmt(this);
		}
	}


}



class Strut_length_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_strut_length_expr;
    }

	size_expr() {
	    return this.getTypedRuleContext(Size_exprContext,0);
	};

	algebraic_number_expr() {
	    return this.getTypedRuleContext(Algebraic_number_exprContext,0);
	};

	HALF() {
	    return this.getToken(ZomicParser.HALF, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterStrut_length_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitStrut_length_expr(this);
		}
	}


}



class Size_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_size_expr;
    }

	explicit_size_expr() {
	    return this.getTypedRuleContext(Explicit_size_exprContext,0);
	};

	named_size_expr() {
	    return this.getTypedRuleContext(Named_size_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSize_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSize_expr(this);
		}
	}


}



class Algebraic_number_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_algebraic_number_expr;
        this.ones = null;
        this.phis = null;
    }

	INT = function(i) {
		if(i===undefined) {
			i = null;
		}
	    if(i===null) {
	        return this.getTokens(ZomicParser.INT);
	    } else {
	        return this.getToken(ZomicParser.INT, i);
	    }
	};


	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterAlgebraic_number_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitAlgebraic_number_expr(this);
		}
	}


}



class Explicit_size_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_explicit_size_expr;
        this.scale = null;
        this.isVariableLength = null;
    }

	SIZE() {
	    return this.getToken(ZomicParser.SIZE, 0);
	};

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	QUESTIONMARK() {
	    return this.getToken(ZomicParser.QUESTIONMARK, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterExplicit_size_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitExplicit_size_expr(this);
		}
	}


}



class Named_size_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_named_size_expr;
    }


	 
		copyFrom(ctx) {
			super.copyFrom(ctx);
		}

}


class SizeShortContext extends Named_size_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	SHORT() {
	    return this.getToken(ZomicParser.SHORT, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSizeShort(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSizeShort(this);
		}
	}


}

ZomicParser.SizeShortContext = SizeShortContext;

class SizeLongContext extends Named_size_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	LONG() {
	    return this.getToken(ZomicParser.LONG, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSizeLong(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSizeLong(this);
		}
	}


}

ZomicParser.SizeLongContext = SizeLongContext;

class SizeMediumContext extends Named_size_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	MEDIUM() {
	    return this.getToken(ZomicParser.MEDIUM, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSizeMedium(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSizeMedium(this);
		}
	}


}

ZomicParser.SizeMediumContext = SizeMediumContext;

class Symmetry_center_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_symmetry_center_expr;
        this.blueAxisIndexNumber = null;
    }

	THROUGH() {
	    return this.getToken(ZomicParser.THROUGH, 0);
	};

	CENTER() {
	    return this.getToken(ZomicParser.CENTER, 0);
	};

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	blue_alias_expr() {
	    return this.getTypedRuleContext(Blue_alias_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterSymmetry_center_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitSymmetry_center_expr(this);
		}
	}


}



class Axis_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_axis_expr;
        this.index = null;
    }

	axis_name_expr() {
	    return this.getTypedRuleContext(Axis_name_exprContext,0);
	};

	axis_index_expr() {
	    return this.getTypedRuleContext(Axis_index_exprContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterAxis_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitAxis_expr(this);
		}
	}


}



class Axis_index_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_axis_index_expr;
        this.indexNumber = null;
        this.handedness = null;
    }

	INT() {
	    return this.getToken(ZomicParser.INT, 0);
	};

	POLARITY() {
	    return this.getToken(ZomicParser.POLARITY, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterAxis_index_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitAxis_index_expr(this);
		}
	}


}



class Axis_name_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_axis_name_expr;
    }

	red_alias_expr() {
	    return this.getTypedRuleContext(Red_alias_exprContext,0);
	};

	blue_alias_expr() {
	    return this.getTypedRuleContext(Blue_alias_exprContext,0);
	};

	yellow_alias_expr() {
	    return this.getTypedRuleContext(Yellow_alias_exprContext,0);
	};

	green() {
	    return this.getTypedRuleContext(GreenContext,0);
	};

	orange() {
	    return this.getTypedRuleContext(OrangeContext,0);
	};

	purple() {
	    return this.getTypedRuleContext(PurpleContext,0);
	};

	black() {
	    return this.getTypedRuleContext(BlackContext,0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterAxis_name_expr(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitAxis_name_expr(this);
		}
	}


}



class Red_alias_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_red_alias_expr;
    }


	 
		copyFrom(ctx) {
			super.copyFrom(ctx);
		}

}


class RedContext extends Red_alias_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	RED() {
	    return this.getToken(ZomicParser.RED, 0);
	};

	PENT() {
	    return this.getToken(ZomicParser.PENT, 0);
	};

	PENTAGON() {
	    return this.getToken(ZomicParser.PENTAGON, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterRed(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitRed(this);
		}
	}


}

ZomicParser.RedContext = RedContext;

class Blue_alias_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_blue_alias_expr;
    }


	 
		copyFrom(ctx) {
			super.copyFrom(ctx);
		}

}


class BlueContext extends Blue_alias_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	BLUE() {
	    return this.getToken(ZomicParser.BLUE, 0);
	};

	RECT() {
	    return this.getToken(ZomicParser.RECT, 0);
	};

	RECTANGLE() {
	    return this.getToken(ZomicParser.RECTANGLE, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterBlue(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitBlue(this);
		}
	}


}

ZomicParser.BlueContext = BlueContext;

class Yellow_alias_exprContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_yellow_alias_expr;
    }


	 
		copyFrom(ctx) {
			super.copyFrom(ctx);
		}

}


class YellowContext extends Yellow_alias_exprContext {

    constructor(parser, ctx) {
        super(parser);
        super.copyFrom(ctx);
    }

	YELLOW() {
	    return this.getToken(ZomicParser.YELLOW, 0);
	};

	TRI() {
	    return this.getToken(ZomicParser.TRI, 0);
	};

	TRIANGLE() {
	    return this.getToken(ZomicParser.TRIANGLE, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterYellow(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitYellow(this);
		}
	}


}

ZomicParser.YellowContext = YellowContext;

class GreenContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_green;
    }

	GREEN() {
	    return this.getToken(ZomicParser.GREEN, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterGreen(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitGreen(this);
		}
	}


}



class OrangeContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_orange;
    }

	ORANGE() {
	    return this.getToken(ZomicParser.ORANGE, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterOrange(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitOrange(this);
		}
	}


}



class PurpleContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_purple;
    }

	PURPLE() {
	    return this.getToken(ZomicParser.PURPLE, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterPurple(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitPurple(this);
		}
	}


}



class BlackContext extends antlr4.ParserRuleContext {

    constructor(parser, parent, invokingState) {
        if(parent===undefined) {
            parent = null;
        }
        if(invokingState===undefined || invokingState===null) {
            invokingState = -1;
        }
        super(parent, invokingState);
        this.parser = parser;
        this.ruleIndex = ZomicParser.RULE_black;
    }

	BLACK() {
	    return this.getToken(ZomicParser.BLACK, 0);
	};

	enterRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.enterBlack(this);
		}
	}

	exitRule(listener) {
	    if(listener instanceof ZomicParserListener ) {
	        listener.exitBlack(this);
		}
	}


}




ZomicParser.ProgramContext = ProgramContext; 
ZomicParser.StmtContext = StmtContext; 
ZomicParser.Compound_stmtContext = Compound_stmtContext; 
ZomicParser.DirectCommandContext = DirectCommandContext; 
ZomicParser.NestedCommandContext = NestedCommandContext; 
ZomicParser.Strut_stmtContext = Strut_stmtContext; 
ZomicParser.Label_stmtContext = Label_stmtContext; 
ZomicParser.Scale_stmtContext = Scale_stmtContext; 
ZomicParser.Build_stmtContext = Build_stmtContext; 
ZomicParser.Move_stmtContext = Move_stmtContext; 
ZomicParser.Rotate_stmtContext = Rotate_stmtContext; 
ZomicParser.Reflect_stmtContext = Reflect_stmtContext; 
ZomicParser.From_stmtContext = From_stmtContext; 
ZomicParser.Symmetry_stmtContext = Symmetry_stmtContext; 
ZomicParser.Repeat_stmtContext = Repeat_stmtContext; 
ZomicParser.Branch_stmtContext = Branch_stmtContext; 
ZomicParser.Save_stmtContext = Save_stmtContext; 
ZomicParser.Strut_length_exprContext = Strut_length_exprContext; 
ZomicParser.Size_exprContext = Size_exprContext; 
ZomicParser.Algebraic_number_exprContext = Algebraic_number_exprContext; 
ZomicParser.Explicit_size_exprContext = Explicit_size_exprContext; 
ZomicParser.Named_size_exprContext = Named_size_exprContext; 
ZomicParser.Symmetry_center_exprContext = Symmetry_center_exprContext; 
ZomicParser.Axis_exprContext = Axis_exprContext; 
ZomicParser.Axis_index_exprContext = Axis_index_exprContext; 
ZomicParser.Axis_name_exprContext = Axis_name_exprContext; 
ZomicParser.Red_alias_exprContext = Red_alias_exprContext; 
ZomicParser.Blue_alias_exprContext = Blue_alias_exprContext; 
ZomicParser.Yellow_alias_exprContext = Yellow_alias_exprContext; 
ZomicParser.GreenContext = GreenContext; 
ZomicParser.OrangeContext = OrangeContext; 
ZomicParser.PurpleContext = PurpleContext; 
ZomicParser.BlackContext = BlackContext; 
