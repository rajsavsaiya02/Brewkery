package com.example.brewkery.data.model

import com.google.gson.annotations.SerializedName

data class StoreResponse(
    @SerializedName("meta") val meta: StoreMeta,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("items") val menuItems: List<MenuItem>
)

data class StoreMeta(
    @SerializedName("app") val appName: String? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("delivery_fee") val deliveryFee: Double = 2.50,
    @SerializedName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerializedName("currency") val currency: String = "USD",
    @SerializedName("currency_symbol") val currencySymbol: String = "$"
)

data class Category(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("item_count") val itemCount: Int? = 0
)

data class MenuItem(
    @SerializedName("id") val id: Int,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("name") val name: String,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("description") val desc: String? = null,
    @SerializedName("base_price") val basePrice: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("review_count") val reviews: Int = 0,
    @SerializedName("badge") val badge: String? = null,
    @SerializedName("image_url") val imageUrl: String,
    @SerializedName("ingredients") val ingredients: List<String>? = emptyList(),
    @SerializedName("customizations") val customizations: CustomizationOptions? = null
) {
    val displayDescription: String get() = desc ?: tagline ?: ""
}

data class CustomizationOptions(
    @SerializedName("sizes") val sizes: List<SizeOption>? = emptyList(),
    @SerializedName("milk_options") val milkOptions: List<MilkOption>? = emptyList(),
    @SerializedName("sugar_levels") val sugarLevels: List<String>? = emptyList()
)

data class SizeOption(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("extra_price") val extraPrice: Double
)

data class MilkOption(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("extra_price") val extraPrice: Double
)
