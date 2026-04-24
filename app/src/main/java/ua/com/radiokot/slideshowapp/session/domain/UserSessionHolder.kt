package ua.com.radiokot.slideshowapp.session.domain

interface UserSessionHolder {
    fun set(session: UserSession)
    fun clear()
}
