
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class JuiceStorageController {

	
	public static final JuiceStorageController sharedStorage = new JuiceStorageController();
	
	private TreeSet <Juice> allJuices = new TreeSet <Juice>();
	private LinkedHashSet <String> allComponents = new LinkedHashSet<String> ();
	
	public void addJuice(Juice juiceToAdd) {
		
		allJuices.add(juiceToAdd);
		
	}
	
	public void addComponent(String comp) {
		
		allComponents.add(comp);
		
	}
	
	
	
	//Mark: - Logic
	
	
	
	
	public int countsToClean() {
		
		int count = 0;
		ExecutorService executor = Executors.newCachedThreadPool();
		for(Juice j : allJuices ) {	
			executor.execute(new countComplexity(j));
		}
		
		executor.shutdown();


		LinkedList <Juice> list = new LinkedList <Juice>(allJuices);
		//Sorting Juices to get the answer
		Collections.sort(list, new Comparator<Juice>() {

			@Override
			public int compare(Juice o1, Juice o2) {
				
				int comp =  o1.getJuiceComponents().size() - o2.getJuiceComponents().size();
				
				if (comp != 0 ) { 
					return comp;
				} else {
					comp =  o1.complexityReal - o2.complexityReal;
					if (comp!=0) return comp;
					else {
						return o1.complexityIm - o2.complexityIm;
					}
			}
			
			} });
		
		
		
		for(Juice j : list ) {
			if (j.getJuiceComponentsCount() == 1) {
				j.complexity = 0;
			} else {
				Iterator <Juice> it = list.iterator();
				while(it.hasNext()){
					Juice juiceToCompare = it.next();
					
					if(j.isContainsComponentsOff(juiceToCompare)){
						if (j.complexity <= juiceToCompare.complexity && juiceToCompare.inUse == false){
							j.complexity = juiceToCompare.complexity + 1 ;
							juiceToCompare.inUse = true;		
						}
					}															
				}				
			}
		
		}
		
		
		for(Juice j : list ) {
			if (j.complexity == 0) {
				count++ ;
			}
		}
		
		return count;
	}
	
	public String[] componentsAscending() {
		
		String [] componentsAcs = componentsAsInInputFile();
		Arrays.sort(componentsAcs);
		
		return componentsAcs;
		
	}
	
	
	
	
	
	public String[] componentsAsInInputFile() {
		
		String [] allComponentsArray = new String [allComponents.size()] ;
		allComponentsArray = allComponents.toArray(allComponentsArray);
		return allComponentsArray;
		
	}
	
	
	
	private class countComplexity implements Runnable
	{
		private Juice juice;
		public countComplexity(Juice juice){
			this.juice = juice;
		}
		
		public String toString(){
			return Thread.currentThread() + "";
		}
		
		public void run(){
			Iterator <Juice> it = allJuices.iterator();
			while(it.hasNext()){
				Juice juiceToCompare = it.next();
				if(juiceToCompare.isContainsComponentsOff(juice)){	
					juice.complexityReal += 1;					
				}	
				
				if (juice.isContainsComponentsOff(juiceToCompare)){
					juice.complexityIm += 1;
				}
			}					
		}
		
	}
	
}
