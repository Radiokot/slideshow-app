package ua.com.radiokot.slideshowapp.creative.domain

class Creative(
    val key: String,
    val contentType: Type,
    val properties: Map<String, String>,
) {
    val soundVolumePercent: Float?
        get() = properties["soundVolume"]?.toFloatOrNull()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Creative) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun toString(): String {
        return "Creative(key='$key', contentType=$contentType)"
    }

    enum class Type {
        Image,
        Video,
        ;
    }
}
