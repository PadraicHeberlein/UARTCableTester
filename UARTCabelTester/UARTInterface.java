package UARTCabelTester;

import com.fazecast.jSerialComm.*;

public class UARTInterface
{
	public static final int TEST_MATRIX_SIZE = 64;
	public static final int INSTRUCTIONS_SIZE = 870;
	public static final int TEST_RESULT_SIZE = 7094;

	public static final byte[] ESC = {0x1b, 0x00};
	public static final byte[] ONE = {0x31, 0x00};
	public static final byte[] TWO = {0x32, 0x00};
	public static final byte[] THREE = {0x33, 0x00};
	public static final byte[] FOUR = {0x34, 0x00};

	public static void main (String[] Args)
   	{	
		try
		{
     		SerialPort port = SerialPort.getCommPorts()[0];
			
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
					if (testMatrix[i][j] == '@') System.out.println("wire " + i + " <- * -> wire " + j);
				
					if (testMatrix[i][j] == 'O') System.out.println("wire " + i + " <-----> wire " + j);

					if (testMatrix[i][j] == 'X') System.out.println("wire " + i + " <--X--> wire " + j);
					
					if (testMatrix[i][j] == 'M') System.out.println("wire " + i + " <--?--> wire " + j);
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
