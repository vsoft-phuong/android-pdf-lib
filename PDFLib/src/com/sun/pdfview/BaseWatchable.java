/*
 * $Id: BaseWatchable.java,v 1.3 2012/04/11 18:14:27 leonardovc Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.sun.pdfview;

/**
 * An abstract implementation of the watchable interface, that is extended
 * by the parser and renderer to do their thing.
 */
public abstract class BaseWatchable implements Watchable {

	/** the current status, from the list in Watchable */
	private int status = Watchable.UNKNOWN;
	/** a lock for status-related operations */
	private Object statusLock = new Object();
	/** when to stop */
	private Gate gate;
	/** suppress local stack trace on setError. */
	private static boolean SuppressSetErrorStackTrace = false;

	/** 
	 * Creates a new instance of BaseWatchable
	 */
	protected BaseWatchable() {
		setStatus(Watchable.NOT_STARTED);
	}

	/**
	 * Perform a single iteration of this watchable.  This is the minimum
	 * granularity which the go() commands operate over.
	 *
	 * @return one of three values: <ul>
	 *         <li> Watchable.RUNNING if there is still data to be processed
	 *         <li> Watchable.NEEDS_DATA if there is no data to be processed but
	 *              the execution is not yet complete
	 *         <li> Watchable.COMPLETED if the execution is complete
	 *  </ul>
	 */
	protected abstract int iterate() throws Exception;

	/** 
	 * Prepare for a set of iterations.  Called before the first iterate() call
	 * in a sequence.  Subclasses should extend this method if they need to do
	 * anything to setup.
	 */
	protected void setup() {
		// do nothing
	}

	/**
	 * Clean up after a set of iterations. Called after iteration has stopped
	 * due to completion, manual stopping, or error.
	 */
	protected void cleanup() {
		// do nothing
	}


	/**
	 * Get the status of this watchable
	 *
	 * @return one of the well-known statuses
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Return whether this watchable has finished.  A watchable is finished
	 * when its status is either COMPLETED, STOPPED or ERROR
	 */
	public boolean isFinished() {
		int s = getStatus();
		return (s == Watchable.COMPLETED ||
				s == Watchable.ERROR);
	}

	/**
	 * return true if this watchable is ready to be executed
	 */
	public boolean isExecutable() {
		return ((status == Watchable.PAUSED || status == Watchable.RUNNING) &&
				(gate == null || !gate.stop()));
	}

	/**
	 * Stop this watchable.  Stop will cause all processing to cease,
	 * and the watchable to be destroyed.
	 */
	public void stop() {
		setStatus(Watchable.STOPPED);
	}

	/**
	 * Start this watchable and run in a new thread until it is finished or
	 * stopped.
	 * Note the watchable may be stopped if go() with a
	 * different time is called during execution.
	 */
	public  void go() {
		gate = null;
		try{
			execute(false);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start this watchable and run until it is finished or stopped.
	 * Note the watchable may be stopped if go() with a
	 * different time is called during execution.
	 *
	 * @param synchronous if true, run in this thread
	 * @throws Exception 
	 */
	public  void go(boolean synchronous) throws Exception {
		gate = null;
		execute(synchronous);
	}

	/**
	 * Start this watchable and run for the given number of steps or until
	 * finished or stopped.
	 *
	 * @param steps the number of steps to run for
	 */
	public  void go(int steps) {
		gate = new Gate();
		gate.setStopIterations(steps);

		try {
			execute(false);
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Start this watchable and run for the given amount of time, or until
	 * finished or stopped.
	 *
	 * @param millis the number of milliseconds to run for
	 * @throws Exception 
	 */
	public  void go(long millis) throws Exception {
		gate = new Gate();
		gate.setStopTime(millis);

		execute(false);
	}


	/**
	 * Start executing this watchable
	 *
	 * @param synchronous if true, run in this thread
	 * @throws Exception 
	 */
	protected void execute(boolean synchronous) throws Exception {
		// call setup once we started
		if (getStatus() == Watchable.NOT_STARTED) {
			setup();
		}

		setStatus(Watchable.PAUSED);


		while (!isFinished() && getStatus() != Watchable.STOPPED) {
			if (isExecutable()) {
				// set the status to running
				setStatus(Watchable.RUNNING);

				try {
					// keep going until the status is no longer running,
					// our gate tells us to stop, or no-one is watching
					while ((getStatus() == Watchable.RUNNING) &&
							(gate == null || !gate.iterate())) {
						// update the status based on this iteration
						setStatus(iterate());
					}

					// make sure we are paused
					if (getStatus() == Watchable.RUNNING) {
						setStatus(Watchable.PAUSED);
					}
				} catch (Exception ex) {
					setError(ex);
					throw ex;
				}
			}
		}


		// System.out.println(Thread.currentThread().getName() + " exiting: status = " + getStatusString());

		// call cleanup when we are done
		if (getStatus() == Watchable.COMPLETED ||
				getStatus() == Watchable.ERROR) {

			cleanup();
		}
	}

	/**
	 * Set the status of this watchable
	 */
	protected void setStatus(int status) {
		synchronized (statusLock) {
			this.status = status;

			// System.out.println(getName() + " status set to " + getStatusString());

			statusLock.notifyAll();
		}
	}

	/**
	 * return true if we would be suppressing setError stack traces.
	 * 
	 * @return  boolean
	 */
	public static boolean isSuppressSetErrorStackTrace () {
		return SuppressSetErrorStackTrace;
	}

	/**
	 * set suppression of stack traces from setError.
	 * 
	 * @param suppressTrace
	 */
	public static void setSuppressSetErrorStackTrace(boolean suppressTrace) {
		SuppressSetErrorStackTrace = suppressTrace;
	}

	/**
	 * Set an error on this watchable
	 */
	protected void setError(Exception error) {
		if (!SuppressSetErrorStackTrace) {
			error.printStackTrace();
		}

		setStatus(Watchable.ERROR);
	}


	/** A class that lets us give it a target time or number of steps,
	 * and will tell us to stop after that much time or that many steps
	 */
	class Gate {

		/** whether this is a time-based (true) or step-based (false) gate */
		private boolean timeBased;
		/** the next gate, whether time or iterations */
		private long nextGate;

		/** set the stop time */
		public void setStopTime(long millisFromNow) {
			timeBased = true;
			nextGate = System.currentTimeMillis() + millisFromNow;
		}

		/** set the number of iterations until we stop */
		public void setStopIterations(int iterations) {
			timeBased = false;
			nextGate = iterations;
		}

		/** check whether we should stop.
		 */
		public boolean stop() {
			if (timeBased) {
				return (System.currentTimeMillis() >= nextGate);
			} else {
				return (nextGate < 0);
			}
		}

		/** Notify the gate of one iteration.  Returns true if we should
		 * stop or false if not
		 */
		public boolean iterate() {
			if (!timeBased) {
				nextGate--;
			}

			return stop();
		}
	}
}
