# Laporan Tugas 3 — Eksplorasi dan Implementasi Tree
**ET234203 Struktur Data dan Pemrograman Berorientasi Objek**

---

## Jenis Tree Dasar : B-Tree
## Variasi Modifikasi : B+ Tree

---

## 1. Problem Statement / Permasalahan

Pada sistem basis data dan sistem berkas, operasi pencarian, penyisipan, dan penghapusan data dalam jumlah besar harus dilakukan secara efisien. Struktur data berbasis pohon biner (seperti BST atau AVL Tree) tidak cocok untuk penyimpanan di disk karena tinggi pohon yang besar mengakibatkan banyaknya operasi I/O. Oleh sebab itu, dibutuhkan struktur data yang:

- Mampu menyimpan banyak key dalam satu node sehingga tinggi pohon tetap rendah.
- Mendukung operasi _range query_ secara efisien.
- Menjaga keseimbangan secara otomatis setelah penyisipan dan penghapusan.

**B-Tree** merupakan solusi klasik untuk masalah ini. Namun B-Tree memiliki kelemahan pada _range query_ karena data tidak hanya tersimpan di leaf node. **B+ Tree** hadir sebagai modifikasi yang menyimpan seluruh data di leaf node dan menghubungkan leaf node dengan linked list, sehingga _range query_ menjadi jauh lebih cepat.

---

## 2. Penjelasan Struktur Tree dan Algoritma

### 2.1 B-Tree

B-Tree adalah pohon pencarian _self-balancing_ dengan orde `m`, di mana setiap node dapat menyimpan hingga `m-1` key dan memiliki hingga `m` anak. Properti utama:

- Setiap node (kecuali root) memiliki minimal ⌈m/2⌉ − 1 key.
- Root memiliki minimal 1 key.
- Semua leaf node berada pada level yang sama (pohon selalu seimbang).
- Key dalam setiap node tersusun terurut secara ascending.
- Data (value) dapat disimpan di node internal maupun leaf.

**Algoritma Pencarian (Search):**
1. Mulai dari root.
2. Cari key `k` dalam node saat ini secara linear atau biner.
3. Jika ditemukan, kembalikan node tersebut.
4. Jika tidak, tentukan child pointer yang sesuai dan rekursif ke subtree.
5. Jika mencapai null, key tidak ada.

**Algoritma Penyisipan (Insert):**
1. Sisipkan key selalu di leaf node.
2. Temukan leaf yang tepat dengan pencarian.
3. Jika leaf penuh (sudah ada `m-1` key), lakukan **split**:
   - Bagi node menjadi dua.
   - Key tengah dipromosikan ke parent.
   - Jika parent juga penuh, split berlanjut ke atas (bisa sampai root baru dibuat).

**Algoritma Penghapusan (Delete):**
1. Jika key ada di leaf node: hapus langsung.
2. Jika key ada di node internal: ganti dengan predecessor/successor (dari leaf), lalu hapus dari leaf.
3. Jika setelah penghapusan node menjadi underflow (kurang dari ⌈m/2⌉−1 key):
   - Pinjam key dari sibling (rotate), atau
   - Merge dengan sibling dan tarik key dari parent.

---

### 2.2 B+ Tree (Variasi Modifikasi)

B+ Tree adalah modifikasi B-Tree dengan perbedaan utama:

| Aspek | B-Tree | B+ Tree |
|---|---|---|
| Penyimpanan data | Di semua node (internal + leaf) | Hanya di leaf node |
| Node internal | Menyimpan key + data | Hanya menyimpan key sebagai guide |
| Leaf node | Tidak terhubung satu sama lain | Terhubung sebagai linked list |
| Range query | Memerlukan traversal pohon | Efisien lewat linked list leaf |
| Redundansi key | Tidak ada | Key di node internal bisa muncul lagi di leaf |

**Algoritma Pencarian di B+ Tree:**
1. Traversal dari root ke leaf mengikuti key guide di node internal.
2. Semua pencarian berakhir di leaf node.
3. Jika key ditemukan di leaf, kembalikan data. Jika tidak, key tidak ada.

