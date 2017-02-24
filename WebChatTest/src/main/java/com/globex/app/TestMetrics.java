package com.globex.app;

//import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author michel
 */
public class TestMetrics {
    
    public static Metrics getMetricsTop(long pid){
        
        ProcessBuilder builder = new ProcessBuilder("top", "-b", "-n", "1", "-p", String.valueOf(pid));
        Process proc = null;
        try {
            proc = builder.start();
        } catch (IOException ex) {
            Logger.getLogger(TestMetrics.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {

            String line;

            // Blank line indicates end of summary.
            while ((line = stdin.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
            }
            
            int indexMem = 0;
            int indexCpu = 0;
            int indexVirt = 0;
            int indexRam = 0;

            // Skip header line.
            if (line != null) {
                line = stdin.readLine();
                indexVirt = Arrays.asList(line.split("\\s+")).indexOf("VIRT");
                indexRam = Arrays.asList(line.split("\\s+")).indexOf("RES");
                indexMem = Arrays.asList(line.split("\\s+")).indexOf("%MEM");
                indexCpu = Arrays.asList(line.split("\\s+")).indexOf("%CPU");
            }
            line = stdin.readLine();
            if (line != null) {
                String[] lineArray = line.trim().split("\\s+");
                return new Metrics(lineArray[indexVirt-1],lineArray[indexRam-1],lineArray[indexCpu-1],lineArray[indexMem-1]);
            }
        } catch (IOException ex) {
            Logger.getLogger(TestMetrics.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("NO METRIC");
        return new Metrics();
    }
    
    public static long getProcessPID(Process process){
        long pid = -1;
        try {
          if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            Field f = process.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getLong(process);
            f.setAccessible(false);
          }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
          pid = -1;
        }
        return pid;
    }
    
//    public static double getCPUuse(){
//        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        return Math.round(os.getSystemCpuLoad() * 100.0) / 100.0;
//    }
//    
//    public static double getMemoryUse(){
//        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        return os.getTotalPhysicalMemorySize();
//    }
//    
//    public static void showMetrics(){
//        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//        double cpu_used = operatingSystemMXBean.getProcessCpuLoad();
//        Runtime r = Runtime.getRuntime();
//        double memory_used = ((r.totalMemory() - r.freeMemory())/ r.maxMemory());
//        System.out.println(" -> CPU: "+cpu_used+" %");
//        System.out.println(" -> MEMORY: "+memory_used+" %");
//
//    }
}
