package org.apache.hadoop.hbase.regionserver;

import org.apache.hadoop.conf.Configuration;
import java.lang.management.ManagementFactory;
import org.apache.hadoop.hbase.util.Threads;
import java.util.Queue;
import java.util.LinkedList;

public class LowerLimitController implements Runnable {

	private static final String LOWER_KEY = "hbase.regionserver.global.memstore.lowerLimit";
	private static final float THRESHOLD  = 1000000;
	private static final float SLA_PER = 0.002f;
	private static final float EPSILON = 0.0002f;
	private static final int ADJUST_INTERVAL = 10;
	private static final int QUEUE_SIZE = 50000;
	private static final float LOWEST_LIMIT = 0.1f;
	private static final float HIGHEST_LIMIT = 0.38f;
	private int checkInterval = 1000;
	private long total;
	private long numViolation;
	private MemStoreFlusher msf;
	private HRegionServer server;
	private long memMax = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
	private float defLower;
	private float lower;
	private boolean debugMode = false;
	private Queue<Integer> latencyQueue;
	private int numSinceAdjusted;

	public LowerLimitController(final MemStoreFlusher _msf, final HRegionServer _server, final Configuration conf) {
		System.out.println("Controller initialized");
		total = 0;
		numViolation = 0;
		msf = _msf;
		server = _server;
		lower = defLower = conf.getFloat(LOWER_KEY, 0.25f);
		latencyQueue = new LinkedList<Integer>();
		numSinceAdjusted = 0;
	}

	public synchronized void addLatency(int latency) {
		if (total > QUEUE_SIZE) {
			int old = latencyQueue.poll();
			//System.out.println("removed:"+old);
			if (old > THRESHOLD) {
				numViolation--;
			}
		} else {
			total++;
		}
		
		numSinceAdjusted++;
		latencyQueue.add(latency);

		if (latency > THRESHOLD) {
			numViolation++;
		}

		if (numSinceAdjusted > QUEUE_SIZE) {
			selfAdjust();
			numSinceAdjusted = 0;
		} 

		/*
		if (latency > THRESHOLD) {
			numViolation++;
			if (numSinceAdjusted > total / 2) {
				selfAdjust();
				numSinceAdjusted = 0;
				//System.out.println("queue size: "+latencyQueue.size());
			}
		}
		*/
	}

	private double getPerVio() {
		return (double)numViolation / (double)total;
	}

	// private float getCurLower() {
	// 	return (float)msf.getLower() / (float)memMax; 
	// }

	private void selfAdjust() {
		if(debugMode) {
			System.out.println("check point");
		}
		double perVio = getPerVio();
		float delta = (float)(10 * (SLA_PER - perVio));
		if (perVio < SLA_PER - EPSILON) {
			//System.out.println(delta);
			if (lower + delta <= HIGHEST_LIMIT && lower + delta >= LOWEST_LIMIT) {
				lower += delta * 0.5;
				msf.setLower((long)(memMax*lower));
			}
		} else if (perVio > SLA_PER) {
			if (lower + delta <= HIGHEST_LIMIT && lower + delta >= LOWEST_LIMIT) {
				lower += delta;
				msf.setLower((long)(memMax*lower));
			}
		}
		// if (perVio > SLA_PER) {
		// 	if (debugMode) {
		// 		System.out.println("violation!");
		// 	}
		// 	if (lower-0.01 >= LOWEST_LIMIT) {
		// 		lower-= 0.01;
		// 		msf.setLower((long)(memMax*lower));
		// 	}
		// } else if (perVio < SLA_PER - EPSILON) {
		// 	if (lower+0.01 <= HIGHEST_LIMIT) {
		// 		lower+=0.01;
		// 		msf.setLower((long)(memMax*lower));
		// 	}
		// }

	}

	public void run() {
		while (!this.server.isStopped()) {
			double perVio = getPerVio();
			if (debugMode) {
				System.out.printf("Current Lower: "+lower+" Current Percentage: %.3f%% Current Number of Violation: "+numViolation+'\n', perVio*100);
			} else {
				if (!Double.isNaN(perVio)){
					System.out.printf("data,%.5f,%.3f\n", perVio*100,lower);
				}
			}
			Threads.sleep(checkInterval);
		}
	}
}
