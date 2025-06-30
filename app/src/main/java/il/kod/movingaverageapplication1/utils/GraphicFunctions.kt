package il.kod.movingaverageapplication1.utils

             import android.graphics.Bitmap
             import android.graphics.BitmapFactory
             import android.graphics.Canvas
             import android.util.Log
             import android.widget.ImageView
             import androidx.core.view.isVisible
             import com.bumptech.glide.Glide
             import com.bumptech.glide.load.DataSource
             import com.bumptech.glide.load.engine.GlideException
             import com.bumptech.glide.request.RequestListener
             import com.bumptech.glide.request.target.CustomTarget
             import com.bumptech.glide.request.target.Target
             import com.bumptech.glide.request.transition.Transition
             import il.kod.movingaverageapplication1.R
             import il.kod.movingaverageapplication1.databinding.StockLayoutBinding
             import androidx.core.graphics.createBitmap

fun createCombinedImage( // for 4,3, or 2 images
    imageView: ImageView,
    uris: List<String>, // List 2-4 uris
    binding: StockLayoutBinding
): Bitmap {

    var combinedBitmap : Bitmap = BitmapFactory.decodeResource(binding.stockImage.context.resources, R.drawable.followseticon) ?: createBitmap(200, 200) // Default bitmap to avoid null

    binding.pictureProgressBar.isVisible=true

    val context = imageView.context



    val bitmaps = mutableListOf<Bitmap?>()
    var arrangedUris: MutableList<String> = uris.toMutableList()

    if (uris.size == 3) {
        arrangedUris.add(context.getString(R.string.placeholder_uri))
    } else if (uris.size == 2) {
        arrangedUris.add(context.getString(R.string.placeholder_uri))
        arrangedUris.add(context.getString(R.string.placeholder_uri))
    }

    arrangedUris.forEach { uri ->
        if (uri == context.getString(R.string.placeholder_uri)) {
            val placeholderBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.login)
            if (placeholderBitmap != null) {
                bitmaps.add(placeholderBitmap)
            } else {
                Log.d("createCombinedImage", "placeholderBitmap is null")
            }
        } else {
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .error(R.mipmap.button_follow_set)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("createCombinedImage", "onLoadFailed: ${e?.message}")
                        binding.pictureProgressBar.isVisible=false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        model: Any,
                        target: Target<Bitmap>,
                        dataSource: DataSource,
                        isFirstResource: Boolean

                    ): Boolean {
                        binding.pictureProgressBar.isVisible=false
                        return false
                    }
                })
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        bitmaps.add(resource)
                        if (bitmaps.size == 4 && bitmaps.all { it != null }) {
                            combinedBitmap = combineBitmaps(bitmaps, 200)

                            imageView.setImageBitmap(combinedBitmap)

                        }
                    }


                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
                })
        }
    }

    return combinedBitmap // Return a default bitmap if no images are loaded
}



private fun combineBitmaps(bitmaps: List<Bitmap?>, targetSquareSide: Int): Bitmap {
    val resizedBitmaps = bitmaps.map { bitmap ->
        bitmap?.let {
            Bitmap.createScaledBitmap(it, targetSquareSide, targetSquareSide, true)
        }
    }

    val combinedBitmap =
        Bitmap.createBitmap(targetSquareSide * 2, targetSquareSide * 2, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(combinedBitmap)

    resizedBitmaps[0]?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    resizedBitmaps[1]?.let { canvas.drawBitmap(it, targetSquareSide.toFloat(), 0f, null) }
    resizedBitmaps[2]?.let { canvas.drawBitmap(it, 0f, targetSquareSide.toFloat(), null) }
    resizedBitmaps[3]?.let {
        canvas.drawBitmap(
            it,
            targetSquareSide.toFloat(),
            targetSquareSide.toFloat(),
            null
        )
    }

    // Create a rounded version of the combined bitmap
    val roundedBitmap =
        createBitmap(targetSquareSide * 2, targetSquareSide * 2)
    val roundedCanvas = Canvas(roundedBitmap)

    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
        shader = android.graphics.BitmapShader(
            combinedBitmap,
            android.graphics.Shader.TileMode.CLAMP,
            android.graphics.Shader.TileMode.CLAMP
        )
    }

    val radius = (targetSquareSide * 2) / 2f
    roundedCanvas.drawCircle(radius, radius, radius, paint)

    return roundedBitmap
}