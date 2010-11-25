/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.assignment7;

import com.eai.assignment7.beans.TaskBean;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/**
 *
 * @author ANSHUMAN
 */
@Singleton
public class CompanySession implements CompanySessionLocal {
    @Resource(name = "jms/ControlOffice")
    private Queue controlOffice;
    @Resource(name = "jms/ControlOfficeFactory")
    private ConnectionFactory centralOfficeFactory;
    @Resource(name = "jms/CompanyTasks")
    private Topic companyTasks;
    @Resource(name = "jms/CompanyTasksFactory")
    private ConnectionFactory companyTasksFactory;
    
    

     

    public void postTasks(List<TaskBean> taskBeans) {
        try {

            for(TaskBean taskBean : taskBeans)
            {
            sendJMSMessageToCompanyTasks(taskBean);
            }

       
            
        } catch (JMSException ex) {
            Logger.getLogger(CompanySession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void routeTask(String taskStatus, String officeStatus, Object messageData) throws JMSException
    {
        Connection connection = null;
        Session session = null;
        try {
            connection = companyTasksFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(companyTasks);
             TextMessage tm = session.createTextMessage();
             TextMessage trouted = (TextMessage)messageData;
             tm.setJMSPriority(trouted.getJMSPriority());
             tm.setIntProperty("task_runtime", trouted.getIntProperty("task_runtime"));
             tm.setText(trouted.getText());
             tm.setStringProperty("task_status",taskStatus);
             tm.setStringProperty("localOffice_status",officeStatus);
            messageProducer.send(tm);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Message createJMSMessageForjmsCompanyTasks(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();

        TaskBean taskBean = (TaskBean)messageData;
        tm.setJMSPriority(taskBean.getPriority());
        tm.setIntProperty("task_runtime", taskBean.getRunTime());
        tm.setJMSExpiration(taskBean.getRunTime()*10);
        tm.setText(taskBean.getName());
        tm.setStringProperty("task_status", "not_started");
        tm.setStringProperty("localOffice_status", taskBean.getOffice() + "_ASSIGNED");
        System.out.println("created message");
        return tm;
        
    }

    public void sendMessageToControlOffice(Object messageData,String taskStatus, String officeStatus)
    {
        try {
            sendJMSMessageToControlOffice(messageData,taskStatus,officeStatus);
        } catch (JMSException ex) {
            Logger.getLogger(CompanySession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendJMSMessageToCompanyTasks(Object messageData) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            connection = companyTasksFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(companyTasks);
            messageProducer.send(createJMSMessageForjmsCompanyTasks(session, messageData));
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Message createJMSMessageForjmsControlOffice(Session session, Object messageData,String taskStaus, String officeStatus) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        int runtime = ((Message) messageData).getIntProperty("task_runtime");
        System.out.println(runtime + " " + taskStaus + " " + officeStatus);

        tm.setIntProperty("task_runtime",runtime);
        tm.setStringProperty("task_status", taskStaus);
        tm.setStringProperty("localOffice_status", officeStatus);
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToControlOffice(Object messageData,String taskStaus, String officeStatus) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            connection = centralOfficeFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(controlOffice);
            messageProducer.send(createJMSMessageForjmsControlOffice(session, messageData,taskStaus,officeStatus));
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
 
}
