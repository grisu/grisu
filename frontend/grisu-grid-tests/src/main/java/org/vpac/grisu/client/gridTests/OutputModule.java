package org.vpac.grisu.client.gridTests;


public interface OutputModule {
	
	public void writeTestsSetup(String setup);
	
	public void writeTestElement(GridTestElementFactory element);

	public void writeTestsStatistic(String statistic);
	
}
