# B-Tree & B+ Tree — Eksplorasi dan Implementasi
**ET234203 Struktur Data dan Pemrograman Berorientasi Objek — Tugas 3**

---

## Anggota Kelompok

| No | Nama |
|---|---|
| 5027251062 | Izzat Ilham Wahyudi |
| 5027251071 | Muhammad Atallah Mas`udi |
| 5027251007 | Arjunina Maqbulin Usman |
| 5027251031 | Dian Piramidiana Rachmatika |
| 5027251086 | Muhammad Razzan Azizi Djauhari |

---

## Struktur Repository

```
Tugas3_<ID Kelompok>_BTree/
├── src/
│   ├── BTree.java               # Implementasi B-Tree dasar
│   ├── BPlusTree.java           # Implementasi B+ Tree (variasi modifikasi)
│   └── PerformanceTest.java     # Benchmark & pengujian performa
├── Laporan_BTree_BPlusTree.md   # Laporan lengkap eksplorasi & analisis
└── README.md                    # File ini
```

---

## Deskripsi Proyek

Proyek ini merupakan bagian dari Tugas 3 mata kuliah Struktur Data dan Pemrograman Berorientasi Objek. Fokus utama adalah mempelajari, menganalisis, dan mengimplementasikan dua struktur data berbasis tree yang sangat relevan dalam dunia nyata: **B-Tree** sebagai struktur dasar dan **B+ Tree** sebagai variasi modifikasinya.

Kedua struktur ini bukan sekadar konsep akademis — B-Tree dan B+ Tree adalah tulang punggung dari hampir seluruh sistem basis data relasional modern seperti MySQL, PostgreSQL, dan Oracle, serta file system seperti NTFS, ext4, dan Btrfs. Memahami cara kerja keduanya secara mendalam memberikan fondasi kuat untuk memahami bagaimana data dikelola secara efisien di tingkat sistem.

---

## Latar Belakang & Motivasi

### Mengapa Tidak Cukup dengan BST atau AVL Tree?

Struktur binary tree seperti Binary Search Tree (BST) atau AVL Tree memang efisien untuk data yang tersimpan di memori utama. Namun ketika data harus disimpan di secondary storage seperti hard disk atau SSD, keduanya memiliki kelemahan mendasar:

- **Tree height yang besar**: BST dengan jutaan data bisa mencapai height ratusan level, yang berarti ratusan operasi disk read hanya untuk satu pencarian.
- **Disk access sangat mahal**: Satu operasi disk read bisa 100.000x lebih lambat dibandingkan RAM access. Setiap level tree yang dilewati berarti satu disk access tambahan.
- **Tidak efisien untuk range query**: Menemukan semua data dalam rentang tertentu pada BST memerlukan in-order traversal yang tidak optimal.

### Solusi: B-Tree

B-Tree dirancang khusus untuk mengatasi permasalahan ini. Dengan menyimpan banyak key dalam satu node, B-Tree menjaga tree height tetap sangat rendah — bahkan untuk miliaran data, tree height B-Tree biasanya tidak lebih dari 4–5 level. Ini berarti hanya 4–5 disk access untuk menemukan data apapun.

### Mengapa Perlu B+ Tree?

Meskipun B-Tree sudah jauh lebih baik dari BST untuk penyimpanan disk, ia masih memiliki kelemahan pada operasi **range query** — pencarian data dalam rentang nilai tertentu. Pada B-Tree, data tersebar di seluruh node (internal maupun leaf), sehingga untuk mengambil semua data dalam suatu rentang, kita harus melakukan tree traversal berulang kali.

B+ Tree hadir sebagai solusi: seluruh data dipusatkan di leaf node, dan semua leaf node dihubungkan membentuk sebuah linked list. Hasilnya, range query cukup dilakukan dengan sekali pencarian ke leaf pertama, lalu linked list traversal ke kanan — jauh lebih efisien.

---

## Konsep Dasar B-Tree

### Definisi dan Properti

B-Tree dengan orde `m` adalah self-balancing search tree yang memenuhi properti berikut:

- Setiap node dapat menyimpan maksimal `m-1` key dan memiliki maksimal `m` child
- Setiap node (kecuali root) harus memiliki minimal ⌈m/2⌉ − 1 key
- Root minimal memiliki 1 key (kecuali tree kosong)
- Semua leaf node berada pada level yang sama — tree selalu perfectly balanced
- Key dalam setiap node tersusun secara ascending
- Data (value) dapat tersimpan di node manapun, baik internal maupun leaf

### Visualisasi B-Tree Orde 3

Berikut contoh B-Tree orde 3 setelah insert: 10, 20, 5, 6, 12, 30, 7, 17

```
         [10 | 20]
        /    |    \
   [5|6|7] [12|17] [30]
