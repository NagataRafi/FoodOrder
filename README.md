# FoodOrder — Aplikasi Pemesanan Makanan Android

FoodOrder adalah aplikasi mobile Android native berbasis **Kotlin** dan **XML Layout** untuk melakukan pemesanan makanan secara praktis, interaktif, dan modern. Aplikasi ini mencakup seluruh siklus pemesanan mulai dari autentikasi login dummy, eksplorasi katalog makanan nusantara, penyesuaian porsi pada halaman detail, manajemen keranjang belanja, ringkasan checkout dengan simulasi pembayaran, hingga pencatatan riwayat pesanan.

Aplikasi juga mendemonstrasikan pemanfaatan **Explicit Intent** untuk navigasi internal dan **Implicit Intent** untuk integrasi ke aplikasi browser dan Google Maps.

---

## 🌟 Fitur Utama

- **Halaman Login Dummy**:
  - Validasi input nama/email dan kata sandi wajib diisi.
  - Fitur show/hide password toggle.
  - Penyimpanan sesi pengguna aktif dalam memori aplikasi (`SessionManager`).
- **Katalog 10 Makanan Nusantara**:
  - Tampilan grid responsif dengan MaterialCardView.
  - Kolom pencarian makanan real-time berdasarkan nama dan deskripsi.
  - Filter kategori (*Semua*, *Makanan utama*, *Minuman*).
  - Indikator badge jumlah item keranjang dinamis.
- **Detail Makanan**:
  - Foto makanan beresolusi tinggi (Vector Drawable lokal tanpa dependensi jaringan).
  - Informasi nama, kategori, harga satuan, dan deskripsi hidangan.
  - Kontrol jumlah porsi (+ dan -) dengan batas minimum 1 porsi.
  - Kalkulasi subtotal otomatis sesuai jumlah yang dipilih.
  - Tombol *Tambah ke Keranjang* (otomatis memperbarui kuantitas jika item sudah ada di keranjang, tanpa entri duplikat).
- **Keranjang Belanja Interaktif**:
  - Daftar makanan yang telah dipilih beserta thumbnail, nama, dan harga satuan.
  - Penambahan, pengurangan, atau penghapusan item langsung dari keranjang.
  - Penghitungan ulang total belanja dan jumlah item secara real-time.
  - Tampilan kondisi kosong (*Empty State*) yang informatif saat keranjang belum terisi.
- **Checkout & Simulasi Pembayaran**:
  - Ringkasan daftar pesanan dan rincian biaya.
  - Pilihan metode pembayaran dummy: **Tunai (Cash)**, **Transfer Bank**, dan **E-Wallet**.
  - Validasi pemilihan metode pembayaran sebelum konfirmasi.
  - Pencegahan pemesanan ganda (*multi-click protection*).
  - Dialog konfirmasi pesanan dengan nomor pesanan unik (`ORD-XXXXXX`), waktu pemesanan, dan status **"Berhasil (Simulasi)"**.
  - Pengosongan keranjang otomatis setelah pesanan berhasil dibuat.
- **Riwayat Pesanan (In-Memory)**:
  - Menyimpan pesanan yang telah dikonfirmasi selama siklus hidup aplikasi berjalan.
  - Urutan pesanan terbaru ditampilkan paling atas.
  - Menampilkan nomor pesanan, waktu, rincian makanan, metode pembayaran, total bayar, dan status transaksi.
- **Integrasi Implicit Intent**:
  - **Cari Restoran Online**: Membuka pencarian restoran di web browser melalui `Intent.ACTION_VIEW`.
  - **Lihat Restoran di Peta**: Membuka Google Maps menggunakan URI `geo:0,0?q=restoran` dengan penanganan fallback otomatis ke Google Maps web browser jika aplikasi peta eksternal tidak tersedia.

---

## 📋 Daftar Menu Makanan

| ID | Nama Makanan | Kategori | Harga Satuan |
|:---:|---|:---:|---:|
| **F001** | Nasi Goreng Spesial | Makanan utama | Rp25.000 |
| **F002** | Mie Goreng Jawa | Makanan utama | Rp22.000 |
| **F003** | Ayam Geprek | Makanan utama | Rp20.000 |
| **F004** | Sate Ayam | Makanan utama | Rp28.000 |
| **F005** | Bakso Sapi | Makanan utama | Rp18.000 |
| **F006** | Soto Ayam | Makanan utama | Rp20.000 |
| **F007** | Rendang Sapi | Makanan utama | Rp32.000 |
| **F008** | Gado-Gado | Makanan utama | Rp17.000 |
| **F009** | Es Teh Manis | Minuman | Rp5.000 |
| **F010** | Jus Alpukat | Minuman | Rp12.000 |

