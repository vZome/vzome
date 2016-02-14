
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vorthmann.ui.Controller;

public class PartsPanel extends JPanel
{
    public PartsPanel( Controller controller )
    {
        super( new BorderLayout() );
        JTable bomTable = new JTable( new PartsTableModel( controller ) );
        bomTable .setDefaultRenderer( Color.class, new ColorRenderer(true) );
        bomTable .setRowSelectionAllowed( false );
        bomTable .setCellSelectionEnabled( false );
        bomTable .getTableHeader() .setReorderingAllowed( false );
        TableColumn column = bomTable .getColumnModel() .getColumn( 0 );
        column .setMaxWidth( 50 );
        column = bomTable .getColumnModel() .getColumn( 1 );
        column .setMaxWidth( 40 );
        column = bomTable .getColumnModel() .getColumn( 2 );
        column .setMaxWidth( 50 );
        JScrollPane bomScroller = new JScrollPane( bomTable );
        add( bomScroller );
    }

    private final class PartsTableModel extends AbstractTableModel implements PropertyChangeListener
    {
        private String[] columnNames = { "count", "color", "name", "length" };
        
        private int balls = 1;
        private Map<String, Object[]> struts = new HashMap<>();
        private List<String> index = new ArrayList<>();

        public PartsTableModel( Controller controller )
        {
            controller .addPropertyListener( this );
        }
        
        @Override
        public Class<?> getColumnClass( int c )
        {
            return getValueAt( 0, c ) .getClass();
        }
        
        @Override
        public boolean isCellEditable( int row, int col )
        {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return false;
        }

        @Override
        public String getColumnName( int col )
        {
            return columnNames[ col ];
        }
 
        @Override
        public int getColumnCount()
        {
            return 4;
        }

        @Override
        public int getRowCount()
        {
            return struts .size() + 1;
        }

        @Override
        public Object getValueAt( int row, int col )
        {
            if ( row == 0 )
            {
                switch ( col ) {
                case 0:
                    return balls;
                    
                case 1:
                    return Color .white;
                    
                case 2:
                    return "ball";
                    
                default:
                    return "";
                }
            }
            else
            {
                String key = index .get( row - 1 );
                Object[] rowData = struts .get( key );
                return rowData[ col ];
            }
        }

        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
            String name = evt .getPropertyName();
            if ( name .equals( "addBall" ) )
            {
                ++ balls;
                this .fireTableCellUpdated( 0, 0 );
            }
            else if ( name .equals( "removeBall" ) )
            {
                -- balls;
                this .fireTableCellUpdated( 0, 0 );
            }
            else if ( name .startsWith( "addStrut-" ) )
            {
                name = name .substring( "addStrut-" .length() );
                StringTokenizer tokens = new StringTokenizer( name, ":" );
                String orbitStr = tokens .nextToken();
                String rgbStr = tokens .nextToken();
                String nameStr = tokens .nextToken();
                String lengthStr = tokens .nextToken();
                String key = orbitStr + ":" + nameStr + ":" + lengthStr;
                Object[] row = struts .get( key );
                if ( row == null )
                {
                    int rgb = Integer .parseInt( rgbStr );
                    Color color = new Color( rgb );
                    row = new Object[]{ 1, color, nameStr, lengthStr };
                    struts .put( key, row );
                    index .add( key );
                    Collections .sort( index );
                }
                else
                {
                    int prev = ((Integer) row[ 0 ]) .intValue();
                    row[ 0 ] = new Integer( ++prev );
                }
                this .fireTableDataChanged();
            }
            else if ( name .startsWith( "removeStrut-" ) )
            {
                name = name .substring( "removeStrut-" .length() );
                StringTokenizer tokens = new StringTokenizer( name, ":" );
                String orbitStr = tokens .nextToken();
                String rgbStr = tokens .nextToken();
                String nameStr = tokens .nextToken();
                String lengthStr = tokens .nextToken();
                String key = orbitStr + ":" + nameStr + ":" + lengthStr;
                Object[] row = struts .get( key );
                int count = ((Integer) row[ 0 ]) .intValue() - 1;
                if ( count == 0 )
                {
                    struts .remove( key );
                    index .remove( key );
                    Collections .sort( index );
                }
                else
                {
                    row[ 0 ] = new Integer( count );
                }
                this .fireTableDataChanged();
            }
        }
        
    }


    private final class ColorRenderer extends JLabel implements TableCellRenderer
    {
        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;
     
        public ColorRenderer( boolean isBordered ) {
            this.isBordered = isBordered;
            setOpaque(true); //MUST do this for background to show up.
        }
     
        @Override
        public Component getTableCellRendererComponent(
                                JTable table, Object color,
                                boolean isSelected, boolean hasFocus,
                                int row, int column) {
            Color newColor = (Color)color;
            setBackground(newColor);
            if (isBordered) {
                if (isSelected) {
                    if (selectedBorder == null) {
                        selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                                  table.getSelectionBackground());
                    }
                    setBorder(selectedBorder);
                } else {
                    if (unselectedBorder == null) {
                        unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
                                                  table.getBackground());
                    }
                    setBorder(unselectedBorder);
                }
            }
             
            setToolTipText("RGB value: " + newColor.getRed() + ", "
                                         + newColor.getGreen() + ", "
                                         + newColor.getBlue());
            return this;
        }
    }

}
