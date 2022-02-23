package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.*;

import java.util.ArrayList;

public class PathManager {

    public ArrayList<PurePursuitPath> paths = new ArrayList<>();

    public Point2D storedPoint;

    public PathManager() {

    }

    public void storePoint(Point2D point) {
        storedPoint = point;
    }

    public void addPathFromPoint(Point2D point) {
        if(storedPoint != null) {
            Angle angle = new Angle(point.x - storedPoint.x, point.y - storedPoint.y);
            Angle angleOther = new Angle(angle.getRadians() + Math.PI);
            paths.add(new PurePursuitPath(
                    new QuinticHermiteSpline(new Pose2D(storedPoint, angle), new Pose2D(point, angleOther)),
                    50,
                    50
            ));
        }
        System.out.println(paths.get(0));

        storedPoint = null;
    }
}
