import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Arrays;

public class pbq3{
    ArrayList<String[]> data;
    ArrayList<String> items;

    HashMap<String[],Integer>freqItemset(double minsup){
        int size = items.size();
        HashMap<String[],Integer> freq = new HashMap<>();

        for(int i=1;i<(1<<size);++i){

            ArrayList<Integer> bits = new ArrayList<>();
            for(int j=0;j<size;++j){
                if((i & (1<<j))>0){
                    bits.add(j);
                }
            }

            int itemsetCount=0;
            for(String[] transaction:data){
                int foundall = 0;
                for(int j=0;j<bits.size();++j){
                    for(String item:transaction){
                        if(item.equals(items.get(bits.get(j)))){
                            foundall++;
                            break;
                        }
                    }
                }
                if(foundall == bits.size()) itemsetCount++;
            }

            if(itemsetCount >= minsup){
                String[] cell = new String[bits.size()];
                for(int j=0;j<bits.size();++j) cell[j]=items.get(bits.get(j));
                freq.put(cell,itemsetCount);
            }

        }
        return freq;
    }

    HashMap<String,Double> genRules(HashMap<String[],Integer> freq,double minconf){
        HashMap<String,Double> rules = new HashMap<>();
        for(String[] itemset:freq.keySet()){
            if(itemset.length>1){

                for(int i=1;i<(1<<(itemset.length))-1;++i){
                    ArrayList<Integer> bitset = new ArrayList<>();
                    for(int j=0;j<itemset.length;++j){
                        if((i&(1<<j))>0){
                            bitset.add(j);
                        }
                    }
                    String[] left = new String[bitset.size()];
                    for(int j=0;j<bitset.size();++j) left[j] = itemset[bitset.get(j)];
                    for(String[] dummy:freq.keySet()){      //hashmap find array with address not content
                        if(Arrays.equals(dummy,left)) left=dummy;
                    }
                    int leftsup = freq.get(left);
                    if((freq.get(itemset)/leftsup)>=minconf){
                        String rule = "";
                        for(int j=0;j<left.length;++j) rule += left[j]+",";
                        rule += "->";
                        for(int j=0;j<itemset.length;++j){
                            if(!bitset.contains(j)) rule += itemset[j]+",";
                        }
                        rules.put(rule,(freq.get(itemset)/(double)leftsup));
                    }
                }
            }
        }
        return rules;
    }
    void readCsv(String filepath){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            data = new ArrayList<>();
            TreeSet<String> ts = new TreeSet<>();
            String row = br.readLine();
            while(row!=null){
                data.add(row.split(","));
                for(String s:row.split(",")) ts.add(s);
                row=br.readLine();
            }
            items = new ArrayList<>();
            Iterator<String> itr = ts.iterator();
            while(itr.hasNext()){
                items.add(itr.next());
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public static void main(String[] args){
        pbq3 q3 = new pbq3();
        q3.readCsv("inp.csv");
        HashMap<String[],Integer> freq = q3.freqItemset(q3.data.size()*0.4);
        System.out.println("Frequent itemsets:");
        for(String[] itemset:freq.keySet()){
            for(String item:itemset)System.out.print(item+",");
            System.out.println(" support count:"+freq.get(itemset));
        }
        HashMap<String,Double> rules = q3.genRules(freq,0.6);
        for(String rule:rules.keySet()) System.out.println(rule+" confidence:"+rules.get(rule));
    }
}