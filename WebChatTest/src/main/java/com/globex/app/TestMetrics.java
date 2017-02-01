package com.globex.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 *
 * @author michel
 */
public class TestMetrics {
    
    public static void showMetrics(long pid) throws IOException {
        
        ProcessBuilder builder = new ProcessBuilder("top", "-b", "-n", "1");
        Process proc = builder.start();

        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {

            String line;

            // Blank line indicates end of summary.
            while ((line = stdin.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }
            }

            // Skip header line.
            if (line != null) {
                System.out.println(line);
                line = stdin.readLine();
            }
            
            System.out.println(" PID USUARIO   PR  NI    VIRT    RES    SHR S  %CPU %MEM     HORA+ ORDEN");

            if (line != null) {
                while ((line = stdin.readLine()) != null) {
                    if (line.contains(String.valueOf(pid))) {
                        System.out.println(line);
                        break;
                    }
                }
            }
        }
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
}
