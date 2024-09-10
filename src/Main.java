import UnsignedInts.UInt8;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory(Assembler.compile("src/program.txt", new UInt8(0)), 256);
        //memory.dump();
        CPU cpu = new CPU(memory);
        //Visualiser visualiser = new Visualiser();
        cpu.run();
    }
}