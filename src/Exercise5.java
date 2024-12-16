import java.util.*;

public class Exercise5 {
    public static void main(String[] args) {
        isAssert(judge("a*",""), true);
//        isAssert(judge("a+",""), false);
//        isAssert(judge("a*","a"), true);
//        isAssert(judge("a*","aa"), true);
//        isAssert(judge("a*","b"), false);
//        isAssert(judge("aaa*","a"), false);
//        isAssert(judge("a*bc*d","abcd"), true);
//        isAssert(judge("a*bc*d","bccd"), true);
    }

    public static <T> void isAssert(T out, T expect) {
        if (!Objects.equals(out, expect)) {
            throw new IllegalArgumentException("Wrong result");
        }
    }

    public static void isInvalidInput(String regex) {
        if (regex.charAt(0) == '*' || regex.charAt(0) == '+') {
            throw new IllegalArgumentException("Invalid regex input.");
        }
    }

    public static class NFA {
        Set<Integer> startStates;
        Set<Integer> endStates;
        Map<Integer, Map<Character, Set<Integer>>> transitions;

        public NFA() {
            this.startStates = new HashSet<>();
            this.endStates = new HashSet<>();
            this.transitions = new HashMap<>();
        }

        public void addTransition(int from, char input, int to) {
            transitions.putIfAbsent(from, new HashMap<>());
            transitions.get(from).putIfAbsent(input, new HashSet<>());
            transitions.get(from).get(input).add(to);
        }

        public void removeTransition(int from, char input, int to) {
            if (transitions.containsKey(from)) {
                Map<Character, Set<Integer>> edges = transitions.get(from);
                if (edges.containsKey(input)) {
                    edges.get(input).remove(to);
                }
                if(edges.get(input).isEmpty()){
                    edges.remove(input);
                }
            }
        }
        // 对一个状态集合求空闭包
        public Set<Integer> emptyTransition(Set<Integer> stateIDs) {
            Set<Integer> closure = new HashSet<>(stateIDs);// 先把自己放进去
            Queue<Integer> queue = new LinkedList<>(stateIDs);

            while (!queue.isEmpty()) {
                int current = queue.poll();// 一个个状态求闭包
                if (transitions.containsKey(current) && transitions.get(current).containsKey('\0')) {
                    for (int nextState : transitions.get(current).get('\0')) {
                        if (!closure.contains(nextState)) {
                            closure.add(nextState);// 重复的不要add
                            queue.add(nextState);
                        }
                    }
                }
            }

            return closure;
        }
    }

    public static class DFA {
        Set<Integer> startStates;
        Set<Integer> endStates;
        Map<Integer, Map<Character, Integer>> transitions;// DFA确定化转移，所以是Map<Character, Integer>

        public DFA() {
            this.startStates = new HashSet<>();
            this.endStates = new HashSet<>();
            this.transitions = new HashMap<>();
        }

        public void addTransition(int from, char input, int to) {
            transitions.putIfAbsent(from, new HashMap<>());
            transitions.get(from).put(input, to);
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
                current++;
            } else {
                nfa.addTransition(current, ch, current + 1);
                current++;
            }
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
            if (!dfa.transitions.containsKey(currentState) || !dfa.transitions.get(currentState).containsKey(input)) {
                return false;
            }
            currentState = dfa.transitions.get(currentState).get(input);
        }

        return dfa.endStates.contains(currentState);
    }
}
