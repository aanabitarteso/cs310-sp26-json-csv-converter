package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        // Create the JSON Containers
        JsonObject root = new JsonObject(); //Object with all information 
        JsonArray prodNums = new JsonArray(); //Array to store prodNums
        JsonArray colHeadings = new JsonArray(); //Array for the colHeadings
        JsonArray data = new JsonArray(); //Array for the rest of data
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // Create a CSV Reader and Iterator         
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();            
            Iterator<String[]> iterator = full.iterator();
                        
            // Read the Header row
            String[] header = iterator.next();
            
            for(String col : header) {
                colHeadings.add(col);
            }
            
            // Read the rest of data rows
            while (iterator.hasNext()){
                
                String[] row = iterator.next();
                
                // prodNums is column 0
                prodNums.add(row[0]);
                
                // Store the rest in row array
                JsonArray rowData = new JsonArray();
                
                rowData.add(row[1]); //This is the Title
                rowData.add(Integer.valueOf(row[2])); //This is the Season
                rowData.add(Integer.valueOf(row[3])); //This is the Episode
                rowData.add(row[4]); //This is the Stardate
                rowData.add(row[5]); //This is the Original Airdate
                rowData.add(row[6]); //This is the Remastered Airdate
                
                data.add(rowData); //Add entire row to the data array
            }
            
            //Put the entire JSON object together
            root.put("ProdNums", prodNums);
            root.put("ColHeadings", colHeadings);
            root.put("Data", data);
            
            result = Jsoner.serialize(root);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!

        try {
            
            // Turn JSON into an JsonObject
            JsonObject root = Jsoner.deserialize(jsonString, new JsonObject());
            
            //Extract the components of the object
            JsonArray colHeadings = (JsonArray) root.get("ColHeadings");
            JsonArray prodNums = (JsonArray) root.get("ProdNums");
            JsonArray data = (JsonArray) root.get("Data");
            
            // Create CSV writer
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);
            
            // Write the row of Headers
            String[] header = new String[colHeadings.size()];
            for (int x = 0; x < colHeadings.size(); x++) {
                header[x] = colHeadings.get(x).toString();
            }
            csvWriter.writeNext(header);
            
            
            //Write the rows of Data
            for (int i = 0; i < data.size(); i++) {             
                JsonArray rowData = (JsonArray) data.get(i);
                String[] row = new String[rowData.size() + 1];
                
                // Column 0 is the ProdNums
                row[0] = prodNums.get(i).toString();

                // Rest of columns from the data array
                for (int j = 0; j < rowData.size(); j++) {
                    Object value = rowData.get(j);


                // Season (j == 1) and Episode (j == 2) need zero-padding
                if (value instanceof Number && (j == 2)) {
                    row[j + 1] = String.format("%02d",
                            ((Number) value).intValue());
                }
                else {
                    row[j + 1] = value.toString();
                }       
                } 
                csvWriter.writeNext(row);
            }
         
            csvWriter.close();
            result = writer.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
