import UnsignedInts.UInt16;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Visualiser extends JFrame {
    private JTextArea registerArea;
    private JTextArea instructionArea;
    private JTextArea outputArea;
    private CPUDiagramPanel diagramPanel;

    public Visualiser() {
        setTitle("CPU Visualiser");
        setSize(1280, 720);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        diagramPanel = new CPUDiagramPanel();
        add(diagramPanel, BorderLayout.NORTH);

        JPanel textAreasPanel = new JPanel(new GridLayout(1, 3));
        registerArea = new JTextArea();
        registerArea.setEditable(false);
        textAreasPanel.add(new JScrollPane(registerArea));

        instructionArea = new JTextArea();
        instructionArea.setEditable(false);
        textAreasPanel.add(new JScrollPane(instructionArea));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        textAreasPanel.add(new JScrollPane(outputArea));

        add(textAreasPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void updateMemory(Memory memory) {
        StringBuilder sb = new StringBuilder();
        sb.append("Memory:\n");
        for (int i = 0; i < memory.getSize(); i++) {
            sb.append(String.format("%03d: %s\n", i, memory.read(i)));
        }
        diagramPanel.updateMemory(memory);
    }

    public void updateOutput(String newOutput) {
        outputArea.append(newOutput + "\n");
    }

    public void updateRegisters(Map<String, CPU.Register> registers) {
        StringBuilder sb = new StringBuilder();
        sb.append("Registers:\n");
        for (Map.Entry<String, CPU.Register> entry : registers.entrySet()) {
            sb.append(String.format("%s: %d (0x%04X)\n", entry.getKey(), entry.getValue().getAsInt(), entry.getValue().getAsInt()));
        }
        registerArea.setText(sb.toString());
        diagramPanel.updateRegisters(registers);
    }

    public void updateInstruction(String instruction) {
        instructionArea.append(instruction + "\n");
        diagramPanel.setCurrentInstruction(instruction);
    }

    private class CPUDiagramPanel extends JPanel {
        private int PADDING = 10;

        private Map<String, CPU.Register> registers;
        private String currentInstruction;
        private List<MemoryEntry> memoryEntries;

        public CPUDiagramPanel() {
            setPreferredSize(new Dimension(1280, 300));
            memoryEntries = new ArrayList<>();
        }

        public void updateRegisters(Map<String, CPU.Register> registers) {
            this.registers = registers;
            repaint();
        }

        public void setCurrentInstruction(String instruction) {
            this.currentInstruction = instruction;
            repaint();
        }

        public void updateMemory(Memory memory) {
            memoryEntries.clear();
            for (int i = 0; i < memory.getSize(); i++) {
                UInt16 value = memory.read(i);
                if (value.getValue() != 0) {
                    memoryEntries.add(new MemoryEntry(i, value));
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int CPU_WIDTH = width/4 - 2*PADDING;
            int CPU_HEIGHT = height - 2*PADDING;

            int ALU_WIDTH = CPU_WIDTH/2 - 2*PADDING;
            int ALU_HEIGHT = CPU_HEIGHT/2 - 2*PADDING;

            int CU_WIDTH = ALU_WIDTH;
            int CU_HEIGHT = ALU_HEIGHT;

            int CACHE_WIDTH = (int) (ALU_WIDTH*1.5);
            int CACHE_HEIGHT = ALU_HEIGHT/2;
            int CACHE_X = width/2 - CACHE_WIDTH/2;
            int CACHE_Y = 3*PADDING+4*PADDING;
            int CACHE_LEFT = CACHE_X;
            int CACHE_RIGHT = CACHE_X +CACHE_WIDTH;

            int MEMORY_LEFT = width * 2/3 + PADDING;

            // Draw CPU outline
            g2d.setColor(Color.GRAY);
            g2d.fillRect(PADDING, PADDING, CPU_WIDTH, CPU_HEIGHT);
            g2d.setColor(Color.WHITE);
            g2d.drawString("CPU", 20, 30);

            // Draw ALU
            g2d.setColor(Color.CYAN);
            g2d.fillRect(2*PADDING, 2*PADDING, ALU_WIDTH, ALU_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString("ALU", PADDING + ALU_WIDTH/2, PADDING + ALU_HEIGHT/2);

            // Draw Control Unit (CU) - Moved to the bottom
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(3*PADDING+ALU_WIDTH, 3*PADDING+ALU_HEIGHT, CU_WIDTH, CU_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString("CU", 8*PADDING+ALU_WIDTH, height - 80);

            // Draw MAR
            g2d.setColor(Color.GREEN);
            g2d.fillRect(3*PADDING+ALU_WIDTH, 2*PADDING, ALU_WIDTH+PADDING, 4*PADDING);
            g2d.setColor(Color.BLACK);
            g2d.drawString("MAR", (int) (ALU_WIDTH*1.5), 3*PADDING);
            if (registers != null && registers.containsKey("MAR")) {
                g2d.drawString(String.valueOf(registers.get("MAR").getAsInt()), (int) (ALU_WIDTH*1.5), 5*PADDING);
            }

            // Draw MDR
            g2d.setColor(Color.RED);
            g2d.fillRect(3*PADDING+ALU_WIDTH, 3*PADDING+4*PADDING, ALU_WIDTH+PADDING, 4*PADDING);
            g2d.setColor(Color.BLACK);
            g2d.drawString("MDR", (int) (ALU_WIDTH*1.5), 3*PADDING+5*PADDING);
            if (registers != null && registers.containsKey("MDR")) {
                g2d.drawString(String.valueOf(registers.get("MDR").getAsInt()), (int) (ALU_WIDTH*1.5), 3*PADDING+7*PADDING);
            }

            // Draw Acc (moved next to ALU)
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(2*PADDING, 3*PADDING+ALU_WIDTH, ALU_WIDTH, 4*PADDING);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Acc", PADDING + ALU_WIDTH/2, 175);
            if (registers != null && registers.containsKey("Acc")) {
                g2d.drawString(String.valueOf(registers.get("Acc").getAsInt()), PADDING + ALU_WIDTH/2, 195);
            }

            // Draw PC (moved below Acc)
            g2d.setColor(Color.PINK);
            g2d.fillRect(2*PADDING, 4*PADDING+ALU_WIDTH+4*PADDING, ALU_WIDTH, 40);
            g2d.setColor(Color.BLACK);
            g2d.drawString("PC", PADDING + ALU_WIDTH/2, 225);
            if (registers != null && registers.containsKey("PC")) {
                g2d.drawString(String.valueOf(registers.get("PC").getAsInt()), PADDING + ALU_WIDTH/2, 245);
            }

            // Draw RAM
            g2d.setColor(Color.GREEN);
            g2d.fillRect(width * 2/3 + 10, 10, width/3 - 20, height - 20);
            g2d.setColor(Color.BLACK);
            g2d.drawString("RAM (Primary Memory)", width * 2/3 + 20, 30);

            // Draw memory contents in multiple columns
            if (memoryEntries != null) {
                int columnWidth = 120;
                int columns = (width/3 - 40) / columnWidth;
                int rowHeight = 20;
                int rows = (height - 50) / rowHeight;

                for (int i = 0; i < memoryEntries.size(); i++) {
                    MemoryEntry entry = memoryEntries.get(i);
                    int column = i / rows;
                    int row = i % rows;

                    if (column < columns) {
                        int x = width * 2/3 + 20 + column * columnWidth;
                        int y = 50 + row * rowHeight;
                        g2d.drawString(String.format("%03d: %s", entry.address, entry.value), x, y);
                    }
                }
            }

            // Draw Cache
            g2d.setColor(Color.GREEN.brighter());
            g2d.fillRect(CACHE_X, CACHE_Y, CACHE_WIDTH, CACHE_HEIGHT);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Cache", CACHE_X+9*PADDING, CACHE_Y+CACHE_HEIGHT/2-PADDING);

            // Draw buses
            g2d.setColor(Color.RED);
            g2d.drawLine(PADDING+CPU_WIDTH, 60, width * 2/3 + 10, 60);
            g2d.drawString("Address Bus", width/2, 50);

            g2d.setColor(Color.BLUE);
            g2d.drawLine(CPU_WIDTH, 3*PADDING+6*PADDING, CACHE_LEFT, 3*PADDING+6*PADDING);
            g2d.drawLine(CACHE_RIGHT, 3*PADDING+6*PADDING, width * 2/3 + PADDING, 3*PADDING+6*PADDING);
            g2d.drawString("Data Bus", PADDING+CPU_WIDTH + (CACHE_LEFT-(PADDING+CPU_WIDTH))/2, 105);
            g2d.drawString("Data Bus", CACHE_RIGHT + (MEMORY_LEFT-CACHE_RIGHT)/2, 105);

            g2d.setColor(Color.RED);
            g2d.drawLine(PADDING+CPU_WIDTH, height - 80, width * 2/3 + 10, height - 80);
            g2d.drawString("Control Bus", width/2, height - 90);

            // Draw current instruction
            if (currentInstruction != null) {
                g2d.setColor(Color.BLACK);
                g2d.drawString("Current Instruction: " + currentInstruction, CACHE_X+PADDING, height - 10);
            }
        }

        private class MemoryEntry {
            int address;
            UInt16 value;

            MemoryEntry(int address, UInt16 value) {
                this.address = address;
                this.value = value;
            }
        }
    }
}