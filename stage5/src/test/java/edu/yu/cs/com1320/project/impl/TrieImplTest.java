package edu.yu.cs.com1320.project.impl;

import com.sun.jdi.Value;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javax.print.Doc;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class TrieImplTest {
    TrieImpl<Integer> tri;
    TrieImpl<Integer> trie;
    TrieImpl.Node<Integer> node;

    @BeforeEach
    void setUp() {
        tri = new TrieImpl<>();
        trie = new TrieImpl<>();
        node = new TrieImpl.Node<>();
    }

    @Test
    void put() {
        TrieImpl<Integer> tri = new TrieImpl<>();
        tri.put("Hi", 6);
        assertEquals("Hi", "Hi");
    }

    @Test
    void getAllSorted() {
        TrieImpl<Document> trip = new TrieImpl<>();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        trip.put("the", document1);
        trip.put("the", document2);
        trip.put("the", document3);
        Comparator<Document> comp = (o1, o2) -> {
            if (o1.wordCount("the") > o2.wordCount("the")) {
                return -1;
            }
            if (o1.wordCount("the") == o2.wordCount("the")) {
                return 0;
            }
            if (o1.wordCount("the") < o2.wordCount("the")) {
                return 1;
            }
            return 1;
        };
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document2);
        list.add(document1);
        assertEquals(list, trip.getAllSorted("the", comp));
    }

    @Test
    void getAllWithPrefixSorted() {
        TrieImpl<Document> trip = new TrieImpl<>();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        trip.put("th", document3);
        trip.put("th", document1);
        trip.put("th", document2);
        Comparator<Document> comp = (o1, o2) -> {
            if (o1.wordCount("th") > o2.wordCount("th")) {
                return -1;
            }
            if (o1.wordCount("th") == o2.wordCount("th")) {
                return 0;
            }
            if (o1.wordCount("th") < o2.wordCount("th")) {
                return 1;
            }
            return 1;
        };
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document1);
        list.add(document2);
        assertEquals(list, trip.getAllWithPrefixSorted("th", comp));
    }

    @Test
    void deleteAllWithPrefix() {
        TrieImpl<Document> trip = new TrieImpl<>();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        trip.put("the", document3);
        trip.put("the", document1);
        trip.put("the", document2);
        Comparator<Document> comp = (o1, o2) -> {
            if (o1.wordCount("th") > o2.wordCount("th")) {
                return -1;
            }
            if (o1.wordCount("th") == o2.wordCount("th")) {
                return 0;
            }
            if (o1.wordCount("th") < o2.wordCount("th")) {
                return 1;
            }
            return 1;
        };
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document2);
        list.add(document1);
        HashSet<Document> setOfDeletedValues = new HashSet<>(list);
        assertEquals(setOfDeletedValues, trip.deleteAllWithPrefix("th"));
        List<Document> emptyList = Collections.<Document>emptyList();;
        assertEquals(emptyList, trip.getAllSorted("the", comp));
    }

    @Test
    @DisplayName("Check that putting multiple values and then deletingAll() returns the deleted values")
    void deleteAll1() {
        TrieImpl<Integer> tri = new TrieImpl<>();
        Set<Integer> intsSet = new HashSet<>();
        intsSet.add(6);
        intsSet.add(5);
        intsSet.add(4);
        tri.put("Hi", 6);
        tri.put("Hi", 5);
        tri.put("Hi", 4);
        assertEquals(intsSet,tri.deleteAll("Hi"));
    }

    @Test
    void deleteAll2() {
        TrieImpl<Document> trip = new TrieImpl<>();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        trip.put("the", document3);
        trip.put("the", document1);
        trip.put("the", document2);
        Comparator<Document> comp = (o1, o2) -> {
            if (o1.wordCount("the") > o2.wordCount("the")) {
                return -1;
            }
            if (o1.wordCount("the") == o2.wordCount("the")) {
                return 0;
            }
            if (o1.wordCount("the") < o2.wordCount("the")) {
                return 1;
            }
            return 1;
        };
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document2);
        list.add(document1);
        HashSet<Document> setOfDeletedValues = new HashSet<>(list);
        assertEquals(setOfDeletedValues, trip.deleteAll("the"));
        List<Document> emptyList = Collections.<Document>emptyList();;
        assertEquals(emptyList, trip.getAllSorted("the", comp));
    }



    @Test
    @DisplayName("Check that putting a value and that deleting that value returns the value")
    void delete1() {
        TrieImpl<Integer> tri = new TrieImpl<>();
        tri.put("Hi", 6);
        assertEquals(6, tri.delete("Hi", 6));
    }

    @Test
    @DisplayName("Check that putting a value and that deleting a different value returns null")
    void delete2() {
        TrieImpl<Integer> tri = new TrieImpl<>();
        tri.put("Hi", 6);
        assertNull(tri.delete("Hi", 5));
    }

    @Test
    @DisplayName("Checking Case Sensitivity: Check that putting a value for hi and HI and then deleting hi doesn't affect HI")
    void delete3() {
        TrieImpl<Integer> tri = new TrieImpl<>();
        tri.put("hi", 5);
        tri.put("Hi", 6);
        assertEquals(5, tri.delete("hi", 5));
    }

    @Test
    public void testPutAndGetAllSorted() {
        trie.put("apple", 1);
        trie.put("banana", 2);
        trie.put("orange", 3);
        trie.put("orange", 4);

        List<Integer> expectedValues = new ArrayList<>();
        expectedValues.add(2);

        List<Integer> actualValues = trie.getAllSorted("banana", Comparator.reverseOrder());
        assertEquals(expectedValues, actualValues);

        expectedValues = new ArrayList<>();
        expectedValues.add(4);
        expectedValues.add(3);

        actualValues = trie.getAllSorted("orange", Comparator.reverseOrder());
        assertEquals(expectedValues, actualValues);
    }

    @Test
    public void testGetAllWithPrefixSorted() {
        trie.put("apple", 1);
        trie.put("banana", 2);
        trie.put("orange", 3);
        trie.put("pear", 4);

        List<Integer> expectedValues = new ArrayList<>();
        expectedValues.add(1);

        List<Integer> actualValues = trie.getAllWithPrefixSorted("ap", Comparator.reverseOrder());
        assertEquals(expectedValues, actualValues);

        expectedValues = new ArrayList<>();
        expectedValues.add(3);

        actualValues = trie.getAllWithPrefixSorted("or", Comparator.reverseOrder());
        assertEquals(expectedValues, actualValues);
    }

    @Test
    public void testDeleteAllWithPrefix() {
        trie.put("apple", 1);
        trie.put("banana", 2);
        trie.put("orange", 3);
        trie.put("pear", 4);
        trie.put("peach", 5);

        Set<Integer> expectedValues = new HashSet<>();
        expectedValues.add(4);
        expectedValues.add(5);

        Set<Integer> actualValues = trie.deleteAllWithPrefix("pe");
        assertEquals(expectedValues, actualValues);

        // Make sure the values were actually deleted
        assertNull(trie.delete("pear", 4));
        assertNull(trie.delete("peach", 5));
    }

    @Test
    public void testDeleteAll() {
        trie.put("apple", 1);
        trie.put("banana", 2);
        trie.put("orange", 3);
        trie.put("pear", 4);
        trie.put("peach", 5);

        Set<Integer> expectedValues = new HashSet<>();
        expectedValues.add(1);

        Set<Integer> actualValues = trie.deleteAll("apple");
        assertEquals(expectedValues, actualValues);

        // Make sure the value was actually deleted
        assertNull(trie.delete("apple", 1));
    }

    @Test
    public void testDelete() {
        trie.put("apple", 1);
        trie.put("banana", 2);
        trie.put("orange", 3);
        trie.put("pear", 4);
        trie.put("peach", 5);

        Integer deletedValue = trie.delete("pear", 4);
        assertEquals(Integer.valueOf(4), deletedValue);

        // Make sure the value was actually deleted
        assertNull(trie.delete("pear", 4));
    }

}