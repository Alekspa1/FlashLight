package com.exampl3.flashlight.Domain.repository

import android.content.ContentResolver
import android.net.Uri

interface GetSystemSoundRepository {
    fun getSound() : Map<String, Uri>
}