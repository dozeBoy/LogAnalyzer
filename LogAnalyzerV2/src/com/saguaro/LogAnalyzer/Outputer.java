package com.saguaro.LogAnalyzer;

import java.util.Timer;
import java.util.TimerTask;


public class Outputer {
	
	private static Timer _timer;
	
	public static void startAnimation() {
		_timer = new Timer();
		_timer.schedule(new AnimationThread(), 0, 100);
	}

	public static void stopAnimation() {
		_timer.cancel();

	}
	
	private static class AnimationThread extends TimerTask {
		private boolean isStarted = false;
		private boolean isOdd = false;
		private int numberOfRuns = 0;

		@Override
		public void run() {
			if (numberOfRuns > 6) {
				numberOfRuns = 0;
				System.out.print("\r");
				System.out.print("                       ");
				System.out.print("\r");
			} else if (numberOfRuns <= 3) {
				System.out.print(".");
			}
			numberOfRuns++;
		}

		public void stop() {
			isStarted = false;
		}

		public void start() {
			isStarted = true;
		}

	}

	
	

	
}
