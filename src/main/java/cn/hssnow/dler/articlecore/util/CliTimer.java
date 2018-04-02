package cn.hssnow.dler.articlecore.util;

import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CliTimer {
	private static ExecutorService singleThreadPool = Executors.newCachedThreadPool();
    private static Future<Double> result;

    private String staticText;
    private String endText;
    private boolean endFlag = false;
    private boolean isError;
    private DecimalFormat format;
    
    private long delay;
    
    public CliTimer(String staticText, String endText, long delay, int decimal) {
        this.staticText = staticText;
        this.endText = endText;
        this.delay = delay;

        StringBuilder sb = new StringBuilder(decimal <= 0 ? "#" : "#.");
        for (int i = 0; i < decimal; i++) {
            sb.append("#");
        }
        format = new DecimalFormat(sb.toString());
    }

    public void start() {
		result = singleThreadPool.submit(() -> {
			long start = System.currentTimeMillis();

			double t = 0.0;
			endFlag = false;

			while (!endFlag && !isError) {
				t = Double.parseDouble(format.format((System.currentTimeMillis() - start) / 1000.0));
				System.out.print("\r");
				System.out.print(staticText + "\tTime: " + t + " s");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					break;
				}
			}

			if (endFlag) {
				System.out.println("\t" + endText);
			} else if (isError) {
				System.out.println("\tError!");
			}

			return t;
		});
	}

	public double error() {
    	isError = true;
    	try {
    		return result.get();
		} catch (InterruptedException | ExecutionException e) {
			return 0;
		}
	}

	public double stop() {
    	endFlag = true;
		try {
			return result.get();
		} catch (InterruptedException | ExecutionException e) {
			return 0;
		}
	}
}
