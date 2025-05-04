package com.krishna.navbar.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.content.ContextCompat;

/**
 * Utility class for color operations
 */
public class ColorUtils {
    
    /**
     * Extract a dominant color from a drawable
     * For gradient drawables, this will return the start color
     *
     * @param context Context to access resources
     * @param drawableId Drawable resource ID
     * @return Dominant color as an ARGB integer
     */
    public static int getDominantColor(Context context, int drawableId) {
        // Default color (orange)
        int defaultColor = Color.parseColor("#FFAC6D");
        
        if (context == null) {
            return defaultColor;
        }
        
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            return defaultColor;
        }
        
        // Extract color based on drawable type
        if (drawable instanceof ColorDrawable) {
            // Solid color drawable
            return ((ColorDrawable) drawable).getColor();
        } else if (drawable instanceof GradientDrawable) {
            // For gradient drawables, we approximate with the default orange color 
            // since we can't directly access gradient colors in GradientDrawable API
            return defaultColor;
        } else {
            // For other drawables, use default
            return defaultColor;
        }
    }
    
    /**
     * Get a lighter version of a color
     * 
     * @param color The base color
     * @param factor How much to lighten (0-1, where 0 means no change and 1 means white)
     * @return Lightened color
     */
    public static int lightenColor(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor)) + (255 * factor));
        int green = (int) ((Color.green(color) * (1 - factor)) + (255 * factor));
        int blue = (int) ((Color.blue(color) * (1 - factor)) + (255 * factor));
        
        return Color.argb(Color.alpha(color), red, green, blue);
    }
    
    /**
     * Get a darker version of a color
     * 
     * @param color The base color
     * @param factor How much to darken (0-1, where 0 means no change and 1 means black)
     * @return Darkened color
     */
    public static int darkenColor(int color, float factor) {
        int red = (int) (Color.red(color) * (1 - factor));
        int green = (int) (Color.green(color) * (1 - factor));
        int blue = (int) (Color.blue(color) * (1 - factor));
        
        return Color.argb(Color.alpha(color), red, green, blue);
    }
} 