package org.freedombox.freedombox.views.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_setup.*
import org.freedombox.freedombox.R
import org.freedombox.freedombox.components.AppComponent
import org.freedombox.freedombox.views.model.ConfigModel
import javax.inject.Inject

class SetupFragment : BaseFragment() {
    val TAG = "SETUP_FRAGMENT"

    @Inject lateinit var sharedPreferences: SharedPreferences
    @Inject lateinit var sharedPreferencesEditor: SharedPreferences.Editor


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val currentBoxName = activity.intent.getStringExtra(getString(R.string.current_box))
        discoveredUrl.setText(currentBoxName)

        saveConfig.setOnClickListener {
            getEnteredDetailsAndStoreInPreferences()
            activity.finish();
        }

    }

    fun getEnteredDetailsAndStoreInPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var configuredBoxesJSON = sharedPreferences.getString(
                getString(R.string.default_box)
                , null)

        val boxName = boxname.editableText.toString()
        val domain = discoveredUrl.editableText.toString()
        val username = username.editableText.toString()
        val password = password.editableText.toString()
        val default = setDefault.isChecked
        val configModel = ConfigModel(boxName, domain, username, password, default)

        var configuredBoxList = ArrayList<ConfigModel>()
        if (configuredBoxesJSON != null) {
            val gson = GsonBuilder().setPrettyPrinting().create()
            configuredBoxList = gson.fromJson(configuredBoxesJSON
                    , object : TypeToken<List<ConfigModel>>() {}.type)
        }
        configuredBoxList.add(configModel)

        sharedPreferencesEditor = sharedPreferences.edit()
        sharedPreferencesEditor.putString(getString(R.string.default_box),
                Gson().toJson(configuredBoxList))
        sharedPreferencesEditor.apply()

    }

    override fun getLayoutId() = R.layout.fragment_setup

    companion object {
        fun new(args: Bundle): SetupFragment {
            val fragment = SetupFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun injectFragment(appComponent: AppComponent) = appComponent.inject(this)

}