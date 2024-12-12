import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Exercise2_2 {
    public static void main(String[] args) {
        String str1 = "-8+7";
        System.out.println(calc(str1));
        String str2 = "12--7-4";
        System.out.println(calc(str2));
    }

    // DFA 节点定义
    static class DFANode {
        int stateId;
        Map<Character, Integer> transitions;

        public DFANode(int stateId) {
            this.stateId = stateId;
            this.transitions = new HashMap<>();
        }

        public void addTransition(char input, int nextState) {
            transitions.put(input, nextState);
        }

        public int nextState(char input) {
            return transitions.getOrDefault(input, -1); // -1 表示非法转移
        }
    }

    // 构造 DFA 邻接表
    private static Map<Integer, DFANode> buildDFA() {
        Map<Integer, DFANode> dfa = new HashMap<>();

        // 状态定义
        DFANode q0 = new DFANode(0); // 初始状态
        DFANode q1 = new DFANode(1); // 读取数字
        DFANode q2 = new DFANode(2); // 操作符状态

        // 状态转移
        q0.addTransition('+', 1);
        q0.addTransition('-', 1);
        q0.addTransition('d', 1); // digit

        q1.addTransition('d', 1);
        q1.addTransition('+', 2);
        q1.addTransition('-', 2);

        q2.addTransition('d', 1);

        // 添加到 DFA 图
        dfa.put(0, q0);
        dfa.put(1, q1);
        dfa.put(2, q2);

        return dfa;
    }

    // 计算表达式的主函数
    public static int calc(String str) {
        if ((str.charAt(0) == '+' || str.charAt(0) == '-') &&
                (str.charAt(1) == '+' || str.charAt(1) == '-')) {
            throw new IllegalArgumentException("Invalid input.");
        }
        str = str.replace("++", "+");
        str = str.replace("--", "+");
        str = str.replace("+-", "-");
        str = str.replace("-+", "-");

        int result = 0;
        Map<Integer, DFANode> dfa = buildDFA();
        int currentState = 0;

        Queue<Integer> nums = new LinkedList<>();
        Queue<Character> ops = new LinkedList<>();

        int num = 0;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            char input = Character.isDigit(ch) ? 'd' : ch;

            // 状态转移
            int nextState = dfa.get(currentState).nextState(input);
            if (nextState == -1) {
                throw new IllegalArgumentException("Invalid input.");
            }

            if (input == 'd') {
                num = num * 10 + (ch - '0');
            } else {
                if (nextState == 2) {
                    nums.add(num);
                    num = 0;
                }
                ops.add(input);
            }
            currentState = nextState;
        }
        // 将最后一个数字入队
        nums.add(num);
        if (str.charAt(0) == '-') {
            result = -nums.poll();
            ops.poll();
        } else {
            if (str.charAt(0) == '+') ops.poll();
            result = nums.poll();
        }
        while (!ops.isEmpty()) {
            char op = ops.poll();
            if (op == '+') {
                result += nums.poll();
            } else if (op == '-') {
                result -= nums.poll();
            }
        }
        return result;
    }
}
