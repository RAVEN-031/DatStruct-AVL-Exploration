/**
 * Implementasi B-Tree Orde 3 (maksimum 2 key per node)
 * 
 * Tugas 3 - ET234203 Struktur Data dan Pemrograman Berorientasi Objek
 * Jenis Tree: B-Tree (Dasar)
 */
public class BTree {

    private static final int ORDER = 3; // Orde B-Tree (max anak per node)
    private static final int MAX_KEYS = ORDER - 1; // Maks key per node = 2
    private static final int MIN_KEYS = (int) Math.ceil(ORDER / 2.0) - 1; // Min key = 1

    // ===================== NODE =====================
    static class Node {
        int[] keys;
        int keyCount;
        Node[] children;
        boolean isLeaf;

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
            this.keys = new int[MAX_KEYS + 1]; // +1 untuk sementara saat split
            this.children = new Node[ORDER + 1];
            this.keyCount = 0;
        }
    }

    private Node root;

    public BTree() {
        root = new Node(true);
    }

    // ===================== SEARCH =====================
    public boolean search(int key) {
        return searchNode(root, key) != null;
    }

    private Node searchNode(Node node, int key) {
        if (node == null) return null;

        int i = 0;
        // Cari posisi key
        while (i < node.keyCount && key > node.keys[i]) {
            i++;
        }

        // Key ditemukan di node ini
        if (i < node.keyCount && key == node.keys[i]) {
            return node;
        }

        // Jika leaf dan tidak ditemukan
        if (node.isLeaf) return null;

        // Rekursif ke child yang tepat
        return searchNode(node.children[i], key);
    }

    // ===================== INSERT =====================
    public void insert(int key) {
        Node r = root;

        // Jika root sudah penuh, buat root baru
        if (r.keyCount == MAX_KEYS) {
            Node newRoot = new Node(false);
            newRoot.children[0] = r;
            splitChild(newRoot, 0, r);
            root = newRoot;
            insertNonFull(newRoot, key);
        } else {
            insertNonFull(r, key);
        }
    }

    private void insertNonFull(Node node, int key) {
        int i = node.keyCount - 1;

        if (node.isLeaf) {
            // Geser key yang lebih besar ke kanan
            while (i >= 0 && key < node.keys[i]) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.keyCount++;
        } else {
            // Temukan child yang tepat
            while (i >= 0 && key < node.keys[i]) {
                i--;
            }
            i++;

            // Jika child penuh, split dulu
            if (node.children[i].keyCount == MAX_KEYS) {
                splitChild(node, i, node.children[i]);
                if (key > node.keys[i]) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key);
        }
    }

    private void splitChild(Node parent, int i, Node fullChild) {
        Node newChild = new Node(fullChild.isLeaf);
        int mid = MAX_KEYS / 2; // Index key tengah

        newChild.keyCount = MAX_KEYS - mid - 1;

        // Copy separuh kanan key ke newChild
        for (int j = 0; j < newChild.keyCount; j++) {
            newChild.keys[j] = fullChild.keys[j + mid + 1];
        }

        // Copy separuh kanan children ke newChild (jika bukan leaf)
        if (!fullChild.isLeaf) {
            for (int j = 0; j <= newChild.keyCount; j++) {
                newChild.children[j] = fullChild.children[j + mid + 1];
            }
        }

        fullChild.keyCount = mid;

        // Geser children parent ke kanan
        for (int j = parent.keyCount; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = newChild;

        // Geser keys parent ke kanan
        for (int j = parent.keyCount - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
        }

        // Promosikan key tengah ke parent
        parent.keys[i] = fullChild.keys[mid];
        parent.keyCount++;
    }

    // ===================== DISPLAY =====================
    public void display() {
        System.out.println("\n=== B-Tree (Orde " + ORDER + ") ===");
        displayNode(root, 0);
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

    // ===================== MAIN =====================
    public static void main(String[] args) {
        BTree tree = new BTree();

        int[] data = {10, 20, 5, 6, 12, 30, 7, 17, 3, 1, 25, 8};
        System.out.println("=== B-Tree Insert Demo ===");
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

        // Performance Test
        System.out.println("\n=== Performance Test (100.000 insert + 10.000 search) ===");
        BTree perfTree = new BTree();

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

        System.out.printf("Insert 100.000 data : %.2f ms%n", (endInsert - startInsert) / 1_000_000.0);
        System.out.printf("Search 10.000 data  : %.2f ms%n", (endSearch - startSearch) / 1_000_000.0);
    }
}
