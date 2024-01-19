package com.exampl3.flashlight.Data

import com.exampl3.flashlight.Domain.NoteBook.DeleteAndSizeNoteBook


object DeleteAndSizeNoteBookImpl: DeleteAndSizeNoteBook {

    override fun sizeNoteBook(size: Float): Float {
        val result = size/3
        return when(result){
            25F -> 35F
            35F -> 45F
            else -> 25F
        }
    }
}