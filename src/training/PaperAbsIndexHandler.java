package training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;

import util.IOUtil;
import util.TXTprocess;

/**
 * @author Chenliang Li [cllee@whu.edu.cn]
 *
 */
public class PaperAbsIndexHandler {
  
  public static final String TITLE = "title";
  public static final String ABSTRACT = "abs";
  public static final String ID = "id";
  
  private String indexPath;
  private IndexReader indexReader;
//  private FixedBitSet bits;
  private Bits searchAllBits;
  
  public PaperAbsIndexHandler(String indexPath){
    this.indexPath = indexPath;
    try {
      indexReader = 
          DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
//      bits = new FixedBitSet(indexReader.numDocs());  
      searchAllBits = new Bits.MatchAllBits(indexReader.numDocs());
//      System.out.println(searchAllBits.length());  
//      this is the num of doc in this class
    } catch ( IOException ioe ){
      ioe.printStackTrace();
      System.exit(-1);
    }
  }
  
  /**
   * Gets the number of documents indexed
   * @return
   */
  public int getSize(){
    return indexReader.numDocs();
  }
  
  public void close(){
    try {
      indexReader.close();
    } catch ( Exception ignore){
    }
  }
  
  /**
   * Gets the term vector for the document specified 
   * by <code>id</code>
   * @param id
   * @return
   */
  public Map<String, Integer> getTermVector(int id){
    Map<String, Integer> tfMap = new HashMap<String, Integer>();
    
    // If we want to get a term vector of a document, 
    // we need to get this document's index (i.e. DocsEnum.nextDoc()).
    // So we need assign each document a primary key field, and
    // this field must be indexed by Lucene.
    Term term = new Term(ID, String.valueOf(id));
    try{
      DocsEnum docEnum = MultiFields.getTermDocsEnum(
          indexReader, searchAllBits, 
          ID, term.bytes());
      int doc = DocsEnum.NO_MORE_DOCS;
      BytesRef ref = null;
      String termText = null;
      DocsEnum reusableDocEnum = null;
      while ((doc=docEnum.nextDoc())!=DocsEnum.NO_MORE_DOCS){
        Terms terms = indexReader.getTermVector(doc, ABSTRACT);
        TermsEnum termsEnum = terms.iterator(null);
        
        
        // iterate each term for obtaining its tf value
        while ((ref=termsEnum.next())!=null){
          termText = ref.utf8ToString();
          
          // here, since termsEnum is obtained from a single
          // document, docFreq will be 1.
          int docFreq = termsEnum.docFreq();
          
          // here, since termsEnum is obtained from a single 
          // document, totalTermFreq() returns tf value.
          int totalFreq = (int)termsEnum.totalTermFreq();
         System.out.println(String.format("%d,%s, %d, %d,%d", id,termText, docFreq, totalFreq,terms.size()));
          tfMap.put(termText, totalFreq);
        }
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return tfMap;
  }
  
  public Map<String, Double> getNormalizedTF(int id){
	    Map<String, Double> tfMap = new HashMap<String, Double>();
	    Term term = new Term(ID, String.valueOf(id));
	    try{
	      DocsEnum docEnum = MultiFields.getTermDocsEnum(
	          indexReader, searchAllBits, 
	          ID, term.bytes());
	      int doc = DocsEnum.NO_MORE_DOCS;
	      BytesRef ref = null;
	      String termText = null;
	      DocsEnum reusableDocEnum = null;
	      while ((doc=docEnum.nextDoc())!=DocsEnum.NO_MORE_DOCS){
	        Terms terms = indexReader.getTermVector(doc, ABSTRACT);
	        TermsEnum termsEnum = terms.iterator(null);
	        // iterate each term for obtaining its tf value
	        while ((ref=termsEnum.next())!=null){
	          termText = ref.utf8ToString();
	          int totalFreq = (int)termsEnum.totalTermFreq();
//	         System.out.println(String.format("%d,%s, %d,%d", id,termText, totalFreq,terms.size()));
	         double  normalizedTF = (double)totalFreq/(double)terms.size();
	          tfMap.put(termText, normalizedTF);
	        }
	      }
	    } catch ( Exception e ){
	      e.printStackTrace();
	    }
	    return tfMap;
	  }
  
  public Map<Integer, String> getIDToTitleMap(){
    Map<Integer, String> idTitleMap = new HashMap<Integer, String>();
    int max = indexReader.numDocs();
//    System.out.println(indexReader.numDocs());
    try {
      for ( int i = 0; i < max; i++ ){
        Document doc = indexReader.document(i);
        String title = doc.get(TITLE);
        String id = doc.get(ID);
//        System.out.println(id+title);
        idTitleMap.put(Integer.parseInt(id), title);
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return idTitleMap;
  }
  
  protected Set<String> getAllTerms(TermsEnum termsEnum){
    Set<String> terms = new HashSet<String>();
    try {
      BytesRef ref = null;
      while ((ref=termsEnum.next())!=null){
        terms.add(ref.utf8ToString());
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return terms;
  }
  
  /**
   * Gets a map of term-df pairs for each term from 
   * the filed {@link #ABSTRACT}. df refers to document_frequency. 
   * @return
   */
  public Map<String, Integer> getTermDFMap(){
    Map<String, Integer> dfMap = new HashMap<String, Integer>();
    try {
      Terms terms = MultiFields.getTerms(indexReader, ABSTRACT);
      TermsEnum termsEnum = terms.iterator(null);
      BytesRef ref = null;
      while ((ref=termsEnum.next())!=null){
//    	  System.out.println(ref.utf8ToString());
//    	  System.out.println(termsEnum.docFreq());
        dfMap.put(ref.utf8ToString(), termsEnum.docFreq());
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return dfMap;
  }
  
  /**
   * Gets a map of term-cf pairs for each term from 
   * the filed {@link #ABSTRACT}. cf refers to collection_frequency. 
   * @return
   */
  public Map<String, Integer> getTermCFMap(){
    Map<String, Integer> cfMap = new HashMap<String, Integer>();
    try {
      Terms terms = MultiFields.getTerms(indexReader, ABSTRACT);
      TermsEnum termEnum = terms.iterator(null);
      BytesRef ref = null;
      while ((ref=termEnum.next())!=null){
        cfMap.put(ref.utf8ToString(), (int)termEnum.totalTermFreq());
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return cfMap;
  }
  
  /**
   * Get all terms indexed for the field {@link #ABSTRACT}
   * @return
   */
  public  Set<String> termDictionary(){
    Set<String> dic = new HashSet<String>();
    try {
      Terms terms = MultiFields.getTerms(indexReader, ABSTRACT);
      TermsEnum termEnum = terms.iterator(null);
      BytesRef ref = null;
      while ((ref=termEnum.next())!=null){
        dic.add(ref.utf8ToString());
      }
    } catch ( Exception e ){
      e.printStackTrace();
    }
    
    return dic;
  }
  
  public Map<String, Integer> termDicForCFC(){
	  Map<String, Integer> dic  =  new  HashMap<String, Integer>();
	  try {
	      Terms terms = MultiFields.getTerms(indexReader, ABSTRACT);
	      TermsEnum termEnum = terms.iterator(null);
	      BytesRef ref = null;
	      while ((ref=termEnum.next())!=null){
	    	  //here have some error dont use this method
	    	  if(!dic.containsKey(ref.utf8ToString())){
	    		  dic.put(ref.utf8ToString(), 1);
	    		  System.out.println("arrive here");
	    	  }
	    	  else{
	    		  dic.put(ref.utf8ToString(), dic.get(ref.utf8ToString()+1));
	    		  System.out.println("arrive here too");
	    	  }
	    	  System.out.println(ref.utf8ToString()+"   "+dic.get(ref.utf8ToString()));
	        }
	  } catch ( Exception e ){
	      e.printStackTrace();
	    }
	  return dic;
  }
  
  /**
   * @param args
   */
//  public static void main(String[] args) {
//    // TODO Auto-generated method stub
//    testCase("acq");
//  }
  //   this test is used to create a dic for the whole training corpus
  public  void getTrainDic(String filepath){
	  Map<String, Integer> dic  =  new  HashMap<String, Integer>();
	  String indexPath = "AbstractIndex";
	  PaperAbsIndexHandler pah = new PaperAbsIndexHandler(indexPath);
	  dic = pah.getTermCFMap();
	  int i=1;
	 for(String term : dic.keySet()){
		 //remember that : if you are index the whole corpues ,use the condition that dic.get(term)>=3
		 if(dic.get(term)>=3){
//			 System.out.println(String.format("%s : %d", term, dic.get(term)));
			 TXTprocess.piplineGeneral(filepath, term);
//			 System.out.println(i++);
		 }
	 }
  }
  public  void getDF(String classname){
	  String indexPath = "AbstractIndex";
	    PaperAbsIndexHandler indexHandler = 
	        new PaperAbsIndexHandler(indexPath);
	    Map<Integer, String> idTitleMap = 
	        indexHandler.getIDToTitleMap();
	    for ( Integer id : idTitleMap.keySet()){
	      String title = idTitleMap.get(id);
	      System.out.println(id+" : "+title);
	    }
	  Map<String, Integer> dfMap =  indexHandler.getTermDFMap();
	  Map<String, Integer>  corpurs = IOUtil.openDIC("E://java//dataset//dic//traindic.txt");
	  for(String term : corpurs.keySet()){
		  if(dfMap.containsKey(term)){
//			  corpurs.replace(term, 0, dfMap.get(term));
			  /*if(term.equals("half"))
				  System.out.println("find half in containskey");*/
			  TXTprocess.piplineGeneral("E://java//dataset//df//"+classname+".txt", String.format("%s : %d", term,dfMap.get(term)));
		  }
		  else{
//			  TXTprocess.piplineGeneral("E://java//dataset//df//"+classname+".txt", String.format("%s : %d", term,corpurs.get(term)));
//			  System.out.println(String.format("%s : %d", term,corpurs.get(term)));
		  }
	  }
  }

  public static void getTFIDF(String classname){
    // this is the directory we used for indexing 
    // in PaperAbsIndexWriter.java
    String indexPath = "AbstractIndex";
    PaperAbsIndexHandler indexHandler = 
        new PaperAbsIndexHandler(indexPath);
    Map<Integer, String> idTitleMap = 
        indexHandler.getIDToTitleMap();
    for ( Integer id : idTitleMap.keySet()){
      String title = idTitleMap.get(id);
//      System.out.println(id+" : "+title);
    }
    
    int id =0 ;
    double tfidf =0;
    Map<String, Integer> dfMap = indexHandler.getTermDFMap();
    for(;id<2349;id++){
    	Map<String, Double> tfMap = indexHandler.getNormalizedTF(id);
    	Map<String, Double> tfidfMap = new HashMap<String, Double>();
//        System.out.println("following are term frequency");
//    	pipline(String.valueOf(id), idTitleMap.get(id));
        for ( String term : tfMap.keySet()){
          double tf = tfMap.get(term);
          tfidf = tf*Math.log(2349.0/(double)dfMap.get(term));
//          if(term.equals("reuter"))
//        	  System.out.println(term+"  :  "+tfidf+"   :   "+tf+"  : "+dfMap.get(term));
          tfidfMap.put(term, tfidf);
//          System.out.println(term+"  :  "+tfidf+"   :   "+tf);
//          System.out.println(String.format("%s : %f", term, tfidf));
//          pipline(String.valueOf(id), String.format("%s : %f", term, tfidf)); 
        }
        TXTprocess.piplineMapdouble("E://java//dataset//test//tfidf//"+id+".txt", tfidfMap);
    }
    
    /**
     * Of course, we can load these term-frequency map from
     * a file. The file should contain each pair in one row, and
     * the term and int value are separated by ','.
     */
    /*dfMap = IOUtil.readStringToIntegerMap(
        "TermDFMap.txt", "utf8");
    for ( String term : dfMap.keySet()){
      int df = dfMap.get(term);
      System.out.println(String.format("%s : %d", term, df));
    }*/
  }
}
