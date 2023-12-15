package com.exampl3.flashlight.Domain.ItemListAction

import com.exampl3.flashlight.Domain.Item

class AddItemList(private val itemListRepository: ItemListRepository) {
    fun addItem(item: Item){
        itemListRepository.addItem(item)
    }
}