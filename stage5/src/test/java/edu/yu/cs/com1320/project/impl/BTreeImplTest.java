package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class BTreeImplTest {

    @Test
    void get1() {
        BTreeImpl<String, Integer> bTree = new BTreeImpl<>();
        bTree.put("one", 1);
        assertEquals(1, bTree.get("one"));
    }

    @Test
    void put() {
    }

    @Test
    void putNull() {
        BTreeImpl<String, Integer> bTree = new BTreeImpl<>();
        bTree.put("one", 1);
        bTree.put("one", null);
        assertNull(bTree.get("one"));
    }

    @Test
    void moveToDisk() throws Exception {
        BTreeImpl<URI, Document> bTree = new BTreeImpl<>();
        bTree.setPersistenceManager(new DocumentPersistenceManager(null));
        final URI uri2 = URI.create("https://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        bTree.put(uri2, document2);
        bTree.moveToDisk(uri2);
        Object obj = bTree.get(uri2);
        assertFalse(obj instanceof File);

    }

    @Test
    void setPersistenceManager() {
    }
}