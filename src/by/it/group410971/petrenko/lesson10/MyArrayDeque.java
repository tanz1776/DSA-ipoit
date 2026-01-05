package by.it.group410971.petrenko.lesson10;

import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Arrays;

public class MyArrayDeque<E> implements Deque<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private E[] elements;
    private int head;
    private int tail;
    private int size;

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        head = 0;
        tail = 0;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public MyArrayDeque(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        elements = (E[]) new Object[initialCapacity];
        head = 0;
        tail = 0;
        size = 0;
    }

    private void resize() {
        @SuppressWarnings("unchecked")
        E[] newElements = (E[]) new Object[elements.length * 2];

        // Копируем элементы в новый массив, начиная с head
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(head + i) % elements.length];
        }

        elements = newElements;
        head = 0;
        tail = size > 0 ? size - 1 : 0;
    }

    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override
    public void addFirst(E element) {
        if (size == elements.length) {
            resize();
        }

        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;

        if (size == 1) {
            tail = head;
        }
    }

    @Override
    public void addLast(E element) {
        if (size == elements.length) {
            resize();
        }

        if (size > 0) {
            tail = (tail + 1) % elements.length;
        }
        elements[tail] = element;
        size++;
    }

    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return elements[head];
    }

    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException("Deque is empty");
        }
        return elements[tail];
    }

    @Override
    public E poll() {
        return pollFirst();
    }

    @Override
    public E pollFirst() {
        if (size == 0) {
            return null;
        }

        E element = elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;

        if (size == 0) {
            head = tail = 0;
        }

        return element;
    }

    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }

        E element = elements[tail];
        elements[tail] = null;
        tail = (tail - 1 + elements.length) % elements.length;
        size--;

        if (size == 0) {
            head = tail = 0;
        }

        return element;
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
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (elements[index] != null) {
                sb.append(elements[index]);
                if (i < size - 1) {
                    sb.append(", ");
                }
            }
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
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (o == null ? elements[index] == null : o.equals(elements[index])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            if (o == null ? elements[index] == null : o.equals(elements[index])) {
                removeAtIndex(i);
                return true;
            }
        }
        return false;
    }

    private void removeAtIndex(int position) {
        if (position < 0 || position >= size) {
            return;
        }

        int actualIndex = (head + position) % elements.length;

        // Сдвигаем все элементы справа от удаляемого на одну позицию влево
        for (int i = position; i < size - 1; i++) {
            int current = (head + i) % elements.length;
            int next = (head + i + 1) % elements.length;
            elements[current] = elements[next];
        }

        // Очищаем последний элемент
        elements[tail] = null;
        size--;

        if (size == 0) {
            head = tail = 0;
        } else {
            tail = (tail - 1 + elements.length) % elements.length;
            if (tail < 0) {
                tail = elements.length - 1;
            }
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            elements[index] = null;
        }
        head = 0;
        tail = 0;
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
        if (c.isEmpty()) {
            return false;
        }
        for (E element : c) {
            add(element);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty()) {
            return false;
        }

        boolean modified = false;
        // Создаем новый массив без удаляемых элементов
        @SuppressWarnings("unchecked")
        E[] newElements = (E[]) new Object[elements.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            E element = elements[index];
            if (!c.contains(element)) {
                newElements[newSize] = element;
                newSize++;
            } else {
                modified = true;
            }
        }

        elements = newElements;
        head = 0;
        tail = newSize > 0 ? newSize - 1 : 0;
        size = newSize;

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        // Создаем новый массив только с нужными элементами
        @SuppressWarnings("unchecked")
        E[] newElements = (E[]) new Object[elements.length];
        int newSize = 0;

        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            E element = elements[index];
            if (c.contains(element)) {
                newElements[newSize] = element;
                newSize++;
            } else {
                modified = true;
            }
        }

        elements = newElements;
        head = 0;
        tail = newSize > 0 ? newSize - 1 : 0;
        size = newSize;

        return modified;
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            int index = (head + i) % elements.length;
            array[i] = elements[index];
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
        }

        System.arraycopy(toArray(), 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    // Остальные методы интерфейса Deque<E>

    @Override public boolean offer(E e) { addLast(e); return true; }
    @Override public boolean offerFirst(E e) { addFirst(e); return true; }
    @Override public boolean offerLast(E e) { addLast(e); return true; }
    @Override public E remove() { if (size == 0) throw new NoSuchElementException(); return pollFirst(); }
    @Override public E removeFirst() { if (size == 0) throw new NoSuchElementException(); return pollFirst(); }
    @Override public E removeLast() { if (size == 0) throw new NoSuchElementException(); return pollLast(); }
    @Override public E peek() { return size == 0 ? null : getFirst(); }
    @Override public E peekFirst() { return size == 0 ? null : getFirst(); }
    @Override public E peekLast() { return size == 0 ? null : getLast(); }
    @Override public boolean removeFirstOccurrence(Object o) { return remove(o); }
    @Override public boolean removeLastOccurrence(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            int index = (head + i) % elements.length;
            if (o == null ? elements[index] == null : o.equals(elements[index])) {
                removeAtIndex(i);
                return true;
            }
        }
        return false;
    }
    @Override public void push(E e) { addFirst(e); }
    @Override public E pop() { return removeFirst(); }
    @Override public java.util.Iterator<E> iterator() {
        return new java.util.Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                int index = (head + cursor) % elements.length;
                cursor++;
                return elements[index];
            }
        };
    }
    @Override public java.util.Iterator<E> descendingIterator() {
        return new java.util.Iterator<E>() {
            private int cursor = size - 1;

            @Override
            public boolean hasNext() {
                return cursor >= 0;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                int index = (head + cursor) % elements.length;
                cursor--;
                return elements[index];
            }
        };
    }
}