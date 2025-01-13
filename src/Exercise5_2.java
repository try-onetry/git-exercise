import java.util.*;
import java.util.function.Predicate;

import static java.lang.Math.max;

public class Exercise5_2 {
    public static void main(String[] args) {
        isAssert(judge("a*", ""), true);
        isAssert(judge("a+", ""), false);
        isAssert(judge("a*", "a"), true);
        isAssert(judge("a*", "aa"), true);
        isAssert(judge("a*", "b"), false);
        isAssert(judge("aa", "a"), false);
        isAssert(judge("aaa*", "a"), false);
        isAssert(judge("a*bc*d", "abcd"), true);
        isAssert(judge("a*bc*d", "bccd"), true);
        isAssert(judge("", "a"), false);
        isAssert(judge("a|b", ""), false);
        isAssert(judge("a|b", "a"), true);
        isAssert(judge("a|b*", "a"), true);// *,+优先级比|高
        isAssert(judge("a|b*", ""), true);
        isAssert(judge("a|b*", "bb"), true);
        isAssert(judge("a|b*", "aa"), false);
        isAssert(judge("(a*)|b", "b"), true);
        isAssert(judge("((ab)*a)", "aba"), true);
        isAssert(judge("((ab)*a)", "a"), true);
        isAssert(judge("((ab)*a)*", ""), true);
        isAssert(judge("(a(b|c)d+)e", "abde"), true);
        isAssert(judge("(a(b|c)d+)e", "abcd"), false);
        isAssert(judge("()", ""), true);
        testInvalidRegex();
    }

    public static <T> void isAssert(T out, T expect) {
        if (!Objects.equals(out, expect)) {
            throw new IllegalArgumentException("Wrong result");
        }
    }

