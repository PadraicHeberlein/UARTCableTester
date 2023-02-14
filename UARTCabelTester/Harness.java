package UARTCabelTester;

import java.util.ArrayList;
import java.util.List;

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
