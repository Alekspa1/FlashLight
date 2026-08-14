package data

import MainViewModel
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import data.perository.AlarmRepeadImp
import data.perository.KmpBackupManager
import data.perository.SaveDeleteImageImpl
import data.room.myDataBase
import domain.repostirory.AlarmRepeadRepository
import domain.repostirory.AlarmRepository
import domain.repostirory.SaveDeleteImageRepositpry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import kotlin.math.sin


import domain.repostirory.SharedPrefRepository
import domain.repostirory.SettingsAppRepository
import data.perository.MultiplatrormSettings
import data.perository.MultiplatrormAppSettings
import domain.repostirory.GetPlatrormRepository
import org.koin.core.qualifier.named

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import io.ktor.client.network.sockets.SocketTimeoutException 
import domain.repostirory.TelegramSyncServiceRepository
import data.perository.TelegramSyncServiceImpl
import org.koin.core.module.dsl.singleOf

expect val moduleAnotherPlatform: Module

val appModule = module {
    //viewModelOf(::MainViewModel)
     singleOf(::MainViewModel)

    single<myDataBase> {

        val builder: androidx.room.RoomDatabase.Builder<myDataBase> = get()

        builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

     single<HttpClient> {
        HttpClient {
            // 💥 САМОЕ ГЛАВНОЕ ДОБАВЛЕНИЕ ДЛЯ РЕАЛТАЙМА:
            install(HttpTimeout) {
                // Время, в течение которого Ктор готов ждать ответ от сервера (45 секунд)
                requestTimeoutMillis = 45_000 
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 45_000
            }

            install(HttpRequestRetry) {
                maxRetries = 3
                exponentialDelay()
                
                retryIf { request, response ->
                    response.status.value in 500..599
                }
                
                retryOnExceptionIf { request, cause ->
                    // Таймауты сети пускай пробрасываются в catch, 
                    // чтобы наш бесконечный цикл сам перезапускал Long Polling!
                    cause is SocketTimeoutException || cause.cause is SocketTimeoutException
                }
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Logger in DiCommon: $message")
                    }
                }
                level = LogLevel.BODY // На этапе теста BODY — супер, в логах будет виден весь JSON от ТГ
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // Это спасет от крашей при обновлении API ТГ!
                })
            }
        }
    }

    single { get<myDataBase>().CourseDao() }
    
    single<SharedPrefRepository> { MultiplatrormSettings(settings = get(named("noteBook")), platform = get()) }
    single<SettingsAppRepository > { MultiplatrormAppSettings(settings = get(named("settings"))) }
    
    single<AlarmRepeadRepository> { AlarmRepeadImp(get(),get()) }
    factory<SaveDeleteImageRepositpry> { SaveDeleteImageImpl(get()) }
    single<TelegramSyncServiceRepository>{TelegramSyncServiceImpl(get()) } 
    single { KmpBackupManager(get(), get(), get(), get()) }

}





fun initKoin(config: KoinAppDeclaration? = null) {

    startKoin {

        config?.invoke(this)

        modules(appModule, moduleAnotherPlatform)

    }

}
