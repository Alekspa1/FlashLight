package data.repostitory

import androidx.core.net.toUri
import domain.repostirory.DeleteImageInItemReository
import java.io.File

class AnroidDeleteImageInItemImpl : DeleteImageInItemReository {
    override fun delete(uri: String) {
        try {
            // Для URI вида "file:///data/data/.../images/123.jpg"
            if (uri.toUri().scheme == "file") {
                File(uri.toUri().path!!).delete()
                return
            }

            // Если URI в строковом формате (из вашего saveImagePermanently)
            val uriString = uri
            if (uriString.startsWith("file://")) {
                File(uriString.substringAfter("file://")).delete()
            }
        } catch (_: Exception) {
        }
    }
}