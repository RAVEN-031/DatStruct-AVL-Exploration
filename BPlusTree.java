// ===============================
// BPlusTree.java
// ===============================

import java.util.*;

class BPlusNode {
    boolean isLeaf;
    List<Integer> keys;

    public BPlusNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
    }
}

class InternalNode extends BPlusNode {
    List<BPlusNode> children;

    public InternalNode() {
        super(false);
        children = new ArrayList<>();
    }
}

class LeafNode extends BPlusNode {
    LeafNode next;

    public LeafNode() {
        super(true);
        next = null;
    }
}

public class BPlusTree {
    private static final int ORDER = 3;

    private BPlusNode root;

    public BPlusTree() {
        root = new LeafNode();
    }

    public void insert(int key) {
        LeafNode leaf = findLeaf(key);

        insertIntoLeaf(leaf, key);
    }

    private LeafNode findLeaf(int key) {
        BPlusNode current = root;

        while (!current.isLeaf) {
            InternalNode internal = (InternalNode) current;

            int i = 0;

            while (i < internal.keys.size()) {
                if (key < internal.keys.get(i))
                    break;

                i++;
            }

            current = internal.children.get(i);
        }

        return (LeafNode) current;
    }

    private void insertIntoLeaf(LeafNode leaf, int key) {
        int i = 0;

        while (i < leaf.keys.size() && key > leaf.keys.get(i))
            i++;

        leaf.keys.add(i, key);

        if (leaf.keys.size() >= ORDER)
            splitLeaf(leaf);
    }

    private void splitLeaf(LeafNode leaf) {
        LeafNode newLeaf = new LeafNode();

        int mid = ORDER / 2;

        while (leaf.keys.size() > mid) {
            newLeaf.keys.add(leaf.keys.remove(mid));
        }

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;

        if (leaf == root) {
            InternalNode newRoot = new InternalNode();

            newRoot.keys.add(newLeaf.keys.get(0));
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);

            root = newRoot;
        }
    }

    public boolean search(int key) {
        LeafNode leaf = findLeaf(key);

        return leaf.keys.contains(key);
    }

    public List<Integer> rangeQuery(int start, int end) {
        List<Integer> result = new ArrayList<>();

        LeafNode leaf = findLeaf(start);

        while (leaf != null) {
            for (int key : leaf.keys) {
                if (key >= start && key <= end)
                    result.add(key);

                if (key > end)
                    return result;
            }

            leaf = leaf.next;
        }

        return result;
    }

    public void printLeaves() {
        BPlusNode current = root;

        while (!current.isLeaf)
            current = ((InternalNode) current).children.get(0);

        LeafNode leaf = (LeafNode) current;

        while (leaf != null) {
            System.out.print(leaf.keys);

            if (leaf.next != null)
                System.out.print(" -> ");

            leaf = leaf.next;
        }

        System.out.println();
    }

    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree();

        int[] values = {10, 20, 5, 6, 12, 30, 7, 17, 3, 1, 25, 8};

        for (int value : values)
            tree.insert(value);

        System.out.println("=== B+ Tree ===");

        System.out.print("Leaf Chain: ");
        tree.printLeaves();

        System.out.println("Search 12: " + tree.search(12));
        System.out.println("Search 99: " + tree.search(99));

        System.out.println("Range [5,17]: " + tree.rangeQuery(5, 17));
        System.out.println("Range [20,30]: " + tree.rangeQuery(20, 30));
    }
}
