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
    private static final ArrayList<String> servers = new ArrayList<>();
    private static final ArrayList<ServerItem> hosts = new ArrayList<>();
    private static ArrayList<Key> masterKey = new ArrayList<>();
    private static final int port = 80;
    private static final String GoodColor = "#589318";
    private static final String QuestionableColor = "#cc9900";
    private static final String BadColor = "#ff6600";
    private static final String CriticalColor = "#cc0000";
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        readBaseHTML();
        readServerHTML();
        getServers();
        printServers();
        try {
            readMasterKeys();
        }catch (Exception e) {
            System.out.println("Error getting keys from key file.");
        }
        System.out.println("Printing Master Keys:");
        printMasterKeys();
        importZones();
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
        Thread updateThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException ex) {
                    }
                    readBaseHTML();
                    readServerHTML();
                    for(ServerItem i: hosts) {
                        i.up(Math.abs(i.lastComm() - System.currentTimeMillis()) < 120000);
                    }
                }
            }
        });
        updateThread.start();
        Scanner reader = new Scanner(System.in);
        while (true) {
            try {
                Thread.sleep(100);
            }catch(Exception e) {}
            if (reader.hasNextLine()) {
                String line = reader.nextLine();
                if(line.equalsIgnoreCase("stop") || line.equals("q")) {
                    System.exit(0);
                }
                if(line.equalsIgnoreCase("emulate")) {
                    emulatePacket();
                }
            }
        }
    }
    
    private static void emulatePacket() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Select a server to send from: ");
        int i = 0;
        for(ServerItem s: hosts) {
            System.out.println(i + ": " + s.getName());
            i++;
        }
        int selection = -1;
        while(!reader.hasNextLine()){}
        try{
            selection = Integer.parseInt(reader.nextLine());
        }catch (Exception e) {
            
        }
        if(selection != -1) {
            ServerItem ser = hosts.get(selection);
            ser.setKey("test", "live");
            ser.lastComm(System.currentTimeMillis());
            ser.up(true);
        }
    }

    private static void readBaseHTML() {
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

    private static void getServers() {
        try {
            File file = new File("db.cslabs");
            file.delete();
            try {
                Runtime.getRuntime().exec("wget --no-check-certificate https://talos.cslabs.clarkson.edu/db.cslabs").waitFor();
            } catch (IOException | InterruptedException ex) {
            }
            while (!file.canRead()) {
            }
            {

            }
            String read;
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
                        servers.add(host);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private static void printServers() {
        for (String host: servers) {
            System.out.println("Server: " + host);
        }
    }
    
    private static void readMasterKeys() throws FileNotFoundException {
        Scanner reader = new Scanner(new File("master.yml"));
        String masterYml = "";
        while(reader.hasNextLine()) {
            masterYml += reader.nextLine() + "\n";
        }
        masterKey = YamlOperator.readKeys(masterYml);
    }
    
    private static void printMasterKeys() {
        for(Key k : masterKey) {
            System.out.println("Key: " + k.getKeyName() + " Value: " + k.getKeyValue());
        }
    }
    
    private static String determineColorSeverity() {
        int down = 0;
        ArrayList<String> critical = new ArrayList<>();
        for(Key k: masterKey) {
            if(k.getKeyName().equalsIgnoreCase("mandatory")) {
                critical.add(k.getKeyValue());
            }
        }
        for(ServerItem i: hosts) {
            for(String s: critical) {
                if(i.getName().equalsIgnoreCase(s)) {
                    if(!i.up()) {
                        down++;
                    }
                }
            }
        }
        if(down == 0) {
            return GoodColor;
        }else if(down < 2) {
            return QuestionableColor;
        }else if(down < 3) {
            return BadColor;
        }else{
            return CriticalColor;
        }
    }
    
    private static void importZones() {
        try {
            File file = new File("db.cslabs");
            file.delete();
            try {
                Runtime.getRuntime().exec("wget --no-check-certificate https://talos.cslabs.clarkson.edu/db.cslabs").waitFor();
            } catch (IOException | InterruptedException ex) {
            }
            while (!file.canRead()) {
            }
            {

            }
            String read;
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
                        ServerItem item = new ServerItem(host, addr);
                        hosts.add(item);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private static ArrayList<String> determineServerList() {
        ArrayList<String> showList = new ArrayList<>();
        for(Key k: masterKey) {
            if(k.getKeyValue().equalsIgnoreCase("mandatory")) {
                showList.add(k.getKeyName());
            }else if(k.getKeyValue().equalsIgnoreCase("optional")) {
                showList.add(k.getKeyName());
            }
        }
        return showList;
    }
    
    private static void printShowServerList() {
        System.out.println("Printing Show Server List:");
        ArrayList<String> showList = determineServerList();
        for(String s: showList) {
            System.out.println("ShowList: " + s);
        }
    }

    private static void server() throws IOException {
        ServerSocket socket = new ServerSocket(port);
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
                    //System.out.println(mode + " " + path);
                }
                
                //checks the origin location to see if it's on the *.145.*
                if(accept.getInetAddress().getAddress()[2] == -111) {
                    for(ServerItem i: hosts) {
                        if(i.getAddress().equals(accept.getInetAddress().getHostAddress())) {
                            String[] parts = path.split("/");
                            i.setKey(parts[0], parts[1].replaceAll("_", " "));
                            i.lastComm(System.currentTimeMillis());
                            i.up(true);
                        }
                    }
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
                    String name = ser.getName();
                    String addr = ser.getAddress();
                    String color = BadColor;
                    if(ser.up()) {
                        color = GoodColor;
                    }
                    int header = server.indexOf("<h1>") + 4;
                    output = server.substring(0, header);
                    output = output.replaceFirst("BGCOLOR", color);
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
                        if(k.getKeyName().equalsIgnoreCase("cpu")) {
                            output += "%";
                        }
                        output += "</td></tr>";
                    }
                } else {
                    int table = html.indexOf("<table>") + 7;
                    output = html.substring(0, table);
                    output = output.replaceFirst("BGCOLOR", determineColorSeverity());
                    output += "<tr><td>Server Name</td><td>IP</td><td>Up?</td><td>Uptime</td></tr>";
                    for (ServerItem host : hosts) {
                        boolean show = false;
                        ArrayList<String> showServers = determineServerList();
                        for(String s: showServers) {
                            if(s.equalsIgnoreCase(host.getName())) {
                                show = true;
                            }
                        }
                        if (show) {
                            output += "<tr><td><a href=\"";
                            output += host.getName();
                            output += "\">";
                            output += host.getName();
                            output += "</a></td><td>";
                            output += host.getAddress();
                            output += "</td><td";
                            if(!host.up()) {
                                output += " style=\"background:#ff6600\" ";
                            }else{
                                output += " style=\"background:#589318\" ";
                            }
                            output += ">";
                            output += host.up();
                            output += "</td><td>";
                            String uptime = host.getKey("uptime");
                            if(uptime.isEmpty()) {
                                output += "erg!";
                            }else{
                                output += host.getKey("uptime");
                            }
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
