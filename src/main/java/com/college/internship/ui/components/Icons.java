package com.college.internship.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * HiDPI Vector Icon Painter using pure Java2D geometry.
 * Provides crisp, resolution-independent commercial icons without external image dependencies.
 */
public final class Icons {

    public enum Type {
        DASHBOARD,
        BRIEFCASE,
        APPLICATIONS,
        PROFILE,
        SHIELD,
        SEARCH,
        PLUS,
        TRASH,
        CHECK,
        CLOSE,
        EYE,
        EYE_OFF,
        LOGOUT,
        AWARD,
        LOCATION,
        CALENDAR,
        DOLLAR,
        SPARKLE,
        ARROW_RIGHT,
        REFRESH
    }

    private Icons() {}

    /**
     * Creates a vector Icon with specified type, size (square px), and paint color.
     */
    public static Icon get(Type type, int size, Color color) {
        return new VectorIcon(type, size, color);
    }

    private static class VectorIcon implements Icon {
        private final Type type;
        private final int size;
        private final Color color;

        VectorIcon(Type type, int size, Color color) {
            this.type = type;
            this.size = size;
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            g2.translate(x, y);
            // Normalized scale from 24x24 coordinate space to target size
            double scale = (double) size / 24.0;
            g2.scale(scale, scale);

            g2.setColor(color != null ? color : c.getForeground());
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            drawIcon(g2, type);

            g2.dispose();
        }
    }

