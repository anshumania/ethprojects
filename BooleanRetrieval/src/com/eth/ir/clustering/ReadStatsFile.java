/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eth.ir.clustering;

import com.eth.ir.spamfiltering.SpamBundle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ANSHUMAN
 */
public class ReadStatsFile {

    static int valueOfK = 1;
    static double totalPurity = 0.0;
    static double totalRandIndex = 0.0;
    static Map<Integer,ClusterMeasure> averageMap = new HashMap<Integer,ClusterMeasure>();
    static int piterationCount = 0;
    static int riterationCount = 0;

    public static void process(String line)
    {
//        System.out.println("processing" + valueOfK);
        if(line.contains("K = "))
        {
            if( valueOfK == 1)
                ++valueOfK;
            else
            {
                    
            double averagePurity = totalPurity / piterationCount;
            double averageRandIndex = totalRandIndex / riterationCount;
                System.out.println("k=" + valueOfK + " iterations= " + piterationCount + ", " + riterationCount);
            averageMap.put(valueOfK, new ClusterMeasure(averageRandIndex, averagePurity));
            ++valueOfK;
            totalPurity = 0.0;
            totalRandIndex = 0.0;
            piterationCount = 0;
            riterationCount = 0;
            }
        }
        if(line.contains("purity="))
        {
                ++piterationCount;
            String purityLine = line.substring("purity=".length(),line.length());
            double purity = Double.parseDouble(purityLine);
            totalPurity += purity;
        }
        if(line.contains("Rand Index = "))
        {
                ++riterationCount;
            String purityLine = line.substring("Rand Index = ".length(),line.length());
            double randIndex = Double.parseDouble(purityLine);
            totalRandIndex += randIndex;
        }
    }

    public static void main(String args[]) {
        String file = SpamBundle.class.getResource(SpamBundle.DOC_CORPUS_DIR).getFile() + "/" + "kmeans-overnight.txt";

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
//                System.out.println("processign");
                process(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("averageMap=" + averageMap);


    }
}