---

## 🛠️ Teknologi & Spesifikasi

- **Bahasa Pemrograman**: Kotlin
- **UI Framework**: Native Android XML Layout & Material Design 3
- **View Binding**: Diaktifkan untuk akses view yang type-safe
- **Arsitektur**: Berbasis Activity dengan pemisahan Model, Data Manager, Adapter, dan Util
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14+ (API 37)
- **Format Mata Uang**: Format Rupiah Indonesia (`RpXX.XXX`)
- **Penyimpanan State**: *In-Memory* singleton managers (`FoodRepository`, `CartManager`, `OrderManager`, `SessionManager`)

---

## 📁 Struktur Direktori

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/foodorder/
│   ├── LoginActivity.kt              # Halaman login awal
│   ├── MainActivity.kt               # Katalog utama, pencarian, filter, dan navigasi
│   ├── DetailMenuActivity.kt         # Detail makanan dan kuantitas
│   ├── CartActivity.kt               # Keranjang pesanan
│   ├── CheckoutActivity.kt           # Ringkasan checkout dan simulasi bayar
│   ├── OrderHistoryActivity.kt       # Riwayat transaksi pesanan
│   ├── model/
│   │   ├── Food.kt                   # Model entitas makanan
│   │   ├── CartItem.kt               # Model item keranjang
│   │   └── Order.kt                  # Model pesanan terkonfirmasi
│   ├── data/
│   │   ├── FoodRepository.kt         # Sumber data 10 menu lokal
│   │   ├── CartManager.kt            # Pengelola keranjang (in-memory)
│   │   ├── OrderManager.kt           # Pengelola riwayat pesanan (in-memory)
│   │   └── SessionManager.kt         # Pengelola sesi pengguna
│   ├── adapter/
│   │   ├── FoodAdapter.kt            # Adapter RecyclerView katalog
│   │   ├── CartAdapter.kt            # Adapter RecyclerView keranjang
│   │   └── OrderHistoryAdapter.kt    # Adapter RecyclerView riwayat pesanan
│   └── util/
│       └── CurrencyUtils.kt          # Utilitas pemformatan mata uang Rupiah
└── res/
    ├── drawable/                     # Vektor grafis makanan, logo, ikon, dan background
    ├── layout/                       # XML layout untuk setiap Activity, item, dan dialog
    ├── menu/                         # Menu bottom navigation
    └── values/                       # colors.xml, strings.xml, themes.xml, dimens.xml
```

---

## 🚀 Cara Menjalankan Proyek

### Prasyarat
1. **Android Studio** (Koala / Ladybug / Jellyfish atau versi yang lebih baru).
2. **JDK 17 atau JDK 21+** (atau bundled JBR dari Android Studio).
3. **Android SDK** dengan platform API 24 ke atas terpasang.

### Langkah-langkah
1. **Clone repositori**:
   ```bash
   git clone https://github.com/NagataRafi/FoodOrder.git
   cd FoodOrder
   ```
2. **Buka di Android Studio**:
   - Pilih **File > Open**, lalu arahkan ke folder `FoodOrder`.
   - Biarkan Gradle melakukan sinkronisasi otomatis (*Sync Project with Gradle Files*).
3. **Build APK Debug**:
   ```bash
   ./gradlew assembleDebug
   ```
4. **Jalankan Unit Test**:
   ```bash
   ./gradlew testDebugUnitTest
   ```
5. **Jalankan Aplikasi**:
   - Pilih emulator Android atau sambungkan perangkat Android fisik dengan mode *USB Debugging*.
   - Tekan tombol **Run 'app'** (`Shift + F10`) di toolbar Android Studio.

---

## 🧪 Pengujian Unit Test

Proyek ini dilengkapi dengan unit test bisnis pada `FoodOrderBusinessLogicTest.kt` yang menguji:
- Validitas dan kelengkapan 10 data menu di `FoodRepository`.
- Pencegahan duplikasi item saat menambahkan makanan yang sama ke keranjang.
- Perhitungan subtotal per item dan total keseluruhan pesanan.
- Logika penambahan, pengurangan, dan penghapusan item di keranjang.
- Penyimpanan snapshot item pesanan pada riwayat transaksi.
- Akurasi pemformatan mata uang Rupiah Indonesia.

---

## 📄 Lisensi

Proyek ini dibuat untuk keperluan demonstrasi dan tugas akademik pengembangan aplikasi mobile Android native. Bebas dikembangkan dan dimodifikasi untuk tujuan pembelajaran.
