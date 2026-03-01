## REFLECTION 1
Pada proyek ini, saya mengimplementasikan fitur Edit Product dan Delete Product menggunakan Spring Boot. Dalam implementasinya, saya menerapkan beberapa prinsip clean code dan secure coding.
Setiap method pada ProductRepository memiliki tanggung jawab yang jelas, seperti update() untuk memperbarui produk dan delete() untuk menghapus produk. Penamaan method dan variabel dibuat deskriptif sehingga kode mudah dipahami dan dirawat. Struktur kode juga ditulis secara konsisten agar meningkatkan keterbacaan.
Dari sisi keamanan, sistem memastikan bahwa produk yang akan diedit atau dihapus benar-benar ada. Jika produk tidak ditemukan, operasi tidak dijalankan dan tidak menyebabkan error, sehingga mencegah perubahan data yang tidak valid. Selain itu, implementasi ini tidak mengekspos data sensitif kepada pengguna.
Namun, masih terdapat beberapa kekurangan. Penanganan error masih menggunakan null dan belum menggunakan custom exception. Validasi input seperti jumlah produk negatif juga belum diterapkan, serta fitur edit dan delete belum dilindungi oleh autentikasi. Ke depannya, hal ini dapat diperbaiki dengan menambahkan exception handling, validasi data, dan Spring Security. Secara keseluruhan, fitur Edit dan Delete telah berjalan dengan baik dan memenuhi prinsip clean code, namun masih dapat ditingkatkan dari sisi keamanan dan validasi.

## REFLECTION 2
Setelah menulis unit test, saya merasa lebih percaya diri terhadap fungsionalitas kode karena setiap fitur dapat diverifikasi secara terpisah. Jumlah unit test dalam satu kelas sebaiknya menyesuaikan jumlah public method dan skenario penting (positif dan negatif). Untuk memastikan unit test sudah cukup, kita dapat menggunakan code coverage sebagai metrik. Namun, 100% code coverage tidak menjamin kode bebas bug, karena coverage hanya memastikan baris kode dieksekusi, bukan kebenaran logika atau kualitas pengujian. 
Pada pembuatan functional test baru untuk mengecek jumlah produk, membuat kelas baru dengan struktur dan setup yang sama dapat menurunkan kualitas clean code karena terjadi duplikasi kode (misalnya setup Selenium, base URL, dan konfigurasi driver). Duplikasi ini membuat kode sulit dirawat dan rawan inkonsistensi. Untuk memperbaikinya, setup bersama sebaiknya dipindahkan ke base class, utility class, atau menggunakan @BeforeEach bersama sehingga kode menjadi lebih bersih, reusable, dan mudah dikembangkan.
REFLECTION 3 - CI/CD dan Code Quality

## REFLECTION 3

## 1. Isu Kualitas Kode yang Diperbaiki

Selama exercise ini, saya mengidentifikasi dan memperbaiki beberapa isu kualitas kode:

**a) Pelanggaran Rule PMD: AvoidAccessibilityAlteration**
- **Isu yang dideteksi:** Pada class `ProductControllerTest.java` (line 39) dan `ProductServiceImplTest.java` (line 21), saya menggunakan `setAccessible(true)` via refleksi untuk mengubah visibility field private, yang melanggar PMD rule AvoidAccessibilityAlteration.
- **Strategi perbaikan:** Menambahkan anotasi `@SuppressWarnings("PMD.AvoidAccessibilityAlteration")` pada kedua test class dengan justifikasi bahwa penggunaan refleksi di unit test adalah praktik umum untuk dependency injection manual tanpa Spring context. Ini adalah trade-off yang diterima karena test berjalan di isolation tanpa container framework, sehingga refleksi diperlukan untuk setup dependency.

**b) Konfigurasi Gradle PMD yang Tidak Valid**
- **Isu yang dideteksi:** Property `isIgnoreFailures` tidak kompatibel dengan Kotlin DSL untuk plugin PMD di Gradle.
- **Strategi perbaikan:** Mengubah `isIgnoreFailures = false` menjadi `ignoreFailures = false` untuk menyesuaikan dengan Kotlin DSL yang benar, sehingga build dapat dikompilasi dan PMD dapat berjalan dengan baik.

**c) Coverage yang Tidak Ideal**
- **Isu yang dideteksi:** Coverage belum mencapai 100% untuk beberapa class seperti `ProductController`, `EshopApplication`, dan `Product`.
- **Strategi perbaikan:** Menambahkan unit test baru (`ProductRepositoryTest`, `ProductServiceImplTest`, `ProductControllerTest`) yang secara spesifik melatih method-method yang belum tercakup dalam test suite awal, meningkatkan coverage secara bertahap.

## 2. Apakah Implementasi CI/CD Memenuhi Definisi Continuous Integration dan Continuous Deployment?

