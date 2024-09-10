import UnsignedInts.UInt16;
import UnsignedInts.UInt8;

import java.util.List;

public class Memory {
    private UInt16[] memory;
    private final int size;

    public Memory(List<UInt16> memory, int size) {
        this.size = size;
        this.memory = new UInt16[size];

        int i = 0;
        while (i < memory.size()) {
            this.memory[i] = memory.get(i);
            i++;
        }
        if (i < size) {
            while (i < size) {
                this.memory[i] = new UInt16(0);
                i++;
            }
        }
    }

    public void write(UInt8 address, UInt16 value) {
        int addr = address.getValue();
        if (addr >= 0 && addr < size) {
            memory[addr] = value;
        } else {
            throw new ArrayIndexOutOfBoundsException("Memory write access out of bounds: " + addr);
        }
    }
    public UInt16 read(int address) {
        if (address >= 0 && address < size) {
            return memory[address];
        } else {
            throw new ArrayIndexOutOfBoundsException("Memory read access out of bounds: " + address);
        }
    }
    public UInt16 read(UInt8 address) {
        return memory[address.getValue()];
    }

    public int getSize() {
        return size;
    }

    public void dump() {
        for (int i = 0; i < size; i++) {
            System.out.println(i + "\t: " + memory[i] + "\t: " + String.format("%16s", Integer.toBinaryString(memory[i].getValue())).replace(' ', '0'));
        }
    }
}
