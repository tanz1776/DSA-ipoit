package by.it.group410971.petrenko.lesson10;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Collection;

public class MyLinkedList<E> implements Deque<E> {
    private static class Node<E> {
        E data;
        Node<E> prev;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        Node<E> newNode = new Node<>(element);

        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        Node<E> newNode = new Node<>(element);

        if (tail == null) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> current = getNode(index);
        return removeNode(current);
    }

    @Override
    public boolean remove(Object o) {
        Node<E> current = head;

        while (current != null) {
            if (o == null && current.data == null) {
                removeNode(current);
                return true;
            } else if (o != null && o.equals(current.data)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }

        return false;
    }

    private Node<E> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<E> current;
        if (index < size / 2) {
            // Ищем с начала
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            // Ищем с конца
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }

        return current;
    }

    private E removeNode(Node<E> node) {
        if (node == null) {
            return null;
        }

        E data = node.data;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.data = null;
        node.prev = node.next = null;
        size--;

        return data;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }

    @Override
    public E getLast() {
        if (tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (head == null) {
            return null;
        }

        return removeNode(head);
    }

    @Override
    public E pollLast() {
        if (tail == null) {
            return null;
        }

        return removeNode(tail);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;

        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }

        sb.append("]");
        return sb.toString();
    }

    // Обязательные методы Collection<E>

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        Node<E> current = head;
        while (current != null) {
            if (o == null && current.data == null) {
                return true;
            } else if (o != null && o.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public void clear() {
        Node<E> current = head;
        while (current != null) {
            Node<E> next = current.next;
            current.data = null;
            current.prev = null;
            current.next = null;
            current = next;
        }
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E element : c) {
            add(element);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;

        // Создаем новый список только с нужными элементами
        Node<E> newHead = null;
        Node<E> newTail = null;
        int newSize = 0;

        Node<E> current = head;
        while (current != null) {
            if (!c.contains(current.data)) {
                Node<E> newNode = new Node<>(current.data);
                if (newHead == null) {
                    newHead = newTail = newNode;
                } else {
                    newTail.next = newNode;
                    newNode.prev = newTail;
                    newTail = newNode;
                }
                newSize++;
            } else {
                modified = true;
            }
            current = current.next;
        }

        // Очищаем старые узлы
        clear();

        // Устанавливаем новые значения
        head = newHead;
        tail = newTail;
        size = newSize;

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;

        // Создаем новый список только с нужными элементами
        Node<E> newHead = null;
        Node<E> newTail = null;
        int newSize = 0;

        Node<E> current = head;
        while (current != null) {
            if (c.contains(current.data)) {
                Node<E> newNode = new Node<>(current.data);
                if (newHead == null) {
                    newHead = newTail = newNode;
                } else {
                    newTail.next = newNode;
                    newNode.prev = newTail;
                    newTail = newNode;
                }
                newSize++;
            } else {
                modified = true;
            }
            current = current.next;
        }

        // Очищаем старые узлы
        clear();

        // Устанавливаем новые значения
        head = newHead;
        tail = newTail;
        size = newSize;

        return modified;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        Node<E> current = head;
        int i = 0;
        while (current != null) {
            array[i++] = current.data;
            current = current.next;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) toArray();
        }

        Node<E> current = head;
        int i = 0;
        while (current != null) {
            a[i++] = (T) current.data;
            current = current.next;
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    // Остальные методы интерфейса Deque<E>

    @Override public boolean offer(E e) { return false; }
    @Override public boolean offerFirst(E e) { return false; }
    @Override public boolean offerLast(E e) { return false; }
    @Override public E remove() { throw new UnsupportedOperationException(); }
    @Override public E removeFirst() { throw new UnsupportedOperationException(); }
    @Override public E removeLast() { throw new UnsupportedOperationException(); }
    @Override public E peek() { return null; }
    @Override public E peekFirst() { return null; }
    @Override public E peekLast() { return null; }
    @Override public boolean removeFirstOccurrence(Object o) { return false; }
    @Override public boolean removeLastOccurrence(Object o) { return false; }
    @Override public void push(E e) { throw new UnsupportedOperationException(); }
    @Override public E pop() { throw new UnsupportedOperationException(); }
    @Override public java.util.Iterator<E> iterator() { return null; }
    @Override public java.util.Iterator<E> descendingIterator() { return null; }
}