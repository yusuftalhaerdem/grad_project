import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestOut {
    File file;
    public int createFile(String fileName){
        try {
            file = new File(fileName+".txt");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
                return 0;
            } else {
                System.out.println("File already exists.");
                return 1;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }

    }
    public static void fileWriter(String fileName, ArrayList<String> arrayList){
        try {
            FileWriter myWriter = new FileWriter(fileName+".txt");
            //myWriter.flush();
            for (String str : arrayList){
                myWriter.write(str+"\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void pathWriter(String fileName, ArrayList<Integer> arrayList){
        try {
            FileWriter myWriter = new FileWriter(fileName+".txt");
            //myWriter.flush();
            for (Integer no : arrayList){
                myWriter.write(String.valueOf(no)+" ");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("hel");
        list.add("lolo");
        list.add("\n\n23");

        fileWriter("deneme",list);


    }

}
















