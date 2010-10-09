/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eth.ir.boolret;

/**
 *
 * @author ANSHUMAN
 */
public class StopWordDictionary extends Dictionary{

   StopWordDictionary(String indexFile)
    {
        super(indexFile);
    }

//   @Override
//   public void addToDictionary(String docId, String key)
//    {
//       if(!StopWords.isStopWord(key.trim()))
//           super.addToDictionary(docId,key);
//    }
}
