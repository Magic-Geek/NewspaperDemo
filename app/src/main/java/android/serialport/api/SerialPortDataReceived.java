package android.serialport.api;

public interface SerialPortDataReceived {
	public void onDataReceivedListener(final byte[] buffer, final int size); 

}
