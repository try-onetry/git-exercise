import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Exercise2 {
    public static void main(String[] args) {
        String str = "-4+6";
        System.out.println(calc(str));
        String str2 = "12--7-4";
        System.out.println(calc(str2));
    }

    public static int calc(String str) {
        Queue<Integer> nums = new LinkedList<>();
        Queue<Character> operation = new LinkedList<>();
        int num = 0;
        int result = 0;
        char end = str.charAt(str.length() - 1);
        if (end == '+' || end == '-') {
            throw new IllegalArgumentException("Invalid input.");
        }
        for(int i = 0; i < str.length() - 2; i++){
            char first = str.charAt(i);
            char second = str.charAt(i + 1);
            char third = str.charAt(i + 2);
            if ((first == '+' || first == '-') &&
                    (second == '+' || second == '-') &&
                    (third == '+' || third == '-')) {
                throw new IllegalArgumentException("Invalid input.");
            }

            str = str.replace("++","+");
            str = str.replace("--","+");
            str = str.replace("+-","-");
            str = str.replace("-+","-");
        }


        for(int i = 0; i < str.length(); i++){
            if (str.charAt(i) == '+' || str.charAt(i) == '-') {
                char op = str.charAt(i);
                operation.add(op);
                nums.add(num);
                num = 0;
            }
            else{
                num = num * 10 + str.charAt(i) - '0';
                if (i == str.length()-1){
                    nums.add(num);
                }
            }
        }
        if (str.charAt(0) == '-'){
            nums.poll();//如果是负数第一个，第一个进队列的是0
            result = -nums.poll();
            operation.poll();
        }
        else{
            result = nums.poll();
        }
        while (!operation.isEmpty()) {
            char op = operation.poll();
            if (op == '+') result += nums.poll();
            else if (op == '-') result -= nums.poll();

        }

        return result;
    }
}
