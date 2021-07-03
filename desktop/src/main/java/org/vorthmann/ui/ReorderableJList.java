
package org.vorthmann.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class ReorderableJList<E> extends JList<E> implements DragSourceListener,
        DropTargetListener, DragGestureListener
{

    // Initializing it this way just ensures that any copied code uses the correct class name for a static Logger in any class.
    private static final String loggerClassName = new Throwable().getStackTrace()[0].getClassName();
    private static final Logger logger = Logger.getLogger(loggerClassName);
    
    static DataFlavor localObjectFlavor;
    static {
        try {
            localObjectFlavor = new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType );
        } catch ( ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
        }
    }

    static DataFlavor[] supportedFlavors = { localObjectFlavor };

    DragSource dragSource;

    DropTarget dropTarget;

    Object dropTargetCell;

    int draggedIndex = -1;
    
    private final ListMoveListener moves;

    private final Class<E> elementClass;

    public ReorderableJList ( DefaultListModel<E> listModel, ListMoveListener moves, Class<E> listElementClass )
    {
        super();
        this.elementClass = listElementClass;
        this .moves = moves;
        setCellRenderer( new ReorderableListCellRenderer() );
        setModel( listModel );
        dragSource = new DragSource();
        dragSource .createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_MOVE, this );
        dropTarget = new DropTarget( this, this );
    }

    // DragGestureListener
    @Override
    public void dragGestureRecognized( DragGestureEvent dge )
    {
//        System.out.println( "dragGestureRecognized" );
        // find object at this x,y
        Point clickPoint = dge.getDragOrigin();
        int index = locationToIndex( clickPoint );
        if ( index == -1 )
            return;
        Object target = getModel().getElementAt( index );
        Transferable trans = new RJLTransferable( target );
        draggedIndex = index;
        dragSource.startDrag( dge, Cursor.getDefaultCursor(), trans, this );
    }

    // DragSourceListener events
    @Override
    public void dragDropEnd( DragSourceDropEvent dsde )
    {
//        System.out.println( "dragDropEnd()" );
        dropTargetCell = null;
        draggedIndex = -1;
        repaint();
    }

    @Override
    public void dragEnter( DragSourceDragEvent dsde ) {}

    @Override
    public void dragExit( DragSourceEvent dse ) {}

    @Override
    public void dragOver( DragSourceDragEvent dsde ) {}

    @Override
    public void dropActionChanged( DragSourceDragEvent dsde ) {}

    // DropTargetListener events
    @Override
    public void dragEnter( DropTargetDragEvent dtde )
    {
//        System.out.println( "dragEnter" );
        if ( dtde.getSource() != dropTarget )
            dtde.rejectDrag();
        else {
            dtde.acceptDrag( DnDConstants.ACTION_COPY_OR_MOVE );
//            System.out.println( "accepted dragEnter" );
        }
    }

    @Override
    public void dragExit( DropTargetEvent dte ) {}

    @Override
    public void dragOver( DropTargetDragEvent dtde )
    {
        // figure out which cell it's over, no drag to self
        if ( dtde.getSource() != dropTarget )
            dtde.rejectDrag();
        Point dragPoint = dtde.getLocation();
        int index = locationToIndex( dragPoint );
        if ( index == -1 )
            dropTargetCell = null;
        else
            dropTargetCell = getModel().getElementAt( index );
        repaint();
    }

    @Override
    public void drop( DropTargetDropEvent dtde )
    {
//        System.out.println( "drop()!" );
        if ( dtde.getSource() != dropTarget ) {
//            System.out.println( "rejecting for bad source ("
//                    + dtde.getSource().getClass().getName() + ")" );
            dtde.rejectDrop();
            return;
        }
        Point dropPoint = dtde.getLocation();
        int index = locationToIndex( dropPoint );
//        System.out.println( "drop index is " + index );
        boolean dropped = false;
        try {
            if ( (index == -1) || (index == draggedIndex) ) {
//                System.out.println( "dropped onto self" );
                dtde.rejectDrop();
                return;
            }
            dtde.acceptDrop( DnDConstants.ACTION_MOVE );
//            System.out.println( "accepted" );
            Object draggedObj =  dtde.getTransferable().getTransferData( localObjectFlavor );
            Class<?> draggedObjClass = draggedObj.getClass();
            if(!elementClass.isAssignableFrom(draggedObjClass)) {
                String msg = "Unsupported type " + draggedObjClass.getName() + " was dropped. Expected " + elementClass.getName();
                logger.fine(msg);
//                System.out.println( msg );
                dtde.rejectDrop();
                return;
            }
            E dragged = elementClass.cast(draggedObj);
            // move items - note that indicies for insert will
            // change if [removed] source was before target
//            System.out.println( "drop " + draggedIndex + " to " + index );
            boolean sourceBeforeTarget = (draggedIndex < index);
//            System.out.println( "source is" + (sourceBeforeTarget ? "" : " not") + " before target" );
//            System.out.println( "insert at " + (sourceBeforeTarget ? index - 1 : index) );
            DefaultListModel<E> mod = (DefaultListModel<E>) getModel();
            
      // an alternative, from ListDataListener tutorial
//            Object aObject = listModel.getElementAt(a);
//            Object bObject = listModel.getElementAt(b);
//            listModel.set(a, bObject);
//            listModel.set(b, aObject);
            
            moves .startMove();
            mod .remove( draggedIndex );
            mod .add( (sourceBeforeTarget ? index - 1 : index), dragged );
            moves .endMove();
            
            dropped = true;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        dtde.dropComplete( dropped );
    }

    @Override
    public void dropActionChanged( DropTargetDragEvent dtde ) {}

    public interface ListMoveListener
    {
        void startMove();
        
        void endMove();
    }

    class RJLTransferable implements Transferable
    {
        Object object;

        public RJLTransferable( Object o )
        {
            object = o;
        }

        @Override
        public Object getTransferData( DataFlavor df )
                throws UnsupportedFlavorException, IOException
        {
            if ( isDataFlavorSupported( df ) )
                return object;
            else
                throw new UnsupportedFlavorException( df );
        }

        @Override
        public boolean isDataFlavorSupported( DataFlavor df )
        {
            return (df.equals( localObjectFlavor ));
        }

        @Override
        public DataFlavor[] getTransferDataFlavors()
        {
            return supportedFlavors;
        }
    }

    class ReorderableListCellRenderer extends DefaultListCellRenderer
    {
        boolean isTargetCell;

        boolean isLastItem;

        public ReorderableListCellRenderer()
        {
            super();
        }

        @Override
        public Component getListCellRendererComponent( JList<?> list,
                Object value, int index, boolean isSelected, boolean hasFocus )
        {
            isTargetCell = (value == dropTargetCell);
            isLastItem = (index == list .getModel() .getSize() - 1);
            boolean showSelected = isSelected & (dropTargetCell == null);
            return super.getListCellRendererComponent( list, value, index, showSelected, hasFocus );
        }

        @Override
        public void paintComponent( Graphics g )
        {
            super.paintComponent( g );
            if ( isTargetCell ) {
                g .setColor( Color.red );
                g .drawRect( 0, 0, getSize().width, 1 );
            }
        }
    }
}
