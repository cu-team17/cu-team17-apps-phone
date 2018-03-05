package cuteam17.cuteam17btlibrary;

/*
 * Copyright (C) 2014 The Android Open Source Project
 * Modifications copyright (C) 2018 Jackson Markowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import cuteam17.cuteam17btlibrary.BtTransferItems.BtTransferItem;

public class BtTransferService extends Service {

	// Debugging
	private static final String TAG = "BtTransferService";

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothTransferSecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE =
			UUID.fromString("10b80536-18d6-11e8-accf-0ed5f89f718b");

	private final BluetoothAdapter mAdapter;
	private Handler mHandler;
	private BtTransferService.AcceptThread mSecureAcceptThread;
	private BtTransferService.ConnectThread mConnectThread;
	private BtTransferService.ConnectedThread mConnectedThread;
	private int mState;
	private int mNewState;
	private int failedConnects = 0;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	public static final String BT_START = "Start";
	public static final String BT_CONNECT = "Connect";
	public static final String BT_STOP = "Stop";
	public static final String BT_WRITE = "Write";

	public static final String INTENT_EXTRA_WRITE = "EXTRA";

	private static final int HEADER_SIZE = 1;
	private static final char EOT = 4;

	public BtTransferService() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mNewState = mState;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		//ToDo: close all threads
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null || intent.getAction() == null) {
			Log.d("Start", "here");
			connectionRestart();
			return START_STICKY;
		}

		switch (intent.getAction()) {
			case BtTransferService.BT_START:
				start();
				break;
			case BtTransferService.BT_CONNECT:
				SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
				String btDeviceAdr = prefs.getString("BT_Connected_Device", null);
				if (btDeviceAdr != null) {
					BluetoothDevice device = mAdapter.getRemoteDevice(btDeviceAdr);
					connect(device);
				}
				break;
			case BtTransferService.BT_STOP:
				stop();
				break;
			case BtTransferService.BT_WRITE:
				try {
					BtTransferItem item = (BtTransferItem) intent.getExtras().getSerializable(INTENT_EXTRA_WRITE);
					write(item, item.type.header);
				} catch (Exception e) {
					return START_STICKY;
				}
				break;
		}

		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	// start AcceptThread to begin a session in listening (server) mode.
	public synchronized void start() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread == null) {
			mSecureAcceptThread = new BtTransferService.AcceptThread();
			mSecureAcceptThread.start();
		}
	}

	public void connectByPref() {
		SharedPreferences prefs = this.getSharedPreferences("cuteam17.phone", Context.MODE_PRIVATE);
		String btDeviceAdr = prefs.getString("BT_Connected_Device", null);
		if (btDeviceAdr != null) {
			BluetoothDevice device = mAdapter.getRemoteDevice(btDeviceAdr);
			connect(device);
		}
	}

	// Start the ConnectThread to initiate a connection to a remote device.
	public synchronized void connect(BluetoothDevice device) {
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		mConnectThread = new BtTransferService.ConnectThread(device);
		mConnectThread.start();
	}

	// Start the ConnectedThread to begin managing a Bluetooth connection
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new BtTransferService.ConnectedThread(socket);
		mConnectedThread.start();

		//device.getName();
	}

	// Stop all threads
	public synchronized void stop() {
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (mSecureAcceptThread != null) {
			mSecureAcceptThread.cancel();
			mSecureAcceptThread = null;
		}

		mState = STATE_NONE;

		//updateUserInterfaceTitle();
	}

	// Write to the ConnectedThread in an unsynchronized manner
	public void write(Object out, char header) {
		BtTransferService.ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
			objectStream.writeObject(out);
			objectStream.close();
		} catch (Exception e) {
			return;
		}
		byte[] outSerialized = outputStream.toByteArray();

		// Perform the write unsynchronized
		r.write(outSerialized, header);
	}

	// Indicate that the connection attempt failed
	protected void connectFailed() {
		mState = STATE_NONE;
		failedConnects++;
		if (failedConnects < 3) {
			//ToDo: wait before reattempting to connect
			try {
				Thread.sleep(2000);
			} catch (Exception e) {}
			connectByPref();
		}

	}

	// Indicate that the connection was lost
	protected void connectionRestart() {
		mState = STATE_NONE;
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted
	 * (or until cancelled).
	 */
	private class AcceptThread extends Thread {
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;

		public AcceptThread() {
			BluetoothServerSocket tmp = null;

			// Create a new listening server socket
			try {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
			} catch (IOException e) {
				Log.e(TAG+"-Accept", "listen() failed");
			}
			mmServerSocket = tmp;
			mState = STATE_LISTEN;
		}

		public void run() {
			setName("AcceptThread");

			BluetoothSocket socket;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG+"-Accept", "accept() failed");
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BtTransferService.this) {
						switch (mState) {
							case STATE_LISTEN:
							case STATE_CONNECTING:
								connected(socket, socket.getRemoteDevice());
								break;
							case STATE_NONE:
							case STATE_CONNECTED:
								// Either not ready or already connected. Terminate new socket.
								try {
									socket.close();
								} catch (IOException e) {
									Log.e(TAG+"-Accept", "close() of unwanted socket failed");
								}
								break;
						}
					}
				}
			}
		}

		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG+"-Accept", "close() of server socket failed", e);
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
			} catch (IOException e) {
				Log.e(TAG+"-Connect", "create() failed");
			}
			mmSocket = tmp;
			mState = STATE_CONNECTING;
		}

		public void run() {
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				mmSocket.connect();
			} catch (IOException e) {
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG+"-Connect", "unable to close() socket during connection failure");
				}

				connectFailed();
				Log.e(TAG+"-Connect", "connect() failed");
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BtTransferService.this) {
				mConnectThread = null;
			}

			failedConnects = 0;
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG+"-Connect", "close() of socket failed");
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG+"-Connected", "temp sockets not created");
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			mState = STATE_CONNECTED;
		}

		public void run() {
			Log.d(TAG+"-Connected", "BEGIN");
			byte[] buffer = new byte[1024];
			int bytes;

			boolean readEOT = false;

			int headerBytesRead = 0;

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ByteArrayOutputStream headerOutputStream = new ByteArrayOutputStream();

			// Keep listening to the InputStream while connected
			while (mState == STATE_CONNECTED) {
				try {
					/*
					try {
						Thread.sleep(10000);
					} catch (Exception e) {}*/
					//Arrays.fill(buffer, (byte)0);
					bytes = mmInStream.read(buffer);

					//ToDo: check for header/EOT errors
					for (int i =0; i < buffer.length; i++) {
						if ((char)buffer[i] == EOT) {
							readEOT = true;
							break;
						}
					}

					int offset = 0;
					if (headerBytesRead < HEADER_SIZE) {
						offset = HEADER_SIZE - headerBytesRead;
						if (bytes < offset) {
							headerOutputStream.write(buffer, 0, bytes);
							headerBytesRead += bytes;
						} else {
							headerOutputStream.write(buffer, 0, offset);
							headerBytesRead += offset;
						}
					}
					if (readEOT) {
						if (offset < bytes) {
							outputStream.write(buffer, offset, bytes-offset-1);

							mHandler.obtainMessage(BtOperations.BT_READ.ordinal(), (int)headerOutputStream.toByteArray()[0], 0, outputStream.toByteArray()).sendToTarget();

							readEOT = false;
							headerBytesRead = 0;
							outputStream = new ByteArrayOutputStream();
							headerOutputStream = new ByteArrayOutputStream();

						}
					} else {
						if (offset < bytes) {
							outputStream.write(buffer, offset, bytes-offset);
						}
					}
				} catch (IOException e) {
					Log.e(TAG+"-Connected", "Disconnected");
					connectionRestart();
					break;
				}
			}
		}

		// Write to the connected OutStream.
		public void write(byte[] buffer, char header) {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				outputStream.write((byte)header);
				outputStream.write(buffer);
				outputStream.write((byte)EOT);

				mmOutStream.write(outputStream.toByteArray());
				//ToDo: make what into constant
				//mHandler.obtainMessage(2, -1, -1, buffer).sendToTarget();
			} catch (IOException e) {
				Log.e(TAG+"-Connected", "Exception during write");
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG+"-Connected", "close() of connected socket failed");
			}
		}
	}
}
