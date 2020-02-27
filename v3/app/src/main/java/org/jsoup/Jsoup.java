package org.jsoup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.jsoup.helper.*;


public class Jsoup {
    private Jsoup() {}

    
    
    public static Connection connect(String url) {
        return HttpConnection.connect(url);
    }

    
        
    
}
