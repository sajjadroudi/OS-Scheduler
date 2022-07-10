# Scheduler
Simple task scheduler written in Java. Created as a university project for Operating Systems course. You can study [project_definition.pdf](https://github.com/sajjadroudi/turing-machine/blob/master/project_definition.pdf) for more details.

## Features

- Support scheduling algorithms:
  - Shortest Job First (SJF)
  - First Come First Serve (FCFS)
  - Round Robin (RR)
- To prevent starvation, when a task is finished and the processor requests another task to execute, we iterate over waiting tasks to find tasks that their waiting duration in the waiting queue is twice of their execution time then if the needed resources of one of them is available we send to directly to processor.

## Build and Run

To build and run the project simply run main method of Main class.

