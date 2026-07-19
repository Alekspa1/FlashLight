package domain.repostirory


interface SettingsAppRepository {

    fun getSort() : String
    fun saveSort(value: String)

    fun getTheme() : String
    fun saveTheme(value: String)

    fun getSize() : String
    fun saveSize(value: String)

    fun getUriAlarm() : String
    fun saveUriAlarm(uri: String)

    fun getOldUriAlarm()  : String
    fun saveOldUriAlarm(uri: String) 
  
}
