package com.example.socialmediaapplicaition.utils

import android.content.Context
import com.example.socialmediaapplicaition.utils.Constants.PREFS_TOKEN_FILE
import com.example.socialmediaapplicaition.utils.Constants.USER_ID
import com.example.socialmediaapplicaition.utils.Constants.USER_NAME
import com.example.socialmediaapplicaition.utils.Constants.USER_PROFILE
import com.example.socialmediaapplicaition.utils.Constants.USER_STATUS

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject



class TokenManager @Inject constructor(@ApplicationContext context:Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE,Context.MODE_PRIVATE)

    fun saveId(id:String){
        val editor = prefs.edit()
        editor.putString(USER_ID,id)
        editor.apply()
    }

    fun getId():String?{
        return prefs.getString(USER_ID,"")
    }


    fun saveProfile(profileUrl:String){
        val editor = prefs.edit()
        editor.putString(USER_PROFILE,profileUrl)
        editor.apply()
    }

    fun getProfile():String?{
        return prefs.getString(USER_PROFILE,"")
    }

    fun saveStatus(userStatus:String){
        val editor = prefs.edit()
        editor.putString(USER_STATUS,userStatus)
        editor.apply()
    }

    fun getStatus():String?{
        return  prefs.getString(USER_STATUS,"")

    }


    fun saveUserName(name:String){
        val editor = prefs.edit()
        editor.putString(USER_NAME,name)
        editor.apply()
    }

    fun getUserName():String?{
        return prefs.getString(USER_NAME,"")
    }



}