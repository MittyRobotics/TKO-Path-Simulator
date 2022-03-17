package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.PathSim;
import com.github.mittyrobotics.pathfollowing.*;

import java.util.ArrayList;

public class Simulator {
    public ExtendedPath path;
    public double MAX_TIME = 60;
    public double DT = 0.02;

    public ArrayList<pState> ppStates = new ArrayList<>();
    public ArrayList<rState> rStates = new ArrayList<>();

    public void updatePath(ExtendedPath path) {
        this.path = path;

        simulate();
    }

    public void simulate() {

        path.update();

        ppStates.clear();

        double cur_time = 0;
        PurePursuitPath pp = path.purePursuitPath;
        Pose2D robotPosition = pp.getParametric().getPose(0);
        double LOOKAHEAD = path.lookahead;
        int NEWTONS_STEPS = path.newtonsSteps;
        double ADJUST_THRESHOLD = path.adjust_threshold;
        double END_THRESHOLD = path.end_threshold;
        double TRACKWIDTH = 28;

        ppStates.add(new pState(robotPosition, pp.getLookaheadFromRobotPose(robotPosition, LOOKAHEAD, NEWTONS_STEPS), pp.getParametric().getPoint(0),
                new Vector2D(pp.getStartVelocity(), pp.getStartVelocity()), pp.getAngularVelocityAtPoint(0, pp.getStartVelocity()), pp.getCurvature(0),
                pp.getStartVelocity(), pp.getParametric()));

        while(cur_time < MAX_TIME) {
            DifferentialDriveState dds = pp.update(robotPosition, DT, LOOKAHEAD, ADJUST_THRESHOLD, NEWTONS_STEPS, TRACKWIDTH);
            double left = dds.getLeftVelocity() * DT;
            double right = dds.getRightVelocity() * DT;
            Angle angle = robotPosition.getAngle();
            double x = robotPosition.getPosition().getX();
            double y = robotPosition.getPosition().getY();
            double new_x, new_y, newAngle;

            if(Math.abs(left - right) < 1e-6) {
                new_x = x + left * angle.cos();
                new_y = y + right * angle.sin();
                newAngle = angle.getRadians();
            } else {
                double turnRadius = TRACKWIDTH * (left + right) / (2 * (right - left));
                newAngle = Math.toRadians(Math.toDegrees(angle.getRadians() + (right - left) / TRACKWIDTH));

                new_x = x + turnRadius * (Math.sin(newAngle) - angle.sin());
                new_y = y - turnRadius * (Math.cos(newAngle) - angle.cos());
            }
            robotPosition = new Pose2D(new_x, new_y, newAngle);
            cur_time += DT;

            ppStates.add(new pState(robotPosition, pp.getLookaheadFromRobotPose(robotPosition, LOOKAHEAD, NEWTONS_STEPS),
                    pp.getParametric().getPoint(pp.getParametric().findClosestPointOnSpline(robotPosition.getPosition(), NEWTONS_STEPS, 5)),
                    new Vector2D(dds.getLeftVelocity(), dds.getRightVelocity()), dds.getAngularVelocity(), pp.getCurvature(),
                    dds.getLinearVelocity(), pp.getParametric()));

            if(pp.isFinished(robotPosition, END_THRESHOLD)) {
                break;
            }
        }

        rStates.clear();

        cur_time = 0;
        RamsetePath rp = path.ramsetePath;
        robotPosition = rp.getParametric().getPose(0);
        double b = path.b;
        double Z = path.Z;
        NEWTONS_STEPS = path.r_newtonsSteps;
        ADJUST_THRESHOLD = path.r_adjust_threshold;
        END_THRESHOLD = path.r_end_threshold;
        TRACKWIDTH = 28;

        rStates.add(new rState(robotPosition, rp.getParametric().getPoint(0),
                new Vector2D(rp.getStartVelocity(), rp.getStartVelocity()), rp.getAngularVelocityAtPoint(0, rp.getStartVelocity()), rp.getCurvature(0),
                rp.getStartVelocity(), rp.getParametric()));

        while(cur_time < MAX_TIME) {
            DifferentialDriveState dds = rp.update(robotPosition, DT, ADJUST_THRESHOLD, NEWTONS_STEPS, b, Z, TRACKWIDTH);
            double left = dds.getLeftVelocity() * DT;
            double right = dds.getRightVelocity() * DT;
            Angle angle = robotPosition.getAngle();
            double x = robotPosition.getPosition().getX();
            double y = robotPosition.getPosition().getY();
            double new_x, new_y, newAngle;

            if(Math.abs(left - right) < 1e-6) {
                new_x = x + left * angle.cos();
                new_y = y + right * angle.sin();
                newAngle = angle.getRadians();
            } else {
                double turnRadius = TRACKWIDTH * (left + right) / (2 * (right - left));
                newAngle = Math.toRadians(Math.toDegrees(angle.getRadians() + (right - left) / TRACKWIDTH));

                new_x = x + turnRadius * (Math.sin(newAngle) - angle.sin());
                new_y = y - turnRadius * (Math.cos(newAngle) - angle.cos());
            }
            robotPosition = new Pose2D(new_x, new_y, newAngle);
            cur_time += DT;

            rStates.add(new rState(robotPosition,
                    rp.getParametric().getPoint(rp.getParametric().findClosestPointOnSpline(robotPosition.getPosition(), NEWTONS_STEPS, 5)),
                    new Vector2D(dds.getLeftVelocity(), dds.getRightVelocity()), dds.getAngularVelocity(), rp.getCurvature(),
                    dds.getLinearVelocity(), rp.getParametric()));

            if(path.purePursuitPath.isFinished(robotPosition, END_THRESHOLD)) {
                break;
            }
        }

        PathSim.renderer3d.resetSim();
    }

    public rState getRState(int ind) {
        return rStates.get(ind);
    }

    public pState getPState(int ind) {
        return ppStates.get(ind);
    }

    public int getEnd(boolean pp) {
        if(pp) return ppStates.size();
        else return rStates.size();
    }

    public class pState {
        public Pose2D robotPose;
        public Point2D lookahead, closest;
        public Vector2D velocity;
        public double angularVel, curvature, linearVel;
        public Parametric parametric;

        public pState(Pose2D robotPose, Point2D lookahead, Point2D closest, Vector2D velocity, double angularVel, double curvature, double linearVel, Parametric parametric) {
            this.robotPose = robotPose;
            this.lookahead = lookahead;
            this.closest = closest;
            this.velocity = velocity;
            this.angularVel = angularVel;
            this.curvature = curvature;
            this.linearVel = linearVel;
            this.parametric = parametric;
        }
    }

    public class rState {
        public Pose2D robotPose;
        public Point2D closest;
        public Vector2D velocity;
        public double angularVel, curvature, linearVel;
        public Parametric parametric;

        public rState(Pose2D robotPose, Point2D closest, Vector2D velocity, double angularVel, double curvature, double linearVel, Parametric parametric) {
            this.robotPose = robotPose;
            this.closest = closest;
            this.velocity = velocity;
            this.angularVel = angularVel;
            this.curvature = curvature;
            this.linearVel = linearVel;
            this.parametric = parametric;
        }
    }
}
