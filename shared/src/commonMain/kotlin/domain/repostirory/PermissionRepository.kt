package domain.repostirory




interface PermissionRepository {
  fun isChekedPermission(permisson: String) : Boolean
  suspend  fun requestPermission(permisson: String) : Boolean
}
