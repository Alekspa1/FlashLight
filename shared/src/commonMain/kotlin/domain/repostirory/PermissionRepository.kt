package domain.repostirory




interface PermissionRepository {
  fun isChekedPermission(permissionName: String) : Boolean
  suspend  fun requestPermission(permissionName: String) : Boolean
}
