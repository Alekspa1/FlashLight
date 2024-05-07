package com.exampl3.flashlight.Presentation


import androidx.lifecycle.ViewModel
import com.exampl3.flashlight.model.AlarmManagerImp
import com.exampl3.flashlight.model.TurnFlashLightImpl
import com.exampl3.flashlight.Domain.Room.Item
import com.exampl3.flashlight.Domain.sharedPreference.SharedPreferenceImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelFlashLight @Inject constructor(
    private val pref: SharedPreferenceImpl,
    private val alarmInsert: AlarmManagerImp,
    private val turnFlashLight: TurnFlashLightImpl
): ViewModel() {
    //private val repository = TurnFlashLightImpl
   // private val turnFlashLight = TurnFlashLight(repository)
//    private val _premium = MutableLiveData<Boolean>()
//    var premium: LiveData<Boolean> = _premium

    fun saveSP(flag: Boolean) = pref.saveSP(flag)


    fun getSP() = pref.getSP()




    fun turnFlasLigh(flag: Boolean){
            turnFlashLight.turnFlashLight(flag)
    }
//    fun alarmInsert(item: Item, time: Long, context: Context,alarmManager: AlarmManager, action: Int){
//        alarmInsert.alarmManagerInsert(item, time, context,alarmManager, action)
//    }
fun alarmInsert(item: Item, action: Int){
    alarmInsert.alarmInsert(item, action)
}


}