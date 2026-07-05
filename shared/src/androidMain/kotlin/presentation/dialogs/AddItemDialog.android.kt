package presentation.dialogs

import android.net.Uri
import androidx.core.net.toUri
import io.github.vinceglb.filekit.core.PlatformFile

actual fun parsePlatformUri(uri: PlatformFile?): String {
    return uri?.uri.toString()
}