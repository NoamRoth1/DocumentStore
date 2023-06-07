package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl<URI, Document> bTree;
    private StackImpl<Undoable> stack;
    private TrieImpl<URI> trie;
    private MinHeapImpl<MinHeapNode> minHeap;
    private ArrayList<URI> serializedURIs;

    private int maxDocumentCount;
    private int maxDocumentBytes;

    private class MinHeapNode implements Comparable<MinHeapNode> {
        private URI uri;

        private BTreeImpl<URI, Document> bTree;

        private MinHeapNode(URI uri, BTreeImpl<URI, Document> bTree) {
            this.uri = uri;
            this.bTree = bTree;
        }




        @Override
        public int compareTo(MinHeapNode o) {
            Document doc = this.bTree.get(this.uri);
            //@throws NullPointerException if the specified object is null
            if (o == null) {
                throw new NullPointerException("the specified object is null");
            }
            //@throws ClassCastException if the specified object's type prevents it from being compared to this object.
            if (!(o instanceof MinHeapNode)) {
                throw new ClassCastException("the specified object's type prevents it from being compared to this object.");
            }
            DocumentImpl otherDoc = (DocumentImpl) this.bTree.get(o.uri);
            if (otherDoc.getLastUseTime() > doc.getLastUseTime()) {
                return -1;
            }
            if (otherDoc.getLastUseTime() == doc.getLastUseTime()) {
                return 0;
            }
            if (doc.getLastUseTime() > otherDoc.getLastUseTime()) {
                return 1;
            }
            return 1;
        }
        private URI getUri() {
            return this.uri;
        }



        @Override
        public boolean equals(Object o) {
            //@throws NullPointerException if the specified object is null
            if (o == null) {
                throw new NullPointerException("the specified object is null");
            }
            //@throws ClassCastException if the specified object's type prevents it from being compared to this object.
            if (!(o instanceof MinHeapNode)) {
                throw new ClassCastException("the specified object's type prevents it from being compared to this object.");
            }
            MinHeapNode otherMinHeapNode = (MinHeapNode) o;
            if (this.uri.equals(otherMinHeapNode.uri)) {
                return true;
            }
            else return false;
        }
    }


    public DocumentStoreImpl() {
        this.bTree = new BTreeImpl<>();
        this.bTree.setPersistenceManager(new DocumentPersistenceManager(null));
        this.stack = new StackImpl();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.maxDocumentCount = Integer.MAX_VALUE;
        this.maxDocumentBytes = Integer.MAX_VALUE;
        this.serializedURIs = new ArrayList<>();
    }

    public DocumentStoreImpl(File baseDir) {
        this.bTree = new BTreeImpl<>();
        this.bTree.setPersistenceManager(new DocumentPersistenceManager(baseDir));
        this.stack = new StackImpl();
        this.trie = new TrieImpl<>();
        this.minHeap = new MinHeapImpl<>();
        this.maxDocumentCount = Integer.MAX_VALUE;
        this.maxDocumentBytes = Integer.MAX_VALUE;
        this.serializedURIs = new ArrayList<>();
    }

    /**
     * @param input  the document being put
     * @param uri    unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException              if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    @Override
    public int put(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (uri == null || format == null) {
            throw new IllegalArgumentException();
        }
        if (input == null) {
            Document objectToDelete = this.get(uri);
            if (objectToDelete == null) {
                return 0;
            }
            DocumentImpl documentToDelete = (DocumentImpl) objectToDelete;
            int hashcode = documentToDelete.hashCode();
            if (this.delete(uri)) {
                return hashcode;
            }
        }
        return this.getObject(input, uri, format);
    }

    //method to check if there's enough bytes in memory for the put and to remove the smallest document if not
    private void checkByteSpace(DocumentImpl docToAdd)  {
        int bytesToAdd = 0;
        //for string documents
        if (docToAdd.getDocumentTxt() != null) {
            bytesToAdd = docToAdd.getDocumentTxt().getBytes().length;
        }
        //for binary documents
        if (docToAdd.getDocumentBinaryData() != null) {
            bytesToAdd = docToAdd.getDocumentBinaryData().length;
        }
        if (this.maxDocumentBytes == 0) {
            return;
        }
        while ((bytesToAdd + this.numberOfBytes()) > this.maxDocumentBytes) {
            //get the smallest element
            MinHeapNode minHeapNode  = this.minHeap.remove();
            //get the Document
            DocumentImpl docToMoveToDisk = (DocumentImpl) this.bTree.get(minHeapNode.uri);
            //move the URI to disk
            try {
                this.bTree.moveToDisk(docToMoveToDisk.getKey());
                //add the uri to the list of serialized URIs
                this.serializedURIs.add(docToAdd.getKey());
            } catch (Exception e) {
                return;
            }
        }
    }

    //method to check if the maxDocumentCount is full and therefore can't add the new document
    private void checkCount(DocumentImpl docToAdd) {
        int bytesToAdd = 0;
        //for string documents
        if (docToAdd.getDocumentTxt() != null) {
            bytesToAdd = docToAdd.getDocumentTxt().getBytes().length;
        }
        //for binary documents
        if (docToAdd.getDocumentBinaryData() != null) {
            bytesToAdd = docToAdd.getDocumentBinaryData().length;
        }
        if (this.maxDocumentCount == 0) {
            return;
        }
        if (this.maxDocumentCount == this.numberOfDocuments()) {
            //get the smallest element
            MinHeapNode minHeapNode  = this.minHeap.remove();
            //get the Document
            DocumentImpl docToMoveToDisk = (DocumentImpl) this.bTree.get(minHeapNode.uri);
            //move the URI to disk
            try {
                this.bTree.moveToDisk(docToMoveToDisk.getKey());
                //add the uri to the list of serialized URIs
                this.serializedURIs.add(docToAdd.getKey());
            } catch (Exception e) {
                return;
            }
        }
    }

    private int getObject(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if (format.toString().equals("TXT")) {
            byte[] text = input.readAllBytes();
            String str =new String(text);
            DocumentImpl document = new DocumentImpl(uri,str, null);
            //add method here to check if there isn't enough bytes in memory to add the new document
            this.checkByteSpace(document);
            //add method here to check if the maxDocumentCount is full and therefore can't add the new document
            this.checkCount(document);
            Object obj = this.bTree.put(uri, document);
            //update the document's last use time
            document.setLastUseTime(System.nanoTime());
            //if there was no previous document return 0
            if (obj == null) {
                //add the new document to the minHeap by creating a MinHeapNode() and adding it
                //to the minHeap
                this.minHeap.insert(new MinHeapNode(document.getKey(), this.bTree));
                //For each word that appears in the Document,
                //add the Document to the Value collection at the appropriate Node in the Trie
                for (String s: document.getWords()) {
                    this.trie.put(s, document.getKey());
                }
                //undo the command by deleting the new document
                Function<URI,Boolean> undo = (x) -> {
                    try {
                        this.put(null, uri, format);
                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                };
                //add a command to the command stack
                this.addGCtoStack(uri, undo);
                //check if this doc was previously serialized
                this.checkSerializedURIs(uri);
                return 0;
            }
            //if they're from the same class return the previous document's hashcode
            if (obj instanceof DocumentImpl) {
                DocumentImpl previousDocument = (DocumentImpl) obj;
                //add the new document to the minHeap by creating a MinHeapNode() and adding it
                //to the minHeap
                this.minHeap.insert(new MinHeapNode(document.getKey(), this.bTree));
                //Create HashMap for Document
                //for every word in the Document, add how often it appears
                for (String s: document.getWords()) {
                    document.wordCountMap.put(s, document.wordCount(s));
                }
                //undo the command by re-putting the overwritten document
                Function<URI,Boolean> undo = (x) -> {
                    try {
                        byte[] byteArrray = previousDocument.getDocumentTxt().getBytes();
                        final InputStream docInput = new ByteArrayInputStream(byteArrray);
                        DocumentFormat df = DocumentFormat.TXT;
                        this.put(docInput, previousDocument.getKey(), df);
                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                };
                //add a command to the command stack
                this.addGCtoStack(uri, undo);
                //check if this doc was previously serialized
                this.checkSerializedURIs(uri);
                return previousDocument.hashCode();
            }
        }
        if (format.toString().equals("BINARY")) {
            byte[] binary = input.readAllBytes();
            DocumentImpl document = new DocumentImpl(uri, binary);
            Object obj = this.bTree.put(uri, document);
            //update the document's last use time
            document.setLastUseTime(System.nanoTime());
            //if there was no previous document return 0
            if (obj == null) {
                //add the new document to the minHeap by creating a MinHeapNode() and adding it
                //to the minHeap
                this.minHeap.insert(new MinHeapNode(document.getKey(), this.bTree));
                //undo the command by deleting the new document
                Function<URI,Boolean> undo = (x) -> {
                    try {
                        this.put(null, uri, format);
                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                };
                //add a command to the command stack
                this.addGCtoStack(uri, undo);
                //check if this doc was previously serialized
                this.checkSerializedURIs(uri);
                return 0;
            }
            //if they're from the same class return the previous document's hashcode
            if (obj instanceof DocumentImpl) {
                DocumentImpl previousDocument = (DocumentImpl) obj;
                //add the new document to the minHeap by creating a MinHeapNode() and adding it
                //to the minHeap
                this.minHeap.insert(new MinHeapNode(document.getKey(), this.bTree));
                //undo the command by re-putting the overwritten document
                Function<URI,Boolean> undo = (x) -> {
                    try {
                        final InputStream docInput = new ByteArrayInputStream(previousDocument.getDocumentBinaryData());
                        DocumentFormat df = DocumentFormat.BINARY;
                        this.put(docInput, previousDocument.getKey(), df);
                    } catch (IOException e) {
                        return false;
                    }
                    return true;
                };
                //add a command to the command stack
                this.addGCtoStack(uri, undo);
                //check if this doc was previously serialized
                this.checkSerializedURIs(uri);
                return previousDocument.hashCode();
            }
        }
        return 0;
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document get(URI uri) {
        Object obj = this.bTree.get(uri);
        if (obj == null) {
            return null;
        }
        else {
                //if the object is a DocumentImpl return the DocumentImpl
                if (obj instanceof DocumentImpl) {
                    DocumentImpl doc = (DocumentImpl) obj;
                    //update the document's last use time
                    doc.setLastUseTime(System.nanoTime());
                    if (!(this.serializedURIs == null)) {
                    if (this.serializedURIs.contains(doc.getKey())) {
                        try {
                            if (doc.getDocumentTxt() != null) {
                                byte[] bytes = doc.getDocumentTxt().getBytes();
                                final InputStream docInput = new ByteArrayInputStream(bytes);
                                DocumentFormat text = DocumentFormat.TXT;
                                this.put(docInput, doc.getKey(), text);
                                this.serializedURIs.remove(doc.getKey());
                                try {
                                    this.minHeap.reHeapify(new MinHeapNode(doc.getKey(), this.bTree));
                                }
                                catch (Exception e) {

                                }
                            }
                            if (doc.getDocumentBinaryData() != null) {
                                final InputStream docInput = new ByteArrayInputStream(doc.getDocumentBinaryData());
                                DocumentFormat binary = DocumentFormat.BINARY;
                                this.put(docInput, doc.getKey(),binary);
                                this.serializedURIs.remove(doc.getKey());
                                try {
                                    this.minHeap.reHeapify(new MinHeapNode(doc.getKey(), this.bTree));
                                }
                                catch (Exception e) {

                                }
                            }

                        } catch (IOException e) {
                            return null;
                        }
                    }
                    }
                    return doc;

            }
        }
        return null;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean delete(URI uri) {
        try {
            Object objectToDelete = this.bTree.put(uri,null);
            if (objectToDelete == null) {
                return false;
            }
            DocumentImpl documentToDelete = (DocumentImpl) objectToDelete;
            this.bTree.put(uri,documentToDelete);
            //delete the document from the trie
            if (documentToDelete.getDocumentTxt() != null) {
                for (String s: documentToDelete.getWords()) {
                    this.trie.delete(s, documentToDelete.getKey());
                }
            }
            //delete the document from the minHeap
            //set the lastUseTime to 0
            documentToDelete.setLastUseTime(0);
            //call reHeapify (new MinHeapNode(documentToDelete.getKey(), this.bTree))
            //to bring the document to the top of minHeap
            MinHeapNode m = new MinHeapNode(uri, this.bTree);
            try {
                this.minHeap.reHeapify(m);
            }
            catch (Exception e) {

            }
            //remove
            this.minHeap.remove();
            objectToDelete = this.bTree.put(uri,null);
            //undo the command by re-putting the deleted document
            Function<URI,Boolean> undo = (x) -> {
                try {
                    if (documentToDelete.getDocumentTxt() != null) {
                        byte[] bytes = documentToDelete.getDocumentTxt().getBytes();
                        final InputStream docInput = new ByteArrayInputStream(bytes);
                        DocumentFormat text = DocumentFormat.TXT;
                        this.put(docInput, documentToDelete.getKey(), text);
                    }
                    if (documentToDelete.getDocumentBinaryData() != null) {
                        final InputStream docInput = new ByteArrayInputStream(documentToDelete.getDocumentBinaryData());
                        DocumentFormat binary = DocumentFormat.BINARY;
                        this.put(docInput, documentToDelete.getKey(),binary);
                    }

                } catch (IOException e) {
                    return false;
                }
                return true;
            };
            //add the command to the stack
            this.addGCtoStack(uri, undo);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }


    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if(this.stack.size() == 0) {
            throw new IllegalStateException("the command stack is empty");
        }
        Undoable undoable = this.stack.pop();
        try {
            GenericCommand<URI> gc = (GenericCommand<URI>) undoable;
            undoable.undo();
            undoable = this.stack.pop();
        }
        catch (ClassCastException e) {
            CommandSet<URI> cs = (CommandSet<URI>) undoable;
            int size = cs.size();
            undoable.undo();
            for (int i = 0; i < size; i++) {
                undoable = this.stack.pop();
            }
        }
    }


    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        if (this.stack.size() == 0) {
            throw new IllegalStateException("there are no actions on the command stack for the given URI");
        }
        StackImpl<Undoable> tempStack = new StackImpl<>();
        //if the URI on top is not equal to this place it onto the tempStack
        Undoable commandObject = this.stack.peek();
        GenericCommand<URI> cmnd = new GenericCommand<>(null,null);
        CommandSet<URI> cs = new CommandSet<>();
        //try to cast to Generic Command if Exception occurs it must be a CommandSet
        try {
            cmnd = (GenericCommand<URI>) commandObject;
        }
        catch (ClassCastException e) {
            cs = (CommandSet<URI>) commandObject;
        }
        while(!uri.equals(cmnd.getTarget()) && !cs.containsTarget(uri)) {
            Undoable undoable = this.stack.pop();
            tempStack.push(undoable);
            try {
                commandObject = this.stack.peek();
                cmnd = (GenericCommand<URI>) commandObject;
                if (cmnd == null) {
                    //put the later actions back into our undo stack
                    while(tempStack.size() > 0) {
                        undoable = tempStack.pop();
                        this.stack.push(undoable);
                    }
                    throw new IllegalStateException("there are no actions on the command stack for the given URI");
                }
            }
            catch (ClassCastException e) {
                cs = new CommandSet<>();
            }
        }
        //undo the last put or delete that was done with the given URI as its key
        Undoable undoable = this.stack.pop();
        try {
            cmnd = (GenericCommand<URI>) commandObject;
            cmnd.undo();
        }
        catch (ClassCastException e) {
            cs = (CommandSet<URI>) commandObject;
            cs.undo(uri);
        }
        undoable = this.stack.pop();
        if (cs.size() > 0) {
            this.stack.push(cs);
        }
        //put the later actions back into our undo stack
        while(tempStack.size() > 0) {
            undoable = tempStack.pop();
            this.stack.push(undoable);
        }
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword) {
        Comparator<URI> comp = (o1, o2) -> {
            DocumentImpl doc1 = (DocumentImpl) this.bTree.get(o1);
            DocumentImpl doc2 = (DocumentImpl) this.bTree.get(o2);
            if (doc1.wordCount(keyword) > doc2.wordCount(keyword)) {
                return 1;
            }
            if (doc1.wordCount(keyword) == doc2.wordCount(keyword)) {
                return 0;
            }
            if (doc1.wordCount(keyword) < doc2.wordCount(keyword)) {
                return -1;
            }
            return 0;
        };
        List<URI> uriList = this.trie.getAllSorted(keyword, comp);
        List<Document> docList = new ArrayList<>();
        for (URI u: uriList) {
            DocumentImpl doc = (DocumentImpl) this.get(u);
            docList.add(doc);
        }
        //update the documents' last use time
        for (Document d: docList) {
            d.setLastUseTime(System.nanoTime());
        }
        Collections.reverse(docList);
        return docList;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix){
        Comparator<URI> comp = (o1, o2) -> {
            DocumentImpl doc1 = (DocumentImpl) this.bTree.get(o1);
            DocumentImpl doc2 = (DocumentImpl) this.bTree.get(o2);
            if (doc1.wordCount(keywordPrefix) > doc2.wordCount(keywordPrefix)) {
                return -1;
            }
            if (doc1.wordCount(keywordPrefix) == doc2.wordCount(keywordPrefix)) {
                return 0;
            }
            if (doc1.wordCount(keywordPrefix) < doc2.wordCount(keywordPrefix)) {
                return 1;
            }
            return 1;
        };
        List<URI> uriList = this.trie.getAllWithPrefixSorted(keywordPrefix, comp);
        List<Document> docList = new ArrayList<>();
        for (URI u: uriList) {
            DocumentImpl doc = (DocumentImpl) this.get(u);
            docList.add(doc);
        }
        //update the documents' last use time
        for (Document d: docList) {
            d.setLastUseTime(System.nanoTime());
        }
        Collections.reverse(docList);
        return docList;
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * Search is CASE SENSITIVE.
     *
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword) {
        //delete the documents from the trie with this keyword (which are represented now by URIs)
        Set<URI> deletedURIs = this.trie.deleteAll(keyword);
        //create new command set
        CommandSet<URI> commandSet = new CommandSet<>();
        for (URI u: deletedURIs) {
            //delete each doc from the HashTable
            //problem: Can't call the regular doc store delete since it will add each individual undo on to the command stack
            //solution: Create a new private delete that will add directly to the new commmand set using the commandset methods
            deleteCS(u, commandSet);
        }
        stack.push(commandSet);
        return deletedURIs;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     *
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix) {
        //delete the documents from the trie with this prefix (which are represented now by URIs)
        Set<URI> deletedURIs = this.trie.deleteAllWithPrefix(keywordPrefix);
        //create new command set
        CommandSet<URI> commandSet = new CommandSet<>();
        for (URI u: deletedURIs) {
            //delete each doc from the HashTable
            //problem: Can't call the regular doc store delete since it will add each individual undo on to the command stack
            //solution: Create a new private delete that will add directly to the new commmand set using the commandset methods
            deleteCS(u, commandSet);
        }
        stack.push(commandSet);
        return deletedURIs;
    }

    /**
     * set maximum number of documents that may be stored
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentCount(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("the limit is negative");
        }
        if (limit < this.numberOfDocuments()) {
            int num = this.numberOfDocuments();
            while (limit < num) {
                //get the smallest element
                MinHeapNode minHeapNode  = this.minHeap.remove();
                //get the URI
                URI uriToMoveToDisk = minHeapNode.getUri();
                //get the Document
                DocumentImpl documentToDelete = (DocumentImpl) this.bTree.get(uriToMoveToDisk);
                //move the URI to disk
                try {
                    this.bTree.moveToDisk(documentToDelete.getKey());
                    this.serializedURIs.add(documentToDelete.getKey());
                } catch (Exception e) {
                    return;
                }
                //subtract 1 from the amount of current documents
                num = num -1;
            }
        }
        this.maxDocumentCount = limit;
    }

    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     *
     * @param limit
     */
    @Override
    public void setMaxDocumentBytes(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("the limit is negative");
        }
        if (limit < this.numberOfBytes()) {
            int numberOfBytes = this.numberOfBytes();
            while (limit < numberOfBytes) {
                //get the smallest element
                MinHeapNode minHeapNode  = this.minHeap.remove();
                //get the URI
                URI uriToMoveToDisk = minHeapNode.getUri();
                //get the Document
                DocumentImpl documentToDelete = (DocumentImpl) this.bTree.get(uriToMoveToDisk);
                //move the URI to disk
                try {
                    this.bTree.moveToDisk(documentToDelete.getKey());
                    this.serializedURIs.add(documentToDelete.getKey());
                } catch (Exception e) {
                    return;
                }
                //subtract the current bytes from the amount of number of bytes
                //for string documents
                if (documentToDelete.getDocumentTxt() != null) {
                    numberOfBytes -= documentToDelete.getDocumentTxt().getBytes().length;
                }
                //for binary documents
                if (documentToDelete.getDocumentBinaryData() != null) {
                    numberOfBytes -= documentToDelete.getDocumentBinaryData().length;
                }
            }
        }
        this.maxDocumentBytes = limit;
    }



    //method to remove all the docs from the minHeap, count the number of bytes of memory used, and then put them back
    private int numberOfBytes() {
        int numberOfBytes = 0;
        MinHeapNode[] minHeapNodesToPutBack = new MinHeapNode[1];
        MinHeapNode node = null;
        boolean condition = true;
        while (condition == true) {
            try {
                //remove the element
                Object objectToDelete = this.minHeap.remove();
                //add the document to the array of documents to put back
                node = (MinHeapNode) objectToDelete;
                //if no exception thrown, up the count of the number of bytes
                DocumentImpl document = (DocumentImpl) this.bTree.get(node.uri);
                //for string documents
                if (document.getDocumentTxt() != null) {
                    numberOfBytes += document.getDocumentTxt().getBytes().length;
                }
                //for binary documents
                if (document.getDocumentBinaryData() != null) {
                    numberOfBytes += document.getDocumentBinaryData().length;
                }
                //add a space for the dock by upping the size of the array
                minHeapNodesToPutBack = arrayEnlargerBytes(minHeapNodesToPutBack);
                minHeapNodesToPutBack[minHeapNodesToPutBack.length-1] = node;
            }
            //the minHeap is empty, so add all the docs back
            catch (NoSuchElementException e) {
                for (int i = 1; i < minHeapNodesToPutBack.length; i++) {
                    this.minHeap.insert(minHeapNodesToPutBack[i]);
                }
                //update the condition to false
                condition = false;
            }
        }
        return numberOfBytes;
    }

    private MinHeapNode[] arrayEnlargerBytes(MinHeapNode[] minHeapNodesToPutBack) {
        //create an array one size bigger than the size of the current array
        MinHeapNode[] arrayPlusOne = new MinHeapNode[minHeapNodesToPutBack.length + 1];
        //copy over all the elements to the new array
        for (int i = 0; i < minHeapNodesToPutBack.length; i++) {
            arrayPlusOne[i] = minHeapNodesToPutBack[i];
        }
        minHeapNodesToPutBack = arrayPlusOne;
        return minHeapNodesToPutBack;
    }

    //method to delete documents, create their undos, and add them to a command set
    private boolean deleteCS(URI uri, CommandSet<URI> commandSet) {
        try {
            //delete the document from the hashtable
            Object objectToDelete = this.bTree.put(uri,null);
            DocumentImpl documentToDelete = (DocumentImpl) objectToDelete;
            objectToDelete = this.bTree.put(uri, documentToDelete);
            //delete the document from the minHeap
            //set the lastUseTime to 0
            documentToDelete.setLastUseTime(0);
            //call reHeapify(documentToDelete) to bring the document to the top of minHeap
            MinHeapNode m = new MinHeapNode(documentToDelete.getKey(), this.bTree);
            try {
                this.minHeap.reHeapify(m);
            }
            catch (Exception e) {

            }
            //remove
            this.minHeap.remove();
            //delete the document from the hashtable
             objectToDelete = this.bTree.put(uri,null);
            //undo the command by re-putting the deleted document
            Function<URI,Boolean> undo = (x) -> {
                try {
                    if (documentToDelete.getDocumentTxt() != null) {
                        byte[] bytes = documentToDelete.getDocumentTxt().getBytes();
                        final InputStream docInput = new ByteArrayInputStream(bytes);
                        DocumentFormat text = DocumentFormat.TXT;
                        this.put(docInput, documentToDelete.getKey(), text);
                    }
                    if (documentToDelete.getDocumentBinaryData() != null) {
                        final InputStream docInput = new ByteArrayInputStream(documentToDelete.getDocumentBinaryData());
                        DocumentFormat binary = DocumentFormat.BINARY;
                        this.put(docInput, documentToDelete.getKey(),binary);
                    }
                } catch (IOException e) {
                    return false;
                }
                return true;
            };
            //create Generic Command
            GenericCommand<URI> command = new GenericCommand<>(uri, undo);
            //add the command to the stack
            commandSet.addCommand(command);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    //method that adds to the stack
    private void addGCtoStack(URI uri, Function<URI,Boolean> undo) {
        GenericCommand<URI> command = new GenericCommand<>(uri, undo);
        stack.push(command);
    }

    //method to remove all the docs from the minHeap, count them, and then put them back
    private int numberOfDocuments() {
        int numberOfDocs = 0;
        MinHeapNode[] minHeapNodesToPutBack = new MinHeapNode[1];
        MinHeapNode node = null;
        boolean condition = true;
        while (condition == true) {
            try {
                //remove the element
                Object objectToDelete = this.minHeap.remove();
                //add the document to the array of documents to put back
                node = (MinHeapNode) objectToDelete;
                //if no exception thrown, up the count
                numberOfDocs += 1;
                //add a space for the dock by upping the size of the array
                minHeapNodesToPutBack = arrayEnlarger(minHeapNodesToPutBack);
                minHeapNodesToPutBack[minHeapNodesToPutBack.length-1] = node;
            }
            //the minHeap is empty, so add all the docs back
            catch (NoSuchElementException e) {
                for (int i = 1; i < minHeapNodesToPutBack.length; i++) {
                    this.minHeap.insert(minHeapNodesToPutBack[i]);
                }
                //update the condition to false
                condition = false;
            }
        }
        return numberOfDocs;
    }

    private MinHeapNode[] arrayEnlarger(MinHeapNode[] minHeapNodesToPutBack) {
        //create an array one size bigger than the size of the current array
        MinHeapNode[] arrayPlusOne = new MinHeapNode[minHeapNodesToPutBack.length + 1];
        //copy over all the elements to the new array
        for (int i = 0; i < minHeapNodesToPutBack.length; i++) {
            arrayPlusOne[i] = minHeapNodesToPutBack[i];
        }
        minHeapNodesToPutBack = arrayPlusOne;
        return minHeapNodesToPutBack;
    }
    //private to method to check if this URI is part of the serialized URIs and to remove it from the list
    private void checkSerializedURIs(URI uri) {
        if (!(this.serializedURIs == null)) {
            if (this.serializedURIs.contains(uri)) {
                this.serializedURIs.remove(uri);
            }
        }
    }



}