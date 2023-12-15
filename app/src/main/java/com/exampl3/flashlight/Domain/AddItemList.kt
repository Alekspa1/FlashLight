package com.exampl3.flashlight.Domain

class AddItemList(private val itemListRepository: ItemListRepository) {
    fun addItem(item: Item){
        itemListRepository.addItem(item)
    }
}