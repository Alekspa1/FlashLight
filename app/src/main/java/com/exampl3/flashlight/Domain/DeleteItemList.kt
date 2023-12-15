package com.exampl3.flashlight.Domain

class DeleteItemList(private val itemListRepository: ItemListRepository) {
    fun deleteItem(item: Item){
        itemListRepository.deleteItem(item)

    }
}