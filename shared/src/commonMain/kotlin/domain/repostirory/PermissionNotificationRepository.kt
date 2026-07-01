package domain.repostirory




interface PermissionNotificationRepository {
  fun isChekedPermission() : Boolean
  suspend  fun requestPermission() : Boolean
}
