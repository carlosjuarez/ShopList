package com.juvcarl.shoplist.repository

import android.content.Context
import com.juvcarl.shoplist.R
import com.juvcarl.shoplist.ShopListApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPrefencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_global), Context.MODE_PRIVATE)

    fun getBooleanValue(key: String, default: Boolean): Boolean{
        return sharedPreferences.getBoolean(key,default)
    }

    fun setBooleanValue(key: String, value: Boolean){
        with (sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getStringValue(key: String, default: String?): String?{
        return sharedPreferences.getString(key,default)
    }

    fun setStringValue(key: String, value: String){
        with (sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

}
object Preference{
    const val CONNECT_NEARBY = "connect_nearby"
    const val LOCAL_USERNAME = "local_username"
}
