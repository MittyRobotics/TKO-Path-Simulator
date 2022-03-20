package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.Pose2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;

public class Splines {
    //Pick up first ball (Forward)
    public static QuinticHermiteSpline spline1 = new QuinticHermiteSpline(
            new Pose2D(26, 99, 90 * (Math.PI / 180)),
            new Pose2D(26, 123, 90 * (Math.PI / 180))
    );

    //Move to Hub to shoot first two balls (Reverse)
    public static QuinticHermiteSpline spline2 = new QuinticHermiteSpline(
            new Pose2D(26, 123, (90 + 180) * (Math.PI / 180)),
            new Pose2D(23, 53, (69 + 180) * (Math.PI / 180))
    );

    //Go from hub to third ball pickup (Forward)
    public static QuinticHermiteSpline spline3 = new QuinticHermiteSpline(
            new Pose2D(23, 53, 69 * (Math.PI / 180)),
            new Pose2D(98, 84, 8 * (Math.PI / 180))
    );

    //Drive back to shooting location for third ball (Reverse)
    public static QuinticHermiteSpline spline4 = new QuinticHermiteSpline(
            new Pose2D(98, 84, (8 + 180) * (Math.PI / 180)),
            new Pose2D(65, 68, (43 + 180) * (Math.PI / 180))
    );

    //Drive from third ball shooting location to human loader (Forward)
    public static QuinticHermiteSpline spline5 = new QuinticHermiteSpline(
            new Pose2D(65, 68, (43) * (Math.PI / 180)),
            new Pose2D(258, 102, 33 * (Math.PI / 180))
    );

    //Drive from human loader to last ball shoot location (Reverse)
    public static QuinticHermiteSpline spline6 = new QuinticHermiteSpline(
            new Pose2D(258, 102, (33 + 180) * (Math.PI / 180)),
            new Pose2D(85, 36, (202) * (Math.PI / 180))
    );

    public static QuinticHermiteSpline[] splines = new QuinticHermiteSpline[]{spline1, spline2, spline3, spline4, spline5, spline6};
}
