package data.repostitory

import android.content.Context
import io.github.vinceglb.filekit.core.PlatformFile
import okio.Sink
import okio.Source
import okio.sink
import okio.source

class AndroidBackupContextImpl(private val context: Context) : PlatformBackupContextRepository {
    
    override fun getSinkFromPlatformFile(platformFile: PlatformFile): Sink {
        // Извлекаем нативный Android Uri из PlatformFile библиотеки FileKit
        val androidUri = platformFile.uri 
        val outputStream = context.contentResolver.openOutputStream(androidUri) 
            ?: throw IllegalStateException("Не удалось открыть OutputStream для Uri: $androidUri")
        return outputStream.sink() // Превращаем стандартный Java Stream в Okio Sink
    }

    override fun getSourceFromPlatformFile(platformFile: PlatformFile): Source {
        // Извлекаем нативный Android Uri для чтения
        val androidUri = platformFile.uri
        val inputStream = context.contentResolver.openInputStream(androidUri)
            ?: throw IllegalStateException("Не удалось открыть InputStream для Uri: $androidUri")
        return inputStream.source() // Превращаем стандартный Java Stream в Okio Source
    }
}
