package com.codehavenx.alpaca.backend.core.repository.supabase

/**
 * Marks a class as a Supabase model.
 */
@RequiresOptIn(message = "This class should only be used for Supabase models.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class SupabaseModel
