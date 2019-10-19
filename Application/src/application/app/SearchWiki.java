package application.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javafx.concurrent.Task;

/**
 * Task that uses wikit to search
 * wikipedia in the background.
 */

public class SearchWiki extends Task<Boolean> {
	
	private String _searchTerm;
	private String _dir;
	
	public SearchWiki(String term) {
		_searchTerm = term;
		_dir = System.getProperty("user.dir");
	}

	@Override
	protected Boolean call() {
		if(_searchTerm.contains("-version")|| _searchTerm.contains("-help")) {
			return false;
		}
		//call bash and use wikit to get the entry for the user's search term. Return true if there is an entry and false if there isn't
		String s = "timeout 5 wikit \""+ _searchTerm +"\" | sed -e 's/^[ \\t]*//' > "+_dir+"/tmp/text/wikitext.txt";

		ProcessBuilder builder = new ProcessBuilder("bash", "-c", s);
		if(isCancelled()){
			return false;
		}
		try {
			Process process = builder.start();
			
			if(isCancelled()){
				process.destroy();
				return false;
			}
			
			process.waitFor();
			BufferedReader reader;
			
			if(isCancelled()){
				return false;
			}
			//read the file to check for an entry
			reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/wikitext.txt")));
			String line= reader.readLine();
			reader.close();
			//if there was no entry, return false
			if (line.contains(_searchTerm+" not found :^(")) {
				return false;
			}else if (line.contains("Ambiguous results, \""+_searchTerm+"\" may refer to:")) {
				return false;
			} else {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {}
		
		return true;
	}

}