package com.exampl3.flashlight.Domain

import android.content.Context
import android.widget.TextView
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.R
import ru.rustore.sdk.appupdate.manager.factory.RuStoreAppUpdateManagerFactory
import ru.rustore.sdk.appupdate.model.UpdateAvailability
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpgrateRustore @Inject constructor(context: Context, val themeImp: ThemeImp) {

    val updateManager = RuStoreAppUpdateManagerFactory.create(context)
   operator fun invoke (textView: TextView){
        updateManager.getAppUpdateInfo().addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability == UpdateAvailability.UPDATE_AVAILABLE) {
                textView.text = "Есть обновление"
                val icon = R.drawable.ic_update_onn
                themeImp.view(mapOf(Const.Action.TEXT_IMAGE to mapOf(textView to icon)))
            }
        }
            .addOnFailureListener { throwable ->
                LogText("getAppUpdateInfo error, $throwable")
            }
    }
}