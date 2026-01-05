package by.it.group410971.petrenko.lesson12;

import java.util.Map;
import java.util.SortedMap;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class RbNode {
        Integer key;
        String value;
        RbNode left;
        RbNode right;
        boolean color;

        RbNode(Integer key, String value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    private RbNode root;
    private int size;

    public MyRbMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы
    private boolean isRed(RbNode node) {
        return node != null && node.color == RED;
    }

    private RbNode rotateLeft(RbNode h) {
        RbNode x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private RbNode rotateRight(RbNode h) {
        RbNode x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    private void flipColors(RbNode h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    private RbNode balance(RbNode h) {
        if (isRed(h.right)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);
        return h;
    }

    private RbNode moveRedLeft(RbNode h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private RbNode moveRedRight(RbNode h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        String[] oldValue = new String[1];
        root = put(root, key, value, oldValue);
        root.color = BLACK;
        if (oldValue[0] == null) size++;
        return oldValue[0];
    }

    private RbNode put(RbNode h, Integer key, String value, String[] oldValue) {
        if (h == null) return new RbNode(key, value, RED);

        int cmp = key.compareTo(h.key);
        if (cmp < 0) {
            h.left = put(h.left, key, value, oldValue);
        } else if (cmp > 0) {
            h.right = put(h.right, key, value, oldValue);
        } else {
            oldValue[0] = h.value;
            h.value = value;
            return h;
        }

        return balance(h);
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer keyInt = (Integer) key;

        if (!containsKey(keyInt)) return null;

        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }

        String[] removedValue = new String[1];
        root = remove(root, keyInt, removedValue);
        if (root != null) root.color = BLACK;
        if (removedValue[0] != null) size--;
        return removedValue[0];
    }

    private RbNode remove(RbNode h, Integer key, String[] removedValue) {
        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = remove(h.left, key, removedValue);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }
            if (key.compareTo(h.key) == 0 && (h.right == null)) {
                removedValue[0] = h.value;
                return null;
            }
            if (!isRed(h.right) && !isRed(h.right.left)) {
                h = moveRedRight(h);
            }
            if (key.compareTo(h.key) == 0) {
                removedValue[0] = h.value;
                RbNode x = min(h.right);
                h.key = x.key;
                h.value = x.value;
                h.right = deleteMin(h.right);
            } else {
                h.right = remove(h.right, key, removedValue);
            }
        }
        return balance(h);
    }

    private RbNode deleteMin(RbNode h) {
        if (h.left == null) return null;

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);
        return balance(h);
    }

    private RbNode min(RbNode x) {
        while (x.left != null) x = x.left;
        return x;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer keyInt = (Integer) key;

        RbNode x = root;
        while (x != null) {
            int cmp = keyInt.compareTo(x.key);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else return x.value;
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof String)) return false;
        return containsValue(root, (String) value);
    }

    private boolean containsValue(RbNode node, String value) {
        if (node == null) return false;
        if (value.equals(node.value)) return true;
        return containsValue(node.left, value) || containsValue(node.right, value);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        inorderToString(root, sb);
        if (sb.length() > 1) sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }

    private void inorderToString(RbNode node, StringBuilder sb) {
        if (node != null) {
            inorderToString(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inorderToString(node.right, sb);
        }
    }

    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        RbNode x = root;
        while (x.left != null) x = x.left;
        return x.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        RbNode x = root;
        while (x.right != null) x = x.right;
        return x.key;
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) throw new NullPointerException();
        MyRbMap headMap = new MyRbMap();
        headMap(root, toKey, headMap);
        return headMap;
    }

    private void headMap(RbNode node, Integer toKey, MyRbMap map) {
        if (node == null) return;
        if (node.key.compareTo(toKey) < 0) {
            headMap(node.left, toKey, map);
            map.put(node.key, node.value);
            headMap(node.right, toKey, map);
        } else {
            headMap(node.left, toKey, map);
        }
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) throw new NullPointerException();
        MyRbMap tailMap = new MyRbMap();
        tailMap(root, fromKey, tailMap);
        return tailMap;
    }

    private void tailMap(RbNode node, Integer fromKey, MyRbMap map) {
        if (node == null) return;
        if (node.key.compareTo(fromKey) >= 0) {
            tailMap(node.left, fromKey, map);
            map.put(node.key, node.value);
            tailMap(node.right, fromKey, map);
        } else {
            tailMap(node.right, fromKey, map);
        }
    }

    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) throw new NullPointerException();
        if (fromKey.compareTo(toKey) > 0) throw new IllegalArgumentException();

        MyRbMap subMap = new MyRbMap();
        subMap(root, fromKey, toKey, subMap);
        return subMap;
    }

    private void subMap(RbNode node, Integer fromKey, Integer toKey, MyRbMap map) {
        if (node == null) return;
        if (node.key.compareTo(fromKey) >= 0 && node.key.compareTo(toKey) < 0) {
            subMap(node.left, fromKey, toKey, map);
            map.put(node.key, node.value);
            subMap(node.right, fromKey, toKey, map);
        } else if (node.key.compareTo(fromKey) < 0) {
            subMap(node.right, fromKey, toKey, map);
        } else {
            subMap(node.left, fromKey, toKey, map);
        }
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        if (m == null) throw new NullPointerException();
        for (Entry<? extends Integer, ? extends String> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public java.util.Set<Integer> keySet() {
        java.util.Set<Integer> keys = new java.util.TreeSet<>();
        keySet(root, keys);
        return keys;
    }

    private void keySet(RbNode node, java.util.Set<Integer> keys) {
        if (node != null) {
            keySet(node.left, keys);
            keys.add(node.key);
            keySet(node.right, keys);
        }
    }

    @Override
    public java.util.Collection<String> values() {
        java.util.List<String> values = new java.util.ArrayList<>();
        values(root, values);
        return values;
    }

    private void values(RbNode node, java.util.Collection<String> values) {
        if (node != null) {
            values(node.left, values);
            values.add(node.value);
            values(node.right, values);
        }
    }

    @Override
    public java.util.Set<Entry<Integer, String>> entrySet() {
        java.util.Set<Entry<Integer, String>> entries = new java.util.HashSet<>();
        entrySet(root, entries);
        return entries;
    }

    private void entrySet(RbNode node, java.util.Set<Entry<Integer, String>> entries) {
        if (node != null) {
            entrySet(node.left, entries);
            entries.add(new java.util.AbstractMap.SimpleEntry<>(node.key, node.value));
            entrySet(node.right, entries);
        }
    }
}