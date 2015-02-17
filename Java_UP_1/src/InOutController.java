import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


public class InOutController {

	static public void readFromFileWithString(String fileName) { 
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
	        String line = br.readLine();
	        while (line != null) {
	            //Input
	        	String [] components = line.split(" ");
	        	for (String comp: components){	        	
	        		comp = comp.toLowerCase();
	        		JuiceStorageController.sharedStorage.addComponent(comp);
	        	}
	        	JuiceStorageController.sharedStorage.addJuice(new Juice(components));
	        	line = br.readLine();

	        }
	        
		} catch (IOException e){
			
			e.printStackTrace();
			System.err.println(e.getLocalizedMessage());
			
		}
		
		
	}
	
	static public void writeToFileWithStringArray(String fileName, String [] array ){
		
		
		try {
			
			FileOutputStream os = new FileOutputStream(fileName,false);
			PrintWriter outputfile = new PrintWriter(os);
			
			for(String line : array) {
				
				outputfile.println(line);
				
			}
			
			outputfile.close();
			
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
}