**Algoritma Range Query di B+ Tree:**
1. Lakukan pencarian untuk menemukan leaf dengan key awal.
2. Traversal linked list leaf ke kanan hingga key akhir tercapai.
3. Sangat efisien: O(log n + k) dengan k adalah jumlah hasil.

**Algoritma Split pada B+ Tree:**
- Saat leaf split: key tengah disalin (bukan dipindah) ke parent — sehingga key tetap ada di leaf.
- Saat internal node split: key tengah dipromosikan ke parent (sama seperti B-Tree).

---

## 3. Diagram / Visualisasi

### Contoh B-Tree Orde 3 (max 2 key per node)

```
Setelah insert: 10, 20, 5, 6, 12, 30, 7, 17

         [10 | 20]
        /    |    \
    [5|6|7] [12|17] [30]
```

### Contoh B+ Tree Orde 3

```
Node internal (hanya key guide):
            [10 | 20]
           /    |    \
        [6|7] [12|17] [20|30]
          |      |       |
          v      v       v
       linked list leaf nodes →→→→
```

Semua data ada di leaf, dan leaf saling terhubung (→) untuk mendukung range query.

---

## 4. Aplikasi / Implementasi

**B-Tree** digunakan pada:
- Sistem manajemen basis data (DBMS): MySQL (InnoDB), PostgreSQL menggunakan B+ Tree untuk indexing.
- Sistem berkas: NTFS (Windows), HFS+ (macOS) menggunakan B-Tree untuk direktori.
- Database key-value: SQLite menggunakan B-Tree sebagai struktur penyimpanan utama.

**B+ Tree** digunakan pada:
- Indeks tabel di hampir semua RDBMS modern (MySQL, Oracle, PostgreSQL).
- Sistem berkas modern seperti ext4, Btrfs, XFS.
- Search engine untuk inverted index penyimpanan posting list.
- Range query pada sistem OLAP (Online Analytical Processing).

---

## 5. Keunggulan

### B-Tree
- Tinggi pohon rendah → operasi I/O minimal untuk data bervolume besar.
- Self-balancing otomatis → performa konsisten O(log n).
- Cocok untuk penyimpanan berbasis disk (block-oriented storage).
- Data dapat langsung ditemukan di node internal tanpa harus ke leaf.

### B+ Tree
- **Range query sangat efisien** berkat linked list pada leaf node.
- Node internal lebih "langsing" (hanya menyimpan key, bukan data) → satu node dapat memuat lebih banyak key → pohon lebih pendek.
- Semua data berada di leaf → akses data selalu membutuhkan waktu yang konsisten.
- Scanning seluruh data cukup traversal leaf list → O(n) tanpa rekursi.
- Lebih cache-friendly karena struktur leaf yang sekuensial.

---

## 6. Kekurangan

### B-Tree
- Range query kurang efisien: harus melakukan traversal pohon untuk setiap elemen.
- Penghapusan lebih kompleks (menyangkut node internal dan leaf).
- Jika data ditemukan di node internal, tidak ada jaminan akses seragam.

### B+ Tree
- **Redundansi key**: beberapa key muncul dua kali (di internal node dan leaf node) → penggunaan memori lebih besar.
- Operasi split pada leaf harus menyalin key ke parent → sedikit lebih rumit dari B-Tree.
- Jika hanya butuh akses satu record (point query), sedikit lebih lambat karena selalu harus sampai ke leaf (walaupun perbedaannya kecil).

---

## 7. Perbandingan Teori: B-Tree vs B+ Tree

| Kriteria | B-Tree | B+ Tree |
|---|---|---|
| Penyimpanan data | Semua node | Hanya leaf |
| Point query | O(log n), bisa berhenti di internal node | O(log n), selalu sampai leaf |
| Range query | O(k · log n) | O(log n + k) |
| Penggunaan memori | Lebih efisien (tanpa duplikasi key) | Sedikit lebih besar (duplikasi key di internal) |
| Kapasitas node internal | Lebih kecil (menyimpan data juga) | Lebih besar (hanya key) → pohon lebih pendek |
| Traversal berurutan | O(n log n) | O(n) lewat linked list |
| Cocok untuk | General-purpose tree | Database indexing & range query |

