package by.it.group410971.petrenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private LinkedNode<E>[] table;
    private LinkedNode<E> head; // первый добавленный элемент
    private LinkedNode<E> tail; // последний добавленный элемент
    private int size;
    private final float loadFactor;

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (LinkedNode<E>[]) new LinkedNode[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet(int initialCapacity) {
        this.table = (LinkedNode<E>[]) new LinkedNode[initialCapacity];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    private static class LinkedNode<E> {
        E element;
        LinkedNode<E> next; // для разрешения коллизий
        LinkedNode<E> before; // предыдущий в порядке добавления
        LinkedNode<E> after; // следующий в порядке добавления

        LinkedNode(E element, LinkedNode<E> next) {
            this.element = element;
            this.next = next;
            this.before = null;
            this.after = null;
        }
    }

    private int getIndex(Object key) {
        if (key == null) return 0;
        int hash = key.hashCode();
        return (hash & 0x7FFFFFFF) % table.length;
    }

    private void linkLast(LinkedNode<E> node) {
        LinkedNode<E> last = tail;
        node.before = last;
        node.after = null;
        tail = node;

        if (last == null) {
            head = node;
        } else {
            last.after = node;
        }
    }

    private void unlink(LinkedNode<E> node) {
        LinkedNode<E> before = node.before;
        LinkedNode<E> after = node.after;

        if (before == null) {
            head = after;
        } else {
            before.after = after;
            node.before = null;
        }

        if (after == null) {
            tail = before;
        } else {
            after.before = before;
            node.after = null;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        int index = getIndex(o);
        LinkedNode<E> current = table[index];

        while (current != null) {
            if (o == null) {
                if (current.element == null) return true;
            } else {
                if (o.equals(current.element)) return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedNode<E>[] oldTable = table;
        table = (LinkedNode<E>[]) new LinkedNode[oldTable.length * 2];

        // Сохраняем порядок добавления
        LinkedNode<E> currentInOrder = head;
        head = null;
        tail = null;
        size = 0;

        while (currentInOrder != null) {
            add(currentInOrder.element);
            currentInOrder = currentInOrder.after;
        }
    }

    @Override
    public boolean add(E e) {
        if (size > table.length * loadFactor) {
            resize();
        }

        int index = getIndex(e);
        LinkedNode<E> current = table[index];

        // Проверяем, существует ли уже элемент
        while (current != null) {
            if (e == null) {
                if (current.element == null) return false;
            } else {
                if (e.equals(current.element)) return false;
            }
            current = current.next;
        }

        // Добавляем новый элемент в начало списка коллизий
        LinkedNode<E> newNode = new LinkedNode<>(e, table[index]);
        table[index] = newNode;

        // Добавляем в конец порядка добавления
        linkLast(newNode);
        size++;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        int index = getIndex(o);
        LinkedNode<E> current = table[index];
        LinkedNode<E> prev = null;

        while (current != null) {
            if (o == null) {
                if (current.element == null) {
                    // Удаляем из списка коллизий
                    if (prev == null) {
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }

                    // Удаляем из порядка добавления
                    unlink(current);
                    size--;
                    return true;
                }
            } else {
                if (o.equals(current.element)) {
                    // Удаляем из списка коллизий
                    if (prev == null) {
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }

                    // Удаляем из порядка добавления
                    unlink(current);
                    size--;
                    return true;
                }
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            if (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        LinkedNode<E> current = head;

        while (current != null) {
            LinkedNode<E> next = current.after;
            if (!c.contains(current.element)) {
                remove(current.element);
                modified = true;
            }
            current = next;
        }
        return modified;
    }

    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";

        StringBuilder sb = new StringBuilder("[");
        LinkedNode<E> current = head;
        boolean first = true;

        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.element);
            first = false;
            current = current.after;
        }
        sb.append("]");
        return sb.toString();
    }

    // Остальные методы
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }
}