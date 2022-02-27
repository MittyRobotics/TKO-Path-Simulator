package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.*;

import java.util.ArrayList;

public class PathManager {

    public ArrayList<Path> paths = new ArrayList<>();
    public int curEditingPath, curEditingNode, curOnPath, curEditingVel;

    public Point2D storedPoint;

    public PathManager() {
        curEditingPath = -1;
        curEditingNode = -1;
        curOnPath = -1;
        storedPoint = null;
    }

    public void storePoint(Point2D point) {
        storedPoint = point;
    }

    public void addPathFromPoint(Point2D point) {
        if(storedPoint != null) {
            Angle angle = new Angle(point.x - storedPoint.x, point.y - storedPoint.y);
            paths.add(new PurePursuitPath(
                    new QuinticHermiteSplineGroup(new QuinticHermiteSpline(new Pose2D(storedPoint, angle), new Pose2D(point, angle))),
                    50,
                    50
            ));
        }
        storedPoint = null;
    }

    public void addPointToPath(Point2D point, int currentPath, boolean front) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) paths.get(currentPath).getParametric();
        if(front) {
            Point2D prev = group.getSplines().get(0).getPose0().getPosition();
            Angle angle = new Angle(point.x - prev.x, point.y - prev.y);
            angle.add(Math.PI);
            group.addSpline(0, new QuinticHermiteSpline(new Pose2D(point, angle), new Pose2D(prev, angle)));
        } else {
            Point2D prev = group.getSplines().get(group.getSplines().size() - 1).getPose1().getPosition();
            Angle angle = new Angle(prev.x - point.x, prev.y - point.y);
            angle.add(Math.PI);
            group.addSpline(new QuinticHermiteSpline(new Pose2D(prev, angle), new Pose2D(point, angle)));
        }
    }

    public void moveToFront(int i) {
        if(i >= 0) paths.add(paths.remove(i));
    }
}
