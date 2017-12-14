package Databases;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private List<Data> arrayList;

    public Controller(){
        try {
            System.out.println("Reading from data.ser");
            FileInputStream fileIn = new FileInputStream("./data.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            arrayList = (ArrayList<Data>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            System.out.println("No Data Found. Creating A new File");
            arrayList = new ArrayList<>(10);
            arrayList.add(0,null);
            serialize(arrayList);
        }
    }

    public List<Data> getArrayList() {
        return arrayList;
    }

    public void serialize(List<Data> databases){
        try {
            FileOutputStream fileOut =
                    new FileOutputStream("./data.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(databases);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in ./data.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }
}
