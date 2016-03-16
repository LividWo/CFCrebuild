package training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import util.IOUtil;
import util.TXTprocess;


public class training {
	private static String[] r52 = {  "acq","alum","bop","carcass","cocoa","coffee","copper","cotton","cpi","cpu","crude","dlr","earn","fuel","gas","gnp",
			"gold","grain","heat","housing","income","instal-debt","interest","ipi","iron-steel","jet","jobs","lead","lei","livestock","lumber","meal-feed","money-fx"
			,"money-supply","nat-gas","nickel","orange","pet-chem","platinum","potato","reserves","retail","rubber","ship","strategic-metal"
			,"sugar","tea","tin","trade","veg-oil","wpi","zinc"
	};
	
	public static void getTitleDic(){
		PaperAbsIndexWriter paw = new PaperAbsIndexWriter();
		PaperAbsIndexHandler pah = new PaperAbsIndexHandler("E://java//CFCrebuild//AbstractIndex");
		paw.cat52("E://java//dataset//analyzer//traingTitleRaw.txt");
		pah.getTrainDic("E://java//dataset//titleTerm//titledic.txt");
	}
	
	public static void getDicForTrainSet(){
		
		PaperAbsIndexWriter paw = new PaperAbsIndexWriter();
		PaperAbsIndexHandler pah = new PaperAbsIndexHandler("E://java//CFCrebuild//AbstractIndex");
		for(int i=0;i<r52.length;i++){
			paw.cat52("E://java//dataset//preprocessDone//"+r52[i]+".txt");
			pah.getTrainDic("E://java//dataset//dic//traindic.txt");
		}
	}
	
	public static void getDFforCFC(){
		PaperAbsIndexWriter paw = new PaperAbsIndexWriter();
		PaperAbsIndexHandler pah = new PaperAbsIndexHandler("E://java//CFCrebuild//AbstractIndex");
		for(int i=0;i<r52.length;i++){
			paw.cat52("E://java//dataset//preprocessDone//"+r52[i]+".txt");
			pah.getDF(r52[i]);
		}
		/*paw.cat52("E://java//dataset//preprocessDone//copper.txt");
		pah.getDF("copper");*/
	}
	
	public static void getCFforCFC(){
		PaperAbsIndexWriter paw = new PaperAbsIndexWriter();
		PaperAbsIndexHandler pah = new PaperAbsIndexHandler("E://java//CFCrebuild//AbstractIndex");
		Map<String, Integer> corpusdic = new HashMap<String, Integer>();
		corpusdic = IOUtil.openDIC("E://java//dataset//dic//traindic.txt");
	    int i=0;
		for (;i<r52.length;i++){
			System.out.println("process class :"+r52[i]);
			paw.cat52("E://java//dataset//preprocessDone//"+r52[i]+".txt");
			pah.getTrainDic("E://java//dataset//cf//classdic//"+r52[i]+".txt");
			File classdic = new File("E://java//dataset//cf//classdic//"+r52[i]+".txt");
			 String term = new String();
			 BufferedReader in = null;
			 try {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(classdic)));
				 while((term = in.readLine())!=null){
					 if(!corpusdic.containsKey(term)){
						 System.out.println("this term occurs less than 3 times in the whole corpurs~");
					}
					 else if (corpusdic.containsKey(term)){
						 corpusdic.replace(term, corpusdic.get(term), corpusdic.get(term)+1);
					 }
			      }
			 }catch (IOException e) {
				e.printStackTrace();
			}finally{
				IOUtil.closeReader(in);
			}
		}
		TXTprocess.piplineMap("E://java//dataset//cf//cf.txt", corpusdic);
	}
	
	private static void getCjForCFC() {
		
		Map<String, Integer> cj = new HashMap<String, Integer>();
		for (int i=0;i<r52.length;i++){
			int doc=0;
		File classdic = new File("E://java//dataset//preprocessDone//"+r52[i]+".txt");
		 String term = new String();
		 BufferedReader in = null;
		 try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(classdic)));
			 while((term = in.readLine())!=null){
				 doc++;
//					 System.out.println(doc++);
				}
		 }catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtil.closeReader(in);
		}
		 cj.put(r52[i], doc);
//		 System.out.println(r52[i]+"   "+doc);
		}
		TXTprocess.piplineMap("E://java//dataset//cj//cj.txt", cj);
	}
	
	private static void getWeight() {
		double b= Math.E-1.7;
		Map<String, Integer> cj =  IOUtil.readStringToIntegerMap("E://java//dataset//cj//cj.txt", "utf-8");
		Map<String, Integer> cf =  IOUtil.readStringToIntegerMap("E://java//dataset//cf//cf.txt", "utf-8");
		Map<String, Integer> titleTerm =  IOUtil.readTitle("E://java//dataset//titleTerm//titledic.txt", "utf-8");
		for (int i=0;i<r52.length;i++){
			System.out.println("calculating "+r52[i]);
			Map<String, Double> weight = IOUtil.openDICdouble("E://java//dataset//dic//traindic"+".txt");
			Map<String, Integer> df =  IOUtil.readStringToIntegerMap("E://java//dataset//df//"+r52[i]+".txt", "utf-8");
			int cj1,cf1,df1;
			cj1 = cj.get(r52[i]);
			for(String term :weight.keySet()){
//				System.out.println(term);
				if(cf.get(term)==null || df.get(term)==null)
					continue;
				cf1 = cf.get(term);
				df1 = df.get(term);
				double innerClass = Math.pow(b, (double)df1/(double)cj1);
				double interClass = Math.log(52.0/(double)cf1);
				if(titleTerm.containsKey(term)){
					weight.put(term, 10*innerClass*interClass);
				    System.out.println("term   "+term+"cj :  "+cj1+"  df :"+df1+"   cf :"+cf1+" inner :"+innerClass+" inter :"+interClass);
				}
				else{
					weight.put(term, innerClass*interClass);
				}
			}
//			TXTprocess.piplineMapdouble("E://java//dataset//centroid//"+r52[i]+".txt", weight);
			weight.clear();
		}
	}
	

	
  public static void main(String[] args) throws Exception {
//	  getTitleDic();
//	  getDicForTrainSet();
//	  getDFforCFC();
//	  getCFforCFC();
//	  getCjForCFC();
//	  getWeight();
	  
  }
}
