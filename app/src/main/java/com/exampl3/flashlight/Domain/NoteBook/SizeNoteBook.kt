package com.exampl3.flashlight.Domain.NoteBook

class SizeNoteBook(private val deleteAndSizeNoteBook: DeleteAndSizeNoteBook) {
    fun sizeNoteBook(size: Float): Float{
        return deleteAndSizeNoteBook.sizeNoteBook(size)

    }
}