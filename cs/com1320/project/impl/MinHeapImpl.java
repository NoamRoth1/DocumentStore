package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.MinHeap;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E> {


    public MinHeapImpl() {
        this.elements = (E[]) new Comparable[1];
    }

    @Override
    public void reHeapify(E element) {
        int position = this.getArrayIndex(element);
        this.upHeap(position);
        this.downHeap(position);
    }

    @Override
    protected int getArrayIndex(E element) {
        for (int i = 1; i < this.count+1; i++) {
            if (this.elements[i].equals(element)) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    protected void doubleArraySize() {
        //create an array twice the size of the current array
        E[] doubleArray = (E[]) new Comparable[this.elements.length * 2];
        //copy over all the elements to the new array
        for (int i = 0; i < this.elements.length; i++) {
            doubleArray[i] = this.elements[i];
        }
        this.elements = doubleArray;
    }
}