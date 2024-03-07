package com.example.socialmediaapplicaition.di

import com.example.socialmediaapplicaition.data.AuthRepository
import com.example.socialmediaapplicaition.data.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {


    @Provides
    fun provideFirebaseAuth():FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl:AuthRepositoryImpl):AuthRepository = impl


    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()



}