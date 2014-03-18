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
    private static String trainFilePath = "E:\\Projects\\Cogni\\NLP_ML\\train.txt";
    
    
    
    public static void main(String[] args) throws IOException{
        getTrainFile(rawFilePath, trainFilePath);
        //get a model with this feature file
        
        // on the fly get feature from test file and predict, for each blank(****) and then replace acc.
    }
    
    static void getTrainFile(String inputFilePath, String outputFilePath) throws IOException{
        File inputFile = new File(inputFilePath);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String actualText;
        
        PrintWriter writer = new PrintWriter(outputFilePath, "UTF-8");
        
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
                    writer.println(left + "\t" + right + "\t" + "PRP$");
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
                            writer.println(left + "\t" + right + "\t" + "PRP_VBP");
                        }
                    }
                }
            }
        }
        br.close();
        writer.close();
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
