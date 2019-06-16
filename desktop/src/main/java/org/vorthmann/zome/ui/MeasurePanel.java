
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.vorthmann.ui.Controller;

public class MeasurePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final Controller controller;
    private final JTable measureTable;
    private final MeasureTableModel measureTableModel;

    public MeasurePanel( Controller controller )
    {
        super( new BorderLayout() );
        this.controller = controller;
        measureTableModel = new MeasureTableModel();
        controller .addPropertyListener( measureTableModel );
        measureTable = new JTable( measureTableModel );
        measureTable .setRowSelectionAllowed( false );
        measureTable .setCellSelectionEnabled( false );
        measureTable .getTableHeader() .setReorderingAllowed( false );
        measureTable .getTableHeader() .setBackground( Color.lightGray );
        TableColumn column = measureTable .getColumnModel() .getColumn( 0 );
        column .setPreferredWidth( 150 );
        JScrollPane scroller = new JScrollPane( measureTable );

        super.add( scroller );
    }

    private final class MeasureTableModel extends AbstractTableModel implements PropertyChangeListener
    {
        private static final long serialVersionUID = 1L;
        private final String[] columnNames = { "measure", "value" };
        private String[] measures, values;

        MeasureTableModel()
        {
        	this .measures = new String[] {};
        	this .values = new String[] {};
        }
        
        @Override
        public Class<?> getColumnClass( int col )
        {
            return String.class;
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
            return columnNames.length;
        }

        @Override
        public int getRowCount()
        {
            return measures.length;
        }

        @Override
        public Object getValueAt( int row, int col )
        {
        	if ( col == 0 )
        		return this .measures[ row ];
        	else
        		return this .values[ row ];
        }

        @Override
        public void propertyChange( PropertyChangeEvent event )
        {
            String propertyName = event .getPropertyName();
            if( propertyName.equals("measures") )
            {
                MeasuresChangeHandler handler = new MeasuresChangeHandler(event);
                if (SwingUtilities.isEventDispatchThread()) {
                    handler.run();
                } else {
                    SwingUtilities.invokeLater(handler);
                }
            }
        }

        private class MeasuresChangeHandler implements Runnable
        {
            public MeasuresChangeHandler( PropertyChangeEvent event ) {}

            @Override
            public void run()
            {
            	measures = controller .getCommandList( "measures" );
            	values = new String[ measures.length ];
            	for ( int i = 0; i < measures.length; i++ ) {
					String measure = measures[i];
					values[ i ] = controller .getProperty( measure );
				}
            	fireTableDataChanged();
            }
        }
    }
}
