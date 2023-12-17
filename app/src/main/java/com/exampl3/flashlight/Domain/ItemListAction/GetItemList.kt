package com.exampl3.flashlight.Domain.ItemListAction

import androidx.lifecycle.LiveData
import com.exampl3.flashlight.Domain.Item

class GetItemList(private val itemListRepository: ItemListRepository) {
    fun getItemList(): LiveData<List<Item>>{
        return itemListRepository.getItemList()
    }
}