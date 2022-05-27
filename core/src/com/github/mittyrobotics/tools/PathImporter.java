package com.github.mittyrobotics.tools;

import com.github.mittyrobotics.pathfollowing.Pose2D;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSpline;
import com.github.mittyrobotics.pathfollowing.QuinticHermiteSplineGroup;
import com.github.mittyrobotics.pathfollowing.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;

public class PathImporter {
    public static ArrayList<ExtendedPath> parse(String input) throws Exception {
        input = input.trim().replaceAll("\n", " ").replaceAll(" +", " ");
        String[] lines = input.split(";");
        ArrayList<ExtendedPath> result = new ArrayList<>();
        HashMap<String, QuinticHermiteSpline> splines = new HashMap<>();
        HashMap<String, QuinticHermiteSplineGroup> groups = new HashMap<>();

        for(String line : lines) {
            line = line.strip();
            if(line.contains("=")) {

                String[] l = line.split("=");
                if(l.length != 2) throw new Exception();
                String left = l[0].strip();
                String[] lefts = left.split(" ");
                String right = l[1].strip();
                if(lefts.length != 2) throw new Exception();
                String type = lefts[0];
                String name = lefts[1];
                if(type.equals("QuinticHermiteSpline")) {
                    if(right.contains("new QuinticHermiteSpline(") && right.contains(")")) {
                        String processable = right.substring(right.indexOf("new QuinticHermiteSpline(") + "new QuinticHermiteSpline(".length(), right.lastIndexOf(")"));

                        String cur = "";
                        Object curObj = null;
                        boolean n = false, b = false;
                        ArrayList<Object> objs = new ArrayList<>();
                        ArrayList<Double> params = new ArrayList<>();

                        for(char c : processable.toCharArray()) {
                            if(c == ' ') {
                                if(cur.equals("new")) {
                                    n = true;
                                    cur = "";
                                }
                            } else if (c == '(') {
                                if (n) {
                                    if (cur.equals("Pose2D")) {
                                        curObj = new Pose2D();
                                    } else if (cur.equals("Vector2D")) {
                                        curObj = new Vector2D();
                                    } else throw new Exception();
                                    cur = "";
                                    n = false;
                                } else throw new Exception();
                            } else if (c == ',') {
                                if(!b) try { params.add(Double.parseDouble(cur)); } catch (Exception e) { throw e; }
                                else b = false;
                                cur = "";
                            } else if (c == ')') {
                                try { params.add(Double.parseDouble(cur)); } catch (Exception e) { throw e; }
                                cur = "";
                                if(curObj instanceof Pose2D && params.size() == 3) {
                                    objs.add(new Pose2D(params.get(0), params.get(1), params.get(2)));
                                } else if (curObj instanceof Vector2D && params.size() == 2) {
                                    objs.add(new Vector2D(params.get(0), params.get(1)));
                                }
                                params.clear();
                                b = true;
                            } else {
                                cur += c;
                            }
                        }

                        try {
                            if (objs.size() == 2) {
                                splines.put(name, new QuinticHermiteSpline((Pose2D) objs.get(0), (Pose2D) objs.get(1)));
                            } else if (objs.size() == 4) {
                                splines.put(name, new QuinticHermiteSpline((Pose2D) objs.get(0), (Pose2D) objs.get(1), (Vector2D) objs.get(2), (Vector2D) objs.get(3)));
                            } else if (objs.size() == 6) {
                                splines.put(name, new QuinticHermiteSpline((Pose2D) objs.get(0), (Pose2D) objs.get(1), (Vector2D) objs.get(2), (Vector2D) objs.get(3), (Vector2D) objs.get(4), (Vector2D) objs.get(5)));
                            } else throw new Exception();
                        } catch (Exception e) {
                            throw e;
                        }
                    } else throw new Exception();
                }

                if(type.equals("QuinticHermiteSplineGroup")) {
                    if(right.contains("new QuinticHermiteSplineGroup(") && right.contains(")")) {
                        String processable = right.substring(right.indexOf("new QuinticHermiteSplineGroup(") + "new QuinticHermiteSplineGroup(".length(), right.lastIndexOf(")"));

                        if(splines.containsKey(processable)) {
                            groups.put(name, new QuinticHermiteSplineGroup(splines.get(processable)));
                            splines.remove(processable);
                        } else {
                            groups.put(name, new QuinticHermiteSplineGroup());
                        }
                    } else throw new Exception();
                }

            } else if (line.contains(".addSpline(") && line.contains(")")) {
                String left = line.substring(0, line.indexOf(".addSpline("));
                String right = line.substring(line.indexOf(".addSpline(") + ".addSpline(".length(), line.lastIndexOf(")"));
                if(groups.containsKey(left) && splines.containsKey(right)) {
                    groups.get(left).addSpline(splines.get(right));
                    splines.remove(right);
                }
            }
        }

        for(QuinticHermiteSpline s : splines.values()) {
            result.add(new ExtendedPath(new QuinticHermiteSplineGroup(s)));
        }

        for(QuinticHermiteSplineGroup g : groups.values()) {
            if(g.getSplines().size() > 0) result.add(new ExtendedPath(g));
        }

        System.out.println(result);
        return result;
    }
}
