package eai;

import java.net.*;
import java.io.*;

public class ConsoleClient {

    public static void main(String[] args) {
        Socket clientSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        BufferedReader userIn = null;

        try {
            clientSocket = new Socket("localhost", 4444);
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
            socketIn = new BufferedReader(
                    new InputStreamReader(
                    clientSocket.getInputStream()));

            userIn = new BufferedReader(new InputStreamReader(System.in));

            String location;
            String action;
            String action_res;
            int customerID;
            String customerID_str;
            int addressID;
            String addressID_str;
            String customer_set;
            String username, password, firstname, lastname, email;
            String street, city, zipcode, country;
            String tokenSeq;

            while (true) {
                location = "";
                action = "";
                action_res = "";
                customerID = -1;
                customerID_str = "";
                addressID = -1;
                addressID_str = "";
                customer_set = "";
                username = "";
                password = "";
                firstname = "";
                lastname = "";
                email = "";
                street = "";
                city = "";
                zipcode = "";
                country = "";

                // get location {Zurich, Bern}
                while (!location.equals("Z") && !location.equals("B")
                        && !location.equals("exit") && !location.equals("restart")) {
                    System.out.print("Are you from (Z)urich or (B)ern? ");
                    location = userIn.readLine();
                }

                if (location.equals("exit")) {
                    break;
                }

                if (location.equals("restart")) {
                    continue;
                }

                // get basic user action {Create, View, Update, Delete}
                while (!action.equals("C") && !action.equals("V")
                        && !action.equals("U") && !action.equals("D")
                        && !action.equals("exit") && !action.equals("restart")) {
                    System.out.print("What do you want to do? (C)reate, (V)iew,"
                            + " (U)pdate, (D)elete? ");
                    action = userIn.readLine();
                }

                if (action.equals("exit")) {
                    break;
                }

                if (action.equals("restart")) {
                    continue;
                }

                // get action resource {Customer, Address}
                if (action.equals("C") || action.equals("U") || action.equals("D")) {
                    while (!action_res.equals("C") && !action_res.equals("A")
                            && !action_res.equals("exit")
                            && !action_res.equals("restart")) {
                        System.out.print("(C)ustomer or (A)ddress? ");
                        action_res = userIn.readLine();
                    }

                    if (action_res.equals("exit")) {
                        break;
                    }

                    if (action_res.equals("restart")) {
                        continue;
                    }
                }

                // get Customer ID
                if (action.equals("U") || action.equals("D")
                        || (action.equals("C") && action_res.equals("A"))) {
                    while (customerID == -1 && !customerID_str.equals("exit")
                            && !customerID_str.equals("restart")) {
                        System.out.print("Customer ID: ");
                        customerID_str = userIn.readLine();

                        try {
                            customerID = Integer.parseInt(customerID_str);
                        } catch (NumberFormatException nfe) {
                        }
                    }

                    if (customerID_str.equals("exit")) {
                        break;
                    }

                    if (customerID_str.equals("restart")) {
                        continue;
                    }
                }

                // get Address ID
                if (action_res.equals("A") && !action.equals("C")) {
                    while (addressID == -1 && !addressID_str.equals("exit")
                            && !addressID_str.equals("restart")) {
                        System.out.print("Address ID: ");
                        addressID_str = userIn.readLine();

                        try {
                            addressID = Integer.parseInt(addressID_str);
                        } catch (NumberFormatException nfe) {
                        }
                    }

                    if (addressID_str.equals("exit")) {
                        break;
                    }

                    if (addressID_str.equals("restart")) {
                        continue;
                    }
                }

                // get Customer set to be viewed
                if (action.equals("V")) {
                    while (!customer_set.equals("A")
                            && !customer_set.equals("Z")
                            && !customer_set.equals("B")
                            && !customer_set.equals("exit")
                            && !customer_set.equals("restart")) {
                        System.out.print("(A)ll customers, or those from "
                                + "(Z)urich or (B)ern? ");
                        customer_set = userIn.readLine();
                    }

                    if (customer_set.equals("exit")) {
                        break;
                    }

                    if (customer_set.equals("restart")) {
                        continue;
                    }
                }

                // get Customer info
                if (action_res.equals("C")
                        && (action.equals("C") || action.equals("U"))) {
                    while (username.equals("")) {
                        System.out.print("username: ");
                        username = userIn.readLine();
                    }

                    if (username.equals("exit")) {
                        break;
                    }

                    if (username.equals("restart")) {
                        continue;
                    }

                    while (password.equals("")) {
                        System.out.print("password: ");
                        password = userIn.readLine();
                    }

                    if (password.equals("exit")) {
                        break;
                    }

                    if (password.equals("restart")) {
                        continue;
                    }

                    while (firstname.equals("")) {
                        System.out.print("first name: ");
                        firstname = userIn.readLine();
                    }

                    if (firstname.equals("exit")) {
                        break;
                    }

                    if (firstname.equals("restart")) {
                        continue;
                    }

                    while (lastname.equals("")) {
                        System.out.print("last name: ");
                        lastname = userIn.readLine();
                    }

                    if (lastname.equals("exit")) {
                        break;
                    }

                    if (lastname.equals("restart")) {
                        continue;
                    }

                    while (email.equals("")) {
                        System.out.print("email: ");
                        email = userIn.readLine();
                    }

                    if (email.equals("exit")) {
                        break;
                    }

                    if (email.equals("restart")) {
                        continue;
                    }
                }

                // get Address details
                if (action_res.equals("A")
                        && (action.equals("C") || action.equals("U"))) {
                    while (street.equals("")) {
                        System.out.print("street: ");
                        street = userIn.readLine();
                    }

                    if (street.equals("exit")) {
                        break;
                    }

                    if (street.equals("restart")) {
                        continue;
                    }

                    while (city.equals("")) {
                        System.out.print("city: ");
                        city = userIn.readLine();
                    }

                    if (city.equals("exit")) {
                        break;
                    }

                    if (city.equals("restart")) {
                        continue;
                    }

                    while (zipcode.equals("")) {
                        System.out.print("ZIP code: ");
                        zipcode = userIn.readLine();
                    }

                    if (zipcode.equals("exit")) {
                        break;
                    }

                    if (zipcode.equals("restart")) {
                        continue;
                    }

                    while (country.equals("")) {
                        System.out.print("Country: ");
                        country = userIn.readLine();
                    }

                    if (country.equals("exit")) {
                        break;
                    }

                    if (country.equals("restart")) {
                        continue;
                    }

                }

                tokenSeq = location + "|" + action;

                if (!customer_set.equals("")) {
                    tokenSeq += ("|" + customer_set);
                }

                if (!action_res.equals("")) {
                    tokenSeq += ("|" + action_res);
                }

                if (customerID != -1) {
                    tokenSeq += ("|" + customerID);
                }

                if (addressID != -1) {
                    tokenSeq += ("|" + addressID);
                }

                if (!username.equals("") && !password.equals("")
                        && !firstname.equals("") && !lastname.equals("")
                        && !email.equals("")) {
                    tokenSeq += ("|" + username + "|" + password + "|"
                            + firstname + "|" + lastname + "|" + email);
                }

                if (!street.equals("") && !city.equals("")
                        && !zipcode.equals("") && !country.equals("")) {
                    tokenSeq += ("|" + street + "|" + city + "|"
                            + zipcode + "|" + country);
                }

                socketOut.println(tokenSeq);
                String response = socketIn.readLine();

                try {
                    int numResponseLines = Integer.valueOf(response).intValue();
                    for (int z = 0; z < numResponseLines; z++) {
                        System.out.println(socketIn.readLine());
                    }
                    System.out.println("");
                } catch (NumberFormatException ex) {
                    System.out.println(response);
                }

            }

            userIn.close();
            socketIn.close();
            socketOut.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
