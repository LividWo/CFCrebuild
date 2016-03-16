package training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import training.PaperAbs;
import util.IOUtil;

/**
 * @author Chenliang Li [cllee@whu.edu.cn]
 *
 */
public class PaperAbsIndexWriter {
  
  public static final String TITLE = "title";
  public static final String ABSTRACT = "abs";
  public static final String ID = "id";
  
  private static final String SUFFIX = ".txt";
  
  /**
   * Create a new index under the specified indexPath for 
   * the abstracts from the specified directory absPath.
   * Note: the old index files with the same name will be removed.
   * @param absPath
   * @param indexPath
   */
  public static void startIndex(String absPath, String indexPath){
      startIndex(absPath, indexPath, true);
  }
  
  public static void startIndex(
          String docPath, String indexPath, boolean createNew){
    try {
      List<String[]> paperList = getTitleContPairOnOneTXT(docPath);
      Directory dir = FSDirectory.open(Paths.get(indexPath));
      Analyzer analyzer = 
          new StandardAnalyzer();
//          new PorterStemAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      
      if ( createNew ){
        // Create a new index in the directory, removing any 
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }
      
      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);
      
      IndexWriter writer = new IndexWriter(dir, iwc);
      
      // make a new, empty document 
      Document doc = new Document();
      Field titleField = new StoredField(TITLE, "");
      doc.add(titleField);
      
      // indexed, not tokenized, and stored.
      Field idField = new StringField(ID, "", Store.YES);
      doc.add(idField);
      
      FieldType fieldTypeForAbs = new FieldType();
      fieldTypeForAbs.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
      fieldTypeForAbs.setTokenized(true); // we need to tokenize the abstract
      fieldTypeForAbs.setStored(false); // we do not need to store the whole abstract
      
      // Yes, we always need term vectors for our research work. 
      fieldTypeForAbs.setStoreTermVectors(true);  
      // Sometimes, we need the token's positions for this field.
      fieldTypeForAbs.setStoreTermVectorPositions(true);
      fieldTypeForAbs.freeze(); // no further change can be made.
      Field absField = new Field(ABSTRACT, "", fieldTypeForAbs);
      doc.add(absField);
      
      int id = 0;
      for ( String[] record : paperList ){
        idField.setStringValue(String.valueOf(id));
        titleField.setStringValue(
            record[0] == null ? "null" : record[0]);
        absField.setStringValue(
            record[1] == null ? "null" : record[1]);
        
        if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
          // New index, so we just add the document (no old document can be there):
          writer.addDocument(doc);
        } else {
          // Existing index (an old copy of this document may have been indexed) so 
          // we use updateDocument instead to replace the old one matching the exact 
          // path, if present:
          System.out.println("updating " + record[0]);
          writer.updateDocument(
              new Term(TITLE, record[0]), doc);
        }
        
        if ( ++id % 2 == 0 ){
          // with this information, we can guess how much time
          // it cost to index the whole corpus
//          System.out.println(id + " have been indexed...");
        }
      }

      // NOTE: if you want to maximize search performance,
      // you can optionally call forceMerge here.  This can be
      // a terribly costly operation, so generally it's only
      // worth it when your index is relatively static (ie
      // you're done adding documents to it):
      // 
      // ****** In our research work, the dataset is almost  *****
      //******* static, and we often index them only once. *****
      //******* So, we need turn this call on. ******
      writer.forceMerge(1);
      writer.close();
      
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
  }
  
  /**
   * Load all title-abstract pairs from .txt files under the
   * specified directory absPath. The title is the file name,
   * the abstract is the content of the file.
   * @param absPath
   * @return A list of two-elements String[]: 
   * [0]=title; [1]=abstract.
   * @throws IOException 
   */
  public static List<String[]> loadAbs(String absPath) throws IOException{
    ArrayList<String[]> list = new ArrayList<String[]>();
    File file = new File(absPath);
    
    if ( !file.exists() || !file.isDirectory()){
      throw new IllegalArgumentException(
          "absPath must be a folder path");
    }
    
    File[] txtFiles = file.listFiles(new FilenameFilter(){
      @Override
      public boolean accept(File dir, String name) {
        // TODO Auto-generated method stub
        return name.endsWith(SUFFIX); // accept only .txt files
//        return true;
      }
    });
    
    for ( File txtFile : txtFiles ){
      String title = txtFile.getName();
      title = title.substring(0, 
          title.length()-SUFFIX.length()); 
      String abs = IOUtil.getFileText(txtFile);
      String[] pair = new String[2];
      pair[0] = title;
      pair[1] = abs;
      list.add(pair);
    }
    
    return list;
  }

  
  public static List<String[]> getTitleContPair(String filepath){
		ArrayList<String[]> list = new ArrayList<String[]>();
		File file = new File(filepath);
		
		if ( !file.exists() || !file.isDirectory()){
		      throw new IllegalArgumentException(
		          "absPath must be a folder path");
		    }
		File[] txtFiles = file.listFiles(new FilenameFilter(){
		      @Override
		      public boolean accept(File dir, String name) {
		        // TODO Auto-generated method stub
		        return name.endsWith(SUFFIX); // accept only .txt files
//		        return true;
		      }
		    });
		String doc = new String();
		
		BufferedReader in = null;
		
		try {
			  for ( File txtFile : txtFiles ){
				  in = new BufferedReader(new InputStreamReader(new FileInputStream(txtFile)));
					 while ((doc=in.readLine())!=null){
						 String title = splitDoc(doc,true);
						 String abs = splitDoc(doc, false);
						 String[] pair = new String[2];
					      pair[0] = title;
					      pair[1] = abs;
					      list.add(pair);
			  }
			      }
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeReader(in);
		}
		
		
		return list;
		
	}
	
  public static List<String[]> getTitleContPairOnOneTXT(String filepath){
		ArrayList<String[]> list = new ArrayList<String[]>();
		File file = new File(filepath);
		
		if ( !file.exists() || !file.isFile()){
		      throw new IllegalArgumentException(
		          "absPath must be a file path");
		    }
	
		String doc = new String();
		
		BufferedReader in = null;
		
		try {
			 
				  in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					 while ((doc=in.readLine())!=null){
						 String title = splitDoc(doc,true);
						 String abs = splitDoc(doc, false);
						 String[] pair = new String[2];
					      pair[0] = title;
					      pair[1] = abs;
					      list.add(pair);
			  
			      }
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			closeReader(in);
		}
		
		
		return list;
		
	}
	private static String splitDoc(String doc, boolean b) {
		String pattern = "\t";
		String title = new String();
		title = doc.substring(0, doc.indexOf(pattern));
		String abs = new String();
		abs = doc.substring(doc.indexOf(pattern)+pattern.length(), doc.length()-1);
		if(b == true )
			return title;
		else 
			return abs;
	}
	
	public static void closeReader(Reader reader){
	    try {
	      if ( reader != null ){
	        reader.close();
	      }
	    } catch ( Exception ignore ){
	    }
	  }
  /**
   * @param args
   */
//  public static void main(String[] args) throws Exception {
    // TODO Auto-generated method stub
    //loadAbs("abstracts");
//    startIndex("abstracts//r52-test-stemmed.txt", "AbstractIndex");
//  }
  public  void cat52(String classroute){
	  startIndex(classroute,"AbstractIndex");
  }

}