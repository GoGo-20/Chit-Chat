package com.example.chitchat.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.PermissionChecker.PERMISSION_DENIED
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.fragment.app.Fragment
import com.example.chitchat.ModelClasses.users
import com.example.chitchat.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment : Fragment() {

    var UserReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    private var RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""
    private var socialChecker: String? = ""

    private val PERMISSION_REQUEST = 10

    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser =  FirebaseAuth.getInstance().currentUser
        UserReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("user images")

        UserReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val users: users? = snapshot.getValue(users::class.java)

                    if (context!=null){

                        view.username_settings.text = users!!.getUserName()
                        Picasso.get().load(users.getProfile()).into(view.profile_image)
                        Picasso.get().load(users.getCover()).into(view.image_cover)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        view.profile_image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(context, permissions)) {
               Toast.makeText(context, "Permissions Already Provided", Toast.LENGTH_LONG).show()
                pickImages()
            }else {

                   requestPermissions(permissions, PERMISSION_REQUEST)
               }
            }else{
                Toast.makeText(context, "Permissions Already Provided", Toast.LENGTH_LONG).show()
                pickImages()
            }

         }

        view.image_cover.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(context, permissions)) {
                    pickImages()
                }else {

                    requestPermissions(permissions, PERMISSION_REQUEST)
                }
            }else{
                pickImages()
            }


            coverChecker = "Cover"
        }

        view.set_facebook.setOnClickListener {
            socialChecker = "facebook"
            setSocialLinks()
        }

        view.set_instagram.setOnClickListener {
            socialChecker = "instagram"
            setSocialLinks()
        }

        view.set_snapchat.setOnClickListener {
            socialChecker = "snapchat"
            setSocialLinks()
        }

        return view
    }

    private fun setSocialLinks() {

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(context, R.style.Theme_AppCompat_DayNight_Dialog_Alert)

        if (socialChecker == "snapchat"){
            builder.setTitle("Write Snap ID: ")
        }else{
            builder.setTitle("Write username: ")
        }

        val editText = EditText(context)

        if (socialChecker == "snapchat"){
            editText.hint = "e.g. gaurank2000"

        }else if (socialChecker == "facebook"){
            editText.hint = "e.g. gaurank.maheshwari"

        }else{
            editText.hint = "e.g. _gogo__20 "
        }

        builder.setView(editText)

        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
            dialog, which ->
            var str = editText.text.toString()

            if(str == "")
            {
                Toast.makeText(context, "Please Write Something", Toast.LENGTH_LONG).show()
            }
            else{
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener {
                dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        var mapSocial = HashMap<String, Any>()


        when(socialChecker){
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "snapchat" -> {
                mapSocial["snapchat"] = "https://m.snapchat.com/$str"
            }
        }
        UserReference!!.updateChildren(mapSocial).addOnCompleteListener {
            task ->
            if (task.isSuccessful)
            {
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun pickImages() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            Toast.makeText(context, "UPLOADING......", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    fun checkPermission(context: Context?, permissionArray: Array<String>): Boolean{
        var allSuccess = true
        for (i in permissionArray.indices){
            if (context?.let { checkCallingOrSelfPermission(it, permissionArray[i]) } == PackageManager.PERMISSION_DENIED){

                allSuccess = false

            }
            return allSuccess
        }
       return false
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Image is Uploading, Please WAIT......")
        progressBar.show()

        if (imageUri != null)
        {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask (Continuation<UploadTask.TaskSnapshot, Task<Uri>> {task ->
                if (!task.isSuccessful){
                    task.exception.let {
                        throw it!!
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    var downloadUrl = task.result
                    var url = downloadUrl.toString()

                    if (coverChecker == "Cover")
                    {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        UserReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else{
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["profile"] = url
                        UserReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (RequestCode == PERMISSION_REQUEST){
            var allSuccess = true

            for(i in permissions.indices){
                if (grantResults[i] == PERMISSION_DENIED){
                    allSuccess = false

                    var RequestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if(RequestAgain){
                        Toast.makeText(context, "Permissions DENIED", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(context, "Go to settings and enable the Permissions", Toast.LENGTH_LONG).show()
                    }
                }
            }
            if (allSuccess){
                Toast.makeText(context, "Permissions Granted", Toast.LENGTH_LONG).show()
            }
        }
    }

}