package com.example.foodorder.data

import com.example.foodorder.R
import com.example.foodorder.model.Food

object FoodRepository {

    private val foods: List<Food> = listOf(
        Food(
            id = "F001",
            name = "Nasi Goreng Spesial",
            category = "Makanan utama",
            description = "Nasi goreng harum dengan bumbu spesial nusantara, disajikan dengan suwiran ayam gurih, telur mata sapi, acar mentimun, dan kerupuk renyah.",
            price = 25000,
            imageResId = R.drawable.ic_food_nasi_goreng
        ),
        Food(
            id = "F002",
            name = "Mie Goreng Jawa",
            category = "Makanan utama",
            description = "Mie kuning kenyal dimasak dengan kecap manis khas Jawa, sawi hijau segar, telur orak-arik lezat, dan taburan bawang merah goreng harum.",
            price = 22000,
            imageResId = R.drawable.ic_food_mie_goreng
        ),
        Food(
            id = "F003",
            name = "Ayam Geprek",
            category = "Makanan utama",
            description = "Ayam goreng tepung super krispi yang digeprek dengan ulekan cabai rawit bawang pedas nampol, disajikan lengkap dengan lalapan timun segar.",
            price = 20000,
            imageResId = R.drawable.ic_food_ayam_geprek
        ),
        Food(
            id = "F004",
            name = "Sate Ayam",
            category = "Makanan utama",
            description = "Tusukan daging ayam empuk yang dipanggang sempurna di atas arang, disiram bumbu kacang kental legit, irisan bawang merah, dan kecap manis.",
            price = 28000,
            imageResId = R.drawable.ic_food_sate_ayam
        ),
        Food(
            id = "F005",
            name = "Bakso Sapi",
            category = "Makanan utama",
            description = "Bakso daging sapi asli kenyal disajikan dalam mangkuk kuah kaldu sapi gurih mendidih, mie kuning, bihun, seledri, dan pangsit goreng renyah.",
            price = 18000,
            imageResId = R.drawable.ic_food_bakso_sapi
        ),
        Food(
            id = "F006",
            name = "Soto Ayam",
            category = "Makanan utama",
            description = "Soto ayam kuah kuning kunyit kaya rempah tradisional, suwiran daging ayam, potongan telur rebus, tauge segar, dan perasan jeruk nipis nikmat.",
            price = 20000,
            imageResId = R.drawable.ic_food_soto_ayam
        ),
        Food(
            id = "F007",
            name = "Rendang Sapi",
            category = "Makanan utama",
            description = "Olahan daging sapi lembut khas Minangkabau yang dimasak berjam-jam dengan santan kelapa murni dan aneka rempah hingga berwarna pekat dan gurih kaya rasa.",
            price = 32000,
            imageResId = R.drawable.ic_food_rendang_sapi
        ),
        Food(
            id = "F008",
            name = "Gado-Gado",
            category = "Makanan utama",
            description = "Kombinasi sayuran rebus segar, tahu dan tempe goreng, potongan kentang, serta telur rebus yang disiram saus bumbu kacang gurih legit dan emping renyah.",
            price = 17000,
            imageResId = R.drawable.ic_food_gado_gado
        ),
        Food(
            id = "F009",
            name = "Es Teh Manis",
            category = "Minuman",
            description = "Seduhan daun teh wangi melati asli yang dipadukan dengan manisnya gula pasir dan es batu kristal yang sangat menyegarkan tenggorokan.",
            price = 5000,
            imageResId = R.drawable.ic_food_es_teh
        ),
        Food(
            id = "F010",
            name = "Jus Alpukat",
            category = "Minuman",
            description = "Jus buah alpukat mentega segar dengan tekstur kental dan creamy, berpadu lelehan susu kental manis cokelat premium disajikan dingin.",
            price = 12000,
            imageResId = R.drawable.ic_food_jus_alpukat
        )
    )

    fun getAllFoods(): List<Food> = foods

    fun getFoodById(id: String): Food? = foods.find { it.id.equals(id, ignoreCase = true) }

    fun searchFoods(query: String, category: String = "Semua"): List<Food> {
        return foods.filter { food ->
            val matchesQuery = query.isBlank() ||
                    food.name.contains(query, ignoreCase = true) ||
                    food.description.contains(query, ignoreCase = true)
            val matchesCategory = category.equals("Semua", ignoreCase = true) ||
                    food.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }
}
