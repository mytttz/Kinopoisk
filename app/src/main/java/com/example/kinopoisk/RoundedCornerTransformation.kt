package com.example.kinopoisk

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import com.squareup.picasso.Transformation

class RoundedCornerTransformation(private val cornerRadius: Float = 20f) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val roundedBitmap = Bitmap.createBitmap(source.width, source.height, source.config)
        val canvas = Canvas(roundedBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        source.recycle()
        return roundedBitmap
    }

    override fun key(): String {
        return "rounded_corner"
    }
}
