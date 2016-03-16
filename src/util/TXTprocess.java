package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TXTprocess {
	//这个类用于将训练集TXT文件按照category进行分割
	public static List<String[]> getTitleContPair(String filepath){
		ArrayList<String[]> list = new ArrayList<String[]>();
		File file = new File(filepath);
		
		if ( !file.exists() || !file.isFile()){
		      throw new IllegalArgumentException(
		          "absPath must be a exsited file");
		    }
		
		String doc = new String();
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			 while ((doc=in.readLine())!=null){
				 System.out.println(doc);
				 String title = splitDoc(doc,true);
				 String abs = splitDoc(doc, false);
				 String[] pair = new String[2];
			      pair[0] = title;
			      pair[1] = abs;
			      list.add(pair);
			      }
		} catch (IOException e) {
			System.out.println(doc);
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
	
	public static  void splitPoint(String str,String pattern){
		System.out.println(pattern.length());
		System.out.println("split at :"+str.indexOf(pattern));
		System.out.println(str.substring(0, str.indexOf(pattern)));
		System.out.println(str.substring(str.indexOf(pattern)+pattern.length(), str.length()-1));
		
	}
	public static void testcase(){
		String[] r52 = {  "acq","alum","bop","carcass","cocoa","coffee","copper","cotton","cpi","cpu","crude","dlr","earn","fuel","gas","gnp",
				"gold","grain","heat","housing","income","instal-debt","interest","ipi","iron-steel","jet","jobs","lead","lei","livestock","lumber","meal-feed","money-fx"
				,"money-supply","nat-gas","nickel","orange","pet-chem","platinum","potato","reserves","retail","rubber","ship","strategic-metal"
				,"sugar","tea","tin","trade","veg-oil","wpi","zinc"
		};
		//test weather readline will read till encounter with \n of one pragraph
		List<String[]> list = new ArrayList<String[]>();
		list = getTitleContPair("E://java//dataset//analyzer//trainingraw.txt");
		int i=0;
		/*for(String[] pair :list){
			System.out.println(pair[0]);
			System.out.println(pair[1]);
			System.out.println(pair[0].length());
			if(pair[0].equals("earn"))
				System.out.println("get here");
			i++;
			System.out.println("here is the number :"+i+"document");
		}*/
		int j=0;
		boolean flag= false;
		for (;i<r52.length;i++){
			for(String[] pair : list){
				if(pair[0].equals(r52[i])){
					flag=true;
					String result =new String();
					result = pair[0]+"\t"+pair[1];
					pipline(r52[i], result);
					System.out.println(++j);
				}
			}
			if(flag==false){
				System.out.println("categorization error");
				System.out.println(r52[i]);
			}
				
			flag= false;
		}
	}
	
	private static void pipline(String classname ,String result ){
		String filepath ="E://java//dataset//preprocessDone//";
		File file = new File(filepath+classname+".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if(!file.exists())
				file.createNewFile();
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.write(result);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("error in pipline");
			e.printStackTrace();
		}
	}
	
	public static void piplineGeneral(String filepath ,String result ){
		File file = new File(filepath);
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			if(!file.exists())
				file.createNewFile();
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.write(result);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("error in pipline");
			e.printStackTrace();
		}
	}
	
	public static void piplineMap(String path ,Map<String, Integer> map) {
		File file = new File(path);
		FileWriter fw = null;
		BufferedWriter bw = null;
	    
		try {
			if(!file.exists())
				file.createNewFile();
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			for(String term :map.keySet()){
				int value = map.get(term);
				bw.write(String.format("%s : %d", term, value));
				bw.newLine();
			}
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("error in pipline");
			e.printStackTrace();
		}
	}
	
	public static void piplineMapdouble(String path ,Map<String, Double> map) {
		File file = new File(path);
		FileWriter fw = null;
		BufferedWriter bw = null;
	    System.out.println("storing :"+path);
		try {
			if(!file.exists())
				file.createNewFile();
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			for(String term :map.keySet()){
				double value = map.get(term);
				bw.write(String.format("%s : %f", term, value));
				bw.newLine();
			}
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("error in pipline");
			e.printStackTrace();
		}
	}
}
