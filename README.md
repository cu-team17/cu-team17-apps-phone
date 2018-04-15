# cu-team17-apps-phone
Android applications and library for transferring basic phone items, such as text messages or notifications, to another Android device to be displayed.

## Basic implementation
* **Phone Application**: app running on a phone that will grab incoming text messages, phone call state, and notifications on the device.  The app then passes these items over Bluetooth to a connected device.

* **RPI Application**: app running on the Raspberry Pi (capable of running on any Android device) that will receive items over Bluetooth, from a connected Phone Application, that are then displayed/overlaid on the device.

* **Bluetooth Transfer Library**: library used by both the phone and rpi applications to transfer items between the two.  Offers Bluetooth connection services, basic handler for managing received/sent items, and Bluetooth widget for enabling/disabling Bluetooth connection easily.


## Limitations
These applications and library are designed to be a temporary work around for transferring phone items from one android device to another.  A more realistic implementation would take advantage of the Bluetooth stack and Bluetooth profiles to simplify and streamline the process.  The reason for not going the more realistic implementation route was due to the complexity of it and the limited experience of the senior project team.  Some other specific limitations are listed bellow.

* Phone call audio can not be transferred over, due to the design of Android, but incoming/missed call “notifications” are transferred over.
* Only SMS messages are handled.  MMS could be implemented, but the corresponding data would need to be deserialized.
* The ability to access notifications, on the phone app, requires a special permission granted by the user
* The ability to overlay the transferred items on the screen of the device running the rpi app requires a special permission granted by the user
* Many many more

