# P2P-File-Sharing

## Terminology

* Peer: network node that joins file sharing service, which can be downloader or uploader.
* Downloader: peer that downloads files from others.
* Uploader: peer that shares file for others to download.
* Tracker: A centralized server that stores the list of shared files and the list of sharing peer for a specific file. A median that uploaders and downloaders can come up to register for files they want to share or to look for files they want to download.
* Swarm: a network of peers (uploaders) being sharing the same file.

## Abstract operation

### Tracker-Peer

* Peers connect to tracker via a fixed port (well known by all peers) using TCP/IP protocol.
* When a peer (downloader) wants to download a file:
  1. Connects to tracker, requires a list of files being shared.
  2. Chooses the file it wants to download and sends that file information to tracker.
  3. Tracker will response the list of peers (uploaders) that is sharing that file. Peer downloader will connect to the peer uploaders in the list to get (download) the file.
* When a peer (uploader) wants to upload a file:
  1. Connects to tracker, requires to upload a file and sends that file information.
  2. When sharing a file, peer uploader must periodically sends message "keep alive" to tracker to indicate that it is still ready for file sharing.

### Peer-Peer

* Peer downloader will connect with peer uploaders in its list (got from tracker) to download pieces of the file.
* When a peer downloader downloads a file from peer uploaders, base on the number of peer uploaders sharing the file, peer downloader will split the file size into equal pieces and download from each peer uploader some pieces (the number of pieces downloaded from each peer uploader will be equal).
   * A piece size is about 64KB, 128KB, 1MB, ...
* If a peer downloader discover a peer uploader dies (when downloading is in progress), it will transfer file pieces downloaded from that peer uploader to remaining online peer uploaders.

## Identifier information

### File information

* Contains necessary information about file that is used for data exchange between peers (uploader and downloader) and between peer and tracker.
  * Name: file name.
  * Size: file size.
  * Hash: SHA1 of a file (based on it content), it can be used as a file identifier and to determine that files with different names but hava the same content is the only one file.

### Peer information

* Contains necessary information about a peer that is used to data exchange between peers (uploader and downloader) and between peer and tracker.
  * ID: peer identifier, a six-number number which is created randomly.
  * IP: ip address which the program resides.
  * Port: peer's waiting port for other peer to connect to download files (this field is only available for uploader peer).
   * Alive: the amount of time that peer uploader uses to indicate when it will send a "keep alive" message to tracker.

## Data fields

### Tracker-Peer

|  PEER_ID - 6 bytes  |  HASH - 40 bytes  |  FILE_NAME - variable  |  FILE_SIZE - 8 bytes  | MESSAGE (string) - 10 bytes  |
|---------------------|-------------------|------------------------|-----------------------|------------------------------|

### Peer-Peer

* From peer downloader to peer uploader:

|  HASH - 40 bytes  | MESSAGE (string) - 10 bytes  | PIECE - 4 bytes  |
|---------------------|-------------------|------------------------|

* From peer uploader to peer downloader:

|  PEER_ID - 6 bytes  |  MESSAGE (string) - 10 bytes  |  PIECE - 4 bytes  |  DATA - 128KB  |
|---------------------|-------------------|------------------------|-----------------------|
