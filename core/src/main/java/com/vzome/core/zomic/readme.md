## Listeners vs. Visitors
A listener implements an event interface, e.g. "enterBlah", "exitBlah". The actual traversal of the tree is performed by an ANTLR mechanism, which makes the event call-outs. The traversal is single-pass. A typical listener implementation will be very heavily stateful, and will have to maintain its own internal stack to store the state.

A listener could be fine for tree translation, bearing in mind the statefulness. It won't work at all for interpretation of a language that requires several passes to resolve identifiers, or to perform iteration, or whatever.

A visitor implementation performs the tree traversal itself, and therefore con control traversal order, repetitions, etc. Furthermore, visitor methods have return values, so the style can be much more functional, storing state in the execution stack.

I would expect a visitor implementation of ZomicASTCompiler to be much more concise and simple than the current listener implementation.

## Parsing Zomic
Having possibly made a more thorough analysis of the existing ANTLR3 and ANTLR4 Zomic parsers, I would take a different approach today, one of the following:

Low impact: rebuild ZomicASTCompiler as a visitor, but continue to build a custom AST with it. All existing AST Visitor subclasses (including the interpreter) will continue to work as before.

High impact: eliminate ZomicASTCompiler, and instead rewrite all Visitors to traverse the generated ANTLR parse tree, rather than our custom AST. If the parse tree retains all tokens, it may even be possible to discard the PrintVisitor, if ANTLR provides a generic parse tree pretty-printer.
