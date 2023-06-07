package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {

    private class Node<T> {
        private T data;
        private Node next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }

        public T getData() {
            return data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }
    }
    private Node<T> top;

    private int count;

    public StackImpl() {
        this.top = null;
        this.count = 0;
    }

    /**
     * @param element object to add to the Stack
     */
    @Override
    public void push(T element) {
        Node temp = new Node(element);
        temp.setNext(top);
        top = temp;
        count++;
    }

    /**
     * removes and returns element at the top of the stack
     *
     * @return element at the top of the stack, null if the stack is empty
     */
    @Override
    public T pop() {
        if (this.count == 0) {
            return null;
        }
        T result = top.getData();
        top = top.getNext();
        count--;
        return result;
    }

    /**
     * @return the element at the top of the stack without removing it
     */
    @Override
    public T peek() {
        if (this.count == 0) {
            return null;
        }
        return top.getData();
    }

    /**
     * @return how many elements are currently in the stack
     */
    @Override
    public int size() {
        return this.count;
    }
}