package data.perository

import CommonConst.SORT_SETTINGS
import CommonConst.SORT_STANDART
import CommonConst.SIZE_SETTINGS
import CommonConst.SIZE_STANDART

import CommonConst.ALARM_SETTINGS
import CommonConst.URI_STANDART
import CommonConst.URI_OLD

import com.russhwolf.settings.Settings
import domain.repostirory.SettingsAppRepository


class MultiplatrormAppSettings(private val settings: Settings) : SettingsAppRepository {

 override   fun getSort() = settings.getString(SORT_SETTINGS, SORT_STANDART)
 override   fun saveSort(value: String){
  settings.putString(SORT_SETTINGS, value) 
 }

  override  fun getTheme() = settings.getString(THEME_SETTINGS, THEME_FUTURE)
  override  fun saveTheme(value: String){
     settings.putString(THEME_SETTINGS, value)
  }

 override   fun getSize() = settings.getString(SIZE_SETTINGS, SIZE_STANDART)
 override   fun saveSize(value: String){
   settings.putString(SIZE_SETTINGS, value)
 }

  override  fun getUriAlarm() = settings.getString(ALARM_SETTINGS, URI_STANDART)
  override  fun saveUriAlarm(uri: Uri){
    settings.putString(ALARM_SETTINGS, uri.toString())
  }

 override  fun getOldUriAlarm() = settings.getString(URI_OLD, URI_STANDART)
 override   fun saveOldUriAlarm(uri: Uri){
    settings.putString(URI_OLD, uri.toString())
 }

}
