package org.vorthmann.zome.app.impl;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author David Hall
 */
public class ClipboardController implements ClipboardOwner {
    /**
     * Empty implementation of the ClipboardOwner interface.
     */
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //do nothing
    }

    /**
     * Place a String on the clipboard, and make this class the owner of the Clipboard's contents.
     */
    public void setClipboardContents(String string) {
        try {
            StringSelection stringSelection = new StringSelection(string);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }
        catch ( HeadlessException | IllegalStateException ex ) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }

    /**
     * Get the String residing on the clipboard.
     *
     * @return any text found on the Clipboard; if none found, return an empty String.
     */
    public String getClipboardContents() {
        String result = "";
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // The param of getContents is not currently used
            Transferable contents = clipboard.getContents(null);
            if( (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor) ) {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch ( UnsupportedFlavorException | IOException | IllegalStateException ex ) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        return result;
    }
}
