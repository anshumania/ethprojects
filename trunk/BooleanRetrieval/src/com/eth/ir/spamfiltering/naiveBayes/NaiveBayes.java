/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.spamfiltering.naiveBayes;

import com.eth.ir.spamfiltering.SpamBundle;
import java.io.File;
import java.io.FileFilter;
import java.util.EnumMap;
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

    public enum sClass {

        SPAM, NOTSPAM
    };
    double prior[];
    Map<String, Map<sClass,Double>> conditionalProbabilityMap;

    NaiveBayes() {
        nbHelper = new NaiveBayesHelper();
        prior = new double[sClass.values().length];
        conditionalProbabilityMap = new HashMap<String, Map<sClass,Double>>();
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

        nbHelper.initializeNBHelper(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile(), skipDir);
        Map<String, Map<sClass, Integer>> vocabulary = nbHelper.extractVocabulary();
        System.out.println("vocabulary=" + vocabulary.size());
        int numDocsTrainingSet = nbHelper.countDocs();
        System.out.println("numDocsTrainingSet=" + numDocsTrainingSet);
        int i = 0;

        //double conditionalProbability[][] = new double[vocabulary.size()][sClass.values().length];



        for (sClass sclass : sClass.values()) {

            int numOfDocsInClass = nbHelper.countDocsInClass(sclass);
//            System.out.println("numOfDocsInClass = " + sclass + "= " + numOfDocsInClass);

            prior[i] = ((double) numOfDocsInClass / numDocsTrainingSet);
//            System.out.println("prior[i]" + prior[i]);
            i++;
            // the total number of tokens in that class
            int textc = nbHelper.concatenateTextOfALLDocsInClass(sclass);
//            System.out.println("textc=" + textc);
            // the constant B ; the number of terms in the vocabulary
            int noTermsInVocab = vocabulary.size();
//            System.out.println("noTermsInVocab=" + noTermsInVocab);
            for (Map.Entry<String, Map<sClass, Integer>> iterator : vocabulary.entrySet()) {
                Map<sClass, Integer> mapClass = iterator.getValue();

                if (null == mapClass.get(sclass)) {
                    // this word {iterator.getKey()} doesnt exist in a document for the current class {sclass}
                    //Logger.getLogger(NaiveBayes.class.getName()).log(Level.SEVERE,"{0} is not in any document of {1} class" , new Object[]{iterator.getKey(),sclass});
                    int tct = 0;
//                    System.out.println("for " + iterator.getKey() + "tct=" + tct);
                    double conditionalProbability = ((double) (tct + 1)) / (textc + noTermsInVocab);
//                    System.out.println("connditionalprob=" + conditionalProbability);
                    
                     Map<sClass,Double> cpm ;
                    if(!conditionalProbabilityMap.containsKey(iterator.getKey()))
                       cpm = new EnumMap<sClass,Double>(sClass.class);
                    else
                       cpm = conditionalProbabilityMap.get(iterator.getKey());

                     cpm.put(sclass, conditionalProbability);

                    conditionalProbabilityMap.put(iterator.getKey(), cpm);
                } else {
                    // tct is the number of occurences of a term t 
                    // in training documents from class c
                    int tct = mapClass.get(sclass);
//                    System.out.println("for " + iterator.getKey() + "tct=" + tct);
                    double conditionalProbability = ((double) (tct + 1)) / (textc + noTermsInVocab);
//                    System.out.println("connditionalprob=" + conditionalProbability);
                    Map<sClass,Double> cpm ;
                    if(!conditionalProbabilityMap.containsKey(iterator.getKey()))
                       cpm = new EnumMap<sClass,Double>(sClass.class);
                    else
                       cpm = conditionalProbabilityMap.get(iterator.getKey());
                    cpm.put(sclass, conditionalProbability);
                    conditionalProbabilityMap.put(iterator.getKey(), cpm);
                }
            }

        }

//        System.out.println("conditionalprob" + conditionalProbabilityMap);


    }

    private class SpamFileFilter implements java.io.FileFilter {
             public boolean accept(File f) {
        if (f.isDirectory() || f.isHidden()) return false;
        String name = f.getName().toLowerCase();
        return name.contains(SpamBundle.SPAM_ID) && name.endsWith("txt");
    }//end accept
}//
    private class SvnFileFilter implements java.io.FileFilter {
             public boolean accept(File f) {
        if (f.isDirectory() || f.isHidden()) return false;
        return true;
    }//end accept
}//

    public void applyMultinomialNB(String testDir)//C,V,prior,condprob,d)
    {
//        W<-extractTokensFromDoc(V,d)
//        for each c in C
//            do score[c] <- log prior[c]
//                for each t in W
//                    do score[c] +=logcondprob[t][c];
//        return arg max(cinC) score[c]


        File directory = new File(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile() + File.separator + testDir);
        FileFilter spamFilter = new SpamFileFilter();
        FileFilter svnFilter = new SvnFileFilter();
        File[] files = directory.listFiles(svnFilter);
        File[] spamFile = directory.listFiles(spamFilter);
        int totalFileCount = files.length;
        int spamTotalCount = spamFile.length;
        int notSpamTotalCount = totalFileCount - spamTotalCount;

        
        // the roc plots of true positives(recall) vs sensitivity(1-false positives)
        Map<Double,Double> rocPlotter = new HashMap<Double,Double>();

        int fileCount = 0;

        for (File tFile : files) {
            if (!tFile.isHidden()) {
            fileCount++;
//            System.out.println("working on testfile=" + tFile.getName());
                List<String> tokens = nbHelper.extractTokensFromDoc(tFile);
//        System.out.println("tokensize" + tokens.size());
                double score[] = new double[sClass.values().length];
                int i = 0;
                for (sClass sclass : sClass.values()) {

                    score[i] = Math.log(prior[i]);
//            score[i] =prior[i];

                    for (String token : tokens) {
//                System.out.println("for " + token);
                        Map<sClass,Double> cpm = conditionalProbabilityMap.get(token);
                        score[i] += Math.log(cpm.get(sclass));
//                        score[i] += Math.log(conditionalProbabilityMap.get(token));
//                score[i] *= conditionalProbabilityMap.get(token);
                    }
                    i++;
                }
//        System.out.println("spam " + score[0] + " notspam " + score[1]);
                if (score[0] > score[1]) {
                    System.out.println(tFile.getName() + " is SPAM");
                } else {
                    System.out.println(tFile.getName() + " is NOTSPAM");
                }


                //TODO generate the ROC Plot
                int truePositive = 0;
                int falsePositive = 0;
                int trueNegative = 0;
                int falseNegative = 0;
                boolean spam = score[0] > score [1];

                //                                    actual value
                //                                   spam  | notspam
                //  predicition outcome      spam  |  tp   |   fp   |
                //                        notspam  |  fn   |   tn   |
                //                                     P        N
                // TPR = tp/P ; FPR = fp/N

                if(spam)
                {
                    if(tFile.getName().contains(SpamBundle.SPAM_ID))
                    {
                        
                        truePositive++;
                        // current True Positive Rate
                        double tpRate = (double)truePositive / spamTotalCount;
                        double fpRate =  (double) falsePositive / notSpamTotalCount;
                        rocPlotter.put(tpRate,fpRate);
                    }
                    else
                    {
                        falsePositive++;
                        // current false Positive Rate
                        double tpRate = (double)truePositive / spamTotalCount;
                        double fpRate =  (double) falsePositive / notSpamTotalCount;
                        rocPlotter.put(tpRate,fpRate);
                    }
                }
                else
                {
                    if(tFile.getName().contains(SpamBundle.SPAM_ID))
                            falseNegative++;
                    else
                        trueNegative++;
                }
            }
        }

        System.out.println("rocPlotter"+rocPlotter);



    }

    public static void main(String args[]) {
        NaiveBayes nb = new NaiveBayes();

        File directory = new File(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile());
        File[] files = directory.listFiles();
        for (File tFile : files) {
            if (!tFile.isHidden()) {

                // for every run the file passed as parameter is the testing set and
                // all other directories are the training set

                nb.trainMultinomialNB(tFile.getName());
                nb.applyMultinomialNB(tFile.getName());
                
            }
        }
    }
}
