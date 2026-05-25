# 🌳 Tugas 3 — Eksplorasi dan Implementasi Tree
**ET234203 Struktur Data dan Pemrograman Berorientasi Objek**

## Jenis Tree
- **Tree Dasar:** B-Tree
- **Variasi Modifikasi:** B+ Tree

## Struktur Repo
| File | Deskripsi |
|---|---|
| `BTree.java` | Implementasi B-Tree Orde 3 (dasar) |
| `BPlusTree.java` | Implementasi B+ Tree Orde 3 (modifikasi) |
| `Laporan_BTree_BPlusTree.md` | Laporan lengkap eksplorasi & analisis |

## Cara Menjalankan
```bash
javac BTree.java && java BTree
javac BPlusTree.java && java BPlusTree
```

## Fitur Utama B+ Tree
- ✅ Insert, Search, Display
- ✅ **Range Query** via linked list leaf node
- ✅ Performance test 100.000 data
