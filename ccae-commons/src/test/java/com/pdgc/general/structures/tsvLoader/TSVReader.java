package com.pdgc.general.structures.tsvLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TSVReader {

	public static RowMap readTSVFile(String filePath) throws IOException {
		RowMap rowMap;
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
	    String line;
	    
	    //1st line is the column names
	    line = br.readLine();
	    rowMap = new RowMap(line.split("\t"));	    
	    
	    while((line=br.readLine())!=null){
	    	rowMap.createNewRow(line.split("\t"));
	    }
	    br.close();
	    
	    return rowMap;
	}
}
