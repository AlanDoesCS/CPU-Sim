import UnsignedInts.UInt16;
import UnsignedInts.UInt8;

import java.util.Map;
import java.util.Scanner;

public class CPU {
    double clockSpeed; // Hz
    long instructionDelay; // ms

    Visualiser visualiser;
    Memory RAM;

    ALU alu = new ALU();
    ControlUnit controlUnit = new ControlUnit();

    static class Register {
        private UInt16 value;
        public Register(UInt16 value) {
            this.value = value;
        }
        public void set(UInt16 value) {
            this.value = value;
        }
        public UInt16 get() {
            return value;
        }

        public int getAsInt() {
            return value.getValue();
        }
    }

    Register MAR = new Register(new UInt16(0));
    Register MDR = new Register(new UInt16(0));
    Register PC = new Register(new UInt16(0));
    Register Acc = new Register(new UInt16(0));
    Register IR = new Register(new UInt16(0));

    boolean halted = false;

    public CPU(Memory memory, Visualiser visualiser, double clockSpeed) {
        this.clockSpeed = clockSpeed;
        this.instructionDelay = (long) Math.max(1000 / clockSpeed, 10);
        this.RAM = memory;
        this.visualiser = visualiser;
    }

    public Map<String, Register> getRegisters() {
        return registers;
    }

    Map<String, Register> registers = Map.of(
        "MAR", MAR,
        "MDR", MDR,
        "PC", PC,
        "Acc", Acc
    );

    public class ALU {
        public UInt16 add(UInt16 a, UInt16 b) {
            return a.add(b);
        }

        public UInt16 subtract(UInt16 a, UInt16 b) {
            return a.subtract(b);
        }

        public UInt16 multiply(UInt16 a, UInt16 b) {
            return a.multiply(b);
        }

        public UInt16 divide(UInt16 a, UInt16 b) {
            return a.divide(b);
        }
    }

    public class ControlUnit {
        public void fetchDecodeExecute() {
            // fetch
            MAR.set(PC.get());
            MDR.set(RAM.read(MAR.getAsInt()));
            IR.set(MDR.get());
            PC.set(alu.add(PC.get(), new UInt16(1)));

            // decode
            UInt8 opcode = new UInt8((IR.getAsInt() >> 8) & 0xFF);
            UInt8 operand = new UInt8(IR.getAsInt() & 0xFF);

            String instruction = Assembler.decodeInstruction(opcode, operand);
            visualiser.updateInstruction(instruction);

            // execute
            switch (opcode.getValue()) {
                case 0: // NOP
                    break;
                case 1: // LDA
                    MAR.set(operand.toUInt16());
                    MDR.set(RAM.read(MAR.get().toUInt8()));
                    Acc.set(MDR.get());
                    break;
                case 2: // LDI
                    Acc.set(operand.toUInt16());
                    break;
                case 3: // STA
                    MAR.set(operand.toUInt16());
                    MDR.set(Acc.get());
                    RAM.write(MAR.get().toUInt8(), MDR.get());
                    break;
                case 4: // ADD
                    MAR.set(operand.toUInt16());
                    MDR.set(RAM.read(MAR.get().toUInt8()));
                    Acc.set(alu.add(Acc.get(), MDR.get()));
                    break;
                case 5: // SUB
                    MAR.set(operand.toUInt16());
                    MDR.set(RAM.read(MAR.get().toUInt8()));
                    Acc.set(alu.subtract(Acc.get(), MDR.get()));
                    break;
                case 6: // MUL
                    MAR.set(operand.toUInt16());
                    MDR.set(RAM.read(MAR.get().toUInt8()));
                    Acc.set(alu.multiply(Acc.get(), MDR.get()));
                    break;
                case 7: // DIV
                    MAR.set(operand.toUInt16());
                    MDR.set(RAM.read(MAR.get().toUInt8()));
                    Acc.set(alu.divide(Acc.get(), MDR.get()));
                    break;
                case 8: // JMP
                    PC.set(operand.toUInt16());
                    break;
                case 9: // JGZ
                    if (Acc.getAsInt() > 0) PC.set(operand.toUInt16());
                    break;
                case 10: // JLZ
                    if (Acc.getAsInt() < 0) PC.set(operand.toUInt16());
                    break;
                case 11: // JEZ
                    if (Acc.getAsInt() == 0) PC.set(operand.toUInt16());
                    break;
                case 12: // JNZ
                    if (Acc.getAsInt() != 0) PC.set(operand.toUInt16());
                    break;
                case 13: // INP
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("[Simulation] Input:\t");
                    Acc.set(new UInt16(scanner.nextInt()));
                    break;
                case 14: // OUT
                    System.out.println("[Simulation] Output:\t" + Acc.get());
                    visualiser.updateOutput(Acc.get().toString());
                    break;
                case 15: // HLT
                    halted = true;
                    break;
                default:
                    throw new IllegalStateException("Unknown opcode: " + opcode);
            }

            visualiser.updateMemory(RAM);
            visualiser.updateRegisters(registers);
        }
    }

    public class Cache {
        private int[] data;
        private int size;

        public Cache() {
            this.size = 16;
            this.data = new int[size];
        }

        public int read(int address) { // Simplified
            return data[address % size];
        }

        public void write(int address, int value) { // Simplified
            data[address % size] = value;
        }
    }

    public void run() {
        while (!halted) {
            controlUnit.fetchDecodeExecute();
            try {
                Thread.sleep(instructionDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[CPU]\tCPU halted.");
    }

    public void dumpRegisters() {
        System.out.println("Register dump:");
        for (Map.Entry<String, Register> entry : registers.entrySet()) {
            System.out.printf("%s: %d (0x%04X)%n", entry.getKey(), entry.getValue().getAsInt(), entry.getValue().getAsInt());
        }
    }
}
