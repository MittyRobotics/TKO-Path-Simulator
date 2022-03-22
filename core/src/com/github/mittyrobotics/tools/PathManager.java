package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

import java.util.ArrayList;

public class PathManager {

    public ArrayList<ExtendedPath> paths = new ArrayList<>();
    public ArrayList<ExtendedPath> toBeDeleted = new ArrayList<>();

    public int curEditingPath, curEditingNode, curOnPath, curEditingVel, curSelectedNode, curHoveringNode, curUIHoveringNode;

    public Point2D storedPoint;

    public PathManager() {
        curEditingPath = -1;
        curEditingNode = -1;
        curSelectedNode = -1;
        curEditingVel = -1;
        curOnPath = -1;
        curHoveringNode = -1;
        curUIHoveringNode = -1;
        storedPoint = null;
    }

    public void removeStoredPoint() {
        storedPoint = null;
    }

    public void delayRemove(ExtendedPath path) {
        toBeDeleted.add(path);
        System.out.println("hi");
    }

    public void updateRemove() {
        for(ExtendedPath p : toBeDeleted) {
            if(paths.indexOf(p) == curEditingPath) curEditingPath = -1;
            else if (paths.indexOf(p) < curEditingPath) curEditingPath--;

            if(paths.indexOf(p) == curOnPath) curOnPath = -1;
            else if (paths.indexOf(p) < curOnPath) curOnPath--;
            paths.remove(p);
            PathSim.renderer2d.ui.populateWidget();
        }
        toBeDeleted.clear();
    }

    public void storePoint(Point2D point) {
        storedPoint = point;
    }

    public void addPathFromPoint(Point2D point) {
        if(storedPoint != null) {
            Angle angle = new Angle(point.x - storedPoint.x, point.y - storedPoint.y);
            paths.add(new ExtendedPath(new QuinticHermiteSplineGroup(new QuinticHermiteSpline(new Pose2D(storedPoint, angle), new Pose2D(point, angle)))));
            PathSim.renderer2d.ui.populateWidget();
        }
        storedPoint = null;
    }

    public void chooseEditPath(ExtendedPath p) {
        if(paths.contains(p)) {
            curEditingPath = paths.indexOf(p);
            curSelectedNode = -1;
            curEditingNode = -1;
            curEditingVel = -1;
        }
    }

    public QuinticHermiteSpline getNewPathPreview(Point2D point) {
        if(storedPoint != null) {
            Angle angle = new Angle(point.x - storedPoint.x, point.y - storedPoint.y);
            return new QuinticHermiteSpline(new Pose2D(storedPoint, angle), new Pose2D(point, angle));
        }
        return null;
    }

    public void addPointToPath(Point2D point, int currentPath, boolean front) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) paths.get(currentPath).purePursuitPath.getParametric();
        if(front) {
            Pose2D pp = group.getSplines().get(0).getPose0();
            Point2D prev = pp.getPosition();
            Angle angle = pp.getAngle();
            Angle other = new Angle(prev.x - point.x, prev.y - point.y);
            group.addSpline(0, new QuinticHermiteSpline(new Pose2D(point, other), new Pose2D(prev, angle)));
        } else {
            Pose2D pp = group.getSplines().get(group.getSplines().size() - 1).getPose1();
            Point2D prev = pp.getPosition();
            Angle angle = pp.getAngle();
            Angle other = new Angle(prev.x - point.x, prev.y - point.y);
            other.add(Math.PI);
            group.addSpline(new QuinticHermiteSpline(new Pose2D(prev, angle), new Pose2D(point, other)));
        }
        PathSim.renderer2d.ui.populateSplineEdit();
    }

    public QuinticHermiteSpline getPotentialSpline(Point2D point, int currentPath, boolean front) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) paths.get(currentPath).purePursuitPath.getParametric();
        if(front) {
            Pose2D pp = group.getSplines().get(0).getPose0();
            Point2D prev = pp.getPosition();
            Angle angle = pp.getAngle();
            Angle other = new Angle(prev.x - point.x, prev.y - point.y);
            return new QuinticHermiteSpline(new Pose2D(point, other), new Pose2D(prev, angle));
        } else {
            Pose2D pp = group.getSplines().get(group.getSplines().size() - 1).getPose1();
            Point2D prev = pp.getPosition();
            Angle angle = pp.getAngle();
            Angle other = new Angle(prev.x - point.x, prev.y - point.y);
            other.add(Math.PI);
            return new QuinticHermiteSpline(new Pose2D(prev, angle), new Pose2D(point, other));
        }
    }

    public void deleteNode(int currentPath, int currentNode) {
        QuinticHermiteSplineGroup group = (QuinticHermiteSplineGroup) paths.get(currentPath).purePursuitPath.getParametric();
        if(currentNode == 0) {
            group.removeSpline(0);
        } else if (currentNode == group.getSplines().size()) {
            group.removeSpline(group.getSplines().size() - 1);
        } else {
            QuinticHermiteSpline prev = group.getSpline(currentNode - 1);
            QuinticHermiteSpline post = group.getSpline(currentNode);
            group.getSplines().set(currentNode-1, new QuinticHermiteSpline(prev.getPose0(), post.getPose1(), prev.getVelocity0(), post.getVelocity1()));
            group.removeSpline(currentNode);
        }
        curSelectedNode = -1;
        PathSim.renderer2d.ui.populateSplineEdit();
    }

    public PurePursuitPath getCurPath() {
        return paths.get(curEditingPath).purePursuitPath;
    }

    public boolean editingPath() {
        return curEditingPath != -1;
    }

    public RamsetePath getCurRPath() {
        return paths.get(curEditingPath).ramsetePath;
    }

    public ExtendedPath getCurEPath() {
        return paths.get(curEditingPath);
    }

    public boolean notEditing() {
        return curEditingPath == -1;
    }
}
