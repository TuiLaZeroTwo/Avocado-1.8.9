package net.ccbluex.avocado.utils

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.texture.TextureUtil
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.LinkedHashMap
import kotlin.math.exp
import kotlin.math.sqrt
import kotlin.math.PI

object GlowUtils {

    private const val MAX_CACHE = 40

    private val cache = object : LinkedHashMap<String, Int>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Int>): Boolean {
            return if (size > MAX_CACHE) {
                GL11.glDeleteTextures(eldest.value)
                true
            } else false
        }
    }

    private fun createKernel(radius: Int): FloatArray {
        val r = radius.coerceAtLeast(2)
        val size = r * 2 + 1
        val kernel = FloatArray(size)
        val sigma = r / 2f
        val sigma22 = 2f * sigma * sigma
        val sqrtPiSigma22 = sqrt((PI * sigma22).toFloat())
        val radius2 = r * r

        var total = 0f
        for (i in -r..r) {
            val distance = (i * i).toFloat()
            if (distance > radius2) {
                kernel[i + r] = 0f
            } else {
                val value = exp((-distance / sigma22).toDouble()).toFloat() / sqrtPiSigma22
                kernel[i + r] = value
                total += value
            }
        }

        for (i in kernel.indices)
            kernel[i] /= total

        return kernel
    }

    private fun blur(image: BufferedImage, radius: Int): BufferedImage {
        val width = image.width
        val height = image.height
        val kernel = createKernel(radius)
        val r = radius

        val temp = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val output = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                var a = 0f
                var red = 0f
                var green = 0f
                var blue = 0f

                for (k in -r..r) {
                    val px = (x + k).coerceIn(0, width - 1)
                    val rgb = image.getRGB(px, y)
                    val weight = kernel[k + r]

                    a += ((rgb ushr 24) and 0xFF) * weight
                    red += ((rgb ushr 16) and 0xFF) * weight
                    green += ((rgb ushr 8) and 0xFF) * weight
                    blue += (rgb and 0xFF) * weight
                }

                temp.setRGB(
                    x, y,
                    ((a.toInt().coerceIn(0, 255) shl 24) or
                            (red.toInt().coerceIn(0, 255) shl 16) or
                            (green.toInt().coerceIn(0, 255) shl 8) or
                            blue.toInt().coerceIn(0, 255))
                )
            }
        }

        for (y in 0 until height) {
            for (x in 0 until width) {
                var a = 0f
                var red = 0f
                var green = 0f
                var blue = 0f

                for (k in -r..r) {
                    val py = (y + k).coerceIn(0, height - 1)
                    val rgb = temp.getRGB(x, py)
                    val weight = kernel[k + r]

                    a += ((rgb ushr 24) and 0xFF) * weight
                    red += ((rgb ushr 16) and 0xFF) * weight
                    green += ((rgb ushr 8) and 0xFF) * weight
                    blue += (rgb and 0xFF) * weight
                }

                output.setRGB(
                    x, y,
                    ((a.toInt().coerceIn(0, 255) shl 24) or
                            (red.toInt().coerceIn(0, 255) shl 16) or
                            (green.toInt().coerceIn(0, 255) shl 8) or
                            blue.toInt().coerceIn(0, 255))
                )
            }
        }

        return output
    }
    fun drawGlow(
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        blurRadius: Int,
        color: Color
    ) {
        if (width <= 0f || height <= 0f) return

        val radius = blurRadius.coerceIn(4, 40)

        val texW = (width + radius * 2).toInt().coerceAtLeast(1)
        val texH = (height + radius * 2).toInt().coerceAtLeast(1)

        val key = texW.toString() + "x" + texH + "_r" + radius + "_" + color.rgb

        val texture = cache.getOrPut(key) {

            val image = BufferedImage(texW, texH, BufferedImage.TYPE_INT_ARGB)
            val g = image.createGraphics()
            g.color = color
            g.fillRect(radius, radius, width.toInt(), height.toInt())
            g.dispose()

            val blurred = blur(image, radius)

            val id = TextureUtil.glGenTextures()
            TextureUtil.uploadTextureImageAllocate(id, blurred, true, false)

            GlStateManager.bindTexture(id)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)

            id
        }
        GlStateManager.pushMatrix()

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glDisable(GL11.GL_CULL_FACE)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_TEXTURE_2D)

        GlStateManager.bindTexture(texture)
        GlStateManager.color(1f, 1f, 1f, 1f)

        val drawX = x - radius
        val drawY = y - radius

        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(drawX, drawY)
        GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(drawX, drawY + texH)
        GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(drawX + texW, drawY + texH)
        GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(drawX + texW, drawY)
        GL11.glEnd()

        GlStateManager.resetColor()
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_CULL_FACE)

        GlStateManager.popMatrix()
        GlStateManager.bindTexture(0)
    }

    fun clearCache() {
        cache.values.forEach { GL11.glDeleteTextures(it) }
        cache.clear()
    }
}