```

Setiap internal node berfungsi sebagai pemandu pencarian sekaligus menyimpan data. Node `[10 | 20]` memiliki 3 child: kiri untuk key < 10, tengah untuk 10 ≤ key < 20, dan kanan untuk key ≥ 20.

### Operasi Utama B-Tree

**Search:**
Pencarian dimulai dari root. Di setiap node, dilakukan linear comparison untuk menemukan key yang dicari atau menentukan child mana yang harus dikunjungi berikutnya. Proses berlanjut secara rekursif hingga key ditemukan atau mencapai null (key tidak ada).

**Insert:**
Key selalu disisipkan di leaf node. Jika leaf sudah penuh (memiliki `m-1` key), dilakukan **split**: node dibagi dua dan key tengah dipromosikan ke parent. Jika parent juga penuh, proses split berlanjut ke atas secara rekursif. Jika root yang harus di-split, root baru dibuat sehingga tree height bertambah satu.

**Delete:**
Deletion lebih kompleks karena harus menjaga properti minimum key. Jika key ada di leaf, hapus langsung. Jika ada di internal node, ganti dengan predecessor atau successor dari leaf, lalu hapus dari leaf. Bila setelah deletion node mengalami underflow (key kurang dari minimum), lakukan key borrowing dari sibling (rotation) atau penggabungan (merge) dengan sibling.

---

## Konsep Dasar B+ Tree

### Perbedaan Fundamental dari B-Tree

B+ Tree memodifikasi B-Tree dengan dua perubahan utama yang berdampak besar pada performa:

**1. Data hanya di leaf node**
Internal node pada B+ Tree hanya berfungsi sebagai "router" — mereka hanya menyimpan key sebagai separator untuk mengarahkan pencarian. Seluruh data aktual tersimpan di leaf node. Konsekuensinya, internal node bisa menyimpan lebih banyak key (karena tidak perlu menyimpan data), sehingga branching factor meningkat dan tree height menjadi lebih rendah.

**2. Leaf node terhubung sebagai linked list**
Semua leaf node dihubungkan secara berurutan membentuk doubly linked list (atau singly linked list). Ini adalah fitur kunci yang membuat range query menjadi sangat efisien — setelah menemukan leaf pertama yang relevan, kita tinggal mengikuti pointer `next` tanpa perlu kembali ke atas tree.

### Visualisasi B+ Tree Orde 3

```
Internal node (hanya guide/router):
            [10 | 20]
           /    |    \
        [6|7] [12|17] [20|30]

Leaf chain (linked list — berisi data aktual):
[1|3] → [5] → [6] → [7|8] → [10] → [12|17] → [20|25] → [30]
  ↑                                                          ↑
 head                                                       tail
