package com.example.socialmediaapplicaition.di

import com.example.socialmediaapplicaition.repository.AuthData.AuthRepository
import com.example.socialmediaapplicaition.repository.AuthData.AuthRepositoryImpl
import com.example.socialmediaapplicaition.repository.firebaseData.FirebaseRepository
import com.example.socialmediaapplicaition.repository.firebaseData.FirebaseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl


    @Provides
    fun providePostRepository(impl:FirebaseRepositoryImpl): FirebaseRepository = impl

    @Provides
    @Singleton
    fun provideFireStoreInstance(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorageInstance(): FirebaseStorage = FirebaseStorage.getInstance()


}