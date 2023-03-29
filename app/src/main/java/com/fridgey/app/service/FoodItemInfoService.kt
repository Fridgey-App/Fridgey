package com.fridgey.app.service

import com.google.gson.annotations.SerializedName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class FoodItemData (
    @SerializedName("code") val code : String,
    @SerializedName("product") val product : Product,
    @SerializedName("status") val status : String,
    @SerializedName("status_verbose") val statusVerbose : String
)

data class Product (
    @SerializedName("_id") val id : String,
    @SerializedName("_keywords") val keywords : List<String>,
    @SerializedName("brands") val brands : String?,
    @SerializedName("categories") val categories : String,
    @SerializedName("categories_hierarchy") val categoriesHierarchy : List<String>,
    @SerializedName("expiration_date") val expirationDate : String,
    @SerializedName("generic_name") val genericName : String,
    @SerializedName("image_front_small_url") val image_front_small_url : String,
    @SerializedName("image_front_thumb_url") val image_front_thumb_url : String,
    @SerializedName("image_front_url") val image_front_url : String,
    @SerializedName("image_small_url") val image_small_url : String,
    @SerializedName("image_thumb_url") val image_thumb_url : String,
    @SerializedName("image_url") val image_url : String,
    @SerializedName("ingredients_text") val ingredientsText : String,
    @SerializedName("no_nutrition_data") val noNutritionData : String,
    @SerializedName("nutrient_levels") val nutrientLevels : NutrientLevels,
    @SerializedName("nutriments") val nutriments : Nutriments,
    @SerializedName("nutriscore_grade") val nutriscoreGrade : String,
    @SerializedName("nutriscore_score") val nutriscoreScore : Int,
    @SerializedName("product_name") val product_name : String,
    @SerializedName("quantity") val quantity : String,
    @SerializedName("selected_images") val selectedImages : SelectedImages
)

data class Front (
    @SerializedName("display") val display : Display,
    @SerializedName("small") val small : Small,
    @SerializedName("thumb") val thumb : Thumb
)

data class Display (
    @SerializedName("en") val en : String
)

data class Small (
    @SerializedName("en") val en : String
)

data class Thumb (
    @SerializedName("en") val en : String
)

data class NutrientLevels (
    @SerializedName("fat") val fat : String,
    @SerializedName("salt") val salt : String,
    @SerializedName("saturated-fat") val saturatedFat : String,
    @SerializedName("sugars") val sugars : String
)

data class SelectedImages (
    @SerializedName("front") val front : Front
)

data class Nutriments (
    @SerializedName("carbohydrates") val carbohydrates : Double,
    @SerializedName("energy-kcal") val energyKcal : Double,
    @SerializedName("fat") val fat : Double,
    @SerializedName("fiber") val fiber : Double,
    @SerializedName("fruits-vegetables-nuts-estimate-from-ingredients_100g") val fruitsVegetablesNutsEstimateFromIngredients100g : Double,
    @SerializedName("proteins") val proteins : Double,
    @SerializedName("salt") val salt : Double,
    @SerializedName("saturated-fat") val saturated_fat : Double,
    @SerializedName("sodium") val sodium : Double,
    @SerializedName("sugars") val sugars : Double,
)


enum class LoadState {
    LOADING, ERROR, READY
}

interface FoodItemInfoService {
    @GET("/api/v0/product/{barcode}.json")
    suspend fun getData(@Path("barcode") barcode: String): FoodItemData

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideBarcodeInfoService(retrofit: Retrofit): FoodItemInfoService {
        return retrofit.create(FoodItemInfoService::class.java)
    }
}