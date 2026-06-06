# 🛍️ Batik Store — Aplikasi E-commerce Batik

Aplikasi katalog & belanja batik dan aksesoris berbasis Android. Data produk
diambil dari API menggunakan Retrofit, mendukung mode offline (cache lokal),
serta tema terang/gelap bernuansa batik coklat.

Tugas Final Lab Mobile 2026 — Tema: *E-commerce / Business*.

## ✨ Fitur
- 🔐 Login & Registrasi pengguna (disimpan lokal via Room).
- 📦 Daftar produk batik dari API (Retrofit) dengan tampilan grid.
- 🔍 Pencarian produk dan filter berdasarkan kategori.
- 🧾 Halaman detail produk lengkap dengan deskripsi.
- ❤️ Tandai produk favorit.
- 🛒 Keranjang belanja dengan pengaturan jumlah + badge jumlah item.
- 💳 Checkout dan Beli Sekarang, tersimpan ke Riwayat Pesanan.
- 👤 Halaman Profil + riwayat pesanan + pengaturan tema.
- 📴 Mode offline: data tetap tampil tanpa internet (cache lokal).
- 🔁 Tombol refresh saat gagal memuat data.
- 🌙 Dark Theme & ☀️ Light Theme.

## 🧰 Teknologi
| Kebutuhan          | Implementasi                              |
|--------------------|-------------------------------------------|
| Bahasa             | Java & XML                                |
| Activity + Intent  | Splash, Login, Register, Main, Detail, Checkout |
| RecyclerView       | Produk, keranjang, riwayat pesanan        |
| Fragment + Navigasi| Beranda, Keranjang, Favorit, Profil (Navigation Component) |
| Background Thread  | ExecutorService + Handler                 |
| Networking         | Retrofit + Gson + OkHttp                  |
| Persistensi Lokal  | Room (SQLite) + SharedPreferences         |
| Tema               | values/themes.xml & values-night/themes.xml |
| Pemuatan Gambar    | Glide                                     |

## 🚀 Cara Penggunaan
1. Clone repository ini, buka di Android Studio, lalu Sync Gradle.
2. Jalankan pada emulator/perangkat (min. Android 7.0 / API 24).
3. Daftar akun lalu login.
4. Telusuri produk, gunakan pencarian/filter kategori, buka detail.
5. Tambahkan ke keranjang atau Beli Sekarang, lalu checkout.
6. Buka tab Profil untuk melihat riwayat pesanan dan mengganti tema.

## 🏗️ Penjelasan Implementasi Teknis
- **Activity & Intent**: navigasi antar Activity (mis. produk ke detail) dan
  pengiriman data produk via Intent (Parcelable).
- **RecyclerView**: menampilkan daftar produk, isi keranjang, dan riwayat pesanan.
- **Fragment & Navigation Component**: empat fragment dikelola lewat nav_graph
  dan disinkronkan dengan Bottom Navigation.
- **Background Thread**: operasi database dijalankan di ExecutorService dan
  hasilnya dikirim ke UI lewat Handler agar tidak memblokir antarmuka.
- **Networking**: Retrofit memanggil endpoint produk; bila gagal/offline, data
  diambil dari cache Room dan tersedia tombol coba lagi.
- **Persistensi Lokal**: Room menyimpan cache produk, favorit, keranjang, dan
  pesanan; SharedPreferences menyimpan sesi login dan preferensi tema.
- **Tema**: palet coklat & emas batik untuk mode terang dan gelap.

## 📝 Konvensi Commit (Semantic)
Format: `<type>: <deskripsi>`. Contoh: `feat:`, `fix:`, `docs:`, `style:`,
`refactor:`, `chore:`.

## 📄 Lisensi
MIT License — untuk keperluan pembelajaran.