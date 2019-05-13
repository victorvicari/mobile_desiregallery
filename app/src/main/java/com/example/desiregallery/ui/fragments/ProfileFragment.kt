package com.example.desiregallery.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.desiregallery.R
import com.example.desiregallery.database.DGDatabase
import com.example.desiregallery.models.User
import com.example.desiregallery.network.DGNetwork
import com.example.desiregallery.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.widget.EditText
import android.widget.ImageView
import com.example.desiregallery.Utils


class ProfileFragment : Fragment() {
    private val TAG = ProfileFragment::class.java.simpleName
    private val REQUEST_IMAGE_CAPTURE = 1

    private var infoChanged = false
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val toolbarProfile = root.findViewById<Toolbar>(R.id.profile_toolbar)
        val imageView = root.findViewById<ImageView>(R.id.profile_image_backdrop)
        val fab = root.findViewById<FloatingActionButton>(R.id.profile_fab)

        val user = (activity as MainActivity).getCurrUser()
        Log.d(TAG, user?.getPhoto())
        if (user != null) {
            toolbarProfile.title = user.getLogin()
            fab.setOnClickListener {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity((activity as MainActivity).packageManager)?.also {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }

            val notSpecified = getString(R.string.not_specified)
            root.profile_email.text = if (!user.getEmail().isEmpty()) user.getEmail() else notSpecified
            root.profile_gender.text = if (!user.getGender().isEmpty()) user.getGender() else notSpecified
            root.profile_birthday.text = if (!user.getBirthday().isEmpty()) user.getBirthday() else notSpecified
            if (!user.getPhoto().isEmpty())
                imageView.setImageBitmap(Utils.base64ToBitmap(user.getPhoto()))

            root.profile_email_view.setOnClickListener {
                editEmail()
            }
            root.profile_gender_view.setOnClickListener {
                editGender()
            }
            root.profile_birthday_view.setOnClickListener {
                editBirthday()
            }
        }
        this.user = user
        return root
    }

    override fun onPause() {
        super.onPause()
        if (!infoChanged)
            return

        if (user != null) {
            DGDatabase.updateUser(user!!)
            DGNetwork.getService().updateUser(user!!.getLogin(), user).enqueue(object: Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, "Unable to update user")
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful)
                        Log.i(TAG, "User has been successfully updated")
                }

            })
        }
        infoChanged = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bundle = data.extras
            if (bundle != null) {
                val imageBitmap = bundle.get("data") as Bitmap
                user?.setPhoto(Utils.bitmapToBase64(imageBitmap))
                profile_image_backdrop.setImageBitmap(imageBitmap)
                infoChanged = true
            }
        }
    }

    private fun editEmail() {
        val emailView = EditText(activity)
        emailView.setText(user?.getEmail())

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.email_dialog_title)
        builder.setView(emailView)
        builder.setCancelable(true)
        builder.setPositiveButton(getString(R.string.OK)) { dialog, _ ->
            val email = emailView.text.toString()
            if (email != user?.getEmail()) {
                user?.setEmail(email)
                profile_email.text = email
                infoChanged = true
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        val emailDialog = builder.create()
        emailDialog.show()
    }

    private fun editBirthday() {
        val currBirthday = user?.getBirthday()
        val cal = Calendar.getInstance()
        if (currBirthday != null && !currBirthday.isEmpty()) {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            cal.time = sdf.parse(currBirthday)
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            val birthday = getString(R.string.date_format, dayOfMonth, monthOfYear+1, year)
            if (birthday != user?.getBirthday()) {
                user?.setBirthday(birthday)
                profile_birthday.text = birthday
                infoChanged = true
            }
        }
        DatePickerDialog(context!!,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun editGender() {
        val gender = user?.getGender()
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.gender_dialog_title)
        val values = resources.getStringArray(R.array.gender)
        val checkedItem = if (gender != null && !gender.isEmpty()) values.indexOf(gender) else -1

        builder.setSingleChoiceItems(values, checkedItem) { dialog, item ->
            user?.setGender(values[item])
            profile_gender.text = values[item]
            infoChanged = true
            dialog.dismiss()
        }
        val genderDialog = builder.create()
        genderDialog.show()
    }
}
