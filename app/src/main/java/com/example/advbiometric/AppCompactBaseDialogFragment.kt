/*
 *  Copyright (c) 2021 Sergey Komlach aka Salat-Cx65; Original project: https://github.com/Salat-Cx65/AdvancedBiometricPromptCompat
 *  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.example.advbiometric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.advbiometric.utils.startBiometric
import dev.skomlach.biometric.compat.BiometricPromptCompat
import dev.skomlach.common.cryptostorage.SharedPreferenceProvider

class AppCompactBaseDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1.
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    // 2.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_first,
            container,
            false
        )

        val buttonsList = view.findViewById<LinearLayout>(R.id.buttons_list)
        view.findViewById<LinearLayout>(R.id.buttons).visibility = View.GONE
        view.findViewById<CheckBox>(R.id.checkboxFullscreen).visibility = View.GONE
        if (!App.isReady) {
            App.onInitListeners.add(object : App.OnInitFinished {
                override fun onFinished() {
                    fillList(inflater, buttonsList)
                    checkDeviceInfo()
                }
            })
        } else {
            fillList(inflater, buttonsList)
        }
        view.findViewById<CheckBox>(R.id.checkboxWindowSecure).isChecked =
            SharedPreferenceProvider.getCryptoPreferences("app_settings")
                .getBoolean("checkboxWindowSecure", false)

        view.findViewById<CheckBox>(R.id.checkboxWindowSecure).setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPreferenceProvider.getCryptoPreferences("app_settings").edit()
                .putBoolean("checkboxWindowSecure", isChecked).apply()
            (activity as MainActivity).updateUI()
            Toast.makeText(context, "Changes applied", Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun checkDeviceInfo() {
        val deviceInfo = BiometricPromptCompat.deviceInfo
        view?.findViewById<TextView>(R.id.text)?.text = deviceInfo.toString()
    }

    private fun fillList(inflater: LayoutInflater, buttonsList: LinearLayout) {
        for (authRequest in App.authRequestList) {
            val container: FrameLayout =
                inflater.inflate(R.layout.button, buttonsList, false) as FrameLayout
            val button = container.findViewById<Button>(R.id.button)
            button.text = "${authRequest.api}/${authRequest.type}"
            button.setOnClickListener {
                startBiometric(authRequest)
            }
            buttonsList.addView(container)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        // 3.
        requireDialog().window?.setWindowAnimations(
            R.style.DialogAnimation
        )
    }
}
