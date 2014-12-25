

package org.vorthmann.ui;

import java.io.InputStream;
import java.util.Properties;

public interface ApplicationController extends Controller
{
    Controller loadController( InputStream bytes, Properties props );
    
    Properties getDefaults();
    
    void initialize( Properties properties );
}
