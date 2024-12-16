import java.util.*;

public class Exercise4 {
    public static void main(String[] args) {
        String str1 = "-6*+3";
        System.out.println(calc(str1));
        String str2 = "-(-1)";
        System.out.println(calc(str2));
    }

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

    private static Map<Integer, DFANode> buildDFA() {
        Map<Integer, DFANode> dfa = new HashMap<>();

        DFANode q0 = new DFANode(0); // 初始状态
        DFANode q1 = new DFANode(1); // 读取数字
        DFANode q2 = new DFANode(2); // 操作符状态

        q0.addTransition('+', 1);
        q0.addTransition('-', 1);
        q0.addTransition('d', 1); // digit

        q1.addTransition('d', 1);
        q1.addTransition('+', 2);
        q1.addTransition('-', 2);
        q1.addTransition('*', 2);
        q1.addTransition('/', 2);

        q2.addTransition('d', 1);
        q2.addTransition('+', 1);
        q2.addTransition('-', 1);

        dfa.put(0, q0);
        dfa.put(1, q1);
        dfa.put(2, q2);

        return dfa;
    }

    // 递归解析括号并计算表达式
    private static int calc(String str) {
        str = str.replaceAll(" ", "");
        StringBuffer substr = new StringBuffer(); // 用于提取括号内的内容
        int result = 0;
        int lflag = 0; // 左括号
        int rflag = 0; // 右括号

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '(') {
                if (lflag > 0) {
                    substr.append(ch);
                }
                lflag++;
            } else if (ch == ')') {
                rflag++;
                if (rflag > lflag) {
                    throw new IllegalArgumentException("Invalid input: unmatched parentheses.");
                } else if (lflag == rflag) {
                    int subResult = calc(substr.toString());
                    str = str.replace('(' + substr.toString() + ')', String.valueOf(subResult));
                    substr.setLength(0); // 清空 substr
                    return calc(str);
                } else { // lflag>rflag
                    substr.append(ch);
                }
            } else {
                if (lflag > rflag) {//在括号里的非括号字符
                    substr.append(ch);
                }
            }
        }
        // 括号匹配
        if (lflag != rflag) {
            throw new IllegalArgumentException("Invalid input: unmatched parentheses.");
        }
        // 没有括号
        return calcNoBracket(str);
    }

    public static int calcNoBracket(String str) {
        if (Objects.equals(str, "")) throw new IllegalArgumentException("Invalid input.");
        int result = 0, num = 0;
        Map<Integer, DFANode> dfa = buildDFA();
        int currentState = 0;
        boolean isNegative = false;

        Queue<Integer> nums = new LinkedList<>();
        Queue<Character> ops = new LinkedList<>();

        if (!Character.isDigit(str.charAt(0))) str = '0' + str; // 符号开头的处理

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            char ch_end = str.charAt(str.length() - 1);
            char input = Character.isDigit(ch) ? 'd' : ch;

            int nextState = dfa.get(currentState).nextState(input);
            if (nextState == -1 || !Character.isDigit(ch_end)) {
                throw new IllegalArgumentException("Invalid input.");
            }

            if (input == 'd') {
                num = num * 10 + (ch - '0');
            } else {
                if (nextState == 2) {
                    nums.add(isNegative ? -num : num);
                    num = 0;
                    ops.add(input);
                    isNegative = false;
                } else if (nextState == 1)
                    isNegative = input == '-';
            }
            currentState = nextState;
        }

        nums.add(isNegative ? -num : num);

        //保证优先级做两个队列，相当于是做完乘除之后的中间结果
        Deque<Integer> newNums = new LinkedList<>();//双端队列
        Queue<Character> newOps = new LinkedList<>();

        newNums.add(nums.poll());
        while (!ops.isEmpty()) {
            char op = ops.poll();
            int b = nums.poll();
            if (op == '*') {
                newNums.add(newNums.pollLast() * b);
            } else if (op == '/') {
                newNums.add(newNums.pollLast() / b);
            } else {
                newNums.add(b);
                newOps.add(op);
            }
        }

        result = newNums.poll();
        while (!newOps.isEmpty()) {
            char op = newOps.poll();
            int b = newNums.poll();
            if (op == '+') {
                result += b;
            } else if (op == '-') {
                result -= b;
            }
        }
        return result;
    }
}

