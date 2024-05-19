package com.exampl3.flashlight.Presentation



import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.exampl3.flashlight.Domain.Room.GfgDatabase
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentBlankFlashLightBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


@AndroidEntryPoint
class FragmentFlashLight : Fragment()  {
    private lateinit var binding: FragmentBlankFlashLightBinding
    private val model: ViewModelFlashLight by activityViewModels()
    @Inject
    lateinit var db: GfgDatabase
    private var calendar: Calendar = Calendar.getInstance()
    private lateinit var calendarDay: CalendarDay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankFlashLightBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db.CourseDao().getAll().asLiveData().observe(viewLifecycleOwner){list ->
            val calendarDays = mutableListOf<CalendarDay>()
            list.forEach {item->
                calendar = Calendar.getInstance()
                calendar.timeInMillis = item.alarmTime
                calendarDay = CalendarDay(calendar)
                calendarDay.imageResource = R.drawable.ic_alarm_on
                if (item.changeAlarm){
                    calendarDays.add(calendarDay)
                }
            }
            binding.calendarView.setCalendarDays(calendarDays)
        }


        binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener{
            override fun onClick(calendarDay: CalendarDay) {
                val time = calendarDay.calendar.get(Calendar.DAY_OF_MONTH)
                val c = Calendar.getInstance()
                CoroutineScope(Dispatchers.IO).launch {
                    db.CourseDao().getAllList().forEach { item->
                        if (item.changeAlarm) {
                            c.timeInMillis = item.alarmTime
                            if (time == c.get(Calendar.DAY_OF_MONTH) ) {
                                Log.d("MyLog", item.name)
                            }
                        }
                    }
                }
            }
        })
    }


    companion object {
        fun newInstance() = FragmentFlashLight()
    }

}