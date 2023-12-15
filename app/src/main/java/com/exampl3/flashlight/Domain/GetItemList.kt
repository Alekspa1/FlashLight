package com.exampl3.flashlight.Domain

class GetItemList(private val itemListRepository: ItemListRepository) {
    fun getItemList(): List<Item>{
        return itemListRepository.getItemList()
    }
}