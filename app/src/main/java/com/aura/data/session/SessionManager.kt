package com.aura.data.session

/**
 * This object is used to manage the session of the user.
 * This version only stock the user identifier.
 *
 * With the development of the API it should also stock the token of the session
 * or use a datastore for local persistence.
 *
 */
object SessionManager {

    private var currentUserId: String? = null

    // TODO: Add a token when API expose it

    // TODO: Add personal info such as first name, last name etc when API expose it

    // TODO: Add a datastore for local persistence

    /**
     * Initialize the session after a succesful login
     *
     * @param identifier The identifier of the user
     */
    fun startSession(identifier: String) {
        currentUserId = identifier
    }

    /**
     * Check if the user is connected
     * For when the the session can be maintained with the data store and the user can go directly to home screen.
     *
     * @return True if the user is connected, false otherwise
     */
    fun isConnected(): Boolean = currentUserId != null


    /**
     * Get the current user identifier
     *
     * @return The current user identifier
     */
    fun getCurrentUserId(): String? = currentUserId

    /**
     * End the current session (disconnection)
     */
    fun clearSession() {
        currentUserId = null

    }

}