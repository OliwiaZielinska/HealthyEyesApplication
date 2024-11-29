package com.example.healthyeyes.app.cloudFirestore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * The FirestoreDatabaseFunction class implements the FirestoreInterface interface.
 * Contains methods to add, retrieve, update and delete user data in the Firestore database.
 * @property db - FirebaseFirestore object to interact with the Firestore database.
 */

class FirestoreDatabaseFunction(private val db: FirebaseFirestore) : FirestoreInterface {

    /**
     * Function to add a new user to the Firestore database, using the
     * coroutine mechanism that allows asynchronous operations to be performed.
     *
     * @param userId Unique identifier of the new user.
     * @param user An object of class UserDatabase to be added to the database.
     */
    override suspend fun addUser(userId: String, user: UserDatabase) {
        try {
            db.collection("users").document(userId).set(user).await()
        }catch (e: Exception) {
            Log.e("addUser", "Błąd podczas dodawania użytkownika: $e")
        }
    }

    /**
     * Function to retrieve user data from the Firestore database, using a
     * coroutine mechanism that allows asynchronous operations to be performed.
     *
     * @param userId Unique identifier of the user whose data is to be retrieved.
     * @return UserDatabase class object corresponding to the user data from the database,
     * or null if a user with the specified identifier does not exist.
     */
    override suspend fun getUser(userId: String): UserDatabase? {
        val snapshot = FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo(FieldPath.documentId(), userId)
            .get()
            .await()

        return snapshot.documents.firstOrNull()?.toObject(UserDatabase::class.java)
    }

    /**
     * Function to update user data with a particular Id in the Firestore database,
     * using a coroutine mechanism that allows asynchronous operations to be performed.
     *
     * @param userId Unique identifier of the user whose data is to be updated.
     * @param updatedUser An object of class UserDatabase with updated data.
     */
    override suspend fun updateUser(userId: String, updatedUser: UserDatabase) {
        try {
            db.collection("users").document(userId).set(updatedUser).await()
        } catch (e: Exception) {
            Log.e("updateUser", "Błąd podczas aktualizacji danych użytkownika: $e")
        }
    }

    /**
     * A function to delete user data from the Firestore database, using a
     * coroutine mechanism that allows asynchronous operations to be performed.
     *
     * @param userId Unique identifier of the user whose data is to be deleted.
     */
    override suspend fun deleteUser(userId: String) {
        try {
            db.collection("users").document(userId).delete().await()
            val user = FirebaseAuth.getInstance().currentUser
            user?.delete()
        } catch (e: Exception) {
            Log.e("deleteUser", "Błąd podczas usuwania użytkownika: $e")
        }
    }
}