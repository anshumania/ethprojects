/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.assignment7;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ANSHUMAN
 */
public class TaskWork implements Runnable {

    String office;
    long runTime;

    TaskWork(String office, long taskTime) {
        this.office = office;
        this.runTime = taskTime;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.office + " starting work");
            if (office.equals("office1")) {
                synchronized (LocalOfficeOnez.lock) {
                    LocalOfficeOnez.busy = true;
                }
            } else if (office.equals("office2")) {
                synchronized (LocalOfficeTwoz.lock) {
                    LocalOfficeTwoz.busy = true;
                }
            }

            Thread.currentThread().sleep(runTime);
            System.out.println(this.office + " finished work");

            if (office.equals("office1")) {
                synchronized (LocalOfficeOnez.lock) {
                    LocalOfficeOnez.busy = false;
                }
            } else if (office.equals("office1")) {
                synchronized (LocalOfficeTwoz.lock) {
                    LocalOfficeTwoz.busy = false;
                }
            }

            } catch  (InterruptedException ex) {
            Logger.getLogger(TaskWork.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
