import java.util.Arrays;

public class hello {
    public static void main(String[] args) {

        byte a = (byte) 0xc1;
        byte b = (byte) 0x01;
        System.out.println((byte) 0x32 ^  (byte) 0xa8 ^ Mult((byte) 0x43,(byte) 0x02) ^ Mult((byte) 0xf6, (byte) 0x03));

        // System.out.println((byte) (b & 0x01));

        // byte [][] matrix = {{0x02, 0x03, 0x01, 0x01}, {0x01, 0x02, 0x03, 0x01}, {0x01, 0x01, 0x02, 0x03}, {0x03, 0x01, 0x01, 0x02}};
        // System.out.println(matrix[3][2]);
    }

    private static byte Mult(byte a, byte b) {
		byte result = 0;
		byte bCarry = 0;
        byte aCarry = 0;
        boolean spin = true;
		while (spin) {
            aCarry = (byte) (a & 0x01);
			if (aCarry == 1) {
                result = (byte) (result ^ b);
            }
			bCarry = (byte) (b & 0x80);
			b = (byte) (b << 1);
			if (bCarry == -128) {
                b = (byte) (b ^ 0x1b);
            }
			a = (byte) ((a & 0xff) >> 1);
            if(a == 0) spin = !spin;
		}
		return result;
	}
}