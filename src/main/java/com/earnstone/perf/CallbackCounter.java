package com.earnstone.perf;

public class CallbackCounter extends Counter {
	
	public static interface CounterUpdate {
		public void update(Counter counter);
	}
	
	private CounterUpdate update;
	
	public void setCounterUpdate(CounterUpdate update) {
		this.update = update;
	}
	
	@Override
	public String getDisplayValue() {				
		synchronized (PerfUtils.Formatter) {
			return PerfUtils.Formatter.format(getValue());
		}
	}
	
	@Override
	public double getValue() {
		notifyUpdate();
		return super.getValue();
	}
	
	protected void notifyUpdate() {
		if (update != null) {
			synchronized (this) {
				update.update(this);
			}
		}
	}
}
