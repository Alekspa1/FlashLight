package com.exampl3.flashlight.Domain.ItemListAction

import com.exampl3.flashlight.Domain.Item

class ChangeItemList(private val itemListRepository: ItemListRepository) {
    fun changeItem(item: Item){
        itemListRepository.changeItem(item)
    }
}