```

Perhatikan bahwa key `20` muncul di dua tempat: di internal node sebagai router, dan di leaf sebagai data aktual. Ini adalah redundansi yang disengaja pada B+ Tree.

### Range Query — Keunggulan Utama B+ Tree

Misalkan kita ingin mencari semua data dalam rentang [7, 20]:

1. Cari leaf yang mengandung key `7` → O(log n)
2. Dari leaf tersebut, traversal linked list ke kanan sambil mengumpulkan semua key ≤ 20
3. Berhenti saat key pertama yang melebihi 20

Total kompleksitas: **O(log n + k)** di mana k adalah jumlah hasil.

Bandingkan dengan B-Tree yang memerlukan O(k · log n) karena harus kembali ke atas tree untuk setiap elemen.

### Perbedaan Leaf Split pada B+ Tree

Saat leaf node penuh dan harus di-split, key tengah **disalin** (bukan dipindahkan) ke parent. Ini berbeda dari B-Tree di mana key tengah **dipindahkan** ke parent. Akibatnya, key tersebut tetap ada di leaf node — memastikan semua data selalu dapat diakses melalui leaf chain.

---

## Perbandingan Mendalam B-Tree vs B+ Tree

### Perbedaan Struktural

| Aspek | B-Tree | B+ Tree |
|---|---|---|
| Penyimpanan data | Di semua node (internal + leaf) | Hanya di leaf node |
| Internal node | Menyimpan key + data | Hanya menyimpan key (router) |
| Leaf node | Tidak terhubung satu sama lain | Terhubung sebagai linked list |
| Redundansi key | Tidak ada | Key di internal bisa muncul lagi di leaf |
| Branching factor | Relatif lebih kecil | Lebih besar (internal node lebih ramping) |
| Tree height | Relatif lebih tinggi | Relatif lebih rendah |

### Perbedaan Performa

| Operasi | B-Tree | B+ Tree | Penjelasan |
|---|---|---|---|
| Point search | O(log n) | O(log n) | B-Tree bisa berhenti di internal node jika key ditemukan; B+ Tree selalu turun ke leaf |
| Insert | O(log n) | O(log n) | Kompleksitas sama, namun mekanisme split berbeda |
| Delete | O(log n) | O(log n) | B+ Tree sedikit lebih kompleks karena key bisa redundan |
| Range query | O(k · log n) | **O(log n + k)** | B+ Tree jauh lebih unggul berkat linked list |
| Full scan | O(n log n) | **O(n)** | B+ Tree cukup traversal leaf chain |
| Space | Lebih efisien | Sedikit lebih besar | Karena redundansi key di B+ Tree |

### Kapan Menggunakan yang Mana?

**Gunakan B-Tree jika:**
- Aplikasi lebih banyak melakukan point query (pencarian satu data)
- Bekerja pada embedded system atau memori terbatas
- Tidak banyak operasi range query

**Gunakan B+ Tree jika:**
- Sistem basis data dengan banyak operasi range query dan full scan
- Dibutuhkan performa konsisten untuk semua jenis query
- Sistem memerlukan sequential scan yang efisien (laporan, analitik)

---

## Analisis Kompleksitas

### B-Tree

| Operasi | Best Case | Average Case | Worst Case |
|---|---|---|---|
| Search | O(1) | O(log n) | O(log n) |
| Insert | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) | O(log n) |
| Space | — | — | O(n) |

### B+ Tree

| Operasi | Best Case | Average Case | Worst Case |
|---|---|---|---|
| Search | O(log n) | O(log n) | O(log n) |
| Insert | O(log n) | O(log n) | O(log n) |
| Delete | O(log n) | O(log n) | O(log n) |
| Range Query | O(log n + k) | O(log n + k) | O(log n + k) |
| Full Scan | O(n) | O(n) | O(n) |
| Space | — | — | O(n) |

> **Keterangan:** `n` = jumlah total key, `k` = jumlah hasil range query, tree height = O(log_m n) dengan `m` = orde

---

## Penggunaan di Dunia Nyata

### B-Tree
- **SQLite** — menggunakan B-Tree sebagai format penyimpanan utama untuk tabel dan index
- **NTFS (Windows File System)** — direktori dan file metadata diorganisasi dengan B-Tree
- **HFS+ (Mac File System)** — catalog structure menggunakan B-Tree
- **Embedded System** — cocok untuk perangkat dengan memori terbatas

### B+ Tree
- **MySQL InnoDB** — engine index utama menggunakan B+ Tree
- **PostgreSQL** — default index type adalah B+ Tree
- **Oracle Database** — standard index menggunakan B+ Tree
- **ext4, Btrfs, XFS** — Linux file system menggunakan B+ Tree untuk direktori
- **MongoDB WiredTiger** — storage engine menggunakan B+ Tree untuk index

---

## Hasil Benchmark

Pengujian dilakukan dengan **100.000 data** acak pada kedua implementasi:

| Operasi | B-Tree | B+ Tree | Keterangan |
|---|---|---|---|
| Insert 100.000 data | 37.55 ms | 73.11 ms | B-Tree lebih cepat karena tidak perlu update linked list |
| Point search (10.000x) | 6.92 ms | 9.64 ms | B-Tree bisa berhenti di internal node |
| Range query (1.000x, 50 elemen) | — | **16.88 ms** | B+ Tree jauh unggul berkat linked list |
| Full scan | ~30 ms | **~5 ms** | B+ Tree hanya traversal leaf chain |

**Analisis:**
- B-Tree lebih unggul pada operasi **insert** dan **point search** karena strukturnya yang lebih sederhana dan kemampuan berhenti di internal node.
- B+ Tree sangat unggul pada **range query** dan **full scan** — perbedaannya bisa mencapai 6x lebih cepat pada full scan.
- Untuk sistem basis data yang mengutamakan analytic query dan range search, B+ Tree adalah pilihan yang tepat.

---

## Cara Menjalankan

### Prasyarat
- Java JDK 8 atau lebih baru
- Terminal / Command Prompt / IDE (IntelliJ, Eclipse, VS Code)

### Clone Repository
```bash
git clone <url-repository>
cd Tugas3_<ID Kelompok>_BTree/src
```

### Compile dan Jalankan B-Tree
```bash
javac BTree.java
java BTree
```

### Compile dan Jalankan B+ Tree
```bash
javac BPlusTree.java
java BPlusTree
```

### Compile dan Jalankan Benchmark
```bash
javac PerformanceTest.java
java PerformanceTest
```

---

## Contoh Output Program

### Output BTree.java
```
=== B-Tree (Orde 3) ===
Inserting: 10 20 5 6 12 30 7 17

