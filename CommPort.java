package UARTCabelTester;

import com.fazecast.jSerialComm.*;
import UARTCableTester.Key;

public class CommPortConnection
{
	static final byte[] KEYS = { 0x1b, 0x31, 0x32, 0x33, 0x34 };

	static final int TEST_MATRIX_SIZE = 64;
	static final int INSTRUCTIONS_SIZE = 870;
	static final int TEST_RESULT_SIZE = 7094;

	private byte[] buffer;
	private SerialPort port;

	// Default constructor
	public CommPortConnection() 
	{ 
		try
		{
			port = SerialPort.getCommCommPorts()[0];
			settingsDefault();
		}
		catch (Exception e)
		{
			System.out.println("No available ports! ->");
			System.out.println(e);
		}
	}

	public void settingsDefaul()
	{
		// Settings from cable-tester.com
		port.setBaudRate(115200);
		port.setNumDataBits(8);
		port.setNumStopBits(1);
		port.setParity(SerialPort.NO_PARITY);
	}
	
	public void displayInstructions()
	{	
		int bytesRead = 0;
		String instructions = "";

		while (bytesRead < INSTRUCTIONS_SIZE)
		{
			while (port.bytesAvailable() == 0)
				Thread.sleep(20);

			buffer = new byte[port.bytesAvailable()];
			port.readBytes(buffer, buffer.length);

			for (int i = 0; i < buffer.length; i++)
				message += (char)buffer[i];
				
			bytesRead += buffer.lentgth;
		}

		System.out.println(instructions);
	}

	public void sendKey(enum theKey)
	{
		byte byteToWrite = 0;
		
		switch (theKey)
		{
			case Key.ESC:
				byteToWrite = 0x1b;
				break;
			case Key.ONE:
				byteToWrite = 0x31;
				break;
			case Key.TWO:
				byteToWrite = 0x32;
				break;
			case Key.THREE:
				byteToWrite = 0x33;
				break;
			case Key.FOUR:
				byteToWrite = 0x34;
				break;
			case Key.FIVE:
				byteToWrite = 0x35;
				break;
			case Key.SIX:
				byteToWrite = 0x36;
				break;
		}
		
		buffer = new byte[1];
		buffer[0] = byteToWrite;
		port.writeBytes(buffer, 1);
	}
	
	public void displaySettings()
	{
		System.out.println("number available comm ports : " + SerialPort.getCommPorts().length);
		System.out.println("port : " + port.getDescriptivePortName());
		System.out.println("baud : " + port.getBaudRate());
		System.out.println("data : " + port.getNumDataBits());
		System.out.println("stop : " + port.getNumStopBits());
		System.out.println("prty : " + port.getParity());
		System.out.println("flow : " + port.getFlowControlSettings());
		System.out.println("rdTO : " + port.getReadTimeout());
		System.out.println("wbuf : " + port.getDeviceWriteBufferSize());
		System.out.println("opening port... (" + port.openPort() + ")");
	}

	public static void main (String[] Args)
   	{	
		try
		{
     			port = SerialPort.getCommPorts()[0];
			
			// Settings from cable-tester.com
			port.setBaudRate(115200);
			port.setNumDataBits(8);
			port.setNumStopBits(1);
			port.setParity(SerialPort.NO_PARITY);

			System.out.println("number available comm ports : " + SerialPort.getCommPorts().length);
			System.out.println("port : " + port.getDescriptivePortName());
			System.out.println("baud : " + port.getBaudRate());
			System.out.println("data : " + port.getNumDataBits());
			System.out.println("stop : " + port.getNumStopBits());
			System.out.println("prty : " + port.getParity());
			System.out.println("flow : " + port.getFlowControlSettings());
			System.out.println("rdTO : " + port.getReadTimeout());
			System.out.println("wbuf : " + port.getDeviceWriteBufferSize());
			System.out.println("opening port... (" + port.openPort() + ")");

			port.writeBytes(ESC, 2);
			
			System.out.println("...pressing esc...");

			int bytesRead = 0;
			byte[] buffer;
			String message = "";

			while (bytesRead < INSTRUCTIONS_SIZE)
			{
				while (port.bytesAvailable() == 0)
					Thread.sleep(20);

				int n = port.bytesAvailable();

				buffer = new byte[n];

				port.readBytes(buffer, n);

				for (int i = 0; i < n; i++)
					message += (char)buffer[i];
				
				bytesRead += n;
			}

			System.out.println(message);

			port.writeBytes(ONE, 2);
			System.out.println("...testing...\n");
			Thread.sleep(1000);

			bytesRead = 0;
			String testResult = "";
			char[][] testMatrix = new char[TEST_MATRIX_SIZE][TEST_MATRIX_SIZE];

			port.writeBytes(THREE, 2);

			while (bytesRead < TEST_RESULT_SIZE)
			{
				while (port.bytesAvailable() == 0)
					Thread.sleep(20);

				byte testBuffer[] = new byte[port.bytesAvailable()];
	
				port.readBytes(testBuffer, testBuffer.length);

				for (int i = 0; i < testBuffer.length; i++)
					testResult += (char)testBuffer[i];

				bytesRead += testBuffer.length;
			}

			String[] testRows = testResult.split("\n");

			//System.out.println(testResult);

			int numChars = 0;

			for (int i = 4; i < testRows.length - 2; i++)
			{
				String currentRow = testRows[i];

				for (int j = 0; j < currentRow.length(); j++)
				{
					if (numChars >= TEST_MATRIX_SIZE) break;
				
					char currentChar = testRows[i].charAt(j);
					
					if (currentChar == '-' || currentChar == '.' || 
						currentChar == 'O' || currentChar == 'X' ||
						currentChar == '@' || currentChar == 'M')
					{
						testMatrix[i - 4][numChars] = currentChar;
						numChars++;
					}
				}
				numChars = 0;
			}

			for (int i = 0; i < TEST_MATRIX_SIZE; i++)
			{
				for (int j = i; j < TEST_MATRIX_SIZE; j++)
				{
					char displayData = testMatrix[i][j];

					switch (displayData)
					{
						case '@':
							System.out.println("wire " + i + " <-----> wire " + j);
							break;
						case 'O':
							System.out.println("wire " + i + " <--$--> wire " + j);
							break;
						case 'X':
							System.out.println("wire " + i + " <--X--> wire " + j);
							break;
						case 'M':
							System.out.println("wire " + i + " <--?--> wire " + j);
							break;
					}
				}
			}

			System.out.println("\n...closing port (" + port.closePort() + ")");
		}
		catch (Exception e)
		{
			System.out.println("No available ports! : " + e);
		}	
   	}	
}
