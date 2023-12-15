package com.exampl3.flashlight.Domain

class ChangeItemList(private val itemListRepository: ItemListRepository) {
    fun changeItem(id: Int): Item{
        return itemListRepository.changeItem(id)
    }
}