import java.util.Scanner;
public class testcase3 {
    public static void main(String args[]){
        Scanner in = new Scanner(System.in);
        int a;
        a = in.nextInt();
        int b = 1;
        while((b) <= (a)) {
            if(((a) % (b)) == (0)) {
                System.out.println(b);
            } else
            {}
            b = (b) + (1);
        }
        a = in.nextInt();
    }
}
