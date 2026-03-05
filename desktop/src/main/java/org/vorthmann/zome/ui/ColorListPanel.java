package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A custom color chooser panel that displays a scrollable list of saved colors.
 * Each row shows a 30x30 swatch, an editable name, and a delete button.
 * The current chooser color is displayed at the top with a button to add it to the list.
 * Clicking a row sets the chooser color.
 */
public class ColorListPanel extends AbstractColorChooserPanel
{
    private static final int SWATCH_SIZE = 30;
    private static final String PREFS_NODE = "org/vorthmann/zome/colorList";
    private static final String PREF_COUNT = "colorCount";
    private static final String PREF_COLOR = "color_";
    private static final String PREF_NAME = "name_";

    /** When true, the panel shows a fixed set of Zometool colors and is not editable. */
    private final boolean fixedZometool;

    private final List<SavedColor> savedColors = new ArrayList<>();
    private ColorTableModel tableModel;
    private JTable table;
    private JLabel currentSwatchLabel;
    private JLabel currentHexLabel;

    /** Official Zometool colors from defaultPrefs.properties. */
    private static final Object[][] ZOMETOOL_COLORS = {
        { "Blue",      new Color( 0, 142, 194 ) },
        { "Yellow",    new Color( 255, 179, 26 ) },
        { "Red",       new Color( 217, 18, 24 ) },
        { "Green",     new Color( 0, 153, 63 ) },
        { "Turquoise", new Color( 0, 179, 161 ) },
        { "White",     new Color( 242, 242, 242 ) },
        { "Black",     new Color( 50, 50, 50 ) },
        { "Orange",    new Color( 235, 82, 0 ) },
        { "Purple",    new Color( 125, 54, 211 ) },
        { "Gray",      new Color( 133, 133, 133 ) },
        // { "Olive",     new Color( 100, 113, 0 ) },
        // { "Lavender",  new Color( 175, 135, 255 ) },
        // { "Maroon",    new Color( 117, 0, 50 ) },
        // { "Rose",      new Color( 255, 51, 143 ) },
        // { "Navy",      new Color( 0, 0, 153 ) },
        // { "Brown",     new Color( 107, 53, 26 ) },
        // { "Apple",     new Color( 116, 195, 0 ) },
        // { "Sand",      new Color( 154, 117, 74 ) },
        // { "Coral",     new Color( 255, 126, 106 ) },
        // { "Sulfur",    new Color( 239, 245, 61 ) },
        // { "Cinnamon",  new Color( 136, 37, 0 ) },
        // { "Spruce",    new Color( 18, 73, 48 ) },
        // { "Magenta",   new Color( 255, 41, 183 ) },
    };

    /** Creates the editable "Saved Colors" panel. */
    public ColorListPanel()
    {
        this( false );
    }

    /**
     * @param fixedZometool  when true, shows the fixed Zometool color palette (read-only)
     */
    public ColorListPanel( boolean fixedZometool )
    {
        this.fixedZometool = fixedZometool;
    }

    private static class SavedColor
    {
        Color color;
        String name;

        SavedColor( Color color, String name )
        {
            this.color = color;
            this.name = name;
        }
    }

    // ===== AbstractColorChooserPanel overrides =====

    @Override
    public String getDisplayName()
    {
        return fixedZometool ? "Zometool Colors" : "Saved Colors";
    }

