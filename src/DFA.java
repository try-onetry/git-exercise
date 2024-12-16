import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFA {
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

    public Set<Integer> getStartStates() {
        return startStates;
    }

    public Set<Integer> getEndStates() {
        return endStates;
    }

    public Map<Integer, Map<Character, Integer>> getTransitions() {
        return transitions;
    }
}
