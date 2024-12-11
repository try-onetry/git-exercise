import java.util.LinkedList;
import java.util.Queue;

public class Exercise2 {
    public static void main(String[] args) {
        String str = "12+8-4";
        System.out.println(calc(str));
    }

    public static int calc(String str) {
        Queue<Integer> nums = new LinkedList<>();
        Queue<Character> operation = new LinkedList<>();
        int num = 0;
        int result = 0;
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
        result = nums.poll();
        while (!operation.isEmpty()) {
            char op = operation.poll();
            if (op == '+') result += nums.poll();
            else if (op == '-') result -= nums.poll();

        }

        return result;
    }


}
