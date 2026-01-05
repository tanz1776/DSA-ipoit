package by.it.group410971.petrenko.lesson13;

import java.util.*;

public class GraphB {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();
        Set<String> vertices = new HashSet<>();

        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0];
            String to = parts[1];

            vertices.add(from);
            vertices.add(to);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }

        boolean hasCycle = false;
        Map<String, Integer> state = new HashMap<>();
        for (String vertex : vertices) {
            state.put(vertex, 0);
        }

        for (String vertex : vertices) {
            if (state.get(vertex) == 0) {
                if (dfs(vertex, graph, state)) {
                    hasCycle = true;
                    break;
                }
            }
        }

        System.out.println(hasCycle ? "yes" : "no");
    }

    private static boolean dfs(String vertex, Map<String, List<String>> graph,
                               Map<String, Integer> state) {
        state.put(vertex, 1);

        if (graph.containsKey(vertex)) {
            for (String neighbor : graph.get(vertex)) {
                if (state.get(neighbor) == 0) {
                    if (dfs(neighbor, graph, state)) {
                        return true;
                    }
                } else if (state.get(neighbor) == 1) {
                    return true;
                }
            }
        }

        state.put(vertex, 2);
        return false;
    }
}