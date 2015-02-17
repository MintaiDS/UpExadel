import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TaskManager
{
	private static class SortManager implements Runnable
	{
		private ArrayList<String> list;
		public SortManager(ArrayList<String> list)
		{
			this.list = list;
		}
		
		public String toString()
		{
			return Thread.currentThread() + "";
		}
		
		public void run()
		{
			System.out.println(this);
			Collections.sort(list, new Comparator<String>()
					{
						public int compare(String firstString, String secondString) {
							return firstString.compareTo(secondString);
					}
						});
		}
		
	}
	
	
	private static void printToFile(Collection<String> collection, String filename) throws IOException
	{
		FileWriter fw = new FileWriter(filename, false);
		PrintWriter pw = new PrintWriter(fw);
		
		for(String s : collection)
		{
			pw.println(s);
		}
		
		pw.close();
	}
	
	private static void printToFile(int number, String filename) throws IOException
	{
		FileWriter fw = new FileWriter(filename, false);
		PrintWriter pw = new PrintWriter(fw);
		
		pw.println(number);
		
		pw.close();
	}
	
	public static void printComponentsByInputOrder(Collection<String> collection) throws IOException
	{
		printToFile(collection, "task1.txt");
	}
	
	public static void printComponentsBySymbolsCodes(Juicer juicer) throws IOException
	{
		ArrayList<ArrayList<String>> recipes = juicer.getRecipes();
		ArrayList<String> recipesToPrint = new ArrayList<String>();
		ExecutorService executor = Executors.newCachedThreadPool();
		for(ArrayList<String> r: recipes)
		{
			executor.execute(new SortManager(r));
		}
		executor.shutdown();
		for(ArrayList<String> r: recipes)
		{
			StringBuilder s = new StringBuilder(r.toString());
			s.deleteCharAt(0);
			s.deleteCharAt(s.length() - 1);
			recipesToPrint.add(s.toString());
		}
		printToFile(recipesToPrint, "task2.txt");
	}
	
	public static void printMinimalNumberOfCleanings(Juicer juicer) throws IOException
	{
		printToFile(juicer.minimalNumberOfCleanings(), "task3.txt");
	}
}
