// ===============================
// PerformanceTest.java
// ===============================

import java.util.*;

public class PerformanceTest {

    public static void main(String[] args) {

        int SIZE = 100000;

        Random rand = new Random();

        BTree btree = new BTree(3);
        BPlusTree bPlusTree = new BPlusTree();

        int[] data = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            data[i] = rand.nextInt(1000000);
        }

        long start;
        long end;

        start = System.nanoTime();

        for (int value : data)
            btree.insert(value);

        end = System.nanoTime();

        System.out.println("B-Tree Insert Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();

        for (int value : data)
            bPlusTree.insert(value);

        end = System.nanoTime();

        System.out.println("B+ Tree Insert Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();

        for (int i = 0; i < 10000; i++)
            btree.search(data[rand.nextInt(SIZE)]);

        end = System.nanoTime();

        System.out.println("B-Tree Search Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();

        for (int i = 0; i < 10000; i++)
            bPlusTree.search(data[rand.nextInt(SIZE)]);

        end = System.nanoTime();

        System.out.println("B+ Tree Search Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();

        for (int i = 0; i < 1000; i++) {
            int a = rand.nextInt(500000);
            int b = a + 50;

            bPlusTree.rangeQuery(a, b);
        }

        end = System.nanoTime();

        System.out.println("B+ Tree Range Query Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
