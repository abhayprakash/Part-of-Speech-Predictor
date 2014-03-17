/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spotlight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Abhay
 */
public class Main {
    private static String rawFilePath = "E:\\Projects\\Cogni\\NLP_ML\\raw.txt";
    private static String trainFilePath = "E:\\Projects\\Cogni\\NLP_ML\\train.txt";
    
    
    
    public static void main(String[] args) throws IOException{
        getTrainFile(rawFilePath, trainFilePath);
        // get a model with this feature file
        // on the fly get feature from test file and predict, for each blank(****) and then replace
    }
    
    static void getTrainFile(String inputFilePath, String outputFilePath) throws IOException{
        File inputFile = new File(inputFilePath);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String actualText;
        
        PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
        
        while ((actualText = br.readLine()) != null) {
            
        }
        br.close();
        writer.close();
    }
    
}
