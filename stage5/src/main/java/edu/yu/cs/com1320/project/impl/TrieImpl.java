package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.Trie;

import java.util.*;


public class TrieImpl<Value> implements Trie<Value> {
    private static final int alphabetSize = 128; // extended ASCII
    private Node<Value> root; // root of trie

    public static class Node<Value>
    {
        protected Set<Value> values = new HashSet<Value>();
        protected Node<Value>[] links = new Node[TrieImpl.alphabetSize];
    }

    public TrieImpl() {
        this.root = null;
    }

    /**
     * add the given value at the given key
     *
     * @param key
     * @param val
     */
    @Override
    public void put(String key, Value val) {
        //deleteAll the value from this key
        if (val == null) {
            this.deleteAll(key);
        }
        else {
            this.root = put(this.root, key, val, 0);
        }
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node<Value>();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            x.values.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE SENSITIVE.
     *
     * @param key
     * @param comparator used to sort values
     * @return a List of matching Values, in descending order
     */
    @Override
    public List<Value> getAllSorted(String key, Comparator<Value> comparator) {
        Node<Value> x = this.get(this.root, key, 0);
        if (x == null) {
            List<Value> emptyList = Collections.<Value>emptyList();;
            return emptyList;
        }
        List<Value> list = new ArrayList<>(x.values);
        Collections.sort(list, comparator);
        return list;
    }

    /**
     * A char in java has an int value.
     * see http://docs.oracle.com/javase/8/docs/api/java/lang/Character.html#getNumericValue-char-
     * see http://docs.oracle.com/javase/specs/jls/se7/html/jls-5.html#jls-5.1.2
     */
    private Node<Value> get(Node<Value> x, String key, int d) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length()) {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }

    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    @Override
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator) {
        List<Value> list = new ArrayList<Value>();
        this.collect(this.get(this.root, prefix, 0), prefix, list);
        Set<Value> set = new HashSet<>(list);
        List<Value> listWithNoDuplicates = new ArrayList<Value>(set);
        Collections.sort(listWithNoDuplicates, comparator);
        return listWithNoDuplicates;
    }
    private void collect(Node<Value> x, String pre,
                         List<Value> list) {
        if (x == null) {
            return;
        }
        if (x.values != null) {
            list.addAll(x.values);
        }
        for (char c = 0; c < alphabetSize; c++) {
            collect(x.links[c], pre + c, list);
        }
    }

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     *
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAllWithPrefix(String prefix) {
        List<Value> deletedValues = new ArrayList<Value>();
        this.collect(this.get(this.root, prefix, 0), prefix, deletedValues);
        Node<Value> x = this.deleteSubtree(this.root, prefix, 0);
        HashSet<Value> setOfDeletedValues = new HashSet<>(deletedValues);
        return setOfDeletedValues;
    }

    private Node<Value> deleteSubtree(Node<Value> x, String key, int d) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            x.values.clear();
            for (char c = 0; c < alphabetSize; c++)  {
                x.links[c] = null;
            }
            return x;
        }
        else {
            char c = key.charAt(d);
            x.links[c] = deleteSubtree(x.links[c], key, d+1);
        }
        if (x.values != null) {
            return x;
        }
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                return x;
            }
            return null;
        }
        return null;
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     *
     * @param key
     * @return a Set of all Values that were deleted.
     */
    @Override
    public Set<Value> deleteAll(String key)  {
        List<Value> deletedValues = this.getForDeleteAll(key);
        this.root = deleteAll(this.root, key, 0);
        HashSet<Value> setOfDeletedValues = new HashSet<>(deletedValues);
        return setOfDeletedValues;
    }
    private List<Value> getForDeleteAll(String key) {
        Node<Value> x = this.get(this.root, key, 0);
        if (x == null) {
            List<Value> emptyList = Collections.<Value>emptyList();;
            return emptyList;
        }
        List<Value> list = new ArrayList<>(x.values);
        return list;
    }
    private Node<Value> deleteAll(Node<Value> x, String key, int d) {
        if (x == null) {
            return null;
        }
        //we're at the node to del - set all the val to null
        if (d == key.length()) {
            x.values.clear();
        }
        //continue down the trie to the target node
        else {
            char c = key.charAt(d);
            x.links[c] = this.deleteAll(x.links[c], key, d + 1);
        }
        //this node has a val – do nothing, return the node
        if (x.values != null) {
            return x;
        }
        //remove subtrie rooted at x if it is completely empty
        for (int c = 0; c < TrieImpl.alphabetSize; c++) {
            if (x.links[c] != null) {
                return x; //not empty
            }
            //empty - set this link to null in the parent
            return null;
        }
        return null;
    }

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     *
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    @Override
    public Value delete(String key, Value val) {
        Value valueToDelete = this.get(this.root, key, 0, val);
        this.root = delete(root, key, 0, val);
        return valueToDelete;
    }
    private Node<Value> delete(Node<Value> x, String key, int d, Value val) {
        if (x == null) {
            return null;
        }
        if (d == key.length()) {
            if(x.values.remove(val)) {
                return x;
            }
            return null;
        }
        else {
            char c = key.charAt(d);
            x.links[c] = delete(x.links[c], key, d+1, val);
        }
        if (x.values != null) {
            return x;
        }
        for (char c = 0; c < alphabetSize; c++) {
            if (x.links[c] != null) {
                return x;
            }
            return null;
        }
        return null;
    }

    private Value get(Node<Value> x, String key, int d, Value val) {
        //link was null - return null, indicating a miss
        if (x == null) {
            return null;
        }
        //we've reached the last node in the key,
        //if node contains the given value return the value otherwise return null
        if (d == key.length()) {
            if (x.values.contains(val)) {
                return val;
            }
            return null;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1, val);
    }
}