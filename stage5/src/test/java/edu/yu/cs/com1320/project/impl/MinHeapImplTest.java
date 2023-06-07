package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapImplTest {
    private MinHeapImpl<Document> minHeap;
    private URI uri1;
    private DocumentImpl doc1;
    private URI uri2;
    private DocumentImpl doc2;
    private URI uri3;
    private DocumentImpl doc3;
    private URI uri4;
    private DocumentImpl doc4;
    private URI uri5;
    private DocumentImpl doc5;
    private URI uri6;
    private DocumentImpl doc6;
    private URI uri7;
    private DocumentImpl doc7;
    private URI uri8;
    private DocumentImpl doc8;


    @BeforeEach
    void setUp() {
        this.minHeap = new MinHeapImpl<>();
        this.uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        this.doc1 = new DocumentImpl(this.uri1, text1);
        this.uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        this.doc2 = new DocumentImpl(this.uri2, text2);
        this.uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        this.doc3 = new DocumentImpl(this.uri3, text3);
        this.uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        this.doc4 = new DocumentImpl(this.uri4, text4);
        this.uri5 = URI.create("http://www.yu.edu/documents/doc5");
        String text5 = "This is the text content of the fifth document.";
        byte[] byteArrray5 = text5.getBytes();
        this.doc5 = new DocumentImpl(uri5, text5);
        this.uri6 = URI.create("http://www.yu.edu/documents/doc6");
        String text6 = "This is the text content of the sixth document.";
        byte[] byteArrray6 = text6.getBytes();
        this.doc6 = new DocumentImpl(uri6, text6);
        this.uri7 = URI.create("http://www.yu.edu/documents/doc7");
        String text7 = "This is the text content of the seventh document.";
        byte[] byteArrray7 = text7.getBytes();
        this.doc7 = new DocumentImpl(uri7, text7);
        this.uri8 = URI.create("http://www.yu.edu/documents/doc8");
        String text8 = "This is the text content of the eighth document.";
        byte[] byteArrray8 = text8.getBytes();
        this.doc8 = new DocumentImpl(uri8, text8);
        this.minHeap.insert(doc1);
        this.minHeap.insert(doc2);
        this.minHeap.insert(doc3);
        this.minHeap.insert(doc4);
        this.minHeap.insert(doc5);
        this.minHeap.insert(doc6);
        this.minHeap.insert(doc7);
        this.minHeap.insert(doc8);

    }
    @Test
    @DisplayName("Check calling reHeapify() on the smallest document moves the document to the top")
    void reHeapify1() {
        this.doc1.setLastUseTime(System.nanoTime());
        this.doc2.setLastUseTime(System.nanoTime());
        this.doc4.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc3);
        assertEquals(1, this.minHeap.getArrayIndex(doc3));
    }

    @Test
    @DisplayName("Check calling reHeapify() on the biggest document moves the document to the bottom")
    void reHeapify2() {
        this.doc1.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc1);
        assertNotEquals(1, this.minHeap.getArrayIndex(doc1));
        assertNotEquals(2, this.minHeap.getArrayIndex(doc1));
    }

    @Test
    @DisplayName("Check calling reHeapify() on a internal document moves the document to the top")
    void reHeapify3() {
        this.doc6.setLastUseTime(System.nanoTime());
        this.doc8.setLastUseTime(System.nanoTime());
        this.doc7.setLastUseTime(System.nanoTime());
        this.doc5.setLastUseTime(System.nanoTime());
        this.doc4.setLastUseTime(System.nanoTime());
        this.doc3.setLastUseTime(System.nanoTime());
        this.doc2.setLastUseTime(System.nanoTime());
        this.doc1.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc6);
        assertEquals(1 , this.minHeap.getArrayIndex(doc6));
    }

    @Test
    @DisplayName("Check calling reHeapify() on the oldest moves the document to the top")
    void reHeapify4() {
        this.doc8.setLastUseTime(System.nanoTime());
        this.doc7.setLastUseTime(System.nanoTime());
        this.doc6.setLastUseTime(System.nanoTime());
        this.doc5.setLastUseTime(System.nanoTime());
        this.doc4.setLastUseTime(System.nanoTime());
        this.doc3.setLastUseTime(System.nanoTime());
        this.doc2.setLastUseTime(System.nanoTime());
        this.doc1.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc8);
        assertEquals(1 , this.minHeap.getArrayIndex(doc8));
    }

    @Test
    @DisplayName("Check calling reHeapify() on the newest moves the document to the bottom")
    void reHeapify5() {
        this.doc8.setLastUseTime(System.nanoTime());
        this.doc7.setLastUseTime(System.nanoTime());
        this.doc6.setLastUseTime(System.nanoTime());
        this.doc5.setLastUseTime(System.nanoTime());
        this.doc4.setLastUseTime(System.nanoTime());
        this.doc3.setLastUseTime(System.nanoTime());
        this.doc2.setLastUseTime(System.nanoTime());
        this.doc1.setLastUseTime(System.nanoTime());
        this.minHeap.reHeapify(doc1);
        assertEquals(7 , this.minHeap.getArrayIndex(doc1));
    }


    @Test
    void getArrayIndex() {
        assertEquals(3, this.minHeap.getArrayIndex(doc3));
    }

    @Test
    void doubleArraySize() {

    }
}