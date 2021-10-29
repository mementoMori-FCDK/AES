import java.util.Arrays;

public class hello {
    public static void main(String[] args) {
        String hexStr = "ae";
        int decimal = Integer.parseInt(hexStr, 16);
        System.out.println(Integer.toBinaryString(decimal));

        // String test = "af";
        // String [] parts = test.split("");
        // System.out.println(Arrays.toString(parts));
    }

    static String a() {
        return "ahuet";
    }

    static void ab () {
        System.out.println(a() + "b");
    }
}