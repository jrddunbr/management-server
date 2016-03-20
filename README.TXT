#Management Server

Management Server is used to run the management application. It recieves packets from the TCP version of management-client and serves out webpages with stats on those machines, as well as reading battery backups on the localhost.

The configuration files are a bit of a mess.

##index.html

This file contains the basic HTML code of the website. This page is automatically updated when there are updates to the file when it diffs against what is in memory.

##master.yml

This file contains server key files. under a tag called keys, it looks at servernames and then looks to see whether they are manadatory or optional.

If they are not listed, they will not show up in the master list.

##networks.txt

This file contains pairs of hostnames of servers and their ip's seperated by a colon with no spaces.


There is also a servers folder that contains all of the server names with a post .txt on them. In these files you can specify the maintianers of the servers.

The format is last name, fist name, email, irc, and cell phone.

It will look like this:

f:
l:
e:
i:
c:

and you add the information after the colon. The first two characters are never read.
