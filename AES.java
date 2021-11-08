/*Grygoriy Bezshaposhnikov 7824369 A3 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class AES {
    public static void main(String[] args) {
        if( args.length == 2) {
            System.out.println(args[0]);
            System.out.println(args[1]);
            String textStr = "";            //input from plaintext file
            String keyStr = "";             //input from key file
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
                String [][] state = new String [4][4];
                String [] text = textStr.split("\\s+");                 //removing spaces
                String [] key = keyStr.split("\\s+");                   //
                System.out.println("input: " + Arrays.toString(text));
                System.out.println("key: " + Arrays.toString(key));
                state = encryp(text, key);
                state = decrypt(state, key);
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
    /**
     * Decrypts the ciphertext
     * @param cipher - 2d array of strings. Each string consists of 2 chars that reperesent hex number
     * @param key    - array of strings. Consists of strings that represent key in hex 
     * @return       - returns 2d array with plaintext
     */
    static String [][] decrypt(String [][] cipher, String [] key) {
        int nr = 10;
        int nb = 4;
        int nk = 4;
        boolean inverse = true;
        String [][] state = cipher;
        String [][] schedule = KeyExpansion(key);
        state = AddRoundKey(state, 40, schedule);

        for(int i = (nr - 1); i > 0; i--) {
            state = InvShiftRows(state);
            state = SubBytes(state, inverse);
            state = AddRoundKey(state, i * nb, schedule);
            state = InvMixColumns(state);
            System.out.println("Round: " + i + " after MixColumns");
            PrintState(state);
            
        }

        state = InvShiftRows(state);
        state = SubBytes(state, inverse);
        state = AddRoundKey(state, 0, schedule);

        System.out.println("Plaintext: ");

        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                System.out.print(state[c][r]);
            }   
        }
        System.out.println("");
        return state;
    }
    
    /**
     * Encrypts the plaintext
     * @param text - array of Strings. Consists of strings that represent plaintext in hex 
     * @param key  - array of strings. Consists of strings that represent key in hex 
     * @return     - returns 2d string array with ciphertext 
     */
    static String [][] encryp(String [] text, String [] key) {
        int nr = 10;                //# rounds
        int nb = 4;                 //# columns in state
        int nk = 4;                 //# of words in state array
        boolean inverse = false;    
        String [][] state = initState(text);
        String [][] schedule = KeyExpansion(key);  //roundkeys
        state = AddRoundKey(state, 0, schedule);

        for(int i = 1; i < nr; i++) {
            state = SubBytes(state, inverse);
            state = ShiftRows(state);
            state = MixColumns(state);
            System.out.println("Round: " + i + " after MixColumns");
            PrintState(state);
            state = AddRoundKey(state, i * nb, schedule);
            
        }

        state = SubBytes(state, inverse);
        state = ShiftRows(state);
        state = AddRoundKey(state, nr * nb, schedule);

        System.out.println("Ciphertext: ");
        //print array in one line
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                System.out.print(state[c][r]);
            }   
        }
        System.out.println("");
        return state;
    }

    /**
     * Adds round key to each word in state. 
     * @param state - 2d array. Current state
     * @param start - round when AddRoundKey was invoked
     * @param schedule - Roundkeys
     * @return - returns the state after XOR
     */
    static String [][] AddRoundKey(String [][] state, int start, String [][] schedule) {

        for (int c = 0; c < state.length; c++) {
            for (int r = 0; r <state.length; r++) {
                state[r][c] = ByteToHex((byte) (HexToByte(state[r][c])^HexToByte(schedule[c + start][r])));
            }
        }
        return state;
    }

    /**
     * Returns 2d array of strings with 44 4-byte words that represent key schedule
     * @param key - array with key value in hex
     * @return - 2d array with key schedule
     */
    static String [][] KeyExpansion(String [] key){
        int nb = 4;     //# columns in state
        int nk = 4;     //# words in state
        int nr = 10;    //# rounds
        String [] temp = new String [4];                //tmp arraya to form a word from input key
        String [][] schedule = new String [44][4];      //schedule array
        //Key expansion algorithm 
        int i = 0;
        while (i < nk) {
            for(int j = 0; j < schedule[0].length; j++) {
                schedule[i][j] = key[4 * i + j];
            }
            i++;
        }

        i = nk;

        while(i < (nb * (nr+1))) {
            temp = schedule[i - 1];
            if((i % nk) == 0) {
                String [] rw = RotWord(temp);
                String [] sb = SubWord(rw);
                temp = XOR(Rcon(i/nk), sb);
            }
            schedule[i] = XOR(schedule[i - nk], temp);
            i++;
        }
        
        return schedule;
    }

    /**
     * Takes two words in string values. Converts to byte words. Performes binary XOR. Returns new hex string values
     * @param a - word a (String)
     * @param b - word b (String)
     * @return - resulting string after binary XOR
     */
    static String [] XOR (String [] a, String [] b) {
        String [] result = new String [4];
        for(int i = 0; i < a.length; i++) {
            result[i] = ByteToHex((byte) (HexToByte(a[i]) ^ HexToByte(b[i])));
        }
        return result;
    }

    /**
     * Returns Rcon word. Based on the round number
     * Multiplies the 0x01 by 0x02 until we reach the desired array (based on the round number).
     * Refrence: https://crypto.stackexchange.com/questions/2418/how-to-use-rcon-in-key-expansion-of-128-bit-advanced-encryption-standard
     * @param n - round number
     * @return returns the Rcon word based on the round number
     */
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

    /**
     * Cyclic permutation of a 4-byte word
     * @param word - 4-byte hex word (in strings)
     * @return - rotated 4-byte hex word (in strings)
     */
    static String [] RotWord(String [] word) {
        String [] result = new String [4]; 
        String tmp = word[0];
        for(int i = 0; i < word.length - 1; i++) {
            result[i] = word[i + 1];
        }
        result[3] = tmp;

        return result;
    }

    /**
     * Applies sbox to the 4-byte word
     * @param word - 4-byte word
     * @return - new 4-byte word
     */
    static String [] SubWord(String [] word) {
        String [] result = new String [4];
        String [][] sbox = sbox(false);

        for(int i = 0; i < word.length; i ++) {
            int [] yx = SboxYX(word[i]);
            result[i] = sbox[yx[0]][yx[1]];
        }
        return result;
    }

    /**
     * Performs inverse MixColumns transformation
     * @param state - input state (2d string array)
     * @return - new state (2d string array)
     */
    static String [][] InvMixColumns(String [][] state) {
        String [][] newState = new String [4][4];       //return variable
        byte [][] byteState = new byte [4][4];          //variable to store state value in bytes
        byte [][] newByteState = new byte [4][4];       //variable to store transformed state value in bytes
        byte [][] matrix = {{0x0e, 0x0b, 0x0d, 0x09}, {0x09, 0x0e, 0x0b, 0x0d}, {0x0d, 0x09, 0x0e, 0x0b}, {0x0b, 0x0d, 0x09, 0x0e}};    //fixed polynomial in hex bytes
        //convert hex strings to byte values 
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                byteState[r][c] = HexToByte(state[r][c]);
            }
        }
        
        //performes multiplication based on the matrix (fixed polynomial)
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
        //converts back to hex strings
        for(int r = 0; r < newState.length; r ++) {
            for (int c = 0; c < newState.length; c++) {
                newState[r][c] = ByteToHex(newByteState[r][c]);
            }
        }

        return newState;
    }

    /**
     * Performs  MixColumns transformation
     * @param state - input state (2d string array)
     * @return - new state (2d string array)
     */
    static String [][] MixColumns(String [][] state) {
        String [][] newState = new String [4][4];       //return variable
        byte [][] byteState = new byte [4][4];          //variable to store state value in bytes
        byte [][] newByteState = new byte [4][4];       //variable to store transformed state value in bytes
        byte [][] matrix = {{0x02, 0x03, 0x01, 0x01}, {0x01, 0x02, 0x03, 0x01}, {0x01, 0x01, 0x02, 0x03}, {0x03, 0x01, 0x01, 0x02}};    //fixed polynomial in hex bytes
        //convert hex strings to byte values 
        for(int r = 0; r < state.length; r++) {
            for(int c = 0; c < state.length; c++) {
                byteState[r][c] = HexToByte(state[r][c]);
            }
        }
        //performes multiplication based on the matrix (fixed polynomial)
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
        //converts back to hex strings
        for(int r = 0; r < newState.length; r ++) {
            for (int c = 0; c < newState.length; c++) {
                newState[r][c] = ByteToHex(newByteState[r][c]);
            }
        }

        return newState;
    }

    /**
     * Multiplies two plynomials in the finite field GF(2).
     * 1) [] ones - stores the positions of "1" in binary representation of byte a
     *    For example: 11110000 -> ones{1,1,1,1,0,0,0,0}
     * 2) Shifts a to the right 8 times to fill "ones" array 
     * 3) [] powers - stores b*0x01, b*0x02, b*0x04, b*0x08, b*0x10.... 
     * @param a - polynomial a (byte a)
     * @param b - polynomial b  (byte b)
     * @return - result value (byte)
     */
    private static byte Mult(byte a, byte b) {
        byte result = 0;        //reslut value
        byte aCarry = 0;        //carry over after right shift of a
        byte bCarry = 0;        //carry over after left shift of b
        int [] ones = new int [8];  //array that stores positions of "1" bits
        byte [] powers = new byte [8];  //array that stores values of b after multiplication by 0x02

        //initialize ones
        for (int i = 0; i < ones.length; i++) {
            aCarry = (byte) (a & 0x01);     //value of the rightmost bit
            a = (byte) ((a & 0xff) >> 1);   //right shift
            if(aCarry == 1) {
                ones[i] = 1;                //fill ones array
            }
            else ones[i] = 0;
        }

        //initialize powers array
        for(int i = 0; i < powers.length; i++) {
            if (i == 0) powers[i] = b;          //b * 0x01
            else {
                bCarry = (byte) (b & 0x80);     //leftmost bit in b 
                b = (byte) (b << 1);            //left shift
                if (bCarry == -128) {           //signed 0xf0 (10000000) (dont know how to convert to unsigned) 
                    b = (byte) (b ^ 0x1b);      //xor in case carry over 1
                }
                powers[i] = b;                  //fill powers array
            }
        }

        //in case bit is set to 1 in a, XOR the result value with powers[position of set bit]
        for(int i = 0; i < ones.length; i++) {
            if(ones[i] == 1) {
                result = (byte) (result ^ powers[i]);
            } 
        }
        return result;
    }
    
    /**
     * ShiftRows transformation
     * @param state - input state
     * @return - new state
     */
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

    /**
     * inverse ShiftRows transformation
     * @param state - input state
     * @return - new state
     */
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

    /**
     * SubBytes transformation
     * @param state - input state 
     * @param inverse - boolean to switch between InvSubBytes and SubBytes
     * @return new state 
     */
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

    /**
     * Takes hex string value as an input. Converts to int. Creates array with coordinates in sbox/invSbox
     * @param stateByte - byte in hex string
     * @return coordinates in sbox/invSbox
     */
    static int [] SboxYX(String stateByte) {
        String [] parts = stateByte.split("");
        int y = Integer.parseInt(parts[0], 16);
        int x = Integer.parseInt(parts[1], 16);
        int [] yx = {y,x};
        return yx;
    }

    /**
     * Reads sbox/invSbox from file
     * @param inverse -switched between sbox and invSbox
     * @return 2d string array with sbox/invSbox
     */
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

    /**
     * Converts from hex string to byte
     * @param str - hex string to convert
     * @return converted byte (hex)
     */
    static byte HexToByte(String str) {
        int first = Character.digit(str.charAt(0), 16);
        int second = Character.digit(str.charAt(1), 16);
        byte result = (byte) ((first << 4) + second);
        return result;
    }

    /**
     * Converts from byte to hex string
     * @param num - byte value to convert
     * @return - converted hex 
     */
    static String ByteToHex(byte num) {
        char[] str = new char[2];
        str[0] = Character.forDigit((num >> 4) & 0xf, 16);
        str[1] = Character.forDigit((num & 0xf), 16);
        return new String(str);
    }

    /**
     * Initializes state 2d array from input plaintext
     * @param text - input plaintext
     * @return initial state
     */
    static String [][] initState(String [] text) {

        String [][] initState = new String [4][4];

        for(int r = 0; r < initState.length; r++) {
            for(int c = 0; c < initState.length; c++) {
                initState[r][c] = text[r + 4*c];
            }
        }

        return initState;
    }

    /**
     * Prints current state 
     * @param state - current state
     */
    static void PrintState(String [][] state) {
        for(int r = 0; r < state.length; r++) {
            System.out.println(Arrays.toString(state[r]));
        }
    } 
}
