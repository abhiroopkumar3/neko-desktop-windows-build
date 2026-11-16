package kotlinx.parcelize

/** Marker only; there is no Parcel serialization on desktop (state is saved through other means). */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Parcelize

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class IgnoredOnParcel
