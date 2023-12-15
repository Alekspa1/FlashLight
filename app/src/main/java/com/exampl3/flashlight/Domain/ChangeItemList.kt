package com.exampl3.flashlight.Domain

class ChangeItemList(private val itemListRepository: ItemListRepository) {
    fun changeItem(item: Item){
        itemListRepository.changeItem(item)
    }
}