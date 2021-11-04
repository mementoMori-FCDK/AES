import java.util.Arrays;

public class hello {
    public static void main(String[] args) {

        byte a = (byte) 0x0b;
        byte b = (byte) 0x1e;
        System.out.println(mult2((byte)0x01,(byte) 0x01));
    }



    private static byte mult2(byte a, byte b) {
        byte result = 0;
        byte aCarry = 0;
        byte bCarry = 0;
        int [] ones = new int [8];
        byte [] powers = new byte [8];

        for (int i = 0; i < ones.length; i++) {
            aCarry = (byte) (a & 0x01);
            a = (byte) ((a & 0xff) >> 1);
            if(aCarry == 1) {
                ones[i] = 1;
            }
            else ones[i] = 0;
        }

        for(int i = 0; i < powers.length; i++) {
            if (i == 0) powers[i] = b;
            else {
                bCarry = (byte) (b & 0x80);
                b = (byte) (b << 1);
                if (bCarry == -128) {
                    b = (byte) (b ^ 0x1b);
                }
                powers[i] = b;
            }
        }

        for(int i = 0; i < ones.length; i++) {
            if(ones[i] == 1) {
                result = (byte) (result ^ powers[i]);
            } 
        }

        return result;
    } 

    static String ByteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}