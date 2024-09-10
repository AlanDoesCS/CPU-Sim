import UnsignedInts.UInt16;
import UnsignedInts.UInt8;

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class Assembler {
    private static final Map<String, UInt8> instructionMap = new HashMap<>(){{
        // Instruction set, built upon: https://ib.compscihub.net/wp-content/uploads/2018/06/2.1.4.pdf
        put("NOP", new UInt8(0));  // No operation
        put("LDA", new UInt8(1));  // Load address into accumulator
        put("LDI", new UInt8(2));  // Load immediate into accumulator
        put("STA", new UInt8(3));  // Store accumulator into address
        put("ADD", new UInt8(4));  // Add accumulator
        put("SUB", new UInt8(5));  // Subtract accumulator
        put("MUL", new UInt8(6));  // Multiply accumulator
        put("DIV", new UInt8(7));  // Divide accumulator
        put("JMP", new UInt8(8));  // Jump
        put("JGZ", new UInt8(9));  // Jump if greater than zero
        put("JLZ", new UInt8(10)); // Jump if less than zero
        put("JEZ", new UInt8(11)); // Jump if equal to zero
        put("JNZ", new UInt8(12)); // Jump if not equal to zero
        put("INP", new UInt8(13)); // Input
        put("OUT", new UInt8(14)); // Output
        put("HLT", new UInt8(15)); // Halt
    }};

    private static final Set<String> instructionSet = instructionMap.keySet();

    // types of instructions
    private static final Set<String> norArgInstructionSet = new HashSet<>(Arrays.asList(
            "NOP", "INP", "OUT", "HLT"
    ));
    private static final Set<String> singleArgInstructionSet = new HashSet<>(Arrays.asList(
            "LDA", "LDI", "STA", "ADD", "SUB", "MUL", "DIV", "JMP", "JGZ", "JLZ", "JEZ", "JNZ"
    ));
    private static final Set<String> takesLabelInstructionSet = new HashSet<>(Arrays.asList(
            "JMP", "JGZ", "JLZ", "JEZ", "JNZ"
    ));

    public static List<UInt16> compile(String path, UInt8 startAddress) {
        List<UInt16> instructions = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(new File(path));
            int lineNumber = 0;

            UInt8 address = startAddress.copy();
            HashMap<String, UInt8> labelAddresses = new HashMap<>(); // 8 bit addresses
            while (scanner.hasNextLine()) {
                lineNumber++;

                String line = scanner.nextLine();
                String[] parts = line.split(" ");

                if (line.startsWith(";") || line.isEmpty()) {
                    continue; // comment or empty line
                } else if (line.endsWith(":")) {
                    labelAddresses.put(line.substring(0, line.length() - 1), address.copy());
                    continue;
                } else if (!instructionSet.contains(parts[0])) {
                    throw new IllegalArgumentException("Invalid instruction: \"" + parts[0] + "\" on line " + lineNumber + " in file " + path + " : "+ line);
                } else if (parts.length == 1) {
                    if (!norArgInstructionSet.contains(parts[0])) {
                        throw new IllegalArgumentException("\"" + parts[0] + "\" takes no arguments. On line " + lineNumber + " in file " + path + " : "+ line);
                    }
                    instructions.add(new UInt16(instructionMap.get(parts[0]).getValue() << 8));
                } else if (parts.length == 2) {
                    if (!singleArgInstructionSet.contains(parts[0])) {
                        throw new IllegalArgumentException("\"" + parts[0] + "\" takes 1 argument. On line " + lineNumber + " in file " + path + " : "+ line);
                    }

                    // check if it is numeric first
                    if (utils.isNumeric(parts[1])) {
                        instructions.add(new UInt16((instructionMap.get(parts[0]).getValue() << 8) | Integer.parseInt(parts[1])));
                    } else if (takesLabelInstructionSet.contains(parts[0])) { // if not numeric, it might be a label
                        if (!labelAddresses.containsKey(parts[1])) {
                            throw new IllegalArgumentException("Label \"" + parts[1] + "\" not found. On line " + lineNumber + " in file " + path + " : "+ line);
                        }
                        instructions.add(new UInt16((instructionMap.get(parts[0]).getValue() << 8) | labelAddresses.get(parts[1]).getValue()));
                    } else {
                        throw new IllegalArgumentException("Invalid instruction: on line " + lineNumber + " in file " + path + " : "+ line);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid instruction: \"" + parts[0] + "\" on line " + lineNumber + " in file " + path + " : "+ line);
                }

                address.setValue(address.add(new UInt8(1)));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("[Assembler] Compilation successful.");
        return instructions;
    }

    public static String decodeInstruction(UInt8 opcode, UInt8 operand) {
        String[] mnemonics = {"NOP", "LDA", "LDI", "STA", "ADD", "SUB", "MUL", "DIV", "JMP", "JGZ", "JLZ", "JEZ", "JNZ", "INP", "OUT", "HLT"};
        return String.format("%s %d", mnemonics[opcode.getValue()], operand.getValue());
    }
}
