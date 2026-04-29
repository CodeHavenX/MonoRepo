package com.cramsan.framework.annotations

/** Marks a class as a back-end Controller (top of the Controller → Service → Datastore stack). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BackendController

/** Marks a class as a back-end Service (middle layer; delegates to Datastores). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BackendService

/** Marks a class as a back-end Datastore (bottom layer; owns data access). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BackendDatastore

/** Marks a class as a front-end ViewModel (top of the ViewModel → Manager → Service stack). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FrontendViewModel

/** Marks a class as a front-end Manager (middle layer; coordinates one or more Services). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FrontendManager

/** Marks a class as a front-end Service (bottom layer; handles API/network calls). */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class FrontendService
