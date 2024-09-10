package UnsignedInts;

public class UInt8 {
    private byte value;

    public UInt8(int value) {
        this.value = (byte) (value & 0xFF);
    }

    public int getValue() {
        return value & 0xFF;
    }

    public void setValue(int value) {
        this.value = (byte) (value & 0xFF);
    }
    public void setValue(UInt8 value) {
        setValue(value.getValue());
    }

    public UInt8 add(UInt8 other) {
        return new UInt8((this.getValue() + other.getValue()) & 0xFF);
    }
    public UInt8 add(int other) {
        return add(new UInt8(other));
    }

    public UInt8 subtract(UInt8 other) {
        return new UInt8((this.getValue() - other.getValue()) & 0xFF);
    }
    public UInt8 subtract(int other) {
        return subtract(new UInt8(other));
    }

    public UInt8 multiply(UInt8 other) {
        return new UInt8((this.getValue() * other.getValue()) & 0xFF);
    }
    public UInt8 multiply(int other) {
        return multiply(new UInt8(other));
    }

    public UInt8 divide(UInt8 other) {
        if (other.getValue() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new UInt8(this.getValue() / other.getValue());
    }
    public UInt8 divide(int other) {
        return divide(new UInt8(other));
    }

    public UInt8 copy() {
        return new UInt8(this.getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    public UInt16 toUInt16() {
        return new UInt16(getValue());
    }
}
