import java.net.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

// This program does 2 functions.
// 1. Get all the network interfaves and its IP addresses for this host (host on which program is run)
// 2. Get the IP addresses of a list of hosts from a text file and writes the results to an output file

public class InetAddressExample {

    public static void main(String[] args) {
        //Get network interface and its IP addresses for this host

        try {
            Enumeration<NetworkInterface> interfaceList = NetworkInterface.getNetworkInterfaces();
            if(interfaceList == null) {
                System.out.println("No Interfaces found");
            }

            while(interfaceList.hasMoreElements()) {
                NetworkInterface iface = interfaceList.nextElement();
                System.out.println("Interface " + iface.getName() + ":");
                Enumeration<InetAddress> addrList = iface.getInetAddresses();
                if(!addrList.hasMoreElements()) {
                    System.out.println("No address for this interface");
                }
                while(addrList.hasMoreElements()) {
                    InetAddress address = addrList.nextElement();
                    System.out.print("\tAddress "
                        + (address instanceof Inet4Address ? "(v4)"
                            : (address instanceof Inet6Address ? "(v6)" : "(?)")));
                    System.out.println(address.getHostAddress());
                }
            }
            
        } catch (SocketException se) {
            System.out.println("Error getting network interfaces" + se.getMessage());
        }

        // Get name(s)/address(es) of hosts from input file. The program takes 1 or 2 arguments.
        // Checks for 1 or 2 arguments. If this is not true, the program exits with message saying
        // 2 arguments are expected.
        // The list of hosts are provided in the first argument as an input text file.
        // The IP addresses are written to the output file that is given as the second argument.

        List<String> output = new ArrayList<String>();

        System.out.println("number of arguments: " + args.length);
        List<String> listofhosts = new ArrayList<String>();
        if((args.length > 2) || (args.length == 0))  {
            System.out.println("Please provide 2 arguments, 1. input filename and 2. output filename");
        }
        else {
            try {
                Path path = Paths.get(args[0]);
                System.out.println("Absolute path: " + path.toAbsolutePath());
                listofhosts = Files.readAllLines(path);
                
                for(String host: listofhosts) {
                    try {
                        output.add(host + ":");
                        InetAddress[] addressList = InetAddress.getAllByName(host);
                        for(InetAddress address: addressList) {
                            output.add("\t" + address.getHostName() + "/" + address.getHostAddress());
                        }
                    } catch (UnknownHostException e) {
                        System.out.println("\tUnable to find address for " + host);
                    }
                }
                
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
            if (args.length == 2) {
                try {
                    Files.write(Paths.get(args[1]), output, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            } 
            else {
                for(String host: listofhosts) {
                    System.out.println(host);
                }
            }
        }
    }
}
