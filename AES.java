import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class AES {
    public static void main(String[] args) {

        if( args.length == 2) {
            // textPath = args[0];
            // keyPath = args[1];
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
                // System.out.println("key: " + keyStr);
                String [] text = textStr.split("\\s+");
                String [][] initState = initState(text);
                System.out.println(Arrays.deepToString(initState));
                SubBytes(initState);
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

    static String cipher() {
        return "";
    }

    static String [][] SubBytes(String [][] state) {
        String [][] newState = new String [4][4];
        String [][] sbox = sbox();
        
        for(int r = 0; r < newState.length; r++) {
            for(int c = 0; c < newState.length; c++) {
                int [] yx = SboxYX(state[r][c]);
                newState[r][c] = sbox[yx[0]][yx[1]];
            }
        }

        System.out.println(Arrays.deepToString(newState));
        return newState;
    }

    static int [] SboxYX(String stateByte) {
        String [] parts = stateByte.split("");
        int y = Integer.parseInt(parts[0], 16);
        int x = Integer.parseInt(parts[1], 16);
        int [] yx = {y,x};
        return yx;
    }

    static String [][] sbox() {
        String [][] sbox = new String [16][16]; 
        try {
            int row = 0;
            Scanner scanner = new Scanner(new File("sbox.txt"));
            while(scanner.hasNext()) {
                String str = scanner.nextLine();
                String [] strSplit = str.split("\\s+");
                sbox[row] = strSplit.clone();
                row++;
            }

        } catch(Exception e) {
            System.out.println("Error reading sbox");
            System.exit(0);
        }
        return sbox;
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
