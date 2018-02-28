package cuteam17.cuteam17phone;

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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BtTransferService {

	// Debugging
	private static final String TAG = "BtTransferService ";

	// Name for the SDP record when creating server socket
	private static final String NAME_SECURE = "BluetoothTransferSecure";

	// Unique UUID for this application
	private static final UUID MY_UUID_SECURE =
			UUID.fromString("10b80536-18d6-11e8-accf-0ed5f89f718b");

	private final BluetoothAdapter mAdapter;
	private Handler mHandler;
	private AcceptThread mSecureAcceptThread;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private int mState;
	private int mNewState;

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	private static final int HEADER_SIZE = 1;
	private static final char EOT = 4;


	private static BtTransferService singletonInstance = null;

	private BtTransferService() {
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = STATE_NONE;
		mNewState = mState;
	}

	public static BtTransferService getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new BtTransferService();
		}
		return singletonInstance;
	}

	//ToDo: potentially don't allow handler to be set from outside class
	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	// Update UI title according to the current state of the chat connection
	private synchronized void updateUserInterfaceTitle() {
		mState = getState();
		mNewState = mState;

		// Give the new state to the Handler so the UI Activity can update
		//mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewState, -1).sendToTarget();
	}

	public synchronized int getState() {
		return mState;
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
			mSecureAcceptThread = new AcceptThread();
			mSecureAcceptThread.start();
		}

		//updateUserInterfaceTitle();
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

		mConnectThread = new ConnectThread(device);
		mConnectThread.start();

		//updateUserInterfaceTitle();
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
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		//Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		//bundle.putString(Constants.DEVICE_NAME, device.getName());
		//msg.setData(bundle);
		//mHandler.sendMessage(msg);

		//updateUserInterfaceTitle();
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
	/*
	public void write(byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != STATE_CONNECTED) return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}*/

	public void write(Object out, char header) {
		ConnectedThread r;
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

	// Indicate that the connection attempt failed and notify the UI Activity
	private void connectionFailed() {
		// Send a failure message back to the Activity
		//Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		//bundle.putString(Constants.TOAST, "Unable to connect device");
		//msg.setData(bundle);
		//mHandler.sendMessage(msg);

		mState = STATE_NONE;

		//updateUserInterfaceTitle();

		// Start the service over to restart listening mode
		BtTransferService.this.start();
	}

	// Indicate that the connection was lost and notify the UI Activity.
	private void connectionLost() {
		// Send a failure message back to the Activity
		//Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		//bundle.putString(Constants.TOAST, "Device connection was lost");
		//msg.setData(bundle);
		//mHandler.sendMessage(msg);

		mState = STATE_NONE;
		// Update UI title
		//updateUserInterfaceTitle();

		// Start the service over to restart listening mode
		BtTransferService.this.start();
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
				Log.e(TAG, "listen() failed", e);
			}
			mmServerSocket = tmp;
			mState = STATE_LISTEN;
		}

		public void run() {
			setName("AcceptThread");

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED) {
				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "accept() failed", e);
					break;
				}

				// If a connection was accepted
				if (socket != null) {
					synchronized (BtTransferService.this) {
						switch (mState) {
							case STATE_LISTEN:
							case STATE_CONNECTING:
								// Situation normal. Start the connected thread.
								connected(socket, socket.getRemoteDevice());
								break;
							case STATE_NONE:
							case STATE_CONNECTED:
								// Either not ready or already connected. Terminate new socket.
								try {
									socket.close();
								} catch (IOException e) {
									Log.e(TAG, "close() of unwanted socket failed", e);
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
				Log.e(TAG, "close() of server socket failed", e);
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
				Log.e(TAG, "create() failed", e);
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
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				//ToDo: don't run connectionFailed
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BtTransferService.this) {
				mConnectThread = null;
			}

			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of socket failed", e);
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
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			mState = STATE_CONNECTED;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
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
					Log.e(TAG, "disconnected", e);
					connectionLost();
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
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}
}
