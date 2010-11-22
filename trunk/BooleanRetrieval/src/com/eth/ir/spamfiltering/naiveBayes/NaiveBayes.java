package com.eth.ir.spamfiltering.naiveBayes;

import com.eth.ir.spamfiltering.SpamBundle;
import java.io.File;
import java.io.FileFilter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

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
    Map<String, Map<sClass, Double>> conditionalProbabilityMap;

    NaiveBayes() {
        nbHelper = new NaiveBayesHelper();
        prior = new double[sClass.values().length];
        conditionalProbabilityMap = new HashMap<String, Map<sClass, Double>>();
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
//        System.out.println("vocabulary=" + vocabulary.size());
        int numDocsTrainingSet = nbHelper.countDocs();
//        System.out.println("numDocsTrainingSet=" + numDocsTrainingSet);
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

                    Map<sClass, Double> cpm;
                    if (!conditionalProbabilityMap.containsKey(iterator.getKey())) {
                        cpm = new EnumMap<sClass, Double>(sClass.class);
                    } else {
                        cpm = conditionalProbabilityMap.get(iterator.getKey());
                    }

                    cpm.put(sclass, conditionalProbability);

                    conditionalProbabilityMap.put(iterator.getKey(), cpm);
                } else {
                    // tct is the number of occurences of a term t 
                    // in training documents from class c
                    int tct = mapClass.get(sclass);
//                    System.out.println("for " + iterator.getKey() + "tct=" + tct);
                    double conditionalProbability = ((double) (tct + 1)) / (textc + noTermsInVocab);
//                    System.out.println("connditionalprob=" + conditionalProbability);
                    Map<sClass, Double> cpm;
                    if (!conditionalProbabilityMap.containsKey(iterator.getKey())) {
                        cpm = new EnumMap<sClass, Double>(sClass.class);
                    } else {
                        cpm = conditionalProbabilityMap.get(iterator.getKey());
                    }
                    cpm.put(sclass, conditionalProbability);
                    conditionalProbabilityMap.put(iterator.getKey(), cpm);
                }
            }

        }

//        System.out.println("conditionalprob" + conditionalProbabilityMap);


    }

    private class SpamFileFilter implements java.io.FileFilter {

        public boolean accept(File f) {
            if (f.isDirectory() || f.isHidden()) {
                return false;
            }
            String name = f.getName().toLowerCase();
            return name.contains(SpamBundle.SPAM_ID) && name.endsWith("txt");
        }//end accept
    }//

    private class SvnFileFilter implements java.io.FileFilter {

        public boolean accept(File f) {
            if (f.isDirectory() || f.isHidden()) {
                return false;
            }
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


        // rocPlotter maps Sensitivity (recall) to false positive rate
        Map<Double, Double> rocPlotter = new TreeMap<Double, Double>();
        TreeSet<Double> notSpamScores = new TreeSet<Double>();
        Map<String, Double> spamScoresForAllFiles = new HashMap<String, Double>();

        int fileCount = 0;
        //for each file, calculate the spam/non-spam scores
        for (File tFile : files) {
            if (!tFile.isHidden()) {
                fileCount++;
                List<String> tokens = nbHelper.extractTokensFromDoc(tFile);
                double score[] = new double[sClass.values().length];
                int i = 0;
                for (sClass sclass : sClass.values()) {
                    score[i] = Math.log(prior[i]);

                    for (String token : tokens) {
                        Map<sClass, Double> cpm = conditionalProbabilityMap.get(token);
                        score[i] += Math.log(cpm.get(sclass));
                    }
                    i++;
                }
/*              System.out.println("spam " + score[0] + " notspam " + score[1]);

                if (score[0] > score[1]) {
                    System.out.println(tFile.getName() + " is SPAM");
                } else {
                    System.out.println(tFile.getName() + " is NOTSPAM");
                }
*/

                //boolean spam = score[0] > score[1];
                //double score_ratio = score[0] / score[1];
                notSpamScores.add(score[1]);
                //scoreRatioForAllFiles.put(tFile.getName(), score_ratio);
                spamScoresForAllFiles.put(tFile.getName(), score[0]);

                /*
                Map<sClass, Double> scores = new EnumMap<sClass, Double>(sClass.class);
                scores.put(sClass.SPAM, score[0]);
                scores.put(sClass.NOTSPAM, score[1]);
                scoresForAllFiles.put(tFile.getName(), scores);
                */
            }
        }

        for(Double notSpamScore : notSpamScores) {
            //reset counters
            int truePositive = 0;
            int falsePositive = 0;
            int trueNegative = 0;
            int falseNegative = 0;

            for(Entry<String, Double> spamScoreForFile : spamScoresForAllFiles.entrySet()) {
                String filename = spamScoreForFile.getKey();

                //                                    actual value
                //                                   spam  | notspam
                //  predicition outcome      spam  |  tp   |   fp   |
                //                        notspam  |  fn   |   tn   |
                //                                     P        N
                // TPR = tp/P ; FPR = fp/N

                //boolean spam = scoreRatioForFile.getValue() > (ratio - 0.00001);
                boolean spam = spamScoreForFile.getValue() > notSpamScore;

                if (spam) {
                    if (filename.contains(SpamBundle.SPAM_ID)) {
                        truePositive++;
                    } else {
                        falsePositive++;
                    }
                } else {
                    if (filename.contains(SpamBundle.SPAM_ID)) {
                        falseNegative++;
                    } else {
                        trueNegative++;
                    }
                }
            }

            double fpRate = (double) falsePositive / notSpamTotalCount;
            double sensitivity = 0.0;
            if((truePositive > 0 || falseNegative > 0)) {
                sensitivity = (double) truePositive / (truePositive + falseNegative);
            }
            rocPlotter.put(sensitivity, fpRate);
            //System.out.println(rocPlotter);
            //System.out.println("Ratio = " + ratio + ", SPAM FOUND = " + truePositive + " (" + falsePositive + " false positives), (" + falseNegative + " false negatives), TOTAL = " + (truePositive + falsePositive + trueNegative + falseNegative) );
        }


        //System.out.println("rocPlotter" + rocPlotter);
        
        System.out.println("Sensitivity vs False Positive Rate for " + testDir);
        for(Entry<Double, Double> entry : rocPlotter.entrySet()) {
            System.out.println(entry.getKey() + "," + entry.getValue());
        }
        
    }

    public static void main(String args[]) {
        NaiveBayes nb = new NaiveBayes();

        File directory = new File(SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile());
        File[] files = directory.listFiles();
        for (File tFile : files) {
            if (!tFile.isHidden()) {

                // for every run the file passed as parameter is the testing set and
                // all other directories are the training set
System.out.println("Testing directory = " + tFile.getName());
                nb.trainMultinomialNB(tFile.getName());
                nb.applyMultinomialNB(tFile.getName());
break; //for testing
            }
        }
    }
}
