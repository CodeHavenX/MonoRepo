package com.cramsan.framework.sample.jbcompose.mpplib

import com.cramsan.minesweepers.android.ui.AndroidFileReader
import com.cramsan.minesweepers.common.FileReader
import com.cramsan.minesweepers.common.StatementManager
import com.cramsan.minesweepers.common.VerbProvider
import com.cramsan.minesweepers.common.main.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFileReader(): FileReader {
        return AndroidFileReader()
    }
    @Provides
    fun provideVerbProvider(
        fileReader: FileReader,
    ): VerbProvider {
        return VerbProvider(
            fileReader
        )
    }

    @Provides
    fun provideStatementManager(
        verbProvider: VerbProvider,
    ): StatementManager {
        return StatementManager(
            verbProvider
        )
    }

    @Provides
    fun provideMainViewModel(
        statementManager: StatementManager,
    ): MainViewModel {
        return MainViewModel(
            statementManager,
        )
    }
}
