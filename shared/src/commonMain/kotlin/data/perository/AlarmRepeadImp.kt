package data.perository

import CommonConst.ALARM_DAY
import CommonConst.ALARM_MONTH
import CommonConst.ALARM_ONE
import CommonConst.ALARM_WEEK
import CommonConst.ALARM_YEAR
import CommonConst.DAY
import MainViewModel
import data.room.CourseDao
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class AlarmRepeadImp(
    private val db: CourseDao,
    private val alarm: AlarmRepository
) : AlarmRepeadRepository {

    override suspend fun alarmRepead(id: Int,sendMessage : (String) -> Unit) {
        val item = db.getItemFromId(id)
        val currentMillis: Long = kotlin.time.Clock.System.now().toEpochMilliseconds()

        when (item.interval) {
            ALARM_ONE -> { sendMessage("Вы выбрали время которое уже прошло")}

            ALARM_DAY -> {
                var nextAlarmTime = item.alarmTime + DAY
                while (currentMillis > nextAlarmTime){
                    nextAlarmTime+=DAY
                }
                val newItem = item.copy(
                    changeAlarm = true,
                    alarmTime = nextAlarmTime
                )

                alarm.createAlarm(newItem)
                db.updateItem(newItem)


            }

            ALARM_WEEK -> {
                var nextAlarmTime = item.alarmTime + DAY*7
                while (currentMillis > nextAlarmTime){
                    nextAlarmTime+=DAY*7
                }
                val newItem = item.copy(
                    changeAlarm = true,
                    alarmTime = nextAlarmTime
                )
                alarm.createAlarm(newItem)
                db.updateItem(newItem)
            }

            ALARM_MONTH -> {
                var nextAlarmTime = addOneYearOrMonth(item.alarmTime,ALARM_MONTH )
                while (currentMillis > nextAlarmTime){
                    nextAlarmTime = addOneYearOrMonth(nextAlarmTime,ALARM_MONTH )
                }

                val newItem =
                    item.copy(changeAlarm = true, alarmTime = nextAlarmTime)
                alarm.createAlarm(newItem)
                db.updateItem(newItem)
            }

            ALARM_YEAR -> {
                var nextAlarmTime = addOneYearOrMonth(item.alarmTime,ALARM_YEAR )
                while (currentMillis > nextAlarmTime){
                    nextAlarmTime = addOneYearOrMonth(nextAlarmTime,ALARM_YEAR )
                }
                val newItem = item.copy(changeAlarm = true, alarmTime = nextAlarmTime)
                alarm.createAlarm(newItem)
                db.updateItem(newItem)
            }
        }


    }


    private fun addOneYearOrMonth(dateInMillis: Long, action: Int): Long {
        val tz = kotlinx.datetime.TimeZone.currentSystemDefault()

        // 1. Переводим миллисекунды в LocalDateTime ОДИН раз
        val localDateTime = kotlinx.datetime.Instant.fromEpochMilliseconds(dateInMillis)
            .toLocalDateTime(tz)

        // 2. Шагаем ровно на 1 месяц или год вперед на уровне LocalDate
        val updatedDate = if (action == ALARM_MONTH) {
            localDateTime.date.plus(1, kotlinx.datetime.DateTimeUnit.MONTH)
        } else {
            localDateTime.date.plus(1, kotlinx.datetime.DateTimeUnit.YEAR)
        }

        // 3. Собираем новую дату со старым временем (localDateTime.time) обратно в Timestamp
        return kotlinx.datetime.LocalDateTime(updatedDate, localDateTime.time)
            .toInstant(tz)
            .toEpochMilliseconds()
    }
}