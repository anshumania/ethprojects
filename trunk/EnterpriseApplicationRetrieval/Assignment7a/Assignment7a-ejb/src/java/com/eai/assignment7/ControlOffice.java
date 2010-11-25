/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.assignment7;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 * @author ANSHUMAN
 */
@MessageDriven(mappedName = "jms/ControlOffice", activationConfig =  {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
public class ControlOffice implements MessageListener {
    @EJB
    private CompanySessionLocal companySession;
    
    public ControlOffice() {
    }

    public void onMessage(Message message) {
        try {
            System.out.println("receivedinControlOffice");
            String task_status = message.getStringProperty("task_status");
            String office_status = message.getStringProperty("localOffice_status");
            System.out.println("Control Office log " + task_status + " | " + office_status);

            analyseAndRoute(task_status,office_status,message);

        } catch (JMSException ex) {
            Logger.getLogger(ControlOffice.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void analyseAndRoute(String taskStatus, String officeStatus, Message message)
    {
        if((taskStatus.equals("not_started")) && officeStatus.contains("AVAILABLE"))
        {
            try {
                System.out.println("There is an office available and task is not started");
                taskStatus = "routed";
                officeStatus = officeStatus.substring(0, officeStatus.indexOf("_"));
                
                companySession.routeTask(taskStatus, officeStatus, message);
            } catch (JMSException ex) {
                Logger.getLogger(ControlOffice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        
    }
    
}
