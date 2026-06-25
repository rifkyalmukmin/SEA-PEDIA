package com.example.seapedia.presentation

import com.example.seapedia.domain.AppReview
import com.example.seapedia.domain.Product
import com.example.seapedia.domain.Store

/**
 * Data dummy untuk Level 1 selama backend katalog belum terintegrasi.
 * Memungkinkan layar publik (katalog, detail, review) bisa didemokan langsung.
 */
object DummyData {

    private val tokoLautBiru = Store(
        id = "s1",
        name = "Toko Laut Biru",
        description = "Hasil laut segar langsung dari nelayan Muara Angke.",
        rating = 4.8,
    )
    private val samuderaFresh = Store(
        id = "s2",
        name = "Samudera Fresh",
        description = "Spesialis udang & cumi premium kualitas ekspor.",
        rating = 4.6,
    )

    val products: List<Product> = listOf(
        Product("p1", "Salmon Fillet Segar 500g", "Salmon Norwegia potong fillet, cocok untuk sashimi dan steak. Disimpan rantai dingin.", 145_000, 24, tokoLautBiru, "Ikan", null),
        Product("p2", "Udang Vaname Kupas 1kg", "Udang vaname segar tanpa kepala, sudah dikupas dan dibersihkan.", 98_000, 40, samuderaFresh, "Udang", null),
        Product("p3", "Cumi-cumi Segar 1kg", "Cumi ukuran sedang, daging tebal dan kenyal. Bersih siap masak.", 75_000, 18, samuderaFresh, "Cumi", null),
        Product("p4", "Kerang Hijau 1kg", "Kerang hijau segar dari perairan bersih, kaya protein.", 32_000, 0, tokoLautBiru, "Kerang", null),
        Product("p5", "Ikan Kakap Merah 1kg", "Kakap merah utuh segar, daging padat manis. Cocok dibakar.", 88_000, 12, tokoLautBiru, "Ikan", null),
        Product("p6", "Gurita Baby 500g", "Gurita baby segar, empuk untuk tumis dan grill.", 65_000, 9, samuderaFresh, "Gurita", null),
    )

    fun productById(id: String): Product? = products.firstOrNull { it.id == id }

    val reviews: List<AppReview> = listOf(
        AppReview("r1", "Andi Pratama", 5, "Aplikasinya gampang dipakai, cari seafood segar jadi cepat. Mantap!"),
        AppReview("r2", "Siti Rahma", 4, "Tampilan bersih dan jelas. Semoga makin banyak tokonya."),
        AppReview("r3", "Budi Santoso", 5, "Suka konsep marketplace multi-toko-nya. Checkout-nya jelas."),
    )
}
