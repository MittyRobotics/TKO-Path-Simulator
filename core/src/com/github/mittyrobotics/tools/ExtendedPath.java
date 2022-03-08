package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.PurePursuitPath;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSplineGroup;
import com.github.mittyrobotics.pathfollowing.RamsetePath;

public class ExtendedPath {
    public PurePursuitPath purePursuitPath;
    public RamsetePath ramsetePath;
    public double lookahead;
    public double adjust_threshold;
    public double end_threshold;
    public int newtonsSteps;
    public double b;
    public double Z;

    public ExtendedPath(QuinticHermiteSplineGroup group) {
        purePursuitPath = new PurePursuitPath(group, 50, 50);
        ramsetePath = new RamsetePath(group, 50, 50);
        lookahead = 15;
        adjust_threshold = 12;
        end_threshold = 3;
        newtonsSteps = 50;
        b = 2;
        Z = 0.2;
    }
}
