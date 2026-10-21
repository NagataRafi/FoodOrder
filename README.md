# FoodOrder — Aplikasi Pemesanan Makanan Android

FoodOrder adalah aplikasi mobile Android native berbasis **Kotlin** dan **XML Layout** untuk melakukan pemesanan makanan secara praktis, interaktif, dan modern. Aplikasi ini mencakup seluruh siklus pemesanan mulai dari autentikasi login pengguna dan sistem administrasi (Admin Dashboard), registrasi akun baru, eksplorasi katalog makanan nusantara dinamis, penyesuaian porsi pada halaman detail, manajemen keranjang belanja, ringkasan checkout dengan simulasi pembayaran (redesain dialog Editorial Food UI), hingga pencatatan riwayat pesanan.

Aplikasi juga mendemonstrasikan pemanfaatan **Explicit Intent** untuk navigasi internal dan **Implicit Intent** untuk integrasi ke aplikasi browser dan Google Maps.

---

## 🌟 Fitur Utama

- **Autentikasi Pengguna & Admin (In-Memory)**:
  - Validasi input nama/email dan kata sandi wajib diisi.
  - Fitur show/hide password toggle.
  - Deteksi peran (*Role-Based Access Control*):
    - **User**: Masuk ke katalog utama `MainActivity`.
    - **Admin**: Masuk ke panel pengelola `AdminDashboardActivity`.
  - Akun admin divalidasi terpisah dari akun user biasa dan tidak dapat dibuat melalui registrasi.
  - Sesi aktif disimpan dan dikelola melalui `SessionManager` dengan enum role `UserRole` (`USER`, `ADMIN`).
- **Registrasi Akun User Baru**:
  - Formulir pendaftaran: Nama lengkap, email, kata sandi, dan konfirmasi kata sandi.
  - Validasi lengkap: format email, kecocokan konfirmasi sandi, proteksi email duplikat, dan pencegahan pembuatan akun admin melalui registrasi.
  - Akun tersimpan dalam memori runtime (`UserManager`) dan langsung dapat digunakan untuk login.
- **Admin Dashboard & Manajemen Katalog Menu**:
  - Tampilan dashboard modern dengan ringkasan statistik: total menu, jumlah makanan utama, dan jumlah minuman yang dihitung secara dinamis.
  - Daftar seluruh katalog menu yang tersedia dengan thumbnail, ID, kategori, dan harga.
  - Tombol **+ Tambah Menu Baru** (`AddMenuActivity`).
  - Tombol **Logout** yang membersihkan sesi dan mengarahkan kembali ke halaman login.
  - Proteksi akses role: Pengguna tanpa peran `ADMIN` ditolak saat mengakses halaman admin.
- **Formulir Tambah Menu oleh Admin (`AddMenuActivity`)**:
  - Input nama menu, pemilihan kategori terstruktur (Dropdown: *Makanan utama* / *Minuman*), harga satuan (integer positif Rupiah), dan deskripsi hidangan.
  - Validasi ketat terhadap input kosong, harga negatif/nol, dan pencegahan multi-click (*double submit*).
  - Penomoran ID menu baru otomatis dan unik (`F011`, `F012`, dst.) tanpa mengganggu sepuluh menu awal (`F001`–`F010`).
  - Menu baru langsung tampil di katalog pengguna dan admin, dapat dicari, difilter berdasarkan kategori, dibuka di detail menu, serta dipesan ke dalam keranjang belanja.
- **Redesain Dialog Bergaya Editorial Food UI**:
  - **Dialog Sukses Pembayaran**: Kartu tanda terima (*receipt*) bertema hangat dengan aksen terakota (`#C85A32`) dan hijau zaitun (`#4A6B3A`), ikon check khusus, nomor pesanan unik, rincian metode pembayaran, waktu transaksi, total pembayaran, status *"Berhasil (Simulasi)"*, catatan simulasi, serta tombol aksi *"Lihat Riwayat Pesanan"* dan *"Kembali ke Katalog"*.
  - **Dialog Validasi Pembayaran**: Tampilan peringatan bertema editorial saat metode pembayaran belum dipilih, mencegah pembuatan pesanan ganda tanpa mengosongkan keranjang.
  - **Dialog Sukses Tambah Menu**: Dialog konfirmasi berdesain seragam setelah admin berhasil menambahkan menu ke katalog.
