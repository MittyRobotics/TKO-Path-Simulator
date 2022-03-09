package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.Parametric;
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

    public double r_adjust_threshold;
    public double r_end_threshold;
    public double r_newtonsSteps;
    public double b;
    public double Z;

    public ExtendedPath(QuinticHermiteSplineGroup group) {
        purePursuitPath = new PurePursuitPath(group, 50, 50, 50, 1000, 0, 0);
        ramsetePath = new RamsetePath(group, 50, 50, 50, 1000, 0, 0);
        lookahead = 15;
        adjust_threshold = 12;
        end_threshold = 3;
        newtonsSteps = 50;
        r_adjust_threshold = 12;
        r_end_threshold = 3;
        r_newtonsSteps = 50;
        b = 2;
        Z = 0.2;
    }

    public void update() {
        purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), purePursuitPath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                purePursuitPath.getMaxAngularVelocity(), purePursuitPath.getStartVelocity(), purePursuitPath.getEndVelocity());
        ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), ramsetePath.getMaxVelocity(),
                ramsetePath.getMaxAngularVelocity(), ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }

    public void setMaxAcceleration(double maxAcceleration, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), maxAcceleration, purePursuitPath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                purePursuitPath.getMaxAngularVelocity(), purePursuitPath.getStartVelocity(), purePursuitPath.getEndVelocity());
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), maxAcceleration, ramsetePath.getMaxDeceleration(), ramsetePath.getMaxVelocity(),
                ramsetePath.getMaxAngularVelocity(), ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }

    public void setMaxDeceleration(double maxDeceleration, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), maxDeceleration, purePursuitPath.getMaxVelocity(),
                purePursuitPath.getMaxAngularVelocity(), purePursuitPath.getStartVelocity(), purePursuitPath.getEndVelocity());
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), maxDeceleration, ramsetePath.getMaxVelocity(),
                ramsetePath.getMaxAngularVelocity(), ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }

    public void setMaxVelocity(double maxVelocity, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), purePursuitPath.getMaxDeceleration(), maxVelocity,
                purePursuitPath.getMaxAngularVelocity(), purePursuitPath.getStartVelocity(), purePursuitPath.getEndVelocity());
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), maxVelocity,
                ramsetePath.getMaxAngularVelocity(), ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }

    public void setMaxAngularVelocity(double maxAngularVelocity, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), purePursuitPath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                maxAngularVelocity, purePursuitPath.getStartVelocity(), purePursuitPath.getEndVelocity());
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                maxAngularVelocity, ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }

    public void setStartVelocity(double startVelocity, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), purePursuitPath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                purePursuitPath.getMaxAngularVelocity(), startVelocity, purePursuitPath.getEndVelocity());
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                ramsetePath.getMaxAngularVelocity(), startVelocity, ramsetePath.getEndVelocity());
    }

    public void setEndVelocity(double endVelocity, boolean pp) {
        if(pp) purePursuitPath = new PurePursuitPath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), purePursuitPath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                purePursuitPath.getMaxAngularVelocity(), purePursuitPath.getStartVelocity(), endVelocity);
        else ramsetePath = new RamsetePath(purePursuitPath.getParametric(), purePursuitPath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), purePursuitPath.getMaxVelocity(),
                ramsetePath.getMaxAngularVelocity(), ramsetePath.getStartVelocity(), endVelocity);
    }

    public void updateRamsetePath() {
        ramsetePath = new RamsetePath(purePursuitPath.getParametric(), ramsetePath.getMaxAcceleration(), ramsetePath.getMaxDeceleration(), ramsetePath.getMaxVelocity(), ramsetePath.getMaxAngularVelocity(),
                ramsetePath.getStartVelocity(), ramsetePath.getEndVelocity());
    }
}
