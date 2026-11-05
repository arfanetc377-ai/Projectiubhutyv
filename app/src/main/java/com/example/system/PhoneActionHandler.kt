package com.example.system

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast

object PhoneActionHandler {

    fun executeAction(context: Context, actionCode: String?): String? {
        if (actionCode == null) return null

        return try {
            when (actionCode.uppercase()) {
                "OPEN_WHATSAPP" -> {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                    if (intent != null) {
                        context.startActivity(intent)
                        "WhatsApp opened!"
                    } else {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://web.whatsapp.com"))
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(webIntent)
                        "Opening WhatsApp Web"
                    }
                }
                "OPEN_FACEBOOK" -> {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.facebook.katana")
                    if (intent != null) {
                        context.startActivity(intent)
                        "Facebook opened!"
                    } else {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com"))
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(webIntent)
                        "Opening Facebook in Browser"
                    }
                }
                "OPEN_TIKTOK" -> {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.zhiliaoapp.musically")
                    if (intent != null) {
                        context.startActivity(intent)
                        "TikTok opened!"
                    } else {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com"))
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(webIntent)
                        "Opening TikTok in Browser"
                    }
                }
                "OPEN_YOUTUBE" -> {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                    if (intent != null) {
                        context.startActivity(intent)
                        "YouTube opened!"
                    } else {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))
                        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(webIntent)
                        "Opening YouTube in Browser"
                    }
                }
                "OPEN_CAMERA" -> {
                    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "Camera opened!"
                }
                "OPEN_MESSAGES" -> {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_MESSAGING)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "Messaging opened!"
                }
                "OPEN_ALARM" -> {
                    val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "Alarms opened!"
                }
                "OPEN_SETTINGS" -> {
                    val intent = Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "Settings opened!"
                }
                "OPEN_DIALER" -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                    "Dialer opened!"
                }
                "SLEEP_MODE_ON" -> {
                    "Sleep mode enabled. Tabu AI will auto-reply!"
                }
                "SLEEP_MODE_OFF" -> {
                    "Sleep mode disabled. Welcome back!"
                }
                else -> null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open action: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }
}
