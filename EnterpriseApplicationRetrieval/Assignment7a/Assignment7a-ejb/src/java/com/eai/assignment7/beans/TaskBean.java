/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eai.assignment7.beans;

/**
 *
 * @author ANSHUMAN
 */
public class TaskBean {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    String  name;
    Integer  priority;
    Integer  runTime;

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }
    String office;
}
