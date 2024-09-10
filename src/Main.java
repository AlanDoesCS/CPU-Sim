import UnsignedInts.UInt8;

public class Main {
    public static void main(String[] args) {
        Memory memory = new Memory(Assembler.compile("src/program.txt", new UInt8(0)), 256);
        //memory.dump(); // Uncomment to see the program in binary
        Visualiser visualiser = new Visualiser();
        CPU cpu = new CPU(memory, visualiser, 1); // 1 is the speed of the CPU (Hz)

        visualiser.updateMemory(memory);
        visualiser.updateRegisters(cpu.getRegisters());

        cpu.run();
    }
}