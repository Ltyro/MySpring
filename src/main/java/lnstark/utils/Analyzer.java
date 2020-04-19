package lnstark.utils;

import lnstark.utils.context.Context;
import lnstark.utils.context.ContextAware;

public abstract class Analyzer {
	
	protected Context context;

	public Analyzer() {
		context = ContextAware.getContext();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	protected abstract void analyze();
}