**Indikasi Continuous Integration:**
Ya, implementasi CI/CD ini telah memenuhi sebagian besar prinsip Continuous Integration. Setiap push ke repository secara otomatis memicu GitHub Actions workflow yang menjalankan test suite (`./gradlew test`), menghasilkan laporan JaCoCo, dan menjalankan analisis code quality dengan PMD. Hal ini memastikan bahwa setiap perubahan kode diverifikasi secara konsisten dan cepat, tanpa menunggu manual testing atau code review yang lama.

**Indikasi Continuous Deployment:**
Implementasi saat ini baru sebagian memenuhi prinsip Continuous Deployment. Workflow `deploy-render.yml` telah dibuat dan di-commit, namun belum fully automated karena memerlukan setup manual dari GitHub Secrets (`RENDER_SERVICE_ID` dan `RENDER_API_KEY`). Setelah secrets dikonfigurasi, workflow akan secara otomatis trigger deploy ke Render PaaS setiap kali ada push ke branch `main` atau `master`, yang berarti deployment benar-benar otomatis dan cepat tanpa intervensi manual di production environment.

## REFLECTION 4
## SOLID Principles 

## 1. SRP (Single Responsibility Principle)

**Implementasi:**
- **CarController** (CarController.java): Hanya bertanggung jawab menangani HTTP request/response untuk fitur Car. Tidak mencampur dengan Product.
- **CarServiceImpl**: Hanya mengoordinasi use case Car (create, read, update, delete). Tidak mengurus penyimpanan langsung.
- **CarRepositoryImpl**: Hanya bertanggung jawab akses data Car (CRUD ke storage).
- **Car** (model): Hanya menyimpan data mobil.

Dulu CarController digabung dengan ProductController dan me-extend-nya, sehingga satu file punya dua tanggung jawab. Sekarang CarController dipisah ke file sendiri dan tidak me-extend ProductController, sehingga tiap class punya satu tanggung jawab.


## 2. OCP (Open/Closed Principle)

**Implementasi:**
- **Interface CarRepository**: Service bergantung pada abstraksi. Perilaku baru (misalnya repository pakai database, cache, atau API eksternal) bisa ditambah dengan membuat class baru yang mengimplementasi CarRepository, tanpa mengubah CarServiceImpl atau controller.
- **Interface CarService / CarReader / CarWriter**: Perilaku baru (validasi, logging, caching) bisa ditambah dengan decorator atau implementasi baru yang mengimplementasi interface yang sama, tanpa mengubah controller atau kode pemanggil.

Dengan bergantung pada interface, kita bisa menambah implementasi baru (extend behavior) tanpa memodifikasi kode yang memakai interface tersebut.


## 3. LSP (Liskov Substitution Principle)

**Implementasi:**
- **Sebelum:** arController me-extend ProductController. Secara semantik Car bukanlah “jenis Product” dalam arti substitusi: endpoint dan dependency berbeda. Mengganti ProductController dengan CarController akan mengubah perilaku (route /car/* vs /product/*), sehingga melanggar LSP.
- **Sesudah:** CarController tidak lagi me-extend ProductController. Masing-masing controller berdiri sendiri. Tidak ada hubungan substitusi antara Product dan Car di layer controller, sehingga tidak ada pelanggaran LSP. Jika nanti ada abstract base (misalnya BaseCrudController) yang memang dirancang untuk di-substitute, subclass harus bisa dipakai menggantikan base tanpa mengubah kontrak (URL, dependency, return behavior).


## 4. ISP (Interface Segregation Principle)

**Implementasi:**
- **CarReader**: Hanya findAll() dan findById(String carId). Client yang cuma butuh baca data (misalnya laporan, tampilan read-only) hanya bergantung pada CarReader.
- **CarWriter**: Hanya create(Car car), update(String carId, Car car), deleteCarById(String carId). Client yang cuma butuh menulis/ubah data hanya bergantung pada CarWriter.
- **CarService**: Extends CarReader dan CarWriter. Controller yang butuh full CRUD bergantung pada CarService saja.

Dengan ini, client tidak dipaksa bergantung pada method yang tidak dipakai (misalnya client read-only tidak perlu tahu create/update/delete).


## 5. DIP (Dependency Inversion Principle)

**Implementasi:**
- **CarController** bergantung pada **CarService** (interface), bukan CarServiceImpl. High-level (controller) tidak tahu detail implementasi service.
- **CarServiceImpl** bergantung pada **CarRepository** (interface), bukan CarRepositoryImpl. Service tidak tahu detail storage (in-memory, DB, dll).
- **CarRepositoryImpl** mengimplementasi **CarRepository** (abstraksi). Detail penyimpanan bergantung pada abstraksi.

Alur dependensi: Controller → CarService (abstraksi) ← CarServiceImpl → CarRepository (abstraksi) ← CarRepositoryImpl. Tidak ada high-level module yang langsung bergantung pada concrete class low-level.





