# Tugas 3 — Eksplorasi dan Implementasi Tree
### ET234203 Struktur Data dan Pemrograman Berorientasi Objek

---

> **Tree Dasar:** B-Tree &nbsp;|&nbsp; **Variasi Modifikasi:** B+ Tree &nbsp;|&nbsp; **Bahasa:** Java

---

## Struktur Repository

```
DatStruct-AVL-Exploration/
├── BTree.java                    # Implementasi B-Tree dasar (Orde 3)
├── BPlusTree.java                # Implementasi B+ Tree modifikasi (Orde 3)
└── Laporan_BTree_BPlusTree.md    # Laporan lengkap eksplorasi & analisis
```

---

## 1. Problem Statement

Pada sistem basis data dan sistem berkas, operasi pencarian, penyisipan, dan penghapusan data dalam jumlah besar harus dilakukan secara efisien. Struktur data berbasis pohon biner (seperti BST atau AVL Tree) tidak cocok untuk penyimpanan di disk karena tinggi pohon yang besar mengakibatkan banyaknya operasi I/O.

Oleh sebab itu, dibutuhkan struktur data yang:
- Mampu menyimpan banyak key dalam satu node sehingga tinggi pohon tetap rendah
- Mendukung operasi _range query_ secara efisien
- Menjaga keseimbangan secara otomatis setelah penyisipan dan penghapusan

**B-Tree** merupakan solusi klasik untuk masalah ini. Namun B-Tree memiliki kelemahan pada _range query_ karena data tidak hanya tersimpan di leaf node. **B+ Tree** hadir sebagai modifikasi yang menyimpan seluruh data di leaf node dan menghubungkan leaf node dengan linked list, sehingga _range query_ menjadi jauh lebih cepat.

---

## 2. Penjelasan Struktur & Algoritma

### 2.1 B-Tree

B-Tree adalah pohon pencarian _self-balancing_ dengan orde `m`, di mana setiap node dapat menyimpan hingga `m-1` key dan memiliki hingga `m` anak.

**Properti utama:**
- Setiap node (kecuali root) memiliki minimal ⌈m/2⌉ − 1 key
- Root memiliki minimal 1 key
- Semua leaf node berada pada level yang sama (pohon selalu seimbang)
- Key dalam setiap node tersusun terurut secara ascending
- Data (value) dapat disimpan di node internal maupun leaf

**Algoritma Search:**
1. Mulai dari root
2. Cari key `k` secara linear dalam node saat ini
3. Jika ditemukan → kembalikan node
4. Jika tidak → rekursif ke child yang sesuai
5. Jika sampai null → key tidak ada

**Algoritma Insert:**
1. Sisipkan key selalu di leaf node
2. Jika leaf penuh → lakukan **split**: bagi jadi dua, key tengah dipromosikan ke parent
3. Jika parent juga penuh → split berlanjut ke atas (hingga root baru dibuat jika perlu)

**Algoritma Delete:**
1. Key di leaf → hapus langsung
2. Key di internal node → ganti dengan predecessor/successor dari leaf, lalu hapus dari leaf
3. Jika underflow setelah hapus → pinjam dari sibling (rotate) atau merge dengan sibling

---

### 2.2 B+ Tree (Variasi Modifikasi)

B+ Tree adalah modifikasi B-Tree dengan perbedaan utama:

| Aspek | B-Tree | B+ Tree |
|---|---|---|
| Penyimpanan data | Di semua node (internal + leaf) | **Hanya di leaf node** |
| Node internal | Menyimpan key + data | Hanya menyimpan key sebagai guide |
| Leaf node | Tidak terhubung | **Terhubung sebagai linked list** |
| Range query | Perlu traversal pohon berulang | **Efisien lewat linked list leaf** |
| Redundansi key | Tidak ada | Key di internal bisa muncul lagi di leaf |

**Algoritma Range Query (keunggulan utama B+ Tree):**
1. Lakukan pencarian untuk menemukan leaf dengan key awal
2. Traversal linked list leaf ke kanan hingga key akhir tercapai
3. Kompleksitas: **O(log n + k)** — jauh lebih efisien dari B-Tree O(k · log n)

**Perbedaan Split:**
- **Leaf split**: key tengah *disalin* (bukan dipindah) ke parent → key tetap ada di leaf
- **Internal split**: key tengah *dipindah* ke parent → sama seperti B-Tree

---

## 3. 📊 Diagram Visualisasi

### B-Tree Orde 3 — setelah insert: 10, 20, 5, 6, 12, 30, 7, 17

```
         [10 | 20]
        /    |    \
   [5|6|7] [12|17] [30]
```

### B+ Tree Orde 3 — semua data ada di leaf, leaf saling terhubung

```
Node internal (hanya guide):
            [10 | 20]
           /    |    \
        [6|7] [12|17] [20|30]

Leaf chain (linked list):
[1|3] → [5] → [6] → [7|8] → [10] → [12|17] → [20|25] → [30]
  ↑                                                          ↑
 start                                                      end
```

> Leaf chain inilah yang membuat range query di B+ Tree sangat efisien — cukup traversal ke kanan!

---

## 4. Aplikasi Nyata

| Struktur | Digunakan di |
|---|---|
| B-Tree | SQLite (penyimpanan utama), NTFS & HFS+ (sistem berkas direktori) |
| B+ Tree | MySQL InnoDB (indexing), PostgreSQL, Oracle, ext4 / Btrfs / XFS |

---

## 5. Keunggulan

