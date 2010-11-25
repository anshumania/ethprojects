/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.assignment7;

import com.eai.assignment7.beans.TaskBean;
import java.util.List;
import javax.ejb.Local;
import javax.jms.JMSException;

/**
 *
 * @author ANSHUMAN
 */
@Local
public interface CompanySessionLocal {

    public void postTasks(List<TaskBean> taskBeans);
    public void sendMessageToControlOffice(Object messageData,String taskStauts, String officeStatus);
    public void routeTask(String taskStatus, String officeStatus, Object messageData) throws JMSException;
}
