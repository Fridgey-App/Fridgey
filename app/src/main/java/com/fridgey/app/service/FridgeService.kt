package com.fridgey.app.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FoodItem(
    val barcode: String,
    val addedTime: Timestamp,
)

class FridgeService @Inject constructor(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore, private val foodItemInfo: FoodItemInfoService) {
    private val uid: String
        get() = auth.currentUser!!.uid

    private val fridgeDocuments: CollectionReference
        get() = firestore.collection("users").document(uid).collection("fridge")

    private val userDocument: DocumentReference
        get() = firestore.collection("users").document(uid)

    suspend fun addFridgeItem(fridgeItem: FoodItem) {
        fridgeDocuments.document(fridgeItem.barcode).set(fridgeItem).await()
    }

    suspend fun addFridgeItems(fridgeItems: List<FoodItem>) {
        fridgeItems.map { fi -> addFridgeItem(fi) }
    }

    suspend fun getFridgeContents(): List<FoodItem> {
        val fridge = fridgeDocuments.get().await()
        return fridge.toObjects()
    }

    suspend fun removeFridgeItem(fridgeItem: FoodItem) {
        fridgeDocuments.document(fridgeItem.barcode).delete().await()
    }

    suspend fun createUserData() {
         userDocument.set(hashMapOf("userId" to uid)).await()
    }

    fun getFoodItemData(items: List<FoodItem>): LiveData<List<Pair<LoadState, FoodItemData?>>> {
        return liveData {
            val result = items.map {  i ->
                try {
                    val data = foodItemInfo.getData(i.barcode)
                    val loadState = if (data.status != "1")
                        LoadState.ERROR
                    else if (data.product?.product_name == null)
                        LoadState.ERROR
                    else
                        LoadState.READY
                    return@map Pair(loadState, data)
                }
                catch (ex: Throwable) {
                    Log.e("[SCAN]", "Error while getting product data! ", ex)
                    return@map Pair(LoadState.ERROR, null)
                }
            }
            emit(result)
        }
    }

}