- **Katalog Makanan Nusantara (Dinamis)**:
  - Tampilan grid responsif dengan MaterialCardView.
  - Kolom pencarian real-time berdasarkan nama dan deskripsi makanan.
  - Filter kategori (*Semua*, *Makanan utama*, *Minuman*).
  - Indikator badge jumlah item keranjang dinamis.
  - Pembaruan otomatis saat kembali dari penambahan menu admin.
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
  - Pilihan metode pembayaran: **Tunai (Cash)**, **Transfer Bank**, dan **E-Wallet**.
  - Validasi pemilihan metode pembayaran dengan dialog peringatan visual.
  - Pencegahan pemesanan ganda (*multi-click protection*).
  - Pengosongan keranjang otomatis setelah pesanan berhasil dibuat.
- **Riwayat Pesanan (In-Memory)**:
  - Menyimpan pesanan yang telah dikonfirmasi selama siklus hidup aplikasi berjalan.
  - Urutan pesanan terbaru ditampilkan paling atas.
  - Menampilkan nomor pesanan, waktu, rincian makanan, metode pembayaran, total bayar, dan status transaksi.
- **Integrasi Implicit Intent**:
  - **Cari Restoran Online**: Membuka pencarian restoran di web browser melalui `Intent.ACTION_VIEW`.
  - **Lihat Restoran di Peta**: Membuka Google Maps menggunakan URI `geo:0,0?q=restoran` dengan penanganan fallback otomatis ke Google Maps web browser jika aplikasi peta eksternal tidak tersedia.

---

## 🔑 Kredensial Akun Demonstrasi

Aplikasi ini menggunakan penyimpanan autentikasi *in-memory* lokal untuk keperluan evaluasi dan demonstrasi:

| Peran (Role) | Email / Username | Kata Sandi | Halaman Tujuan |
|:---|:---|:---|:---|
| **ADMIN** | `admin@foodorder.local` | `Admin123!` | `AdminDashboardActivity` |
| **USER (Default Demo)** | `budi@example.com` | `password123` | `MainActivity` |
| **USER (Alternatif Demo)** | `user@foodorder.local` | `User123!` | `MainActivity` |

> [!NOTE]
> Pengguna baru dapat mendaftarkan akun secara mandiri melalui tombol **Daftar Akun Baru** pada halaman login. Akun yang didaftarkan berstatus `USER` dan tersimpan di dalam memori aplikasi selama sesi berjalan.

---

## 📋 Daftar Menu Makanan Awal

Katalog awal menyediakan 10 menu tradisional nusantara. Menu baru yang ditambahkan oleh admin akan diteruskan mulai dari `F011`:

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
- **Konsep Visual Dialog**: Editorial Food UI (Aksen Terakota `#C85A32`, Hijau Zaitun `#4A6B3A`, Latar Hangat `#FAF7F2`)
- **View Binding**: Diaktifkan untuk akses view yang type-safe
- **Arsitektur**: Berbasis Activity dengan pemisahan Model, Data Manager, Adapter, dan Util
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14+ (API 37)
- **Format Mata Uang**: Format Rupiah Indonesia (`RpXX.XXX`)
- **Penyimpanan State**: *In-Memory* singleton managers (`FoodRepository`, `UserManager`, `CartManager`, `OrderManager`, `SessionManager`)

---

## 📁 Struktur Direktori

