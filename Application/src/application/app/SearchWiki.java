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
	private final String _TIMEOUT = "5";
	
	public SearchWiki(String term) {
		_searchTerm = term;
		_dir = System.getProperty("user.dir"); //Current directory
	}

	@Override
	protected Boolean call() {
		if(_searchTerm.matches("(?s)-.+") || _searchTerm.contains("\"")) {
			return false;
		}
		//Call bash and use wikit to get the entry for the user's search term.
		
		//Return true if there is an entry.
		//Return false if there is not or if there is a timeout.
		
		String command = "timeout " + _TIMEOUT + " wikit \""+ _searchTerm +"\" | sed -e 's/^[ \\t]*//' > "+_dir+"/tmp/text/wikitext.txt";

		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
		if(isCancelled()){
			return false;
		}
		try {
			Process process = processBuilder.start();
			
			if(isCancelled()){
				process.destroy();
				return false;
			}
			
			process.waitFor();
			BufferedReader reader;
			
			if(isCancelled()){
				return false;
			}
			//Read the file to check for an entry
			reader = new BufferedReader(new FileReader(new File(_dir+"/tmp/text/wikitext.txt")));
			String line= reader.readLine();
			reader.close();
			
			//Return the appropriate response
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