import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import java.util.Scanner;

@Getter
@Setter
public class FileOperation {
    private ArrayList<ArrayList<Integer>> dataList;

    public FileOperation(){
        dataList = new ArrayList<ArrayList<Integer>>();
    }

    public void  readCSV(String fineName) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(fineName));
        sc.useDelimiter("\r\n");   //sets the delimiter pattern
        while (sc.hasNext())  //returns a boolean value
        {
            String next= sc.next();
            String[] tempListString = next.split(",");
            ArrayList<Integer> tempListInt= new ArrayList<Integer>();

            for (String s : tempListString) {
                tempListInt.add(Integer.parseInt(s));
            }

            dataList.add(tempListInt);
        }
        sc.close();  //closes the scanner
    }
}
