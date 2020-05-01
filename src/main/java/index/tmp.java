package index;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

public class tmp {

    public static void main(String[] args) {

        String csvFile = "D:\\eclipse newest\\New folder\\eclipse\\exe\\hello\\1.csv";

        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                System.out.println("Country [id= " );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
