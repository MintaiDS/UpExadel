
import java.util.Iterator;
import java.util.TreeSet;


public class Juice implements Comparable<Juice> {

	private TreeSet<String> juiceComponents = new TreeSet<String>();

	int complexity = 0;
	int complexityReal = 0;
	int complexityIm = 0;
	boolean inUse = false;
	
	
	public Juice(String [] components) {
		
		for (String comp: components){
		
			juiceComponents.add(comp);
			
		}
	}
	
	@Override
	public int compareTo(Juice juiceToCompare) {

		int comp =  this.getJuiceComponents().size() - juiceToCompare.getJuiceComponents().size();
		
		if (comp != 0 ) { 
			return comp;
		} else {
			
			Iterator <String> it = juiceToCompare.getJuiceComponents().iterator() ;
			
			while (it.hasNext()){
				
				if (!this.getJuiceComponents().contains(it.next())){
					return 1;
				}
				
			}
			
			return 0;
		}
	}
	
	public TreeSet<String> getJuiceComponents(){
		return juiceComponents;
	}
	
	public int getJuiceComponentsCount() {
		
		return juiceComponents.size();
		
	}
	
	
	public void printComponents () {
		
		System.out.println("Juice:");
		for(String comp: juiceComponents)
		System.out.println(comp);
		
	}
	
	public boolean isContainsComponentsOff(Juice j){
		
		if (j.getJuiceComponentsCount() >= this.getJuiceComponentsCount())
			return false;
		
		Iterator <String> it = j.getJuiceComponents().iterator() ;
		
		while (it.hasNext()){
			
			if (!this.getJuiceComponents().contains(it.next())){
				return false;
			}
			
		}
		
		return true;
	}
	

	
	
	
}
