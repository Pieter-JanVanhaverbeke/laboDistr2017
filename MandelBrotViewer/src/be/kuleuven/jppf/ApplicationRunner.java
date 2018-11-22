/*
 * JPPF.
 * Copyright (C) 2005-2016 JPPF Team.
 * http://www.jppf.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.kuleuven.jppf;

import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFConnectionPool;
import org.jppf.client.JPPFJob;
import org.jppf.client.Operator;
import org.jppf.node.protocol.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static be.kuleuven.mandelbrot.MandelBrotViewer.height;
import static be.kuleuven.mandelbrot.MandelBrotViewer.width;


/**
 * This is a template JPPF application runner.
 * It is fully commented and is designed to be used as a starting point
 * to write an application using JPPF.
 * @author Laurent Cohen
 */
public class ApplicationRunner {

  private JPPFClient client;

  /**
   * The entry point for this application runner to be run from a Java command line.
   * however nothing prevents us from using them if need be.
   */
  public ApplicationRunner() {

    // create the JPPFClient. This constructor call causes JPPF to read the configuration file
    // and connect with one or multiple JPPF drivers.
      JPPFClient jppfClient = new JPPFClient();
  }

  /**
   * Create a JPPF job that can be submitted for execution.
   * @param jobName an arbitrary, human-readable name given to the job.
   * @return an instance of the {@link JPPFJob JPPFJob} class.
   * @throws Exception if an error occurs while creating the job or adding tasks.
   */
  public void createJob(final String jobName, Random rnd) throws Exception {
    // create a JPPF job
    JPPFJob job = new JPPFJob();
    // give this job a readable name that we can use to monitor and manage it.
    job.setName(jobName);

      for (int w = 0; w < width; w++) {
          //setProgress((int)(100.0 * w / width));

          for(int h = 0; h<height; h++) {
              // add a task to the job.
              Task<?> task = job.add(new TemplateJPPFTask(w, h, rnd));
              // provide a user-defined name for the task
              task.setId(jobName + " - Template task");
          }
      }


    // add more tasks here ...

    // there is no guarantee on the order of execution of the tasks,
    // however the results are guaranteed to be returned in the same order as the tasks.
    //return job;

      job.setBlocking(true);
      List<Task<?>> results = client.submitJob(job);
      processExecutionResults(job.getName(), results);

      System.out.println("gelukt!!! :)");
  }

  /**
   * Process the execution results of each submitted task.
   * @param jobName the name of the job whose results are processed.
   * @param results the tasks results after execution on the grid.
   */
  public synchronized void processExecutionResults(final String jobName, final List<Task<?>> results) {
    // print a results header
    System.out.printf("Results for job '%s' :\n", jobName);
    // process the results
    for (Task<?> task: results) {
      String taskName = task.getId();
      // if the task execution resulted in an exception
      if (task.getThrowable() != null) {
        // process the exception here ...
        System.out.println(taskName + ", an exception was raised: " + task.getThrowable ().getMessage());
      } else {
        // process the result here ...
        System.out.println(taskName + ", execution result: " + task.getResult());
      }
    }
  }
}
