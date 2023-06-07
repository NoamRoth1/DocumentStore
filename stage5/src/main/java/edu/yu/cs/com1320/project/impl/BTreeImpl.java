package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value> {
    //max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private static final int MAX = 4;
    private Node root; //root of the B-tree
    private Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree
    private PersistenceManager<Key, Value> pm;


    //B-tree node data type
    private static final class Node {
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[BTreeImpl.MAX]; // the array of children
        private Node next;
        private Node previous;

        // create a node with k entries
        private Node(int k)
        {
            this.entryCount = k;
        }

        private void setNext(Node next)
        {
            this.next = next;
        }
        private Node getNext()
        {
            return this.next;
        }
        private void setPrevious(Node previous)
        {
            this.previous = previous;
        }
        private Node getPrevious()
        {
            return this.previous;
        }

        private Entry[] getEntries()
        {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    //internal nodes: only use key and child
    //external nodes: only use key and value
    public static class Entry {
        private Comparable key;
        private Object val;
        private Node child;

        public Entry(Comparable key, Object val, Node child)
        {
            this.key = key;
            this.val = val;
            this.child = child;
        }
        public Object getValue()
        {
            return this.val;
        }
        public Comparable getKey()
        {
            return this.key;
        }
    }

    /**
     * Initializes an empty B-tree.
     */
    public BTreeImpl() {
        this.root = new Node(0);
        this.leftMostExternalNode = this.root;
        this.height = 0;
        this.n = 0;
        this.pm = null;
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param k the key
     * @return the value associated with the given key if the key is in the
     *         symbol table and {@code null} if the key is not in the symbol
     *         table
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    @Override
    public Value get(Key k) {
        if (k == null) {
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry entry = this.get(this.root, k, this.height);
        if(entry != null) {
            if (entry.val instanceof  File) {
                //if the entry is returning a value that's a file
                //deserialize the document, delete it's reference, update it's last use time
                //and set the entries new value to this doc
                try {
                    Value v = this.pm.deserialize(k);
                    DocumentImpl doc = (DocumentImpl) v;
                    doc.setLastUseTime(System.nanoTime());
                    this.pm.delete(k);
                    entry.val = doc;
                    return v;
                }
                catch (IOException ex) {
                    return null;
                }
            }
            else {
                return (Value) entry.val;
            }
        }
        return null;
    }

    // comparison functions - make Comparable instead of Key to avoid casts
    private static boolean less(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) < 0;
    }

    private static boolean isEqual(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) == 0;
    }

    private Entry get(Node currentNode, Key key, int height) {
        Entry[] entries = currentNode.entries;
        //current node is external (i.e. height == 0)
        if (height == 0) {
            for (int j = 0; j < currentNode.entryCount; j++) {
                if (isEqual(key, entries[j].key)) {
                    //found desired key. Return the entry
                    return entries[j];
                }
            }
            //didn't find the key
            return null;
        }
        //current node is internal (height > 0)
        else {
            for (int j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry, a.k.a.
                //entries[j]), then recurse into the current entry’s child/subtree
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key)) {
                    return this.get(entries[j].child, key, height - 1);
                }
            }
        }
        return null;
    }


    /**
     * Inserts the key-value pair into the symbol table, overwriting the old
     * value with the new value if the key is already in the symbol table. If
     * the value is {@code null}, this effectively deletes the key from the
     * symbol table.
     *
     * @param k the key
     * @param v the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    @Override
    public Value put(Key k, Value v) {
        if (k == null) {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null) {
            Value oldValueToReturn = (Value) alreadyThere.val;
            //replace the value
            alreadyThere.val = v;
            if (oldValueToReturn instanceof  File) {
                //if the entry is returning a value that's a file
                //deserialize the document, delete it's reference, update it's last use time
                //and return the old doc
                try {
                    v = this.pm.deserialize(k);
                    DocumentImpl doc = (DocumentImpl) v;
                    doc.setLastUseTime(System.nanoTime());
                    this.pm.delete(k);
                    return (Value) doc;
                }
                catch (IOException ex) {
                    return null;
                }
            }
            return oldValueToReturn;
        }
        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null){
            //no split of root, we’re done so return null since there was no previous value for the given key
            return null;
        }
        //private put method only returns non-null if root.entryCount == Btree.MAX
        //(see if-else on previous slide.) Private code will have copied the upper M/2
        //entries over. We now set the old root to be new root's first entry, and
        //set the node returned from private method to be new root's second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        //a split at the root always increases the tree height by 1
        this.height++;
        //return null since there was no previous value for the given key
        return null;
    }

    private Node put(Node currentNode, Key k, Value v, int height) {
        int j;
        Entry newEntry = new Entry(k, v, null);
        //if this is an external/leaf node...
        if (height == 0) {
            //Set j to the index of the first entry in the current node whose
            //key > the new key
            for (j = 0; j < currentNode.entryCount; j++) {
                if (less(k, currentNode.entries[j].key)) {
                    break;
                }
            }
        }
        else {
            //find the index in currentNode.entries[] at which we recurse down to the next
            //level. On the way back up, we may have to insert a link to a new node.
            for (j = 0; j < currentNode.entryCount; j++) {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry, a.k.a.
                //entries[j]), then recurse into the current entry’s child...
                if ((j + 1 == currentNode.entryCount) || less(k, currentNode.entries[j + 1].key)) {
                    //increment j (j++) after the call so that a new entry created by a
                    //split will be inserted in the next slot
                    Node newNode = this.put(currentNode.entries[j++].child, k, v, height - 1);
                    if (newNode == null) {
                        return null;
                    }
                    //if !null, I have to add the new entry to the current node. Populate the newEntry
                    //variable created earlier:
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        //shift greater entries over one place to make for the new entry
        for (int i = currentNode.entryCount; i > j; i--) {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        //insert the new entry into slot j in the current node
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX) {
            //no structural changes needed in the tree
            //so just return null
            return null;
        }
        else {
            //we have to split this node and create a new entry in the parent due
            //to the split. We return the new node which is created by the split
            //for which the parent will have to create an entry
            return this.split(currentNode);
        }
    }

    private Node split (Node currentNode) {
        Node newNode = new Node(BTreeImpl.MAX/2);
        //copy tail end of currentNode, which has been chopped off into newNode
        for (int j = 0; j < BTreeImpl.MAX / 2; j++){
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
            //set references in tail end of currentNode to null to avoid memory leaks
            currentNode.entries[BTreeImpl.MAX / 2 + j] = null;
        }
        //divide currentNode.entryCount by 2
        currentNode.entryCount = BTreeImpl.MAX / 2;
        return newNode;
    }


    @Override
    public void moveToDisk(Key k) throws Exception {
        //get the entry in memory for the given key
        Entry e = this.get(this.root, k, this.height);
        //if the value is not of type DocumentImpl then the document has already been written to disk or deleted
        if (!(e.val instanceof DocumentImpl)) {
            throw new NoSuchElementException();
        }
        if (k == null) {
            throw new IllegalArgumentException();
        }
        this.pm.serialize(k, (Value) e.val);
        //set the value of the entry to a reference to where the document is stored on disk
        //i.e "reference to file on disk" would usually be something like a File object or a path to the doc on disk,
        //while the key would be something that you should have a deterministic way to translate into a path on disk
        //to save the doc to
        String newUri = k.toString().substring(7) + ".json";
        String userDir = System.getProperty(("user.dir"));
        File file = new File(new File(userDir), newUri);
        e.val = file;
    }

    @Override
    public void setPersistenceManager(PersistenceManager<Key, Value> pm) {
        this.pm = pm;
    }

}
