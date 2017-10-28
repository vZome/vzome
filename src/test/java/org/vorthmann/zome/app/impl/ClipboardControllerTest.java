package org.vorthmann.zome.app.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 * @author David Hall
 */
public class ClipboardControllerTest {

    private static final String NULL_TEXT = "<null>";
    @Test
    public void noop() { // just so we have a valid test to run when testTextTransfer() is disabled
        System.out.println("TODO: ClipboardControllerTest.testTextTransfer() may not be enabled.");
    }

//    @Test
    public void testTextTransfer() {
        System.out.println("testTextTransfer");
        ClipboardController clipboard = new ClipboardController();
        
        String content = getClipboardContent(clipboard);
        assertNotNull(content);

        content = null;
        clipboard.setClipboardContents(content);        
        String result = getClipboardContent(clipboard);
        assertEquals(NULL_TEXT, result);

        String delim = "";
        StringBuffer sb = new StringBuffer("");
        for( int i = 0; i <= 16; i++) {
            sb.append(delim).append(i);
            delim = ", ";
            content = sb.toString();
            clipboard.setClipboardContents(content);
            result = getClipboardContent(clipboard);
            assertEquals(content, result);
        }
    }

    private static String getClipboardContent(ClipboardController clipboard) {
        String content = clipboard.getClipboardContents();
        if(content == null) {
            content = NULL_TEXT;
        }
        System.out.println("Clipboard contains:\n" + content + "\n");
        return content;
    }
}
