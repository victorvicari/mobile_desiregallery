package com.example.desiregallery.presenters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.example.desiregallery.Utils
import com.example.desiregallery.logging.DGLogger
import com.example.desiregallery.models.Post
import com.example.desiregallery.network.DGNetwork
import com.example.desiregallery.ui.activities.CommentsActivity
import com.example.desiregallery.ui.activities.FullScreenImageActivity
import com.example.desiregallery.ui.views.PostView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostPresenter(
    private val view: PostView,
    private val post: Post = Post()
) {
    companion object {
        private val TAG = PostPresenter::class.java.simpleName
    }

    fun setRating() {
        view.updateRating(post.rating)
    }

    fun updateRating(rate: Float) {
        post.updateRating(rate)
        view.updateRating(post.rating)
        DGNetwork.getBaseService().updatePost(post.id, post).enqueue(object: Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                DGLogger.logError(TAG, "Unable to update post ${post.id}: ${t.message}")
            }

            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful)
                    DGLogger.logInfo(TAG, "Post ${post.id} has been successfully updated")
            }
        })
    }

    fun setImageView() {
        view.updateImage(post.imageUrl.toString())
    }

    fun setAuthorName() {
        view.updateAuthorName(post.author.login)
    }

    fun setAuthorPhoto() {
        view.updateAuthorPhoto(post.author.photo)
    }

    fun goToCommentActivity(context: Context) {
        val intent = Intent(context, CommentsActivity::class.java).apply { putExtra(CommentsActivity.EXTRA_POST, post) }
        context.startActivity(intent)
    }

    fun goToImageFullScreen(context: Context, bmpImage: Bitmap) {
        val intent = Intent(context, FullScreenImageActivity::class.java).apply {
            putExtra(FullScreenImageActivity.EXTRA_IMAGE, Utils.bitmapToBytes(bmpImage))
            putExtra(FullScreenImageActivity.EXTRA_POST_ID, post.id)
        }
        context.startActivity(intent)
    }
}
