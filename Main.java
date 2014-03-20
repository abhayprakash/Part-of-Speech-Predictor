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
            String[] words = actualText.split(" ");
            
            for(int i = 0; i < words.length; i++)
            {
                System.out.println("Word is : " + words[i]);
                if((i != words.length - 1) && words[i].equals("****"))
                {
                    System.out.println("2 cont");
                    if(words[i+1].startsWith("****"))
                    {
                        if(i == 0)
                            writer.print("You're your");
                        else
                            writer.print("you're your");
                        
                        List<List<String>> wordPos = getWordPOS(words[i+1]);
                        
                        if(wordPos.get(0).size() != 1)
                            writer.print(wordPos.get(0).get(1));
                        
                        writer.print(" ");
                        
                        i++;
                        continue;
                    }
                }
                
                if(words[i].startsWith("****"))
                {
                    System.out.println("Found ****");
                    
                    String leftFeature = "O";
                    String rightFeature = "O";
                    
                    String leftString, rightString;
                    
                    if(i != 0)
                    {
                        leftString = words[i-1];
                        List<List<String>> wordPos = getWordPOS(leftString);
                        int index = wordPos.get(1).size() - 1;
                        leftFeature = wordPos.get(1).get(index);
                    }
                    
                    rightString = words[i];
                    
                    if(i != words.length - 1)
                    {
                        rightString += words[i+1];
                    }
                    
                    List<List<String>> wordPos = getWordPOS(rightString);
                    
                    if(wordPos.get(1).size() != 1)
                    {
                        rightFeature = wordPos.get(1).get(1);
                    }
                    
                    String likelyPOS = Rb.getResult(leftFeature, rightFeature);
                    if(likelyPOS.equals("PRP$"))
                    {
                        if(words[i].length() == 4)
                        {
                            if(i == 0)
                                writer.write("Your ");
                            else
                                writer.write("your ");
                        }
                        else
                        {
                            if(i == 0)
                                writer.write("Your");
                            else
                                writer.write("your");
                            
                            if(wordPos.get(1).size() != 1)
                                writer.write(wordPos.get(1).get(1)+ " ");
                        }                           
                    }
                    else
                    {
                        if(words[i].length() == 4)
                        {
                            if(i == 0)
                                writer.write("You're ");
                            else
                                writer.write("you're ");
                        }
                        else
                        {
                            if(i == 0)
                                writer.write("You're");
                            else
                                writer.write("you're");
                            
                            if(wordPos.get(1).size() != 1)
                                writer.write(wordPos.get(1).get(1)+ " ");
                        }
                    }
                }
                else
                {
                    writer.print(words[i] + " ");
                }
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
