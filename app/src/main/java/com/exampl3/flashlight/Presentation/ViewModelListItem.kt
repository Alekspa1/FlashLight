package com.exampl3.flashlight.Presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Data.ItemListRepositoryImpl
import com.exampl3.flashlight.Domain.Item
import com.exampl3.flashlight.Domain.ItemListAction.AddItemList
import com.exampl3.flashlight.Domain.ItemListAction.ChangeItemList
import com.exampl3.flashlight.Domain.ItemListAction.DeleteItemList
import com.exampl3.flashlight.Domain.ItemListAction.GetItemId
import com.exampl3.flashlight.Domain.ItemListAction.GetItemList
import com.exampl3.flashlight.Domain.ItemListAction.ItemListRepository

class ViewModelListItem: ViewModel() {
    private val repository = ItemListRepositoryImpl
    private val getItemList = GetItemList(repository)
    private val addItemList = AddItemList(repository)
    private val deleteItemList = DeleteItemList(repository)
    private val changeItemList = ChangeItemList(repository)
    private val getItemId = GetItemId(repository)

    val listItem = getItemList.getItemList()


    fun addItem(item: Item){
        addItemList.addItem(item)
    }
    fun deleteItem(item: Item){
        deleteItemList.deleteItem(item)
    }
    fun changeItem(item: Item){
//        val oldItem = getItemId(item.id)
//        deleteItem(oldItem)
        val newItem = item.copy(change = !item.change)
        changeItemList.changeItem(newItem)
    }
    fun getItemId(id: Int): Item{
        return getItemId.getItemId(id)
    }
}
