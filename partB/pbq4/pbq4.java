import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
class pbq4{
	ArrayList<String[]> data;
	private void readCsv(String filepath){
		data = new ArrayList<>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
			String s = br.readLine();
			s = br.readLine();
			while(s != null){
				data.add(s.split(","));
				s = br.readLine();
			}
		}catch(Exception e){
			System.out.println("some error has occured");
		}
	}
	private double giniIndex(int colno){
		int totalrows = data.size();
		int totalcols = data.get(0).length;
		HashMap<String,HashMap<String,Integer>> hm = new HashMap<>();
		for(String[] arr : data){
			String key = arr[colno];
			String label = arr[totalcols-1];
			if(hm.containsKey(key)){
				HashMap<String,Integer> inner = new HashMap<>();
				inner = hm.get(key);
				if(inner.containsKey(label)){
					int temp = inner.get(label);
					(hm.get(key)).put(label,++temp);
				}else{
					(hm.get(key)).put(label,1);
				}
			}else{
				HashMap<String,Integer> countmap = new HashMap<>();
				countmap.put(label,1);
				hm.put(key,countmap);
			}
		}
		
		double weightedginicoeff = 0.0;
		Iterator<Map.Entry<String, HashMap<String,Integer>>> parent = hm.entrySet().iterator();
		while(parent.hasNext()){
			Map.Entry<String,HashMap<String,Integer>> parentpair = parent.next();
			String node = (String)parentpair.getKey();
			int denom = 0,numer = 0;
			Iterator<Map.Entry<String,Integer>> child = (parentpair.getValue()).entrySet().iterator();
			while(child.hasNext()){
				Map.Entry<String,Integer> childpair = child.next();
				String classlabel = (String)childpair.getKey();
				int count = (int)childpair.getValue();
				numer += count*count;
				denom += count;
			}
			double ginicoeffnode = 1-(numer)/(denom*denom);
			ginicoeffnode *= denom;
			weightedginicoeff += ginicoeffnode/totalrows;
		}
		
		return weightedginicoeff;
	}
	
	private int bestSplit(){
		int totalcols = data.get(0).length,col=0;
		double mincount = Integer.MAX_VALUE;
		for(int i=0;i<totalcols-1;++i){
			double ginicoeff = giniIndex(i);
			System.out.println("weighted gini index of column " + Integer.toString(i) + " is " + ginicoeff);
			if(mincount > ginicoeff){
				mincount = ginicoeff;
				col=i;
			}
		}
		return col;
	}

	public static void main(String []args){
		pbq4 q4 = new pbq4();
		q4.readCsv("inp.csv");
		for(String[] arr : q4.data){
			for(String s : arr) System.out.print(s+" ");	
			System.out.println();	
		}
		System.out.println("Index of column no. for best split "+Integer.toString(q4.bestSplit()));
	}
}
