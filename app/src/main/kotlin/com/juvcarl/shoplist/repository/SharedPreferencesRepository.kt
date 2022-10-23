package com.juvcarl.shoplist.repository

import android.content.Context
import com.juvcarl.shoplist.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesRepositoryImpl @Inject constructor(
    val context: Context
): SharedPrefenceRepository{

    val sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences_global), Context.MODE_PRIVATE)




}