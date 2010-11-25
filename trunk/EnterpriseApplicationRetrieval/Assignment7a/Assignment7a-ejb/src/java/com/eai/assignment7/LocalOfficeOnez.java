/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eai.assignment7;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author ANSHUMAN
 */
@MessageDriven(mappedName = "jms/CompanyTasks", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "LocalOfficeOnez"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "LocalOfficeOnez")
})
public class LocalOfficeOnez implements MessageListener {

    @EJB
    private CompanySessionLocal companySession;
    static boolean busy = false;
    final static Object lock = new Object();

    public LocalOfficeOnez() {
    }

    public void onMessage(Message message) {
        System.out.println("received in office1");

        try {

            String taskStatus = message.getStringProperty("task_status");
            String localOfficeStatus = message.getStringProperty("localOffice_status");
            if (localOfficeStatus.contains("office1")) {
                System.out.println("LOCAL OFFICE | " + taskStatus + " | busy=" + busy + " | " + localOfficeStatus );
                if (!busy) {
                    if (taskStatus.equals("routed")) {
                        int runTime = message.getIntProperty("task_runtime");
                        doWork(runTime);
//              message.setJMSCorrelationID("task_FinishingIn_"+runTime);
                        taskStatus = "Finishing in " + runTime;
//              message.setStringProperty("task_status", "Finishing in " + runTime);
                    } else {
                        if (taskStatus.equals("not_started")) {
                            localOfficeStatus = "office1_AVAILABLE";
                        }
                        //message.setStringProperty("localOffice_status", "office1_AVAILABLE");
                    }
                } else {
                    localOfficeStatus = "office1_BUSY";
                }
//                message.setStringProperty("localOffice_status", "office1_BUSY");


                companySession.sendMessageToControlOffice(message, taskStatus, localOfficeStatus);
            }

        } catch (JMSException ex) {
            Logger.getLogger(LocalOfficeOnez.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void doWork(long runTime) {
        Runnable runWork = new TaskWork("office1", runTime);
        Thread threadWork = new Thread(runWork);
        threadWork.start();
    }
}
