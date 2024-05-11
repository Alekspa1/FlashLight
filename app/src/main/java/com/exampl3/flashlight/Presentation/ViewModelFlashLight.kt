package com.exampl3.flashlight.Presentation


import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.Domain.model.AlarmManagerImp
import com.exampl3.flashlight.Domain.model.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.model.sharedPreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val turnFlashLight: TurnFlashLightImpl
): ViewModel() {

    fun savePremium(flag: Boolean) = pref.savePremium(flag)
    fun getPremium() = pref.getPremium()

    fun saveNoteBook(value: String) = pref.saveStringNoteBook(value)
    fun getNotebook() = pref.getStringNoteBook()




    fun turnFlasLigh(flag: Boolean){
            turnFlashLight.turnFlashLight(flag)
    }
fun alarmInsert(item: Item, action: Int){
    alarmInsert.alarmInsert(item, action)
}


}