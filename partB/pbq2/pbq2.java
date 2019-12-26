import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Iterator;

class SomeComparator implements Comparator<Pair>{
    public int compare(Pair first,Pair second){
        if(first.b>second.b) return 1;
        else return -1;
    }
}
class Pair{
    String a;
    int b;
    Pair(String a,int b){
        this.a=a;
        this.b=b;
    }
}
public class pbq2{
    ArrayList<String[]> data;

    void updateCsv(){
        try{
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("out.csv"))));
            for(String[] row:data){
                String s = Arrays.toString(row);
                pw.println(s.substring(1,s.length()-1));
            }
            pw.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void readCsv(String filepath){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(filepath)));
            String row = br.readLine();
            data = new ArrayList<>();
            while(row!=null){
                String values[] = row.split(",");
                if(data.size()>0){
                    if(values.length==0){
                        values = new String[data.get(0).length];
                    }else{
                        String temp[] = new String[data.get(0).length];
                        for(int i=0;i<values.length;++i){
                            if(values[i].equals("")) temp[i]=null;
                            else temp[i]=values[i];
                        }
                        values = temp;
                    }
                }
                data.add(values);
                row = br.readLine();
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    void replaceByMean(int colno){
        double mean = 0.0;
        int rowcount=0;
        ArrayList<Integer> index = new ArrayList<>();
        for(int i=0;i<data.size();++i){
            String[] row = data.get(i);
            if(row[colno]!=null){
                mean+=Integer.parseInt(row[colno]);
                rowcount++;
            }else{
                index.add(i);
            }
        }
        mean /= rowcount;
        for(int i=0;i<index.size();++i){
            String[] row = data.get(index.get(i));
            row[colno]=Double.toString(mean);
            data.set(index.get(i),row);
        }
    }

    void replaceByMax(int colno){
        TreeSet<Pair> ts =new TreeSet<Pair>(new SomeComparator());
        ArrayList<Integer> index = new ArrayList<>();
        for(int i=0;i<data.size();++i){
            String[] row = data.get(i);
            if(row[colno]!=null){
                Iterator<Pair> itr = ts.iterator();
                boolean found = false;
                while(itr.hasNext()){
                    Pair p = itr.next();
                    if(p.a.equals(row[colno])){
                        found = true;
                        int prev = p.b;
                        prev+=1;
                        itr.remove();
                        ts.add(new Pair(row[colno],prev));
                        break;
                    }
                }
                if(!found){
                    ts.add(new Pair(row[colno],1));
                }
            }else{
                index.add(i);
            }
        }

        String max = ts.last().a;
        for(int i=0;i<index.size();++i){
            String[] row = data.get(index.get(i));
            row[colno] = max;
            data.set(index.get(i),row);
        }
    }
    public static void main(String[] args){
        pbq2 q2 = new pbq2();
        q2.readCsv("inp.csv");
        q2.replaceByMean(0);
        q2.replaceByMax(1);
        q2.updateCsv();
    }
}