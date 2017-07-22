package org.apache.hadoop.hbase.regionserver;

import org.apache.hadoop.conf.Configuration;
import java.lang.management.ManagementFactory;
import org.apache.hadoop.hbase.util.Threads;
import java.util.Queue;
import java.util.LinkedList;

public class NewLowerLimitController implements Runnable {

	private static final String LOWER_KEY = "hbase.regionserver.global.memstore.lowerLimit";
	private static final double LOWEST_LIMIT = 0.1;
	private static final double HIGHEST_LIMIT = 0.38;
	private HRegionServer server;
	private double memMax = (double)ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
	private double defLower;
	private double lower;
	private boolean debugMode = false;
	private Queue<Integer> latencyQueue;
	private double alpha = 0.0;
	private int length = 4;
	private double x[];
	private double y[];
	private int stage;
	private int worstLatency = 0;
	private double GOAL = 10000000;
	private double P = 0.9;

	public NewLowerLimitController(final HRegionServer _server, final Configuration conf) {
		System.out.println("Controller initialized");
		server = _server;
		lower = defLower = conf.getFloat(LOWER_KEY, 0.25f);
		latencyQueue = new LinkedList<Integer>();
		x = new double[length];
		y = new double[length];
		double step = (HIGHEST_LIMIT - LOWEST_LIMIT) / (length-1);
		for (int i=0; i<length; i++) {
			x[i] = LOWEST_LIMIT + i * step;
		}
		stage = -1;
	}

/*
	private int getMax() {
		int max, tmp;
		while ((tmp = latencyQueue.poll()) && tmp != null) {
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}
*/

	private long computeLower(double worstLatency_) {
		double newLower = lower + (1-P)/alpha * (GOAL - worstLatency_);
		System.out.printf("[PUT],%.2f,%.3f\n", worstLatency_/(double)1000000, lower);
		lower = newLower;
		return (long)(lower * memMax);
	}

	private void computeAlpha() {
		for (int i=0; i<length; i++) {
			System.out.println(i+" "+x[i]+" "+y[i]);
		}
		LinearRegression lg = new LinearRegression(x,y);
		alpha = lg.slope();
		System.out.println("alpha: "+alpha);
	}

	public synchronized long controlEvent() {
		double worstLatency_ = (double)worstLatency;
		worstLatency = 0;
		System.out.println("stage: "+stage);
		//int worstLatency = getMax();
		if (stage < length-1) {
			// profiling
			if (stage>=0) {
				y[stage] = worstLatency_;
			}
			return (long)(x[++stage] * memMax);
		} else if (stage == length-1) {
			y[stage] = worstLatency_;
			computeAlpha();
			stage++;
			return (long)(defLower * memMax);
		} else {
			return computeLower(worstLatency_);
		}
	}

	public synchronized void addLatency(int latency) {
		//this.latencyQueue.add(latency);
		if (latency > worstLatency) {
			worstLatency = latency;
		}
	}

	public void run() {

	}
}

class LinearRegression {
    private final double intercept, slope;
    private final double r2;
    private final double svar0, svar1;

   /**
     * Performs a linear regression on the data points {@code (y[i], x[i])}.
     *
     * @param  x the values of the predictor variable
     * @param  y the corresponding values of the response variable
     * @throws IllegalArgumentException if the lengths of the two arrays are not equal
     */
    public LinearRegression(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("array lengths are not equal");
        }
        int n = x.length;

        // first pass
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
        for (int i = 0; i < n; i++) {
            sumx  += x[i];
            sumx2 += x[i]*x[i];
            sumy  += y[i];
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        slope  = xybar / xxbar;
        intercept = ybar - slope * xbar;

        // more statistical analysis
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = slope*x[i] + intercept;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }

        int degreesOfFreedom = n-2;
        r2    = ssr / yybar;
        double svar  = rss / degreesOfFreedom;
        svar1 = svar / xxbar;
        svar0 = svar/n + xbar*xbar*svar1;
    }

   /**
     * Returns the <em>y</em>-intercept &alpha; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the <em>y</em>-intercept &alpha; of the best-fit line <em>y = &alpha; + &beta; x</em>
     */
    public double intercept() {
        return intercept;
    }

   /**
     * Returns the slope &beta; of the best of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>.
     *
     * @return the slope &beta; of the best-fit line <em>y</em> = &alpha; + &beta; <em>x</em>
     */
    public double slope() {
        return slope;
    }

   /**
     * Returns the coefficient of determination <em>R</em><sup>2</sup>.
     *
     * @return the coefficient of determination <em>R</em><sup>2</sup>,
     *         which is a real number between 0 and 1
     */
    public double R2() {
        return r2;
    }

   /**
     * Returns the standard error of the estimate for the intercept.
     *
     * @return the standard error of the estimate for the intercept
     */
    public double interceptStdErr() {
        return Math.sqrt(svar0);
    }

   /**
     * Returns the standard error of the estimate for the slope.
     *
     * @return the standard error of the estimate for the slope
     */
    public double slopeStdErr() {
        return Math.sqrt(svar1);
    }

   /**
     * Returns the expected response {@code y} given the value of the predictor
     * variable {@code x}.
     *
     * @param  x the value of the predictor variable
     * @return the expected response {@code y} given the value of the predictor
     *         variable {@code x}
     */
    public double predict(double x) {
        return slope*x + intercept;
    }

   /**
     * Returns a string representation of the simple linear regression model.
     *
     * @return a string representation of the simple linear regression model,
     *         including the best-fit line and the coefficient of determination
     *         <em>R</em><sup>2</sup>
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(String.format("%.2f n + %.2f", slope(), intercept()));
        s.append("  (R^2 = " + String.format("%.3f", R2()) + ")");
        return s.toString();
    }

}