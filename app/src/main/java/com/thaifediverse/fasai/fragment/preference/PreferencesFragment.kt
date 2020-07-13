/* Copyright 2018 Conny Duck
 *
 * This file is a part of Tusky.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Tusky; if not,
 * see <http://www.gnu.org/licenses>. */

package com.thaifediverse.fasai.fragment.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.thaifediverse.fasai.PreferencesActivity
import com.thaifediverse.fasai.R
import com.thaifediverse.fasai.util.ThemeUtils
import com.thaifediverse.fasai.util.getNonNullString
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.colorInt
import com.mikepenz.iconics.utils.sizeRes

fun PreferenceFragmentCompat.requirePreference(key: String): Preference {
    return findPreference(key)!!
}

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.preferences)

        val themePreference: Preference = requirePreference("appTheme")
        themePreference.icon = IconicsDrawable(themePreference.context, GoogleMaterial.Icon.gmd_palette).apply { sizeRes = R.dimen.preference_icon_size; colorInt = ThemeUtils.getColor(themePreference.context, R.attr.iconColor) }

        val emojiPreference: Preference = requirePreference("emojiCompat")
        emojiPreference.icon = IconicsDrawable(emojiPreference.context, GoogleMaterial.Icon.gmd_sentiment_satisfied).apply { sizeRes = R.dimen.preference_icon_size; colorInt = ThemeUtils.getColor(themePreference.context, R.attr.iconColor) }

        val textSizePreference: Preference = requirePreference("statusTextSize")
        textSizePreference.icon = IconicsDrawable(textSizePreference.context, GoogleMaterial.Icon.gmd_format_size).apply { sizeRes = R.dimen.preference_icon_size; colorInt = ThemeUtils.getColor(themePreference.context, R.attr.iconColor) }

        val timelineFilterPreferences: Preference = requirePreference("timelineFilterPreferences")
        timelineFilterPreferences.setOnPreferenceClickListener {
            activity?.let { activity ->
                val intent = PreferencesActivity.newIntent(activity, PreferencesActivity.TAB_FILTER_PREFERENCES)
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            true
        }

        val httpProxyPreferences: Preference = requirePreference("httpProxyPreferences")
        httpProxyPreferences.setOnPreferenceClickListener {
            activity?.let { activity ->
                val intent = PreferencesActivity.newIntent(activity, PreferencesActivity.PROXY_PREFERENCES)
                activity.startActivity(intent)
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
            true
        }

        val languagePreference: Preference = requirePreference("language")
        languagePreference.icon = IconicsDrawable(languagePreference.context, GoogleMaterial.Icon.gmd_translate).apply { sizeRes = R.dimen.preference_icon_size; colorInt = ThemeUtils.getColor(themePreference.context, R.attr.iconColor) }

        val botIndicatorPreference = requirePreference("showBotOverlay")

        botIndicatorPreference.icon = ThemeUtils.getTintedDrawable(requireContext(), R.drawable.ic_bot_24dp, R.attr.iconColor)
    }

    override fun onResume() {
        super.onResume()
        updateHttpProxySummary()
    }

    private fun updateHttpProxySummary() {

        val httpProxyPref: Preference = requirePreference("httpProxyPreferences")

        val sharedPreferences = preferenceManager.sharedPreferences

        val httpProxyEnabled = sharedPreferences.getBoolean("httpProxyEnabled", false)

        val httpServer = sharedPreferences.getNonNullString("httpProxyServer", "")

        try {
            val httpPort = sharedPreferences.getNonNullString("httpProxyPort", "-1").toInt()

            if (httpProxyEnabled && httpServer.isNotBlank() && httpPort > 0 && httpPort < 65535) {
                httpProxyPref.summary = "$httpServer:$httpPort"
                return
            }
        } catch (e: NumberFormatException) {
            // user has entered wrong port, fall back to empty summary
        }

        httpProxyPref.summary = ""

    }

    companion object {
        fun newInstance(): PreferencesFragment {
            return PreferencesFragment()
        }
    }
}