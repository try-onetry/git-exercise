import java.util.*;

public class Exercise5 {
    public static void main(String[] args) {
        isAssert(judge("a*", ""), true);
        isAssert(judge("a+", ""), false);
        isAssert(judge("a*", "a"), true);
        isAssert(judge("a*", "aa"), true);
        isAssert(judge("a*", "b"), false);
        isAssert(judge("aaa*", "a"), false);
        isAssert(judge("a*bc*d", "abcd"), true);
        isAssert(judge("a*bc*d", "bccd"), true);
        isAssert(judge("", "a"), false);
        testInvalidRegex();
    }

    public static <T> void isAssert(T out, T expect) {
        if (!Objects.equals(out, expect)) {
            throw new IllegalArgumentException("Wrong result");
        }
    }

    public static void testInvalidRegex() {
        List<String> invalidInputs = Arrays.asList(
                "*abc", "+abc", "ab**c"
        );

        for (String regex : invalidInputs) {
            try {
                judge(regex, "abc");
                throw new RuntimeException("Unexpected exception");
            } catch (IllegalArgumentException e) {
                System.out.println("Correctly threw exception for regex: " + regex);
            }
        }
    }

    public static void isInvalidInput(String regex) {
        if (!regex.isEmpty()) {
            if (regex.charAt(0) == '*' || regex.charAt(0) == '+') {
                throw new IllegalArgumentException("Invalid regex input.");
            }
        }
        for (int i = 0; i < regex.length() - 1; i++) {
            char ch = regex.charAt(i);
            char chNext = regex.charAt(i + 1);
            if ((ch == '*' || ch == '+') &&
                    (chNext == '*' || chNext == '+')) {
                throw new IllegalArgumentException("Invalid regex input.");
            }
        }
    }

    public static NFA buildNFA(String regex) {
        NFA nfa = new NFA();
        int current = 0;

        for (int i = 0; i < regex.length(); i++) {
            char ch = regex.charAt(i);
            if (ch == '*' || ch == '+') {
                if (ch == '*') {
                    nfa.removeTransition(current - 1, regex.charAt(i - 1), current);
                    current--;
                }
                nfa.addTransition(current, '\0', current + 1);
                current++;
                nfa.addTransition(current, regex.charAt(i - 1), current);
                nfa.addTransition(current, '\0', current + 1);
            } else {
                nfa.addTransition(current, ch, current + 1);
            }
            current++;
        }

        nfa.startStates.add(0);
        nfa.endStates.add(current);
        return nfa;
    }

    public static DFA NFA2DFA(NFA nfa) {
        DFA dfa = new DFA();
        Map<Set<Integer>, Integer> stateMapping = new HashMap<>();// NFA到DFA的状态映射表
        Queue<Set<Integer>> queue = new LinkedList<>();
        int stateID = 0; // DFA初始状态

        // 找到所有边上的字符
        Set<Character> allCharacters = new HashSet<>();
        for (Map<Character, Set<Integer>> edges : nfa.transitions.values()) {
            allCharacters.addAll(edges.keySet());
        }
        allCharacters.remove('\0'); // 排除空边

        // 对于NFA初始态，找闭包作为DFA的初始态
        Set<Integer> startState = nfa.emptyTransition(nfa.startStates);

        stateMapping.put(startState, stateID++);
        dfa.startStates.add(0);
        queue.add(startState);
        // 判断初态是不是终态
        for (Integer s : startState) {
            if (nfa.endStates.contains(s)) {
                dfa.endStates.add(0);
            }
        }
        while (!queue.isEmpty()) {
            Set<Integer> currentState = queue.poll();
            int currentID = stateMapping.get(currentState); // DFA状态ID

            for (Character ch : allCharacters) {
                Set<Integer> nextState = new HashSet<>();
                for (Integer state : currentState) {
                    if (nfa.transitions.containsKey(state) && nfa.transitions.get(state).containsKey(ch)) {
                        nextState.addAll(nfa.transitions.get(state).get(ch)); // a弧转换
                    }
                }
                nextState = nfa.emptyTransition(nextState); // a弧转换后求空闭包（新状态）

                if (!nextState.isEmpty()) {
                    if (!stateMapping.containsKey(nextState)) {
                        stateMapping.put(nextState, stateID++);
                        queue.add(nextState); // 重复的状态不要add
                    }

                    dfa.addTransition(currentID, ch, stateMapping.get(nextState));
                    // 终态判断
                    for (Integer s : nextState) {
                        if (nfa.endStates.contains(s)) {
                            dfa.endStates.add(stateMapping.get(nextState));
                            break;
                        }
                    }
                }
            }
        }

        return dfa;
    }

    public static boolean judge(String regex, String str) {
        isInvalidInput(regex);
        NFA nfa = buildNFA(regex);
        DFA dfa = NFA2DFA(nfa);
        int currentState = 0;

        for (int i = 0; i < str.length(); i++) {
            char input = str.charAt(i);
            if (!dfa.getTransitions().containsKey(currentState) || !dfa.getTransitions().get(currentState).containsKey(input)) {
                return false;
            }
            currentState = dfa.getTransitions().get(currentState).get(input);
        }

        return dfa.getEndStates().contains(currentState);
    }
}