    @Override
    public Icon getSmallDisplayIcon()
    {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon()
    {
        return null;
    }

    @Override
    protected void buildChooser()
    {
        setLayout( new BorderLayout( 8, 0 ) );

        if ( !fixedZometool )
        {
            // --- Left panel: current color preview + add button ---
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout( new BoxLayout( leftPanel, BoxLayout.Y_AXIS ) );
            leftPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 4 ) );

            JLabel label = new JLabel( "Current:" );
            label.setAlignmentX( Component.CENTER_ALIGNMENT );
            leftPanel.add( label );

            leftPanel.add( Box.createVerticalStrut( 4 ) );

            currentSwatchLabel = new JLabel();
            currentSwatchLabel.setOpaque( true );
            currentSwatchLabel.setPreferredSize( new Dimension( SWATCH_SIZE, SWATCH_SIZE ) );
            currentSwatchLabel.setMaximumSize( new Dimension( SWATCH_SIZE, SWATCH_SIZE ) );
            currentSwatchLabel.setMinimumSize( new Dimension( SWATCH_SIZE, SWATCH_SIZE ) );
            currentSwatchLabel.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
            currentSwatchLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
            updateCurrentSwatch();
            leftPanel.add( currentSwatchLabel );

            leftPanel.add( Box.createVerticalStrut( 4 ) );

            currentHexLabel = new JLabel( hexString( getColorFromModel() ) );
            currentHexLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
            leftPanel.add( currentHexLabel );

            leftPanel.add( Box.createVerticalStrut( 8 ) );

            JButton addButton = new JButton( "Add to List" );
            addButton.setAlignmentX( Component.CENTER_ALIGNMENT );
            addButton.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    Color c = getColorFromModel();
                    savedColors.add( new SavedColor( c, "" ) );
                    int row = savedColors.size() - 1;
                    tableModel.fireTableRowsInserted( row, row );
                    table.scrollRectToVisible( table.getCellRect( row, 0, true ) );
                    persistColors();
                }
            } );
            leftPanel.add( addButton );

            leftPanel.add( Box.createVerticalGlue() );

            add( leftPanel, BorderLayout.WEST );
        }

        // --- Table of saved colors ---
        tableModel = new ColorTableModel();
        table = new JTable( tableModel );
        table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        table.setRowHeight( SWATCH_SIZE + 4 );
        table.setShowGrid( false );
        table.setIntercellSpacing( new Dimension( 0, 2 ) );

        // Column 0: swatch (fixed width)
        table.getColumnModel().getColumn( 0 ).setPreferredWidth( SWATCH_SIZE + 8 );
        table.getColumnModel().getColumn( 0 ).setMaxWidth( SWATCH_SIZE + 8 );
        table.getColumnModel().getColumn( 0 ).setCellRenderer( new SwatchRenderer() );

        // Column 1: name (stretches; editable only when not fixed)
        table.getColumnModel().getColumn( 1 ).setPreferredWidth( 200 );
        table.getColumnModel().getColumn( 1 ).setCellRenderer( new NameRenderer() );
        if ( !fixedZometool ) {
            table.getColumnModel().getColumn( 1 ).setCellEditor( new NameEditor() );
        }

        if ( !fixedZometool )
        {
            // Column 2: delete button (fixed width) — only for editable list
            table.getColumnModel().getColumn( 2 ).setPreferredWidth( 60 );
            table.getColumnModel().getColumn( 2 ).setMaxWidth( 70 );
            table.getColumnModel().getColumn( 2 ).setCellRenderer( new DeleteButtonRenderer() );
            table.getColumnModel().getColumn( 2 ).setCellEditor( new DeleteButtonEditor() );
        }

        // Click on swatch or name sets the chooser color
        table.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                int row = table.rowAtPoint( e.getPoint() );
                int col = table.columnAtPoint( e.getPoint() );
                if ( row >= 0 && ( fixedZometool || col != 2 ) )
                {
                    getColorSelectionModel().setSelectedColor( savedColors.get( row ).color );
                }
            }
        } );

        // The table must not track the viewport height, so the scroll pane
        // actually scrolls instead of growing to fit all rows.
        table.setFillsViewportHeight( true );
        // Set the table's preferred viewport size for a reasonable initial size;
        // the scroll pane itself will stretch to fill available space via BorderLayout.CENTER.
        table.setPreferredScrollableViewportSize( new Dimension( 320, 200 ) );

        JScrollPane scrollPane = new JScrollPane( table );
        scrollPane.setBorder( BorderFactory.createEmptyBorder( 8, fixedZometool ? 8 : 4, 8, 8 ) );
        add( scrollPane, BorderLayout.CENTER );

        // Populate the list
        if ( fixedZometool ) {
            loadZometoolColors();
        } else {
            loadColors();
        }
    }

    @Override
    public void updateChooser()
    {
        updateCurrentSwatch();
    }

    private void updateCurrentSwatch()
    {
        Color c = getColorFromModel();
        if ( currentSwatchLabel != null ) {
            currentSwatchLabel.setBackground( c );
        }
        if ( currentHexLabel != null ) {
            currentHexLabel.setText( hexString( c ) );
        }
    }

    private static String hexString( Color c )
    {
        if ( c == null )
            return "";
        return String.format( "#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue() );
    }

    // ===== Persistence via java.util.prefs =====

    private void persistColors()
    {
        try {
            Preferences prefs = Preferences.userRoot().node( PREFS_NODE );
            prefs.putInt( PREF_COUNT, savedColors.size() );
            for ( int i = 0; i < savedColors.size(); i++ ) {
                SavedColor sc = savedColors.get( i );
                prefs.putInt( PREF_COLOR + i, sc.color.getRGB() );
                prefs.put( PREF_NAME + i, sc.name != null ? sc.name : "" );
            }
            prefs.flush();
        } catch ( Exception e ) {
            // silently ignore persistence failures
        }
    }

    private void loadColors()
    {
        try {
            Preferences prefs = Preferences.userRoot().node( PREFS_NODE );
            int count = prefs.getInt( PREF_COUNT, 0 );
            for ( int i = 0; i < count; i++ ) {
                int rgb = prefs.getInt( PREF_COLOR + i, 0 );
                String name = prefs.get( PREF_NAME + i, "" );
                savedColors.add( new SavedColor( new Color( rgb, true ), name ) );
            }
            if ( count > 0 ) {
                tableModel.fireTableDataChanged();
            }
        } catch ( Exception e ) {
            // silently ignore
        }
    }

    private void loadZometoolColors()
    {
        for ( Object[] entry : ZOMETOOL_COLORS ) {
            savedColors.add( new SavedColor( (Color) entry[ 1 ], (String) entry[ 0 ] ) );
        }
        tableModel.fireTableDataChanged();
    }

    // ===== Table Model =====

    private class ColorTableModel extends AbstractTableModel
    {
        private final String[] COLUMNS = fixedZometool
            ? new String[] { "Color", "Name" }
            : new String[] { "Color", "Name", "" };

        @Override
        public int getRowCount()
        {
            return savedColors.size();
        }

        @Override
        public int getColumnCount()
        {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName( int column )
        {
            return COLUMNS[ column ];
        }

        @Override
        public Object getValueAt( int row, int col )
        {
            SavedColor sc = savedColors.get( row );
            switch ( col ) {
                case 0: return sc.color;
                case 1: return sc.name;
                case 2: return "Delete";
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable( int row, int col )
        {
            if ( fixedZometool )
                return false;
            return col == 1 || col == 2; // name and delete are editable/clickable
        }

        @Override
        public void setValueAt( Object value, int row, int col )
        {
            if ( col == 1 ) {
                savedColors.get( row ).name = (String) value;
                persistColors();
            }
        }
    }

    // ===== Cell Renderers and Editors =====

    /** Renders a color swatch in the table cell. */
    private static class SwatchRenderer extends JLabel implements TableCellRenderer
    {
        SwatchRenderer()
        {
            setOpaque( true );
            setHorizontalAlignment( CENTER );
            setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 4 ) );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column )
        {
            Color c = (Color) value;
            setIcon( new SwatchIcon( c, SWATCH_SIZE, SWATCH_SIZE ) );
            setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
            return this;
        }
    }

    /** A simple square icon filled with a color. */
    private static class SwatchIcon implements Icon
    {
        private final Color color;
        private final int width;
        private final int height;

        SwatchIcon( Color color, int width, int height )
        {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public void paintIcon( Component c, Graphics g, int x, int y )
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            // checkerboard background for alpha
            g2.setColor( Color.WHITE );
            g2.fillRect( x, y, width, height );
            g2.setColor( Color.LIGHT_GRAY );
            int half = width / 2;
            g2.fillRect( x + half, y, width - half, half );
            g2.fillRect( x, y + half, half, height - half );
            // actual color on top
            g2.setColor( color );
            g2.fillRect( x, y, width, height );
            g2.setColor( Color.GRAY );
            g2.drawRect( x, y, width - 1, height - 1 );
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }
    }

    /** Renders the editable name text. */
    private static class NameRenderer extends JLabel implements TableCellRenderer
    {
        NameRenderer()
        {
            setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 4 ) );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column )
        {
            String name = (String) value;
            setText( name == null || name.isEmpty() ? "(click to name)" : name );
            setForeground( name == null || name.isEmpty() ? Color.GRAY : table.getForeground() );
            setBackground( isSelected ? table.getSelectionBackground() : table.getBackground() );
            setOpaque( true );
            return this;
        }
    }

    /** Editor for the name column—a simple text field. */
    private class NameEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final JTextField field = new JTextField();

        NameEditor()
        {
            field.setBorder( BorderFactory.createEmptyBorder( 2, 4, 2, 4 ) );
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value,
                boolean isSelected, int row, int column )
        {
            field.setText( value != null ? (String) value : "" );
            return field;
        }

        @Override
        public Object getCellEditorValue()
        {
            return field.getText();
        }
    }

    /** Renders a "Delete" button in the cell. */
    private static class DeleteButtonRenderer extends JButton implements TableCellRenderer
    {
        DeleteButtonRenderer()
        {
            setOpaque( true );
        }

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column )
        {
            setText( "\u2715" ); // ✕ character
            setToolTipText( "Remove this color" );
            return this;
        }
    }

    /** Editor for the delete column—clicking it removes the row. */
    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor
    {
        private final JButton button = new JButton( "\u2715" );
        private int editingRow;

        DeleteButtonEditor()
        {
            button.setToolTipText( "Remove this color" );
            button.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent e )
                {
                    fireEditingStopped();
                    if ( editingRow >= 0 && editingRow < savedColors.size() ) {
                        savedColors.remove( editingRow );
                        tableModel.fireTableRowsDeleted( editingRow, editingRow );
                        persistColors();
                    }
                }
            } );
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value,
                boolean isSelected, int row, int column )
        {
            editingRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue()
        {
            return "\u2715";
        }
    }
}
