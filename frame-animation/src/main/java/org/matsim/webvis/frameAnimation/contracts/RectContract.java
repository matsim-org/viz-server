package org.matsim.webvis.frameAnimation.contracts;

import org.matsim.core.utils.collections.QuadTree;

public class RectContract {
    private double left;
    private double right;
    private double top;
    private double bottom;

    public RectContract(double left, double right, double top, double bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public double getLeft() {
        return left;
    }

    public double getRight() {
        return right;
    }

    public double getTop() {
        return top;
    }

    public double getBottom() {
        return bottom;
    }

    public QuadTree.Rect copyToMatsimRect() {
        return new QuadTree.Rect(left, top, right, bottom);
    }


}
