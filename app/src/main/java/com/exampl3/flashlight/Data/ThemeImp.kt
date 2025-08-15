package com.exampl3.flashlight.Data

import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Domain.repository.ThemeRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.component1


@Singleton
class ThemeImp @Inject constructor(private val context: Application) : ThemeRepository {


    override fun view(map: Map<Const.Action, Map<View, Int>>) {
        map.forEach { (action, value) ->
            when (action) {
                Const.Action.TEXT_STYLE -> setTextStyle(value)
                Const.Action.TEXT_COLOR -> setTextColor(value)
                Const.Action.TEXT_IMAGE -> textImage(value)

                Const.Action.IMAGE_RESOURCE -> imageResource(value)
                Const.Action.BACKGROUND_COLOR -> backgroudColor(value)
                Const.Action.BACKGROUND_RESOURCE ->  backgroudResource(value)


            }
        }
    }


    fun imageResource(map: Map<View, Int>) {
        map.forEach { (view, resource) ->
            (view as ImageView).setImageResource(resource)
        }

    }


    fun backgroudColor(map: Map<View, Int>) {
        map.forEach { (view, resource) ->
            view.setBackgroundColor(ContextCompat.getColor(context,resource))
        }

    }



    fun backgroudResource(map: Map<View, Int>) {
        map.forEach { (view, resource) ->
            view.setBackgroundResource(resource)
        }

    }

    fun setTextStyle(map: Map<View, Int>) {
        map.forEach { (view, resource) ->
            (view as TextView).setTextAppearance(resource)
        }
    }

    fun setTextColor(map: Map<View, Int>) {
        map.forEach {(view, resource) ->
            (view as TextView).setTextColor(ContextCompat.getColor(context, resource))
        }


    }

    fun textImage(map: Map<View, Int>) {
        map.forEach { (view, resource) ->
            (view as TextView).setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(context, resource),
                null, // Top
                null, // End
                null  // Bottom
            )
        }

    }
}

