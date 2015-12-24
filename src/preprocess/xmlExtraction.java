package preprocess;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import util.TXTprocess;

public class xmlExtraction {

	/**
	 * @param file address
	 * @return an document entity in jsoup
	 */
	public static Document openFile(String str){
		File input = new File(str);
		String basuri = new String();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(input);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Document doc = null;
		try {
			doc = Jsoup.parse(fis, "utf-8",basuri , org.jsoup.parser.Parser.xmlParser());
		} catch (IOException e) {
			System.out.println("open file error 1`");
			e.printStackTrace();
		}
		System.out.println("open success");
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * @param doc an document entity
	 * @param isTitle decide to return a title or class name and content 
	 * @return result at the format of string
	 */
	public static String extractContent(Document doc,boolean isTitle,boolean isTrain){
		StringBuilder sb = new StringBuilder();
		StringBuilder wordInTitle = new StringBuilder();
		Elements set = new  Elements();
		if(isTrain)
					set = doc.select("REUTERS[LEWISSPLIT=TRAIN]");
		else
			        set = doc.select("REUTERS[LEWISSPLIT=TEST]");
		for(Element train : set){
				Element topic = train.select("TOPICS").first();
				Elements topicsd = topic.select("D");
				if(topicsd.size()==1){
					sb.append(topicsd.text());
					sb.append("   ");
				}
				else
					continue;
				Elements titles = train.select("TITLE");
				for (Element title :titles){
					sb.append(Analyzers.analyzer(title.text()));
					sb.append(" ");
					wordInTitle.append(Analyzers.analyzer(title.text()));
					wordInTitle.append("\r\n");
				}
				Elements contents = train.select("BODY");
				for(Element content : contents){
					sb.append(Analyzers.analyzer(content.text()));
					sb.append("\r\n");
				}
			}
		if(isTitle ==true)
			return wordInTitle.toString();
		else 
		    return sb.toString();
	}
	
	/**
	 * @param corpusNum
	 * @param result
	 * notice that : if you want to write a different file change the param setting in extractconten too!!!
	 */
	public static void pipline(int corpusNum ,String result ){
		String filepath ="E://java//CFC//";
		File file = new File(filepath+"trainingraw"+".txt");
//		File file = new File(filepath+"testingraw"+".txt");
//		File file = new File(filepath+"traingTitleRaw"+".txt");
//		File file = new File(filepath+"testingTitleRaw"+".txt");
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {
			if(!file.exists())
				file.createNewFile();
//			fw = new FileWriter(file);
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();
		} catch (IOException e) {
			System.out.println("error in pipline");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		/*int corpus;
		for( corpus = 0;corpus<=21;corpus++){
			Document doc = openFile("E://java//reuters//"+corpus+".xml");
//			String Title = extractContent(doc,true,true);
			String Abs = extractContent(doc,false,true);
			pipline(corpus, Abs);
		}*/
		TXTprocess test = new TXTprocess();
		test.testcase();
	}

}
