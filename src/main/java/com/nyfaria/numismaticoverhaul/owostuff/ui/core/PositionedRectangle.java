package com.nyfaria.numismaticoverhaul.owostuff.ui.core;

import net.minecraft.util.Mth;

/**
 * Represents a rectangle positioned in 2D-space
 */
public interface PositionedRectangle extends Animatable<PositionedRectangle> {

    /**
     * @return The x-coordinate of the top-left corner of this rectangle
     */
    int x();

    /**
     * @return The y-coordinate of the top-left corner of this rectangle
     */
    int y();

    /**
     * @return The width of this rectangle
     */
    int width();

    /**
     * @return The height of this rectangle
     */
    int height();

    /**
     * @return {@code true} if this rectangle contains the given point
     */
    default boolean isInBoundingBox(double x, double y) {
        return x >= this.x() && x < this.x() + this.width() && y >= this.y() && y < this.y() + this.height();
    }

    default boolean intersects(PositionedRectangle other) {
        return other.x() < this.x() + this.width()
                && other.x() + other.width() >= this.x()
                && other.y() < this.y() + this.height()
                && other.y() + other.height() >= this.y();
    }

    default PositionedRectangle intersection(PositionedRectangle other) {

        // my brain is fucking dead on the floor
        // this code is really, really simple
        // and honestly quite obvious
        //
        // my brain did not agree
        // glisco, 2022

        int leftEdge = Math.max(this.x(), other.x());
        int topEdge = Math.max(this.y(), other.y());

        int rightEdge = Math.min(this.x() + this.width(), other.x() + other.width());
        int bottomEdge = Math.min(this.y() + this.height(), other.y() + other.height());

        return of(
                leftEdge,
                topEdge,
                Math.max(rightEdge - leftEdge, 0),
                Math.max(bottomEdge - topEdge, 0)
        );
    }

    @Override
    default PositionedRectangle interpolate(PositionedRectangle next, float delta) {
        return PositionedRectangle.of(
                (int) Mth.lerp(delta, this.x(), next.x()),
                (int) Mth.lerp(delta, this.y(), next.y()),
                (int) Mth.lerp(delta, this.width(), next.width()),
                (int) Mth.lerp(delta, this.height(), next.height())
        );
    }

    static PositionedRectangle of(int x, int y, Size size) {
        return of(x, y, size.width(), size.height());
    }

    static PositionedRectangle of(int x, int y, int width, int height) {
        return new PositionedRectangle() {
            @Override
            public int x() {
                return x;
            }

            @Override
            public int y() {
                return y;
            }

            @Override
            public int width() {
                return width;
            }

            @Override
            public int height() {
                return height;
            }
        };
    }
}
