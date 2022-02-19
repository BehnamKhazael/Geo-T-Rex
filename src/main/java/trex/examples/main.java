package trex.examples;

/**
 * Created by sony on 3/28/2021.
 */
public class main {
    public static void main (String[] args){

        String s = "http://0.0.0.0:7998/event";

        String[] s2 = s.split("/" , 4);
        System.out.println("1) " + s2[0]);
        System.out.println("2) " + s2[1]);
        System.out.println("3) " + s2[2]);
        String[] s3 = s2[2].split(":", 2);
        System.out.println("3.1) " + s3[0]);
        System.out.println("3.2) " + s3[1]);
        System.out.println("4) " +s2[3]);

    }
}
