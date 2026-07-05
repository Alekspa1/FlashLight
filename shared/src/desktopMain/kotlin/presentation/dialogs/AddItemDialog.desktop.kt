package presentation.dialogs

import io.github.vinceglb.filekit.core.PlatformFile

actual fun parsePlatformUri(uri: PlatformFile?): String {
    return uri?.path.toString()
}