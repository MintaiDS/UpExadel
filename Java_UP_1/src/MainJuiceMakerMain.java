
public class MainJuiceMakerMain {

	public static void main(String[] args) {


		
		InOutController.readFromFileWithString("juice.in");
		
		String [] task1 = JuiceStorageController.sharedStorage.componentsAsInInputFile();
		
		String [] task2 = JuiceStorageController.sharedStorage.componentsAscending();
		
		
		InOutController.writeToFileWithStringArray("1.txt", task1);
		
		InOutController.writeToFileWithStringArray("2.txt", task2);
		
		
		int task3 = JuiceStorageController.sharedStorage.countsToClean();
		InOutController.writeToFileWithStringArray("3.txt", new String [] {"Min count, to clean JuiceMaker = " + task3});

		
	}

}
