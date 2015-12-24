package preprocess;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;




import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;

import util.CloseStream;


public class Analyzers {
	public static void main(String[] args){
		
		 String text = "The Lucene PMC's is pleased've to announce the release of the Apache Solr Reference Guide for Solr 4.4.";  
		 String result = analyzer(text);
		 System.out.println(result);
	}
	
	public static String analyzer(String text){
		StringBuilder sb= new StringBuilder();
		try {  
            CharArraySet cas = new CharArraySet(0, true);  
            cas	= openStopWordList("E://java//dataset//stopwords.txt");
            SimpleAnalyzer sia = new SimpleAnalyzer();
            TokenStream ts = sia.tokenStream("field", new StringReader(text));
            ts = new StopFilter(ts, new CharArraySet(cas, false));
            CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);  
            sia.close();
            
            ts.reset();  
            while (ts.incrementToken()) {  
//                System.out.println(ch.toString());  
                sb.append(ch.toString());
                sb.append(" ");
            }  
            ts.end();  
            ts.close();  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }
		return sb.toString();
	}
	
	public static CharArraySet openStopWordList(String filename){
		CharArraySet cas = new CharArraySet(0, true);
		File file = new File(filename);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while((line=in.readLine())!=null){
				cas.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			CloseStream.closeReader(in);
		}
		
		
		return cas;
	}
	
	
}