```text
app/src/main/
├── AndroidManifest.xml
├── java/com/example/foodorder/
│   ├── LoginActivity.kt              # Halaman login (User & Admin)
│   ├── RegisterActivity.kt           # Halaman registrasi akun user baru
│   ├── AdminDashboardActivity.kt     # Dashboard statistik & katalog menu admin
│   ├── AddMenuActivity.kt            # Formulir tambah menu baru oleh admin
│   ├── MainActivity.kt               # Katalog utama, pencarian, filter, dan navigasi
│   ├── DetailMenuActivity.kt         # Detail makanan dan kuantitas
│   ├── CartActivity.kt               # Keranjang pesanan
│   ├── CheckoutActivity.kt           # Ringkasan checkout, dialog validasi & konfirmasi bayar
│   ├── OrderHistoryActivity.kt       # Riwayat transaksi pesanan
│   ├── model/
│   │   ├── Food.kt                   # Model entitas makanan
│   │   ├── CartItem.kt               # Model item keranjang
│   │   ├── Order.kt                  # Model pesanan terkonfirmasi
│   │   └── User.kt                   # Model pengguna & enum UserRole (USER, ADMIN)
│   ├── data/
│   │   ├── FoodRepository.kt         # Sumber data katalog menu & penambahan dinamis
│   │   ├── UserManager.kt            # Pengelola autentikasi & registrasi (in-memory)
│   │   ├── CartManager.kt            # Pengelola keranjang (in-memory)
│   │   ├── OrderManager.kt           # Pengelola riwayat pesanan (in-memory)
│   │   └── SessionManager.kt         # Pengelola sesi & otorisasi role pengguna
│   ├── adapter/
│   │   ├── FoodAdapter.kt            # Adapter RecyclerView katalog pengguna
│   │   ├── AdminFoodAdapter.kt       # Adapter RecyclerView katalog admin
│   │   ├── CartAdapter.kt            # Adapter RecyclerView keranjang
│   │   └── OrderHistoryAdapter.kt    # Adapter RecyclerView riwayat pesanan
│   └── util/
│       └── CurrencyUtils.kt          # Utilitas pemformatan mata uang Rupiah
└── res/
    ├── drawable/                     # Vektor grafis makanan, logo, ikon editorial, dan background
    ├── layout/                       # XML layout untuk setiap Activity, item, dan dialog editorial
    ├── menu/                         # Menu bottom navigation
    └── values/                       # colors.xml, strings.xml, themes.xml, dimens.xml
```

---

## 🚀 Cara Menjalankan Proyek

### Prasyarat
1. **Android Studio** (Koala / Ladybug / Meerkat atau versi yang lebih baru).
2. **JDK 17 atau JDK 21+** (menggunakan bundled JBR dari Android Studio).
3. **Android SDK** dengan platform API 24 ke atas terpasang.

### Langkah-langkah
1. **Buka di Android Studio**:
   - Pilih **File > Open**, lalu arahkan ke direktori proyek `FoodOrder`.
   - Biarkan Gradle melakukan sinkronisasi otomatis (*Sync Project with Gradle Files*).
2. **Build APK Debug**:
   ```bash
   .\gradlew.bat assembleDebug
   ```
3. **Jalankan Unit Test**:
   ```bash
   .\gradlew.bat testDebugUnitTest
   ```
4. **Jalankan Aplikasi**:
   - Pilih emulator Android atau sambungkan perangkat Android fisik dengan mode *USB Debugging*.
   - Tekan tombol **Run 'app'** (`Shift + F10`) di toolbar Android Studio.

---

## 🧪 Pengujian Unit Test

Proyek ini dilengkapi dengan unit test bisnis komprehensif pada `FoodOrderBusinessLogicTest.kt` yang menguji:
- Kelengkapan 10 menu awal dan integritas datanya.
- Penambahan menu baru oleh admin: penugasan ID berurutan (`F011`, `F012`), validasi kategori (*Makanan utama*, *Minuman*), serta penolakan harga ≤ 0 dan input kosong.
- Pencarian dan filter kategori katalog yang mencakup menu baru.
- Integrasi menu baru ke keranjang belanja dan akurasi kalkulasi checkout.
- Autentikasi user dan admin (kredensial benar vs. salah).
- Registrasi user baru, pencegahan registrasi akun ber-email admin, serta penolakan email duplikat.
- Pengelolaan status sesi dan pengecekan role (`SessionManager`).
- Pencegahan duplikasi item belanja dan konsistensi snapshot pesanan riwayat transaksi.

---

## ⚠️ Batasan & Perilaku Penyimpanan Data

- **Penyimpanan Lokal In-Memory**: Seluruh data akun baru, menu tambahan, keranjang belanja, sesi login, dan riwayat pesanan disimpan secara *in-memory* pada runtime aplikasi.
- **Siklus Hidup Data**: Data baru akan tetap ada selama proses aplikasi berjalan. Apabila aplikasi dihentikan sepenuhnya (*force stop* atau restart proses), data akan kembali ke kondisi awal (10 menu default dan akun demo bawaan).
- **Simulasi Transaksi**: Pembayaran checkout bersifat simulasi lokal dan tidak memproses transfer keuangan riil.

---

## 📄 Lisensi

Proyek ini dikembangkan untuk keperluan demonstrasi dan tugas akademik pengembangan aplikasi mobile Android native.
