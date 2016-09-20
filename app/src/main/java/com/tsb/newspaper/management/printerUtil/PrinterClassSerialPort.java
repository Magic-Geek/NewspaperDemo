package com.tsb.newspaper.management.printerUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.serialport.api.SerialPortClass;
import android.serialport.api.SerialPortClass.SERIALPORT;
import android.serialport.api.SerialPortDataReceivedPrint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PrinterClassSerialPort implements PrinterClass {
	private static final String TAG = "PrinterClassSerialPort";
	PrintService printservice = new PrintService();
	private Handler mHandler;

	// ������ݶ���
	private WriteThread mWriteThread;
	private List<byte[]> messageList = new ArrayList<byte[]>();
	boolean iswrite = false;
	public static boolean canWrite = true;

	public PrinterClassSerialPort(Handler _mHandler) {
		if (!SerialPortClass.serialPortHelper.IsOpen()) {
			SerialPortClass.serialPortHelper.OpenSerialPort(SerialPortClass.choosed_serial, SerialPortClass.choosed_buad);
		}
		if(!SerialPortClass.serialPortName.equals(SERIALPORT.comPrinter)){
			SerialPortClass.serialPortName=SERIALPORT.comPrinter;
			SerialPortClass.serialPortHelper.Write(SerialPortClass.bt_printer);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mHandler = _mHandler;
		mWriteThread = new WriteThread();
		mWriteThread.start();

		SerialPortClass.serialPortHelper.setOnserialportDataReceivedPrint(new SerialPortDataReceivedPrint() {
			@Override
			public void onDataReceivedListener(byte[] buffer, int size) {
				if (size > 0) {
					if (buffer[0] == 0x08||buffer[0] == 0x01||buffer[0] == 0x04||buffer[0] == 0x02) {//buffer[0] == 0x13||
						canWrite = true;
					}else if (iswrite) {
						if (buffer[0] == 0) {
							canWrite = true;
						} else {
							canWrite = false;
							SerialPortClass.serialPortHelper.Write(new byte[] { 0x0a });
						}
						iswrite = false;
					} else {
						canWrite = true;
					}
					mHandler.obtainMessage(MESSAGE_READ, size,
							-1, buffer).sendToTarget();
				}
			}
		});
	}

	private class WriteThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				for (int i = 0; i < messageList.size(); i++) {
					byte[] buffer = messageList.get(i);
					iswrite = true;
					SerialPortClass.serialPortHelper.Write(new byte[] { 0x1b, 0x76 });
					for (int m = 0; m < 300; m++) {
						canWrite = true;
						if (canWrite) {
							canWrite = false;
							iswrite = false;
							Log.i(TAG, "Wait state time��" + m);

							if (SerialPortClass.serialPortHelper.Write(buffer)) {
								messageList.remove(i);
							}
							break;
						}
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					canWrite = false;
					iswrite = false;
				}
			}
		}
	}

	public boolean setSerialPortBaudrate(int _baudrate) {
		if (SerialPortClass.serialPortHelper.CloseSerialPort()) {
			if (SerialPortClass.serialPortHelper.OpenSerialPort(SerialPortClass.choosed_serial, SerialPortClass.choosed_buad)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean open(Context context) {
		if (SerialPortClass.serialPortHelper.OpenSerialPort(SerialPortClass.choosed_serial, SerialPortClass.choosed_buad)) {
			return true;
		}
		if(!SerialPortClass.serialPortName.equals(SERIALPORT.comPrinter)){
			SerialPortClass.serialPortName=SERIALPORT.comPrinter;
			SerialPortClass.serialPortHelper.Write(SerialPortClass.bt_printer);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean close(Context context) {
		if (mWriteThread != null) {
			mWriteThread = null;
		}
		return true;
	}

	@Override
	public void scan() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Device> getDeviceList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopScan() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean connect(String device) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disconnect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setState(int state) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean IsOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean write(byte[] buffer) {
		/*SerialPortClass.serialPortHelper.Write(new byte[] { 0x1b, 0x76 });
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(!canWrite){
			return false;
		}*/
		if(!SerialPortClass.serialPortName.equals(SERIALPORT.comPrinter)){
			SerialPortClass.serialPortName=SERIALPORT.comPrinter;
			SerialPortClass.serialPortHelper.Write(SerialPortClass.bt_printer);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//return messageList.add(buffer);
		return SerialPortClass.serialPortHelper.Write(buffer);
	}

	@Override
	public boolean printText(String textStr) {
		byte[] buffer = printservice.getText(textStr);
		return write(buffer);
	}

	@Override
	public boolean printImage(Bitmap bitmap) {
		return write(printservice.getImage(bitmap));
	}

	@Override
	public boolean printUnicode(String textStr) {
		return write(printservice.getTextUnicode(textStr));
	}
}