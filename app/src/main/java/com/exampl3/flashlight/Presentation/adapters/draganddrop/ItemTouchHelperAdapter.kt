package com.exampl3.flashlight.Presentation.adapters.draganddrop

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onMoveComplete()
}