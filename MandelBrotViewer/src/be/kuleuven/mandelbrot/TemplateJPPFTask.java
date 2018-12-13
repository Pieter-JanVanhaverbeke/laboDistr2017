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
package be.kuleuven.mandelbrot;

import org.jppf.node.protocol.AbstractTask;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import static be.kuleuven.mandelbrot.MandelBrotViewer.*;

/**
 * This class is a template for a standard JPPF task.
 * There are 3 parts to a task that is to be executed on a JPPF grid:
 * <ol>
 * <li>the task initialization: this is done on the client side, generally from the task constructor,
 * or via explicit method calls on the task from the application runner.</li>
 * <li>the task execution: this part is performed by the node. It consists in invoking the {@link #run() run()} method,
 * and handling an eventual uncaught {@link Throwable Throwable} that would result from this invocation.</li>
 * <li>getting the execution results: the task itself, after its execution, is considered as the result.
 * JPPF provides the convenience methods {@link org.jppf.node.protocol.Task#setResult(Object) setResult(Object)} and
 * to this effect, however any accessible attribute of the task will be available when the task is returned to the client.</li>
 * </ol>
 * @author Laurent Cohen
 */
public class TemplateJPPFTask extends AbstractTask<float[][]> {
    private int w;
    private Random rnd;
    float[][] rgbValues;
    int height;
    Rectangle2D.Double viewPort;
    int width;


    /**
   * Perform initializations on the client side,
   * before the task is executed by the node.
   */
  public TemplateJPPFTask(int width, Rectangle2D.Double viewport, int height, int w, Random rnd) {
      this.w = w;
      this.rnd = rnd;
    // perform initializations here ...
      this.height = height;
      this.viewPort = viewport;
      this.width = width;
  }

  /**
   * This method contains the code that will be executed by a node.
   * Any uncaught {@link Throwable Throwable} will be stored in the task via a call to {@link org.jppf.node.protocol.Task#setThrowable(Throwable) Task.setThrowable(Throwable)}.
   */
  @Override
  public void run() {
      rgbValues= new float[height][3];
      // write your task code here.
      System.out.println("Hello, this is the node executing a template JPPF task");

      System.out.println(height);
      for (int h = 0; h < height; h++) {

          float r = 0, g = 0, b = 0;
          for (int sample = 0; sample < superSamples; sample++) {
          /*if(isCancelled()) {
            return null;
          }*/

              // escape time algorithm
              double x0, y0;
              if (superSamples == 1) {
                  x0 = viewPort.getMinX() + (w + .5) / width * viewPort.getWidth();
                  y0 = viewPort.getMaxY() - (h + 0.5) / height * viewPort.getHeight();
              } else {
                  x0 = viewPort.getMinX() + (w + rnd.nextDouble()) / width * viewPort.getWidth();
                  y0 = viewPort.getMaxY() - (h + rnd.nextDouble()) / height * viewPort.getHeight();
              }
              double x = 0;
              double y = 0;

              long iteration = 0;
              long max_iteration = maxIterations;

              while (x * x + y * y < 4 && iteration < max_iteration) {
                  double xtemp = x * x - y * y + x0;
                  y = 2 * x * y + y0;
                  x = xtemp;
                  iteration++;
              }

              // determine the color
              Color color = Color.BLACK;
              if (iteration < max_iteration) {
                  double quotient = (double) iteration / (double) max_iteration;
                  float c = (float) Math.pow(quotient, 1.0 / 3);
                  if (quotient > 0.5) {
                      // Close to the mandelbrot set the color changes from green to white
                      r += c;
                      g += 1.f;
                      b += c;
                  } else {
                      // Far away it changes from black to green
                      g += c;
                  }
              }
          }

          rgbValues[h][0]= r/superSamples;
          rgbValues[h][1]= g/superSamples;
          rgbValues[h][2]= b/superSamples;

          System.out.println(r + g + b);
          System.out.println("einde");

      }

      // eventually set the execution results
      setResult(rgbValues);

  }
}
