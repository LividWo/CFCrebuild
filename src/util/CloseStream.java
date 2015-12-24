package util;

import java.io.Reader;

public class CloseStream {
	public static void closeReader(Reader reader){
	    try {
	      if ( reader != null ){
	        reader.close();
	      }
	    } catch ( Exception ignore ){
	    }
	  }
}
