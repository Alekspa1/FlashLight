package domain.repostirory


interface SettingsAppRepository {

    fun getSort()
    fun saveSort(value: String)

    fun getTheme()
    fun saveTheme(value: String)

    fun getSize()
    fun saveSize(value: String)

    fun getUriAlarm()
    fun saveUriAlarm(uri: String)

    fun getOldUriAlarm() 
    fun saveOldUriAlarm(uri: String)
  
}
