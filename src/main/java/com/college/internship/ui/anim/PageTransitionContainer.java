package com.college.internship.ui.anim;

import com.college.internship.ui.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * High-performance Page Transition Container for Java Swing.
 * Implements smooth directional slide-and-fade transitions between actual rendered
 * Swing components using a 60 FPS javax.swing.Timer on the Event Dispatch Thread (EDT).
 *
 * Features:
 * - 400 ms fluid transition with cubic ease-out curve (Easing.easeOutCubic)
 * - Noticeable 65px horizontal slide displacement
 * - Smooth alpha opacity blending (0.0 to 1.0)
 * - Preemption & debounce: gracefully finishes in-flight animations on rapid clicks
 * - Proper post-transition layout restoration and keyboard focus
 */
public class PageTransitionContainer extends JPanel {

    public enum TransitionDirection {
        FORWARD(1), BACKWARD(-1), NONE(0);

        public final int sign;
        TransitionDirection(int sign) {
            this.sign = sign;
        }
    }

    private static final int ANIMATION_DURATION_MS = 400; // 400 ms smooth transition
    private static final int TRAVEL_DISTANCE_PX = 65;     // 65 px noticeable horizontal travel

    private JComponent currentComponent;
    private JComponent outgoingComponent;
    private JComponent incomingComponent;

    private TransitionWrapper outgoingWrapper;
    private TransitionWrapper incomingWrapper;

    private Timer animationTimer;
    private long animationStartTime;
    private boolean isTransitioning = false;
    private TransitionDirection activeDirection = TransitionDirection.FORWARD;

    public PageTransitionContainer() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_MAIN);
        setOpaque(true);
    }

    /**
     * Instantly shows a component without animation (useful for initial frame loading).
     */
    public void showImmediate(JComponent component) {
        stopTimer();
        removeAll();
        setLayout(new BorderLayout());
        this.currentComponent = component;
        this.outgoingComponent = null;
        this.incomingComponent = null;
        this.outgoingWrapper = null;
        this.incomingWrapper = null;
        this.isTransitioning = false;

        if (component != null) {
            add(component, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    /**
     * Transitions smoothly to the target component with directional slide-and-fade.
     *
     * @param targetComponent The new panel to display
     * @param direction Forward (slide left from right) or Backward (slide right from left)
     */
    public void transitionTo(JComponent targetComponent, TransitionDirection direction) {
        if (targetComponent == null || targetComponent == currentComponent) {
            return;
        }

        // If not yet visible or zero dimensions, show immediately
        int width = getWidth();
        int height = getHeight();
        if (currentComponent == null || width <= 0 || height <= 0 || direction == TransitionDirection.NONE) {
            showImmediate(targetComponent);
            return;
        }

        // Stop any running animation and ensure clean baseline
        if (isTransitioning) {
            finishActiveAnimationImmediately();
        }

        this.outgoingComponent = currentComponent;
        this.incomingComponent = targetComponent;
        this.activeDirection = direction;
        this.isTransitioning = true;

        // Wrap actual live rendered components in alpha-capable transition wrappers
        this.outgoingWrapper = new TransitionWrapper(outgoingComponent);
        this.incomingWrapper = new TransitionWrapper(incomingComponent);

        removeAll();
        setLayout(null); // Absolute bounds animation

        int sign = direction.sign;
        int startX = (sign >= 0) ? TRAVEL_DISTANCE_PX : -TRAVEL_DISTANCE_PX;

        outgoingWrapper.setBounds(0, 0, width, height);
        outgoingWrapper.setAlpha(1.0f);

        incomingWrapper.setBounds(startX, 0, width, height);
        incomingWrapper.setAlpha(0.0f);

        // Add in order: incoming on top (index 0), outgoing underneath (index 1)
        add(incomingWrapper);
        add(outgoingWrapper);

        // Force validation so components have actual sizes and layout hierarchy
        outgoingWrapper.validate();
        incomingWrapper.validate();
        revalidate();
        repaint();

        // 60 FPS Timer (16 ms interval on Swing EDT)
        this.animationStartTime = System.currentTimeMillis();
        this.animationTimer = new Timer(16, e -> {
            long elapsed = System.currentTimeMillis() - animationStartTime;
            float rawT = (float) elapsed / (float) ANIMATION_DURATION_MS;

            if (rawT >= 1.0f) {
                finishTransition();
            } else {
                float t = Easing.easeOutCubic(rawT);
                int currentSign = activeDirection.sign;

                // Outgoing slides away: FORWARD moves left (0 to -D), BACKWARD moves right (0 to +D)
                int xOut = -(int) (currentSign * t * TRAVEL_DISTANCE_PX);
                float alphaOut = Math.max(0.0f, Math.min(1.0f, 1.0f - t));

                // Incoming slides into 0: FORWARD starts at +D, BACKWARD starts at -D
                int xIn = (int) (currentSign * (1.0f - t) * TRAVEL_DISTANCE_PX);
                float alphaIn = Math.max(0.0f, Math.min(1.0f, t));

                outgoingWrapper.setLocation(xOut, 0);
                outgoingWrapper.setAlpha(alphaOut);

                incomingWrapper.setLocation(xIn, 0);
                incomingWrapper.setAlpha(alphaIn);

                repaint();
            }
        });

        animationTimer.start();
    }

    private void finishTransition() {
        stopTimer();
        removeAll();
        setLayout(new BorderLayout());

        this.currentComponent = incomingComponent;
        this.outgoingComponent = null;
        this.incomingComponent = null;
        this.outgoingWrapper = null;
        this.incomingWrapper = null;
        this.isTransitioning = false;

        if (currentComponent != null) {
            add(currentComponent, BorderLayout.CENTER);
        }

        revalidate();
        repaint();

        if (currentComponent != null) {
            currentComponent.requestFocusInWindow();
        }
    }

    private void finishActiveAnimationImmediately() {
        if (incomingComponent != null) {
            this.currentComponent = incomingComponent;
        }
        finishTransition();
    }

    private void stopTimer() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
            animationTimer = null;
        }
    }

    @Override
    public void doLayout() {
        super.doLayout();
        if (isTransitioning) {
            int w = getWidth();
            int h = getHeight();
            if (outgoingWrapper != null) {
                outgoingWrapper.setSize(w, h);
                outgoingWrapper.doLayout();
            }
            if (incomingWrapper != null) {
                incomingWrapper.setSize(w, h);
                incomingWrapper.doLayout();
            }
        }
    }

    public JComponent getCurrentComponent() {
        return currentComponent;
    }

    public boolean isTransitioning() {
        return isTransitioning;
    }

    /**
     * Internal wrapper that renders real Swing page components with dynamic alpha transparency.
     */
    private static class TransitionWrapper extends JComponent {
        private final JComponent content;
        private float alpha = 1.0f;

        public TransitionWrapper(JComponent content) {
            this.content = content;
            setLayout(new BorderLayout());
            setOpaque(false);
            if (content != null) {
                add(content, BorderLayout.CENTER);
            }
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        public void paint(Graphics g) {
            if (alpha <= 0.005f) {
                return; // Completely transparent, skip drawing
            }
            if (alpha >= 0.995f) {
                super.paint(g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2);
            g2.dispose();
        }
    }
}
