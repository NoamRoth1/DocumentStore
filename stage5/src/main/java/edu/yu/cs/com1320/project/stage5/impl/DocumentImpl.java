package edu.yu.cs.com1320.project.stage5.impl;


import edu.yu.cs.com1320.project.stage5.Document;

import java.net.URI;

import java.util.*;

public class DocumentImpl implements Document {
    private URI uri;
    private String text;
    private byte[] binaryData;
    private long lastUseTime;
    protected Map<String, Integer> wordCountMap = new HashMap<>();
    public DocumentImpl(URI uri, String text) {
        if(uri == null || text == null) {
            throw new IllegalArgumentException();
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = text;
        this.lastUseTime = 0L;
    }
    public DocumentImpl(URI uri, byte[] binaryData) {
        if(uri == null || binaryData == null) {
            throw new IllegalArgumentException();
        }
        if (binaryData.length == 0) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }
    public DocumentImpl(URI uri, String text, Map<String, Integer> wordCountMap) {
        if(uri == null || text == null) {
            throw new IllegalArgumentException();
        }
        if (text.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = text;
        //If the map is null, you build a map in the constructor.
        if (wordCountMap == null) {
            this.wordCountMap = new HashMap<>();
            //for each word in the document
            //add it and the number of times in appears in the document to the word count HashMap
            for (String word: this.getWords()) {
                this.wordCountMap.put(word, this.wordCount(word));
            }
        }
        //If it is not null (e.g. this document was just deserialized), you use the map that has been passed in.
        else {
            this.wordCountMap = wordCountMap;
        }
    }
    /**
     * @return content of text document
     */
    @Override
    public String getDocumentTxt() {
        return this.text;
    }
    /**
     * @return content of binary data document
     */
    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }
    /**
     * @return URI which uniquely identifies this document
     */
    @Override
    public URI getKey() {
        return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     *
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word) {
        if(this.binaryData != null) {
            return 0;
        }
        String text = this.text.replaceAll("\\p{Punct}","");
        String[] textArray = text.split(" ");
        List<String> textList = new ArrayList<>();
        for (String s: textArray) {
            textList.add(s);
        }
        int wordCount = this.countTheWords(textList, word, 0);
        return wordCount;
    }

    private int countTheWords(List<String> text, String word, int wordCount) {
        int beginning = text.indexOf(word);
        int end = text.lastIndexOf(word);
        if (beginning == -1 && end == -1) {
            return 0;
        }
        if (beginning == end) {
            return wordCount + 1;
        }
        text.remove(beginning);
        wordCount += 1;
        return countTheWords(text, word,wordCount);
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        String text = this.text.replaceAll("\\p{Punct}","");
        String[] textArray = this.text.split(" ");
        Set<String> getWords = new HashSet<>();
        for (String s : textArray) {
            getWords.add(s);
        }
        if (getWords.contains("")) {
            getWords.remove("");
        }
        return getWords;
    }

    @Override
    public long getLastUseTime() {
        return this.lastUseTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastUseTime = timeInNanoseconds;
    }

    @Override
    public Map<String, Integer> getWordMap() {
        return this.wordCountMap;
    }

    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordCountMap = wordMap;
    }

    @Override
    public boolean equals(Object o) {
        //see if it's the same object
        if (this == o) {
            return true;
        }
        //see if it's null
        if (o == null) {
            return false;
        }
        //see if they're from the same class
        if (!(o instanceof DocumentImpl)) {
            return false;
        }
        //cast other object to my class
        DocumentImpl otherDocumentImpl = (DocumentImpl) o;
        //test equality of the hashCodes, if the hashCodes are equal return True, else return False
        if (this.hashCode() == otherDocumentImpl.hashCode()) {
            return true;
        }
        else return false;
    }
    @Override
    public int hashCode() {
        int result = this.uri.hashCode();
        result = 31 * result + (this.text != null ? this.text.hashCode() : 0); result = 31 * result + Arrays.hashCode(this.binaryData);
        return Math.abs(result);
    }


    @Override
    public int compareTo(Document o) {
        //@throws NullPointerException if the specified object is null
        if (o == null) {
            throw new NullPointerException("the specified object is null");
        }
        //@throws ClassCastException if the specified object's type prevents it from being compared to this object.
        if (!(o instanceof DocumentImpl)) {
            throw new ClassCastException("the specified object's type prevents it from being compared to this object.");
        }
        DocumentImpl otherDocumentImpl = (DocumentImpl) o;
        if (otherDocumentImpl.lastUseTime > this.lastUseTime) {
            return -1;
        }
        if (otherDocumentImpl.lastUseTime == this.lastUseTime) {
            return 0;
        }
        if (this.lastUseTime > otherDocumentImpl.lastUseTime) {
            return 1;
        }
        return 1;
    }
}