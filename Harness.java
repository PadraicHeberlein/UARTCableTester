package UARTCableTester;

import java.uitl.Collections;
import HarnessTerminal;

public class Harness
{
	private String name;
	private List<HarnessTerminal> terminals;

	public Harness(String theName)
	{
		name = theName;
		terminals = new ArrayList<HarnessTerminal>();
	}
}
