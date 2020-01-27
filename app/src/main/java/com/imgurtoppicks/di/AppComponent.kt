package com.imgurtoppicks.di

import android.content.Context
import com.imgurtoppicks.ui.search.SearchTopPicsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DataModule::class, AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun appContext(appContext: Context): Builder

        fun build(): AppComponent
    }

    fun viewModelFactory(): ViewModelFactory<SearchTopPicsViewModel>

}

