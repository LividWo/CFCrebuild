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

import util.IOUtil;


public class Analyzers {
	public static void main(String[] args){
		
		 String text = "earn   united presidential corp <upco> 4th qtr net shr 39 cts vs 50 cts net 1,545,160 vs 2,188,933 revs "
		 		+ "25.2 mln vs 19.5 mln year shr 1.53 dlrs vs 1.21 dlrs net 6,635,318 vs 5,050,044 revs 92.2 mln vs 77.4 mln note: "
		 		+ "results include adjustment of 848,600 dlrs or 20 cts shr for 1986 year and both 1985 periods from improvement "
		 		+ "in results of its universal life business than first estimated. reuter";  
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
            
            ts.reset();  
            while (ts.incrementToken()) {  
//                System.out.println(ch.toString());  
                sb.append(ch.toString());
                sb.append(" ");
            }  
            ts.end();  
            ts.close();  
            sia.close();
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
//			int i=1;
			while((line=in.readLine())!=null){
				cas.add(line);
//				System.out.println(i+++"  "+line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtil.closeReader(in);
		}
		
		
		return cas;
	}
	
	
}
