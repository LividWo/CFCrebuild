package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	
	private static void outputAsTXT(String filepath ,String result ){
		File file = new File(filepath+".txt");
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
}
