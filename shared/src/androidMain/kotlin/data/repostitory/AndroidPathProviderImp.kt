package data.repostitory

import android.content.Context
import domain.repostirory.PathProviderRepostitory

class AndroidPathProviderImp(val context: Context) : PathProviderRepostitory {
    override fun getInternalAppPath(): String {
       return context.filesDir.absolutePath
    }
}