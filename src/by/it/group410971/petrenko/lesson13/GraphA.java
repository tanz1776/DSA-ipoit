package by.it.group410971.petrenko.lesson13;

import java.util.*;

public class GraphA {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Set<String> vertices = new HashSet<>();

        String[] edges = input.split(", ");
        for (String edge : edges) {
            String[] parts = edge.split(" -> ");
            String from = parts[0];
            String to = parts[1];

            vertices.add(from);
            vertices.add(to);

            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            inDegree.putIfAbsent(to, 0);
            inDegree.putIfAbsent(from, 0);
            inDegree.put(to, inDegree.get(to) + 1);
        }

        PriorityQueue<String> queue = new PriorityQueue<>();
        for (String vertex : vertices) {
            if (inDegree.getOrDefault(vertex, 0) == 0) {
                queue.offer(vertex);
            }
        }

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            if (graph.containsKey(current)) {
                List<String> neighbors = new ArrayList<>(graph.get(current));
                Collections.sort(neighbors);
                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.offer(neighbor);
                    }
                }
            }
        }

        System.out.println(String.join(" ", result));
    }
}