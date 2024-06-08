package com.cramsan.edifikana.client.lib.di

import androidx.lifecycle.ViewModel

expect fun <VM : ViewModel> retrieveViewModel(): VM
