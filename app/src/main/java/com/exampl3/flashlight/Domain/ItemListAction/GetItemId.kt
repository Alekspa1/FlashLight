package com.exampl3.flashlight.Domain.ItemListAction

import com.exampl3.flashlight.Domain.Item

class GetItemId(private val itemListRepository: ItemListRepository) {
    fun getItemId(id: Int): Item {
        return itemListRepository.getItemId(id)
    }
}