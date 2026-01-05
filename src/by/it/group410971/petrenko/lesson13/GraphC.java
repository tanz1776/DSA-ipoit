package by.it.group410971.petrenko.lesson13;

import java.util.*;

public class GraphC {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, List<String>> reverseGraph = new HashMap<>();
        Set<String> vertices = new HashSet<>();

        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split("->");
            String from = parts[0].trim();
            String to = parts[1].trim();

            vertices.add(from);
            vertices.add(to);

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            reverseGraph.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        }

        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String vertex : vertices) {
            if (!visited.contains(vertex)) {
                dfs1(vertex, graph, visited, order);
            }
        }

        Collections.reverse(order);
        visited.clear();
        List<List<String>> components = new ArrayList<>();

        for (String vertex : order) {
            if (!visited.contains(vertex)) {
                List<String> component = new ArrayList<>();
                dfs2(vertex, reverseGraph, visited, component);
                Collections.sort(component);
                components.add(component);
            }
        }

        for (List<String> component : components) {
            System.out.println(String.join("", component));
        }
    }

    private static void dfs1(String vertex, Map<String, List<String>> graph,
                             Set<String> visited, List<String> order) {
        visited.add(vertex);
        if (graph.containsKey(vertex)) {
            for (String neighbor : graph.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfs1(neighbor, graph, visited, order);
                }
            }
        }
        order.add(vertex);
    }

    private static void dfs2(String vertex, Map<String, List<String>> reverseGraph,
                             Set<String> visited, List<String> component) {
        visited.add(vertex);
        component.add(vertex);
        if (reverseGraph.containsKey(vertex)) {
            for (String neighbor : reverseGraph.get(vertex)) {
                if (!visited.contains(neighbor)) {
                    dfs2(neighbor, reverseGraph, visited, component);
                }
            }
        }
    }
}
