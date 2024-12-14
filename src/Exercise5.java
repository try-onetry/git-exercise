import com.sun.org.apache.xpath.internal.objects.XBoolean;

import java.util.*;

public class Exercise5 {
    public static void main(String[] args) {
        isAssert(judge("a+bc", "abc"), true);
        isAssert(judge("abde", "abbde"), false);
        isAssert(judge("ab*de", "abbde"), true);
        isAssert(judge("ab*98de", "abb98de"), true);
        isAssert(judge("ab+98de", "a98de"), false);
//        isAssert(judge("+", "a98de"), false);
//        isAssert(judge("ab*+98de", "a98de"), false);
    }

    public static <T> void isAssert(T out, T expect) {
        if (!Objects.equals(out, expect)) {
            throw new IllegalArgumentException("Wrong result");
        }
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

        public void removeTransition(char input) {
            transitions.remove(input);
        }

        public int nextState(char input) {
            return transitions.getOrDefault(input, -1); // -1 表示非法转移
        }

    }

    private static Map<Integer, DFANode> buildDFA(String str) {
        Map<Integer, DFANode> dfa = new HashMap<>();

        DFANode currentNode = new DFANode(0);
        dfa.put(0, currentNode);// 初始状态
        int nextState = 1;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch != '*') {
                DFANode nextNode = new DFANode(nextState);
                currentNode.addTransition(ch, nextState);
                dfa.put(nextState, nextNode);
                currentNode = nextNode;
                nextState++;
            } else {
                nextState--;
                currentNode = dfa.get(nextState - 1);
                currentNode.removeTransition(ch);//删边
                dfa.remove(nextState);//删结点
                currentNode.addTransition(str.charAt(i - 1), currentNode.stateId);
            }
        }
        return dfa;
    }

    private static boolean judge(String regex, String str) {
        if (str.charAt(0) == '*' || str.charAt(0) == '+') throw new IllegalArgumentException("Invalid regex input.");
        for (int i = 0; i < regex.length() - 1; i++) {
            char ch = regex.charAt(i);
            char chNext = regex.charAt(i + 1);
            if ((ch == '*' || ch == '+') &&
                    (chNext == '*' || chNext == '+')) {
                throw new IllegalArgumentException("Invalid regex input.");
            }
            //x+处理为xx*
            if (ch == '+') {
                regex = regex.substring(0, i) + str.charAt(i - 1) + "*" + str.substring(i);
            }
        }

        Map<Integer, DFANode> dfa = buildDFA(regex);
        int currentState = 0;
        for (int i = 0; i < str.length(); i++) {
            char input = str.charAt(i);
            int nextState = dfa.get(currentState).nextState(input);

            if (nextState == -1) {
                return false;
            }
            currentState = nextState;
        }
        return true;
    }
}