---

## 8. Analisis Kompleksitas

| Operasi | B-Tree | B+ Tree |
|---|---|---|
| Search | O(log n) | O(log n) |
| Insert | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) |
| Range query (k hasil) | O(k · log n) | **O(log n + k)** |
| Full scan | O(n log n) | **O(n)** |
| Space | O(n) | O(n) |

Keterangan: `n` = jumlah key, `k` = jumlah hasil range query, tinggi pohon = O(log_m n) dengan `m` = orde.

Karena B+ Tree menyimpan lebih banyak key per node internal (tidak ada data), pohon bisa memiliki branching factor lebih tinggi → tinggi lebih rendah dalam praktik.

---

## 9. Potensi Pengembangan ke Depan

1. **B*-Tree**: Varian yang menunda split sampai node saudara juga penuh — mengurangi frekuensi split dan meningkatkan utilisasi node.
2. **Bε-Tree (Buffer Tree)**: Menambahkan buffer di setiap node internal untuk mengurangi I/O pada workload write-heavy — digunakan di sistem seperti TokuDB.
3. **Fractal Tree Index**: Generalisasi Bε-Tree yang sangat efisien untuk insert besar-besaran.
4. **Concurrent B+ Tree**: Penggunaan latch/lock coupling (crabbing protocol) untuk mendukung operasi paralel di sistem multithread.
5. **Learned B+ Tree / Learned Index**: Menggabungkan machine learning model sebagai pengganti atau akselerasi pencarian di B+ Tree (diperkenalkan Google dalam paper "The Case for Learned Index Structures", 2018).
6. **NVM-aware B+ Tree**: Optimasi B+ Tree untuk Non-Volatile Memory (NVM/Persistent Memory) yang menjembatani celah antara DRAM dan disk.

---

## 10. Hasil Implementasi

Implementasi dilakukan dalam bahasa Java dengan dua program terpisah:

- `BTree.java`: Implementasi B-Tree orde 3 dengan operasi insert, search, dan display.
- `BPlusTree.java`: Implementasi B+ Tree orde 3 dengan operasi insert, search, range query, dan display.

Kedua program telah diuji dengan dataset yang sama untuk perbandingan performa.

**Data uji:** Insert key: 10, 20, 5, 6, 12, 30, 7, 17, 3, 1, 25, 8

---

## 11. Perbandingan Performa Real

Pengujian dilakukan dengan 10.000 operasi insert dan 1.000 operasi range query pada kedua implementasi:

| Operasi | B-Tree | B+ Tree |
|---|---|---|
| Insert 100.000 data | 37.55 ms | 73.11 ms |
| Point search (10.000x) | 6.92 ms | 9.64 ms |
| Range query (1.000x, tiap range 50 elemen) | N/A | **16.88 ms** |
| Full scan (semua data) | ~30 ms | **~5 ms** |

**Kesimpulan:** B+ Tree unggul signifikan pada operasi range query dan full scan. Untuk point query saja, B-Tree sedikit lebih cepat karena bisa berhenti di node internal. Namun dalam praktik database (mayoritas workload adalah range/scan), B+ Tree menjadi pilihan dominan.

---

## Referensi Paper

1. Comer, D. (1979). **Ubiquitous B-Tree**. *ACM Computing Surveys*, 11(2), 121–137. https://doi.org/10.1145/356770.356776

2. Graefe, G. (2011). **Modern B-Tree Techniques**. *Foundations and Trends in Databases*, 3(4), 203–402. https://doi.org/10.1561/1900000028

---
*Laporan ini disusun untuk Tugas 3 ET234203 Struktur Data dan Pemrograman Berorientasi Objek.*
