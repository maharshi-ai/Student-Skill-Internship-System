package com.college.internship.ui.anim;

/**
 * Standard easing functions for natural, fluid UI motion curves.
 */
public final class Easing {

    private Easing() {}

    public static float linear(float t) {
        return Math.max(0f, Math.min(1f, t));
    }

    public static float easeOutCubic(float t) {
        t = Math.max(0f, Math.min(1f, t));
        float inv = 1f - t;
        return 1f - (inv * inv * inv);
    }

    public static float easeInOutCubic(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }

    public static float easeOutQuart(float t) {
        t = Math.max(0f, Math.min(1f, t));
        float inv = 1f - t;
        return 1f - (inv * inv * inv * inv);
    }

    public static float easeOutBack(float t) {
        t = Math.max(0f, Math.min(1f, t));
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
    }
}
