package com.manoelh.task.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.manoelh.task.R
import com.manoelh.task.business.UserBusiness
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.UserConstants
import com.manoelh.task.entity.UserEntity
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException
import com.squareup.picasso.Picasso
import java.io.File
import java.lang.Exception

private const val TAG = "UserRepository"
private const val IMAGE_PREFIX = "images"
private const val IMAGE_SUFFIX = "jpg"

class UserRepository(val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mUserBusiness = UserBusiness(context)
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val mSecurityPreferences = SecurityPreferences(context)

    fun userAuthentication( email: String, password: String, callback: (FirebaseUser?) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, context.getString(R.string.login_successful))
                    val user = auth.currentUser
                    callback(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, context.getString(R.string.login_unsuccessful), task.exception)
                    Toast.makeText(context, context.getString(R.string.message_wrong_email_or_password),
                        Toast.LENGTH_SHORT).show()
                    callback(null)
                }
            }
    }

    fun insertIntoFirebaseAuthenticationSystem(userEntity: UserEntity): MutableLiveData<FirebaseUser>{

        val userEntityMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()

        try {
            mUserBusiness.validateRegisterUser(userEntity.name, userEntity.email, userEntity.password)

            auth.createUserWithEmailAndPassword(userEntity.email, userEntity.password)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, context.getString(R.string.login_successful))
                        val user = auth.currentUser
                        val userId = user!!.uid
                        userEntity.id = userId
                        mUserBusiness.saveSharedPreferencesUser(userEntity)
                        userEntityMutableLiveData.postValue(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, context.getString(R.string.login_unsuccessful), authResult.exception)
                        Toast.makeText(context, context.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show()
                        userEntityMutableLiveData.postValue(null)
                    }
                }
        }catch (ve: ValidationException){
            Toast.makeText(context, ve.message, Toast.LENGTH_LONG).show()
            userEntityMutableLiveData.postValue(null)
        }
        return userEntityMutableLiveData
    }

    fun insertUser(isAuthenticated: Boolean, userEntity: UserEntity): MutableLiveData<UserEntity>{

        val userEntityMutableLiveData: MutableLiveData<UserEntity> = MutableLiveData()

        if (isAuthenticated){
            val userDatabase =
                hashMapOf(
                    DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.NAME to userEntity.name,
                    DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.AUTHENTICATION_ID to userEntity.id)
            db.collection(DatabaseConstants.COLLECTIONS.USERS.COLLECTION_NAME)
                .add(userDatabase)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG,
                        context.getString(R.string.user_added_log) + documentReference.id)
                    Toast.makeText(context, context.getString(R.string.user_saved_message), Toast.LENGTH_LONG).show()
                    userEntityMutableLiveData.postValue(userEntity)
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, context.getString(R.string.adding_error_user), e)
                    userEntityMutableLiveData.postValue(null)
                }
        }
        return userEntityMutableLiveData
    }

    fun updateUserPassword(userEntity: UserEntity): MutableLiveData<String>{
        val idUser: MutableLiveData<String> = MutableLiveData()
        try {
            mUserBusiness.validateUpdateUser(userEntity.name, userEntity.password)

            val user = auth.currentUser

            user?.updatePassword(userEntity.password)
                ?.addOnSuccessListener {
                    updateUsername(userEntity)
                    Log.d(TAG, context.getString(R.string.user_updated))
                    Toast.makeText(context, context.getString(R.string.user_updated), Toast.LENGTH_LONG).show()
                    idUser.postValue(userEntity.id)
                }
                ?.addOnFailureListener {
                    Toast.makeText(context, context.getString(R.string.error_update_user), Toast.LENGTH_LONG).show()
                    idUser.postValue(null)
                }
        }catch (ve: ValidationException){
            Toast.makeText(context, ve.message, Toast.LENGTH_LONG).show()
            idUser.postValue(null)
        }
        return idUser
    }

    private fun updateUsername(userEntity: UserEntity){

        db.collection(DatabaseConstants.COLLECTIONS.USERS.COLLECTION_NAME).document(userEntity.id)
            .update(mapOf(DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.NAME to userEntity.name))
            .addOnSuccessListener {
                Log.d(TAG, context.getString(R.string.username_updated) + userEntity.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, context.getString(R.string.error_update_username), e)
                Toast.makeText(context, context.getString(R.string.error_update_username), Toast.LENGTH_LONG).show()
            }
    }

    fun getUserName(): MutableLiveData<String>{
        val userName: MutableLiveData<String> = MutableLiveData()

        try {
            db.collection(DatabaseConstants.COLLECTIONS.USERS.COLLECTION_NAME)
                .whereEqualTo(
                    DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.AUTHENTICATION_ID,
                    mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val name = document.get(DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.NAME).toString()
                        userName.postValue(name)
                        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, name)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, context.getString(R.string.error_getting_user_name), exception)
                }

        }catch (e: Exception){
            throw e
        }
        return userName
    }

    fun uploadPhoto(file: File?, uri: Uri?){

        val storageReference = storage.reference
        val profilePhotoReference = storageReference.child(UserConstants.PROFILE_PHOTO.returnProfilePhotoReference(context))
        val uploadTask: UploadTask
        if(file == null)
            uploadTask = profilePhotoReference.putFile(uri!!)
        else
            uploadTask = profilePhotoReference.putFile(file.toUri())
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            Toast.makeText(context, context.getString(R.string.upload_failed), Toast.LENGTH_LONG).show()
            Log.e(TAG, "Error: ${it.message}")
        }.addOnSuccessListener {
            Toast.makeText(context, context.getString(R.string.image_uploaded), Toast.LENGTH_LONG).show()
        }
    }

    fun downloadPhoto(imageViewProfile: ImageView) {
        val storageReference = FirebaseStorage.getInstance().reference
        val path = UserConstants.PROFILE_PHOTO.returnProfilePhotoReference(context)
        val profilePhotoReference = storageReference.child(path)

        val localFile = File.createTempFile(IMAGE_PREFIX, IMAGE_SUFFIX)

        profilePhotoReference.getFile(localFile).addOnSuccessListener {
            val downloadURL = it.storage.downloadUrl
            downloadURL.addOnCompleteListener { task->
                val image = task.result
                Picasso.with(context).load(image).into(imageViewProfile)
                imageViewProfile.rotation = 270f
            }.addOnFailureListener { exception ->
                Log.e(TAG, exception.message!!)
            }
        }.addOnFailureListener {
            Log.e(TAG, it.message!!)
        }

    }
}