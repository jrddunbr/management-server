package org.ja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jared
 */
public class ManagementServer {

    private static String html = "";
    private static String server = "";
    private static final ArrayList<ServerItem> hosts = new ArrayList<>();

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //we can expect HTML after these two lines
        readHTML();
        readServerHTML();
        //we can expect hosts to work after these two lines
        importZones();//after this we can expect there to be hosts in the array list
        Thread serverthread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    //we do this just to make sure that it keeps running always!
                    try {
                        server();
                    } catch (IOException ex) {
                    }
                }
            }
        });
        serverthread.start();
        Thread updateHTMLThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException ex) {
                    }
                    readHTML();
                    readServerHTML();
                }
            }
        });
        updateHTMLThread.start();
        Scanner reader = new Scanner(System.in);
        while (true) {
            if (reader.hasNext()) {
                System.exit(0);
            }
        }
    }

    private static void readHTML() {
        try {
            File file = new File("index.html");
            Scanner reader = new Scanner(file);
            String read = "";
            while (reader.hasNextLine()) {
                read += reader.nextLine();
            }
            if(!html.equals(read)) {
                html = read;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void readServerHTML() {
        try {
            File file = new File("server.html");
            Scanner reader = new Scanner(file);
            String read = "";
            while (reader.hasNextLine()) {
                read += reader.nextLine();
            }
            if(!server.equals(read)) {
                server = read;
                System.out.println("Updated Server HTML Template");
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void importZones() {
        try {
            File file = new File("db.cslabs");
            file.delete();
            try {
                Runtime.getRuntime().exec("wget https://talos.cslabs.clarkson.edu/db.cslabs").waitFor();
            } catch (IOException | InterruptedException ex) {
            }
            while (!file.canRead()) {
            }
            {

            }
            String read = "";
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                read = reader.nextLine();
                read = read.trim();
                if (read.length() > 1) {
                    char first = read.charAt(0);

                    if (Character.isAlphabetic(first) && read.contains("IN A")) {
                        int end = read.indexOf('\t');
                        int begin = read.indexOf("IN A") + 4;
                        String host = read.substring(0, end);
                        String addr = read.substring(begin, read.length()).trim();
                        ServerItem si = new ServerItem(host, addr);
                        hosts.add(si);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private static void printHosts() {
        for (ServerItem host : hosts) {
            System.out.println("Address: " + host.getAddress() + "\tHost: " + host.getName());
        }
    }

    private static void server() throws IOException {
        ServerSocket socket = new ServerSocket(8080);
        System.out.println("Starting Web Server");
        //made a server, let's get the clients.
        while (true) {
            boolean isServerRoom = false;
            Socket accept = socket.accept();
            byte[] client = accept.getLocalAddress().getAddress();
            if ((client[0] == 128) && (client[1] == 153) && (client[2] == 145)) {
                isServerRoom = true;
            }
            Scanner in = new Scanner(accept.getInputStream());
            PrintStream out = new PrintStream(accept.getOutputStream());
            String command = "";
            if (in.hasNextLine()) {
                command = in.nextLine();
            }
            //System.out.println("[" + command + "]");//raw headers from browser
            if (command.isEmpty()) {
                System.out.println("Closing Connection - erronous request.");
                accept.close();
            } else {
                String mode = command.substring(0, command.indexOf(" "));
                String path = command.substring(command.indexOf(" ") + 1, command.indexOf(" ", command.indexOf(" ") + 1));
                path = path.substring(1).trim();
                //print out anything that is a command or path.
                if (!path.equalsIgnoreCase("favicon.ico") && !path.isEmpty()) {
                    System.out.println(mode + " " + path);
                }
                boolean isServer = false;
                ServerItem ser = null;
                for (ServerItem i : hosts) {
                    if (path.equalsIgnoreCase(i.getName())) {
                        isServer = true;
                        ser = i;
                    }
                }

                String output;
                if (isServer) {
                    output = "";
                    String name = ser.getName();
                    String addr = ser.getAddress();

                    int header = server.indexOf("<h1>") + 4;
                    output = server.substring(0, header);
                    output += name;
                    int table = server.indexOf("<table>") + 7;
                    output += server.substring(header, table);
                    output += "<tr><td class=\"thead\">";
                    output += name;
                    output += "</td><td>";
                    output += addr;
                    output += "</td></tr>";
                    for (Key k : ser.getKeys()) {
                        output += "<tr><td>";
                        output += k.getKeyName();
                        output += "</td><td>";
                        output += k.getKeyValue();
                        output += "</td></tr>";
                    }
                } else {
                    output = "";
                    int table = html.indexOf("<table>") + 7;
                    output = html.substring(0, table);
                    output += "<tr><td>Server Name</td><td>IP</td><td>Up?</td><td>Uptime</td></tr>";
                    for (ServerItem host : hosts) {
                        if (!host.getKey("disable").equalsIgnoreCase("disable")) {
                            output += "<tr><td><a href=\"";
                            output += host.getName();
                            output += "\">";
                            output += host.getName();
                            output += "</a></td><td>";
                            output += host.getAddress();
                            output += "</td></tr>";
                        }
                    }
                }

                /* 
                 If we are in the root action directory (or approved directory),
                 then return some info, otherwise do a 302 redirect to known territory
                 Typically we leave known territory when making requests, and they
                 bump us right back to the root thanks to the 302. The browser gives us the
                 ability to collect data thanks to the redirect since it calls a function
                 from the path that we specified in the initial redirect link that we click.
                 */
                if (path.isEmpty() || isServer) {
                    //basic HTTP response with success. Makes the browser happy.
                    out.println("HTTP/1.1 200 OK");
                    out.println("Connection: close");
                    out.println("Content-Type: text/html");
                    out.println("Content-Length: " + output.length());
                    out.println();
                    out.println(output);
                } else if (path.equals("favicon.ico")) {
                    //redirect to the favicon to make the browser happy.
                    //the user doesn't see anything.
                    out.println("HTTP/1.1 302 Found");
                    out.println("Location: http://clarkson.edu/favicon.ico");
                    //well, browsers like to get favicons so let's just not.
                    //out.println("HTTP/1.1 400 NOT FOUND");

                } else {
                    //redirect to the root directory within the browser,
                    //the user doesn't see anything.
                    out.println("HTTP/1.1 302 Found");
                    out.println("Location: /");
                }
                accept.close();
            }
        }
    }
}
