package de.jtem.jrworkspace.plugin.simplecontroller;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StartupChain implements Runnable {
	
	private List<Runnable>
		chain = new ArrayList<Runnable>();
	private int activeJob = 0;
	
	@Override
	public void run() {
		if (activeJob >= chain.size()) {
			synchronized (this) {
				notifyAll();
			}
			return;
		}
		try {
			chain.get(activeJob).run();
		} finally {
			activeJob++;
			EventQueue.invokeLater(this);
		}
	}
	
	public void startDirect() {
		for (Runnable job : chain) {
			job.run();
		}
	}
	
	public synchronized void startQueuedAndWait() {
		EventQueue.invokeLater(this);
		try {
			this.wait();
		} catch (InterruptedException e) {}
	}
	
	public void appendJob(Runnable job) {
		chain.add(job);
	}
	public void appendAll(Collection<Runnable> jobs) {
		chain.addAll(jobs);
	}

}