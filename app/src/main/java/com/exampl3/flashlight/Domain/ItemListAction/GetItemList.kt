package com.exampl3.flashlight.Domain.ItemListAction

import com.exampl3.flashlight.Domain.Item

class GetItemList(private val itemListRepository: ItemListRepository) {
    fun getItemList(): List<Item>{
        return itemListRepository.getItemList()
    }
}