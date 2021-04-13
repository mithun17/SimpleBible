package com.mithun.simplebible.di

import android.content.Context
import com.mithun.simplebible.utilities.Prefs
import com.mithun.simplebible.utilities.ResourcesUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {

    @Provides
    @Singleton
    fun provideStringResource(@ApplicationContext context: Context): ResourcesUtil {
        return ResourcesUtil(context)
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): Prefs {
        return Prefs(context)
    }
}
