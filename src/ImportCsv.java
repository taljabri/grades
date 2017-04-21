
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.opencsv.CSVReader;

import static java.awt.SystemColor.text;

public class ImportCsv
{
    public static void main(String[] args)
    {
        readCsv();
      //  readCsvUsingLoad();
    }

    private static void readCsv()
    {

        try (CSVReader reader = new CSVReader(new FileReader("MOCK_DATA.csv"), ','); Connection connection = DBConnection.getConnection();)
        {
            String insertQuery = "Insert into student (student_id, student_name) values (?,?)";
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
            String[] rowData = null;
            int i = 0;
            while((rowData = reader.readNext()) != null){
                for (String data : rowData)
                {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
                  //  pstmt.setString((i % 2) + 1, String.valueOf(hash));
                    pstmt.setBytes((i % 2) + 1, hash);

                    if (++i % 2 == 0)
                        pstmt.addBatch();// add batch

                    if (i % 20 == 0)// insert when the batch size is 10
                        pstmt.executeBatch();
                }}
            System.out.println("Data Successfully Uploaded");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void readCsvUsingLoad()
    {
        try (Connection connection = DBConnection.getConnection())
        {

            String loadQuery = "LOAD DATA LOCAL INFILE '" + "MOCK_DATA.csv" + "' INTO TABLE student FIELDS TERMINATED BY ','"
                    + " LINES TERMINATED BY '\n' (student_id, student_name) ";
            System.out.println(loadQuery);
            Statement stmt = connection.createStatement();
            stmt.execute(loadQuery);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }






}