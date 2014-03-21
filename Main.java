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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static String rawFilePath = "E:\\Projects\\Cogni\\NLP_ML\\dataSource\\raw.txt";
    private static String trainFilePath = "E:\\Projects\\Cogni\\NLP_ML\\dataSource\\trainRecent.txt";
    private static String testFilePath = "E:\\Projects\\Cogni\\NLP_ML\\dataSource\\test.txt";
    private static String resultFilePath = "E:\\Projects\\Cogni\\NLP_ML\\result.txt";
    
    //static PrintWriter writerDeb;// = new PrintWriter(trainFilePath, "UTF-8");
    
    static ruleBook Rb = new ruleBook();
    
    public static void main(String[] args) throws IOException{
        //writerDeb = new PrintWriter(trainFilePath, "UTF-8");
        getTrainModel(rawFilePath, trainFilePath);
        long startTime = System.currentTimeMillis();
        getResultForTest(testFilePath, resultFilePath);
        long endTime = System.currentTimeMillis();
        System.out.println("Time : " + (endTime - startTime) + " ms");
        //writerDeb.close();
    }
    
    static void getResultForTest(String testFile, String resultFile) throws IOException
    {
        File inputFile = new File(testFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
        String actualText;
        
        PrintWriter writer = new PrintWriter(resultFile, "UTF-8");
        
        int numberOfTest = Integer.parseInt(br.readLine().toString());
        while ((actualText = br.readLine()) != null) {
            if(numberOfTest == 0)
                break;
            numberOfTest--;
            String[] words = actualText.split(" ");
            
            List<List<String>> wordPOS = getWordPOS(actualText);
            
            List<String> featureLeftToThis = new ArrayList<>();
            List<String> featureRightToThis = new ArrayList<>();
            
            //System.out.println("wordPOS size " + wordPOS.size());
            
            for(int i = 0; i < wordPOS.get(0).size(); i++)
            {
                if(wordPOS.get(0).get(i).equals("\\*\\*\\*\\*"))
                {
                    if(i != 0)
                        featureLeftToThis.add(wordPOS.get(1).get(i-1));
                    else
                        featureLeftToThis.add("O");
                    
                    if(i != wordPOS.size()-1)
                        featureRightToThis.add(wordPOS.get(1).get(i+1));
                    else
                        featureRightToThis.add("O");
                }
            }
            
            
            //System.out.println("size or num of **** : " + featureRightToThis.size() + " :: " + featureLeftToThis.size());
            int starIndex = 0;
            
            for(int i = 0; i < words.length; i++)
            {
                //System.out.println("Word is : " + words[i]);
                if((i != words.length - 1) && words[i].equals("****"))
                {
                    if(words[i+1].startsWith("****"))
                    {
                        //System.out.println("2 cont");
                        if(i == 0)
                            writer.print("You're your");
                        else
                            writer.print("you're your");
                        
                        starIndex += 2;
                        
                        List<List<String>> wordPos = getWordPOS(words[i]);
                        
                        if(wordPos.get(0).size() != 1)
                            writer.print(wordPos.get(0).get(1));
                        
                        writer.print(" ");
                        
                        i++;
                        continue;
                    }
                }
                
                if(words[i].contains("****"))
                {
                    //System.out.println("Found **** " + starIndex);
                    
                    String leftChar = "", rightChar = "";
                    
                    /** for getting other stray chars **/
                    List<List<String>> wordPos = getWordPOS(words[i]);
                    
                    if(wordPos.get(0).size() == 3)
                    {
                        leftChar = wordPos.get(0).get(0);
                        rightChar = wordPos.get(0).get(2);
                    }
                    else if(wordPos.get(0).size() == 2)
                    {
                        if(wordPos.get(0).get(0).equals("\\*\\*\\*\\*"))
                            rightChar = wordPos.get(0).get(1);
                        else
                            leftChar = wordPos.get(0).get(0);
                    }
                    
                    /** get leftFeature and rightFeature and predict likelyPOS**/
                    String leftFeature = featureLeftToThis.get(starIndex);
                    String rightFeature = featureRightToThis.get(starIndex);
                    starIndex++;
                    
                    //writerDeb.println("Obtained " + leftFeature + " : " + rightFeature);
                    String likelyPOS = Rb.getResult(leftFeature, rightFeature);
                    
                    /***********************************************/
                    if(likelyPOS.equals("PRP$"))
                    {
                        writer.write(leftChar);
                        
                        if(i == 0)
                            writer.write("Your");
                        else
                            writer.write("your");
                        
                        writer.write(rightChar + " ");
                    }
                    else
                    {
                        writer.write(leftChar);
                        
                        if(i == 0)
                            writer.write("You're");
                        else
                            writer.write("you're");
                        
                        writer.write(rightChar + " ");
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
                        //writerDeb.print(Word_POS.get(0).get(i-1) + " ");
                        left = Word_POS.get(1).get(i-1);
                    }
                    if(i!=Word_POS.get(1).size()-1)
                    {
                        //writerDeb.print(Word_POS.get(0).get(i+1) + " ");
                        
                        right = Word_POS.get(1).get(i+1);
                    }
                    
                    //writerDeb.println(left + "\t" + right + "\t" + "PRP$");
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
                                //writerDeb.print(Word_POS.get(0).get(i-1) + " ");
                                left = Word_POS.get(1).get(i-1);
                            }
                            if(i != Word_POS.get(1).size()-2)
                            {
                                //writerDeb.print(Word_POS.get(0).get(i+2) + " ");
                                right = Word_POS.get(1).get(i+2);
                            }
                            
                            //writerDeb.println(left + "\t" + right + "\t" + "PRP_VBP");
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
