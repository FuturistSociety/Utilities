/**
 * @author Steven L. Moxley
 * @version 1.0
 */
package org.futurist.util;

import java.io.File;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.futurist.util.jobs.DeadlineQueue;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class QueueTest {

	private ConcurrentLinkedQueue<Object> concurrentLinkedQ;
	private LinkedBlockingQueue<Object> linkedBlockQ;
	private PriorityQueue<Object> priorityQ;
	private DeadlineQueue<Object> deadlineQ;
	private ArrayList<Queue<Object>> queues;
	private ConcurrentHashMap<Integer, String> queueNameMap;
	private long[] times;
	
	public QueueTest() {
		concurrentLinkedQ = new ConcurrentLinkedQueue<Object>();
		linkedBlockQ = new LinkedBlockingQueue<Object>();
		priorityQ = new PriorityQueue<Object>();
		deadlineQ = new DeadlineQueue<Object>(1);
	
		queues = new ArrayList<Queue<Object>>();
		queues.add(concurrentLinkedQ);
		queues.add(linkedBlockQ);
		queues.add(priorityQ);
		//queues.add(deadlineQ);
		
		times = new long[queues.size()];
		for(int i = 0; i < times.length; i++) {
			times[i] = 0;
		}
		
		queueNameMap = new ConcurrentHashMap<Integer, String>();
		queueNameMap.put(0, "Concurrent Linked Queue");
		queueNameMap.put(1, "Linked Blocking Queue");
		queueNameMap.put(2, "Priority Queue");
		//queueNameMap.put(3, "Deadline Queue");
	}
	
	public void loadData(AbstractCollection data) {
		for(Queue<Object> q : queues) {
			q.addAll(data);
		}
	}
	
	public void processQueues() {
		for(int i = 0; i < queues.size(); i++) {
			Long startTime = System.currentTimeMillis();
			Queue<Object> q = queues.get(i);
			while(!q.isEmpty()) {
				q.poll();
			}
			times[i] = System.currentTimeMillis() - startTime;
		}
	}
	
	public long[] getTimes() {
		return times;
	}
	
	public JFreeChart createXYGraph(long[] intData, long[] doubleData, long[] stringData) {
		
		XYSeries intSeries = new XYSeries("Integer Test");
		for(int i = 0; i < intData.length; i++) {
			intSeries.add(i, new Double(intData[i]));
		}
		XYSeries doubleSeries = new XYSeries("Double Test");
		for(int i = 0; i < doubleData.length; i++) {
			doubleSeries.add(i, new Double(doubleData[i]));
		}
		XYSeries stringSeries = new XYSeries("String Test");
		for(int i = 0; i < stringData.length; i++) {
			stringSeries.add(i, new Double(stringData[i]));
		}
		XYSeriesCollection seriesCollection = new XYSeriesCollection();
		seriesCollection.addSeries(intSeries);
		seriesCollection.addSeries(doubleSeries);
		seriesCollection.addSeries(stringSeries);

		ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
		XYPlot plot = new XYPlot(seriesCollection, new NumberAxis("Test #"), new NumberAxis("Time (ms)"), renderer);
		return new JFreeChart("Queue Test", plot);
	}
	
	public JFreeChart createCategoryGraph(long[] intData, long[] doubleData, long[] stringData) {
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < intData.length; i++) {
			dataset.addValue(intData[i], "Integer Test", queueNameMap.get(i));
		}
		for(int i = 0; i < doubleData.length; i++) {
			dataset.addValue(doubleData[i], "Double Test", queueNameMap.get(i));
		}
		for(int i = 0; i < stringData.length; i++) {
			dataset.addValue(stringData[i], "String Test", queueNameMap.get(i));
		}
		/*
		for(int i = 0; i < intData.length; i++) {
			dataset.addValue(intData[i], queueNameMap.get(i), "Integer Test");
		}
		for(int i = 0; i < doubleData.length; i++) {
			dataset.addValue(doubleData[i], queueNameMap.get(i), "Double Test");
		}
		for(int i = 0; i < stringData.length; i++) {
			dataset.addValue(stringData[i], queueNameMap.get(i), "String Test");
		}
		*/
		
		BarRenderer renderer = new BarRenderer();
		CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis("Queue Type"), new NumberAxis("Time (ms)"), renderer);
		return new JFreeChart("Queue Test", plot);
	}
	
	public static void main(String[] args) throws IOException {
		int dataSize = 1000000;
		Random rng = new Random();
		QueueTest test = new QueueTest();
		
		System.out.println("Running Integer Test...");
		ArrayList<Integer> testInts = new ArrayList<Integer>(dataSize);
		for(int i = 0; i < dataSize; i++) {
			testInts.add(rng.nextInt());
		}
		test.loadData(testInts);
		//System.out.println("Finished loading test ints...");
		test.processQueues();
		//System.out.println("Finished processing int queues...");
		long[] intTimes = test.getTimes();
		
		System.out.println("Running Double Test...");
		ArrayList<Double> testDoubles = new ArrayList<Double>(dataSize);
		for(int i = 0; i < dataSize; i++) {
			testDoubles.add(rng.nextDouble());
		}
		test.loadData(testDoubles);
		test.processQueues();
		long[] doubleTimes = test.getTimes();
		
		System.out.println("Running String Test...");
		ArrayList<String> testStrings = new ArrayList<String>(dataSize);
		for(int i = 0; i < dataSize; i++) {
			testStrings.add(Integer.toString(rng.nextInt()));
		}
		test.loadData(testStrings);
		test.processQueues();
		long[] stringTimes = test.getTimes();
		
		//String saveDir = "/Users/steve/Downloads/";
		String saveDir = "C:\\Users\\smoxley\\Desktop\\";
		JFreeChart xyGraph = test.createXYGraph(intTimes, doubleTimes, stringTimes);
		ChartUtilities.saveChartAsPNG(new File(saveDir+"XY Graph.png"), xyGraph, 1024, 768);
		JFreeChart categoryGraph = test.createCategoryGraph(intTimes, doubleTimes, stringTimes);
		ChartUtilities.saveChartAsPNG(new File(saveDir+"Category Graph.png"), categoryGraph, 1024, 768);
		System.out.println("Saved graph images.  Reached EOF.");
	}

}