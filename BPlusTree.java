/**
 * Implementasi B+ Tree Orde 3
 * Variasi modifikasi dari B-Tree dengan:
 * - Semua data tersimpan di leaf node
 * - Leaf node terhubung sebagai linked list (untuk range query efisien)
 * - Node internal hanya menyimpan key sebagai guide
 * 
 * Tugas 3 - ET234203 Struktur Data dan Pemrograman Berorientasi Objek
 * Jenis Tree: B+ Tree (Modifikasi B-Tree)
 */
import java.util.ArrayList;
import java.util.List;

public class BPlusTree {

    private static final int ORDER = 3;
    private static final int MAX_KEYS = ORDER - 1;

    // ===================== NODE =====================
    static class Node {
        int[] keys;
        int keyCount;
        Node[] children;
        boolean isLeaf;

        // Hanya untuk leaf node: pointer ke leaf berikutnya
        Node next;

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new int[MAX_KEYS + 1];
            this.children = new Node[ORDER + 1];
            this.keyCount = 0;
            this.next = null;
        }
    }

    private Node root;

    public BPlusTree() {
        root = new Node(true);
    }

    // ===================== SEARCH =====================
    /**
     * Pencarian selalu berakhir di leaf node (berbeda dengan B-Tree).
     */
    public boolean search(int key) {
        Node leaf = findLeaf(root, key);
        for (int i = 0; i < leaf.keyCount; i++) {
            if (leaf.keys[i] == key) return true;
        }
        return false;
    }

    private Node findLeaf(Node node, int key) {
        if (node.isLeaf) return node;

        int i = 0;
        while (i < node.keyCount && key >= node.keys[i]) {
            i++;
        }
        return findLeaf(node.children[i], key);
    }

    // ===================== RANGE QUERY =====================
    /**
     * Range Query: menemukan semua key antara low dan high (inklusif).
     * Keunggulan utama B+ Tree: efisien karena leaf terhubung sebagai linked list.
     * Kompleksitas: O(log n + k) dengan k = jumlah hasil.
     */
    public List<Integer> rangeQuery(int low, int high) {
        List<Integer> result = new ArrayList<>();
        Node leaf = findLeaf(root, low);

        while (leaf != null) {
            for (int i = 0; i < leaf.keyCount; i++) {
                if (leaf.keys[i] >= low && leaf.keys[i] <= high) {
                    result.add(leaf.keys[i]);
                } else if (leaf.keys[i] > high) {
                    return result;
                }
            }
            leaf = leaf.next; // Traversal linked list leaf
        }
        return result;
    }

    // ===================== INSERT =====================
    public void insert(int key) {
        // Jika root penuh, buat root baru
        if (root.keyCount == MAX_KEYS) {
            Node newRoot = new Node(false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0, root);
            root = newRoot;
        }
        insertNonFull(root, key);
    }

    private void insertNonFull(Node node, int key) {
        if (node.isLeaf) {
            // Sisipkan key di leaf secara terurut
            int i = node.keyCount - 1;
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.keyCount++;
        } else {
            int i = 0;
            while (i < node.keyCount && key >= node.keys[i]) {
                i++;
            }

            if (node.children[i].keyCount == MAX_KEYS) {
                splitChild(node, i, node.children[i]);
                if (key >= node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key);
        }
    }

    /**
     * Split node dalam B+ Tree.
     * PERBEDAAN UTAMA dengan B-Tree:
     * - Saat split LEAF: key tengah disalin (bukan dipindah) ke parent,
     *   sehingga key tetap ada di leaf.
     * - Saat split INTERNAL: key tengah dipindah ke parent (sama seperti B-Tree).
     * - Setelah split leaf, atur pointer linked list (next).
     */
    private void splitChild(Node parent, int i, Node fullChild) {
        Node newChild = new Node(fullChild.isLeaf);
        int mid = MAX_KEYS / 2;

        if (fullChild.isLeaf) {
            // Split leaf: salin separuh kanan ke newChild
            newChild.keyCount = fullChild.keyCount - mid;
            for (int j = 0; j < newChild.keyCount; j++) {
                newChild.keys[j] = fullChild.keys[j + mid];
            }
            fullChild.keyCount = mid;

            // Atur linked list leaf
            newChild.next = fullChild.next;
            fullChild.next = newChild;

            // Geser children dan keys di parent
            for (int j = parent.keyCount; j >= i + 1; j--) {
                parent.children[j + 1] = parent.children[j];
            }
            parent.children[i + 1] = newChild;

            for (int j = parent.keyCount - 1; j >= i; j--) {
                parent.keys[j + 1] = parent.keys[j];
            }

            // Promosikan key pertama dari newChild (disalin, bukan dipindah)
            parent.keys[i] = newChild.keys[0];
            parent.keyCount++;

        } else {
            // Split internal node (sama seperti B-Tree: key tengah dipindah ke parent)
            newChild.keyCount = MAX_KEYS - mid - 1;
            for (int j = 0; j < newChild.keyCount; j++) {
                newChild.keys[j] = fullChild.keys[j + mid + 1];
            }
            for (int j = 0; j <= newChild.keyCount; j++) {
                newChild.children[j] = fullChild.children[j + mid + 1];
            }
            fullChild.keyCount = mid;

            for (int j = parent.keyCount; j >= i + 1; j--) {
                parent.children[j + 1] = parent.children[j];
            }
            parent.children[i + 1] = newChild;

            for (int j = parent.keyCount - 1; j >= i; j--) {
                parent.keys[j + 1] = parent.keys[j];
            }

            // Promosikan key tengah ke parent (key dipindah, tidak ada di internal lagi)
            parent.keys[i] = fullChild.keys[mid];
            parent.keyCount++;
        }
    }

    // ===================== DISPLAY =====================
    public void display() {
        System.out.println("\n=== B+ Tree (Orde " + ORDER + ") ===");
        displayNode(root, 0);
        displayLeafChain();
        System.out.println();
    }

    private void displayNode(Node node, int level) {
        if (node == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("  ".repeat(level));
        sb.append("[");
        for (int i = 0; i < node.keyCount; i++) {
            sb.append(node.keys[i]);
            if (i < node.keyCount - 1) sb.append("|");
        }
        sb.append("]");
        if (node.isLeaf) sb.append(" (leaf)");
        System.out.println(sb.toString());

        if (!node.isLeaf) {
            for (int i = 0; i <= node.keyCount; i++) {
                displayNode(node.children[i], level + 1);
            }
        }
    }

    private void displayLeafChain() {
        // Tampilkan linked list leaf dari kiri ke kanan
        Node leaf = getLeftmostLeaf(root);
        System.out.print("Leaf Chain: ");
        while (leaf != null) {
            System.out.print("[");
            for (int i = 0; i < leaf.keyCount; i++) {
                System.out.print(leaf.keys[i]);
                if (i < leaf.keyCount - 1) System.out.print("|");
            }
            System.out.print("]");
            if (leaf.next != null) System.out.print(" -> ");
            leaf = leaf.next;
        }
        System.out.println();
    }

    private Node getLeftmostLeaf(Node node) {
        if (node.isLeaf) return node;
        return getLeftmostLeaf(node.children[0]);
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        BPlusTree tree = new BPlusTree();

        int[] data = {10, 20, 5, 6, 12, 30, 7, 17, 3, 1, 25, 8};
        System.out.println("=== B+ Tree Insert Demo ===");
        System.out.print("Inserting: ");
        for (int key : data) {
            System.out.print(key + " ");
            tree.insert(key);
        }
        System.out.println();

        tree.display();

        // Test Search
        System.out.println("=== Search Test ===");
        int[] searchKeys = {12, 99, 7, 25};
        for (int k : searchKeys) {
            System.out.println("Search " + k + ": " + (tree.search(k) ? "FOUND" : "NOT FOUND"));
        }

        // Test Range Query
        System.out.println("\n=== Range Query Test ===");
        System.out.println("Range [5, 17]  : " + tree.rangeQuery(5, 17));
        System.out.println("Range [1, 30]  : " + tree.rangeQuery(1, 30));
        System.out.println("Range [20, 30] : " + tree.rangeQuery(20, 30));

        // Performance Test
        System.out.println("\n=== Performance Test (100.000 insert + 10.000 search + 1.000 range query) ===");
        BPlusTree perfTree = new BPlusTree();

        long startInsert = System.nanoTime();
        for (int i = 0; i < 100_000; i++) {
            perfTree.insert(i);
        }
        long endInsert = System.nanoTime();

        long startSearch = System.nanoTime();
        for (int i = 0; i < 10_000; i++) {
            perfTree.search(i * 10);
        }
        long endSearch = System.nanoTime();

        long startRange = System.nanoTime();
        for (int i = 0; i < 1_000; i++) {
            perfTree.rangeQuery(i * 100, i * 100 + 50);
        }
        long endRange = System.nanoTime();

        System.out.printf("Insert 100.000 data         : %.2f ms%n", (endInsert - startInsert) / 1_000_000.0);
        System.out.printf("Search 10.000 data          : %.2f ms%n", (endSearch - startSearch) / 1_000_000.0);
        System.out.printf("Range query 1.000x (50 item): %.2f ms%n", (endRange - startRange) / 1_000_000.0);
    }
}