    private static void drawIcon(Graphics2D g2, Type type) {
        switch (type) {
            case DASHBOARD -> {
                // 4 modern tiles
                g2.draw(new RoundRectangle2D.Float(3, 3, 7, 7, 2, 2));
                g2.draw(new RoundRectangle2D.Float(14, 3, 7, 7, 2, 2));
                g2.draw(new RoundRectangle2D.Float(3, 14, 7, 7, 2, 2));
                g2.draw(new RoundRectangle2D.Float(14, 14, 7, 7, 2, 2));
            }
            case BRIEFCASE -> {
                // Case body & handle
                g2.draw(new RoundRectangle2D.Float(3, 7, 18, 14, 3, 3));
                g2.draw(new RoundRectangle2D.Float(8, 3, 8, 4, 2, 2));
                g2.draw(new Line2D.Float(3, 12, 21, 12));
                g2.draw(new RoundRectangle2D.Float(10, 11, 4, 3, 1, 1));
            }
            case APPLICATIONS -> {
                // Document with check lines
                g2.draw(new RoundRectangle2D.Float(4, 3, 16, 18, 3, 3));
                g2.draw(new Line2D.Float(8, 8, 16, 8));
                g2.draw(new Line2D.Float(8, 12, 16, 12));
                g2.draw(new Line2D.Float(8, 16, 13, 16));
            }
            case PROFILE -> {
                // Head circle + body arc
                g2.draw(new Ellipse2D.Float(8, 4, 8, 8));
                Path2D p = new Path2D.Float();
                p.moveTo(4, 20);
                p.curveTo(4, 16, 8, 15, 12, 15);
                p.curveTo(16, 15, 20, 16, 20, 20);
                g2.draw(p);
            }
            case SHIELD -> {
                Path2D p = new Path2D.Float();
                p.moveTo(12, 3);
                p.lineTo(19, 6);
                p.curveTo(19, 14, 15, 19, 12, 21);
                p.curveTo(9, 19, 5, 14, 5, 6);
                p.closePath();
                g2.draw(p);
                // Inner checkmark
                Path2D chk = new Path2D.Float();
                chk.moveTo(9, 12);
                chk.lineTo(11, 14);
                chk.lineTo(15, 10);
                g2.draw(chk);
            }
            case SEARCH -> {
                g2.draw(new Ellipse2D.Float(4, 4, 11, 11));
                g2.draw(new Line2D.Float(13, 13, 19, 19));
            }
            case PLUS -> {
                g2.draw(new Line2D.Float(12, 5, 12, 19));
                g2.draw(new Line2D.Float(5, 12, 19, 12));
            }
            case TRASH -> {
                g2.draw(new Line2D.Float(4, 6, 20, 6));
                g2.draw(new Line2D.Float(9, 3, 15, 3));
                Path2D body = new Path2D.Float();
                body.moveTo(6, 6);
                body.lineTo(7, 19);
                body.curveTo(7, 20, 8, 21, 9, 21);
                body.lineTo(15, 21);
                body.curveTo(16, 21, 17, 20, 17, 19);
                body.lineTo(18, 6);
                g2.draw(body);
            }
            case CHECK -> {
                Path2D chk = new Path2D.Float();
                chk.moveTo(5, 12);
                chk.lineTo(10, 17);
                chk.lineTo(19, 7);
                g2.draw(chk);
            }
            case CLOSE -> {
                g2.draw(new Line2D.Float(6, 6, 18, 18));
                g2.draw(new Line2D.Float(18, 6, 6, 18));
            }
            case EYE -> {
                Path2D eye = new Path2D.Float();
                eye.moveTo(3, 12);
                eye.curveTo(7, 6, 17, 6, 21, 12);
                eye.curveTo(17, 18, 7, 18, 3, 12);
                eye.closePath();
                g2.draw(eye);
                g2.draw(new Ellipse2D.Float(10, 10, 4, 4));
            }
            case EYE_OFF -> {
                Path2D eye = new Path2D.Float();
                eye.moveTo(3, 12);
                eye.curveTo(7, 6, 17, 6, 21, 12);
                eye.curveTo(17, 18, 7, 18, 3, 12);
                eye.closePath();
                g2.draw(eye);
                g2.draw(new Line2D.Float(4, 4, 20, 20));
            }
            case LOGOUT -> {
                g2.draw(new Line2D.Float(10, 4, 5, 4));
                g2.draw(new Line2D.Float(5, 4, 5, 20));
                g2.draw(new Line2D.Float(5, 20, 10, 20));
                g2.draw(new Line2D.Float(10, 12, 19, 12));
                Path2D arr = new Path2D.Float();
                arr.moveTo(15, 8);
                arr.lineTo(19, 12);
                arr.lineTo(15, 16);
                g2.draw(arr);
            }
            case AWARD -> {
                g2.draw(new Ellipse2D.Float(6, 4, 12, 12));
                Path2D rib = new Path2D.Float();
                rib.moveTo(9, 15);
                rib.lineTo(7, 21);
                rib.lineTo(12, 19);
                rib.lineTo(17, 21);
                rib.lineTo(15, 15);
                g2.draw(rib);
            }
            case LOCATION -> {
                Path2D pin = new Path2D.Float();
                pin.moveTo(12, 21);
                pin.curveTo(7, 14, 5, 11, 5, 8);
                pin.curveTo(5, 4.5, 8, 2, 12, 2);
                pin.curveTo(16, 2, 19, 4.5, 19, 8);
                pin.curveTo(19, 11, 17, 14, 12, 21);
                pin.closePath();
                g2.draw(pin);
                g2.draw(new Ellipse2D.Float(10, 6, 4, 4));
            }
            case CALENDAR -> {
                g2.draw(new RoundRectangle2D.Float(4, 5, 16, 15, 3, 3));
                g2.draw(new Line2D.Float(4, 10, 20, 10));
                g2.draw(new Line2D.Float(8, 2, 8, 6));
                g2.draw(new Line2D.Float(16, 2, 16, 6));
            }
            case DOLLAR -> {
                g2.draw(new Line2D.Float(12, 3, 12, 21));
                Path2D s = new Path2D.Float();
                s.moveTo(16, 7);
                s.curveTo(15, 5, 9, 5, 9, 9);
                s.curveTo(9, 13, 15, 12, 15, 16);
                s.curveTo(15, 19, 9, 19, 8, 17);
                g2.draw(s);
            }
            case SPARKLE -> {
                Path2D sp = new Path2D.Float();
                sp.moveTo(12, 2);
                sp.curveTo(12, 8, 16, 12, 22, 12);
                sp.curveTo(16, 12, 12, 16, 12, 22);
                sp.curveTo(12, 16, 8, 12, 2, 12);
                sp.curveTo(8, 12, 12, 8, 12, 2);
                sp.closePath();
                g2.fill(sp);
            }
            case ARROW_RIGHT -> {
                g2.draw(new Line2D.Float(5, 12, 19, 12));
                Path2D arr = new Path2D.Float();
                arr.moveTo(13, 6);
                arr.lineTo(19, 12);
                arr.lineTo(13, 18);
                g2.draw(arr);
            }
            case REFRESH -> {
                g2.draw(new Arc2D.Float(4, 4, 16, 16, 45, 270, Arc2D.OPEN));
                Path2D arr = new Path2D.Float();
                arr.moveTo(15, 4);
                arr.lineTo(19, 6);
                arr.lineTo(17, 10);
                g2.draw(arr);
            }
        }
    }
}
