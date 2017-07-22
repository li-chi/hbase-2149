import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class InsertData{

    public static void main(String[] args) throws IOException {

        byte[] large_byte = null;


        large_byte = readBytesFromFile("./small_file");

        //System.out.println("Converted to bytes!");


        // Instantiating Configuration class
        Configuration config = HBaseConfiguration.create();

        // Instantiating HTable class
        HTable hTable = new HTable(config, "emp");

        // Instantiating Put class
        // accepts a row name.
        for (int i=1; i<=5; i++) {
            Put p = new Put(Bytes.toBytes("test"+i)); 
            p.add(Bytes.toBytes("personal"),
                Bytes.toBytes("name"),Arrays.copyOfRange(large_byte, 0, 
                    large_byte.length-ThreadLocalRandom.current().nextInt(1, 10)));
            hTable.put(p);
        }

        long start,end; 
        for (int i=1; i<=300; i++) {
            Put p = new Put(Bytes.toBytes("test"+i)); 

            // adding values using add() method
            // accepts column family name, qualifier/row name ,value
            //LOG.debug("start job"+i+"at "+System.currentTimeMillis());
            p.add(Bytes.toBytes("personal"),
                Bytes.toBytes("name"),Arrays.copyOfRange(large_byte, 0, 
                    large_byte.length-ThreadLocalRandom.current().nextInt(1, 10)));

            start = System.currentTimeMillis();
            // Saving the put Instance to the HTable.
            hTable.put(p);
            end = System.currentTimeMillis();
            System.out.println( i +", "+ (end - start));
            //System.out.println("test"+i+": "+"data inserted");
            //LOG.debug("finish job"+i+"at "+System.currentTimeMillis());

            //System.out.println(System.currentTimeMillis());
        }


      // closing HTable
        hTable.close();

    }

    private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }
}