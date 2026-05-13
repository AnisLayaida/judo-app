package com.anislayaida.judoapp

import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.di.FirebaseModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.Mockito
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces   = [FirebaseModule::class]
)
object TestAuthModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        Mockito.mock(FirebaseAuth::class.java)

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore =
        Mockito.mock(FirebaseFirestore::class.java)

    @Provides @Singleton
    fun provideAuthRepo(): AuthRepo =
        Mockito.mock(AuthRepo::class.java)

    @Provides @Singleton
    fun provideUserRepo(): UserRepo =
        Mockito.mock(UserRepo::class.java)

    @Provides @Singleton
    fun provideTechniqueRepo(): TechniqueRepo =
        Mockito.mock(TechniqueRepo::class.java)
}