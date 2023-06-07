package edu.yu.cs.com1320.project.stage5.impl;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class DocumentPersistenceManagerTest {

    @Test
    void serialize1() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);

        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);


        dpm.serialize(uri2, document2);

    }

    @Test
    void serialize2() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);

        URI uri2 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1);


        dpm.serialize(uri2, document2);

    }

    @Test
    void deserializeTextHttps() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri2 = URI.create("https://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        uri2 = URI.create("https://www.example.com/document1");
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertEquals(document2, dpm.deserialize(uri2));
    }


    @Test
    void deserializeTextHttp() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri2 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        uri2 = URI.create("http://www.example.com/document1");
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertEquals(document2, dpm.deserialize(uri2));
    }

    @Test
    void deserializeTextNoHttp() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri2 = URI.create("www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        uri2 = URI.create("www.example.com/document1");
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertEquals(document2, dpm.deserialize(uri2));
    }

    @Test
    void deserializeBinary() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri2 = URI.create("http://www.example.com/document1");
        String binary = "This is the first example document.";
        byte[] binarydata = binary.getBytes();
        DocumentImpl document2 = new DocumentImpl(uri2, binarydata);
        dpm.serialize(uri2, document2);
        uri2 = URI.create("http://www.example.com/document1");
        binary = "This is the first example document.";
        binarydata = binary.getBytes();
        document2 = new DocumentImpl(uri2, binarydata);
        assertEquals(document2, dpm.deserialize(uri2));
    }

    @Test
    void deserializeBinaryNoHTTP() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        URI uri2 = URI.create("www.example.com/document1");
        String binary = "This is the first example document.";
        byte[] binarydata = binary.getBytes();
        DocumentImpl document2 = new DocumentImpl(uri2, binarydata);
        dpm.serialize(uri2, document2);
        uri2 = URI.create("www.example.com/document1");
        binary = "This is the first example document.";
        binarydata = binary.getBytes();
        document2 = new DocumentImpl(uri2, binarydata);
        assertEquals(document2, dpm.deserialize(uri2));
    }

    @Test
    void deleteHTTP() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        final URI uri2 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        dpm.delete(uri2);
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertThrows(FileNotFoundException.class, () ->  dpm.deserialize(uri2));
    }


    @Test
    void deleteHTTPS() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        final URI uri2 = URI.create("https://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        dpm.delete(uri2);
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertThrows(FileNotFoundException.class, () ->  dpm.deserialize(uri2));
    }

    @Test
    void deleteNoHTTP() throws IOException {
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        final URI uri2 = URI.create("www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text1, null);
        dpm.serialize(uri2, document2);
        dpm.delete(uri2);
        text1 = "This is the first example document.";
        document2 = new DocumentImpl(uri2, text1, null);
        assertThrows(FileNotFoundException.class, () ->  dpm.deserialize(uri2));
    }
}