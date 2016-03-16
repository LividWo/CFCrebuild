package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import training.PaperAbsIndexHandler;
import training.PaperAbsIndexWriter;
import util.IOUtil;
import util.TXTprocess;

public class testing {
	private static String[] r52 = {  "acq","alum","bop","carcass","cocoa","coffee","copper","cotton","cpi","cpu","crude","dlr","earn","fuel","gas","gnp",
		"gold","grain","heat","housing","income","instal-debt","interest","ipi","iron-steel","jet","jobs","lead","lei","livestock","lumber","meal-feed","money-fx"
		,"money-supply","nat-gas","nickel","orange","pet-chem","platinum","potato","reserves","retail","rubber","ship","strategic-metal"
		,"sugar","tea","tin","trade","veg-oil","wpi","zinc"
};
	
	public static void main(String[] args) throws Exception {
//		getTFIDF("E://java//CFCrebuild//AbstractIndex");
//		getClassOfTestDoc("E://java//dataset//analyzer//testingraw.txt",2349);
		calculateNorm2();
	}
	public static int maxScore(double[] score52) {
		int result=0;
		double max = score52[0];
		for(int i=0;i<score52.length-1;i++){
			if(score52[i+1]>=max){
				result = i+1;
				max = score52[1+i];
			}
		}
		return result;
	}
	
	public static double scoring(String centroid ,int doc)  {
		double score=0;
		Map<String, Double> docWeightMap = IOUtil.getStringToDoubleMap("E://java//dataset//test//tfidf//"+doc+".txt");
		Map<String, Double> centroidMap = IOUtil.getStringToDoubleMap("E://java//dataset//centroid//"+centroid+".txt");
		double cos= getCosin(centroidMap, docWeightMap,centroid) ;
		double centroidNorm = getNorm(centroid);
		if(centroidNorm==0)
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		score = cos*centroidNorm;
		return score;
	}
	public static double getCosin(Map<String, Double> centroid ,Map<String, Double> doc,String centroidNorm2){
		double cos = 0;
		double dot = 0;
		for (String term:doc.keySet()){
			if(centroid.get(term)!=null)
				dot = dot + doc.get(term)*centroid.get(term);
		}
		double centroidNorm = getNorm(centroidNorm2);
		double docNorm = norm2(doc);
		cos = dot/(centroidNorm*docNorm);
		return cos;
	}
	public static double getNorm(String centroid){
		double norm = 0 ;
		Map<String, Double> normMap = IOUtil.getStringToDoubleMap("E://java//dataset//centroidNorm.txt");
		if(normMap.get(centroid)!=null){
			norm = normMap.get(centroid);
		}
		return norm;
	}
	public static  void calculateNorm2(){
		double norm = 0  ;
		Map<String, Double> normMap= new HashMap<String, Double>();
		for(int i=0;i<r52.length;i++){
			Map<String, Double> centroidMap = IOUtil.getStringToDoubleMap("E://java//dataset//centroid//"+r52[i]+".txt");
			norm = norm2(centroidMap);
			normMap.put(r52[i], norm);
		}
		TXTprocess.piplineMapdouble("E://java//dataset//centroidNorm.txt", normMap);
	}
	
	public static double norm2(Map<String, Double> map){
		double norm2 = 0;
		for(String term : map.keySet()){
			if(map.get(term) > 0)
				 norm2 = norm2 + map.get(term)*map.get(term);
		}
		norm2 = Math.sqrt(norm2);
		return norm2;
	}
	private static void getTFIDF(String path) {
//		PaperAbsIndexWriter paw = new PaperAbsIndexWriter();
//		paw.cat52("E://java//dataset//analyzer//testingraw.txt");
		PaperAbsIndexHandler pah = new PaperAbsIndexHandler(path);
		pah.getTFIDF("ignore this param");
	}

	public static String[] getClassOfTestDoc(String path,int docNum) {
		String[] baseline = new String[docNum];
		File testRaw = new File(path);
		try {
			BufferedReader in =  IOUtil.getFileReader(testRaw);
			String line;
//			int i=1;  use to get the number of all test doc
			int i=0;
			while((line = in.readLine())!=null){
//				System.out.println(i++);
				int position = line.indexOf("\t");
				baseline[i] = line.substring(0, position);
//				System.out.println("doc :"+i+"   "+baseline[i]);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("successfully get baseline");
		return baseline;
	}
	
}
