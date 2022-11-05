package com.juvcarl.shoplist.util

import com.juvcarl.shoplist.repository.Preference
import com.juvcarl.shoplist.repository.SharedPrefencesRepository
import java.util.*
import javax.inject.Inject

class IdentityUtils @Inject constructor(
    private val sharedPrefencesRepository: SharedPrefencesRepository
) {

    fun getLocalUserName(): String {
        return sharedPrefencesRepository.getStringValue(Preference.LOCAL_USERNAME,null) ?: saveNewLocalUserName()
    }

    private fun saveNewLocalUserName(): String{
        val userName = generateRandomUserName()
        sharedPrefencesRepository.setStringValue(Preference.LOCAL_USERNAME,userName)
        return userName
    }

    private fun generateRandomUserName(): String{
        var name = ""
        val random = Random()
        for (i in 0..4) {
            name += random.nextInt(10)
        }
        return name
    }
}