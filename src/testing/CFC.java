package testing;

import java.util.HashMap;
import java.util.Map;

import util.IOUtil;
import util.TXTprocess;

public class CFC {
	static String[] r52 = {  "acq","alum","bop","carcass","cocoa","coffee","copper","cotton","cpi","cpu","crude","dlr","earn","fuel","gas","gnp",
			"gold","grain","heat","housing","income","instal-debt","interest","ipi","iron-steel","jet","jobs","lead","lei","livestock","lumber","meal-feed","money-fx"
			,"money-supply","nat-gas","nickel","orange","pet-chem","platinum","potato","reserves","retail","rubber","ship","strategic-metal"
			,"sugar","tea","tin","trade","veg-oil","wpi","zinc"
	};
	public static String[] model(int docNums){
	   double[] score52 =new double[52];
	   Map<String, Double> modelPipline = new HashMap<String, Double>();
	   double score =0;
	   String[] model = new String[docNums];
	   for(int i=0;i<docNums;i++){
		   for(int j=0;j<r52.length;j++){
			   score = testing.scoring(r52[j], i);
//			   System.out.println("class : "+r52[j]+"  score :"+score+"  number :"+((i-1)*52+j));
			   score52[j] = score;
			   modelPipline.put(r52[j], score);
		   }
		   TXTprocess.piplineMapdouble("E://java//dataset//test//classScore//"+i+".txt", modelPipline);
		   modelPipline.clear();
		   int result = testing.maxScore(score52);
		   model[i] = r52[result];
		   System.out.println("process "+i+"  doc");
	   }
	   return model;
	}
	
	public static void main(String[] args) throws Exception {
//		getTFIDF("E://java//CFCrebuild//AbstractIndex");
//		getClassOfTestDoc("E://java//dataset//analyzer//testingraw.txt",2349);
		int docNums = 2349;
		int micro =0;
		Map<String, Integer> macro = new HashMap<String, Integer>();
		for(int i =0;i<52;i++)
			macro.put(r52[i], 0);
//		String[] model = model(docNums);
		Map<String, Integer> docNumOfClass = IOUtil.readStringToIntegerMap("E://java//dataset//preprocessDone//docNums.txt", "utf-8");
		String[] model = getModelResult(docNums);
		String[] baseline = testing.getClassOfTestDoc("E://java//dataset//analyzer//testingraw.txt", docNums);
		for(int i=0;i<docNums;i++){
			System.out.println("this is doc num :"+i+"CFC classify this doc to :"+model[i]+"     it belongs to : "+baseline[i]);
			if(model[i].equals(baseline[i])){
//				System.out.println(micro++);
				micro++;
				int value = macro.get(baseline[i]);
				macro.put(model[i], value+1);
			}
		}
		double macrof1 =0;
//		int classnum =0;
		for(String term :macro.keySet()){
//			System.out.println(term);
//			System.out.println(macro.get(term));
//			System.out.println(docNumOfClass.get(term));
			if(docNumOfClass.get(term)>1){
				double classf1 = (double)macro.get(term)/(double)docNumOfClass.get(term);
				macrof1 = macrof1+classf1;
				System.out.println("classes :  "+term+"\t classify num :"+macro.get(term)+"\tclass num :"+docNumOfClass.get(term)+"\tf1 value  "+classf1);
//				classnum++;
			}
		}
		System.out.println(macrof1/52.0);
		System.out.println((double)micro/2349.0);
	}

	private static String[] getModelResult(int docNums) {
		String[] model = new String[docNums];
		double[] score52 =new double[r52.length];
		for(int i=0;i<docNums;i++){
			Map<String, Double> map = IOUtil.getStringToDoubleMap("E://java//dataset//test//classScore//"+i+".txt");
			for(int j=0;j<r52.length;j++){
				score52[j]=map.get(r52[j]);
			}
			int result = testing.maxScore(score52);
			model[i] = r52[result];
		}
		return model;
	}
}
