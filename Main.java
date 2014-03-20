/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spotlight;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 *
 * @author Abhay
 */
public class Main {
    private static String rawFilePath = "E:\\Projects\\Cogni\\NLP_ML\\raw.txt";
    private static String trainFilePath = "E:\\Projects\\Cogni\\NLP_ML\\trainstar.txt";
    private static String testFilePath = "E:\\Projects\\Cogni\\NLP_ML\\test.txt";
    private static String resultFilePath = "E:\\Projects\\Cogni\\NLP_ML\\result.txt";
    
    
    static ruleBook Rb = new ruleBook();
    
    public static void main(String[] args) throws IOException{
        getTrainModel(rawFilePath, trainFilePath);
        getResultForTest(testFilePath, resultFilePath);
    }
    
    static void getResultForTest(String testFile, String resultFile) throws IOException
    {
        File inputFile = new File(testFile);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String actualText;
        
        PrintWriter writer = new PrintWriter(resultFile, "UTF-8");
        
        while ((actualText = br.readLine()) != null) {
            System.out.println("test : " + actualText);
            List<List<String>> Word_POS = getWordPOS(actualText);
            
            for(int i = 0; i < Word_POS.get(0).size(); i++)
            {
                String left = "O";
                String right = "O";
                
                String fillThis = Word_POS.get(0).get(i);
                
                System.out.println("word is : " + fillThis);
                
                if(i != Word_POS.get(0).size() - 1)
                {
                    if(Word_POS.get(0).get(i).equals("\\*\\*\\*\\*") && Word_POS.get(0).get(i+1).equals("\\*\\*\\*\\*"))
                    {
                        writer.print("you're your");
                        i++;
                        continue;
                    }
                }
                
                if(Word_POS.get(0).get(i).equals("\\*\\*\\*\\*"))
                {
                    System.out.println("Found ****");
                    if(i != 0)
                    {
                        left = Word_POS.get(1).get(i-1);
                    }
                    if(i != Word_POS.get(1).size()-1)
                    {
                        right = Word_POS.get(1).get(i+1);
                    }

                    System.out.println(left + "::" + right);
                    
                    String likelyPOS = Rb.getResult(left, right);
                    if(likelyPOS.equals("PRP$"))
                        fillThis = "your";
                    else
                        fillThis = "you're";
                }
                
                if(i != 0 && !fillThis.matches("[,.;''\"\":]"));
                    writer.print(" ");
                
                writer.print(fillThis);
            }
            writer.println();
        }
        writer.close();
    }
    
    static void getTrainModel(String inputFilePath, String outputFilePath) throws IOException{
        File inputFile = new File(inputFilePath);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String actualText;
        
        //PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
        
        //int lineNum = 0;
        while ((actualText = br.readLine()) != null) {
            //lineNum++;
            //writer.print(lineNum + " : ");
            List<List<String>> Word_POS = getWordPOS(actualText);
            for(int i = 0; i < Word_POS.get(0).size(); i++)
            {
                String left = "O";
                String right = "O";
                if(Word_POS.get(1).get(i).equals("PRP$"))
                {
                    if(i != 0)
                    {
                        left = Word_POS.get(1).get(i-1);
                    }
                    if(i!=Word_POS.get(1).size()-1)
                    {
                        right = Word_POS.get(1).get(i+1);
                    }
                    //writer.println(left + "*" + right + "*" + "PRP$");
                    Rb.add(left, right, "PRP$");
                }
                else if(Word_POS.get(1).get(i).equals("PRP"))
                {
                    if(i!=Word_POS.get(1).size()-1)
                    {
                        if(Word_POS.get(1).get(i+1).equals("VBP"))
                        {
                            if(i != 0)
                            {
                                left = Word_POS.get(1).get(i-1);
                            }
                            if(i+1 != Word_POS.get(1).size()-1)
                            {
                                right = Word_POS.get(1).get(i+2);
                            }
                            Rb.add(left, right, "PRP_VBP");
                        }
                    }
                }
            }
        }
        br.close();
        //writer.close();
    }
    static List<List<String>> getWordPOS(String s){
        List<List<String>> toRet = new ArrayList<List<String>>();
        toRet.add(new Vector<String>());
        toRet.add(new Vector<String>());
        
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        
        Annotation document = new Annotation(s);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                toRet.get(0).add(word);
                String pos = token.get(PartOfSpeechAnnotation.class);
                toRet.get(1).add(pos);
            }
        }
        return toRet;
    }
}
