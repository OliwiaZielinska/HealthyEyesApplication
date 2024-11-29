package com.example.healthyeyes.app.cloudFirestore
/**
 * FirestoreInterface allows you to define functions related to creating, updating, downloading
 * and deleting users from the Firestore database.
 */
interface FirestoreInterface {

    /**
     * Function to add a new user (new record) to the Firestore database.
     *
     * @param userId Unique identifier of the new user.
     * @param user UserDatabase class object of the new user being added.
     */
    suspend fun addUser(userId: String, user: UserDatabase)

    /**
     * Function to retrieve user information from the database based on the user's Id.
     *
     * @param userId Unique identifier of the user whose data is to be retrieved.
     * @return UserDatabase class object - user data from the database,
     *          or null if a user with the specified ID does not exist.
     */
    suspend fun getUser(userId: String): UserDatabase?

    /**
     * Function to update information about an existing user in the Firestore database.
     *
     * @param userId Unique identifier of the user whose data is to be updated.
     * @param updatedUser UserDatabase class object with updated data.
     */
    suspend fun updateUser(userId: String, updatedUser: UserDatabase)

    /**
     * Function to remove an existing user from the database based on their Id.
     *
     * @param userId Unique identifier of the user whose data is to be deleted.
     */
    suspend fun deleteUser(userId: String)
}