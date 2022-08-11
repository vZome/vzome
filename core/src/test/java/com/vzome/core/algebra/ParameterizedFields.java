package com.vzome.core.algebra;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author David Hall
 * This class is a collection of static methods,
 * originally intended for developing, testing and documenting ParameterizedFields,
 * but ultimately useful for other AlgebraicFields as well. 
 */
public class ParameterizedFields {
    
    public static String getCanonicalLabel(int i) {
        if(i<0) { 
            return "?";
        }
        // no leading space
        final String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int k = (i-1) % s.length();
        final String suffix = (k == i-1) ? "" : "'";
        return i == 0 
                ? "_"
                : s.substring(k, k+1) + suffix;
    }
    

    public static String termsAddedToString(AlgebraicField field, boolean evaluate) {
//        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        return mathTableToString( field, evaluate,
                (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.plus(n2));
    }
    
    public static String termsSubtractedToString(AlgebraicField field, boolean evaluate) {
//        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        return mathTableToString( field, evaluate,
                (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.minus(n2));
    }
    
    public static String termsMultipliedToString(AlgebraicField field, boolean evaluate) {
//        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        return mathTableToString( field, evaluate,
                (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.times(n2));
    }
    
    public static String termsDividedToString(AlgebraicField field, boolean evaluate) {
//        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        return mathTableToString( field, evaluate,
                (AlgebraicNumber n1, AlgebraicNumber n2) -> n1.dividedBy(n2));
    }
    
    public static String mathTableToString(AlgebraicField field, boolean evaluate,
            BiFunction<AlgebraicNumber, AlgebraicNumber, AlgebraicNumber> op) 
    {
         return mathTableToString(field, evaluate, op, TableFormat.DEFAULT_FORMAT);
    }
    
    public static String mathTableToString(AlgebraicField field, boolean evaluate,
            BiFunction<AlgebraicNumber, AlgebraicNumber, AlgebraicNumber> op, 
            TableFormat tf ) 
    {
        final Function<Integer, AlgebraicNumber> operandFactory = (field instanceof PolygonField) 
                ? ((PolygonField) field)::getUnitDiagonal 
                : field::getUnitTerm;
        final int limit = (field instanceof PolygonField) ? ((PolygonField) field).diagonalCount() : field.getOrder();
        
        StringBuffer buf = new StringBuffer();
        buf.append(tf.tblPrefix);
        for (int i = 0; i < limit; i++) {
            AlgebraicNumber n1 = operandFactory.apply(i);
            buf.append(tf.rowPrefix);
            for (int j = 0; j < limit; j++) {
                AlgebraicNumber n2 = operandFactory.apply(j);
                AlgebraicNumber result = op.apply(n1, n2);
                String s = (evaluate ? result.evaluate() : result).toString();
                buf.append(tf.colPrefix);
                buf.append(tf.formatter.apply(s));
                buf.append(tf.colSuffix);
            }
            buf.append(tf.rowSuffix);
        }
        buf.append(tf.tblSuffix);
        return buf.toString();
    }

    public static String exponentTableToString(AlgebraicField field, int range) 
    {
        return exponentTableToString(field, range, TableFormat.DEFAULT_FORMAT); 
    }
    
    public static String exponentTableToString(AlgebraicField field, int range, TableFormat tf) 
    {
        final int nUnique = 1+field.getNumMultipliers();
        final int order = field.getOrder();
        final int limit = Math.min(nUnique, order);
        range = range * order / nUnique;
        
        StringBuffer buf = new StringBuffer();
        buf.append(tf.tblPrefix);
        for (int irrat = 1; irrat < limit; irrat++) {
            buf.append(tf.rowPrefix);
            for (int power = -range; power <= range; power++) {
                AlgebraicNumber result = field.createPower(power, irrat);
                buf.append(tf.colPrefix);
                buf.append(tf.formatter.apply(result.toString()));
                buf.append(tf.colSuffix);
            }
            buf.append(tf.rowSuffix);
        }
        buf.append(tf.tblSuffix);
        return buf.toString();
    }

    public static class TableFormat {
        public static final String stripBlanks(Object o) {
            return o.toString().replace(" ", "");
        }
        
//        public static final String htmlEncode(Object o) {
//            return escapeHTML(stripBlanks(o));
//        }
//        
//        public static String escapeHTML(String str) {
//            return str.codePoints().mapToObj(c -> c > 127 || "\"'<>&".indexOf(c) != -1 
//                    ? "&#" + c + ";" 
//                    : new String(Character.toChars(c)))
//               .collect(Collectors.joining());
//        }
        
        public static final TableFormat CONSOLE = new TableFormat(
                "{\n",          // tblPrefix
                "  { ",         // rowPrefix
                "",             // colPrefix
                ",\t",          // colSuffix
                "},\n",         // rowSuffix
                "}\n",          // tblSuffix
                TableFormat::stripBlanks);
        
        public static final TableFormat JS = new TableFormat(
                "{\n",          // tblPrefix
                "  { ",         // rowPrefix
                "\"",           // colPrefix
                "\",\t",        // colSuffix
                "},\n",         // rowSuffix
                "}\n",          // tblSuffix
                TableFormat::stripBlanks);
        
//        public static final TableFormat HTML = new TableFormat(
//                "<table class=mathTable>\n",  // tblPrefix
//                "  <tr>",       // rowPrefix
//                "<td>",         // colPrefix
//                "</td>",        // colSuffix
//                "  </tr>\n",    // rowSuffix
//                "</table>\n",   // tblSuffix
//                TableFormat::htmlEncode);
        
        // TODO: public static final TableFormat LATEX = new TableFormat(...);
        
        public static final TableFormat DEFAULT_FORMAT = CONSOLE;

        public final String tblPrefix;
        public final String rowPrefix;
        public final String colPrefix;
        public final String colSuffix;
        public final String rowSuffix;
        public final String tblSuffix;
        public final Function<String, String> formatter;
        
        public TableFormat(String tblPrefix, String rowPrefix, String colPrefix, String colSuffix, String rowSuffix, String tblSuffix, Function<String, String> formatter) {
            this.tblPrefix = tblPrefix;
            this.rowPrefix = rowPrefix;
            this.colPrefix = colPrefix;
            this.colSuffix = colSuffix;
            this.rowSuffix = rowSuffix;
            this.tblSuffix = tblSuffix;
            this.formatter = formatter;
        }
    }
    
	public static String multiplicationTensorToString(ParameterizedField field) {
		StringBuffer buf = new StringBuffer();
		buf.append("{\n");
		for (short[][] outer : field.multiplicationTensor) {
			buf.append("  {\n");
			for (short[] inner : outer) {
				buf.append("    { ");
				for (short i : inner) {
				    if(i < 10) {
				        buf.deleteCharAt(buf.length()-1);
				    }
					buf.append(i).append(", ");
				}
				buf.append("},\n");
			}
			buf.append("  },\n");
		}
		buf.append("}\n");
		return buf.toString();
	}

    public static void printMathTables(AlgebraicField field) {
        String name = "( " + field.toString() + " ) = \n";
//        System.out.println("Add Algebraic Numbers" + name + termsAddedToString(field, false));
//        System.out.println("Add then Evaluate" + name + termsAddedToString(field, true));
//        
//        System.out.println("Subtract Algebraic Numbers" + name + termsSubtractedToString(field, false));
//        System.out.println("Subtract then Evaluate" + name + termsSubtractedToString(field, true));
        
        System.out.println("Multiply Algebraic Numbers" + name + termsMultipliedToString(field, false));
        System.out.println("Multiply then Evaluate" + name + termsMultipliedToString(field, true));
        
        System.out.println("Divide Algebraic Numbers" + name + termsDividedToString(field, false));
        System.out.println("Divide then Evaluate" + name + termsDividedToString(field, true));
        
        System.out.println();
	}

    public static void printExponentTable(AlgebraicField field, int range) {
        String name = "( " + field.toString() + " ) = \n";
        System.out.println("Powers of Algebraic Numbers" + name + exponentTableToString(field, range));
        
        System.out.println();
    }

    public static void printMultiplicationTensor(ParameterizedField field) {
        String name = "( " + field.toString() + " ) = \n";
        System.out.println("Multiplication Tensor" + name + multiplicationTensorToString(field));
    }

}
