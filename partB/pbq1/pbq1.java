import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

public class pbq1{
    ArrayList<String[]> data;
    static Scanner sc = new Scanner(System.in);

    ArrayList<String[]> getData(String ...cols){
        ArrayList<String[]> rows = new ArrayList<>();
        if(data.size()>0){
            int colno[] = new int[cols.length];
            for(int i=0;i<data.get(0).length;++i){
                for(int j=0;j<cols.length;++j){
                    if(cols[j].equals(data.get(0)[i])){
                        colno[j]=i;
                    }
                }
            }
            for(int i=1;i<data.size();++i){
                String row[] = new String[colno.length];
                for(int j=0;j<colno.length;++j){
                    row[j] = data.get(i)[colno[j]];
                }
                rows.add(row);
            }
        }
        return rows;
    }

    void aggr_discre_sample(String colname,boolean discrete,boolean numeric){
        ArrayList<String[]> column = getData(colname);
        System.out.println("Aggregate of "+colname+((discrete)?(" ,discrete"):(" ,continuous"))+((numeric)?(" ,numeric"):(" ,text")));
        if(discrete){
            HashMap<String,Integer> hm = new HashMap<>();
            for(String row[]:column){
                if(hm.containsKey(row[0])){
                    int temp = hm.get(row[0]);
                    hm.put(row[0],temp+1);
                }else{
                    hm.put(row[0],1);
                }
            }
            System.out.println("frequency of each class:");
            for(String key:hm.keySet()){
                System.out.println(key+" : "+hm.get(key));
            }
            System.out.println("want to sample? (y/n)");
            String ans = sc.next();
            if(ans.equals("y") | ans.equals("Y")){
                System.out.println("Enter no. of desired samples");
                int sampleSize = sc.nextInt();
                HashMap<Integer,Integer> randomKeys = new HashMap<>();
                Random rand = new Random();
                for(String key:hm.keySet()){
                    for(int i=0;i<(sampleSize*hm.get(key)/(data.size()-1));++i){
                        int randKey = rand.nextInt(data.size()-1)+1;
                        while(column.get(randKey-1).equals(key) && randomKeys.containsKey(randKey)){
                            randKey = rand.nextInt(data.size()-1)+1;
                        }
                        randomKeys.put(randKey,1);
                        for(String cell:data.get(randKey)) System.out.print(", "+cell);
                        System.out.println();
                    }
                }
            }
        }else{
            if(numeric){
               double max=Double.MIN_VALUE,min=Double.MAX_VALUE;
               double avg=0.0;
               for(String row[]:column){
                   if(Double.parseDouble(row[0])>max) max=Double.parseDouble(row[0]);
                   if(Double.parseDouble(row[0])<min) min=Double.parseDouble(row[0]);
                   avg += Double.parseDouble(row[0]);
               }
               avg /= column.size();
               System.out.println("Minimum value: "+min);
               System.out.println("Maximum value: "+max);
               System.out.println("Average: "+avg);
               System.out.println("want to discretize? (y/n)");
               String ans = pbq1.sc.next();
               if(ans.equals("y") || ans.equals("Y")){
                    try{
                        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("titanic_"+colname+"_discretized.csv")));
                        String prevData = Arrays.toString(data.get(0));
                        bw.write(prevData.substring(1,prevData.length()-1)+","+colname+"_discretized"+"\n");
                        for(int i=0;i<column.size();++i){
                            String newRow="";
                            if(Double.parseDouble(column.get(i)[0])>=min && Double.parseDouble(column.get(i)[0])<avg){
                                prevData = Arrays.toString(data.get(i+1));
                                newRow = prevData.substring(1,prevData.length()-1)+","+Integer.toString((int)(min+avg)/2);
                            }
                            if(Double.parseDouble(column.get(i)[0])>=avg && Double.parseDouble(column.get(i)[0])<=max){
                                prevData = Arrays.toString(data.get(i+1));
                                newRow = prevData.substring(1,prevData.length()-1)+","+Integer.toString((int)(max+avg)/2);
                            }
                        bw.write(newRow+"\n");
                        }
                        bw.close();
                    }catch(Exception e){
                       e.printStackTrace();
                    }
               }
            }
        }

    }

    void readCsv(String filepath,String fieldDelimiter){
        try{
            BufferedReader br =  new BufferedReader(new FileReader(new File(filepath)));
            String row = br.readLine();
            data = new ArrayList<>();
            while(row != null){
                data.add(row.split(fieldDelimiter));
                row = br.readLine();
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        pbq1 q1 = new pbq1();
        q1.readCsv("titanic.csv",",");
        q1.aggr_discre_sample("Survived",true,true);
        q1.aggr_discre_sample("Pclass",true,true);
        q1.aggr_discre_sample("Age",false,true);
        q1.aggr_discre_sample("Fare",false,true);
    }
}
