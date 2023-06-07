package edu.yu.cs.com1320.project.stage5.impl;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentImplTest {
    DocumentImpl stringDocument;
    DocumentImpl binaryDataDocument;
    URI sdURI;
    URI bddURI;
    byte[] bite;

    DocumentImplTest() {
    }

    @BeforeEach
    void setUp() throws URISyntaxException {
        this.bite = new byte[5];

        for(int i = 0; i < 5; ++i) {
            this.bite[i] = (byte)i;
        }
        this.sdURI = new URI("BigDog");
        this.bddURI = new URI("BinaryDataDocument");
        this.stringDocument = new DocumentImpl(this.sdURI, "BigDog");
        this.binaryDataDocument = new DocumentImpl(this.bddURI, this.bite);
    }

    @Test
    @DisplayName("The constructor should throw an exception if the String is empty")
    void constructor1() {
        String emptyString = "";
        assertThrows(IllegalArgumentException.class, () -> {
            this.stringDocument = new DocumentImpl(this.sdURI, emptyString);
        });
    }

    @Test
    @DisplayName("The constructor should throw an exception if the String is null")
    void constructor2() {
        String nullString = null;
        assertThrows(IllegalArgumentException.class, () -> {
            this.stringDocument = new DocumentImpl(this.sdURI, nullString);
        });
    }

    @Test
    @DisplayName("The constructor should throw an exception if the byte[] is null")
    void constructor3() {
        byte[] nullByteArray = null;
        assertThrows(IllegalArgumentException.class, () -> {
            this.stringDocument = new DocumentImpl(this.sdURI, nullByteArray);
        });
    }

    @Test
    @DisplayName("The constructor should throw an exception if the byte[] is empty")
    void constructor4() {
        byte[] emptyByteArray = new byte[0];
        assertThrows(IllegalArgumentException.class, () -> {
            this.stringDocument = new DocumentImpl(this.sdURI, emptyByteArray);
        });
    }

    @Test
    @DisplayName("return content of text document")
    void getDocumentTxt1() {
        assertEquals("BigDog", this.stringDocument.getDocumentTxt());
    }

    @Test
    @DisplayName("return content of the byte array")
    void getDocumentBinaryData() {
        byte[] byteArrayValue = this.bite;
        assertEquals( byteArrayValue, binaryDataDocument.getDocumentBinaryData());
    }

    @Test
    @DisplayName("return the URI which uniquely identifies the document (for both text and binary data)")
    void getKey() {
        assertEquals(this.sdURI, this.stringDocument.getKey());
        assertEquals(this.bddURI, this.binaryDataDocument.getKey());
    }

    @Test
    @DisplayName("compare the equality of two objects")
    void testEquals() {
        String a = "b";
        String b = "b";
        assertEquals(a,b);
    }

    @Test
    @DisplayName("test that wordCount() returns 3 after adding the word hi 3 times")
    void wordCount1() {
        String hi = "hi my name is Fat Al, I like to say hi a lot, why do I say hi I don't know, good question";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(3, doc.wordCount("hi"));
    }

    @Test
    @DisplayName("test that wordCount() returns 0 after adding the word hi 3 times")
    void wordCount2() {
        String hi = "hihihi";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(0, doc.wordCount("hi"));
    }

    @Test
    @DisplayName("test that getWords() returns a list of the words in the Document")
    void getWords1() {
        String hi = "hi my name is Fat Al, I like to say hi a lot, why do I say hi I don't know, good question";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(18, doc.getWords().size());
    }

    @Test
    @DisplayName("test that Hi and hi are different words")
    void getWords2() {
        String hi = "hi yall, Hi hi";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(3, doc.getWords().size());
    }

    @Test
    @DisplayName("test that numbers count as words")
    void getWords3() {
        String hi = "12 14";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(2, doc.getWords().size());
    }

    @Test
    @DisplayName("test that numbers count as words")
    void getWords4() {
        String hi = "12     14";
        DocumentImpl doc = new DocumentImpl(this.sdURI, hi);
        assertEquals(2, doc.getWords().size());
    }

}
