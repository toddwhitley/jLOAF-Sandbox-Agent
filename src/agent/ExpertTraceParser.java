package agent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jLOAF.casebase.Case;
import org.jLOAF.casebase.CaseBase;
import org.jLOAF.tools.CaseBaseIO;

import common.Config;

public abstract class ExpertTraceParser {

	protected String filename;
	protected CaseBase casebase;
	
	public ExpertTraceParser(String filename){
		this.filename = filename;
		this.casebase = new CaseBase();
	}

	public abstract Case parseLine(String tokens[], Case c);
	
	public boolean parseFile(){
		BufferedReader ip = null;
		try {
			ip  = new BufferedReader (new FileReader(filename));
			String line = ip.readLine();
			Case c = null;
			while(line != null){
				if (line.isEmpty()){
					continue;
				}

				String tokens[] = line.split(Config.DEFAULT_DELIMITER);
				
				c = parseLine(tokens, c);
				casebase.add(c);
				line = ip.readLine();
			}
			if (ip != null){
				ip.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void saveCaseBase(String filename){
		try {
			CaseBaseIO.saveCaseBase(casebase, filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
