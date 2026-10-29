package com.college.internship.ui.anim;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Animated number interpolator for dashboard metrics and KPI cards.
 * Smoothly counts from 0 to the actual database value using an ease-out curve.
 */
public class AnimatedCounter {

    private final double targetValue;
    private final int durationMs;
    private final String format;
    private final Consumer<String> onUpdate;
    private Timer timer;

    public AnimatedCounter(double targetValue, int durationMs, String format, Consumer<String> onUpdate) {
        this.targetValue = targetValue;
        this.durationMs = Math.max(100, durationMs);
        this.format = format;
        this.onUpdate = onUpdate;
    }

    public static void animateTo(JLabel label, double targetValue, String format) {
        animateTo(label, targetValue, 650, format);
    }

    public static void animateTo(JLabel label, double targetValue, int durationMs, String format) {
        AnimatedCounter counter = new AnimatedCounter(targetValue, durationMs, format, label::setText);
        counter.start();
    }

    public void start() {
        if (targetValue == 0) {
            onUpdate.accept(String.format(format, 0.0));
            return;
        }

        long startTime = System.currentTimeMillis();
        timer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float rawT = (float) elapsed / (float) durationMs;

            if (rawT >= 1f) {
                timer.stop();
                onUpdate.accept(String.format(format, targetValue));
            } else {
                float eased = Easing.easeOutCubic(rawT);
                double current = targetValue * eased;
                onUpdate.accept(String.format(format, current));
            }
        });

        timer.start();
    }

    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }
}
