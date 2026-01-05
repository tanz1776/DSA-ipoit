package by.it.group410971.petrenko.lesson12;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Comparator;
import java.util.NavigableSet;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class SplayNode {
        Integer key;
        String value;
        SplayNode left;
        SplayNode right;
        SplayNode parent;

        SplayNode(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private SplayNode root;
    private int size;

    public MySplayMap() {
        root = null;
        size = 0;
    }

    // Вспомогательные методы для splay-дерева
    private void rotate(SplayNode x) {
        SplayNode parent = x.parent;
        if (parent == null) return;

        SplayNode grandparent = parent.parent;

        if (parent.left == x) {
            SplayNode b = x.right;
            x.right = parent;
            parent.left = b;
            if (b != null) b.parent = parent;
        } else {
            SplayNode b = x.left;
            x.left = parent;
            parent.right = b;
            if (b != null) b.parent = parent;
        }

        parent.parent = x;
        x.parent = grandparent;

        if (grandparent != null) {
            if (grandparent.left == parent) {
                grandparent.left = x;
            } else {
                grandparent.right = x;
            }
        }
    }

    private void splay(SplayNode x) {
        while (x.parent != null) {
            SplayNode parent = x.parent;
            SplayNode grandparent = parent.parent;

            if (grandparent == null) {
                rotate(x);
            } else if ((grandparent.left == parent && parent.left == x) ||
                    (grandparent.right == parent && parent.right == x)) {
                rotate(parent);
                rotate(x);
            } else {
                rotate(x);
                rotate(x);
            }
        }
        root = x;
    }

    // Обязательные методы
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        inOrderToString(root, sb);
        if (sb.length() > 1) sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }

    private void inOrderToString(SplayNode node, StringBuilder sb) {
        if (node != null) {
            inOrderToString(node.left, sb);
            sb.append(node.key).append("=").append(node.value).append(", ");
            inOrderToString(node.right, sb);
        }
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();

        if (root == null) {
            root = new SplayNode(key, value);
            size++;
            return null;
        }

        SplayNode current = root;
        SplayNode parent = null;

        while (current != null) {
            parent = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                String oldValue = current.value;
                current.value = value;
                splay(current);
                return oldValue;
            }
        }

        SplayNode newNode = new SplayNode(key, value);
        newNode.parent = parent;

        if (key.compareTo(parent.key) < 0) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }

        splay(newNode);
        size++;
        return null;
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer keyInt = (Integer) key;

        SplayNode node = findNode(keyInt);
        if (node == null) return null;

        String removedValue = node.value;

        if (node.left == null) {
            root = node.right;
            if (root != null) root.parent = null;
        } else if (node.right == null) {
            root = node.left;
            if (root != null) root.parent = null;
        } else {
            SplayNode rightSubtree = node.right;
            rightSubtree.parent = null;

            SplayNode minNode = rightSubtree;
            while (minNode.left != null) {
                minNode = minNode.left;
            }

            splay(minNode);

            minNode.left = node.left;
            if (node.left != null) {
                node.left.parent = minNode;
            }

            root = minNode;
        }

        size--;
        return removedValue;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) return null;
        Integer keyInt = (Integer) key;

        SplayNode node = findNode(keyInt);
        return node != null ? node.value : null;
    }

    private SplayNode findNode(Integer key) {
        SplayNode current = root;
        SplayNode lastVisited = null;

        while (current != null) {
            lastVisited = current;
            int cmp = key.compareTo(current.key);

            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                splay(current);
                return current;
            }
        }

        if (lastVisited != null) splay(lastVisited);
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

    private boolean containsValue(SplayNode node, String value) {
        if (node == null) return false;
        if (value.equals(node.value)) {
            splay(node);
            return true;
        }
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
    public Integer firstKey() {
        if (root == null) throw new java.util.NoSuchElementException();

        SplayNode current = root;
        while (current.left != null) {
            current = current.left;
        }
        splay(current);
        return current.key;
    }

    @Override
    public Integer lastKey() {
        if (root == null) throw new java.util.NoSuchElementException();

        SplayNode current = root;
        while (current.right != null) {
            current = current.right;
        }
        splay(current);
        return current.key;
    }

    @Override
    public Integer lowerKey(Integer key) {
        if (root == null) return null;

        SplayNode node = findNode(key);
        if (node == null) {
            SplayNode current = root;
            SplayNode best = null;

            while (current != null) {
                if (current.key.compareTo(key) < 0) {
                    best = current;
                    current = current.right;
                } else {
                    current = current.left;
                }
            }

            if (best != null) {
                splay(best);
                return best.key;
            }
            return null;
        } else {
            if (node.left != null) {
                SplayNode pred = node.left;
                while (pred.right != null) pred = pred.right;
                splay(pred);
                return pred.key;
            } else {
                SplayNode parent = node.parent;
                SplayNode current = node;
                while (parent != null && current == parent.left) {
                    current = parent;
                    parent = parent.parent;
                }
                if (parent != null) {
                    splay(parent);
                    return parent.key;
                }
                return null;
            }
        }
    }

    @Override
    public Integer floorKey(Integer key) {
        if (root == null) return null;

        SplayNode node = findNode(key);
        if (node != null) return node.key;
        return lowerKey(key);
    }

    @Override
    public Integer ceilingKey(Integer key) {
        if (root == null) return null;

        SplayNode node = findNode(key);
        if (node != null) return node.key;
        return higherKey(key);
    }

    @Override
    public Integer higherKey(Integer key) {
        if (root == null) return null;

        SplayNode node = findNode(key);
        if (node == null) {
            SplayNode current = root;
            SplayNode best = null;

            while (current != null) {
                if (current.key.compareTo(key) > 0) {
                    best = current;
                    current = current.left;
                } else {
                    current = current.right;
                }
            }

            if (best != null) {
                splay(best);
                return best.key;
            }
            return null;
        } else {
            if (node.right != null) {
                SplayNode succ = node.right;
                while (succ.left != null) succ = succ.left;
                splay(succ);
                return succ.key;
            } else {
                SplayNode parent = node.parent;
                SplayNode current = node;
                while (parent != null && current == parent.right) {
                    current = parent;
                    parent = parent.parent;
                }
                if (parent != null) {
                    splay(parent);
                    return parent.key;
                }
                return null;
            }
        }
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey) {
        MySplayMap headMap = new MySplayMap();
        collectHeadMap(root, toKey, headMap);
        return headMap;
    }

    private void collectHeadMap(SplayNode node, Integer toKey, MySplayMap map) {
        if (node == null) return;

        if (node.key.compareTo(toKey) < 0) {
            collectHeadMap(node.left, toKey, map);
            map.put(node.key, node.value);
            collectHeadMap(node.right, toKey, map);
        } else {
            collectHeadMap(node.left, toKey, map);
        }
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey) {
        MySplayMap tailMap = new MySplayMap();
        collectTailMap(root, fromKey, tailMap);
        return tailMap;
    }

    private void collectTailMap(SplayNode node, Integer fromKey, MySplayMap map) {
        if (node == null) return;

        if (node.key.compareTo(fromKey) >= 0) {
            collectTailMap(node.left, fromKey, map);
            map.put(node.key, node.value);
            collectTailMap(node.right, fromKey, map);
        } else {
            collectTailMap(node.right, fromKey, map);
        }
    }

    // остальные методы интерфейса
    @Override
    public Comparator<? super Integer> comparator() {
        return null;
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> descendingMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> navigableKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<Integer> descendingKeySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive,
                                                Integer toKey, boolean toInclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> firstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> lastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> lowerEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> floorEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> ceilingEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map.Entry<Integer, String> higherEntry(Integer key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Set<Map.Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}