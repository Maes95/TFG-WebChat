/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.globex.app;

/**
 *
 * @author michel
 */
public class Metrics {
    private final double cpu;
    private final double memory;
    
    public Metrics(String cpu, String memory){
        this.cpu = Double.valueOf(cpu.replace(",", "."));
        this.memory = Double.valueOf(memory.replace(",", "."));
    }
    
    public Metrics(){
        this.cpu = 0;
        this.memory = 0;
    }
    
    @Override
    public String toString(){
        return "CPU: "+cpu+"%  MEMORY: "+memory+"%";
    }

    /**
     * @return the cpu
     */
    public double getCpu() {
        return cpu;
    }

    /**
     * @return the memory
     */
    public double getMemory() {
        return memory;
    }
    
    
}