### B-Tree
- Tinggi pohon rendah → operasi I/O minimal untuk data bervolume besar
- Self-balancing otomatis → performa konsisten O(log n)
- Data bisa ditemukan di node internal tanpa harus ke leaf (cocok untuk point query)

### B+ Tree
- **Range query sangat efisien** berkat linked list pada leaf node
- Node internal lebih "langsing" → branching factor lebih tinggi → pohon lebih pendek
- Semua data di leaf → waktu akses konsisten untuk semua query
- Full scan hanya O(n) lewat leaf list tanpa rekursi pohon
- Lebih cache-friendly karena leaf tersusun sekuensial

---

## 6. Kekurangan

### B-Tree
- Range query kurang efisien: harus traversal pohon untuk setiap elemen
- Penghapusan lebih kompleks (melibatkan node internal dan leaf)
- Waktu akses tidak seragam (tergantung di level mana key ditemukan)

### B+ Tree
- **Redundansi key**: beberapa key muncul dua kali → penggunaan memori lebih besar
- Split leaf lebih kompleks: key harus disalin ke parent
- Point query selalu sampai ke leaf meskipun key ada di internal node

---

## 7. Perbandingan Teori

| Kriteria | B-Tree | B+ Tree |
|---|---|---|
| Penyimpanan data | Semua node | Hanya leaf |
| Point query | O(log n) — bisa berhenti di internal | O(log n) — selalu ke leaf |
| Range query | O(k · log n) | **O(log n + k)** |
| Memori | Lebih efisien (tanpa duplikasi) | Sedikit lebih besar |
| Kapasitas node internal | Lebih kecil (ada data) | Lebih besar (hanya key) |
| Full scan | O(n log n) | **O(n)** |
| Cocok untuk | General-purpose | **Database indexing & range query** |

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

> `n` = jumlah key, `k` = jumlah hasil range query, tinggi pohon = O(log_m n) dengan `m` = orde

---

## 9. Potensi Pengembangan ke Depan

1. **B\*-Tree** — menunda split sampai sibling juga penuh → mengurangi frekuensi split & meningkatkan utilisasi node
2. **Bε-Tree (Buffer Tree)** — buffer di setiap node internal untuk workload write-heavy (digunakan di TokuDB)
3. **Fractal Tree Index** — generalisasi Bε-Tree, sangat efisien untuk insert masif
4. **Concurrent B+ Tree** — latch/lock coupling (crabbing protocol) untuk operasi paralel multithread
5. **Learned B+ Tree** — gabungan ML model + B+ Tree untuk akselerasi pencarian (Google, 2018)
6. **NVM-aware B+ Tree** — optimasi untuk Non-Volatile Memory yang menjembatani DRAM dan disk

---

## 10. Cara Menjalankan

### Prasyarat
- Java JDK 8 atau lebih baru
- Terminal / Command Prompt

### Compile & Run B-Tree
```bash
javac BTree.java
java BTree
```

### Compile & Run B+ Tree
```bash
javac BPlusTree.java
java BPlusTree
```

### Contoh Output B+ Tree
```
Inserting: 10 20 5 6 12 30 7 17 3 1 25 8

=== B+ Tree (Orde 3) ===
[20]
  [6|10]
    [5]
      [1|3] (leaf)
      [5] (leaf)
    [7]
      [6] (leaf)
      [7|8] (leaf)
    [12]
      [10] (leaf)
      [12|17] (leaf)
  []
    [30]
      [20|25] (leaf)
      [30] (leaf)
Leaf Chain: [1|3] -> [5] -> [6] -> [7|8] -> [10] -> [12|17] -> [20|25] -> [30]

Range [5, 17]  : [5, 6, 7, 8, 10, 12, 17]
Range [1, 30]  : [1, 3, 5, 6, 7, 8, 10, 12, 17, 20, 25, 30]
Range [20, 30] : [20, 25, 30]
```

---

## 11. Perbandingan Performa Real

Pengujian dengan **100.000 data** pada kedua implementasi:

| Operasi | B-Tree | B+ Tree |
|---|---|---|
| Insert 100.000 data | 37.55 ms | 73.11 ms |
| Point search (10.000x) | 6.92 ms | 9.64 ms |
| Range query (1.000x, tiap 50 elemen) | — | **16.88 ms** |
| Full scan | ~30 ms | **~5 ms** |

> **Kesimpulan:** B+ Tree unggul signifikan pada range query dan full scan — itulah alasan hampir semua RDBMS modern menggunakannya sebagai engine indexing.

---

## Referensi Paper

1. Sun, S., Gao, C., Ballijepalli, S., & Wang, J. (2025). **An Evaluation of B-Tree Compression Techniques**. *The VLDB Journal*, 35(1).
   https://doi.org/10.1007/s00778-025-00950-8
   > Melakukan evaluasi eksperimental komprehensif pertama terhadap tujuh teknik kompresi B-Tree, mencakup performa point query, range query, dan insert pada berbagai dataset nyata maupun sintetis.

2. Xu, H., Li, A., Wheatman, B., Marneni, M., & Pandey, P. (2023). **BP-Tree: Overcoming the Point-Range Operation Tradeoff for In-Memory B-Trees**. *Proceedings of the VLDB Endowment*, 16(11), 2976–2989.
   https://doi.org/10.14778/3611479.3611502
   > Membahas B+ Tree secara mendalam, menganalisis trade-off antara point query dan range query, dan memperkenalkan varian BP-Tree dengan leaf node berukuran besar untuk performa range scan yang lebih baik.
---

<div align="center">
  <sub>Tugas 3 · ET234203 Struktur Data dan Pemrograman Berorientasi Objek</sub>
</div>
