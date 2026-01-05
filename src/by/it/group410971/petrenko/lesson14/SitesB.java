package by.it.group410971.petrenko.lesson14;

import java.util.*;

public class SitesB {

    static class DSU {
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> rank = new HashMap<>();
        Map<String, Integer> size = new HashMap<>();

        void makeSet(String x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                rank.put(x, 0);
                size.put(x, 1);
            }
        }

        String find(String x) {
            if (!parent.get(x).equals(x)) {
                parent.put(x, find(parent.get(x))); // path compression
            }
            return parent.get(x);
        }

        void union(String x, String y) {
            String rootX = find(x);
            String rootY = find(y);

            if (!rootX.equals(rootY)) {
                if (rank.get(rootX) < rank.get(rootY)) {
                    parent.put(rootX, rootY);
                    size.put(rootY, size.get(rootY) + size.get(rootX));
                } else if (rank.get(rootX) > rank.get(rootY)) {
                    parent.put(rootY, rootX);
                    size.put(rootX, size.get(rootX) + size.get(rootY));
                } else {
                    parent.put(rootY, rootX);
                    size.put(rootX, size.get(rootX) + size.get(rootY));
                    rank.put(rootX, rank.get(rootX) + 1); // rank heuristic
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();
        List<String> allSites = new ArrayList<>();

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equals("end")) {
                break;
            }

            String[] sites = line.split("\\+");
            String site1 = sites[0];
            String site2 = sites[1];

            allSites.add(site1);
            allSites.add(site2);

            dsu.makeSet(site1);
            dsu.makeSet(site2);
            dsu.union(site1, site2);
        }
        scanner.close();

        Set<String> processedRoots = new HashSet<>();
        List<Integer> clusterSizes = new ArrayList<>();

        for (String site : allSites) {
            String root = dsu.find(site);
            if (!processedRoots.contains(root)) {
                clusterSizes.add(dsu.size.get(root));
                processedRoots.add(root);
            }
        }

        Collections.sort(clusterSizes, Collections.reverseOrder());

        for (int size : clusterSizes) {
            System.out.print(size + " ");
        }
    }
}