# Simple Shell

Simple task scheduler written in Java. Created as a University project for Operating Systems course.

## Features

- Support scheduling algorithms:
  - Shortest Job First (SJF)
  - First Come First Serve (FCFS)
  - Round Robin (RR)
- To prevent starvation, when a task is finished and the processor requests another task to execute, we iterate over waiting tasks to find tasks that their waiting duration in the waiting queue is twice of their execution time then if the needed resources of one of them is available we send to directly to processor.

## Build and Run

To build and run the project simply run main method of Main class.

