import java.util.Scanner;
public class testcase1 {
    public static void main(String args[]){
        Scanner in = new Scanner(System.in);
        double a;
        a = in.nextDouble();
        a = Math.pow(a,2);
        int b = ((int) ((a) + (1))) >> (2);
        System.out.println(b);
    }
}
