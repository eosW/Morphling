import java.util.Scanner;
public class testcase2 {
    public static void main(String args[]){
        Scanner in = new Scanner(System.in);
        int a;
        a = in.nextInt();
        if((a) < (0)) {
            a = - (a);
        } else
        if((a) > (100)) {
            while((a) > (5)) {
                a = (a) / (2);
            }
        } else
        if(true) {
            while(true) {
                a = (a) - (1);
                if((((a) % (3)) == (0)) && ((a) < (0))) {
                    break;
                } else
                {}
            }
        } else
        {}
        System.out.println(a);
    }
}
