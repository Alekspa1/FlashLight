package com.exampl3.flashlight.Domain

class GetItemId(private val itemListRepository: ItemListRepository) {
    fun getItemId(id: Int): Item{
        return itemListRepository.getItemId(id)
    }
}