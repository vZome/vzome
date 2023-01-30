package com.vzome.core.antlr;

import java.util.Stack;

import antlr.RecognitionException;
import antlr.Token;


/**
 * Presents a simple SAX-like (event-based) interface to an ANTLR parser
 * for driving a SAX ContentHandler or building any other XML representation.
 * 
 * @author Scott Vorthmann 2003
 */
@Deprecated
public interface ANTLR2XML {

    void setParser( antlr.Parser parser );

    void startElement(String name) throws RecognitionException;

    void attribute(String name, String value) throws RecognitionException;

    void endElement() throws RecognitionException;

    @SuppressWarnings("serial")
    @Deprecated
	public class ANTLR2XMLException extends RecognitionException {
        
        public ANTLR2XMLException( Token t, String message ) {
            super( message );
            line = t.getLine();
            column = t.getColumn();
        }

    }

    @Deprecated
	public abstract class Default extends Object implements ANTLR2XML {

        private Stack<String> m_elementNames = new Stack<>();

        protected antlr.Parser m_parser;

        @Override
        public void setParser( antlr.Parser parser ) {
            m_parser = parser;
        }

        @Override
        public void startElement(String name) throws RecognitionException {
            m_elementNames.push(name);
        }

        @Override
        public void endElement() throws RecognitionException {
            m_elementNames.pop();
        }

        protected String topElementName() {
            return m_elementNames.peek();
        }

        protected int getDepth() {
            return m_elementNames.size();
        }
    }

    public class Test extends Default {

        private boolean m_startIsPending = false;

        private void indent() {
            int depth = getDepth();

            for (int i = 0; i < depth; i++) {
                System.out.print("\t");
            }
        }

        @Override
        public void startElement(String name) throws RecognitionException {
            if (m_startIsPending) {
                System.out.println(">");
                m_startIsPending = false;
            }

            super.startElement(name);
            indent();
            System.out.print("<" + name);
            m_startIsPending = true;
        }

        @Override
        public void attribute(String name, String value)
                       throws RecognitionException {
            System.out.print(" " + name + "=\"" + value + "\"");
        }

        @Override
        public void endElement() throws RecognitionException {
            if (m_startIsPending) {
                System.out.println("/>");
                m_startIsPending = false;
            } else {
                indent();
                System.out.println("</" + topElementName() + ">");
            }

            super.endElement();
        }
    }
}
