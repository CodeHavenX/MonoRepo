package com.cramsan.edifikana.client.lib.ui.di

import io.github.jan.supabase.coil.Coil3Integration

/**
 * Provides the Coil3Integration to be used in the application. Allows a level of indirection to allow for
 * different implementations of the Coil3Integration.
 */
class Coil3Provider(val coil3Integration: Coil3Integration)
