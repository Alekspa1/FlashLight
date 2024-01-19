package com.exampl3.flashlight.Presentation

import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Data.DeleteAndSizeNoteBookImpl
import com.exampl3.flashlight.Domain.NoteBook.SizeNoteBook

class ViewModelNoteBook: ViewModel() {
    private val repository = DeleteAndSizeNoteBookImpl
    private val sizeNoteBook = SizeNoteBook(repository)

    fun  sizeNoteBook(size: Float): Float{
        return sizeNoteBook.sizeNoteBook(size)
    }

}