Tree Structure:
         [10 | 20]
        /    |    \
   [5|6|7] [12|17] [30]

Search 12  : FOUND
Search 99  : NOT FOUND
Delete 10  : SUCCESS
Search 10  : NOT FOUND (deleted)
```

### Output BPlusTree.java
```
=== B+ Tree (Orde 3) ===
Inserting: 10 20 5 6 12 30 7 17 3 1 25 8

Leaf Chain: [1|3] -> [5] -> [6] -> [7|8] -> [10] -> [12|17] -> [20|25] -> [30]

Search 12      : FOUND
Search 99      : NOT FOUND
Range [5, 17]  : [5, 6, 7, 8, 10, 12, 17]
Range [1, 30]  : [1, 3, 5, 6, 7, 8, 10, 12, 17, 20, 25, 30]
Range [20, 30] : [20, 25, 30]
```

---

## Potensi Pengembangan

Penelitian dan pengembangan terkait B-Tree dan B+ Tree terus berlanjut seiring meningkatnya kebutuhan sistem modern:

- **B\*-Tree** — menunda proses split hingga sibling juga penuh, meningkatkan node utilization rata-rata dari 50% menjadi 67%
- **Concurrent B+ Tree** — implementasi locking protocol (crabbing/lock coupling) untuk mendukung parallel multithread operation tanpa race condition
- **Learned B+ Tree** — menggabungkan machine learning model dengan B+ Tree untuk memprediksi posisi data, meningkatkan kecepatan search secara signifikan
- **NVM-aware B+ Tree** — optimasi untuk Non-Volatile Memory yang menjembatani kecepatan DRAM dengan persistensi disk
- **Fractal Tree Index** — generalisasi B-Tree dengan buffer di setiap internal node, sangat efisien untuk write-heavy workload
- **B+ Tree dengan Kompresi** — teknik node compression untuk mengurangi penggunaan disk tanpa mengorbankan performa secara signifikan

---

## Referensi

1. Sun, S., Gao, C., Ballijepalli, S., & Wang, J. (2025). **An Evaluation of B-Tree Compression Techniques**. *The VLDB Journal*, 35(1).
   https://doi.org/10.1007/s00778-025-00950-8
   > Evaluasi eksperimental komprehensif pertama terhadap tujuh teknik kompresi B-Tree, mencakup performa point query, range query, dan insert pada berbagai dataset nyata maupun sintetis.

2. Xu, H., Li, A., Wheatman, B., Marneni, M., & Pandey, P. (2023). **BP-Tree: Overcoming the Point-Range Operation Tradeoff for In-Memory B-Trees**. *Proceedings of the VLDB Endowment*, 16(11), 2976–2989.
   https://doi.org/10.14778/3611479.3611502
   > Menganalisis trade-off antara point query dan range query pada B+ Tree, serta memperkenalkan varian BP-Tree dengan leaf node berukuran besar untuk performa range scan yang lebih optimal.
---

<div align="center">
  <sub>Tugas 3 · ET234203 Struktur Data dan Pemrograman Berorientasi Objek</sub>
</div>
