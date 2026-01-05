package by.it.group410971.petrenko.lesson10;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Arrays;

public class MyPriorityQueue<E> implements Queue<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private E[] heap;
    private int size;
    private final Comparator<? super E> comparator;

    @SuppressWarnings("unchecked")
    public MyPriorityQueue() {
        heap = (E[]) new Object[DEFAULT_CAPACITY];
        size = 0;
        comparator = null;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Comparator<? super E> comparator) {
        heap = (E[]) new Object[DEFAULT_CAPACITY];
        size = 0;
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public MyPriorityQueue(Collection<E> c) {
        heap = (E[]) new Object[Math.max(DEFAULT_CAPACITY, c.size())];
        size = 0;
        comparator = null;

        for (E element : c) {
            offer(element);
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        E[] newHeap = (E[]) new Object[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }

    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2);
        } else {
            return ((Comparable<? super E>) e1).compareTo(e2);
        }
    }

    private void siftUp(int index) {
        E element = heap[index];

        while (index > 0) {
            int parent = (index - 1) / 2;

            if (compare(element, heap[parent]) >= 0) {
                break;
            }

            heap[index] = heap[parent];
            index = parent;
        }

        heap[index] = element;
    }

    private void siftDown(int index) {
        E element = heap[index];

        while (index * 2 + 1 < size) {
            int child = index * 2 + 1;

            if (child + 1 < size && compare(heap[child + 1], heap[child]) < 0) {
                child++;
            }

            if (compare(element, heap[child]) <= 0) {
                break;
            }

            heap[index] = heap[child];
            index = child;
        }

        heap[index] = element;
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException("Null elements are not allowed");
        }

        if (size == heap.length) {
            resize();
        }

        heap[size] = element;
        siftUp(size);
        size++;
        return true;
    }

    @Override
    public E remove() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return poll();
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }

        E result = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            siftDown(0);
        }

        return result;
    }

    @Override
    public E element() {
        if (size == 0) {
            throw new NoSuchElementException("Queue is empty");
        }
        return heap[0];
    }

    @Override
    public E peek() {
        if (size == 0) {
            return null;
        }
        return heap[0];
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
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < size; i++) {
            if (o == null ? heap[i] == null : o.equals(heap[i])) {
                return true;
            }
        }
        return false;
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

        boolean modified = false;
        for (E element : c) {
            if (offer(element)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c.isEmpty() || size == 0) {
            return false;
        }

        // строим новое без удаляемых элементов
        @SuppressWarnings("unchecked")
        E[] newHeap = (E[]) new Object[heap.length];
        int newSize = 0;
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            E element = heap[i];
            if (!c.contains(element)) {
                newHeap[newSize] = element;
                newSize++;
            } else {
                modified = true;
            }
        }

        // Заменяем старое новым
        heap = newHeap;
        size = newSize;

        // Перестраиваем
        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }

        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (size == 0) {
            return false;
        }

        if (c.isEmpty()) {
            if (size > 0) {
                clear();
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        E[] newHeap = (E[]) new Object[heap.length];
        int newSize = 0;
        boolean modified = false;

        for (int i = 0; i < size; i++) {
            E element = heap[i];
            if (c.contains(element)) {
                newHeap[newSize] = element;
                newSize++;
            } else {
                modified = true;
            }
        }

        // Заменяем старое новым
        heap = newHeap;
        size = newSize;

        // Перестраиваем
        for (int i = size / 2 - 1; i >= 0; i--) {
            siftDown(i);
        }

        return modified;
    }

    @Override
    public boolean remove(Object o) {
        // Находим индекс элемента
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (o == null ? heap[i] == null : o.equals(heap[i])) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        }

        // Удаляем элемент
        heap[index] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (index < size) {
            siftDown(index);
            if (index > 0 && compare(heap[index], heap[(index - 1) / 2]) < 0) {
                siftUp(index);
            }
        }

        return true;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Остальные методы интерфейса

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] result = (T[]) Arrays.copyOf(heap, size, a.getClass());
            return result;
        }

        System.arraycopy(heap, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    @Override
    public java.util.Iterator<E> iterator() {
        return new java.util.Iterator<E>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                return heap[cursor++];
            }
        };
    }
}