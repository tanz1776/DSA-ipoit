package by.it.group410971.petrenko.lesson14;

import java.util.*;

public class StatesHanoiTowerC {

    static class DSU {
        int[] parent;
        int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
        }

        void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (size[rootX] < size[rootY]) {
                    parent[rootX] = rootY;
                    size[rootY] += size[rootX];
                } else {
                    parent[rootY] = rootX;
                    size[rootX] += size[rootY];
                }
            }
        }
    }

    // Итеративное решение Ханойской башни
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        scanner.close();

        if (N <= 0) {
            System.out.println();
            return;
        }

        // Количество состояний = 2^N - 1
        int totalMoves = (1 << N) - 1;

        // Массив для хранения состояний: каждая тройка (a, b, c)
        int[][] states = new int[totalMoves][3];

        // Генерируем состояния
        // Используем итеративный алгоритм
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{N, 0, 1, 2});

        int[] current = {N, 0, 0};
        int index = 0;

        while (!stack.isEmpty()) {
            int[] params = stack.pop();
            int n = params[0];
            int from = params[1];
            int to = params[2];
            int aux = params[3];

            if (n == 1) {
                // Перемещаем диск 1
                current[from]--;
                current[to]++;

                // Сохраняем состояние
                states[index][0] = current[0];
                states[index][1] = current[1];
                states[index][2] = current[2];
                index++;
            } else {
                // Порядок обратный для стека
                stack.push(new int[]{n - 1, aux, to, from});
                stack.push(new int[]{1, from, to, aux});
                stack.push(new int[]{n - 1, from, aux, to});
            }
        }

        DSU dsu = new DSU(totalMoves);

        // Группируем по максимальной высоте
        // Сначала находим максимальную высоту для каждого состояния
        int[] maxHeights = new int[totalMoves];
        for (int i = 0; i < totalMoves; i++) {
            int max = states[i][0];
            if (states[i][1] > max) max = states[i][1];
            if (states[i][2] > max) max = states[i][2];
            maxHeights[i] = max;
        }

        // Объединяем состояния с одинаковой максимальной высотой
        // Используем массив списков для группировки
        List<Integer>[] groups = new ArrayList[N + 1];
        for (int i = 0; i <= N; i++) {
            groups[i] = new ArrayList<>();
        }

        for (int i = 0; i < totalMoves; i++) {
            groups[maxHeights[i]].add(i);
        }

        for (int height = 0; height <= N; height++) {
            List<Integer> indices = groups[height];
            if (indices.size() > 1) {
                int first = indices.get(0);
                for (int j = 1; j < indices.size(); j++) {
                    dsu.union(first, indices.get(j));
                }
            }
        }

        // Собираем размеры кластеров
        List<Integer> clusterSizes = new ArrayList<>();
        for (int i = 0; i < totalMoves; i++) {
            if (dsu.find(i) == i) {
                clusterSizes.add(dsu.size[i]);
            }
        }

        // Сортировка по возрастанию
        int[] sizesArray = new int[clusterSizes.size()];
        for (int i = 0; i < sizesArray.length; i++) {
            sizesArray[i] = clusterSizes.get(i);
        }

        // Пузырьковая сортировка
        for (int i = 0; i < sizesArray.length - 1; i++) {
            for (int j = 0; j < sizesArray.length - i - 1; j++) {
                if (sizesArray[j] > sizesArray[j + 1]) {
                    int temp = sizesArray[j];
                    sizesArray[j] = sizesArray[j + 1];
                    sizesArray[j + 1] = temp;
                }
            }
        }

        // Вывод
        if (sizesArray.length > 0) {
            System.out.print(sizesArray[0]);
            for (int i = 1; i < sizesArray.length; i++) {
                System.out.print(" " + sizesArray[i]);
            }
        }
    }
}