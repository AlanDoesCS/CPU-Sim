package UnsignedInts;

public class UInt16 {
    private short value;

    public UInt16(int value) {
        this.value = (short) (value & 0xFFFF);
    }

    public int getValue() {
        return value & 0xFFFF;
    }

    public void setValue(int value) {
        this.value = (short) (value & 0xFFFF);
    }

    public UInt16 add(UInt16 other) {
        return new UInt16((this.getValue() + other.getValue()) & 0xFFFF);
    }
    public UInt16 add(int other) {
        return add(new UInt16(other));
    }

    public UInt16 subtract(UInt16 other) {
        return new UInt16((this.getValue() - other.getValue()) & 0xFFFF);
    }
    public UInt16 subtract(int other) {
        return subtract(new UInt16(other));
    }

    public UInt16 multiply(UInt16 other) {
        return new UInt16((this.getValue() * other.getValue()) & 0xFFFF);
    }
    public UInt16 multiply(int other) {
        return multiply(new UInt16(other));
    }

    public UInt16 divide(UInt16 other) {
        if (other.getValue() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new UInt16(this.getValue() / other.getValue());
    }
    public UInt16 divide(int other) {
        return divide(new UInt16(other));
    }

    public UInt16 copy() {
        return new UInt16(this.getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    public UInt8 toUInt8() {
        return new UInt8(getValue());
    }
}
