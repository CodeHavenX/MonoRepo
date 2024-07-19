package com.cramsan.edifikana.lib.supa

@RequiresOptIn(message = "This class should only be used for Supabase models.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SupabaseModel
