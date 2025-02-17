package com.example.desiregallery.ui.dialogs

import androidx.appcompat.app.AlertDialog
import com.example.desiregallery.R
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_change_password.*

@Deprecated("Restore password via email")
class ChangePasswordDialog(private val activity: Activity) : AlertDialog(activity) {
    private var currentPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val content = View.inflate(context, R.layout.dialog_change_password, null)
        setView(content)
        setTitle(R.string.change_password)
        setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.OK)) { _, _ -> handleConfirm() }
        setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.Cancel)) { _, _ -> handleCancel() }
        setCancelable(false)

        //currentPassword = (activity as MainActivity).getCurrUser()?.password

        super.onCreate(savedInstanceState)
    }

    private fun handleConfirm() {
        if (dialog_password_current.text.isEmpty()
            || dialog_password_new.text.isEmpty()
            || dialog_password_confirm.text.isEmpty()) {
            Toast.makeText(activity, R.string.fields_are_empty, Toast.LENGTH_LONG).show()
            return
        }
        if (dialog_password_current.text.toString() != currentPassword) {
            Toast.makeText(activity, R.string.current_password_wrong, Toast.LENGTH_LONG).show()
            return
        }
        if (dialog_password_new.text.toString() != dialog_password_confirm.text.toString()) {
            Toast.makeText(activity, R.string.non_equal_passwords, Toast.LENGTH_LONG).show()
            return
        }
        updatePassword()
        Toast.makeText(activity, R.string.password_changed, Toast.LENGTH_LONG).show()
        dismiss()
    }

    private fun updatePassword() {
        /*val user = (activity as MainActivity).getCurrUser()
        user?.let {
            it.password = dialog_password_new.text.toString()
            DGDatabase.updateUser(it)
            DGNetwork.getBaseService().updateUser(it.getLogin(), it).enqueue(object: Callback<VKUser> {
                override fun onFailure(call: Call<VKUser>, t: Throwable) {
                    Log.e(TAG, "Unable to update user ${it.getLogin()}: ${t.message}")
                }

                override fun onResponse(call: Call<VKUser>, response: Response<VKUser>) {
                    if (response.isSuccessful)
                        Log.i(TAG, "VKUser ${it.getLogin()} has been successfully updated")
                }
            })
        }*/
    }

    private fun handleCancel() = dismiss()
}