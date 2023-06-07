package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentStoreImplTest {
    DocumentStoreImpl docStore;
    DocumentImpl doc;
    URI sdURI;
    String stringDocument;
    InputStream input;
    DocumentStore.DocumentFormat format;
    byte[] buf;
    private URI newURI;

    /**
     * @param // input the document being put
     * @param // uri unique identifier for the document
     * @param // format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws // IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */

    @BeforeEach
    void setUp() {
        try {
            this.sdURI = new URI("BigDog");
        }
        catch (Exception e) {
            String beans;
        }
        this.doc = new DocumentImpl(this.sdURI, "BigDog");
        this.docStore = new DocumentStoreImpl();
        String s = "Alan Reid";
        this.buf = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = new ByteArrayInputStream(buf);
        format = DocumentStore.DocumentFormat.TXT;
    }

    @Test
    @DisplayName("if there is no previous doc at the given URI, return 0")
    void put1() throws IOException {
        assertEquals(0, this.docStore.put(input, sdURI, format));
    }
    @Test
    @DisplayName("if there is a previous doc at the given URI return the hashCode of the previous doc")
    void put2() throws IOException {
        this.docStore.put(input, sdURI, format);
        byte[] newByteArray = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = new ByteArrayInputStream(newByteArray);
        assertEquals(this.docStore.get(sdURI).hashCode(), this.docStore.put(input, sdURI, format));
    }
    @Test
    @DisplayName(" If InputStream is null, this is a delete, and thus return the hashCode of the deleted doc")
    void put3() throws IOException {
        this.docStore.put(input, sdURI, format);
        byte[] newByteArray = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = null;
        assertEquals(this.docStore.get(sdURI).hashCode(), this.docStore.put(input, sdURI, format));
    }
    @Test
    @DisplayName(" If InputStream is null, this is a delete, and thus return 0 since there is no doc to delete.")
    void put4() throws IOException,URISyntaxException {
        this.docStore.put(input, sdURI, format);
        byte[] newByteArray = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = null;
        URI uriThatDoesntExist = new URI("Doesn't_Exist");
        assertEquals(0, this.docStore.put(input, uriThatDoesntExist, format));
    }

    @Test
    @DisplayName("IllegalArgumentException if format is null")
    void put5() throws IOException,URISyntaxException {
        this.docStore.put(input, sdURI, format);
        byte[] newByteArray = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = null;
        this.format = null;
        URI uriThatDoesntExist = new URI("Doesn't_Exist");
        assertThrows(IllegalArgumentException.class,() -> this.docStore.put(input, uriThatDoesntExist, format));
    }

    @Test
    @DisplayName("IllegalArgumentException if uri is null")
    void put6() throws IOException,URISyntaxException {
        this.docStore.put(input, sdURI, format);
        byte[] newByteArray = new byte[5];
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)i;
        }
        this.input = null;
        URI uriIsNull = null;
        assertThrows(IllegalArgumentException.class,() -> this.docStore.put(input, uriIsNull, format));
    }

    /**
     * @param "uri" the unique identifier of the document to get
     * @return the given document
     */
    @Test
    @DisplayName("takes the uri the document and returns the given document")
    void get() throws IOException {
        this.docStore.put(input, sdURI, format);
        assertEquals(this.docStore.get(sdURI), this.docStore.get(sdURI));
    }

    /**
     * @param "uri" the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Test
    void delete() throws IOException{
        this.docStore.put(input, sdURI, format);
        assertTrue(this.docStore.delete(sdURI));
    }
    @Test
    @DisplayName("undoes the first item on the stack")
    void undo1() throws IOException {
        this.docStore = new DocumentStoreImpl();
        this.format = DocumentStore.DocumentFormat.TXT;
        try {
            this.newURI = new URI("Hello");
        }
        catch (Exception e) {
            String beans;
        }
        String hello = "Hello";
        byte[] byteArrray = hello.getBytes();
        InputStream helloIS = new ByteArrayInputStream(byteArrray);
        this.docStore.put(helloIS, newURI, format);
        String bye = "Bye";
        byteArrray = bye.getBytes();
        InputStream byeIS = new ByteArrayInputStream(byteArrray);
        Document beforeUndo = this.docStore.get(newURI);
        this.docStore.put(byeIS, newURI, format);
        this.docStore.undo();
        Document afterUndo = this.docStore.get(newURI);
        assertEquals(afterUndo.getDocumentTxt(), beforeUndo.getDocumentTxt());
    }

    @Test
    @DisplayName("undoes the first item on the stack")
    void undo2() throws IOException {
        this.docStore = new DocumentStoreImpl();
        this.format = DocumentStore.DocumentFormat.TXT;
        try {
            this.newURI = new URI("Hello");
        }
        catch (Exception e) {
            String beans;
        }
        String hello = "Hello";
        byte[] byteArrray = hello.getBytes();
        InputStream helloIS = new ByteArrayInputStream(byteArrray);
        this.docStore.put(helloIS, newURI, format);
        String bye = "Bye";
        byteArrray = bye.getBytes();
        InputStream byeIS = new ByteArrayInputStream(byteArrray);
        Document beforeSecondPut = this.docStore.get(newURI);
        this.docStore.put(byeIS, newURI, format);
        Document afterSecondPut = this.docStore.get(newURI);
        this.docStore.undo();
        Document afterUndo = this.docStore.get(newURI);
        assertEquals(afterUndo.getDocumentTxt(), beforeSecondPut.getDocumentTxt());
        assertNotEquals(beforeSecondPut.getDocumentTxt(), afterSecondPut.getDocumentTxt());
    }
    @Test
    @DisplayName("trying to get the binary data of a text doc should return null")
    void testGetTxtDocAsBinary() throws IOException{
        DocumentStore.DocumentFormat f = DocumentStore.DocumentFormat.BINARY;
        this.docStore.put(input, sdURI, f);
        assertNull(this.docStore.get(sdURI).getDocumentTxt());
    }

    @Test
    @DisplayName("putting a txt doc and then getting the doc and its txt should return the correct values")
    void testGetTxtDoc() throws IOException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String txt = "This is the text of doc1, in plain text. No fancy file format - just plain old String";
        byte[] txtToByte = txt.getBytes();
        InputStream txtIS = new ByteArrayInputStream(txtToByte);
        this.docStore.put(txtIS, sdURI, t);
        assertEquals(txt, this.docStore.get(sdURI).getDocumentTxt());
    }
    @Test
    @DisplayName("putting a new version of a binary doc should return the hashCode of the old doc and subsequently getting the binary of the doc should return the new binary")
    void testPutNewVersionOfDocumentBinary() throws IOException{
        DocumentStore.DocumentFormat f = DocumentStore.DocumentFormat.BINARY;
        InputStream byteArrayInputStream = new ByteArrayInputStream(this.buf);
        this.docStore.put(byteArrayInputStream , sdURI, f);
        Document orignalBD = this.docStore.get(sdURI);
        int originalHashcode = orignalBD.hashCode();
        for(int i = 0; i < 5; ++i) {
            this.buf[i] = (byte)2;
        }
        byteArrayInputStream = new ByteArrayInputStream(this.buf);
        assertEquals(originalHashcode, this.docStore.put(byteArrayInputStream , sdURI, f));
        Document newBD = this.docStore.get(sdURI);
        int newHashcode = newBD.hashCode();
        assertNotEquals(originalHashcode, newHashcode);
    }

    @Test
    @DisplayName("putting a new version of a txt doc should return the hashCode of the old doc and subsequently getting the txt of the doc should return the new txt")
    void testPutNewVersionOfDocumentTxt() throws IOException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String txt = "Good Hock";
        byte[] txtToByte = txt.getBytes();
        InputStream txtIS = new ByteArrayInputStream(txtToByte);
        this.docStore.put(txtIS, sdURI, t);
        Document orignalTD = this.docStore.get(sdURI);
        int originalHashcode = orignalTD.hashCode();
        txt = "This is the text of doc1, in plain text. No fancy file format - just plain old String";
        txtToByte = txt.getBytes();
        txtIS = new ByteArrayInputStream(txtToByte);
        assertEquals(originalHashcode,  this.docStore.put(txtIS, sdURI, t));
        Document newTD = this.docStore.get(sdURI);
        assertNotEquals(orignalTD.getDocumentTxt(), newTD.getDocumentTxt());
    }

    @Test
    @DisplayName("calling get on URI from which doc was deleted should returned null (when the deleted doc was the only thing in the table)")
    void testDeleteDoc() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String txt = "Good Hock";
        byte[] txtToByte = txt.getBytes();
        InputStream txtIS = new ByteArrayInputStream(txtToByte);
        this.docStore.put(txtIS,this.sdURI,t);
        this.docStore.delete(this.sdURI);
        assertNull(this.docStore.get(sdURI));
    }

    @Test
    @DisplayName("calling get on URI from which doc was deleted (when the deleted doc was the first in the linked list) should returned null")
    void testDeleteDoc2() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[3]);
        assertNull(this.docStore.get(uris[3]));
    }
    @Test
    @DisplayName("calling get on URI from which doc was deleted (when the deleted doc was in the middle of the linked list) should returned null")
    void testDeleteDoc3() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[8]);
        assertNull(this.docStore.get(uris[8]));
    }

    @Test
    @DisplayName("calling get on URI from which doc was deleted (when the deleted doc was at the end of the linked list) should returned null")
    void testDeleteDoc4() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[14]);
        assertNull(this.docStore.get(uris[14]));
    }

    @Test
    @DisplayName("check that deleting a doc returns the correct value when the deleted doc was the first in the linked list")
    void testDeleteDocReturnValue() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[3]);
        assertFalse(this.docStore.delete(uris[3]));
    }

    @Test
    @DisplayName("check that deleting a doc returns the correct value when the deleted doc was in the middle of the linked list")
    void testDeleteDocReturnValue2() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[8]);
        assertFalse(this.docStore.delete(uris[8]));
    }

    @Test
    @DisplayName("check that deleting a doc returns the correct value when the deleted doc was at the end of the linked list")
    void testDeleteDocReturnValue3() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = {"var1", "var2", "var3", "var4", "var5", "var6", "var7", "var8", "var9", "var10", "var11", "var12", "var13", "var14", "var15"};
        URI[] uris = new URI[15];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[14]);
        assertFalse(this.docStore.delete(uris[14]));
    }

    @Test
    @DisplayName("check that after doubling the array twice no element was lost")
    void testArrayDoubling() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[61];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[61];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        for (int k = 0; k < 61; k++) {
            assertNotNull(this.docStore.get(uris[k]));
        }
    }

    @Test
    @DisplayName("check that after undoing the last command on the stack (a put with a new document) that the document doesn't exist anymore")
    void testUndo1() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.undo();
        assertNull(this.docStore.get(uris[9]));
    }

    @Test
    @DisplayName("check that after undoing the last command the stack (overwriting a document) that the document exists as it previously did")
    void testUndo2() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        String gevaldig = "Gevaldig";
        byte[] gevaldigBytes = gevaldig.getBytes();
        InputStream gevaldigIS = new ByteArrayInputStream(gevaldigBytes);
        this.docStore.put(gevaldigIS, uris[9],t);
        this.docStore.undo();
        assertEquals("var9", this.docStore.get(uris[9]).getDocumentTxt());
    }

    @Test
    @DisplayName("check that after undoing the last command the stack (deleting a document) that the document exists as it previously did")
    void testUndo3() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[9]);
        this.docStore.undo();
        assertEquals("var9", this.docStore.get(uris[9]).getDocumentTxt());
    }


    @Test
    @DisplayName("throw an Illegal State Exception since there's no elements on the stack")
    void testUndo4() throws IOException, URISyntaxException{
        assertThrows(IllegalStateException.class,() -> this.docStore.undo());
    }


    @Test
    @DisplayName("check that after undoing the last command on the given URI (a put with a new document) that the document doesn't exist anymore")
    void testUndoURI1() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.undo(uris[5]);
        assertNull(this.docStore.get(uris[5]));
    }

    @Test
    @DisplayName("check that after undoing the last command on the given URI (overwriting a document) that the document exists as it previously did")
    void testUndoURI2() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        String gevaldig = "Gevaldig";
        byte[] gevaldigBytes = gevaldig.getBytes();
        InputStream gevaldigIS = new ByteArrayInputStream(gevaldigBytes);
        this.docStore.put(gevaldigIS, uris[7],t);
        this.docStore.undo(uris[7]);
        assertEquals("var7", this.docStore.get(uris[7]).getDocumentTxt());
    }


    @Test
    @DisplayName("check that after undoing the last command on the given URI (deleting a document) that the document exists as it previously did")
    void testUndoURI3() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.delete(uris[2]);
        this.docStore.undo(uris[2]);
        assertEquals("var2", this.docStore.get(uris[2]).getDocumentTxt());
    }

    @Test
    @DisplayName("check that after undoing every put they're all null")
    void testUndoURI4() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[60];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[60];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        for (int k = 0; k < variables.length; k++) {
            String a = "var" + k;
            assertEquals(a, this.docStore.get(uris[k]).getDocumentTxt());
        }
    }

    @Test
    @DisplayName("undo by uri should throw an exception when there's nothing to undo")
    void undoByURIWhenEmptyShouldThrow() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat t = DocumentStore.DocumentFormat.TXT;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],t);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.undo(uris[0]);
        assertThrows(IllegalStateException.class,() -> this.docStore.undo(uris[0]));
    }




    @Test
    @DisplayName("check that after undoing the last command on the given URI (a put with a new document) that the document doesn't exist anymore")
    void testUndoURI6() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat b = DocumentStore.DocumentFormat.BINARY;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],b);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.undo(uris[5]);
        assertNull(this.docStore.get(uris[5]));
    }


    @Test
    @DisplayName("throw an Illegal State Exception since there's no element with that URI")
    void testUndoURI7() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat b = DocumentStore.DocumentFormat.BINARY;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],b);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        this.docStore.undo(uris[0]);
        assertThrows(IllegalStateException.class,() -> this.docStore.undo(uris[0]));
    }

    @Test
    @DisplayName("check that after undoing the last command on the given URI (overwriting a document) that the document exists as it previously did")
    void testUndoURI8() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat b = DocumentStore.DocumentFormat.BINARY;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],b);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        String gevaldig = "Gevaldig";
        byte[] gevaldigBytes = gevaldig.getBytes();
        InputStream gevaldigIS = new ByteArrayInputStream(gevaldigBytes);
        this.docStore.put(gevaldigIS, uris[7],b);
        String hello = "Hello";
        byte[] helloBytes = hello.getBytes();
        InputStream helloIS = new ByteArrayInputStream(helloBytes);
        this.docStore.put(helloIS, uris[7],b);
        this.docStore.undo(uris[7]);
        assertEquals(gevaldigBytes.length, this.docStore.get(uris[7]).getDocumentBinaryData().length);
    }


    @Test
    @DisplayName("check that after undoing the last command on the given URI (deleting a document) that the document exists as it previously did")
    void testUndoURI9() throws IOException, URISyntaxException{
        DocumentStore.DocumentFormat b = DocumentStore.DocumentFormat.BINARY;
        String[] variables = new String[10];
        for (int j = 0; j < variables.length; j++) {
            variables[j] = "var" + j;
        }
        URI[] uris = new URI[10];
        for (int i = 0; i < variables.length; i++) {
            byte[] txtToByte = variables[i].getBytes();
            InputStream txtIS = new ByteArrayInputStream(txtToByte);
            try {
                uris[i] = new URI(variables[i]);
                this.docStore.put(txtIS, uris[i],b);
            } catch (URISyntaxException e) {
                String h;
            }
        }
        String gevaldig = "Gevaldig";
        byte[] gevaldigBytes = gevaldig.getBytes();
        InputStream gevaldigIS = new ByteArrayInputStream(gevaldigBytes);
        this.docStore.put(gevaldigIS, uris[7],b);
        String hello = "Hello";
        byte[] helloBytes = hello.getBytes();
        InputStream helloIS = new ByteArrayInputStream(helloBytes);
        this.docStore.put(helloIS, uris[7],b);
        this.docStore.delete(uris[7]);
        this.docStore.undo(uris[7]);
        assertEquals(helloBytes.length, this.docStore.get(uris[7]).getDocumentBinaryData().length);
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param //keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Test
    void search() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document2);
        list.add(document1);
        assertEquals(list, ds.search("the"));
    }

    @Test
    void searchByPrefix1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document.";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        list.add(document3);
        list.add(document2);
        list.add(document1);
        assertEquals(1, ds.searchByPrefix("Bl").size());
    }

    @Test
    void searchByPrefix2() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "This is the the the third example document.";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        list.add(document4);
        list.add(document1);
        assertEquals(list, ds.searchByPrefix("Bl"));
    }

    @Test
    void searchByPrefix3() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black Bl";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        list.add(document4);
        list.add(document3);
        list.add(document1);
        assertEquals(list, ds.searchByPrefix("Bl"));
    }


    @Test
    void deleteAll() {
    }

    @Test
    void deleteAllWithPrefix1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black Bl";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri1);
        hashSet.add(uri3);
        hashSet.add(uri4);
        assertEquals(hashSet, ds.deleteAllWithPrefix("Bl"));
        assertEquals(list, ds.searchByPrefix("Bl"));
    }

    @Test
    void deleteAllWithPrefixUndo1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri1);
        assertEquals(hashSet, ds.deleteAllWithPrefix("Bl"));
        assertEquals(list, ds.searchByPrefix("Bl"));
        ds.undo(uri1);
        list.add(document1);
        assertEquals(list, ds.searchByPrefix("Bl"));
    }
    @Test
    void deleteAllWithPrefixUndo2() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black Bl";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri1);
        hashSet.add(uri3);
        hashSet.add(uri4);
        assertEquals(hashSet, ds.deleteAllWithPrefix("Bl"));
        assertEquals(list, ds.searchByPrefix("Bl"));
        ds.undo(uri1);
        list.add(document1);
        assertEquals(list, ds.searchByPrefix("Bl"));
    }


    @Test
    void deleteAll1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri3);
        hashSet.add(uri4);
        assertEquals(hashSet, ds.deleteAll("Black"));
    }


    @Test
    void deleteAll2() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri1);
        hashSet.add(uri4);
        assertEquals(hashSet, ds.deleteAll("Bl"));
    }

    @Test
    @DisplayName("Test that's that after undoing a delete all the commands are re-added")
    void deleteAllUndo1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri4);
        hashSet.add(uri1);
        assertEquals(hashSet, ds.deleteAll("Bl"));
        ds.undo();
        list.add(document4);
        list.add(document1);
        assertEquals(list, ds.search("Bl"));
    }

    @Test
    void deleteAllUndo2() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri1);
        hashSet.add(uri4);
        assertEquals(hashSet, ds.deleteAll("Bl"));
        ds.undo();
        ds.undo();
        list.add(document4);
        assertEquals(list, ds.search("Bl"));
    }

    @Test
    void deleteAllUndo3() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        HashSet<URI> hashSet = new HashSet<>();
        hashSet.add(uri4);
        ds.delete(uri1);
        ds.undo();
        ds.undo();
        list.add(document4);
        assertEquals(list, ds.search("Bl"));
    }


    @Test
    void putUndo1() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        ds.undo(uri1);
        assertNull(this.docStore.get(uri1));
    }

    @Test
    void putUndo2() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        ds.undo();
        assertNull(this.docStore.get(uri1));
        ds.undo();
        assertNull(this.docStore.get(uri4));
    }

    @Test
    @DisplayName("test undo by URI which is EARLIER than most recent")
    void stage3UndoByURIThatImpactsEarlierThanLast() throws IOException {
        DocumentStoreImpl ds = new DocumentStoreImpl();
        URI uri1 = URI.create("http://www.example.com/document1");
        String text1 = "This is the first example document. Bl";
        DocumentImpl document1 = new DocumentImpl(uri1, text1);
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        URI uri2 = URI.create("http://www.example.com/document2");
        String text2 = "This is the the second example document.";
        DocumentImpl document2 = new DocumentImpl(uri2, text2);
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        URI uri3 = URI.create("http://www.example.com/document3");
        String text3 = "Black";
        DocumentImpl document3 = new DocumentImpl(uri3, text3);
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        URI uri4 = URI.create("http://www.example.com/document4");
        String text4 = "Black Bl";
        DocumentImpl document4 = new DocumentImpl(uri4, text4);
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        ds.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        ds.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        ds.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        ds.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        List<Document> list = new ArrayList<>();
        ds.delete(uri3);
        ds.delete(uri2);
        ds.undo(uri3);
        list.add(document3);
        assertEquals(document3, ds.get(uri3));
    }

    @Test
    @DisplayName("Check that after changing the limit to need to delete one Document " +
            "that the smallest Document no longer exists")
    void setMaxDocumentCount1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        doc4.setLastUseTime(System.nanoTime());
        doc3.setLastUseTime(System.nanoTime());
        doc2.setLastUseTime(System.nanoTime());
        doc1.setLastUseTime(System.nanoTime());
        this.docStore.setMaxDocumentCount(3);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
    }


    @Test
    @DisplayName("Check that after changing the limit to need to delete one Document " +
            "that the smallest Document no longer exists (w/Gets)")
    void setMaxDocumentCount2() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        this.docStore.setMaxDocumentCount(3);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri4.getScheme() != null)  {
            String host = uri4.getHost();
            String path = uri4.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri4.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("Check that after changing the limit to 0 all the documents are deleted")
    void setMaxDocumentCount3() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        this.docStore.setMaxDocumentCount(0);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri4.getHost();
            String path = uri4.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        newUri = "";
        if (uri3.getScheme() != null)  {
            String host3 = uri3.getHost();
            String path3 = uri3.getPath();
            newUri = host3 + path3 + ".json";
        }
        else {
            newUri = uri3.toString();
        }
        File file3 = new File(dir, newUri);
        String fileName3 = file3.toString();
        Path path3 = Paths.get(fileName3);
        assertTrue(Files.exists(path3));
        newUri = "";
        if (uri2.getScheme() != null)  {
            String host2 = uri2.getHost();
            String path2 = uri2.getPath();
            newUri = host2 + path2 + ".json";
        }
        else {
            newUri = uri2.toString();
        }
        File file2 = new File(dir, newUri);
        String fileName2 = file2.toString();
        Path path2 = Paths.get(fileName2);
        assertTrue(Files.exists(path2));
        newUri = "";
        if (uri1.getScheme() != null)  {
            String host1 = uri1.getHost();
            String path1 = uri1.getPath();
            newUri = host1 + path1 + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file1 = new File(dir, newUri);
        String fileName1 = file1.toString();
        Path path1 = Paths.get(fileName1);
        assertTrue(Files.exists(path1));
    }

    @Test
    @DisplayName("Check that setting the limit to 0 throws an Illegal Argument Exception")
    void setMaxDocumentCount4() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        assertThrows(IllegalArgumentException.class,() -> this.docStore.setMaxDocumentCount(-1));
    }


    @Test
    @DisplayName("Check that after changing the limit to need to delete one Document (in terms of bytes)" +
            "that the smallest Document no longer exists")
    void setMaxDocumentBytes1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/DOCFOUR");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        this.docStore.setMaxDocumentBytes(143);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri4.getScheme() != null)  {
            String host = uri4.getHost();
            String path = uri4.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri4.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        //asserts that document four doesn't exist in the trie
        assertNotNull(this.docStore.search("fourth"));
        assertNotNull(this.docStore.get(uri3));
        assertNotNull(this.docStore.get(uri2));
        assertNotNull(this.docStore.get(uri1));
    }

    @Test
    @DisplayName("Check that after changing the limit to need to delete two Documents (in terms of bytes)" +
            "that the smallest Documents no longer exists")
    void setMaxDocumentBytes2() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        this.docStore.setMaxDocumentBytes(141);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri4.getScheme() != null)  {
            String host = uri4.getHost();
            String path = uri4.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri4.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        newUri = "";
        if (uri3.getScheme() != null)  {
            String host3 = uri3.getHost();
            String path3 = uri3.getPath();
            newUri = host3 + path3 + ".json";
        }
        else {
            newUri = uri3.toString();
        }
        File file3 = new File(dir, newUri);
        String fileName3 = file3.toString();
        Path path3 = Paths.get(fileName3);
        assertTrue(Files.exists(path3));
        List<Document> emptyList = new ArrayList<>();
        //asserts that document four doesn't exist in the trie
        assertNotNull(this.docStore.search("fourth"));
        //asserts that document three doesn't exist in the trie
        assertNotNull(this.docStore.search("third"));
        assertNotNull(this.docStore.get(uri2));
        assertNotNull(this.docStore.get(uri1));
    }


    @Test
    @DisplayName("Check that after changing the limit to 0 all the documents are deleted")
    void setMaxDocumentBytes3() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        this.docStore.setMaxDocumentBytes(0);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri4.getHost();
            String path = uri4.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        newUri = "";
        if (uri3.getScheme() != null)  {
            String host3 = uri3.getHost();
            String path3 = uri3.getPath();
            newUri = host3 + path3 + ".json";
        }
        else {
            newUri = uri3.toString();
        }
        File file3 = new File(dir, newUri);
        String fileName3 = file3.toString();
        Path path3 = Paths.get(fileName3);
        assertTrue(Files.exists(path3));
        newUri = "";
        if (uri2.getScheme() != null)  {
            String host2 = uri2.getHost();
            String path2 = uri2.getPath();
            newUri = host2 + path2 + ".json";
        }
        else {
            newUri = uri2.toString();
        }
        File file2 = new File(dir, newUri);
        String fileName2 = file2.toString();
        Path path2 = Paths.get(fileName2);
        assertTrue(Files.exists(path2));
        newUri = "";
        if (uri1.getScheme() != null)  {
            String host1 = uri1.getHost();
            String path1 = uri1.getPath();
            newUri = host1 + path1 + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file1 = new File(dir, newUri);
        String fileName1 = file1.toString();
        Path path1 = Paths.get(fileName1);
        assertTrue(Files.exists(path1));
    }



    @Test
    @DisplayName("Check that setting the limit to 0 throws an Illegal Argument Exception")
    void setMaxDocumentBytes4() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.get(uri4);
        this.docStore.get(uri3);
        this.docStore.get(uri2);
        this.docStore.get(uri1);
        assertThrows(IllegalArgumentException.class,() -> this.docStore.setMaxDocumentBytes(-1));
    }


    @Test
    @DisplayName("Check that adding 1 Document over the count" +
            " limit deletes the previous smallest and adds the new one")
    void putLimit1() throws IOException {
        URI uri1;
        DocumentImpl docOne;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/docOne");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        docOne = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.setMaxDocumentCount(3);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("Check that adding 1 Document over the byte" +
            " limit deletes the previous smallest and adds the new one")
    void putLimit2() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1ONE");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.setMaxDocumentBytes(142);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("Check that adding 1 Document over the count" +
            " limit through undo(uri) deletes the previous smallest and adds the new one")
    void undoLimit1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.setMaxDocumentCount(3);
        this.docStore.undo(uri4);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNotNull(uri2);
        assertNotNull(uri3);
        assertNotNull(uri4);
    }


    @Test
    @DisplayName("Check that adding 1 Document over the count" +
            " limit through undo() deletes the previous smallest and adds the new one")
    void undoLimit2() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.delete(uri1);
        this.docStore.setMaxDocumentCount(3);
        this.docStore.undo();
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri2.getScheme() != null)  {
            String host = uri2.getHost();
            String path = uri2.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri2.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNotNull(uri1);
        assertNotNull(uri3);
        assertNotNull(uri4);
    }


    @Test
    @DisplayName("Check that searching for documents update's their last use time")
    void searchUpdate1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.search("third");
        this.docStore.search("first");
        this.docStore.search("fourth");
        this.docStore.search("second");
        this.docStore.setMaxDocumentCount(3);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri3.getScheme() != null)  {
            String host = uri3.getHost();
            String path = uri3.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri3.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNotNull(uri1);
        assertNotNull(uri2);
        assertNotNull(uri4);
    }

    @Test
    @DisplayName("Check that searching for prefix update's their last use time")
    void searchPrefixUpdate1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.searchByPrefix("third");
        this.docStore.searchByPrefix("first");
        this.docStore.searchByPrefix("fourth");
        this.docStore.searchByPrefix("second");
        this.docStore.setMaxDocumentCount(3);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri3.getScheme() != null)  {
            String host = uri3.getHost();
            String path = uri3.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri3.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNotNull(uri1);
        assertNotNull(uri2);
        assertNotNull(uri4);
    }


    @Test
    @DisplayName("Check that deleting document's update's their last use time")
    void deleteAllUndoUpdate1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.deleteAll("third");
        this.docStore.deleteAll("first");
        this.docStore.deleteAll("fourth");
        this.docStore.deleteAll("second");
        this.docStore.undo(uri3);
        this.docStore.undo(uri1);
        this.docStore.undo(uri4);
        this.docStore.undo(uri2);
        this.docStore.setMaxDocumentCount(3);
        assertNull(this.docStore.get(uri3));
        assertNotNull(uri1);
        assertNotNull(uri2);
        assertNotNull(uri4);
    }

    @Test
    @DisplayName("Check that deleting document's update's their last use time")
    void deleteAllWithPrefixUndoUpdate1() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third document.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth document.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.deleteAllWithPrefix("third");
        this.docStore.deleteAllWithPrefix("first");
        this.docStore.deleteAllWithPrefix("fourth");
        this.docStore.deleteAllWithPrefix("second");
        this.docStore.undo(uri1);
        this.docStore.undo(uri2);
        this.docStore.undo(uri4);
        this.docStore.undo(uri3);
        this.docStore.setMaxDocumentCount(3);
        assertNull(this.docStore.get(uri1));
        assertNotNull(uri3);
        assertNotNull(uri2);
        assertNotNull(uri4);
    }

    @Test
    @DisplayName("Check that deleting document's update's their last use time")
    void deleteAllWithPrefixUndoUpdate2() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third page.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth page.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.deleteAllWithPrefix("page");
        this.docStore.undo(uri3);
        this.docStore.setMaxDocumentCount(2);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNull(this.docStore.get(uri4));
        assertNotNull(this.docStore.get(uri1));
        assertNotNull(this.docStore.get(uri2));
        assertNotNull(this.docStore.get(uri3));
    }

    @Test
    @DisplayName("test that documents move to and from disk and memory as expected when a doc is deleted then " +
            "another is added to memory then the delete is undone causing another doc to be pushed out to disk")
    void stage5PushToDiskViaMaxDocCountViaUndoDelete() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third page.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth page.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.deleteAllWithPrefix("page");
        this.docStore.undo(uri3);
        this.docStore.setMaxDocumentCount(2);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(Files.exists(path));
        assertNull(this.docStore.get(uri4));
        assertNotNull(this.docStore.get(uri1));
        assertNotNull(this.docStore.get(uri2));
        assertNotNull(this.docStore.get(uri3));
    }

    @Test
    @DisplayName("test that documents move to and from disk and memory as expected when the maxdoc count is 2")
    void stage5PushToDiskViaMaxDocCount() {

    }

    @Test
    @DisplayName("test that documents move to and from disk and memory as expected when the maxdoc count is reached" +
            " and a doc is pushed out to disk and then a doc is deleted and then the doc on " +
            "disk has to be brought back in because of a search")
    void stage5PushToDiskViaMaxDocCountBringBackInViaDeleteAndSearch() throws IOException {
        URI uri1;
        DocumentImpl doc1;
        URI uri2;
        DocumentImpl doc2;
        URI uri3;
        DocumentImpl doc3;
        URI uri4;
        DocumentImpl doc4;
        uri1 = URI.create("http://www.yu.edu/documents/doc1");
        String text1 = "This is the text content of the first document.";
        byte[] byteArrray1 = text1.getBytes();
        InputStream text1IS = new ByteArrayInputStream(byteArrray1);
        doc1 = new DocumentImpl(uri1, text1);
        uri2 = URI.create("http://www.yu.edu/documents/doc2");
        String text2 = "This is the text content of the second document.";
        byte[] byteArrray2 = text2.getBytes();
        InputStream text2IS = new ByteArrayInputStream(byteArrray2);
        doc2 = new DocumentImpl(uri2, text2);
        uri3 = URI.create("http://www.yu.edu/documents/doc3");
        String text3 = "This is the text content of the third page.";
        byte[] byteArrray3 = text3.getBytes();
        InputStream text3IS = new ByteArrayInputStream(byteArrray3);
        doc3 = new DocumentImpl(uri3, text3);
        uri4 = URI.create("http://www.yu.edu/documents/doc4");
        String text4 = "This is the text content of the fourth page.";
        byte[] byteArrray4 = text4.getBytes();
        InputStream text4IS = new ByteArrayInputStream(byteArrray4);
        doc4 = new DocumentImpl(uri4, text4);
        this.docStore.put(text1IS, uri1, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text2IS, uri2, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text3IS, uri3, DocumentStore.DocumentFormat.TXT);
        this.docStore.put(text4IS, uri4, DocumentStore.DocumentFormat.TXT);
        this.docStore.setMaxDocumentCount(3);
        String userDir = System.getProperty(("user.dir"));
        File dir = new File(userDir);
        String newUri = "";
        if (uri1.getScheme() != null)  {
            String host = uri1.getHost();
            String path = uri1.getPath();
            newUri = host + path + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        File file = new File(dir, newUri);
        String fileName = file.toString();
        Path path = Paths.get(fileName);
        assertTrue(file.exists());
        this.docStore.delete(uri2);
        this.docStore.search("first");
        userDir = System.getProperty(("user.dir"));
        dir = new File(userDir);
        newUri = "";
        if (uri1.getScheme() != null)  {
            String host2 = uri1.getHost();
            String path2 = uri1.getPath();
            newUri = host2 + path2 + ".json";
        }
        else {
            newUri = uri1.toString();
        }
        file = new File(dir, newUri);
        fileName = file.toString();
        path = Paths.get(fileName);
        assertFalse(file.exists());
    }



}
