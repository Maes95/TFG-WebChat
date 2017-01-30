
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

/**
 *
 * @author michel
 */
public class WebChatAplication {
    
    private static final String PATH = System.getProperty("user.dir").substring(0,System.getProperty("user.dir").length() - 11);
    
    private final String folderName;
    private final String app;
    
    private Process process;
    
    public WebChatAplication(String app){
        System.out.println("Starting "+app+" application");
        this.app = app;
        this.folderName = app+"-WebChat";
    }
    
    public void run(){
        this.run("./run.sh");
    }
    
    public void run(String... command){
        try {
            process = new ProcessBuilder(command).directory(new File(PATH+folderName)).start();
            Thread.sleep(5000);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    public void showMetrics(){
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpu_used = Math.round(operatingSystemMXBean.getProcessCpuLoad() * 100.0) / 100.0;
        Runtime r = Runtime.getRuntime();
        double memory_used = Math.round(((r.totalMemory() - r.freeMemory())/ r.maxMemory())* 100.0) / 100.0;
        
        System.out.println(" -> CPU: "+cpu_used+" %");
        System.out.println(" -> MEMORY: "+memory_used+" %");
    }
    
    public void destroy(){
        System.out.println("Stopping "+app+" application");
        process.destroy();
    }
    
    public String getAppName(){
        return this.app;
    }
}
