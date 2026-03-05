package com.example.example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            // Создаём Intent для запуска вашей MainActivity
            val intent = Intent(context, MainActivity::class.java).apply {
                // Флаг FLAG_ACTIVITY_NEW_TASK обязателен для запуска из виджета [citation:10]
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Добавляем идентификатор, чтобы открывался именно этот экземпляр
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            // Создаём PendingIntent [citation:6]
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId, // requestCode (используем ID виджета для уникальности)
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Связываем RemoteViews с вашим layout
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)

            // Устанавливаем PendingIntent на корневой элемент виджета [citation:10]
            // Если хотите, чтобы кликабельной была конкретная кнопка, замените R.id.widget на R.id.your_button_id
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Обновляем виджет
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val remoteViews = RemoteViews(context.packageName, R.layout.new_app_widget)
    // 2. Получаем текущую дату
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val currentDayOfWeek = getDayOfWeekInRussian(calendar)

    remoteViews.setTextViewText(R.id.tv_day, "$currentDay")

    remoteViews.setTextViewText(R.id.tv_day_of_week, "$currentDayOfWeek")

    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
}

private fun getDayOfWeekInRussian(calendar: Calendar): String {
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Пн"
        Calendar.TUESDAY -> "Вторник"
        Calendar.WEDNESDAY -> "Среда"
        Calendar.THURSDAY -> "Четверг"
        Calendar.FRIDAY -> "Пятница"
        Calendar.SATURDAY -> "Суббота"
        Calendar.SUNDAY -> "Вс"
        else -> "Пн"
    }
}