
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.vorthmann.ui.Controller;
import org.vorthmann.zome.app.impl.PartsController.PartInfo;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class PartsPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private final Controller controller;
    private final JTable bomTable;
    private final PartsTableModel partsTableModel;
    private Point popupTriggerLocation = null;

    public PartsPanel( Controller controller )
    {
        super( new BorderLayout() );
        this.controller = controller;
        partsTableModel = new PartsTableModel();
        controller .addPropertyListener( partsTableModel );
        bomTable = new JTable( partsTableModel ){
            @Override
            public Point getPopupLocation(MouseEvent event) {
                popupTriggerLocation = event == null ? null : event.getPoint();
                return super.getPopupLocation(event);
            }
        };
        bomTable .setDefaultRenderer( Color.class, new ColorRenderer(true) );
        bomTable .setRowSelectionAllowed( false );
        bomTable .setCellSelectionEnabled( false );
        bomTable .getTableHeader() .setReorderingAllowed( false );
        bomTable .getTableHeader() .setBackground( Color.lightGray );
        TableColumn column = bomTable .getColumnModel() .getColumn( 0 );
        column .setMaxWidth( 50 );
        column = bomTable .getColumnModel() .getColumn( 1 );
        column .setMaxWidth( 40 );
        column = bomTable .getColumnModel() .getColumn( 2 );
        column .setMaxWidth( 50 );
        JScrollPane bomScroller = new JScrollPane( bomTable );

        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(newMenuItem("Select"));
        popupMenu.add(newMenuItem("Deselect"));
        bomTable.setComponentPopupMenu(popupMenu);

        super.add( bomScroller );
    }

    private JMenuItem newMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleContextMenuEvent(e);
            }
        });
        return menuItem;
    }

    private void handleContextMenuEvent( ActionEvent e )
    {
        int rowAtPoint = bomTable .rowAtPoint( popupTriggerLocation );
        PartsTableRow row = partsTableModel .getRow( rowAtPoint );
        PartInfo partInfo = row .partInfo;
        String cmd = e .getActionCommand() .toLowerCase();
        switch( row .partClassGroupingOrder )
        {
        case BALLS_TOTAL:
            controller .actionPerformed( this, "AdjustSelectionByClass/" + cmd + "Balls" );
            break;

        case STRUTS_TOTAL:
            controller .actionPerformed( this, "AdjustSelectionByClass/" + cmd + "Struts" );
            break;

        case PANELS_TOTAL:
            controller .actionPerformed( this, "AdjustSelectionByClass/" + cmd + "Panels" );
            break;

        case STRUTS:
        {
            String tail = "/" + partInfo.orbitStr;
            tail += "/" + partInfo.strutLength.toString( AlgebraicField .ZOMIC_FORMAT );
            controller .actionPerformed( this, "AdjustSelectionByOrbitLength/" + cmd + "SimilarStruts" + tail );
        }
        break;

        case PANELS:
        {
            controller .actionPerformed( this, "AdjustSelectionByOrbitLength/" + cmd + "SimilarPanels" + "/" + partInfo .orbitStr );
        }
        break;

        default:
            break;
        }
    }

    /**
     * PartsTableRow allows the PartInfo instances that are received in the PropertyChangeEvent
     * to be managed as rows and columns in the PartsTableModel.
     * It also supports the aggregate (total) columns that are generated and used locally.
     */
    public static final class PartsTableRow implements Comparable<PartsTableRow>
    {
        private static final int COUNT_COLUMN = 0;
        public final PartInfo partInfo;
        public final PartGroupingOrderEnum partClassGroupingOrder;
        public final Color color;
        public final Boolean isAutomaticDirection;
        public final String key;
        private Integer count = 0;

        /**
         * Used internally for generating the initial aggregate rows
         * @param name Name to be displayed on this aggregate row (e.g. "balls", "struts" or "panels").
         * @param partType Class to be counted by this row.
         * @param initialCount Initial count to be displayed.
         */
        private PartsTableRow(String name, Class<? extends Manifestation> partType, int initialCount) {
            this( new PartInfo(name, partType), true, initialCount );
        }

        /**
         * Constructed from the PartInfo from the received in the PropertyChangeEvent
         * @param partData PartInfo that's received in the PropertyChangeEvent
         */
        private PartsTableRow(PartInfo partData) {
            this( partData, false, 1 );
        }

        /**
         * Common c'tor called by the other special purpose c'tors.
         * @param partData
         * @param isAggregate
         * @param initialCount
         */
        private PartsTableRow(PartInfo partData, boolean isAggregate, int initialCount ) {
            partInfo = partData;
            partClassGroupingOrder = getPartGroupingOrder(partInfo.partClass, isAggregate);
            color = new Color(partInfo.rgbColor);
            isAutomaticDirection = (partInfo.automaticDirectionIndex >= 0);
            key = calculateKey(partInfo);
            count = initialCount;
        }

        /**
         * Allows specific internal fields to be addresses as columns
         * Note the special case of {@code col} = -1 which returns this.
         * The JPanel doesn't ever use columns less than 0,
         * but it can be used within this class to allow an individual cell
         * to access its underlying PartInfo as in the case of the ColorRenderer
         * @param col Column number (zero based)
         * @return
         */
        public Object getValueAt(int col) {
            switch (col) {
                case -1:
                    return this; // used internally for the color column tool tip text

                case COUNT_COLUMN:
                    return count;

                case 1:
                    return color;

                case 2:
                    return partInfo.sizeNameStr;

                case 3:
                    return partInfo.lengthStr;
            }
            throw new IllegalArgumentException("unexpected column number: " + Integer.toString(col));
        }

        /**
         * Sort numerically by realLength and automaticDirectionIndex
         * instead of alphabetically by their String counterparts
         */
        @Override
        public int compareTo(PartsTableRow that) {
            int comparison = this.partClassGroupingOrder.compareTo(that.partClassGroupingOrder);
            if (comparison != 0)
                return comparison;
            comparison = this.isAutomaticDirection.compareTo(that.isAutomaticDirection);
            if (comparison != 0)
                return comparison;
            comparison = this.partInfo.automaticDirectionIndex.compareTo(that.partInfo.automaticDirectionIndex);
            if (comparison != 0)
                return comparison;
            comparison = this.partInfo.orbitStr.compareTo(that.partInfo.orbitStr);
            if (comparison != 0)
                return comparison;
            comparison = this.partInfo.realLength.compareTo(that.partInfo.realLength);
            if (comparison != 0)
                return comparison;
            comparison = this.partInfo.sizeNameStr.compareTo(that.partInfo.sizeNameStr);
            return comparison;
        }

        /**
         * used only by the c'tors
         * @param partInfo
         * @return
         */
        private static String calculateKey(PartInfo partInfo) {
            return partInfo.orbitStr + ":" + partInfo.sizeNameStr + ":" + partInfo.lengthStr;
        }

        /**
         * used only by the c'tors
         * @param partClass
         * @param isAggregate
         * @return
         */
        private static PartGroupingOrderEnum getPartGroupingOrder(Class<? extends Manifestation> partClass, boolean isAggregate ) {
            if(partClass.equals(Connector.class) && isAggregate ) {
                return PartGroupingOrderEnum.BALLS_TOTAL;
            } 
            else if(partClass.equals(Strut.class)) {
                return isAggregate 
                        ? PartGroupingOrderEnum.STRUTS_TOTAL
                        : PartGroupingOrderEnum.STRUTS;
            } 
            else if(partClass.equals(Panel.class)) {
                return isAggregate 
                        ? PartGroupingOrderEnum.PANELS_TOTAL
                        : PartGroupingOrderEnum.PANELS;
            }
            return PartGroupingOrderEnum.TEMP;
        }
    }

    /**
     * The order listed here is the order the rows will be grouped and sorted for display
     */
    public enum PartGroupingOrderEnum {
        BALLS_TOTAL,
        STRUTS_TOTAL,
        STRUTS,
        PANELS_TOTAL,
        PANELS,
        TEMP; // nothing displayed in this case
    }


    private final class PartsTableModel extends AbstractTableModel implements PropertyChangeListener
    {
        private static final long serialVersionUID = 1L;
        private final String[] columnNames = { "count", "orbit", "size", "length (orbit units)" };
        private final TreeSet<PartsTableRow> tableRows = new TreeSet<>(); // self-sorting since PartsTableRow implements Comparable
        private final PartsTableRow ballsTotalRow = new PartsTableRow("balls", Connector.class, 1 );
        private final PartsTableRow strutsTotalRow = new PartsTableRow("struts", Strut.class, 0 );
        private final PartsTableRow panelsTotalRow = new PartsTableRow("panels", Panel.class, 0 );

        PartsTableModel() {
            tableRows.add(ballsTotalRow);
        }
        
        @Override
        public Class<?> getColumnClass( int col )
        {
            return ballsTotalRow.getValueAt( col ).getClass();
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
            return tableRows.size();
        }

        @Override
        public Object getValueAt( int row, int col )
        {
            PartsTableRow rowData = getRow( row );
            return rowData == null ? null : rowData.getValueAt( col );
        }

        public PartsTableRow getRow( int row ) {
            return tableRows.stream().skip(row).findFirst().orElse(null);
        }

        public PartsTableRow getRow( String key ) {
            return tableRows.stream().filter(r -> r.key.equals(key)).findFirst().orElse(null);
        }

        public int getRowNumber( PartsTableRow row ) {
            return tableRows.contains(row) 
                    ? tableRows. headSet(row).size() // the number of elements that are less than row
                    : -1;
        }

        @Override
        public void propertyChange( PropertyChangeEvent event )
        {
            String propertyName = event .getPropertyName();
            if( propertyName.equals("addBall") ||
                propertyName.equals("removeBall") ||
                propertyName.equals("addStrut") ||
                propertyName.equals("removeStrut") ||
                propertyName.equals("addPanel") ||
                propertyName.equals("removePanel") )
            {
                ManifestationChangeHandler handler = new ManifestationChangeHandler(event);
                if (SwingUtilities.isEventDispatchThread()) {
                    handler.run();
                } else {
                    SwingUtilities.invokeLater(handler);
                }
            }
        }

        private class ManifestationChangeHandler implements Runnable {
            private final String propertyName;
            private final PartsTableRow oldRow;
            private final PartsTableRow newRow;
            public ManifestationChangeHandler(PropertyChangeEvent event) {
                // Don't maintain a reference to the actual event.
                // Just copy the data to be used later in another thread.
                propertyName = event.getPropertyName();
                PartInfo oldPart = (PartInfo) event.getOldValue();
                PartInfo newPart = (PartInfo) event.getNewValue();
                oldRow = oldPart == null ? null : new PartsTableRow(oldPart);
                newRow = newPart == null ? null : new PartsTableRow(newPart);
            }

            @Override
            public void run() {
                switch(propertyName) {
                    case "addBall":
                        incrementAggregateRow(ballsTotalRow, false);
                        break;

                    case "removeBall":
                        decrementAggregateRow(ballsTotalRow, false);
                        break;

                    case "addStrut":
                        incrementAggregateRow(strutsTotalRow, true);
                        updateOrInsertNewRow();
                        break;

                    case "removeStrut":
                        decrementAggregateRow(strutsTotalRow, true);
                        updateOrRemoveOldRow();
                        break;
                    
                    case "addPanel":
                        incrementAggregateRow(panelsTotalRow, true);
                        updateOrInsertNewRow();
                        break;

                    case "removePanel":
                        decrementAggregateRow(panelsTotalRow, true);
                        updateOrRemoveOldRow();
                        break;
                }
            }

            private void incrementAggregateRow(PartsTableRow aggregateRow, boolean addInitial ) {
                if (addInitial) {
                    updateOrInsertRow(aggregateRow);
                } else {
                    aggregateRow.count++;
                    int row = getRowNumber(aggregateRow);
                    fireTableCellUpdated(row, PartsTableRow.COUNT_COLUMN);
                }
            }

            private void decrementAggregateRow(PartsTableRow tableRow, boolean removeEmpty ) {
                if (removeEmpty) {
                    updateOrDeleteRow(tableRow);
                } else {
                    tableRow.count--;
                    int row = getRowNumber(tableRow);
                    fireTableCellUpdated(row, PartsTableRow.COUNT_COLUMN);
                }
            }

            private void updateOrInsertNewRow() {
                updateOrInsertRow(newRow);
            }

            private void updateOrRemoveOldRow() {
                updateOrDeleteRow(oldRow);
            }

            private void updateOrInsertRow(PartsTableRow insertRow) {
                PartsTableRow existingRow = getRow(insertRow.key);
                if (existingRow == null) {
                    insertRow.count = 1;
                    tableRows.add(insertRow);
                    // determine rowNumber AFTER adding insertedRow
                    int rowNumber = getRowNumber(insertRow);
                    fireTableRowsInserted(rowNumber, rowNumber);
                } else {
                    existingRow.count++;
                    int row = getRowNumber(existingRow);
                    fireTableCellUpdated(row, PartsTableRow.COUNT_COLUMN);
                }
            }

            private void updateOrDeleteRow(PartsTableRow deleteRow) {
                PartsTableRow existingRow = getRow(deleteRow.key);
                existingRow.count--;     // existingRow should never be null
                if (existingRow.count == 0) {
                    // determine rowNumber BEFORE removing deleteRow
                    int rowNumber = getRowNumber(existingRow);
                    tableRows.remove(existingRow);
                    fireTableRowsDeleted(rowNumber, rowNumber);
                } else {
                    int row = getRowNumber(existingRow);
                    fireTableCellUpdated(row, PartsTableRow.COUNT_COLUMN);
                }
            }
        }
    }

    private final class ColorRenderer extends JLabel implements TableCellRenderer
    {
        private static final long serialVersionUID = 1L;
        Border unselectedBorder = null;
        Border selectedBorder = null;
        boolean isBordered = true;
     
        public ColorRenderer( boolean isBordered ) {
            this.isBordered = isBordered;
            super.setOpaque(true); // MUST do this for background to show up.
        }
     
        @Override
        public Component getTableCellRendererComponent(
                                JTable table, Object color,
                                boolean isSelected, boolean hasFocus,
                                int row, int column)
        {
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

            PartsTableRow rowData = (PartsTableRow) table.getValueAt(row, -1);
            if( rowData.partClassGroupingOrder == PartGroupingOrderEnum.PANELS ||
                    rowData.partClassGroupingOrder == PartGroupingOrderEnum.STRUTS )
            {
                if(rowData.isAutomaticDirection) {
                    setToolTipText("auto " + rowData.partInfo.orbitStr);
                }
                else {
                    setToolTipText(rowData.partInfo.orbitStr + " RGB: "
                            + newColor.getRed() + ", "
                            + newColor.getGreen() + ", "
                            + newColor.getBlue() );
                }
            }
            else {
                setToolTipText(null);
            }
            return this;
        }
    }

}
