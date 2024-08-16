package com.codehavenx.alpaca.backend.storage.entity

@RequiresOptIn(message = "This class should only be used for Supabase models.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SupabaseModel
