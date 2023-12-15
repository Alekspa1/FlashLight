package com.exampl3.flashlight.Domain.ItemListAction

import com.exampl3.flashlight.Domain.Item

class DeleteItemList(private val itemListRepository: ItemListRepository) {
    fun deleteItem(item: Item){
        itemListRepository.deleteItem(item)

    }
}