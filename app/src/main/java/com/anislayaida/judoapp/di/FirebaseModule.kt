package com.anislayaida.judoapp.di

import com.anislayaida.judoapp.data.AuthRepo
import com.anislayaida.judoapp.data.AuthRepository
import com.anislayaida.judoapp.data.technique.TechniqueDao
import com.anislayaida.judoapp.data.technique.TechniqueRepo
import com.anislayaida.judoapp.data.technique.TechniqueRepository
import com.anislayaida.judoapp.data.technique.TechniqueSeeder
import com.anislayaida.judoapp.data.user.UserDao
import com.anislayaida.judoapp.data.user.UserRepo
import com.anislayaida.judoapp.data.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserDao(firestore: FirebaseFirestore): UserDao = UserDao(firestore)

    @Provides
    @Singleton
    fun provideUserRepo(dao: UserDao): UserRepo = UserRepository(dao)

    @Provides
    @Singleton
    fun provideTechniqueDao(firestore: FirebaseFirestore): TechniqueDao = TechniqueDao(firestore)

    @Provides
    @Singleton
    fun provideTechniqueRepo(dao: TechniqueDao): TechniqueRepo = TechniqueRepository(dao)

    @Provides
    @Singleton
    fun provideAuthRepo(
        auth: FirebaseAuth,
        userRepo: UserRepo
    ): AuthRepo = AuthRepository(auth, userRepository = userRepo)

    @Provides
    @Singleton
    fun provideTechniqueSeeder(
        firestore: FirebaseFirestore
    ): TechniqueSeeder = TechniqueSeeder(firestore)
}