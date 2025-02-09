import java.util.*;

public class NFA {
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
            if (edges.get(input).isEmpty()) {
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

    public Set<Integer> getStartStates() {
        return startStates;
    }

    public Set<Integer> getEndStates() {
        return endStates;
    }

    public void setStartStates(Set<Integer> set) {
        this.startStates = set;
    }

    public void setEndStates(Set<Integer> set) {
        this.endStates = set;
    }

    public void setTransitions(Map<Integer, Map<Character, Set<Integer>>> map) {
        this.transitions = map;
    }


    public Map<Integer, Map<Character, Set<Integer>>> getTransitions() {
        return transitions;
    }
}
