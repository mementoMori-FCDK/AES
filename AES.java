/*Grygoriy Bezshaposhnikov A3 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class AES {
    public static void main(String[] args) {

        if( args.length == 2) {
            System.out.println(args[0]);
            System.out.println(args[1]);
            String textStr = "";
            String keyStr = "";
            try{
                Scanner scanner = new Scanner(new File(args[0]));
                if(scanner.hasNextLine()){
                    textStr = scanner.nextLine();
                } else {
                    System.out.println("File is empty");
                    System.exit(0);
                }

                scanner = new Scanner(new File(args[1]));
                if(scanner.hasNextLine()) {
                    keyStr = scanner.nextLine();
                }
                else {
                    System.out.println("File is empty");
                    System.exit(0);
                }
                //-----------------------------TEST-------------------------------------------------------
                String [] text = textStr.split("\\s+");
                String [][] state = initState(text);

                System.out.println("key: " + keyStr);
                String [] key = keyStr.split("\\s+");
                System.out.println(Arrays.toString(key));
                KeyExpanshion(key);
                // String [] text = textStr.split("\\s+");
                // System.out.println(Arrays.toString(text));
                // String [][] state = initState(text);
                // System.out.println(Arrays.deepToString(state));
                // state = InvMixColumns(state);
                // System.out.println(Arrays.deepToString(state));
                //-----------------------------TEST-------------------------------------------------------
            } catch(FileNotFoundException e) {
                System.out.println("No such file in the directory");
            }
        }
        else {
            System.out.println("Arguments: [plaintext].txt [key].txt");
            System.exit(0);
        }
    }

    static String encryp() {
        return "";
    }

    static String [][] KeyExpanshion(String [] key){
        int nb = 4;
        int nk = 4;
        int nr = 10;
        String [] temp = new String [4];
        String [][] schedule = new String [44][4];
        int i = 0;
        while (i < nk) {
            for(int j = 0; j < schedule[0].length; j++) {
                schedule[i][j] = key[4*i + j];
            }
            i++;
        }

        i = nk;

        while(i < (nb * (nr+1))) {
            temp = schedule[i - 1];
            if((i % nk) == 0) {
                String [] rw = RotWord(temp);
                // System.out.println("----------------------");
                // System.out.println("i: " + i);
                // System.out.println("RotWord: " + Arrays.toString(rw));
                String [] sb = SubWord(rw);
                //System.out.println("SubWord: " + Arrays.toString(sb));
                temp = XOR(Rcon(i/nk), sb);
                // System.out.println("After XOR: " + Arrays.toString(temp));
                // System.out.println(Arrays.toString(temp) + "|" + Arrays.toString(Rcon(i/nk)));
                // System.out.println("----------------------");
            }
            schedule[i] = XOR(schedule[i - nk], temp);
            i++;
        }

        for(int r = 0; r < schedule.length; r++) {
            System.out.println(r + ": " + Arrays.toString(schedule[r]));
        }
        
        return schedule;
    }

    static String [] XOR (String [] a, String [] b) {
        String [] result = new String [4];
        for(int i = 0; i < a.length; i++) {
            result[i] = ByteToHex((byte) (HexToByte(a[i]) ^ HexToByte(b[i])));
        }
        return result;
    }

    static String [] Rcon(int n) {
        String [] rCon = new String [4];
        byte rVal = (byte) 0x01;
        byte mult = (byte) 0x02;
        int i = 1;
        while(i != n) {
            rVal = Mult(rVal, mult);
            i++;
        }
        for(int j = 1; j < rCon.length; j++) {
            rCon[j] = ByteToHex((byte) 0x00);
        }
        rCon[0] = ByteToHex(rVal);

        return rCon;
    }

    static String [] RotWord(String [] word) {
        String [] result = new String [4]; 
        String tmp = word[0];
        for(int i = 0; i < word.length - 1; i++) {
            result[i] = word[i + 1];
        }
        result[3] = tmp;

        return result;
    }

    static String [] SubWord(String [] word) {
        String [] result = new String [4];
        String [][] sbox = sbox(false);

        for(int i = 0; i < word.length; i ++) {
            int [] yx = SboxYX(word[i]);
            result[i] = sbox[yx[0]][yx[1]];
        }
        return result;
    }

    static String [][] InvMixColumns(String [][] state) {
        String [][] newState = new String [4][4];
        byte [][] byteState = new byte [4][4];
        byte [][] newByteState = new byte [4][4];
        byte [][] matrix = {{0x0e, 0x0b, 0x0d, 0x09}, {0x09, 0x0e, 0x0b, 0x0d}, {0x0d, 0x09, 0x0e, 0x0b}, {0x0b, 0x0d, 0x09, 0x0e}};
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                byteState[r][c] = HexToByte(state[r][c]);
            }
        }
        
        for(int c = 0; c < byteState.length; c++) {
            newByteState[0][c] = (byte) (Mult(byteState[0][c], matrix[0][0]) ^ Mult(byteState[1][c], matrix[0][1]) 
                                            ^ Mult(byteState[2][c], matrix[0][2]) ^ Mult(byteState[3][c], matrix[0][3]));
            newByteState[1][c] = (byte) (Mult(byteState[0][c], matrix[1][0]) ^ Mult(byteState[1][c], matrix[1][1]) 
                                            ^ Mult(byteState[2][c], matrix[1][2]) ^ Mult(byteState[3][c], matrix[1][3]));
            newByteState[2][c] = (byte) (Mult(byteState[0][c], matrix[2][0]) ^ Mult(byteState[1][c], matrix[2][1]) 
                                            ^ Mult(byteState[2][c], matrix[2][2]) ^ Mult(byteState[3][c], matrix[2][3]));
            newByteState[3][c] = (byte) (Mult(byteState[0][c], matrix[3][0]) ^ Mult(byteState[1][c], matrix[3][1]) 
                                            ^ Mult(byteState[2][c], matrix[3][2]) ^ Mult(byteState[3][c], matrix[3][3]));
        }

        for(int r = 0; r < newState.length; r ++) {
            for (int c = 0; c < newState.length; c++) {
                newState[r][c] = ByteToHex(newByteState[r][c]);
            }
        }

        return newState;
    }

    static String [][] MixColumns(String [][] state) {
        String [][] newState = new String [4][4];
        byte [][] byteState = new byte [4][4];
        byte [][] newByteState = new byte [4][4];
        byte [][] matrix = {{0x02, 0x03, 0x01, 0x01}, {0x01, 0x02, 0x03, 0x01}, {0x01, 0x01, 0x02, 0x03}, {0x03, 0x01, 0x01, 0x02}};
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                byteState[r][c] = HexToByte(state[r][c]);
            }
        }
        
        for(int c = 0; c < byteState.length; c++) {
            newByteState[0][c] = (byte) (Mult(byteState[0][c], matrix[0][0]) ^ Mult(byteState[1][c], matrix[0][1]) 
                                            ^ Mult(byteState[2][c], matrix[0][2]) ^ Mult(byteState[3][c], matrix[0][3]));
            newByteState[1][c] = (byte) (Mult(byteState[0][c], matrix[1][0]) ^ Mult(byteState[1][c], matrix[1][1]) 
                                            ^ Mult(byteState[2][c], matrix[1][2]) ^ Mult(byteState[3][c], matrix[1][3]));
            newByteState[2][c] = (byte) (Mult(byteState[0][c], matrix[2][0]) ^ Mult(byteState[1][c], matrix[2][1]) 
                                            ^ Mult(byteState[2][c], matrix[2][2]) ^ Mult(byteState[3][c], matrix[2][3]));
            newByteState[3][c] = (byte) (Mult(byteState[0][c], matrix[3][0]) ^ Mult(byteState[1][c], matrix[3][1]) 
                                            ^ Mult(byteState[2][c], matrix[3][2]) ^ Mult(byteState[3][c], matrix[3][3]));
        }

        for(int r = 0; r < newState.length; r ++) {
            for (int c = 0; c < newState.length; c++) {
                newState[r][c] = ByteToHex(newByteState[r][c]);
            }
        }

        return newState;
    }

    private static byte Mult(byte a, byte b) {
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

    static String [][] InvShiftRows(String [][] state) {
        String [][] newState = new String [4][4];
        for( int r = 0; r < newState.length; r++) {
            if(r == 1) {
                newState[r][0] = state[r][3];
                newState[r][1] = state[r][0];
                newState[r][2] = state[r][1];
                newState[r][3] = state[r][2];
            }
            else if(r == 2) {
                newState[r][0] = state[r][2];
                newState[r][1] = state[r][3];
                newState[r][2] = state[r][0];
                newState[r][3] = state[r][1];
            }
            else if(r==3) {
                newState[r][0] = state[r][1];
                newState[r][1] = state[r][2];
                newState[r][2] = state[r][3];
                newState[r][3] = state[r][0];
            }
            else{
                newState[r] = state[r];
            }
        }
        return newState;
    }

    static String [][] ShiftRows(String [][] state) {
        String [][] newState = new String [4][4];
        for( int r = 0; r < newState.length; r++) {
            if(r == 1) {
                newState[r][0] = state[r][1];
                newState[r][1] = state[r][2];
                newState[r][2] = state[r][3];
                newState[r][3] = state[r][0];
            }
            else if(r == 2) {
                newState[r][0] = state[r][2];
                newState[r][1] = state[r][3];
                newState[r][2] = state[r][0];
                newState[r][3] = state[r][1];
            }
            else if(r==3) {
                newState[r][0] = state[r][3];
                newState[r][1] = state[r][0];
                newState[r][2] = state[r][1];
                newState[r][3] = state[r][2];
            }
            else{
                newState[r] = state[r];
            }
        }
        
        return newState;
    }

    static String [][] SubBytes(String [][] state, boolean inverse) {
        String [][] sbox = sbox(inverse);
        
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                int [] yx = SboxYX(state[r][c]);
                state[r][c] = sbox[yx[0]][yx[1]];
            }
        }

        return state;
    }

    static int [] SboxYX(String stateByte) {
        String [] parts = stateByte.split("");
        int y = Integer.parseInt(parts[0], 16);
        int x = Integer.parseInt(parts[1], 16);
        int [] yx = {y,x};
        return yx;
    }

    static String [][] sbox(boolean inverse) {
        String [][] sbox = new String [16][16];
        File source = null;
        if(inverse) {
            source = new File("inv_sbox.txt");
        } else {
            source = new File("sbox.txt");
        }
        try {
            int row = 0;
            Scanner scanner = new Scanner(source);
            while(scanner.hasNext()) {
                String str = scanner.nextLine();
                String [] strSplit = str.split("\\s+");
                sbox[row] = strSplit.clone();
                row++;
            }
            scanner.close();
        } catch(Exception e) {
            System.out.println("Error reading sbox");
            System.exit(0);
        }
        return sbox;
    }

    static byte HexToByte(String str) {
        int first = Character.digit(str.charAt(0), 16);
        int second = Character.digit(str.charAt(1), 16);
        byte result = (byte) ((first << 4) + second);
        return result;
    }

    static String ByteToHex(byte num) {
        char[] str = new char[2];
        str[0] = Character.forDigit((num >> 4) & 0xf, 16);
        str[1] = Character.forDigit((num & 0xf), 16);
        return new String(str);
    }

    static String [][] initState(String [] text) {

        String [][] initState = new String [4][4];

        for(int r = 0; r < initState.length; r++) {
            for(int c = 0; c < initState.length; c++) {
                initState[r][c] = text[r + 4*c];
            }
        }

        return initState;
    }
}
