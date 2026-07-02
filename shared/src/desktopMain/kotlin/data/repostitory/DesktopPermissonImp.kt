package com.dragon.shared.data.repostitory

import domain.repostirory.PermissionRepository

class DesktopPermissonImp : PermissionRepository {
    override fun isChekedPermission(permissionName: String): Boolean = true

    override suspend fun requestPermission(permissionName: String): Boolean = true
}