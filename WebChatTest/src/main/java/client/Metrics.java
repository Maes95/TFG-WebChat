package client;

/**
 *
 * @author michel
 */
public class Metrics {
    
    private final double cpu;
    private final double memory;
    private final double virtual;
    private final double ram;
    
    public Metrics(String virtual, String ram,String cpu, String memory){
        this.cpu = parseToDouble(cpu);
        this.memory = parseToDouble(memory);
        this.virtual = parseToDouble(virtual);
        this.ram = parseToDouble(ram);
    }
    
    public Metrics(){
        this.cpu = 0;
        this.memory = 0;
        this.virtual = 0;
        this.ram = 0;
    }
    
    private double parseToDouble(String number){
        // MByte
        if(number.contains("m")){
            number = number.replace("m", "");
            return parseToDouble(number) * 1024;
        }
        // GByte
        if(number.contains("g")){
            number = number.replace("g", "");
            return parseToDouble(number) * 1024 * 1024;
        }
        // KByte
        return Double.valueOf(number.replace(",", "."));
    }
    
    @Override
    public String toString(){
        return "VIRT: "+getVirtual()+" RAM: "+getRam()+" CPU: "+cpu+"%  MEMORY: "+memory+"%";
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

    /**
     * @return the virtual
     */
    public double getVirtual() {
        return virtual;
    }

    /**
     * @return the ram
     */
    public double getRam() {
        return ram;
    }
    
    
}
