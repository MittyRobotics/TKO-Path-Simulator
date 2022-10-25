# TKO-Path-Simulator

2D/3D Java (LibGDX) planner and visualizer for autonomous path following using Pure Pursuit/Ramsete in 2022 FRC Rapid React. Utilizes path following algorithms from [path following libraries](https://github.com/MittyRobotics/path-following).

### A demonstration!

https://user-images.githubusercontent.com/54689920/197676562-1c9734f6-30a8-41d2-8c0e-ef83d72545cd.mp4

### Features

* Accurate field models rendered from the official field CAD
* Cross-platform, Gradle build tool to run

#### 2D:
* Intuitive and powerful interface for creating and editing Quintic Hermite splines on a 2D field
  * Controllable poses and velocities, all precisely measured in inches
  * Path manager widget to organize complex path systems
* Exported paths can directly be copy-pasted into code

#### 3D:
* Accurate robot model for easy visualization, CAD-based 3D controls
* Real-time simulation with video-style scrubbing
* Allows control over all aspects of the path and motion profile
  * Lookahead, end threshold, adjust threshold, Newton's steps
  * Max acceleration, velocity, angular velocity, starting and ending velocities
* Directly exports into Java code

#### Importing paths
* Custom-made parser for code compatible with path-following libraries
* Splines and paths are stored and organized using dictionaries
  * Variable storage and postfix stack calculator allows function arguments to be expressed by variables and numeric expressions

