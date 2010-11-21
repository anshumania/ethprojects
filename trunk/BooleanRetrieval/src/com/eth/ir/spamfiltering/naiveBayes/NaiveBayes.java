/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.spamfiltering.naiveBayes;

import com.eth.ir.spamfiltering.SpamBundle;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class NaiveBayes {

    NaiveBayesHelper nbHelper;
    public enum sClass { SPAM, NOTSPAM };
    double prior[];
    Map<String,Double> conditionalProbabilityMap;

    NaiveBayes()
    {
        nbHelper = new NaiveBayesHelper();
        prior = new double[sClass.values().length];
        conditionalProbabilityMap = new HashMap<String,Double>();
    }

    public void trainMultinomialNB(String skipDir) //Classification C, DocumentSet D)
    {
//        v<-extractVocabulary(D);
//        n<-countDocs(D);
//        for each c in C
//                do Nc <- countDocsInClass(D,c);
//                prior[c]<-Nc/N
//                textc <- concatenateTextOfALLDocsInClass(D,c)
//                for each t<-V
//                    do Tct <- countTokensOfTerm(textc,t);
//                for each t<-V
//                        do condprob[t][c]<- (Tct + 1) / sigmat* (Tct* + 1)
//        return V, prior, condprob
        
        nbHelper.initializeNBHelper(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile(),skipDir);
        Map<String,Map<sClass,Integer>> vocabulary = nbHelper.extractVocabulary();
        System.out.println("vocabulary="+ vocabulary.size());
        int numDocsTrainingSet  = nbHelper.countDocs();
        System.out.println("numDocsTrainingSet="+numDocsTrainingSet);
        int i=0;

        //double conditionalProbability[][] = new double[vocabulary.size()][sClass.values().length];



        for(sClass sclass : sClass.values())
        {
            
            int numOfDocsInClass = nbHelper.countDocsInClass(sclass);
            System.out.println("numOfDocsInClass = " + sclass + "= " + numOfDocsInClass);

            prior[i] = ((double)numOfDocsInClass / numDocsTrainingSet) ;
            System.out.println("prior[i]" + prior[i]);
            i++;
            // the total number of tokens in that class
            int textc = nbHelper.concatenateTextOfALLDocsInClass(sclass);
            System.out.println("textc=" + textc);
            // the constant B ; the number of terms in the vocabulary
            int noTermsInVocab = vocabulary.size();
            System.out.println("noTermsInVocab=" + noTermsInVocab);
            for(Map.Entry<String,Map<sClass,Integer>> iterator : vocabulary.entrySet())
            {
                Map<sClass,Integer> mapClass = iterator.getValue();
                              
                if(null == mapClass.get(sclass))
                {
                    // this word {iterator.getKey()} doesnt exist in a document for the current class {sclass}
                    //Logger.getLogger(NaiveBayes.class.getName()).log(Level.SEVERE,"{0} is not in any document of {1} class" , new Object[]{iterator.getKey(),sclass});
                    int tct = 0;
//                    System.out.println("for " + iterator.getKey() + "tct=" + tct);
                    double conditionalProbability = ((double)(tct + 1))/ (textc + noTermsInVocab);
//                    System.out.println("connditionalprob=" + conditionalProbability);
                conditionalProbabilityMap.put(iterator.getKey(), conditionalProbability);
                }
                else
                {
                    // tct is the number of occurences of a term t in
                    // in training documents from class c
                int tct = mapClass.get(sclass);
//                    System.out.println("for " + iterator.getKey() + "tct=" + tct);
                double conditionalProbability = ((double)(tct + 1))/ (textc + noTermsInVocab);
//                    System.out.println("connditionalprob=" + conditionalProbability);
                conditionalProbabilityMap.put(iterator.getKey(), conditionalProbability);
                }
            }

        }

//        System.out.println("conditionalprob" + conditionalProbabilityMap);


    }

    public void applyMultinomialNB(String testDir)//C,V,prior,condprob,d)
    {
//        W<-extractTokensFromDoc(V,d)
//        for each c in C
//            do score[c] <- log prior[c]
//                for each t in W
//                    do score[c] +=logcondprob[t][c];
//        return arg max(cinC) score[c]



        File directory = new File(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile() + File.separator + testDir);
        File[] files = directory.listFiles();
        for(File tFile: files)
        {
            if(!tFile.isHidden())
            {

//            System.out.println("working on testfile=" + tFile.getName());
        List<String> tokens = nbHelper.extractTokensFromDoc(tFile);
//        System.out.println("tokensize" + tokens.size());
        double score[] = new double[sClass.values().length];
        int i =0;
        for(sClass sclass : sClass.values())
        {
            
            score[i] = Math.log(prior[i]);
//            score[i] =prior[i];

            for(String token : tokens)
            {
//                System.out.println("for " + token);
                score[i] += Math.log(conditionalProbabilityMap.get(token));
//                score[i] *= conditionalProbabilityMap.get(token);
            }
            i++;
        }
//        System.out.println("spam " + score[0] + " notspam " + score[1]);
        if(score[0] > score[1] )
            System.out.println( tFile.getName() + " is SPAM");
        else
            System.out.println(tFile.getName() + " is NOTSPAM");

        }
        }


        
    }

    public static void main(String args[])
    {
        NaiveBayes nb = new NaiveBayes();

        File directory = new File(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile());
        File[] files = directory.listFiles();
        for(File tFile: files)
        {
            if(!tFile.isHidden())
            {
        nb.trainMultinomialNB(tFile.getName());
        nb.applyMultinomialNB(tFile.getName());
            }
        }
    }

}