    public static void testInvalidRegex() {
        List<String> invalidInputs = Arrays.asList("*abc", "+abc", "ab**c", "(()", ")(", "(ab)c)", "|a", "a|", "(|a", "a|*", "a|+");

        for (String regex : invalidInputs) {
            try {
                judge(regex, "abc");
                throw new RuntimeException("Unexpected exception");
            } catch (IllegalArgumentException e) {
                System.out.println("Correctly threw exception for regex: " + regex);
            }
        }
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
        Set<Integer> startState = nfa.emptyTransition(nfa.getStartStates());

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

    public static void isInvalidInput(String regex) {
        if (!regex.isEmpty()) {
            isInvalidSubInput(regex);
            int left = 0, right = 0;
            Stack<Integer> leftBracket = new Stack<>();
            // 先看括号匹配
            for (int i = 0; i < regex.length(); i++) {
                char ch = regex.charAt(i);
                if (ch == '(') left++;
                if (ch == ')') right++;
                if (right > left) throw new IllegalArgumentException("Brackets not match");
            }
            if (!(right == left)) throw new IllegalArgumentException("Brackets not match");
            // 如果括号匹配，再看括号里面的子串是否合法
            for (int i = 0; i < regex.length(); i++) {
                char ch = regex.charAt(i);
                if (ch == '(') {
                    leftBracket.push(i);
                }
                if (ch == ')') {
                    int id = leftBracket.pop();
                    String sub = regex.substring(id + 1, i);
                    isInvalidSubInput(sub);
                    regex = regex.replaceFirst(sub, "");
                }
            }
        }
    }

    // 无括号
    public static void isInvalidSubInput(String regex) {
        if (!regex.isEmpty()) {
            if (regex.charAt(0) == '*' || regex.charAt(0) == '+' || regex.charAt(0) == '|' || regex.charAt(regex.length() - 1) == '|') {
                throw new IllegalArgumentException("Invalid regex input.");
            }
        }
        for (int i = 0; i < regex.length() - 1; i++) {
            char ch = regex.charAt(i);
            char chNext = regex.charAt(i + 1);
            if ((ch == '*' || ch == '+' || ch == '|') && (chNext == '*' || chNext == '+')) {
                throw new IllegalArgumentException("Invalid regex input.");
            }
            if (ch == '|' && chNext == '|') {
                throw new IllegalArgumentException("Invalid regex input.");
            }
        }
    }

    public static NFA buildNFA(String regex) {
        Stack<NFA> nfa = new Stack<>();
        Stack<String> operations = new Stack<>();
        boolean connection = false;
        for (int i = 0; i < regex.length(); i++) {
            char ch = regex.charAt(i);
            if (ch == '*') {
                NFA topNFA = nfa.pop();
                for (Integer end : topNFA.getEndStates()) {
                    for (Integer start : topNFA.getStartStates()) {
                        topNFA.addTransition(end, '\0', start);
                    }
                }
                addNNode(topNFA, 1);
                int startState = 0;
                Set<Integer> startSet = new HashSet<>();
                startSet.add(startState);
                for (Integer start : topNFA.getStartStates()) {
                    topNFA.addTransition(startState, '\0', start);
                }
                Set<Integer> endSet = new HashSet<>();
                int endMax = 0;
                for (Integer end : topNFA.getEndStates()) {
                    endMax = max(end, endMax);
                }
                int endState = endMax + 1;
                for (Integer end : topNFA.getEndStates()) {
                    topNFA.addTransition(end, '\0', endState);
                }
                endSet.add(endState);
                topNFA.setStartStates(startSet);
                topNFA.setEndStates(endSet);
                for (Integer end : topNFA.getEndStates()) {
                    for (Integer start : topNFA.getStartStates()) {
                        topNFA.addTransition(start, '\0', end);
                    }
                }
                nfa.push(topNFA);
            } else if (ch == '+') {
                NFA topNFA = nfa.pop();
                for (Integer end : topNFA.getEndStates()) {
                    for (Integer start : topNFA.getStartStates()) {
                        topNFA.addTransition(end, '\0', start);
                    }
                }
                nfa.push(topNFA);
            } else if (ch == '|') {
                operations.push("|");
                connection = false;
            } else if (ch == '(') {
                operations.push("(");
            } else if (ch == ')') {
                connection = true;
                if (!nfa.isEmpty()) {
                    // 一直出栈到左括号
                    NFA tempNFA = buildNFAFromStack(nfa, operations, ops -> !ops.isEmpty() && !Objects.equals(ops.peek(), "("));
                    nfa.push(tempNFA);
                }
                else nfa.push(buildNullNFA());
                operations.pop(); // 把左括号出栈
            } else {
                if (connection) operations.push("++");
                NFA subNFA = new NFA();
                int current = 0;
                subNFA.addTransition(current, ch, current + 1);
                subNFA.startStates.add(0);
                subNFA.endStates.add(current + 1);
                nfa.push(subNFA);
                connection = true;
            }
        }
        if (!nfa.isEmpty()) return buildNFAFromStack(nfa, operations, ops -> !ops.isEmpty());
         else return buildNullNFA();

    }

    // 只有双目操作符
    public static NFA buildNFAFromStack(Stack<NFA> nfa, Stack<String> operations, Predicate<Stack<String>> loopCondition) {
        while (loopCondition.test(operations)) {
            String op = operations.pop();
            NFA nfa1 = nfa.pop();
            NFA nfa2 = nfa.pop();
            if (Objects.equals(op, "++")) {
                int currentMax = 0;
                for (Integer end : nfa2.getEndStates()) {
                    currentMax = max(end, currentMax);
                }
                for (Integer end : nfa2.getEndStates()) {
                    for (Integer start : nfa1.getStartStates()) {
                        nfa2.addTransition(end, '\0', start + currentMax + 1);
                    }
                }
                // 遍历nfa1的边，合并到nfa2
                for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : nfa1.getTransitions().entrySet()) {
                    int from = entry.getKey();
                    Map<Character, Set<Integer>> edges = entry.getValue();
                    for (Map.Entry<Character, Set<Integer>> edgeEntry : edges.entrySet()) {
                        char input = edgeEntry.getKey();
                        Set<Integer> toSet = edgeEntry.getValue();
                        for (Integer to : toSet) {
                            // 修改起点和终点，+currentMax+1
                            nfa2.addTransition(from + currentMax + 1, input, to + currentMax + 1);
                        }
                    }
                }
                Set<Integer> endState = new HashSet<>();
                for (Integer end : nfa1.getEndStates()) {
                    endState.add(end + currentMax + 1);
                }
                nfa2.setEndStates(endState);
                nfa.push(nfa2);
            } else if (Objects.equals(op, "|")) {
                addNNode(nfa2, 1);
                int currentMax = 0;
                for (Integer end : nfa2.getEndStates()) {
                    currentMax = max(currentMax, end);
                }
                Map<Integer, Map<Character, Set<Integer>>> oldTransitions = nfa2.getTransitions();
                addNNode(nfa1, currentMax + 1);
                Map<Integer, Map<Character, Set<Integer>>> newTransitions = nfa1.getTransitions();
                // 合并 oldTransitions 和 newTransitions
                for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : newTransitions.entrySet()) {
                    Integer fromState = entry.getKey();
                    Map<Character, Set<Integer>> newEdges = entry.getValue();
                    // 获取 oldTransitions 中对应的边集
                    Map<Character, Set<Integer>> oldEdges = oldTransitions.get(fromState);
                    if (oldEdges == null) {
                        // 如果 oldTransitions 中没有这个 fromState，则直接添加
                        oldTransitions.put(fromState, newEdges);
                    } else {
                        // 如果 oldTransitions 中已有这个 fromState，合并每个字符的目标状态集合
                        for (Map.Entry<Character, Set<Integer>> edgeEntry : newEdges.entrySet()) {
                            Character input = edgeEntry.getKey();
                            Set<Integer> newToStates = edgeEntry.getValue();
                            // 获取 oldEdges 中对应的字符的目标状态集
                            Set<Integer> oldToStates = oldEdges.get(input);
                            if (oldToStates == null) {
                                // 如果 oldEdges 中没有该字符的目标状态集，则直接添加
                                oldEdges.put(input, newToStates);
                            } else {
                                // 如果 oldEdges 中已有该字符的目标状态集，合并目标状态
                                oldToStates.addAll(newToStates);
                            }
                        }
                    }
                }
                nfa2.setTransitions(oldTransitions);

                int startState = 0;
                for (Integer start : nfa2.getStartStates()) {
                    nfa2.addTransition(startState, '\0', start);
                }
                for (Integer start : nfa1.getStartStates()) {
                    nfa2.addTransition(startState, '\0', start);
                }
                // 更新终点
                for (Integer end : nfa1.getEndStates()) {
                    currentMax = max(currentMax, end);
                }
                int endState = currentMax + 1;
                for (Integer end : nfa2.getEndStates()) {
                    nfa2.addTransition(end, '\0', endState);
                }
                for (Integer end : nfa1.getEndStates()) {
                    nfa2.addTransition(end, '\0', endState);
                }
                Set<Integer> startStateSet = new HashSet<>();
                startStateSet.add(0);
                nfa2.setStartStates(startStateSet);
                Set<Integer> endStateSet = new HashSet<>();
                endStateSet.add(endState);
                nfa2.setEndStates(endStateSet);
                nfa.push(nfa2);
            } else {
                throw new IllegalArgumentException("Invalid regex input.");
            }
        }
        return nfa.pop();
    }

    public static NFA buildNullNFA(){
        NFA nullNFA = new NFA();
        Set<Integer> oneElement = new HashSet<>();
        oneElement.add(0);
        nullNFA.setStartStates(oneElement);
        oneElement.clear();
        oneElement.add(1);
        nullNFA.setEndStates(oneElement);
        nullNFA.addTransition(0,'\0',1);
        return nullNFA;
    }
    // 只改边，不改初态终态
    public static void addNNode(NFA nfa, int n) {
        Map<Integer, Map<Character, Set<Integer>>> newTransitions = new HashMap<>();
        for (Map.Entry<Integer, Map<Character, Set<Integer>>> entry : nfa.getTransitions().entrySet()) {
            int from = entry.getKey();
            Map<Character, Set<Integer>> edges = entry.getValue();

            for (Map.Entry<Character, Set<Integer>> edgeEntry : edges.entrySet()) {
                char input = edgeEntry.getKey();
                Set<Integer> toSet = edgeEntry.getValue();

                // 遍历目标状态集合 toSet 使用临时集合来保存要添加的新元素
                Set<Integer> newElements = new HashSet<>();
                Iterator<Integer> iterator = toSet.iterator();
                while (iterator.hasNext()) {
                    Integer to = iterator.next();
                    iterator.remove(); // 删除旧元素
                    newElements.add(to + n); // 将新元素加入临时集合
                }

                // 最后将新元素添加回原集合
                toSet.addAll(newElements);
                Map<Character, Set<Integer>> innerMap = new HashMap<>();
                innerMap.put(input, toSet);
                newTransitions.put(from + n, innerMap);
            }
        }
        // 更新 transitions 为新的映射
        nfa.setTransitions(newTransitions);
        Set<Integer> newStart = new HashSet<>();
        for (Integer start : nfa.getStartStates()) {
            start += n;
            newStart.add(start);
        }
        nfa.setStartStates(newStart);
        Set<Integer> newEnd = new HashSet<>();
        for (Integer end : nfa.getEndStates()) {
            end += n;
            newEnd.add(end);
        }
        nfa.setEndStates(newEnd);
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
