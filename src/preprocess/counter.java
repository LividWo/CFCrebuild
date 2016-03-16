package preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import util.IOUtil;
import util.TXTprocess;

public class counter {
	static String[] r52 = {  "acq","alum","bop","carcass","cocoa","coffee","copper","cotton","cpi","cpu","crude","dlr","earn","fuel","gas","gnp",
		"gold","grain","heat","housing","income","instal-debt","interest","ipi","iron-steel","jet","jobs","lead","lei","livestock","lumber","meal-feed","money-fx"
		,"money-supply","nat-gas","nickel","orange","pet-chem","platinum","potato","reserves","retail","rubber","ship","strategic-metal"
		,"sugar","tea","tin","trade","veg-oil","wpi","zinc"
   };
	public static void getDocNumsOfTrainClass(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int i=0;i<r52.length;i++){
			File file = new File("E://java//dataset//preprocessDone//"+r52[i]+".txt");
			int j=0;  
			try {
				BufferedReader in =  IOUtil.getFileReader(file);
				String line;
				while((line = in.readLine())!=null){
					j++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			map.put(r52[i], j);
		}
		IOUtil.writeStringToIntegerMap("E://java//dataset//preprocessDone//docNums.txt", map);
	}
	
	
	public static void main(String[] args) throws Exception {
		getDocNumsOfTrainClass();
	}
}
